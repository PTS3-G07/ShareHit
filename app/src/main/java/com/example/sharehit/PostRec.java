package com.example.sharehit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.util.HashMap;

import static com.example.sharehit.DezerApi.EXTRA_FAN;
import static com.example.sharehit.DezerApi.EXTRA_NAME;
import static com.example.sharehit.DezerApi.EXTRA_URL;

public class PostRec extends AppCompatActivity {


    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference myRef;
    FirebaseDatabase database;
    private StorageReference mStorageRef;
    TextView nameAr;
    Button postRec;
    String pseudoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_rec);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
        Query query = myRef.orderByChild("email").equalTo(user.getEmail());
        Intent intent = getIntent();
        final String imageUrl = intent.getStringExtra(EXTRA_URL);
        final String name = intent.getStringExtra(EXTRA_NAME);
        String nbFan = intent.getStringExtra(EXTRA_FAN);

        postRec = findViewById(R.id.postRec);
        nameAr = findViewById(R.id.nameAr);
        nameAr.setText(pseudoData);





        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    pseudoData = ""+ ds.child("pseudo").getValue();
                    String emailData = user.getEmail();
                    String pdpUrl = ""+ ds.child("pdpUrl").getValue();
                    final String userUID = firebaseAuth.getCurrentUser().getUid();
                    final String timeStamp = String.valueOf(System.currentTimeMillis());
                    final User user = new User(
                            pseudoData,
                            emailData
                    );
                    postRec.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                Recommendation recommendation = new Recommendation(
                  "Artist",
                        user,
                        name,
                        imageUrl
                );

                            HashMap usersMap = new HashMap();
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID).child("Recommendations");
                            usersMap.put("Recom"+timeStamp, recommendation);
                            usersRef.updateChildren(usersMap);


                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
