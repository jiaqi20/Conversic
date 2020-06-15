package com.example.conversic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LibraryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnBack;

    private MyAdapter adapter;

    private FirebaseUser user;
    private DatabaseReference databaseRef;
    private ArrayList<Upload> uploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        //Navigation: Back Button
        btnBack = findViewById(R.id.buttonBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveBack();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        uploads = new ArrayList<>();

        user = FirebaseAuth.getInstance().getCurrentUser();
        //REFERENCE
        databaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postDataSnapshot : dataSnapshot.child(user.getUid()).getChildren()) {
                    Upload upload = postDataSnapshot.getValue(Upload.class);
                    uploads.add(upload);
                }
                adapter = new MyAdapter(recyclerView,LibraryActivity.this, uploads);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LibraryActivity.this, databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void moveBack(){
        Intent intent = new Intent(LibraryActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
