package com.nebula.NebulaApp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InstituteID extends AppCompatActivity {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    EditText institute_id_edit_text;
    ImageButton next_image_btn;
    public static final String SHARED_PREFS = "shared_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institute_id);

        institute_id_edit_text = findViewById(R.id.Institute_id);
        next_image_btn = findViewById(R.id.Next_button);
        //to check whether user is already login or not
        Log.d("InstituteID","Checking whether user is already login or not");
        checkbox();
        Log.d("InstituteID","Checking Uer already login, completed");
        // Next button clicked
        next_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Institute_id = institute_id_edit_text.getText().toString();
                Log.d("InstituteID","Fetched institute Id from user" + Institute_id);

                reference.child("Institute").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(Institute_id)) {
                            Log.d("InstituteID","Institute ID present in DB");
                            Intent intent = new Intent(getApplicationContext(),On_Boarding_2.class);
                            intent.putExtra("MainActivity_Institute_id",Institute_id);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d("InstituteID","Institute ID not present in DB");
                            Toast.makeText(getApplicationContext(), "Invalid institute id", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle database error
                        System.out.println("Database error: " + databaseError.getMessage());
                    }
                });
            }
        });
    }
    private void checkbox() {
        Log.d("InstituteID","Inside checkbox(), checking session usinf sp");
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        boolean remember_me = sharedPref.getBoolean("Remember me",false);

        if (remember_me){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}