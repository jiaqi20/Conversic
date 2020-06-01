package com.example.conversic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ConversicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversic);

        //Navigation: Back Button
        Button btnBack;

        //Navigation: Selection Buttons
        Button btn1;
        Button btn2;
        Button btnCLib;

        //Navigation: Back Button
        btnBack = findViewById(R.id.buttonBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveBack();
            }
        });

        //Navigation: Selection Buttons
        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        btnCLib = findViewById(R.id.buttonCLib);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToConversic1();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToConversic2();
            }
        });

        btnCLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToLibActivity();
            }
        });
    }

    //Navigation: Selection Buttons
    private void moveToLibActivity(){
        Intent intent = new Intent(ConversicActivity.this, LibraryActivity.class);
        startActivity(intent);
    }

    private void moveToConversic1(){
        Intent intent = new Intent(ConversicActivity.this, Conversic1.class);
        startActivity(intent);
    }

    private void moveToConversic2(){
        Intent intent = new Intent(ConversicActivity.this, Conversic2.class);
        startActivity(intent);
    }

    private void moveBack(){
        Intent intent = new Intent(ConversicActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
