package com.nebula.NebulaApp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class Nebula_register extends AppCompatActivity {
    TextView login_redirect_text_view,pwd,already_tv;
    ImageButton sign_up_image_button,google_sign_up_image_button;
    EditText sign_up_email_edit_text,sign_up_password_edit_text,confirm_password_edit_text;
    ImageView pwd_show_hide,confirm_pwd_show_hide;
    DatabaseReference reference;
    private FirebaseAuth auth;
    FirebaseUser user;
    public static final String SHARED_PREFS = "shared_prefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nebula_register);
        login_redirect_text_view = findViewById(R.id.login_redirect_textview);
        sign_up_image_button = findViewById(R.id.imageButton4);
        google_sign_up_image_button = findViewById(R.id.imageButton2);
        sign_up_email_edit_text = findViewById(R.id.editTextTextEmailAddress);
        sign_up_password_edit_text = findViewById(R.id.editTextTextPassword);
        confirm_password_edit_text = findViewById(R.id.editTextTextPassword2);
        pwd_show_hide = findViewById(R.id.imageView5);
        confirm_pwd_show_hide = findViewById(R.id.imageView6);
        auth = FirebaseAuth.getInstance();
        pwd_show_hide.setImageResource(R.drawable.baseline_key_off_24);
        confirm_pwd_show_hide.setImageResource(R.drawable.baseline_key_off_24);
        pwd = findViewById(R.id.textView7);
        already_tv = findViewById(R.id.login_redirect);
        //password show hide
        pwd_show_hide.setOnClickListener(v -> {
            if (sign_up_password_edit_text.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                sign_up_password_edit_text.setTransformationMethod(PasswordTransformationMethod.getInstance());
                //change icon
                pwd_show_hide.setImageResource(R.drawable.baseline_key_off_24);
            }
            else {
                sign_up_password_edit_text.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                pwd_show_hide.setImageResource(R.drawable.baseline_key_24);
            }
        });
        //confirm password show hide
        confirm_pwd_show_hide.setOnClickListener(v -> {
            if (confirm_password_edit_text.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                confirm_password_edit_text.setTransformationMethod(PasswordTransformationMethod.getInstance());
                //change icon
                confirm_pwd_show_hide.setImageResource(R.drawable.baseline_key_off_24);
            }
            else {
                confirm_password_edit_text.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                confirm_pwd_show_hide.setImageResource(R.drawable.baseline_key_24);
            }
        });
        //redirect to login page
        login_redirect_text_view.setOnClickListener(v -> {
            String institute_id = getIntent().getStringExtra("Login_Institute_id");
            String Designation = getIntent().getStringExtra("Login_Designation");
            Intent intent = new Intent(Nebula_register.this, Nebula_login.class);
            intent.putExtra("Register_Institute_id",institute_id);
            intent.putExtra("Register_designation",Designation);
            intent.putExtra("source_activity", "Nebula_register");
            startActivity(intent);
        });
        sign_up_password_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString();
                if (!PasswordValidator.isValidPassword(password)) {
                    // Show error message indicating password requirements
                    google_sign_up_image_button.setVisibility(View.GONE);
                    login_redirect_text_view.setVisibility(View.GONE);
                    already_tv.setVisibility(View.GONE);
                    pwd.setText("Password must contain at least 8 characters.\n1 special character.\n1 uppercase letter.\n1 lowercase letter.\n1 digit.");
                } else {
                    pwd.setText(null); // Clear the error
                    google_sign_up_image_button.setVisibility(View.VISIBLE);
                    login_redirect_text_view.setVisibility(View.VISIBLE);
                    already_tv.setVisibility(View.VISIBLE);
                }
            }
        });
        // sign up button clicked
        sign_up_image_button.setOnClickListener(v ->{
            reference = FirebaseDatabase.getInstance().getReference();
            String email = sign_up_email_edit_text.getText().toString();
            String password = sign_up_password_edit_text.getText().toString();
            String Confirm_pwd = confirm_password_edit_text.getText().toString();
            String institute_id = getIntent().getStringExtra("Login_Institute_id");
            String Designation = getIntent().getStringExtra("Login_Designation");
            @SuppressLint("HardwareIds") String android_device_id = Settings.Secure.getString(getContentResolver()
                    , Settings.Secure.ANDROID_ID);
            boolean valid_email = validate_email(email);
            if (!valid_email){
                Toast.makeText(this, "Enter proper email address.", Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                Toast.makeText(Nebula_register.this, " Please enter email id and password. ", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(Confirm_pwd) && TextUtils.isEmpty(password)) {
                Toast.makeText(Nebula_register.this, " Password required. ", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(Nebula_register.this, " Please enter password. ", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(Confirm_pwd)) {
                Toast.makeText(Nebula_register.this, " Please fill confirm password. ", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(email)) {
                Toast.makeText(Nebula_register.this, " Please enter email id. ", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (!Confirm_pwd.equals(password)) {
                Toast.makeText(Nebula_register.this, "Both password does not match.", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (!TextUtils.isEmpty(email) && valid_email) {
                try{
                auth = FirebaseAuth.getInstance();
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(signInTask -> {
                            if (signInTask.isSuccessful()) {
                                // Email is already registered
                                Toast.makeText(Nebula_register.this, "Email is already registered. Please log in.", Toast.LENGTH_SHORT).show();
                                // Handle accordingly, maybe redirect to login activity
                            } else {
                                // Email is not registered, proceed with registration
                                auth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(registerTask -> {
                                            if (registerTask.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                auth.getCurrentUser().sendEmailVerification()
                                                        .addOnCompleteListener(emailVerificationTask -> {
                                                            if (emailVerificationTask.isSuccessful()) {
                                                                boolean newuser = true;
                                                                writeNewUser(newuser,institute_id, android_device_id, email,Designation);
                                                                Toast.makeText(Nebula_register.this, "Registered successfully.\nPlease verify your email.", Toast.LENGTH_LONG).show();
                                                                Intent intent1 = new Intent(getApplicationContext(), Nebula_login.class);
                                                                startActivity(intent1);
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(Nebula_register.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();

            }

        }

        });
    }
    private boolean validate_email(String email) {
        // Valid email format
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private void getInstituteID_Designation() {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);


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
    public static class PasswordValidator {

        private static final Pattern PASSWORD_PATTERN =
                Pattern.compile("^" +
                        "(?=.*[0-9])" +         // at least 1 digit
                        "(?=.*[a-z])" +         // at least 1 lowercase letter
                        "(?=.*[A-Z])" +         // at least 1 uppercase letter
                        "(?=.*[@#$%^&+=])" +    // at least 1 special character
                        "(?=\\S+$)" +           // no white spaces
                        ".{8,}" +               // at least 8 characters
                        "$");

        public static boolean isValidPassword(String password) {
            return PASSWORD_PATTERN.matcher(password).matches();
        }
    }
}