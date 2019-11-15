package com.example.sharehit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class SplashScreen extends Activity {

    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logo = (ImageView) findViewById(R.id.logoSplashScreen);
        final Animation anim = (AnimationUtils.loadAnimation(this, R.anim.zoomin));

        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(SplashScreen.this, LoginPage.class));
                finish();
            }
        }, secondsDelayed * 1000);


    }
}
