package com.example.sharehit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

public class FeedPage extends AppCompatActivity {

    TextView email;
    Button logout;
    SpaceNavigationView navigationView;
    FirebaseAuth firebaseAuth;

    private  void checkUserStatus(){
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            email.setText(user.getEmail());
        }else {
            startActivity(new Intent(FeedPage.this, MainActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_page);

        email = findViewById(R.id.showEmail);
        logout = findViewById(R.id.logout);
        navigationView = findViewById(R.id.space);
        navigationView.initWithSaveInstanceState(savedInstanceState);
        navigationView.addSpaceItem(new SpaceItem("", R.drawable.home));
        navigationView.addSpaceItem(new SpaceItem("", R.drawable.search));
        navigationView.addSpaceItem(new SpaceItem("", R.drawable.notification));
        navigationView.addSpaceItem(new SpaceItem("", R.drawable.profil));
        navigationView.showIconOnly();
        checkUserStatus();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                checkUserStatus();
            }
        });
        navigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Toast.makeText(FeedPage.this,"onCentreButtonClick", Toast.LENGTH_SHORT).show();
                navigationView.setCentreButtonSelectable(true);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                Toast.makeText(FeedPage.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Toast.makeText(FeedPage.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
