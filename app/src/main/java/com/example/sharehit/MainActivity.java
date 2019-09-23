package com.example.sharehit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button gotologin;
    private Button gotosign;
    private Button gototutoriel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gotologin = (Button) findViewById(R.id.gotologin);
        gotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myLoginpage = new Intent(MainActivity.this, LoginPage.class);
                startActivity(myLoginpage);
            }
        });

        gotosign = findViewById(R.id.gotosign);
        gotosign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mySignUpPage = new Intent(MainActivity.this, SignUp.class);
                startActivity(mySignUpPage);
            }
        });

        gototutoriel = findViewById(R.id.gototutroriel);
        gototutoriel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                configureViewPager();
            }
        });


    }


    private void configureViewPager(){
        setContentView(R.layout.tutoriel);
        // 1 - Get ViewPager from layout
        ViewPager pager = (ViewPager)findViewById(R.id.activity_main_viewpager);
        // 2 - Set Adapter PageAdapter and glue it together
        pager.setAdapter(new PageAdapter(getSupportFragmentManager(), getResources().getIntArray(R.array.colorPagesViewPager)) {
        });
        setContentView(R.layout.tutoriel);

    }
}
