package com.nebula.NebulaApp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class On_Boarding_2 extends AppCompatActivity {
    ImageButton Student_image_button,management_image_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding2);
        Student_image_button = findViewById(R.id.imageButton3);
        management_image_button = findViewById(R.id.imageButton5);
        // student image button clicked
        Student_image_button.setOnClickListener(v -> {
            String designation = "Student";
            String institute_ID = getIntent().getStringExtra("MainActivity_Institute_id");
            Log.w("institute",institute_ID);
            Intent intent = new Intent(On_Boarding_2.this, Nebula_login.class);
            intent.putExtra("OnBoarding2_Student", designation);
            intent.putExtra("OnBoarding2_Institute_id",institute_ID);
            intent.putExtra("source_activity", "On_boarding_2");
            startActivity(intent);
            finish();
        });

        management_image_button.setOnClickListener(v -> {
            String designation = "Management";
            String institute_ID = getIntent().getStringExtra("MainActivity_Institute_id");
            Log.w("institute",institute_ID);
            Intent intent = new Intent(On_Boarding_2.this, Nebula_login.class);
            intent.putExtra("OnBoarding2_designation", designation);
            intent.putExtra("OnBoarding2_Institute_id",institute_ID);
            intent.putExtra("source_activity", "On_boarding_2");
            startActivity(intent);
            finish();
        });

    }
}