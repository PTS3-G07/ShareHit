package com.example.sharehit.Utilities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.sharehit.Authentification.LoginPage;
import com.example.sharehit.R;

public class SplashScreen extends AppCompatActivity  {

    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logo = (ImageView) findViewById(R.id.logoSplashScreen);
        final Animation anim = (AnimationUtils.loadAnimation(this, R.anim.zoomin));
        final Animation fadeOut = new AlphaAnimation(1, 0);
        logo.startAnimation(anim);

        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(SplashScreen.this, LoginPage.class));
                overridePendingTransition(R.anim.fadeout,0);
                finish();
            }
        }, secondsDelayed*1500);


    }
}
