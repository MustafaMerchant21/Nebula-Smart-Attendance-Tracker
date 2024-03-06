package com.nebula.NebulaApp;

import static com.nebula.NebulaApp.HomeFragment.SHARED_PREFS;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class NewLeaveApplication extends Fragment {
    public NewLeaveApplication() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    DatabaseReference reference;
    SharedPreferences sharedPref;
    TextInputEditText title,dsc;
    TextView selectedDate;
    Button submit;
    ProgressBar pb;
    long totalDaysSelected;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_leave_application, container, false);
        selectedDate = (TextView) v.findViewById(R.id.date_range);
        submit = (Button) v.findViewById(R.id.submitBtn);
        title = v.findViewById(R.id.leaveTitle);
        dsc = v.findViewById(R.id.leaveDsc);
        pb = v.findViewById(R.id.progressBar2);
        selectedDate.setOnClickListener(view ->  DatePickerdialog());
        sharedPref = this.requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        reference = FirebaseDatabase.getInstance().getReference();

        Random random = new Random();
        int num = random.nextInt(0x10000000);
        String leaveID = String.format("%08x", num);
        submit.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Scale down when button is pressed
                    scaleButton(submit, 0.9f);
                    break;
                case MotionEvent.ACTION_UP:
                    // Scale back to normal when button is released
                    scaleButton(submit, 1.0f);
                    break;
            }
            // Return false to allow the event to continue to the next listener
            return false;
        });
        submit.setOnClickListener(view ->submitLeaveRequest(title.getText().toString(), dsc.getText().toString(), selectedDate.getText().toString(), sharedPref.getString("Email",""),v));

        return v;
    }
    private void DatePickerdialog() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select a date range");

        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Long startDate = selection.first;
            Long endDate = selection.second;
            // Todo Ensure that the first day is not less than that of the current date 👆👆👆
            totalDaysSelected = (TimeUnit.MILLISECONDS.toDays(endDate - startDate))+1;
            Log.d("== NewLeaveApplication ==", String.valueOf(totalDaysSelected));

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String startDateString = sdf.format(new Date(startDate));
            String endDateString = sdf.format(new Date(endDate));

            String selectedDateRange = startDateString + " - " + endDateString;

            selectedDate.setText(selectedDateRange);
        });

        // Showing the date picker dialog
        datePicker.show(requireActivity().getSupportFragmentManager(), "DATE_PICKER");
    }

    //Submit Leave Request >>>
    public void submitLeaveRequest(String title, String description, String dates, String requestedBy, View v) {
        pb.setVisibility(View.VISIBLE);
        if (title.isEmpty() || description.isEmpty() || dates.isEmpty() || requestedBy.isEmpty() || totalDaysSelected == 0) {
            pb.setVisibility(View.GONE);
            Log.d("NewLeaveApplication:Error", "Empty Fields");
            Snackbar snackbar = Snackbar.make(v, "Please fill all the fields", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {
            // Get reference to the leave requests node for the specific department
            DatabaseReference leaveRequestsRef = reference.child("Institute").child(sharedPref.getString("Institute_id", ""))
                    .child("Departments").child(sharedPref.getString("selectedCourse", ""))
                    .child("Leave Data").child("Applied");

            // Generate a unique key for the leave request
            Random random = new Random();
            int num = random.nextInt(0x10000000);
            String leaveRequestId = String.format("%08x", num);

            // Create a HashMap to hold the leave request data
            HashMap<String, Object> leaveRequestData = new HashMap<>();
            leaveRequestData.put("Title", title);
            leaveRequestData.put("Description", description);
            leaveRequestData.put("Dates", dates);
            leaveRequestData.put("TotalDays", String.valueOf(totalDaysSelected));
            leaveRequestData.put("Status", "Pending");
            leaveRequestData.put("RequestedBy", requestedBy);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("LeaveRequestID", leaveRequestId);
            editor.apply();
            Log.d("** LeaveRequestID **", sharedPref.getString("LeaveRequestID", ""));

            // Store the leave request data in the database under the generated key
            leaveRequestsRef.child(String.valueOf(leaveRequestId)).setValue(leaveRequestData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pb.setVisibility(View.GONE);
                            Log.d("NewLeaveApplication:Error", "Leave request submitted successfully 🥳");
                            Toast.makeText(requireContext().getApplicationContext(), "Submitted successfully 🥳", Toast.LENGTH_SHORT).show();
                            Snackbar snackbar = Snackbar.make(v, "Submitted successfully 🥳", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            // Leave request submitted successfully
                            // You can show a success message or perform any additional actions
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pb.setVisibility(View.GONE);
                            Log.d("NewLeaveApplication:Error", "Failed to submit leave request");
                            Toast.makeText(requireContext().getApplicationContext(), "Failed to submit leave request 😕", Toast.LENGTH_SHORT).show();
                            Snackbar snackbar = Snackbar.make(v, "Failed to submit leave request", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            // Failed to submit leave request
                            // Handle the error and display an error message if necessary
                        }
                    });
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.frameLayout, new LeaveFragment());
            fragmentTransaction.commit();
        }
    }

    //Image Button animation
    private void scaleButton(Button button, float scale) {
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
}
