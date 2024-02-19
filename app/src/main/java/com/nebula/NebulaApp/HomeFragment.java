package com.nebula.NebulaApp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;


public class HomeFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    static TextView locationParam;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
//    private GeofencingClient geofencingClient;
//    private GeofenceProvider geofenceProvider;
//    private GeofenceBroadcastReceiver geofenceBroadcastReceiver = geofenceBroadcastReceiver = new GeofenceBroadcastReceiver(this);
    private static final String TAG = "HomeFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private GeofencingClient geofencingClient;
    private GeofenceProvider geofenceProvider;
    private PendingIntent geofencePendingIntent;
    private TextView geofenceStatus;
    private Button requestPermissionButton;
    String studentInstituteSecreteCode; // Fetch institute secret code of student inside this var;
    public static class SHAEncoding
    {
        public byte[] obtainSHA(String s) throws NoSuchAlgorithmException
        {
            MessageDigest msgDgst = MessageDigest.getInstance("SHA-512");
            return msgDgst.digest(s.getBytes(StandardCharsets.UTF_8));
        }

        public String toHexStr(byte[] hash)
        {
            BigInteger no = new BigInteger(1, hash);
            StringBuilder hexStr = new StringBuilder(no.toString(16));
            while (hexStr.length() < 32) hexStr.insert(0, '0');
            return hexStr.toString();
        }
    }
//    private BroadcastReceiver geofenceReceiver = new BroadcastReceiver() {
//        public static final String ACTION_GEOFENCE_TRANSITION = "com.nebula.NebulaApp.GeofenceBroadcastReceiver.ACTION_GEOFENCE_TRANSITION";
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
//            if (geofencingEvent.hasError()) {
//                Log.d(TAG, "onReceive: Error receiving geofence event...");
//                return;
//            }
//
//            int transition = geofencingEvent.getGeofenceTransition();
//
//            if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
//                Log.d(TAG, "onReceive: ENTER");
//                locationParam.setText("You are inside the geofence");
//            } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
//                Log.d(TAG, "onReceive: EXIT");
//                locationParam.setText("You are outside the geofence");
//            }
//        }
//    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home,container,false);
        ImageButton scanQr = v.findViewById(R.id.scanQrCode);
        locationParam = (TextView)v.findViewById(R.id.locationParam);
        Toolbar toolbar = (Toolbar) requireActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        scanQr.setOnClickListener(v1 -> {
            IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(HomeFragment.this);
            intentIntegrator.setPrompt("Scan the QR Code");
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
//            intentIntegrator.setCaptureActivity(ZxingCaptureCustomActivity.class);
            intentIntegrator.setBeepEnabled(false);
            intentIntegrator.setOrientationLocked(true);
            intentIntegrator.initiateScan();
        });
        reference.child("Nebula").child("Institute").child("GGSP0369").child("Institute Secret Code").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    studentInstituteSecreteCode = String.valueOf(task.getResult().getValue());
                    Log.d("Institute Secrete code: ", String.valueOf(task.getResult().getValue()));
                }
            }
        });

//        geofencingClient = LocationServices.getGeofencingClient(requireActivity().getApplicationContext());
//        geofenceProvider = new GeofenceProvider();
//        LatLng collegeLatLng = new LatLng(19.99470415620573, 73.79992932149065);
//        addGeofence(collegeLatLng, 10);
//        requestLocationPermission();
//        geofencingClient = LocationServices.getGeofencingClient(getActivity());
//        geofenceProvider = new GeofenceProvider();
//        addGeofences();
        return v;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(GeofenceBroadcastReceiver.ACTION_GEOFENCE_TRANSITION);
//        getActivity().registerReceiver(geofenceReceiver, filter);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        getActivity().unregisterReceiver(geofenceReceiver);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        removeGeofences();
//    }
//
//    private void addGeofences() {
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            // Create a geofence with the desired college location and radius
//            LatLng collegeLatLng = new LatLng(18.6519, 73.7776); // Change this to your college location
//            float radius = 100; // Change this to your desired radius in meters
//            Geofence geofence = geofenceProvider.createGeofence(collegeLatLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
//
//            // Create a geofencing request with the geofence and the initial trigger
//            GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
//                    .addGeofence(geofence)
//                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT)
//                    .build();
//
//            // Create a pending intent that will be triggered when the geofence transitions occur
//            geofencePendingIntent = getGeofencePendingIntent();
//
//            // Add the geofence using the geofencing client
//            geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Log.d(TAG, "onSuccess: Geofence Added...");
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d(TAG, "onFailure: " + e.getMessage());
//                        }
//                    });
//        } else {
//            // Request location permission if not granted yet
//            requestLocationPermission();
//        }
//    }
//
//    private void removeGeofences() {
//        // Remove the geofence using the geofencing client and the pending intent
//        geofencingClient.removeGeofences(geofencePendingIntent)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "onSuccess: Geofence Removed...");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "onFailure: " + e.getMessage());
//                    }
//                });
//    }
//
//    private PendingIntent getGeofencePendingIntent() {
//        // Reuse the pending intent if we already have it
//        if (geofencePendingIntent != null) {
//            return geofencePendingIntent;
//        }
//        // Create an intent that will be handled by the GeofenceBroadcastReceiver class
//        Intent intent = new Intent(getActivity(), GeofenceBroadcastReceiver.class);
//        intent.setAction(GeofenceBroadcastReceiver.ACTION_GEOFENCE_TRANSITION);
//        // Create a pending intent that will start the broadcast receiver
//        geofencePendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        return geofencePendingIntent;
//    }
//
//    private void requestLocationPermission() {
//        // Check if the permission is already granted
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            // Do nothing, permission is already granted
//        } else {
//            // Request the permission
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        // Handle the result of the permission request
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission was granted, add the geofences
//                addGeofences();
//            } else {
//                // Permission was denied, show a message to the user
//                Toast.makeText(getActivity(), "Location permission is required for geofencing", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }




//    private void addGeofence(LatLng latLng, float radius) {
//        Geofence geofence = geofenceProvider.createGeofence(latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
//        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
//                .addGeofence(geofence)
//                .build();
//        PendingIntent pendingIntent = getGeofencePendingIntent();
//        if (ActivityCompat.checkSelfPermission(requireActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
//                .addOnSuccessListener((Activity) requireActivity().getApplicationContext(), new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "onSuccess: Geofence Added...");
//                    }
//                })
//                .addOnFailureListener((Activity) requireActivity().getApplicationContext(), new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "onFailure: " + e.getMessage());
//                    }
//                });
//    }
//    private PendingIntent getGeofencePendingIntent() {
//        Intent intent = new Intent(requireActivity().getApplicationContext(), geofenceBroadcastReceiver.getClass());
//        return PendingIntent.getBroadcast(requireActivity().getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//    }
    public static void setLocationText(String text) {
        locationParam.setText(text.toString());
    }
    public void runOnUI(Runnable runnable) {
        requireActivity().runOnUiThread(runnable);
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
                try {
                    studentInstituteSecreteCodeEncoded = sha.toHexStr(sha.obtainSHA(studentInstituteSecreteCode));
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("\n" + studentInstituteSecreteCode + " : " + studentInstituteSecreteCodeEncoded); // TODO: Testing
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
}