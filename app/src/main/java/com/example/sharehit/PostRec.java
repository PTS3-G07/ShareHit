package com.example.sharehit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sharehit.Model.Recommendation;
import com.example.sharehit.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;

import static com.example.sharehit.ApiManager.EXTRA_ID;
import static com.example.sharehit.ApiManager.EXTRA_NAME;
import static com.example.sharehit.ApiManager.EXTRA_PREVIEW;
import static com.example.sharehit.ApiManager.EXTRA_TYPE;
import static com.example.sharehit.ApiManager.EXTRA_URL;

public class PostRec extends AppCompatActivity {


    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference myRef;
    FirebaseDatabase database;
    private StorageReference mStorageRef;
    TextView nameAr;
    Button postRec;
    String pseudoData;

    private TextView nomPost, descPost;
    private ImageView imgProfilPost;
    private ImageButton imgPost;
    private TextView like;
    private TextView comment;
    private final static MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_rec);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        nomPost = (TextView) findViewById(R.id.namePost);
        descPost = (TextView) findViewById(R.id.descPost);
        imgProfilPost = (ImageView) findViewById(R.id.imgProfilPost);
        imgPost = (ImageButton) findViewById(R.id.img_arPost);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        Query query = myRef.orderByChild("email").equalTo(user.getEmail());
        Intent intent = getIntent();
        final String imageUrl = intent.getStringExtra(EXTRA_URL);
        final String name = intent.getStringExtra(EXTRA_NAME);
        final String userUID = intent.getStringExtra(EXTRA_ID);
        final String type = intent.getStringExtra(EXTRA_TYPE);
        final String urlPreview = intent.getStringExtra(EXTRA_PREVIEW);
        //String nbFan = intent.getStringExtra(EXTRA_FAN);

        Picasso.with(getApplicationContext()).load(imageUrl).fit().centerInside().into(imgPost);
        descPost.setText(name);

        myRef.child(userUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nomPost.setText(dataSnapshot.child("pseudo").getValue().toString() + " a recommandé " + type);
                Picasso.with(getApplicationContext()).load(dataSnapshot.child("pdpUrl").getValue().toString()).fit().centerInside().into(imgProfilPost);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (urlPreview!=null) {
                    try {
                        if (mp.isPlaying()){
                            mp.pause();
                            mp.reset();
                        }else {
                            mp.setDataSource(urlPreview);
                            mp.prepareAsync();
                            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mp.start();
                                }
                            });
                        }
                    } catch (IOException e) {
                        Log.e("pa2chance", "prepare() failed");
                    }
                }
            }
        });

        postRec = findViewById(R.id.postRec);





        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                postRec.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.e("testest", "laaaaaaaaaaaaaaaaaaaa");

                        Recommendation recommendation = new Recommendation(
                                type,
                                userUID,
                                name,
                                imageUrl,
                                urlPreview,
                                new Timestamp(System.currentTimeMillis()).getTime()
                        );

                        HashMap usersMap = new HashMap();
                        DatabaseReference recomRef = FirebaseDatabase.getInstance().getReference().child("recos");
                        String key = recomRef.push().getKey();
                        usersMap.put(key, recommendation);
                        recomRef.updateChildren(usersMap);

                        mp.stop();

                        startActivity(new Intent(PostRec.this, FeedPage.class));
                        finish();


                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
