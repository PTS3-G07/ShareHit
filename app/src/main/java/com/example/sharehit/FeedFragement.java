package com.example.sharehit;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharehit.Adapter.AdapterRecs;
import com.example.sharehit.Model.Recommendation;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedFragement extends Fragment {

    RecyclerView recyclerView;
    private DatabaseReference recosRef, usersRef;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private boolean CURRENT_LIKE=false, test=false;
    private final static MediaPlayer mp = new MediaPlayer();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragement_feed, null);



        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        recosRef = FirebaseDatabase.getInstance().getReference().child("recos");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        recyclerView = root.findViewById(R.id.postRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);


        displayAllRecos();

        return root;
    }


    private void displayAllRecos() {

        FirebaseRecyclerAdapter<Recommendation, RecosViewHolder> fireBaseRecyclerAdapter = new FirebaseRecyclerAdapter<Recommendation, RecosViewHolder>
                (
                        Recommendation.class,
                        R.layout.recommandation_item,
                        RecosViewHolder.class,
                        recosRef
                ) {

            @Override
            protected void populateViewHolder(final RecosViewHolder recosViewHolder, final Recommendation model, final int i) {

                Picasso.with(getContext()).load(model.getImg()).fit().centerInside().into(recosViewHolder.getImg());

                recosViewHolder.setDesc(model.getName());
                final String idReco = getRef(i).getKey();


                recosRef.child(idReco).child("likeUsersUid").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        recosViewHolder.setNbrLike(Long.toString(dataSnapshot.getChildrenCount()) + " like");
                    }
                    @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

                recosRef.child(idReco).child("likeUsersUid").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) recosViewHolder.getLikeButton().setImageResource(R.drawable.like);
                        else{
                            recosViewHolder.getLikeButton().setImageResource(R.drawable.heart);
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
                });



                recosViewHolder.getLikeButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(CURRENT_LIKE == false){
                            getRef(i).child("likeUsersUid").child(mAuth.getCurrentUser().getUid()).child("like_done").setValue("yes");
                            //recosViewHolder.getLikeButton().setImageResource(R.drawable.heart);
                            CURRENT_LIKE=true;
                        } else if(CURRENT_LIKE == true){
                            getRef(i).child("likeUsersUid").child(mAuth.getCurrentUser().getUid()).removeValue();
                            //recosViewHolder.getLikeButton().setImageResource(R.drawable.like);
                            CURRENT_LIKE=false;
                        }


                    }


                });

                recosViewHolder.getImg().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (model.getUrlPreview()!=null) {
                            try {
                                if (mp.isPlaying()){
                                    mp.pause();
                                    mp.reset();
                                }else {
                                    mp.reset();
                                    mp.setDataSource(model.getUrlPreview());
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


                usersRef.child(model.getUserRecoUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String pseudo = dataSnapshot.child("pseudo").getValue().toString();
                        recosViewHolder.setTitre(pseudo + " a recommandé " + model.getType());
                        if(dataSnapshot.child("pdpUrl").exists()){
                            Picasso.with(getContext()).load(dataSnapshot.child("pdpUrl").getValue().toString()).fit().centerInside().into(recosViewHolder.getImgProfil());
                        }
                        /*DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date date_mini = dateFormat.format(timestamp.getTime());*/

                        Date date=new Date(model.getTimeStamp());
                        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"+" à "+"H-mm");
                        recosViewHolder.setTime("Le "+dateFormat.format(date));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }
        };
        recyclerView.setAdapter(fireBaseRecyclerAdapter);



    }

    public static class RecosViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public RecosViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
        }

        public void setTime(String timeText){
            TextView time = (TextView) mView.findViewById(R.id.time);
            time.setText(timeText);
        }

        public void setDesc(String desc){
            TextView descR = (TextView) mView.findViewById(R.id.desc);
            descR.setText(desc);
        }

        public void setTitre(String text){
            TextView nameR = (TextView) mView.findViewById(R.id.name);
            nameR.setText(text);
        }

        public ImageButton getImg() {
            ImageButton imgR = (ImageButton) mView.findViewById(R.id.img_ar);
            return imgR;
        }

        public void setmView(View mView) {
            this.mView = mView;
        }

        public CircleImageView getImgProfil(){
            CircleImageView imgProfil = (CircleImageView) mView.findViewById(R.id.imgProfil);
            return imgProfil;
        }

        public ImageButton getLikeButton(){
            ImageButton imgButton = (ImageButton) mView.findViewById(R.id.likeButton);
            return imgButton;
        }

        public void setNbrLike(String text){
            TextView nbrlike = (TextView) mView.findViewById(R.id.nbrLike);
            nbrlike.setText(text);
        }
    }



}
