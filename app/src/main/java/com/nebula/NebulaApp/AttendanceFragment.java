package com.nebula.NebulaApp;

import static com.nebula.NebulaApp.Nebula_login.SHARED_PREFS;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applandeo.materialcalendarview.CalendarView;
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

public class AttendanceFragment extends Fragment {
    private MaterialCalendarView materialCalendarView;
    Calendar calendar = Calendar.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    SharedPreferences sharedPref;
    private AttendaneOperations attendaneOperations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_attendance, container, false);
        materialCalendarView = (MaterialCalendarView) v.findViewById(R.id.calenderAttendance);
        Toolbar toolbar = (Toolbar) requireActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Attendance");

        attendaneOperations = new AttendaneOperations(materialCalendarView);

        // Get data from HomeFragment after successful scan
        assert getArguments() != null;
        String isMarked;

        sharedPref = this.requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        reference = FirebaseDatabase.getInstance().getReference().child("Institute")
                .child(sharedPref.getString("Institute_id",""))
                .child("Student").child(sharedPref.getString("sanitized_email","")).child("Attendance Data");

        loadAttendanceData();
//        reference.child("Institute").child(sharedPref.getString("Institute_id",""))
//                .child("Student").child(sharedPref.getString("sanitized_email",""))
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        // Check if the dataSnapshot exists
//                        if (dataSnapshot.exists()) {
//                            for (DataSnapshot yearSnapshot : dataSnapshot.getChildren()) {
//                                for (DataSnapshot monthSnapshot : yearSnapshot.getChildren()) {
//                                    for (DataSnapshot daySnapshot : monthSnapshot.getChildren()) {
//                                        DataSnapshot isMarkedSnapshot = daySnapshot.child("isMarked");
//                                        // Check if the isMarked field exists
//                                        if (isMarkedSnapshot.exists()) {
//                                            Boolean isMarkedValue = isMarkedSnapshot.getValue(Boolean.class);
//                                            if (isMarkedValue != null) {
//                                                int year = Integer.parseInt(yearSnapshot.getKey());
//                                                int month = Integer.parseInt(monthSnapshot.getKey());
//                                                String dayfor = daySnapshot.getKey();
//                                                String[] lastDayComponents = dayfor.split("\\|");
//                                                int day = Integer.parseInt(lastDayComponents[0]);
//                                                CalendarDay currentDate = CalendarDay.from(year, month, day);
//                                                if (isMarkedValue) {
//                                                    EventDecorator eventDecorator = new EventDecorator(currentDate);
//                                                    materialCalendarView.addDecorator(eventDecorator);
//                                                } else {
//                                                    EventDecolored eventDecorator = new EventDecolored(currentDate);
//                                                    materialCalendarView.addDecorator(eventDecorator);
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        } else {
//                            Log.d("MainActivity: AttendanceFragment", "No data found for the user");
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        // Handle errors
//                        Log.e("MainActivity: AttendanceFragment", "Error fetching data: " + databaseError.getMessage());
//                    }
//                });


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

//    public void addDecoratorToCalendar(CalendarDay currentDate){
//        EventDecorator eventDecorator = new EventDecorator(currentDate);
//        materialCalendarView.addDecorator(eventDecorator);
//    }
}