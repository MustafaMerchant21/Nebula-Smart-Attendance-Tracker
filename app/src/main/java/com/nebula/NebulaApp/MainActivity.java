package com.nebula.NebulaApp;

import android.app.Activity;
import android.app.PendingIntent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.gms.location.Geofence;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    private List<Geofence> geofenceList = new ArrayList<>();
    private PendingIntent geofencePendingIntent;
//    private GeofencingClient geofencingClient = LocationServices.getGeofencingClient(getApplicationContext());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        //TODO: Geofencing continuation --> https://leehari007.medium.com/how-to-create-a-geofence-app-in-android-ae456f16c0d0

//        geofenceList.add(new Geofence.Builder()
//                // Set the request ID of the geofence. This is a string to identify this
//                // geofence.
//                .setRequestId("GGSP_COLLEGE_GEOFENCE")
//                .setCircularRegion(
//                        Double.parseDouble("19.994701635668104"),// lat
//                        Double.parseDouble("73.79993066259617"),// lng
//                        (float) 10)// add the radius in float.
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
//                .setExpirationDuration(Geofence.NEVER_EXPIRE)
//                .setNotificationResponsiveness(1000)
//                .build());

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }


        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setItemActiveIndicatorColor(getColorStateList(R.color.menuItemActiveIndicator));
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();
                if (itemId == R.id.navHome) {
                    getFragment(new HomeFragment(), false);
                } else if (itemId == R.id.navAttendance) {
                    getFragment(new AttendanceFragment(), false);
                } else if (itemId == R.id.navLeave) {
                    getFragment(new LeaveFragment(), false);
                } else { // navProfile
                    getFragment(new ProfileFragment(), false);
                }
                return true;
            }
        });
        // TODO: DB Dynamic Attendance Data Firebase >>>

//        String instituteID = reference.child("Institute").getKey().toString();
//        String studentID = reference.child("Institute").child("GGSP0369").child("kifivew599@fahih_com").getKey().toString();
//        updateOnRegisterUserAttendanceRecord(instituteID,studentID);
//        DatabaseReference dob = reference.push();
//        dob.setValue("12-12-2004"); // Casual approach


            getFragment(new HomeFragment(), true);
    }

//    public void updateOnRegisterUserAttendanceRecord(String instituteId, String studentId){
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH) + 1;
//        int date = calendar.get(Calendar.DATE);
//        String currentYearStr = String.valueOf(year);
//        String currentMonthStr = String.format("%02d", month);
//        String currentDateStr = String.format("%02d-%02d-%04d", date, month, year);
//
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference studentRef = database.getReference("Institute").child("GGSP0369")
//                .child("Student").child("kifivew599@fahih_com");
//        DatabaseReference attendanceRef = studentRef.push();
//        Map<String, Object> yearNode = new HashMap<String, Object>();
//        Map<String, Object> monthNode = new HashMap<String, Object>();
//        Map<String, Object> dayNode = new HashMap<String, Object>();
//        yearNode.put(currentYearStr, monthNode);
//        monthNode.put(currentMonthStr,dayNode);
//        Map<String, Object> dateMap = (Map<String, Object>) dayNode.get(currentDateStr);
//        assert dateMap != null;
//        dateMap.put("isMarked", "false");
//        dateMap.put("time", "00:00:00");
//    }

    //TODO: Geofencing continuation --> https://leehari007.medium.com/how-to-create-a-geofence-app-in-android-ae456f16c0d0
//    private void addGeofence() {
//        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
//                .addOnSuccessListener(this, aVoid -> {
//                    Toast.makeText(getApplicationContext()
//                            , "Geofencing has started", Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(this, e -> {
//                    Toast.makeText(getApplicationContext()
//                            , "Geofencing failed", Toast.LENGTH_SHORT).show();
//
//                });
//    }
//
//    private void removeGeofence() {
//        geofencingClient.removeGeofences(getGeofencePendingIntent())
//                .addOnSuccessListener(this, aVoid -> {
//                    Toast.makeText(getApplicationContext()
//                            , "Geofencing has been removed", Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(this, e -> {
//                    Toast.makeText(getApplicationContext()
//                            , "Geofencing could not be removed", Toast.LENGTH_SHORT).show();
//                });
//    }
//    private GeofencingRequest getGeofencingRequest() {
//        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
//        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
//        builder.addGeofences(geofenceList);
//        return builder.build();
//    }
//
//    private PendingIntent getGeofencePendingIntent() {
//        // Reuse the PendingIntent if we already have it.
//        if (geofencePendingIntent != null) {
//            return geofencePendingIntent;
//        }
//        Toast.makeText(getApplicationContext(), "starting broadcast", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, MyBroadCastReceiver.class);
//        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        return geofencePendingIntent;
//    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    private void getFragment(Fragment fragment, boolean isInitialized){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle args = new Bundle();
//        boolean isMarked = true;  // TODO: Retrieve from Database the value of isMarked
//        args.putString("isMarked", isMarked);
        args.putString("isMarked", "False"); // TESTING PURPOSE
        fragment.setArguments(args);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(isInitialized){
            fragmentTransaction.add(R.id.frameLayout, new HomeFragment());
        }else {
            fragmentTransaction.replace(R.id.frameLayout, fragment);
        }
        fragmentTransaction.commit();
    }
}