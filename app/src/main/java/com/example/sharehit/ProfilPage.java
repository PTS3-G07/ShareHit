package com.example.sharehit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sharehit.Model.Recommandation;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilPage extends AppCompatActivity {

    private CircleImageView pdp;
    private TextView pseudo, textView;
    private RecyclerView post;
    private Button follow;

    private DatabaseReference recosRef, usersRef;
    private FirebaseAuth mAuth;

    public boolean CURRENT_LIKE, CURRENT_FOLLOW;

    private final static MediaPlayer mp = new MediaPlayer();

    private String keyFollowed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_page);

        CURRENT_FOLLOW = false;

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mAuth = FirebaseAuth.getInstance();
        recosRef = FirebaseDatabase.getInstance().getReference().child("recos");
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        pdp = (CircleImageView) findViewById(R.id.pdpProfilPage);
        pseudo = (TextView) findViewById(R.id.pseudoProfilPage);
        post = (RecyclerView) findViewById(R.id.postProfilPageRecyclerView);
        follow = (Button) findViewById(R.id.followProfilPage);
        textView = (TextView) findViewById(R.id.textView8);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        post.setLayoutManager(layoutManager);

        final Bundle b = getIntent().getExtras();

        if(mAuth.getCurrentUser().getUid().equals(b.getString("key"))){
            follow.setVisibility(View.INVISIBLE);
            textView.setText("Mes recommandations");

        }

        usersRef.child(mAuth.getCurrentUser().getUid()).child("followed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if(ds.getValue().equals(b.getString("key"))){
                        CURRENT_FOLLOW = true;
                        keyFollowed = ds.getRef().getKey();
                        follow.setText("Ne plus suivre");
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CURRENT_FOLLOW == false){
                    HashMap usersMap = new HashMap();
                    usersMap.put(usersRef.child(mAuth.getCurrentUser().getUid()).child("followed").push().getKey(), b.getString("key"));
                    usersRef.child(mAuth.getCurrentUser().getUid()).child("followed").updateChildren(usersMap);
                    usersRef.child(mAuth.getCurrentUser().getUid()).child("followed").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds: dataSnapshot.getChildren()){
                                if(ds.getValue().equals(b.getString("key"))){
                                    Log.e("Followed key", ds.getRef().getKey());
                                    keyFollowed = ds.getRef().getKey();

                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    follow.setText("Ne plus suivre");
                    CURRENT_FOLLOW=true;

                } else if(CURRENT_FOLLOW == true){
                    usersRef.child(mAuth.getCurrentUser().getUid()).child("followed").child(keyFollowed).removeValue();
                    follow.setText("Suivre");
                    CURRENT_FOLLOW=false;
                }
            }
        });

        Picasso.with(getApplicationContext()).load("https://firebasestorage.googleapis.com/v0/b/share-hit-52071.appspot.com/o/Pdp%2F"+b.getString("key")+"?alt=media&token=32f03c76-31a8-4ea2-8cac-8fa92bef6667").networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).fit().centerInside().into(pdp);

        usersRef.child(b.getString("key")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pseudo.setText(dataSnapshot.child("pseudo").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        displayAllRecosUser(b.getString("key"));


    }

    private void displayAllRecosUser(String uid) {

        final Intent intent1 = new Intent(getApplicationContext(), ListLikePage.class);
        final Intent intent2 = new Intent(getApplicationContext(), CommentPage.class);
        final Intent intent3 = new Intent(getApplicationContext(), ProfilPage.class);
        final Bundle b = new Bundle();

        Query postUser = recosRef.orderByChild("userRecoUid").startAt(uid).endAt(uid+"\uf8ff");

        FirebaseRecyclerAdapter<Recommandation, FeedFragement.RecosViewHolder> fireBaseRecyclerAdapter = new FirebaseRecyclerAdapter<Recommandation, FeedFragement.RecosViewHolder>
                (
                        Recommandation.class,
                        R.layout.recommandation_item,
                        FeedFragement.RecosViewHolder.class,
                        postUser
                ) {

            @Override
            protected void populateViewHolder(final FeedFragement.RecosViewHolder recosViewHolder, final Recommandation model, final int i) {






            }
        };
        post.setAdapter(fireBaseRecyclerAdapter);
    }
}
