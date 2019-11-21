package com.example.sharehit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sharehit.Model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListLikePage extends AppCompatActivity {

    private DatabaseReference recosRef, usersRef;
    private FirebaseAuth mAuth;
    private String current_user_id;
    public RecyclerView recyclerLike;
    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_like_page);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Bundle b = getIntent().getExtras();
        list = new ArrayList<String>();

        recyclerLike = (RecyclerView) findViewById(R.id.likeRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerLike.setLayoutManager(layoutManager);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        Log.e("keyReco2", b.getString("key"));
        recosRef = FirebaseDatabase.getInstance().getReference().child("recos").child(b.getString("key")).child("likeUsersUid");
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        displayAllUserLike();

        recosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    list.add(child.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void displayAllUserLike() {
        FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>
                (
                        User.class,
                        R.layout.like_user_profil_display,
                        UserViewHolder.class,
                        usersRef
                ) {
            @Override
            protected void populateViewHolder(final UserViewHolder userViewHolder, final User user, final int i) {
                recosRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean test = false;
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            if(data.getValue().equals(getRef(i).getKey())){
                                test = true;

                            }
                        }
                        if(test){
                            userViewHolder.setPseudoListLike(user.getPseudo());
                            Picasso.with(getApplicationContext()).load("https://firebasestorage.googleapis.com/v0/b/share-hit-52071.appspot.com/o/Pdp%2F"+getRef(i).getKey()+"?alt=media&token=32f03c76-31a8-4ea2-8cac-8fa92bef6667").fit().centerInside().into(userViewHolder.getImgProfil());
                        } else {
                            userViewHolder.layout_hide();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        };
        recyclerLike.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout layout;
        final LinearLayout.LayoutParams params;
        View mView;


        public UserViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
            layout =(LinearLayout)itemView.findViewById(R.id.linearLayoutListLike);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        public CircleImageView getImgProfil(){
            CircleImageView img = (CircleImageView) mView.findViewById(R.id.imageProfilListLike);
            return img;
        }

        public void setPseudoListLike(String name){
            TextView tx = (TextView) mView.findViewById(R.id.pseudoProfilListLike);
            tx.setText(name);
        }
        private void layout_hide() {
            layout.setVisibility(View.GONE);

        }

    }
}
