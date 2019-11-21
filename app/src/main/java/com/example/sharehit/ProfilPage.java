package com.example.sharehit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.util.concurrent.TimeUnit;

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

    private ProgressBar mSeekBarPlayer;
    private ImageButton stop;
    private ImageButton btnPause;
    private LinearLayout lecteur;
    private TextView nameLect;
    private ImageView musicImg;

    private Animation buttonClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_page);

        CURRENT_FOLLOW = false;

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        buttonClick = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click);

        lecteur = findViewById(R.id.lecteur);
        stop = findViewById(R.id.button1);
        btnPause = findViewById(R.id.button2);
        mSeekBarPlayer = findViewById(R.id.progressBar);
        nameLect = findViewById(R.id.nameLect);
        musicImg = findViewById(R.id.musicImg);

        lecteur.setVisibility(View.INVISIBLE);

        ViewGroup.LayoutParams params = lecteur.getLayoutParams();
        params.height=0;
        lecteur.setLayoutParams(params);

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

        if (mAuth.getCurrentUser().getUid().equals(b.getString("key"))) {
            follow.setVisibility(View.INVISIBLE);
            textView.setText("Mes recommandations");

        }

        usersRef.child(mAuth.getCurrentUser().getUid()).child("followed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getValue().equals(b.getString("key"))) {
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
                if (CURRENT_FOLLOW == false) {
                    HashMap usersMap = new HashMap();
                    usersMap.put(usersRef.child(mAuth.getCurrentUser().getUid()).child("followed").push().getKey(), b.getString("key"));
                    usersRef.child(mAuth.getCurrentUser().getUid()).child("followed").updateChildren(usersMap);
                    usersRef.child(mAuth.getCurrentUser().getUid()).child("followed").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if (ds.getValue().equals(b.getString("key"))) {
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
                    CURRENT_FOLLOW = true;

                } else if (CURRENT_FOLLOW == true) {
                    usersRef.child(mAuth.getCurrentUser().getUid()).child("followed").child(keyFollowed).removeValue();
                    follow.setText("Suivre");
                    CURRENT_FOLLOW = false;
                }
            }
        });

        Picasso.with(getApplicationContext()).load("https://firebasestorage.googleapis.com/v0/b/share-hit-52071.appspot.com/o/Pdp%2F" + b.getString("key") + "?alt=media&token=32f03c76-31a8-4ea2-8cac-8fa92bef6667").networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).fit().centerInside().into(pdp);

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

    @Override
    public void onPause() {
        super.onPause();
        mp.pause();
    }

    private void displayAllRecosUser(String uid) {

        final Intent intent1 = new Intent(getApplicationContext(), ListLikePage.class);
        final Intent intent2 = new Intent(getApplicationContext(), CommentPage.class);
        final Intent intent3 = new Intent(getApplicationContext(), ProfilPage.class);
        final Bundle b = new Bundle();

        Query postUser = recosRef.orderByChild("userRecoUid").startAt(uid).endAt(uid + "\uf8ff");

        final int[] tailleTableau = new int[1];
        postUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tailleTableau[0] = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<Recommandation, FeedFragement.RecosViewHolder> fireBaseRecyclerAdapter = new FirebaseRecyclerAdapter<Recommandation, FeedFragement.RecosViewHolder>
                (
                        Recommandation.class,
                        R.layout.recommandation_item,
                        FeedFragement.RecosViewHolder.class,
                        postUser
                ) {

            @Override
            protected void populateViewHolder(final FeedFragement.RecosViewHolder recosViewHolder, final Recommandation model, final int i) {
                final String[] keyBookmark = new String[tailleTableau[0]];
                final boolean[] CURRENT_BOOKMARK = new boolean[tailleTableau[0]];
                final String[] keyLike = new String[tailleTableau[0]];
                final boolean[] CURRENT_LIKE = new boolean[tailleTableau[0]];


                Picasso.with(getApplicationContext()).load(model.getUrlImage()).fit().centerInside().into(recosViewHolder.getImg());

                if (model.getType().equals("track")) {
                    String desc = "<b>" + model.getTrack() + "</b>" + " de " + "<b>" + model.getArtist() + "</b>";
                    recosViewHolder.setDesc(Html.fromHtml(desc));
                } else if (model.getType().equals("album")) {
                    String desc = "<b>" + model.getAlbum() + "</b>" + " de " + "<b>" + model.getArtist() + "</b>";
                    recosViewHolder.setDesc(Html.fromHtml(desc));
                } else {
                    String desc = "<b>" + model.getArtist() + "</b>";
                    recosViewHolder.setDesc(Html.fromHtml(desc));
                }

                final String idReco = getRef(i).getKey();

                //Log.e("testesto", model.getName()+" - "+model.getUrlPreview() +" - "+idReco);

                //Log.e(""+recosRef.child(idReco).toString(), "CURRENT_LIKE="+CURRENT_LIKE);

                recosRef.child(idReco).child("Coms").limitToLast(1).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.e("letesta", "" + dataSnapshot);
                        if (!dataSnapshot.hasChildren()) {
                            recosViewHolder.setPseudoCom("Aucun commentaire");
                            recosViewHolder.setAutreComment("0");
                            recosViewHolder.setNbrCom("");
                        } else {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                if (child.child("com").exists()) {

                                    recosViewHolder.setNbrCom(child.child("com").getValue().toString());
                                }
                                final String index = child.getKey();
                                String idUsr = dataSnapshot.child(index).child("uid").getValue().toString();
                                usersRef.child(idUsr).child("pseudo").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        recosViewHolder.setPseudoCom(dataSnapshot.getValue().toString() + " :");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                recosRef.child(idReco).child("Coms").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            recosViewHolder.setAutreComment("" + dataSnapshot.getChildrenCount());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                recosRef.child(idReco).child("likeUsersUid").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        recosViewHolder.setNbrLike(Long.toString(dataSnapshot.getChildrenCount()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                usersRef.child(mAuth.getCurrentUser().getUid()).child("bookmarks").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean test = false;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.getValue().equals(getRef(i).getKey())) {
                                test = true;
                                keyBookmark[i] = ds.getRef().getKey();
                                //follow.setText("Ne plus suivre");
                            }
                        }
                        if (test) {
                            recosViewHolder.getBookButton().setImageResource(R.drawable.bookmark_ok);
                            CURRENT_BOOKMARK[i] = true;
                        } else {
                            CURRENT_BOOKMARK[i] = false;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                recosRef.child(getRef(i).getKey()).child("likeUsersUid").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean test = false;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.getValue().equals(mAuth.getCurrentUser().getUid())) {
                                test = true;
                                keyLike[i] = ds.getRef().getKey();
                                //follow.setText("Ne plus suivre");
                            }
                        }
                        if (test) {
                            recosViewHolder.getLikeButton().setImageResource(R.drawable.red_heart);
                            CURRENT_LIKE[i] = true;
                        } else {
                            recosViewHolder.getLikeButton().setImageResource(R.drawable.heart);
                            CURRENT_LIKE[i] = false;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                recosViewHolder.getLikeButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*
                        if(CURRENT_LIKE[i] == false){
                            getRef(i).child("likeUsersUid").child(mAuth.getCurrentUser().getUid()).child("like_done").setValue("yes");
                            CURRENT_LIKE=true;
                        } else if(CURRENT_LIKE == true){
                            getRef(i).child("likeUsersUid").child(mAuth.getCurrentUser().getUid()).removeValue();
                            CURRENT_LIKE=false;
                        }

                         */
                        if (CURRENT_LIKE[i] == false) {
                            HashMap usersMap = new HashMap();
                            usersMap.put(recosRef.child(getRef(i).getKey()).child("likeUsersUid").push().getKey(), mAuth.getCurrentUser().getUid());
                            recosRef.child(getRef(i).getKey()).child("likeUsersUid").updateChildren(usersMap);
                            recosRef.child(getRef(i).getKey()).child("likeUsersUid").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        if (ds.getValue().equals(mAuth.getCurrentUser().getUid())) {
                                            Log.e("Like key", ds.getRef().getKey());
                                            keyLike[i] = ds.getRef().getKey();

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            //follow.setText("Ne plus suivre");
                            recosViewHolder.getLikeButton().setImageResource(R.drawable.red_heart);
                            CURRENT_LIKE[i] = true;

                        } else if (CURRENT_LIKE[i] == true) {
                            recosRef.child(getRef(i).getKey()).child("likeUsersUid").child(keyLike[i]).removeValue();
                            recosViewHolder.getLikeButton().setImageResource(R.drawable.heart);
                            //follow.setText("Suivre");
                            CURRENT_LIKE[i] = false;

                        }

                    }

                });


                final GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        if (CURRENT_LIKE[i] == false) {
                            HashMap usersMap = new HashMap();
                            usersMap.put(recosRef.child(getRef(i).getKey()).child("likeUsersUid").push().getKey(), mAuth.getCurrentUser().getUid());
                            recosRef.child(getRef(i).getKey()).child("likeUsersUid").updateChildren(usersMap);
                            recosRef.child(getRef(i).getKey()).child("likeUsersUid").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        if (ds.getValue().equals(mAuth.getCurrentUser().getUid())) {
                                            Log.e("Like key", ds.getRef().getKey());
                                            keyLike[i] = ds.getRef().getKey();

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            //follow.setText("Ne plus suivre");
                            recosViewHolder.getLikeButton().setImageResource(R.drawable.red_heart);
                            CURRENT_LIKE[i] = true;

                        } else if (CURRENT_LIKE[i] == true) {
                            recosRef.child(getRef(i).getKey()).child("likeUsersUid").child(keyLike[i]).removeValue();
                            recosViewHolder.getLikeButton().setImageResource(R.drawable.heart);
                            //follow.setText("Suivre");
                            CURRENT_LIKE[i] = false;

                        }
                        return true;
                    }

                    public boolean onSingleTapConfirmed(MotionEvent e) {

                        String link = "";
                        Log.e("testest", "" + model.getId());
                        if (model.getType().equals("track")) {
                            link = "https://www.deezer.com/fr/track/" + model.getId();
                        } else if (model.getType().equals("album")) {
                            link = "https://www.deezer.com/fr/album/" + model.getId();
                        } else if (model.getType().equals("artist")) {
                            link = "https://www.deezer.com/fr/artist/" + model.getId();
                        } else {
                            link = "https://www.imdb.com/title/" + model.getId();
                        }
                        Intent viewIntent =
                                new Intent("android.intent.action.VIEW",
                                        Uri.parse(link));
                        startActivity(viewIntent);

                        return true;
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

                recosViewHolder.getBookButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CURRENT_BOOKMARK[i] == false) {
                            HashMap usersMap = new HashMap();
                            usersMap.put(usersRef.child(mAuth.getCurrentUser().getUid()).child("bookmarks").push().getKey(), getRef(i).getKey());
                            usersRef.child(mAuth.getCurrentUser().getUid()).child("bookmarks").updateChildren(usersMap);
                            usersRef.child(mAuth.getCurrentUser().getUid()).child("bookmarks").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        if (ds.getValue().equals(b.getString("key"))) {
                                            Log.e("Bookmark key", ds.getRef().getKey());
                                            keyBookmark[i] = ds.getRef().getKey();

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            //follow.setText("Ne plus suivre");
                            recosViewHolder.getBookButton().setImageResource(R.drawable.bookmark_ok);
                            CURRENT_BOOKMARK[i] = true;

                        } else if (CURRENT_BOOKMARK[i] == true) {
                            usersRef.child(mAuth.getCurrentUser().getUid()).child("bookmarks").child(keyBookmark[i]).removeValue();
                            recosViewHolder.getBookButton().setImageResource(R.drawable.bookmark);
                            //follow.setText("Suivre");
                            CURRENT_BOOKMARK[i] = false;

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


                Picasso.with(getApplicationContext()).load("https://firebasestorage.googleapis.com/v0/b/share-hit-52071.appspot.com/o/Pdp%2F" + model.getUserRecoUid() + "?alt=media&token=32f03c76-31a8-4ea2-8cac-8fa92bef6667").fit().centerInside().into(recosViewHolder.getImgProfil());

                usersRef.child(model.getUserRecoUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String pseudo = dataSnapshot.child("pseudo").getValue().toString();
                        String typeReco = "";
                        if (model.getType().equals("track")) {
                            typeReco = "un morceau";
                        } else if (model.getType().equals("artist")) {
                            typeReco = "un artiste";
                        } else if (model.getType().equals("album")) {
                            typeReco = "un album";
                        } else if (model.getType().equals("movie")) {
                            typeReco = "un film";
                        } else if (model.getType().equals("serie")) {
                            typeReco = "une série";
                        } else if (model.getType().equals("game")) {
                            typeReco = "un jeu vidéo";
                        }
                        final String sourceString = "<b>" + pseudo + "</b>" + " a recommandé " + "<b>" + typeReco + "</b>";

                        recosViewHolder.setTitre(Html.fromHtml(sourceString));

                        long currentTimestamp = System.currentTimeMillis();
                        long searchTimestamp = Long.parseLong(model.getTimestamp());
                        long difference = Math.abs(currentTimestamp - searchTimestamp);
                        if (TimeUnit.MILLISECONDS.toSeconds(currentTimestamp) == TimeUnit.MILLISECONDS.toSeconds(searchTimestamp)) {
                            recosViewHolder.setTime("À l'instant");
                        } else {
                            recosViewHolder.setTime("Il y a " + convertTimeStampToBelleHeureSaMere(difference));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                recosViewHolder.getDesc().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String link = "";
                        Log.e("testest", "" + model.getId());
                        if (model.getType().equals("track")) {
                            link = "https://www.deezer.com/fr/track/" + model.getId();
                        } else if (model.getType().equals("album")) {
                            link = "https://www.deezer.com/fr/album/" + model.getId();
                        } else if (model.getType().equals("artiste")) {
                            link = "https://www.deezer.com/fr/artist/" + model.getId();
                        } else {
                            link = "https://www.imdb.com/title/" + model.getId();
                        }
                        Intent viewIntent =
                                new Intent("android.intent.action.VIEW",
                                        Uri.parse(link));
                        startActivity(viewIntent);
                    }
                });

                recosRef.child(idReco).child("urlPreview").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (!dataSnapshot.getValue().toString().equals("")) {
                                recosViewHolder.playButton.setVisibility(View.VISIBLE);
                                recosViewHolder.circle.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                /*Log.e("jhsvhx", recosViewHolder.getDesc().getText().toString().equals(nameLect.getText().toString())+"");

                if(recosViewHolder.getDesc().getText().toString().equals(nameLect.getText().toString())){
                    Log.e("jhsvhx", ""+recosViewHolder.getDesc().getText());
                    recosViewHolder.playButton.setVisibility(View.INVISIBLE);
                    recosViewHolder.player.setVisibility(View.VISIBLE);
                }else{
                    recosViewHolder.playButton.setVisibility(View.VISIBLE);
                    recosViewHolder.player.setVisibility(View.INVISIBLE);
                }*/

                /*recosViewHolder.playButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        recosViewHolder.playButton.startAnimation(buttonClick);
                        return true;
                    }
                });*/

                recosViewHolder.playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mp.seekTo(mp.getDuration());
                        mp.reset();
                        if (lecteur.getVisibility() == View.INVISIBLE) {
                            lecteur.setVisibility(View.VISIBLE);
                            ViewGroup.LayoutParams params = lecteur.getLayoutParams();
                            params.height = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
                            lecteur.setLayoutParams(params);
                        }
                        try {
                            Log.e("testest", "" + model.getUrlPreview());
                            mp.setDataSource(model.getUrlPreview());
                        } catch (IOException ex) {
                            Log.e("testest", "Can't found data:" + model.getUrlPreview());
                        }


                        if (model.getType().equals("track"))
                            nameLect.setText(model.getTrack());
                        else if (model.getType().equals("artist"))
                            nameLect.setText(model.getArtist());
                        else if (model.getType().equals("album"))
                            nameLect.setText(model.getAlbum());
                        /*recosViewHolder.playButton.setVisibility(View.INVISIBLE);
                        recosViewHolder.player.setVisibility(View.VISIBLE);*/


                        Picasso.with(getApplicationContext()).load(model.getUrlImage()).fit().centerInside().into(musicImg);
                        mp.prepareAsync();
                        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                int duration = mp.getDuration();
                                mSeekBarPlayer.setMax(duration);
                                mp.start();
                                mSeekBarPlayer.postDelayed(onEverySecond, 500);
                            }
                        });

                        recosViewHolder.playButton.startAnimation(buttonClick);

                        stop.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                mp.stop();
                                mp.reset();
                                lecteur.setVisibility(View.INVISIBLE);



                                /*recosViewHolder.playButton.setVisibility(View.VISIBLE);
                                recosViewHolder.playButton.setImageResource(R.drawable.ic_play);
                                recosViewHolder.player.setVisibility(View.INVISIBLE);*/

                                ViewGroup.LayoutParams params = lecteur.getLayoutParams();
                                params.height = 0;
                                lecteur.setLayoutParams(params);
                            }
                        });


                        btnPause.setOnClickListener(new View.OnClickListener() {


                            @Override
                            public void onClick(View v) {
                                if (mp.isPlaying()) {
                                    mp.pause();
                                    btnPause.setImageResource(R.drawable.ic_play);
                                    /*recosViewHolder.playButton.setVisibility(View.VISIBLE);
                                    recosViewHolder.playButton.setImageResource(R.drawable.ic_pause);
                                    recosViewHolder.player.setVisibility(View.INVISIBLE);*/

                                } else {
                                    btnPause.setImageResource(R.drawable.ic_pause);
                                    /*recosViewHolder.playButton.setVisibility(View.INVISIBLE);
                                    recosViewHolder.player.setVisibility(View.VISIBLE);*/
                                    try {
                                        mp.prepare();
                                    } catch (IllegalStateException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    mp.start();
                                    mSeekBarPlayer.postDelayed(onEverySecond, 1000);
                                }

                            }
                        });
                    }
                });


            }
        };


        post.setAdapter(fireBaseRecyclerAdapter);
    }

    private String convertTimeStampToBelleHeureSaMere(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        if (days != 0) {
            if (days == 1) {
                sb.append(days);
                sb.append(" jour ");
            } else {
                sb.append(days);
                sb.append(" jours ");
            }

        } else if (hours != 0) {
            if (hours == 1) {
                sb.append(hours);
                sb.append(" heure ");
            } else {
                sb.append(hours);
                sb.append(" heures ");
            }

        } else if (minutes != 0) {
            if (minutes == 1) {
                sb.append(minutes);
                sb.append(" minute ");
            } else {
                sb.append(minutes);
                sb.append(" minutes ");
            }
        } else if (seconds != 0) {
            sb.append(seconds);
            sb.append(" secondes");
        }


        return (sb.toString());
    }

    private Runnable onEverySecond = new Runnable() {
        @Override
        public void run(){
            if(mp != null) {
                mSeekBarPlayer.setProgress(mp.getCurrentPosition());
            }

            if(mp.isPlaying()) {
                btnPause.setImageResource(R.drawable.ic_pause);
                mSeekBarPlayer.postDelayed(onEverySecond, 100);
            }else{
                btnPause.setImageResource(R.drawable.ic_play);
            }
        }
    };

}
