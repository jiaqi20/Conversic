package com.example.conversic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ConversicActivity extends AppCompatActivity {

    //Navigation: Back Button
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversic);

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
        Intent intent = new Intent(ConversicActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
