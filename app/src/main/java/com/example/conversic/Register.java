package com.example.conversic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    //register
    private TextView register, login;
    private EditText name, email, password;
    private Button register2;
    private FirebaseAuth fAuth;


    //Navigation: Back Button
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register = findViewById(R.id.textView2);
        login = findViewById(R.id.textView3);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        fAuth = FirebaseAuth.getInstance();
        //progressbar
        register2 = findViewById(R.id.buttonRegister);
        btnBack = findViewById(R.id.buttonBack);

        //Navigation: Back Button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveBack();
            }
        });

        if(fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }

        register2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    public void onClick(View view) {
        startActivity(new Intent(getApplicationContext(), Login.class));
    }

    //Navigation: Back Button
    private void moveBack(){
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
    }

    private void registerUser() {
        String em = email.getText().toString().trim();
        String pw = password.getText().toString().trim();

        //validate the email and password
        if(TextUtils.isEmpty(em)) {
            email.setError("Email is required!");
            return;
        }
        if(TextUtils.isEmpty(pw)) {
            password.setError("Password is required!");
            return;
        }

        //progressBar.setVisibility(View.VISIBLE); need to set invisible first

        //register user
        fAuth.createUserWithEmailAndPassword(em, pw).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(Register.this, "User created",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(Register.this, "Error!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
