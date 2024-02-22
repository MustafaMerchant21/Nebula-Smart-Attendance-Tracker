package com.nebula.NebulaApp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private HomeFragment homeFragment;
    private static String locationText;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    private LocationManager locationManager;
    private LocationListener locationListener;

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
            getFragment(new HomeFragment(), true);
    }
    //TODO: Geofencing continuation --> https://leehari007.medium.com/how-to-create-a-geofence-app-in-android-ae456f16c0d0
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