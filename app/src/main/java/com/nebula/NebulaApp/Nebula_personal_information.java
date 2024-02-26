package com.nebula.NebulaApp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Map;

public class Nebula_personal_information extends AppCompatActivity {
    EditText firstname,middlename,lastname,DOB,mobile,email,college_id,college_name;
    Spinner semester,year_of_study,course,gender;
    ImageButton proceed;
    ImageView dateofbirt;
    String selectedGender,selectedsemester,selectedcourse,selectedyear_of_study;
    DatabaseReference reference;
    public static final String SHARED_PREFS = "shared_prefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nebula_personal_information);
        firstname = findViewById(R.id.First_name);
        middlename = findViewById(R.id.Middle_name);
        lastname = findViewById(R.id.Last_name);
        DOB = findViewById(R.id.Date_of_birth);
        mobile = findViewById(R.id.Mobile);
        email = findViewById(R.id.Email);
        semester = findViewById(R.id.semester_option);
        year_of_study = findViewById(R.id.year_of_study);
        course = findViewById(R.id.coursespinner);
        gender = findViewById(R.id.genderSpinner);
        proceed = findViewById(R.id.proceed);
        dateofbirt = findViewById(R.id.DOB);
        college_id = findViewById(R.id.College_id);
        college_name = findViewById(R.id.College_Name);
        reference = FirebaseDatabase.getInstance().getReference();

