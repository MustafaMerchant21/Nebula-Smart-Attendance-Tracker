    package com.nebula.NebulaApp;

    import static android.content.Context.MODE_APPEND;
    import static android.content.Context.MODE_PRIVATE;
    import static androidx.core.location.LocationManagerCompat.getCurrentLocation;

    import android.annotation.SuppressLint;
    import android.app.PendingIntent;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.SharedPreferences;
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
    import android.security.keystore.KeyGenParameterSpec;
    import android.security.keystore.KeyProperties;
    import android.util.Base64;
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
    import androidx.appcompat.app.AlertDialog;
    import androidx.appcompat.widget.Toolbar;
    import androidx.biometric.BiometricPrompt;
    import androidx.core.app.ActivityCompat;
    import androidx.core.content.ContextCompat;
    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentActivity;

    import com.google.android.gms.location.FusedLocationProviderClient;
    import com.google.android.gms.location.Geofence;
    import com.google.android.gms.location.GeofencingClient;
    import com.google.android.gms.location.GeofencingRequest;
    import com.google.android.gms.location.LocationServices;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;
    import com.google.zxing.integration.android.IntentIntegrator;
    import com.google.zxing.integration.android.IntentResult;

    import java.io.IOException;
    import java.math.BigInteger;
    import java.nio.charset.StandardCharsets;
    import java.security.KeyPairGenerator;
    import java.security.KeyStore;
    import java.security.MessageDigest;
    import java.security.NoSuchAlgorithmException;
    import java.text.DateFormat;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.Date;
    import java.util.List;
    import java.util.Locale;
    import java.util.Map;
    import java.util.concurrent.Executor;


    public class HomeFragment extends Fragment {
        @SuppressLint("StaticFieldLeak")
        static TextView locationParam, userFirstName;
        private Context fragmentContext;
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
        AttendaneOperations attendaneOperations;
        LastNodeKeyNameRetriever lastNodeKeyNameRetriever;

        private static final int REQUEST_LOCATION_PERMISSION = 1;
        private FusedLocationProviderClient fusedLocationProviderClient;
        private LocationManager locationManager;
        private LocationListener locationListener;
        String studentInstituteSecreteCode; // Fetch institute secret code of student inside this var;

        private boolean showDialog = true;
        private static final int REQUEST_CODE = 5;
        private Executor executor;
        private BiometricPrompt biometricPrompt;
        private BiometricPrompt.PromptInfo promptInfo;
        private static final String HASH_KEY_ALIAS = "biometric_hash_key";
        private String hashKey = null;
        private String Stored_hash_key;
        public static final String SHARED_PREFS = "shared_prefs";
        SharedPreferences sharedPref;


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
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            fragmentContext = context; // Store the context when fragment is attached
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_home, container, false);
            scanQr = v.findViewById(R.id.scanQrCode);
            currentTimeTv = (TextView) v.findViewById(R.id.currentTime);
            currentDateTv = (TextView) v.findViewById(R.id.currentDate);
            locationParam = (TextView) v.findViewById(R.id.locationParam);
            userFirstName = (TextView) v.findViewById(R.id.userFirstName);
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
//            showWarningDialog();

            sharedPref = this.requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            storePersonalInformationInSharedPreferences(reference.child("Institute").child(sharedPref.getString("Institute_id",""))
                    .child("Student").child(sharedPref.getString("sanitized_email","")).child("Personal Information"));
            String uname = sharedPref.getString("Firstname","");
            userFirstName.setText(String.format(" %s\uD83D\uDC4B", uname));
