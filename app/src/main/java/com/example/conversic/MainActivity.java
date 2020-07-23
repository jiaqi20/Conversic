package com.example.conversic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private TextView appName;
    //private Button logout, btnConversic, btnCC, btnLib;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appName = findViewById(R.id.textViewMainMenu);
        /*logout = findViewById(R.id.buttonLogOut);

        //Navigation: Menu Buttons
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
        });*/

        //Navigation Panel
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            LayoutInflater inflater = getLayoutInflater();
            FrameLayout container = (FrameLayout) findViewById(R.id.fragment_container);
            inflater.inflate(R.layout.activity_conversic, container);
            navigationView.setCheckedItem(R.id.nav_conversic);

        }
    }

    //Navigation: Menu Buttons
    /*private void moveToConversicActivity() {
        Intent intent = new Intent(MainActivity.this, ConversicActivity.class);
        startActivity(intent);
    }

    private void moveToCCActivity() {
        Intent intent = new Intent(MainActivity.this, CrashCourseActivity.class);
        startActivity(intent);
    }

    private void moveToLibActivity() {
        Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }*/


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
