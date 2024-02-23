    package com.nebula.NebulaApp;

    import static androidx.core.location.LocationManagerCompat.getCurrentLocation;

    import android.annotation.SuppressLint;
    import android.app.PendingIntent;
    import android.content.Context;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.graphics.PointF;
    import android.location.Address;
    import android.location.Geocoder;
    import android.location.Location;
    import android.location.LocationListener;
    import android.location.LocationManager;
    import android.os.Build;
    import android.os.Bundle;
    import android.os.Handler;
    import android.os.Looper;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.ImageButton;
    import android.widget.LinearLayout;
    import android.widget.TextView;
    import android.widget.Toast;
    import android.Manifest;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.appcompat.widget.Toolbar;
    import androidx.core.app.ActivityCompat;
    import androidx.fragment.app.Fragment;

    import com.google.android.gms.location.FusedLocationProviderClient;
    import com.google.android.gms.location.Geofence;
    import com.google.android.gms.location.GeofencingClient;
    import com.google.android.gms.location.GeofencingRequest;
    import com.google.android.gms.location.LocationServices;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.zxing.integration.android.IntentIntegrator;
    import com.google.zxing.integration.android.IntentResult;

    import java.io.IOException;
    import java.math.BigInteger;
    import java.nio.charset.StandardCharsets;
    import java.security.MessageDigest;
    import java.security.NoSuchAlgorithmException;
    import java.text.DateFormat;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.Date;
    import java.util.List;
    import java.util.Locale;
    import java.util.concurrent.Executor;


    public class HomeFragment extends Fragment {
        @SuppressLint("StaticFieldLeak")
        static TextView locationParam;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        private static final String TAG = "HomeFragment";
        private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
        private boolean isEnableQRButton;
        private GeofencingClient geofencingClient;
        private GeofenceProvider geofenceProvider;
        private PendingIntent geofencePendingIntent;
        private TextView geofenceStatus, currentTimeTv, currentDateTv;
        private Button requestPermissionButton;
        private ImageButton scanQr;
        private LinearLayout streakLayout,locationContainer;
        private LocationAlgorithm locationAlgorithm;

        private static final int REQUEST_LOCATION_PERMISSION = 1;
        private FusedLocationProviderClient fusedLocationProviderClient;
        private LocationManager locationManager;
        private LocationListener locationListener;
        String studentInstituteSecreteCode; // Fetch institute secret code of student inside this var;

        // TODO: SHA-512 Encoding Logic
        public static class SHAEncoding {
            public byte[] obtainSHA(String s) throws NoSuchAlgorithmException {
                MessageDigest msgDgst = MessageDigest.getInstance("SHA-512");
                return msgDgst.digest(s.getBytes(StandardCharsets.UTF_8));
            }

            public String toHexStr(byte[] hash) {
                BigInteger no = new BigInteger(1, hash);
                StringBuilder hexStr = new StringBuilder(no.toString(16));
                while (hexStr.length() < 32) hexStr.insert(0, '0');
                return hexStr.toString();
            }
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_home, container, false);
            scanQr = v.findViewById(R.id.scanQrCode);
            currentTimeTv = (TextView) v.findViewById(R.id.currentTime);
            currentDateTv = (TextView) v.findViewById(R.id.currentDate);
            locationParam = (TextView) v.findViewById(R.id.locationParam);
            streakLayout = (LinearLayout) v.findViewById(R.id.streakContainer);
            locationContainer = (LinearLayout) v.findViewById(R.id.locationContainer);
            List<PointF> targetLocation = new ArrayList<>();
            targetLocation.add(new PointF(19.957222156243933f, 73.77811728369765f));
            targetLocation.add(new PointF(19.957528082061817f, 73.77860483776155f));
            targetLocation.add(new PointF(19.95746542208065f, 73.77880613361674f));
            targetLocation.add(new PointF(19.95691254384423f, 73.77869110590693f));
            targetLocation.add(new PointF(19.95702511223012f, 73.77817730626943f));
            targetLocation.add(new PointF(19.957061156293843f, 73.77811923808011f));
            targetLocation.add(new PointF(19.957162079798966f, 73.77810280356336f));
            targetLocation.add(new PointF(19.95721460125589f, 73.77813348104402f));
            locationAlgorithm = new LocationAlgorithm(targetLocation);
            Toolbar toolbar = (Toolbar) requireActivity().findViewById(R.id.toolbar);
            toolbar.setTitle("Home");
            updateTimeAndDate();
            streakButtonStateChange(false);
            changeQRCodeButtonState(false);

            scanQr.setOnClickListener(v1 -> {
                if(scanQr.isEnabled()) {
                    IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(HomeFragment.this);
                    intentIntegrator.setPrompt("Scan the QR Code");
                    intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                    //            intentIntegrator.setCaptureActivity(ZxingCaptureCustomActivity.class);
                    intentIntegrator.setBeepEnabled(false);
                    intentIntegrator.setOrientationLocked(true);
                    intentIntegrator.initiateScan();
                }else{
                    Toast.makeText(requireActivity().getApplicationContext(), "Enable location to Mark Attendance", Toast.LENGTH_SHORT).show();
                }
            });
            locationContainer.setOnClickListener(v12 -> {
                if(scanQr.isEnabled()){
                    scanQr.setEnabled(false);
                    scanQr.setAlpha(.3f);
                }else{
                    scanQr.setEnabled(true);
                    scanQr.setAlpha(1.0f);
                }
            });
            reference.child("Nebula").child("Institute").child("GGSP0369").child("Institute Secret Code").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        studentInstituteSecreteCode = String.valueOf(task.getResult().getValue());
                        Log.d("Institute SC >>>>: ", String.valueOf(task.getResult().getValue()));
                    }
                }
            });

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity().getApplicationContext());

            // Check for location permission
            if (ActivityCompat.checkSelfPermission(requireActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Request permission
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            } else {
                // Permission already granted, get location
                getCurrentLocation();
            }

    //
            locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    PointF coordinates = new PointF((float)latitude,(float)longitude);

                    Geocoder geocoder = new Geocoder(requireActivity().getApplicationContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        if (addresses != null && addresses.size() > 0) {
                            Address address = addresses.get(0);
                            String addressName = address.getFeatureName();
                            Log.d("Location_Fetched", addressName);
                            locationParam.setText(addressName);
                            boolean iteration = true;
//                            locationAlgorithm.finalOutput(requireActivity().getApplicationContext(), coordinates);
                            changeQRCodeButtonState(locationAlgorithm.abc(coordinates));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }
                @Override
                public void onProviderEnabled(String provider) {
                }
                @Override
                public void onProviderDisabled(String provider) {
                }
            };

            // Check if the app has the permission to access the location
            if (ActivityCompat.checkSelfPermission(requireActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // If not, request the permission
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {
                // If yes, request location updates from the location manager
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
            return v;
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == REQUEST_LOCATION_PERMISSION) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    changeQRCodeButtonState(false);
                    Toast.makeText(requireActivity().getApplicationContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }

        private void getCurrentLocation() {
            if (ActivityCompat.checkSelfPermission(requireActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
               return;
            }
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            // Got last known location
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            PointF coordinates = new PointF((float)latitude,(float)longitude);
                            changeQRCodeButtonState(locationAlgorithm.abc(coordinates));
                            getAddressFromLocation(latitude, longitude);
                        } else {
                            changeQRCodeButtonState(false);
                            Toast.makeText(requireActivity().getApplicationContext(), "Location not available", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        private void getAddressFromLocation(double latitude, double longitude) {
            Geocoder geocoder = new Geocoder(requireActivity().getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    PointF coordinates = new PointF((float)latitude,(float)longitude);
                    Address address = addresses.get(0);
                    String addressName = address.getFeatureName();
                    if(addressName!=null) {
                        Log.d("Location_Fetched", addressName);
                        locationParam.setText(addressName);
                        changeQRCodeButtonState(locationAlgorithm.abc(coordinates));
                    }else{
                        Log.d("Location_Fetched", "None");
                        locationParam.setText("None");
                        changeQRCodeButtonState(false);
                    }
                } else {
                    locationParam.setText("Address not found");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private void updateTimeAndDate() {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Calendar calendar = Calendar.getInstance();
                    Date currentTime = calendar.getTime();
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String formattedTime = timeFormat.format(currentTime);
                    DateFormat dateFormat = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
                    String formattedDate = dateFormat.format(currentTime);
                    currentTimeTv.setText(formattedTime);
                    currentDateTv.setText(formattedDate);
                    handler.postDelayed(this, 1000);
                }
            });
        }

        public void updateLocationText(String newText) {
                locationParam.setText(newText);
        }


        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            // if the intentResult is null then toast a message as "cancelled" --->
            if (intentResult != null) {
                if (intentResult.getContents() == null) {
                    Toast.makeText(requireActivity().getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                } else
                { // if the intentResult is not null we'll set the content and format of scan message --->
                    String urlData = intentResult.getContents();
                    String studentInstituteSecreteCodeEncoded;
                    SHAEncoding sha = new SHAEncoding();
                    try { //TODO: Convert str to encrypted-str >>>
                        studentInstituteSecreteCodeEncoded = sha.toHexStr(sha.obtainSHA(studentInstituteSecreteCode));
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("\n" + studentInstituteSecreteCode + " : " + studentInstituteSecreteCodeEncoded); // TODO: REMOVE AFTER Testing
                    if (urlData.equals(studentInstituteSecreteCodeEncoded)){
                        // perform attendance marking process here --->
                        Fragment mFragment = new AttendanceFragment();
                        Bundle args = new Bundle();
                        args.putString("isMarked", "True");
                        mFragment.setArguments(args);
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, mFragment).commit();
                        Toast.makeText(requireActivity().getApplicationContext(),"Attendance Marked! 🥳",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(requireActivity().getApplicationContext(),"Failed marking your attendance",Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

        // To change the appearance of the qr button based on location >>>
        public void changeQRCodeButtonState(boolean state){
            if(!state){
//                scanQr.setImageResource(R.drawable.home_mark_attendance_button_disabled);
                scanQr.setEnabled(false);
                scanQr.setAlpha(0.3f);
                isEnableQRButton = false;
//                Toast.makeText(requireActivity().getApplicationContext(), "Enable location to Mark Attendance", Toast.LENGTH_SHORT).show();
            }else{
//                scanQr.setImageResource(R.drawable.home_mark_attendance_button);
                scanQr.setEnabled(true);
                scanQr.setAlpha(1.0f);
                isEnableQRButton = true;
//                Toast.makeText(requireActivity().getApplicationContext(), "Location Enabled", Toast.LENGTH_SHORT).show();
            }
        }

        // Change streaksButton state >>>
        public void streakButtonStateChange(boolean state){
            if(!state) {
                int childCount = streakLayout.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = streakLayout.getChildAt(i);
                    child.setVisibility(View.GONE);
                }
                streakLayout.setBackgroundResource(R.drawable.nonstreak_image);
            }else{
                //manage steaks here >>>
                int childCount = streakLayout.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = streakLayout.getChildAt(i);
                    child.setVisibility(View.VISIBLE);
                }
                streakLayout.setBackgroundResource(R.drawable.streak_container);
            }

        }
    }