package com.example.sharehit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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


    /*
    private  void checkUserStatus(){
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            email.setText(user.getEmail());
        }else {
            startActivity(new Intent(FeedPage.this, MainActivity.class));
        }
    }

     */


    private boolean loadFragement(Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment)
                    .commit();

            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_page);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        email = findViewById(R.id.showEmail);
        logout = findViewById(R.id.logout);
        navigationView = findViewById(R.id.space);
        navigationView.initWithSaveInstanceState(savedInstanceState);
        navigationView.addSpaceItem(new SpaceItem("", R.drawable.time_icon));
        navigationView.addSpaceItem(new SpaceItem("", R.drawable.search));
        navigationView.addSpaceItem(new SpaceItem("", R.drawable.star_icon));
        navigationView.addSpaceItem(new SpaceItem("", R.drawable.profil));
        navigationView.showIconOnly();
        //checkUserStatus();
        /*
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                checkUserStatus();
            }
        });
         */

        Fragment fragment = new FeedFragement();
        loadFragement(fragment);
        navigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            Fragment fragment = null;
            @Override
            public void onCentreButtonClick() {
                Toast.makeText(FeedPage.this,"onCentreButtonClick", Toast.LENGTH_SHORT).show();
                navigationView.setCentreButtonSelectable(true);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                if(itemIndex == 0){
                    fragment = new FeedFragement();
                    loadFragement(fragment);
                }else if (itemIndex == 1){
                    fragment = new SearchFragement();
                    loadFragement(fragment);
                }else if(itemIndex == 2){
                    fragment = new NotifFragement();
                    loadFragement(fragment);
                }else if(itemIndex == 3){
                    fragment = new ProfilFragement();
                    loadFragement(fragment);
                }
               // Toast.makeText(FeedPage.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                //Toast.makeText(FeedPage.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
