package com.example.conversic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private EditText email;
    private Button button;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        email = findViewById(R.id.editText);
        button = findViewById(R.id.buttonSE);
        fAuth = FirebaseAuth.getInstance();
        Button btnBack = findViewById(R.id.buttonBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveBack();
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString();
                if(TextUtils.isEmpty(userEmail)) {
                    Toast.makeText(ResetPassword.this, "Invalid email input!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    fAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(ResetPassword.this, "Link is successfully sent to your email!",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPassword.this, Login.class));
                            } else {
                                String err = task.getException().getMessage();
                                Toast.makeText(ResetPassword.this, "Error occurred: " + err,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void moveBack(){
        Intent intent = new Intent(ResetPassword.this, Login.class);
        startActivity(intent);
    }
}
