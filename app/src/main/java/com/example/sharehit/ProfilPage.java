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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sharehit.Model.Recommendation;
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

        FirebaseRecyclerAdapter<Recommendation, FeedFragement.RecosViewHolder> fireBaseRecyclerAdapter = new FirebaseRecyclerAdapter<Recommendation, FeedFragement.RecosViewHolder>
                (
                        Recommendation.class,
                        R.layout.recommandation_item,
                        FeedFragement.RecosViewHolder.class,
                        postUser
                ) {

            @Override
            protected void populateViewHolder(final FeedFragement.RecosViewHolder recosViewHolder, final Recommendation model, final int i) {

                Picasso.with(getApplicationContext()).load(model.getImg()).fit().centerInside().into(recosViewHolder.getImg());



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
                            recosViewHolder.getLikeButton().setImageResource(R.drawable.like);
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

                recosViewHolder.getBookButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String key = usersRef.child(mAuth.getCurrentUser().getUid()).child("bookmarks").push().getKey();
                        usersRef.child(mAuth.getCurrentUser().getUid()).child("bookmarks").child(key).setValue(getRef(i).getKey());

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
                            Picasso.with(getApplicationContext()).load(dataSnapshot.child("pdpUrl").getValue().toString()).fit().centerInside().into(recosViewHolder.getImgProfil());
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
                        b.putString("key", model.getUserRecoUid());
                        intent3.putExtras(b);
                        startActivity(intent3);
                    }
                });




            }
        };
        post.setAdapter(fireBaseRecyclerAdapter);
    }
}
