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

public class Login extends AppCompatActivity {
    //login
    private EditText email, password;
    private Button login;
    private TextView welcome, register;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        welcome = findViewById(R.id.welcome);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.buttonLogin);
        register = findViewById(R.id.textView);
        fAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }


    public void register(View view) {
        startActivity(new Intent(getApplicationContext(), Register.class));
    }

    public void forgetPassword(View view) {
        startActivity(new Intent(getApplicationContext(), ResetPassword.class));
    }

    public void loginUser() {
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

        //authenticate the user
        fAuth.signInWithEmailAndPassword(em, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(Login.this, "Logged in successfully",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    Toast.makeText(Login.this, "Error: " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
