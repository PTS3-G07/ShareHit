package com.example.sharehit;

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

public class SearchFragement extends Fragment {


    RecyclerView recyclerView;
    private DatabaseReference recosRef, usersRef;
    private FirebaseAuth mAuth;
    private String current_user_id;
    public boolean CURRENT_LIKE, CURRENT_BOOK, test=false;
    private final static MediaPlayer mp = new MediaPlayer();

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

        Query postUser = usersRef.child(mAuth.getCurrentUser().getUid()).child("followed");
        FirebaseRecyclerAdapter<Recommendation, FeedFragement.RecosViewHolder> fireBaseRecyclerAdapter = new FirebaseRecyclerAdapter<Recommendation, FeedFragement.RecosViewHolder>
                (
                        Recommendation.class,
                        R.layout.recommandation_item,
                        FeedFragement.RecosViewHolder.class,
                        recosRef
                ) {

            @Override
            protected void populateViewHolder(final FeedFragement.RecosViewHolder recosViewHolder, final Recommendation model, final int i) {


                usersRef.child(mAuth.getCurrentUser().getUid()).child("followed").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot child:dataSnapshot.getChildren()){
                            if(dataSnapshot.getKey().equals(model.getUserRecoUid())){
                                Picasso.with(getContext()).load(model.getImg()).fit().centerInside().into(recosViewHolder.getImg());

                                recosViewHolder.setDesc(model.getName());
                                final String idReco = getRef(i).getKey();
                                recosViewHolder.autreComment.setHeight(0);

                /*if(recosRef.child(idReco).child("likeUsersUid").child(mAuth.getCurrentUser().getUid()).child("like_done").equals("yes")){
                    CURRENT_LIKE=true;
                }else{
                    CURRENT_LIKE=false;
                }*/

                                Log.e(""+recosRef.child(idReco).toString(), "CURRENT_LIKE="+CURRENT_LIKE);

                                recosRef.child(idReco).child("Coms").limitToLast(1).addValueEventListener(new ValueEventListener(){

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.hasChildren()){
                                            recosViewHolder.setPseudoCom("Aucun commentaire");
                                        }
                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            final String index = child.getKey();
                                            String idUsr = dataSnapshot.child(index).child("uid").getValue().toString();
                                            usersRef.child(idUsr).child("pseudo").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    recosViewHolder.setPseudoCom(dataSnapshot.getValue().toString()+":");
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            recosRef.child(idReco).child("Coms").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.getChildrenCount()-1>0){
                                                        recosViewHolder.autreComment.setHeight(recosViewHolder.nbrlike.getHeight());
                                                        if (dataSnapshot.getChildrenCount()-1==1){
                                                            recosViewHolder.setAutreComment("Voir l'autre commentaire.");
                                                        }
                                                        else{
                                                            recosViewHolder.setAutreComment("Voir les "+(dataSnapshot.getChildrenCount()-1)+" autres commentaires.");
                                                        }
                                                    }
                                                    else {
                                                        recosViewHolder.autreComment.setHeight(0);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            recosViewHolder.setNbrCom(dataSnapshot.child(index).child("com").getValue().toString());
                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                recosRef.child(idReco).child("likeUsersUid").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.getChildrenCount()==0){
                                            recosViewHolder.setNbrLike("Personne n'aime ça");
                                        }
                                        else if(dataSnapshot.getChildrenCount()==1){
                                            recosViewHolder.setNbrLike("Aimé par 1 personne");
                                        }
                                        else{
                                            recosViewHolder.setNbrLike("Aimé par " + Long.toString(dataSnapshot.getChildrenCount()) + " personnes");
                                        }
                                    }
                                    @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
                                });

                                recosRef.child(idReco).child("likeUsersUid").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()) {
                                            recosViewHolder.getLikeButton().setImageResource(R.drawable.red_heart);
                                        }
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
                                            CURRENT_LIKE=true;
                                        } else if(CURRENT_LIKE == true){
                                            getRef(i).child("likeUsersUid").child(mAuth.getCurrentUser().getUid()).removeValue();
                                            CURRENT_LIKE=false;
                                        }

                                    }

                                });


                                final GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
                                    @Override
                                    public boolean onDoubleTap(MotionEvent e) {
                                        if(CURRENT_LIKE == false){
                                            getRef(i).child("likeUsersUid").child(mAuth.getCurrentUser().getUid()).child("like_done").setValue("yes");
                                            CURRENT_LIKE=true;
                                        } else if(CURRENT_LIKE == true){
                                            getRef(i).child("likeUsersUid").child(mAuth.getCurrentUser().getUid()).removeValue();
                                            CURRENT_LIKE=false;
                                        }
                                        return true;
                                    }

                                    @Override
                                    public void onLongPress(MotionEvent e) {
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
                                            } catch (IOException ex) {
                                                Log.e("pa2chance", "prepare() failed");
                                            }
                                        }

                                    }
                                };

                                final GestureDetector detector = new GestureDetector(listener);

                                detector.setOnDoubleTapListener(listener);
                                detector.setIsLongpressEnabled(true);

                                recosViewHolder.getImg().setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View view, MotionEvent event) {
                                        return detector.onTouchEvent(event);
                                    }
                                });


                                usersRef.child(mAuth.getCurrentUser().getUid()).child("bookmarks").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.child(getRef(i).getKey()).exists()){
                                            CURRENT_BOOK=true;
                                            recosViewHolder.getBookButton().setBackgroundColor(Color.RED);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                recosViewHolder.getBookButton().setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(CURRENT_BOOK==false){

                                            usersRef.child(mAuth.getCurrentUser().getUid()).child("bookmarks").child(getRef(i).getKey()).child("bookmark_done").setValue("yes");
                                            recosViewHolder.getBookButton().setBackgroundColor(Color.RED);
                                            CURRENT_BOOK=true;

                                        } else if(CURRENT_BOOK==true){
                                            usersRef.child(mAuth.getCurrentUser().getUid()).child("bookmarks").child(getRef(i).getKey()).child("bookmark_done").removeValue();
                                            recosViewHolder.getBookButton().setBackgroundColor(Color.TRANSPARENT);
                                            CURRENT_BOOK=false;
                                        }



                                    }
                                });

                                recosViewHolder.autreComment.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        b.putString("key", getRef(i).getKey());
                                        intent2.putExtras(b);
                                        startActivity(intent2);
                                    }
                                });

                                recosViewHolder.getListLike().setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        b.putString("key", getRef(i).getKey());
                                        intent1.putExtras(b);
                                        startActivity(intent1);

                                    }
                                });

                                recosViewHolder.getCommentButton().setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        b.putString("key", getRef(i).getKey());
                                        intent2.putExtras(b);
                                        startActivity(intent2);

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
                                        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"+" à "+"H"+":"+"mm");
                                        recosViewHolder.setTime("Le "+dateFormat.format(date));

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                recosViewHolder.getImgProfil().setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(mAuth.getCurrentUser().getUid().equals(model.getUserRecoUid())){
                                            Fragment fragment = new ProfilFragement();
                                            loadFragement(fragment);

                                        } else {
                                            b.putString("key", model.getUserRecoUid());
                                            intent3.putExtras(b);
                                            startActivity(intent3);
                                        }

                                    }
                                });

                                recosViewHolder.getDesc().setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.e("testest",""+model.getLink());
                                        Intent viewIntent =
                                                new Intent("android.intent.action.VIEW",
                                                        Uri.parse(model.getLink()));
                                        startActivity(viewIntent);
                                    }
                                });
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