//            if (isAdded()) {

                scanQr.setOnClickListener(v1 -> {
                    if (scanQr.isEnabled()) {
                        checkBiometricAuthentication();
                        IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(HomeFragment.this);
                        intentIntegrator.setPrompt("Scan the QR Code");
                        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                        intentIntegrator.setBeepEnabled(false);
                        intentIntegrator.setOrientationLocked(true);
                        intentIntegrator.initiateScan();
                    } else {
                        Toast.makeText(requireActivity().getApplicationContext(), "Enable location to Mark Attendance", Toast.LENGTH_SHORT).show();
                    }
                });
                locationContainer.setOnClickListener(v12 -> {
                    if (scanQr.isEnabled()) {
                        scanQr.setEnabled(false);
                        scanQr.setAlpha(.3f);
                    } else {
                        scanQr.setEnabled(true);
                        scanQr.setAlpha(1.0f);
                    }
                });
                reference.child("Institute").child(sharedPref.getString("Institute_id", "")).child("Institute Secret Code").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                //attendance operations
                //Todo Fetch sanitized Email and institute code from user's Shared Preferences >>>
                String instID = sharedPref.getString("Institute_id", "");
                String sanEmail = sharedPref.getString("sanitized_email", "");



                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity().getApplicationContext());
                // Check for location permission
                if (ActivityCompat.checkSelfPermission(requireActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Request permission
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                } else {
                    // Permission already granted, get location
                    getCurrentLocation();
                }

                // Request location updates from the location manager
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (getContext() != null) { // Check if the fragment is attached to a context
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            PointF coordinates = new PointF((float) latitude, (float) longitude);
                            Geocoder geocoder = new Geocoder(fragmentContext, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                if (addresses != null && addresses.size() > 0) {
                                    Address address = addresses.get(0);
                                    String addressName = address.getFeatureName();
                                    Log.d("Location_Fetched", addressName);
                                    locationParam.setText(addressName);
                                    boolean iteration = true;
                                    changeQRCodeButtonState(locationAlgorithm.abc(coordinates));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.e(TAG, "Fragment not attached to a context");
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
//            attendaneOperations.markAttendance(true);
//            Log.d("======= HomeFragment: =======",String.valueOf(attendaneOperations.getAllAttendedPer())); //Todo why returns 0.0

                //Biometrics Implementation >>>
                reference = FirebaseDatabase.getInstance().getReference();
                executor = ContextCompat.getMainExecutor(requireActivity().getApplicationContext());
                biometricPrompt = new BiometricPrompt(HomeFragment.this,
                        executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(requireActivity().getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(
                            @NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Toast.makeText(requireActivity().getApplicationContext(), "Authentication succeeded.", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(requireActivity().getApplicationContext(), "Authentication failed",
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                });
                promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Nebula")
                        .setSubtitle("Authenticate Yourself")
                        .setNegativeButtonText("Cancel")
                        .build();
//            }
            attendaneOperations = new AttendaneOperations(sharedPref.getString("sanitized_email", ""), sharedPref.getString("Institute_id", ""));
            lastNodeKeyNameRetriever = new LastNodeKeyNameRetriever(reference.child("Institute").child(sharedPref.getString("Institute_id",""))
                    .child("Student").child(sharedPref.getString("sanitized_email","")).child("Attendance Data"));
            lastNodeKeyNameRetriever.getLastNodeKeyNames((lastYear, lastMonth, lastDay) -> {
                attendaneOperations.checkNewDay(lastYear, lastMonth, lastDay);
            });
            return v;
        }

        //Biometrics Implementation
        private String generateHashKey() {
            try {
                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);
                if (!keyStore.containsAlias(HASH_KEY_ALIAS)) {
                    KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(HASH_KEY_ALIAS,
                            KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                            .setDigests(KeyProperties.DIGEST_SHA256)
                            .setUserAuthenticationRequired(true)
                            .build();
                    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
                    keyPairGenerator.initialize(keyGenParameterSpec);
                    keyPairGenerator.generateKeyPair();
                }

                // Generate hash key
                byte[] hash = generateHash("Your secret data".getBytes(StandardCharsets.UTF_8));
                hashKey = Base64.encodeToString(hash, Base64.DEFAULT);
                Log.d(TAG, "Hash key generated: " + hashKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return hashKey;
        }

        private byte[] generateHash(byte[] input) throws NoSuchAlgorithmException {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(input);
        }
        private void write_user_authentication_info(String Designation, String sanitizedEmail, String instituteID, String Authentication_Key) {
            // Create a reference to the "Personal Information" node for the specific user
            DatabaseReference personalInfoRef = reference.child("Institute").child(instituteID)
                    .child(Designation).child(sanitizedEmail).child("Personal Information");


            // Retrieve existing data from the database
            personalInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // If dataSnapshot exists and has children
                    if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                        // Get the current data
                        Map<String, Object> currentData = (Map<String, Object>) dataSnapshot.getValue();

                        // Create a Post object with the provided data
                        PostEncodedHashkey post = new PostEncodedHashkey( sanitizedEmail, instituteID, Authentication_Key);

                        // Convert the Post object to a map
                        Map<String, Object> newPostValue = post.toMap();

                        // Append the new data to the existing data
                        currentData.putAll(newPostValue);

                        // Update the "Personal Information" node with the updated data
                        personalInfoRef.setValue(currentData);
                    } else {
                        // If no existing data, simply set the new data
                        PostEncodedHashkey post = new PostEncodedHashkey( sanitizedEmail, instituteID,Authentication_Key);

                        // Convert the Post object to a map
                        Map<String, Object> newPostValue = post.toMap();

                        // Update the "Personal Information" node with the new data
                        personalInfoRef.setValue(newPostValue);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                    Log.e(TAG, "Error appending data to Personal Information: " + databaseError.getMessage());
                    Toast.makeText(requireActivity().getApplicationContext(), "Error appending data to Personal Information: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        private void checkSourceActivity() {
            Bundle b = this.getArguments();
            String intent = b.getString("source_activity");
            if (intent != null && intent.equals("Nebula_personal_information")) {
                // Show warning dialog for Nebula_personal_information
                showDialog = true;
                showWarningDialog();
            } else {
                showDialog = false;
                checkBiometricAuthentication();
            }
        }


        private void showWarningDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity().getApplicationContext(), R.style.CustomAlertDialogTheme);
            builder.setTitle("Warning")
                    .setMessage("Your current fingerprint is permanent and final for future app logins. Changes require admin approval.")
                    .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (showDialog) {
                                checkBiometricAuthentication();
                            }
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();
        }
        private void checkBiometricAuthentication() {
            if (showDialog) {
                biometricPrompt.authenticate(promptInfo);
            }
        }
//    Intent intent = getIntent();
//                if (intent != null && intent.hasExtra("source_activity")) {
//        String sourceActivity = intent.getStringExtra("source_activity");
//        if (sourceActivity.equals("Nebula_personal_information")) {
//            String encoded_hashkey,app_encoded_hashkey;
//            Stored_hash_key = generateHashKey();
//            SHAEncoding sha = new SHAEncoding();
//            try { //TODO: Convert str to encrypted-str >>>
//                SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
//                String college_id = sharedPref.getString("Institute_id","");
//                String Designation = sharedPref.getString("Designation","");
//                String Email = sharedPref.getString("Email","");
//                String sanitizedEmail = Email.replace(".", "_")
//                        .replace("#", "_")
//                        .replace("$", "_")
//                        .replace("[", "_")
//                        .replace("]", "_");
//                encoded_hashkey = sha.toHexStr(sha.obtainSHA(Stored_hash_key));
//                SharedPreferences.Editor editor = sharedPref.edit();
//                editor.putString("encoded_hashkey", encoded_hashkey);
//                editor.apply();
//                write_user_authentication_info(Designation,sanitizedEmail,college_id,encoded_hashkey);
//                Toast.makeText(getApplicationContext(),
//                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
//            } catch (NoSuchAlgorithmException e) {
//                throw new RuntimeException(e);
//            }
//        } else if (sourceActivity.equals("Nebula_login")) {
//            // Handle checking the hashkey for Nebula_login
//            SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//            String storedEncodedHashkey = sharedPref.getString("encoded_hashkey", "");
//            String generatedHashkey = generateHashKey();
//            SHAEncoding sha = new SHAEncoding();
//            String generatedEncodedHashkey = null;
//            try {
//                generatedEncodedHashkey = sha.toHexStr(sha.obtainSHA(generatedHashkey));
//            } catch (NoSuchAlgorithmException e) {
//                throw new RuntimeException(e);
//            }
//            if (storedEncodedHashkey.equals(generatedEncodedHashkey)) {
//                // Hashkeys match, provide access to Nebula_dashboard
//                Toast.makeText(getApplicationContext(),
//                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
//                Intent nebulaDashboardIntent = new Intent(Nebula_dashboard.this, Nebula_dashboard.class);
//                startActivity(nebulaDashboardIntent);
//                finish(); // Finish login activity to prevent user from going back
//            } else {
//                // Hashkeys don't match, show authentication failed message
//                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
//                // Increment authentication attempt counter and handle lockout logic if needed
//            }
//        }
//        else {
//            // Handle checking the hashkey for Nebula_login
//            SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//            String storedEncodedHashkey = sharedPref.getString("encoded_hashkey", "");
//            String generatedHashkey = generateHashKey();
//            SHAEncoding sha = new SHAEncoding();
//            String generatedEncodedHashkey = null;
//            try {
//                generatedEncodedHashkey = sha.toHexStr(sha.obtainSHA(generatedHashkey));
//            } catch (NoSuchAlgorithmException e) {
//                throw new RuntimeException(e);
//            }
//
//            if (storedEncodedHashkey.equals(generatedEncodedHashkey)) {
//                // Hashkeys match, provide access to Nebula_dashboard
//                Toast.makeText(getApplicationContext(),
//                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
//                Intent nebulaDashboardIntent = new Intent(Nebula_dashboard.this, Nebula_dashboard.class);
//                startActivity(nebulaDashboardIntent);
//                finish(); // Finish login activity to prevent user from going back
//            } else {
//                // Hashkeys don't match, show authentication failed message
//                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
//                // Increment authentication attempt counter and handle lockout logic if needed
//            }
//        }
//    }

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
            Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
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

        public void storePersonalInformationInSharedPreferences(DatabaseReference ref) {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        for (DataSnapshot fieldSnapshot : dataSnapshot.getChildren()) {
                            String fieldName = fieldSnapshot.getKey();
                            Object fieldValue = fieldSnapshot.getValue();

                            // Handle different field types
                            if (fieldValue instanceof Boolean) {
                                editor.putBoolean(fieldName, (Boolean) fieldValue);
                            } else if (fieldValue instanceof String) {
                                editor.putString(fieldName, (String) fieldValue);
                            } else if (fieldValue instanceof Long) {
                                editor.putLong(fieldName, (Long) fieldValue);
                            } else if (fieldValue instanceof Integer) {
                                editor.putInt(fieldName, (Integer) fieldValue);
                            }
//                            Log.d(fieldName,String.valueOf(fieldValue));
                        }
                        editor.apply();

//                        Toast.makeText(requireActivity().getApplicationContext(), "Personal information stored in SharedPreferences", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireActivity().getApplicationContext(), "No personal information found for the user", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase", "Error retrieving personal information: " + databaseError.getMessage());
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
                        AttendanceFragment mFragment = new AttendanceFragment();
                        Bundle args = new Bundle();
                        args.putString("isMarked", "True");
                        mFragment.setArguments(args);
                        attendaneOperations.markAttendance(true);
                        Toast.makeText(requireActivity().getApplicationContext(),"Attendance Marked Successfully!",Toast.LENGTH_LONG).show();
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, mFragment).commit();
                    }else{
                        attendaneOperations.markAttendance(false);
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
