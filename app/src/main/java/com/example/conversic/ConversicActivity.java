package com.example.conversic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class ConversicActivity extends FragmentActivity {

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_conversic, container, false);
    }

    //Navigation: Selection Buttons
    ImageButton btn1;
    ImageButton btn2;
    Button btnCLib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversic);

        //Navigation: Selection Buttons
        btn1 = findViewById(R.id.imageButton1);
        btn2 = findViewById(R.id.imageButton2);
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
}
