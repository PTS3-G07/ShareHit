package com.example.sharehit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    private Button gotologin;
    private Button gotosign, send;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    public void checkUser(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser == null){

        }else {
            startActivity(new Intent(MainActivity.this, FeedPage.class));
            finish();
            return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkUser();


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

        int test = 1;
        send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mySignUpPage = new Intent(MainActivity.this, FeedPage.class);
                startActivity(mySignUpPage);
            }
        });



    }
}
