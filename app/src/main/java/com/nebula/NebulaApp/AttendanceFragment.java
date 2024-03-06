package com.nebula.NebulaApp;

import static com.nebula.NebulaApp.HomeFragment.SHARED_PREFS;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.renderer.PieChartRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class AttendanceFragment extends Fragment {
    private MaterialCalendarView materialCalendarView;
    Calendar calendar = Calendar.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    SharedPreferences sharedPref;
    private AttendaneOperations attendaneOperations;

    PieChart pieChart;
    TextView monthPercentage,attendancePercentage;
    ImageButton shareProgressBtn;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_attendance, container, false);
        materialCalendarView = (MaterialCalendarView) v.findViewById(R.id.calenderAttendance);
        pieChart = v.findViewById(R.id.pieChart_view);
        monthPercentage = v.findViewById(R.id.monthPercentage);
        shareProgressBtn = v.findViewById(R.id.shareProgressBtn);
        attendancePercentage = v.findViewById(R.id.attendancePercentage);
        sharedPref = this.requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        Toolbar toolbar = (Toolbar) requireActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Attendance");

        attendaneOperations = new AttendaneOperations(sharedPref.getString("sanitized_email","")
                ,sharedPref.getString("Institute_id",""),sharedPref);

        // Get data from HomeFragment after successful scan
        assert getArguments() != null;
        String isMarked;

        shareProgressBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Scale down when button is pressed
                        scaleButton(shareProgressBtn, 0.9f);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        // Scale back to normal when button is released
                        scaleButton(shareProgressBtn, 1.0f);
                        break;
                }
                // Return false to allow the event to continue to the next listener
                return false;
            }
        });

        sharedPref = this.requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        reference = FirebaseDatabase.getInstance().getReference().child("Institute")
                .child(sharedPref.getString("Institute_id",""))
                .child("Student").child(sharedPref.getString("sanitized_email","")).child("Attendance Data");

        loadAttendanceData();
        initPieChart();
        showPieChart();
        return v;
    }

    private void loadAttendanceData() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> allMonths = new ArrayList<String>(Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"));
                if (dataSnapshot.exists()) {
                    for (DataSnapshot yearSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot monthSnapshot : yearSnapshot.getChildren()) {
                            for (DataSnapshot daySnapshot : monthSnapshot.getChildren()) {
                                DataSnapshot isMarkedSnapshot = daySnapshot.child("isMarked");
                                if (isMarkedSnapshot.exists()) {
                                    Boolean isMarkedValue = isMarkedSnapshot.getValue(Boolean.class);
                                    if (isMarkedValue != null) {
                                        int year = Integer.parseInt(yearSnapshot.getKey());
                                        String month = monthSnapshot.getKey();
                                        String dayfor = daySnapshot.getKey();
                                        String[] lastDayComponents = dayfor.split("\\|");
                                        int day = Integer.parseInt(lastDayComponents[0]);
                                        CalendarDay currentDate = CalendarDay.from(year, allMonths.indexOf(month), day);
                                        if (isMarkedValue) {
                                            Log.d("AttendanceFragment", "isMarked: TRUE (" + currentDate);
                                            EventDecorator eventDecorator = new EventDecorator(currentDate);
                                            materialCalendarView.addDecorator(eventDecorator);
                                        } else {
                                            Log.d("AttendanceFragment", "isMarked: FALSE (" + year + "|" + allMonths.indexOf(month) + 1 + "|" + day);
                                            EventDecolored eventDecolored = new EventDecolored(currentDate);
                                            materialCalendarView.addDecorator(eventDecolored);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Log.d("AttendanceFragment", "No attendance data found for the user");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("AttendanceFragment", "Error fetching data: " + databaseError.getMessage());
            }
        });
    }

    private void showPieChart(){
        attendaneOperations.getAllAttendedPer(new AttendanceCallback() {
            @Override
            public void onAttendancePercentage(double percentage) {
            int attendedPer = (int) Math.round(percentage);
            int absentPer = 100 - attendedPer;
            int attendancePercentageTextColor = getColorForAttendancePercentage(attendedPer);
            attendancePercentage.setText(String.valueOf(attendedPer)+"%");
            attendancePercentage.setTextColor(attendancePercentageTextColor);
            Log.d("AttendanceFragment", "All Attendance Perc:" + attendedPer);
            Log.d("AttendanceFragment", "All Absent Perc:" + absentPer);

            ArrayList<PieEntry> pieEntries = new ArrayList<>();
            String label = "Attendance Percentage";

            Map<String, Integer> typeAmountMap = new HashMap<>();
            typeAmountMap.put("Attended", attendedPer);
            typeAmountMap.put("Absent", absentPer);

//            ArrayList<Integer> colors = new ArrayList<>();
//            colors.add(Color.parseColor("#312954"));
//            colors.add(Color.parseColor("#547AFF"));

            ArrayList<Integer> colors = getColorsForAttendancePercentage(attendedPer);


                for (String type : typeAmountMap.keySet()) {
                pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
            }

            PieDataSet pieDataSet = new PieDataSet(pieEntries, label);
            pieDataSet.setValueTextSize(12f);
            pieDataSet.setColors(colors);
            PieData pieData = new PieData(pieDataSet);
            pieData.setDrawValues(true);
            // Add shadow to the pie >>
//            pieChart.getRenderer().getPaintRender().setShadowLayer(80, 0, 0, attendancePercentageTextColor);
            pieChart.setData(pieData);
            pieChart.invalidate();

            }

            @Override
            public void onContributionPercentage(double percentage) {

            }
        });
        attendaneOperations.getThisMonthContribution(new AttendanceCallback() {
            @Override
            public void onAttendancePercentage(double percentage) {
            }

            @Override
            public void onContributionPercentage(double percentage) {
                if(percentage>0) {
                    monthPercentage.setTextColor(Color.parseColor("#51FC62"));
                    monthPercentage.setText("+"+String.valueOf(percentage)+"%");
                }
                else {
                    monthPercentage.setTextColor(Color.parseColor("#FC7051"));
                    monthPercentage.setText(String.valueOf(percentage)+"%");
                }
            }
        });
    }

    private void initPieChart(){
        pieChart.setRenderer(new RoundedSlicesPieChartRenderer(pieChart, pieChart.getAnimator(), pieChart.getViewPortHandler()));

        //using percentage as values instead of amount
        pieChart.setUsePercentValues(true);
        //remove the description label on the lower left corner, default true if not set
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        //enabling the user to rotate the chart, default true
        pieChart.setRotationEnabled(true);
        //adding friction when rotating the pie chart
        pieChart.setDragDecelerationFrictionCoef(0.6f);
        //setting the first entry start from right hand side, default starting from top
        pieChart.setRotationAngle(270);

        //highlight the entry when it is tapped, default true if not set
        pieChart.setHighlightPerTapEnabled(true);
        //adding animation so the entries pop up from 0 degree
        pieChart.animateY(2000, Easing.EasingOption.EaseInOutQuad);
        //setting the color of the hole in the middle, default white
        pieChart.setHoleColor(Color.parseColor("#00000000"));
        pieChart.setCenterText("Attendance Percentage");
        pieChart.setCenterTextColor(R.color.white);

    }
    private ArrayList<Integer> getColorsForAttendancePercentage(int attendancePercentage) {
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#312954"));
        int[] colorGradient = {Color.parseColor("#862000"), Color.parseColor("#C7601A"), Color.parseColor("#F68E2D"),
                Color.parseColor("#F6A72D"), Color.parseColor("#F6BE2D"), Color.parseColor("#C6CE2D"),
                Color.parseColor("#97DD2D"), Color.parseColor("#49F62D"), Color.parseColor("#3BF65C"), Color.parseColor("#2DF68A")};

        // Calculate index based on the attendance percentage
        int index = (int) (attendancePercentage / 10.0) - 1;
        if (index < 0) {
            index = 0;
        } else if (index >= colorGradient.length) {
            index = colorGradient.length - 1;
        }

        // Fill the colors array with the gradient colors based on the index
//        for (int i =0; i <= index; i++) {
//        }
            colors.add(colorGradient[index]);
        return colors;
    }
    private int getColorForAttendancePercentage(int attendancePercentage) {
        int[] colorGradient = {Color.parseColor("#862000"), Color.parseColor("#C7601A"), Color.parseColor("#F68E2D"),
                Color.parseColor("#F6A72D"), Color.parseColor("#F6BE2D"), Color.parseColor("#C6CE2D"),
                Color.parseColor("#97DD2D"), Color.parseColor("#49F62D"), Color.parseColor("#3BF65C"), Color.parseColor("#2DF68A")};

        // Calculate index based on the attendance percentage
        int index = (int) (attendancePercentage / 10.0) - 1;
        if (index < 0) {
            index = 0;
        } else if (index >= colorGradient.length) {
            index = colorGradient.length - 1;
        }

        // Return the color from the gradient based on the index
        return colorGradient[index];
    }

    //Round Corners >>
    public class RoundedSlicesPieChartRenderer extends PieChartRenderer {
        public RoundedSlicesPieChartRenderer(PieChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
            super(chart, animator, viewPortHandler);

            chart.setDrawHoleEnabled(true);
        }

        @Override
        protected void drawDataSet(Canvas c, IPieDataSet dataSet) {
            float angle = 0;
            float rotationAngle = mChart.getRotationAngle();

            float phaseX = mAnimator.getPhaseX();
            float phaseY = mAnimator.getPhaseY();

            final RectF circleBox = mChart.getCircleBox();

            final int entryCount = dataSet.getEntryCount();
            final float[] drawAngles = mChart.getDrawAngles();
            final MPPointF center = mChart.getCenterCircleBox();
            final float radius = mChart.getRadius();
            final boolean drawInnerArc = mChart.isDrawHoleEnabled() && !mChart.isDrawSlicesUnderHoleEnabled();
            final float userInnerRadius = drawInnerArc
                    ? radius * (mChart.getHoleRadius() / 100.f)
                    : 0.f;
            final float roundedRadius = (radius - (radius * mChart.getHoleRadius() / 100f)) / 2f;
            final RectF roundedCircleBox = new RectF();

            int visibleAngleCount = 0;
            for (int j = 0; j < entryCount; j++) {
                // draw only if the value is greater than zero
                if ((Math.abs(dataSet.getEntryForIndex(j).getY()) > Utils.FLOAT_EPSILON)) {
                    visibleAngleCount++;
                }
            }

            final float sliceSpace = visibleAngleCount <= 1 ? 0.f : getSliceSpace(dataSet);
            final Path pathBuffer = new Path();
            final RectF mInnerRectBuffer = new RectF();

            for (int j = 0; j < entryCount; j++) {
                float sliceAngle = drawAngles[j];
                float innerRadius = userInnerRadius;

                Entry e = dataSet.getEntryForIndex(j);

                // draw only if the value is greater than zero
                if (!(Math.abs(e.getY()) > Utils.FLOAT_EPSILON)) {
                    angle += sliceAngle * phaseX;
                    continue;
                }

                // Don't draw if it's highlighted, unless the chart uses rounded slices
                if (mChart.needsHighlight(j) && !drawInnerArc) {
                    angle += sliceAngle * phaseX;
                    continue;
                }

                final boolean accountForSliceSpacing = sliceSpace > 0.f && sliceAngle <= 180.f;

                mRenderPaint.setColor(dataSet.getColor(j));

                final float sliceSpaceAngleOuter = visibleAngleCount == 1 ?
                        0.f :
                        sliceSpace / (Utils.FDEG2RAD * radius);
                final float startAngleOuter = rotationAngle + (angle + sliceSpaceAngleOuter / 2.f) * phaseY;
                float sweepAngleOuter = (sliceAngle - sliceSpaceAngleOuter) * phaseY;

                if (sweepAngleOuter < 0.f) {
                    sweepAngleOuter = 0.f;
                }

                pathBuffer.reset();

                float arcStartPointX = center.x + radius * (float) Math.cos(startAngleOuter * Utils.FDEG2RAD);
                float arcStartPointY = center.y + radius * (float) Math.sin(startAngleOuter * Utils.FDEG2RAD);

                if (sweepAngleOuter >= 360.f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
                    // Android is doing "mod 360"
                    pathBuffer.addCircle(center.x, center.y, radius, Path.Direction.CW);
                } else {
                    if (drawInnerArc) {
                        float x = center.x + (radius - roundedRadius) * (float) Math.cos(startAngleOuter * Utils.FDEG2RAD);
                        float y = center.y + (radius - roundedRadius) * (float) Math.sin(startAngleOuter * Utils.FDEG2RAD);

                        roundedCircleBox.set(x - roundedRadius, y - roundedRadius, x + roundedRadius, y + roundedRadius);
                        pathBuffer.arcTo(roundedCircleBox, startAngleOuter - 180, 180);
                    }

                    pathBuffer.arcTo(
                            circleBox,
                            startAngleOuter,
                            sweepAngleOuter
                    );
                }

                // API < 21 does not receive floats in addArc, but a RectF
                mInnerRectBuffer.set(
                        center.x - innerRadius,
                        center.y - innerRadius,
                        center.x + innerRadius,
                        center.y + innerRadius);

                if (drawInnerArc && (innerRadius > 0.f || accountForSliceSpacing)) {

                    if (accountForSliceSpacing) {
                        float minSpacedRadius =
                                calculateMinimumRadiusForSpacedSlice(
                                        center, radius,
                                        sliceAngle * phaseY,
                                        arcStartPointX, arcStartPointY,
                                        startAngleOuter,
                                        sweepAngleOuter);

                        if (minSpacedRadius < 0.f)
                            minSpacedRadius = -minSpacedRadius;

                        innerRadius = Math.max(innerRadius, minSpacedRadius);
                    }

                    final float sliceSpaceAngleInner = visibleAngleCount == 1 || innerRadius == 0.f ?
                            0.f :
                            sliceSpace / (Utils.FDEG2RAD * innerRadius);
                    final float startAngleInner = rotationAngle + (angle + sliceSpaceAngleInner / 2.f) * phaseY;
                    float sweepAngleInner = (sliceAngle - sliceSpaceAngleInner) * phaseY;
                    if (sweepAngleInner < 0.f) {
                        sweepAngleInner = 0.f;
                    }
                    final float endAngleInner = startAngleInner + sweepAngleInner;

                    if (sweepAngleOuter >= 360.f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
                        // Android is doing "mod 360"
                        pathBuffer.addCircle(center.x, center.y, innerRadius, Path.Direction.CCW);
                    } else {
                        float x = center.x + (radius - roundedRadius) * (float) Math.cos(endAngleInner * Utils.FDEG2RAD);
                        float y = center.y + (radius - roundedRadius) * (float) Math.sin(endAngleInner * Utils.FDEG2RAD);

                        roundedCircleBox.set(x - roundedRadius, y - roundedRadius, x + roundedRadius, y + roundedRadius);

                        pathBuffer.arcTo(roundedCircleBox, endAngleInner, 180);
                        pathBuffer.arcTo(mInnerRectBuffer, endAngleInner, -sweepAngleInner);
                    }
                } else {

                    if (sweepAngleOuter % 360f > Utils.FLOAT_EPSILON) {
                        if (accountForSliceSpacing) {

                            float angleMiddle = startAngleOuter + sweepAngleOuter / 2.f;

                            float sliceSpaceOffset =
                                    calculateMinimumRadiusForSpacedSlice(
                                            center,
                                            radius,
                                            sliceAngle * phaseY,
                                            arcStartPointX,
                                            arcStartPointY,
                                            startAngleOuter,
                                            sweepAngleOuter);

                            float arcEndPointX = center.x +
                                    sliceSpaceOffset * (float) Math.cos(angleMiddle * Utils.FDEG2RAD);
                            float arcEndPointY = center.y +
                                    sliceSpaceOffset * (float) Math.sin(angleMiddle * Utils.FDEG2RAD);

                            pathBuffer.lineTo(
                                    arcEndPointX,
                                    arcEndPointY);

                        } else {
                            pathBuffer.lineTo(
                                    center.x,
                                    center.y);
                        }
                    }

                }

                pathBuffer.close();

                mBitmapCanvas.drawPath(pathBuffer, mRenderPaint);

                angle += sliceAngle * phaseX;
            }

            MPPointF.recycleInstance(center);
        }
    }

    //Image Button animation
    private void scaleButton(ImageButton button, float scale) {
        // Scale the button using ObjectAnimator
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(button, "scaleX", scale);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(button, "scaleY", scale);
        scaleDownX.setDuration(150);
        scaleDownY.setDuration(150);

        // Listen for the end of the animation to reset the alpha value
        scaleDownX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });

        // Start the animation
        scaleDownX.start();
        scaleDownY.start();
    }
//    public void addDecoratorToCalendar(CalendarDay currentDate){
//        EventDecorator eventDecorator = new EventDecorator(currentDate);
//        materialCalendarView.addDecorator(eventDecorator);
//    }
}