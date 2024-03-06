package com.nebula.NebulaApp;

import static com.nebula.NebulaApp.HomeFragment.SHARED_PREFS;
import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        sharedPref = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
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
        String timeStamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
        AttendaneOperations attendaneOperations = new AttendaneOperations(sharedPref.getString("sanitized_email",""),sharedPref.getString("Institute_id",""),sharedPref);
        LastNodeKeyNameRetriever lastNodeKeyNameRetriever = new LastNodeKeyNameRetriever(attendaneOperations.getDbRef());
        lastNodeKeyNameRetriever.getLastNodeKeyNames(new LastNodeKeyNamesCallback() {
            @Override
            public void onLastNodeKeyNamesRetrieved(String lastYear, String lastMonth, String lastDay) {
                Log.d("=== Last year === ", lastYear);
                Log.d("=== Last month === ", lastMonth);
                Log.d("=== Last day === ", lastDay);
                Log.d("MainActivity","New Day Checked");
            attendaneOperations.checkNewDay(lastYear,lastMonth,lastDay);
            }
        });

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
        getFragment(new HomeFragment(), true);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        HomeFragment homeFragment1 = new HomeFragment();
//        Intent passedFromLogin = getIntent();
//        Bundle b = new Bundle();
//        b.putString("source_activity",passedFromLogin.getStringExtra("source_activity"));
//        homeFragment1.setArguments(b);
//        fragmentTransaction.add(R.id.frameLayout, homeFragment1);
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
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        Intent passedFromLogin = getIntent();
        Bundle args = new Bundle();
        args.putString("source_activity",passedFromLogin.getStringExtra("source_activity"));
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