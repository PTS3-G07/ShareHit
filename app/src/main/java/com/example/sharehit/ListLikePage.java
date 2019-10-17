package com.example.sharehit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.sharehit.Model.Recommendation;
import com.example.sharehit.Model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListLikePage extends AppCompatActivity {

    private DatabaseReference recosRef, usersRef;
    private FirebaseAuth mAuth;
    private String current_user_id;
    public RecyclerView listLike;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_like_page);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Bundle b = getIntent().getExtras();

        listLike = (RecyclerView) findViewById(R.id.likeRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        listLike.setLayoutManager(layoutManager);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        recosRef = FirebaseDatabase.getInstance().getReference().child("recos").child(b.getString("key")).child("likeUserUid");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>
                (
                        User.class,
                        R.layout.like_user_profil_display,
                        UserViewHolder.class,
                        recosRef
                ) {
            @Override
            protected void populateViewHolder(final UserViewHolder userViewHolder, final User user, int i) {
                Log.e("IDUSER", getRef(i).getKey());
                usersRef.child(getRef(i).getKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userViewHolder.setPseudoListLike(dataSnapshot.child("pseudo").getValue().toString());
                        Picasso.with(getApplicationContext()).load(dataSnapshot.child("pdpUrl").getValue().toString()).fit().centerInside().into(userViewHolder.getImgProfil());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        };

        listLike.setAdapter(firebaseRecyclerAdapter);


    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UserViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
        }

        public CircleImageView getImgProfil(){
            CircleImageView img = (CircleImageView) mView.findViewById(R.id.imageProfilListLike);
            return img;
        }

        public void setPseudoListLike(String name){
            TextView tx = (TextView) mView.findViewById(R.id.pseudoProfilListLike);
            tx.setText(name);
        }

    }
}
