package com.example.conversic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CrashCourseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_course);

        //Navigation: Back Button
        Button btnBack;

        //Navigation: Back Button
        btnBack = findViewById(R.id.buttonBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveBack();
            }
        });
    }

    private void moveBack(){
        Intent intent = new Intent(CrashCourseActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