        firstname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((EditText) v).setBackgroundResource(R.drawable.rectangle_1); // Reset border color
                }
            }
        });
        middlename.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((EditText) v).setBackgroundResource(R.drawable.rectangle_1); // Reset border color
                }
            }
        });
        lastname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((EditText) v).setBackgroundResource(R.drawable.rectangle_1); // Reset border color
                }
            }
        });
        DOB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((EditText) v).setBackgroundResource(R.drawable.rectangle_1); // Reset border color
                }
            }
        });
        mobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((EditText) v).setBackgroundResource(R.drawable.rectangle_1); // Reset border color
                }
            }
        });

        // by default setting college id and college name
        set_college_id_and_name(college_id,college_name,email);
        // gender
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGender = parent.getItemAtPosition(position).toString();
                gender.setBackgroundResource(R.drawable.rectangle_1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where nothing is selected...
            }
        });
        //course
        course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedcourse = parent.getItemAtPosition(position).toString();
                course.setBackgroundResource(R.drawable.rectangle_1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where nothing is selected...
            }
        });
        //semester
        semester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedsemester = parent.getItemAtPosition(position).toString();
                semester.setBackgroundResource(R.drawable.rectangle_1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where nothing is selected...
            }
        });
        // year of study
        year_of_study.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedyear_of_study = parent.getItemAtPosition(position).toString();
                year_of_study.setBackgroundResource(R.drawable.rectangle_1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where nothing is selected...
            }
        });


        // select DOB
        DOB.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // on below line we are creating a variable for date picker dialog.
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    Nebula_personal_information.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // on below line we are setting date to our text view.
                            DOB.setText(dayOfMonth +"/"+(monthOfYear + 1)+"/"+year);

                        }
                    },
                    year, month, day);
            datePickerDialog.show();
        });
        dateofbirt.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // on below line we are creating a variable for date picker dialog.
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    Nebula_personal_information.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // on below line we are setting date to our text view.
                            DOB.setText(dayOfMonth +"/"+(monthOfYear + 1)+"/"+year);

                        }
                    },
                    year, month, day);
            datePickerDialog.show();
        });
        DOB.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No implementation needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No implementation needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 2 || s.length() == 5) {
                    if (s.length() == 2 && s.charAt(1) != '/') {
                        s.insert(2, "/");
                        DOB.setText(s);
                        DOB.setSelection(DOB.getText().length());
                    } else if (s.length() == 5 && s.charAt(4) != '/') {
                        s.insert(5, "/");
                        DOB.setText(s);
                        DOB.setSelection(DOB.getText().length());
                    }
                }
            }
        });
        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No implementation needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No implementation needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim().replaceAll("[^\\d]", ""); // Remove any non-numeric characters
                if (input.length() > 10) {
                    input = input.substring(0, 10); // Trim to 10 digits if longer
                }
                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < input.length(); i++) {
                    formatted.append(input.charAt(i));
                    if (i == 2 || i == 5) {
                        formatted.append("-");
                    }
                }
                mobile.removeTextChangedListener(this); // Remove listener to prevent infinite loop
                mobile.setText(formatted.toString());
                mobile.setSelection(formatted.length());
                mobile.addTextChangedListener(this); // Add listener back
            }
        });



        proceed.setOnClickListener(v -> {
            reference = FirebaseDatabase.getInstance().getReference();
            validateFields();
            String Firstname = firstname.getText().toString();
            String Middlename = middlename.getText().toString();
            String Lastname = lastname.getText().toString();
            String Mobile = mobile.getText().toString();
            String Email = email.getText().toString();
            String Date_of_birth = DOB.getText().toString();
            String Selectedgender = selectedGender;
            String SelectedCourse = selectedcourse;
            String SelectedSemester = selectedsemester;
            String Selected_year_of_study = selectedyear_of_study;
            if (!TextUtils.isEmpty(SelectedSemester) && !TextUtils.isEmpty(Selected_year_of_study) && !TextUtils.isEmpty(SelectedCourse)
                    && !TextUtils.isEmpty(Firstname) && !TextUtils.isEmpty(Mobile)
                    && !TextUtils.isEmpty(Middlename) && !TextUtils.isEmpty(Lastname)
                    && !TextUtils.isEmpty(Email) && !TextUtils.isEmpty(Date_of_birth)
                    && !TextUtils.isEmpty(Selectedgender)){
                String institute_id = getIntent().getStringExtra("Login_Institute_id");
                String sanitizedEmail = getIntent().getStringExtra("Login_Snaitized_email");
                String Designation = getIntent().getStringExtra("Login_Designation");
                boolean newuser = false;
                try {
                    write_user_personal_info(newuser,Designation, sanitizedEmail, institute_id, Firstname, Middlename, Lastname, Mobile, Email, Date_of_birth,
                            Selectedgender, SelectedCourse, SelectedSemester, Selected_year_of_study);
                    saveUserData(Firstname,Middlename,Lastname, Mobile,Email,Date_of_birth, Selectedgender, SelectedCourse,SelectedSemester,Selected_year_of_study);
                    Intent intent = new Intent(Nebula_personal_information.this, MainActivity.class);
                    intent.putExtra("source_activity", "Nebula_personal_information");
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Log.e(TAG, "Error writing user personal info: " + e.getMessage());
                    Toast.makeText(Nebula_personal_information.this, "Error writing user personal info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }else {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            }


        });

    }
    private boolean validateFields() {
        boolean isValid = true;

        // Reset border colors
        firstname.setBackgroundResource(R.drawable.rectangle_1);
        middlename.setBackgroundResource(R.drawable.rectangle_1);
        lastname.setBackgroundResource(R.drawable.rectangle_1);
        DOB.setBackgroundResource(R.drawable.rectangle_1);
        mobile.setBackgroundResource(R.drawable.rectangle_1);
        email.setBackgroundResource(R.drawable.rectangle_1);
        college_id.setBackgroundResource(R.drawable.rectangle_1);
        college_name.setBackgroundResource(R.drawable.rectangle_1);
        gender.setBackgroundResource(R.drawable.rectangle_1);
        course.setBackgroundResource(R.drawable.rectangle_1);
        year_of_study.setBackgroundResource(R.drawable.rectangle_1);
        semester.setBackgroundResource(R.drawable.rectangle_1);
        // Validate each field
        if (TextUtils.isEmpty(firstname.getText().toString().trim())) {
            firstname.setBackgroundResource(R.drawable.red_border); // Apply red border
            isValid = false;
        }
        if (TextUtils.isEmpty(middlename.getText().toString().trim())) {
            middlename.setBackgroundResource(R.drawable.red_border); // Apply red border
            isValid = false;
        }
        if (TextUtils.isEmpty(lastname.getText().toString().trim())) {
            lastname.setBackgroundResource(R.drawable.red_border); // Apply red border
            isValid = false;
        }
        if (TextUtils.isEmpty(DOB.getText().toString().trim())) {
            DOB.setBackgroundResource(R.drawable.red_border); // Apply red border
            isValid = false;
        }
        if (TextUtils.isEmpty(mobile.getText().toString().trim())) {
            mobile.setBackgroundResource(R.drawable.red_border); // Apply red border
            isValid = false;
        }
        if (TextUtils.isEmpty(email.getText().toString().trim())) {
            email.setBackgroundResource(R.drawable.red_border); // Apply red border
            isValid = false;
        }
        if (TextUtils.isEmpty(college_id.getText().toString().trim())) {
            college_id.setBackgroundResource(R.drawable.red_border); // Apply red border
            isValid = false;
        }
        if (TextUtils.isEmpty(college_name.getText().toString().trim())) {
            college_name.setBackgroundResource(R.drawable.red_border); // Apply red border
            isValid = false;
        }
        if (gender.getSelectedItemPosition() == 0) {
            gender.setBackgroundResource(R.drawable.red_border);
            isValid = false;
        }
        if (course.getSelectedItemPosition() == 0) {
            course.setBackgroundResource(R.drawable.red_border);
            isValid = false;
        }
        if (semester.getSelectedItemPosition() == 0) {
            semester.setBackgroundResource(R.drawable.red_border);
            isValid = false;
        }
        if (year_of_study.getSelectedItemPosition() == 0) {
            year_of_study.setBackgroundResource(R.drawable.red_border);
            isValid = false;
        }

        return isValid;
    }


    private void set_college_id_and_name(EditText collegeId, EditText collegeName, EditText email) {

        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String Email = sharedPref.getString("Email","");
        String college_id = sharedPref.getString("Institute_id","");
        String Designation = sharedPref.getString("Designation","");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Institute").child(college_id);
        ValueEventListener postListener = new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean emailFound = false;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String email = childSnapshot.child("Personal Information").child("email").getValue(String.class);
                    if (email.equals(Email)) {
                        emailFound = true;
                        break;
                    }
                }
                if (emailFound){
                    //set email id
                    email.setText(Email);
                    email.setEnabled(false);
                    email.setFocusable(false);
                    email.setFocusableInTouchMode(false);
                    //set college id
                    collegeId.setText(college_id);
                    collegeId.setEnabled(false);
                    collegeId.setFocusable(false);
                    collegeId.setFocusableInTouchMode(false);
//                    String collegeName;
                    //set college name
                    ref.child("College Name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String collegeNameFromDb = dataSnapshot.getValue(String.class);
                                collegeName.setText(collegeNameFromDb);
                                collegeName.setEnabled(false);
                                collegeName.setFocusable(false);
                                collegeName.setFocusableInTouchMode(false);
                                System.out.println("College Name for Institute ID " + college_id + ": " + collegeNameFromDb);
                            } else {
                                collegeName.setText("None");
                                System.out.println("College Name not found for Institute ID " + college_id);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("Error retrieving College Name: " + databaseError.getMessage());
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        reference.child("Institute").child(college_id).child(Designation).addValueEventListener(postListener);
    }

    private void write_user_personal_info(boolean newuser,String Designation, String sanitizedEmail, String instituteID, String firstName, String middleName, String lastName,
                                          String mobile, String email, String dateOfBirth, String selectedGender, String selectedCourse,
                                          String selectedSemester, String selectedYearOfStudy) {
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
                    Post post = new Post(newuser, sanitizedEmail, instituteID, firstName, middleName, lastName,
                            mobile, email, dateOfBirth, selectedGender, selectedCourse, selectedSemester, selectedYearOfStudy);

                    // Convert the Post object to a map
                    Map<String, Object> newPostValue = post.toMap();

                    // Append the new data to the existing data
                    currentData.putAll(newPostValue);

                    // Update the "Personal Information" node with the updated data
                    personalInfoRef.setValue(currentData);
                } else {
                    // If no existing data, simply set the new data
                    Post post = new Post(newuser, sanitizedEmail, instituteID, firstName, middleName, lastName,
                            mobile, email, dateOfBirth, selectedGender, selectedCourse, selectedSemester, selectedYearOfStudy);

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
                Toast.makeText(Nebula_personal_information.this, "Error appending data to Personal Information: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveUserData(String firstName, String middleName, String lastName,
                                                 String mobile, String email, String dateOfBirth,
                                                 String selectedGender, String selectedCourse,
                                                 String selectedSemester, String selectedYearOfStudy) {
        // Initialize SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Store user data in SharedPreferences
        editor.putString("firstName", firstName);
        editor.putString("middleName", middleName);
        editor.putString("lastName", lastName);
        editor.putString("mobile", mobile);
        editor.putString("email", email);
        editor.putString("dateOfBirth", dateOfBirth);
        editor.putString("selectedGender", selectedGender);
        editor.putString("selectedCourse", selectedCourse);
        editor.putString("selectedSemester", selectedSemester);
        editor.putString("selectedYearOfStudy", selectedYearOfStudy);
        // Apply changes
        editor.apply();
    }

}