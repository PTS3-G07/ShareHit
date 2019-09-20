package com.example.sharehit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


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

        int test = 1;
        send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("message");
                myRef.setValue(" Alexandre wsh wsh t con ou quoi!");
            }
        });



    }
}
