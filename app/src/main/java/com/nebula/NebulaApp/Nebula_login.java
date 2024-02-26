package com.nebula.NebulaApp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;


import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;


public class Nebula_login extends AppCompatActivity {
    EditText email_id_edit_text, password_edit_text;
    TextView forgot_password_text_view, sign_up_text_view;
    ImageButton sign_in_image_btn, google_image_button;
    DatabaseReference reference;
    ImageView pwd_show_hide;
    private FirebaseAuth auth;
    ProgressBar progressBar;
    View backgroundOverlay;
    FirebaseUser user;
    private CheckBox rememberMeCheckBox;
    public static final String SHARED_PREFS = "shared_prefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nebula_login);
        auth = FirebaseAuth.getInstance();
        pwd_show_hide = findViewById(R.id.imageView5);
        email_id_edit_text = findViewById(R.id.editTextTextEmailAddress_login);
        password_edit_text = findViewById(R.id.editTextTextPassword_login);
        sign_in_image_btn = findViewById(R.id.imageButton4);
        google_image_button = findViewById(R.id.imageButton2);
        forgot_password_text_view = findViewById(R.id.textView3);
        sign_up_text_view = findViewById(R.id.textView6);
        progressBar = findViewById(R.id.progressBar);
        backgroundOverlay = findViewById(R.id.backgroundOverlay);
        reference = FirebaseDatabase.getInstance().getReference();
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);

        // password show hide
        pwd_show_hide.setOnClickListener(v -> {
            if (password_edit_text.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                password_edit_text.setTransformationMethod(PasswordTransformationMethod.getInstance());
                //change icon
                pwd_show_hide.setImageResource(R.drawable.baseline_key_off_24);
            } else {
                password_edit_text.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                pwd_show_hide.setImageResource(R.drawable.baseline_key_24);
            }
        });
        // redirect Signup/register page
        sign_up_text_view.setOnClickListener(v -> {
            String Institute_ID = getIntent().getStringExtra("OnBoarding2_Institute_id");
            String designation = getIntent().getStringExtra("OnBoarding2_Student");
            Intent intent = new Intent(Nebula_login.this,Nebula_register.class);
            intent.putExtra("Login_Institute_id",Institute_ID);
            intent.putExtra("Login_Designation",designation);
            startActivity(intent);
            finish();
        });

        // signing button clicked
        sign_in_image_btn.setOnClickListener(v -> {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra("source_activity")) {
                String sourceActivity = intent.getStringExtra("source_activity");
                if (sourceActivity.equals("On_boarding_2")) {
                    String institute_id = getIntent().getStringExtra("OnBoarding2_Institute_id");
                    String Designation = getIntent().getStringExtra("OnBoarding2_Student");
                    String login_email = email_id_edit_text.getText().toString();
                    String login_password = password_edit_text.getText().toString();
                    String sanitizedEmail = login_email.replace(".", "_")
                            .replace("#", "_")
                            .replace("$", "_")
                            .replace("[", "_")
                            .replace("]", "_");

                    // calling validate email method to verify entered value in edittext is proper email or not
                    boolean validemail = validate_email(login_email);
                    @SuppressLint("HardwareIds") String android_device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    if (TextUtils.isEmpty(login_email)) {
                        Toast.makeText(Nebula_login.this, " Please enter email id. ", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (TextUtils.isEmpty(login_password)) {
                        Toast.makeText(Nebula_login.this, " Please enter password. ", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (TextUtils.isEmpty(login_email) && TextUtils.isEmpty(login_password)) {
                        Toast.makeText(Nebula_login.this, " Please enter email id and password. ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (validemail) {
                        auth = FirebaseAuth.getInstance();
                        auth.signInWithEmailAndPassword(login_email, login_password)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = auth.getCurrentUser();
                                        if (user.isEmailVerified()) {
                                            // Email is verified
                                            ValueEventListener postListener = new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    boolean emailAndroidIDFound = false;
                                                    boolean emailFound = false;
                                                    boolean emailnewuser = false;
                                                    boolean emailnotfound = false;
                                                    String Authentication_key = null;
                                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                        String email = childSnapshot.child("Personal Information").child("email").getValue(String.class);
                                                        String android_id = childSnapshot.child("Personal Information").child("userId").getValue(String.class);
                                                        boolean Newuser = Boolean.TRUE.equals(childSnapshot.child("Personal Information").child("newuser").getValue(boolean.class));
                                                        String authentication_key = childSnapshot.child("Personal Information").child("Authentication Key").getValue(String.class);
                                                        // Check if email and android ID match
                                                        System.out.println(Newuser);

                                                        if (email.equals(login_email) && android_id.equals(android_device_id) && Newuser != true) {
                                                            Authentication_key = authentication_key;
                                                            emailAndroidIDFound = true;
                                                            break;
                                                        }
                                                        if (email.equals(login_email) && android_id.equals(android_device_id) && Newuser == true) {
                                                            emailnewuser = true;
                                                            break;
                                                        }
                                                    }
                                                    if (emailnewuser) {
                                                        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sharedPref.edit();
                                                        editor.putString("Email", login_email);
                                                        editor.putBoolean("Remember me", rememberMeCheckBox.isChecked());
                                                        editor.putString("Institute_id", institute_id);
                                                        editor.putString("Designation", Designation);
                                                        editor.putString("sanitized_email", sanitizedEmail);
                                                        editor.apply();
                                                        Intent intent = new Intent(Nebula_login.this, Nebula_personal_information.class);
                                                        intent.putExtra("Login_Institute_id", institute_id);
                                                        intent.putExtra("Login_Snaitized_email", sanitizedEmail);
                                                        intent.putExtra("Login_Designation", Designation);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                    // If email and android ID combination is found, allow login
                                                    else if (emailAndroidIDFound) {
                                                        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sharedPref.edit();
                                                        editor.putString("Email", login_email);
                                                        editor.putBoolean("Remember me", rememberMeCheckBox.isChecked());
                                                        editor.putString("Institute_id", institute_id);
                                                        editor.putString("Designation", Designation);
                                                        editor.putString("sanitized_email", sanitizedEmail);
                                                        editor.putString("encoded_hashkey", Authentication_key);
                                                        editor.apply();
                                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Institute")
                                                                .child("Student").child(sanitizedEmail).child("Personal Information");
                                                        storePersonalInformationInSharedPreferences(ref);
                                                        Intent intent = new Intent(Nebula_login.this, MainActivity.class);
                                                        intent.putExtra("source_activity", "Nebula_login");
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                    else{
                                                        Toast.makeText(Nebula_login.this, "Sorry can't login.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    // Getting data failed, log a message
                                                    Log.w(TAG, "loadData:onCancelled", databaseError.toException());
                                                }
                                            };
                                            reference.child("Institute").child(institute_id).child(Designation).addListenerForSingleValueEvent(postListener);
                                        } else {
                                            // Email not verified, send verification email
                                            user.sendEmailVerification()
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            Toast.makeText(Nebula_login.this, "Please verify your email. Email sent to " + login_email, Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                        }
                                    } else {
                                        // Sign-in failed
                                        Toast.makeText(Nebula_login.this, "Email or password is incorrect.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        Toast.makeText(this, "Enter proper email address.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (sourceActivity.equals("Nebula_register")) {
                    String institute_id = getIntent().getStringExtra("Register_Institute_id");
                    String Designation = getIntent().getStringExtra("Register_designation");
                    String login_email = email_id_edit_text.getText().toString();
                    String login_password = password_edit_text.getText().toString();
                    String sanitizedEmail = login_email.replace(".", "_")
                            .replace("#", "_")
                            .replace("$", "_")
                            .replace("[", "_")
                            .replace("]", "_");

                    // calling validate email method to verify entered value in edittext is proper email or not
                    boolean validemail = validate_email(login_email);
                    @SuppressLint("HardwareIds") String android_device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    if (TextUtils.isEmpty(login_email)) {
                        Toast.makeText(Nebula_login.this, " Please enter email", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (TextUtils.isEmpty(login_password)) {
                        Toast.makeText(Nebula_login.this, " Please enter password", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (TextUtils.isEmpty(login_email) && TextUtils.isEmpty(login_password)) {
                        Toast.makeText(Nebula_login.this, " Please enter email and password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (validemail) {
                        auth = FirebaseAuth.getInstance();
                        auth.signInWithEmailAndPassword(login_email, login_password)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = auth.getCurrentUser();
                                        if (user.isEmailVerified()) {
                                            // Email is verified
                                            ValueEventListener postListener = new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    boolean emailAndroidIDFound = false;
                                                    boolean emailFound = false;
                                                    boolean emailnewuser = false;
                                                    boolean emailnotfound = false;
                                                    String Authentication_key = null;
                                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                        String email = childSnapshot.child("Personal Information").child("email").getValue(String.class);
                                                        String android_id = childSnapshot.child("Personal Information").child("userId").getValue(String.class);
                                                        boolean Newuser = Boolean.TRUE.equals(childSnapshot.child("Personal Information").child("newuser").getValue(boolean.class));
                                                        String authentication_key = childSnapshot.child("Personal Information").child("Authentication Key").getValue(String.class);
                                                        // Check if email and android ID match
                                                        System.out.println(Newuser);

                                                        if (email.equals(login_email) && android_id.equals(android_device_id) && Newuser != true) {
                                                            Authentication_key = authentication_key;
                                                            emailAndroidIDFound = true;
                                                            break;
                                                        }
                                                        if (email.equals(login_email) && android_id.equals(android_device_id) && Newuser == true) {
                                                            emailnewuser = true;
                                                            break;
                                                        }
                                                    }
                                                    if (emailnewuser) {
                                                        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sharedPref.edit();
                                                        editor.putString("Email", login_email);
                                                        editor.putBoolean("Remember me", rememberMeCheckBox.isChecked());
                                                        editor.putString("Institute_id", institute_id);
                                                        editor.putString("Designation", Designation);
                                                        editor.putString("sanitized_email", sanitizedEmail);
                                                        editor.apply();
                                                        Intent intent = new Intent(Nebula_login.this, Nebula_personal_information.class);
                                                        intent.putExtra("Login_Institute_id", institute_id);
                                                        intent.putExtra("Login_Snaitized_email", sanitizedEmail);
                                                        intent.putExtra("Login_Designation", Designation);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                    // If email and android ID combination is found, allow login
                                                    else if (emailAndroidIDFound) {
                                                        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sharedPref.edit();
                                                        editor.putString("Email", login_email);
                                                        editor.putBoolean("Remember me", rememberMeCheckBox.isChecked());
                                                        editor.putString("Institute_id", institute_id);
                                                        editor.putString("Designation", Designation);
                                                        editor.putString("sanitized_email", sanitizedEmail);
                                                        editor.putString("encoded_hashkey", Authentication_key);
                                                        editor.apply();
                                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Institute")
                                                                .child("Student").child(sanitizedEmail).child("Personal Information");
                                                        storePersonalInformationInSharedPreferences(ref);
                                                        Intent intent = new Intent(Nebula_login.this, MainActivity.class);
                                                        intent.putExtra("source_activity", "Nebula_login");
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                    else{
                                                        Toast.makeText(Nebula_login.this, "Sorry! can't login.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    // Getting data failed, log a message
                                                    Log.w(TAG, "loadData:onCancelled", databaseError.toException());
                                                }
                                            };
                                            reference.child("Institute").child(institute_id).child(Designation).addListenerForSingleValueEvent(postListener);
                                        } else {
                                            // Email not verified, send verification email
                                            user.sendEmailVerification()
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            Toast.makeText(Nebula_login.this, "Please verify your email. Email sent to " + login_email, Toast.LENGTH_LONG).show();
                                                            Intent intent1 = new Intent(getApplicationContext(), Nebula_login.class);
                                                            startActivity(intent1);
                                                        }
                                                    });
                                        }
                                    } else {
                                        // Sign-in failed
                                        Toast.makeText(Nebula_login.this, "Email or password is incorrect", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        Toast.makeText(this, "Enter valid email address", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
        // google login
        google_image_button.setOnClickListener(v ->{
            try {
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.GoogleBuilder().build());

                // Create and launch sign-in intent
                Intent signInIntent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build();
                signInLauncher.launch(signInIntent);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String institute_id = getIntent().getStringExtra("OnBoarding2_Institute_id");
            String Designation = getIntent().getStringExtra("OnBoarding2_Student");
            String login_email = response.getEmail();
            String sanitizedEmail = login_email.replace(".", "_")
                    .replace("#", "_")
                    .replace("$", "_")
                    .replace("[", "_")
                    .replace("]", "_");
            @SuppressLint("HardwareIds") String android_device_id = Settings.Secure.
                    getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean emailAndroidIDFound_newuser_false = false;
                    boolean emailAndroidIDFound_newuser_true = false;
                    boolean emailFound = false;
                    boolean emailnewuser = false;
                    boolean email_androidid_notfound = false;
                    String Authentication_key = null;
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String email = childSnapshot.child("Personal Information").child("email").getValue(String.class);
                        String android_id = childSnapshot.child("Personal Information").child("userId").getValue(String.class);

                        if (email.equals(login_email) && android_id.equals(android_device_id)) {
                            boolean Newuser = Boolean.TRUE.equals(childSnapshot.child("Personal Information").child("newuser").getValue(boolean.class));
                            if (Newuser){
                                emailAndroidIDFound_newuser_true = true;
                            }
                            else{
                                String authentication_key = childSnapshot.child("Personal Information").child("Authentication Key").getValue(String.class);
                                Authentication_key = authentication_key;
                                emailAndroidIDFound_newuser_false = true;
                            }
                            break;

                        }
                        if (!email.equals(login_email) && !android_id.equals(android_device_id)) {
                            email_androidid_notfound = true;
                        }
                    }
                    if (emailAndroidIDFound_newuser_true){
                        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("Email", login_email);
                        editor.putBoolean("Remember me", rememberMeCheckBox.isChecked());
                        editor.putString("Institute_id", institute_id);
                        editor.putString("Designation", Designation);
                        editor.putString("sanitized_email", sanitizedEmail);
                        editor.apply();
                        Intent intent = new Intent(Nebula_login.this, Nebula_personal_information.class);
                        intent.putExtra("Login_Institute_id", institute_id);
                        intent.putExtra("Login_Snaitized_email", sanitizedEmail);
                        intent.putExtra("Login_Designation", Designation);
                        startActivity(intent);
                        finish();
                    } else if (emailAndroidIDFound_newuser_false) {
                        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("Email", login_email);
                        editor.putBoolean("Remember me", rememberMeCheckBox.isChecked());
                        editor.putString("Institute_id", institute_id);
                        editor.putString("Designation", Designation);
                        editor.putString("sanitized_email", sanitizedEmail);
                        editor.putString("encoded_hashkey", Authentication_key);
                        editor.apply();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Institute")
                                .child("Student").child(sanitizedEmail).child("Personal Information");
                        storePersonalInformationInSharedPreferences(ref);
                        Intent intent = new Intent(Nebula_login.this, MainActivity.class);
                        intent.putExtra("source_activity", "Nebula_login");
                        startActivity(intent);
                        finish();
                    }
                    else if (email_androidid_notfound){
                        boolean Newuser = true;
                        writeNewUser(Newuser,institute_id, android_device_id, login_email,Designation);
                        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("Email", login_email);
                        editor.putBoolean("Remember me", rememberMeCheckBox.isChecked());
                        editor.putString("Institute_id", institute_id);
                        editor.putString("Designation", Designation);
                        editor.putString("sanitized_email", sanitizedEmail);
                        editor.apply();
                        Intent intent = new Intent(Nebula_login.this, Nebula_personal_information.class);
                        intent.putExtra("Login_Institute_id", institute_id);
                        intent.putExtra("Login_Snaitized_email", sanitizedEmail);
                        intent.putExtra("Login_Designation", Designation);
                        startActivity(intent);
                        finish();
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting data failed, log a message
                    Log.w(TAG, "loadData:onCancelled", databaseError.toException());
                }
            };
            reference.child("Institute").child(institute_id).child(Designation).addListenerForSingleValueEvent(postListener);
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }
    private boolean validate_email(String email) {
        // Valid email format
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public void writeNewUser( boolean newuser ,String Institute_ID,String userId, String email,String Designation) {
        String sanitizedEmail = email.replace(".", "_")
                .replace("#", "_")
                .replace("$", "_")
                .replace("[", "_")
                .replace("]", "_");

        Student user = new Student(newuser,email, userId);
        reference.child("Institute").child(Institute_ID).child(Designation).child(sanitizedEmail).child("Personal Information").setValue(user);
    }
    private void storePersonalInformationInSharedPreferences(DatabaseReference ref) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    for (DataSnapshot fieldSnapshot : dataSnapshot.getChildren()) {
                        String fieldName = fieldSnapshot.getKey();
                        String fieldValue = fieldSnapshot.getValue(String.class);

                        editor.putString(fieldName, fieldValue);
                    }
                    editor.apply();

                    Toast.makeText(getApplicationContext(), "Personal information stored in SharedPreferences", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "No personal information found for the user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error retrieving personal information: " + databaseError.getMessage());
            }
        });
    }

}
//    SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
//    SharedPreferences.Editor editor = sharedPref.edit();
//                                                editor.putString("Email", login_email);
//                                                editor.putBoolean("Remember me", isChecked);
//                                                editor.putString("Institute_id", institute_id);
//                                                editor.putString("Designation", Designation);
//                                                editor.apply();
