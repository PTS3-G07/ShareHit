package com.example.sharehit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.squareup.picasso.Picasso;

import static com.example.sharehit.ApiManager.EXTRA_ID;
import static com.example.sharehit.ApiManager.EXTRA_LINK;
import static com.example.sharehit.ApiManager.EXTRA_NAME;
import static com.example.sharehit.ApiManager.EXTRA_PREVIEW;
import static com.example.sharehit.ApiManager.EXTRA_TYPE;
import static com.example.sharehit.ApiManager.EXTRA_URL;

import static com.example.sharehit.R.*;

public class FeedPage extends AppCompatActivity {

    TextView email;
    Button logout;
    SpaceNavigationView navigationView;
    FirebaseAuth firebaseAuth;
    Dialog myDialog;
    ImageButton artiste;
    ImageButton album;
    ImageButton morceau;
    ImageButton jeuVideo;
    ImageButton serie;
    ImageButton film;
    public int idSearch;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(FeedPage.this, MainActivity.class));
        }
    }





    private boolean loadFragement(Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager().beginTransaction().replace(id.container, fragment)
                    .commit();

            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_feed_page);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        email = findViewById(id.showEmail);
        navigationView = findViewById(id.space);
        navigationView.initWithSaveInstanceState(savedInstanceState);
        navigationView.addSpaceItem(new SpaceItem("", drawable.time_icon));
        navigationView.addSpaceItem(new SpaceItem("", drawable.star_icon));
        navigationView.addSpaceItem(new SpaceItem("", drawable.bookmark));
        navigationView.addSpaceItem(new SpaceItem("", drawable.profil));
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


/*
        myDialog=new Dialog(this, style.DialogTheme);
        Window window = myDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        /*wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;*/

        final Dialog d = new Dialog(this, R.style.DialogTheme);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.new_recommendation);
        ImageButton i = d.findViewById(id.artiste);
        Window window = d.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);


        //window.setAttributes(wlp);

        Fragment fragment = new FeedFragement();
        loadFragement(fragment);
        navigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            Fragment fragment = null;
            @Override
            public void onCentreButtonClick() {

                final Intent intent = new Intent(FeedPage.this, ApiManager.class);
                final Bundle b = new Bundle();
                d.show();
                artiste = d.findViewById(R.id.artiste);
                artiste.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        b.putInt("key", 1); //Your id
                        intent.putExtras(b); //Put your id to your next Intent
                        startActivity(intent);
                        //finish();
                    }
                });
                album = d.findViewById(id.album);
                album.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        b.putInt("key", 2); //Your id
                        intent.putExtras(b); //Put your id to your next Intent
                        startActivity(intent);
                        //finish();
                        }
                });

                morceau = d.findViewById(id.morceau);
                morceau.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        b.putInt("key", 3);
                        intent.putExtras(b);
                        startActivity(intent);
                        //finish();
                    }
                });

                film = d.findViewById(id.film);
                film.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        b.putInt("key", 4);
                        intent.putExtras(b);
                        startActivity(intent);
                        //finish();
                    }
                });

                jeuVideo = d.findViewById(id.jeuVideo);
                jeuVideo.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        b.putInt("key", 6);
                        intent.putExtras(b);
                        startActivity(intent);
                        //finish();
                    }
                });

                serie = d.findViewById(id.serie);
                serie.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        b.putInt("key", 5);
                        intent.putExtras(b);
                        startActivity(intent);
                        //finish();
                    }
                });
                //int height = (int)(getResources().getDisplayMetrics().heightPixels*0.4);

                /*
                myDialog.setContentView(R.layout.new_recommendation);
                //Toast.makeText(FeedPage.this,"onCentreButtonClick", Toast.LENGTH_SHORT).show();
                myDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                myDialog.show();

                 */
                /*fragment = new NewRecommendationFragment();
                loadFragement(fragment);*/
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
