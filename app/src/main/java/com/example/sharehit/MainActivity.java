package com.example.sharehit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button gotologin;
    private Button gotosign, send;

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

        send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadpdp = new Intent(MainActivity.this, Uploadpdp.class);
                startActivity(uploadpdp);
            }
        });

    }


}
