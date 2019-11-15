package com.example.sharehit;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharehit.Model.Recommendation;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class SearchFragement extends Fragment {


    RecyclerView recyclerView;
    private DatabaseReference recosRef, usersRef;
    private FirebaseAuth mAuth;
    private String current_user_id;
    public boolean CURRENT_LIKE, CURRENT_BOOK, test=false;
    private final static MediaPlayer mp = new MediaPlayer();
    private ProgressBar mSeekBarPlayer;
    private ImageButton stop;
    private ImageButton btnPause;
    private LinearLayout lecteur;
    private TextView nameLect;
    private ImageView musicImg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragement_search, null);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        recosRef = FirebaseDatabase.getInstance().getReference().child("recos");
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");


        recyclerView = root.findViewById(R.id.postFollowRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);


        displayAllRecosFollow();

        return root;
    }

    private void displayAllRecosFollow() {
        final Intent intent1 = new Intent(getContext(), ListLikePage.class);
        final Intent intent2 = new Intent(getContext(), CommentPage.class);
        final Intent intent3 = new Intent(getContext(), ProfilPage.class);
        final Bundle b = new Bundle();

        final boolean[] isFollowed = new boolean[1];
        final int[] tailleTableau = new int[1];

        recosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tailleTableau[0] = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<Recommendation, FeedFragement.RecosViewHolder> fireBaseRecyclerAdapter = new FirebaseRecyclerAdapter<Recommendation, FeedFragement.RecosViewHolder>
                (
                        Recommendation.class,
                        R.layout.recommandation_item,
                        FeedFragement.RecosViewHolder.class,
                        recosRef
                ) {

            @Override
            protected void populateViewHolder(final FeedFragement.RecosViewHolder recosViewHolder, final Recommendation model, final int i) {



            }
        };
        recyclerView.setAdapter(fireBaseRecyclerAdapter);










    }

    private boolean loadFragement(Fragment fragment){
        if(fragment != null){
            getFragmentManager().beginTransaction().replace(R.id.container, fragment)
                    .commit();

            return true;
        }
        return false;
    }
}
