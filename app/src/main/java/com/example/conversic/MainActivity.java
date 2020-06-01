package com.example.conversic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private TextView appName;
    private Button logout;

    //Navigation: Menu Buttons
    private Button btnConversic;
    private Button btnCC;
    private Button btnLib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appName = findViewById(R.id.textViewMainMenu);
        logout = findViewById(R.id.buttonLogOut);

        btnConversic = findViewById(R.id.buttonConversic);
        btnCC = findViewById(R.id.buttonCC);
        btnLib = findViewById(R.id.buttonLib);

        //Navigation: Menu Buttons
        btnConversic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToConversicActivity();
            }
        });
        btnCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToCCActivity();
            }
        });
        btnLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToLibActivity();
            }
        });
    }

    //Navigation: Menu Buttons
    private void moveToConversicActivity(){
        Intent intent = new Intent(MainActivity.this, ConversicActivity.class);
        startActivity(intent);
    }

    private void moveToCCActivity(){
        Intent intent = new Intent(MainActivity.this, CrashCourseActivity.class);
        startActivity(intent);
    }

    private void moveToLibActivity(){
        Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }
}
