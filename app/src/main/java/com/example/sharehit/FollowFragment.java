package com.example.sharehit;

import android.app.ActionBar;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharehit.Model.Recommandation;
import com.example.sharehit.Utilities.OnSwipeTouchListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class FollowFragment extends Fragment {


    RecyclerView recyclerView;
    private DatabaseReference recosRef, followRef, usersRef;
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
    private MyListenerFollow callBack;

    private Animation buttonClick;

    @Override
    public void onPause() {
        super.onPause();
        mp.pause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragement_search, null);

        callBack = (MyListenerFollow) getActivity();

        root.setOnTouchListener(new OnSwipeTouchListener(getContext()) {

            public void onSwipeLeft() {
                callBack.onSwipeLeftFollow();
            }
            public void onSwipeRight() {
                callBack.onSwipeRightFollow();
            }

        });

        buttonClick = AnimationUtils.loadAnimation(getContext(), R.anim.click);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        recosRef = FirebaseDatabase.getInstance().getReference().child("recos");
        followRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("followed");
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        lecteur = root.findViewById(R.id.lecteur);
        stop = root.findViewById(R.id.button1);
        btnPause = root.findViewById(R.id.button2);
        mSeekBarPlayer = root.findViewById(R.id.progressBar);
        nameLect = root.findViewById(R.id.nameLect);
        musicImg = root.findViewById(R.id.musicImg);

        lecteur.setVisibility(View.INVISIBLE);

        ViewGroup.LayoutParams params = lecteur.getLayoutParams();
        params.height=0;
        lecteur.setLayoutParams(params);

        recyclerView = root.findViewById(R.id.postFollowRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);


        displayAllRecosFollow();

        return root;
    }

    private String convertTimeStampToBelleHeureSaMere(long millis) {
        if(millis < 0) {
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
        if(days != 0){
            if(days == 1){
                sb.append(days);
                sb.append(" jour ");
            } else {
                sb.append(days);
                sb.append(" jours ");
            }

        } else if(hours != 0){
            if(hours == 1){
                sb.append(hours);
                sb.append(" heure ");
            } else {
                sb.append(hours);
                sb.append(" heures ");
            }

        } else if(minutes != 0){
            if(minutes == 1){
                sb.append(minutes);
                sb.append(" minute ");
            } else {
                sb.append(minutes);
                sb.append(" minutes ");
            }
        } else if(seconds != 0){
            sb.append(seconds);
            sb.append(" secondes");
        }




        return(sb.toString());
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

        FirebaseRecyclerAdapter<Recommandation, FeedFragment.RecosViewHolder> fireBaseRecyclerAdapter = new FirebaseRecyclerAdapter<Recommandation, FeedFragment.RecosViewHolder>
                (
                        Recommandation.class,
                        R.layout.recommandation_item,
                        FeedFragment.RecosViewHolder.class,
                        recosRef
                ) {

            @Override
            protected void populateViewHolder(final FeedFragment.RecosViewHolder recosViewHolder, final Recommandation model, final int i) {


                followRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean test = false;
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            if(data.getValue().equals(model.getUserRecoUid())){
                                test = true;

                            }
                        }
                        if(test){
                            final String[] keyBookmark = new String[tailleTableau[0]];
                            final boolean[] CURRENT_BOOKMARK = new boolean[tailleTableau[0]];
                            final String[] keyLike = new String[tailleTableau[0]];
                            final boolean[] CURRENT_LIKE = new boolean[tailleTableau[0]];


                            Picasso.with(getContext()).load(model.getUrlImage()).fit().centerInside().into(recosViewHolder.getImg());

                            if(model.getType().equals("track")){
                                String desc ="<b>"+model.getTrack()+"</b>"+" de "+"<b>"+model.getArtist()+"</b>";
                                recosViewHolder.setDesc(Html.fromHtml(desc));
                            } else if(model.getType().equals("album")){
                                String desc ="<b>"+model.getAlbum()+"</b>"+" de "+"<b>"+model.getArtist()+"</b>";
                                recosViewHolder.setDesc(Html.fromHtml(desc));
                            } else {
                                String desc ="<b>"+model.getArtist()+"</b>";
                                recosViewHolder.setDesc(Html.fromHtml(desc));
                            }

                            final String idReco = getRef(i).getKey();

                            //Log.e("testesto", model.getName()+" - "+model.getUrlPreview() +" - "+idReco);

                            //Log.e(""+recosRef.child(idReco).toString(), "CURRENT_LIKE="+CURRENT_LIKE);

                            recosRef.child(idReco).child("Coms").limitToLast(1).addValueEventListener(new ValueEventListener(){

                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Log.e("letesta", ""+dataSnapshot );
                                    if (!dataSnapshot.hasChildren()){
                                        recosViewHolder.setPseudoCom("Aucun commentaire");
                                        recosViewHolder.setAutreComment("0");
                                        recosViewHolder.setNbrCom("");
                                    }
                                    else {
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
                                    if(dataSnapshot.exists()) {
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
                                @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
                            });

                            usersRef.child(mAuth.getCurrentUser().getUid()).child("bookmarks").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    boolean test = false;
                                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                                        if(ds.getValue().equals(getRef(i).getKey())){
                                            test = true;
                                            keyBookmark[i] = ds.getRef().getKey();
                                            //follow.setText("Ne plus suivre");
                                        }
                                    }
                                    if(test){
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
                                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                                        if(ds.getValue().equals(mAuth.getCurrentUser().getUid())){
                                            test = true;
                                            keyLike[i] = ds.getRef().getKey();
                                            //follow.setText("Ne plus suivre");
                                        }
                                    }
                                    if(test){
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
                                    if(CURRENT_LIKE[i] == false){
                                        HashMap usersMap = new HashMap();
                                        usersMap.put(recosRef.child(getRef(i).getKey()).child("likeUsersUid").push().getKey(), mAuth.getCurrentUser().getUid());
                                        recosRef.child(getRef(i).getKey()).child("likeUsersUid").updateChildren(usersMap);
                                        recosRef.child(getRef(i).getKey()).child("likeUsersUid").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                                    if(ds.getValue().equals(mAuth.getCurrentUser().getUid())){
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
                                        CURRENT_LIKE[i] =true;

                                    } else if(CURRENT_LIKE[i] == true){
                                        recosRef.child(getRef(i).getKey()).child("likeUsersUid").child(keyLike[i]).removeValue();
                                        recosViewHolder.getLikeButton().setImageResource(R.drawable.heart);
                                        //follow.setText("Suivre");
                                        CURRENT_LIKE[i] =false;

                                    }

                                }

                            });


                            final GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
                                @Override
                                public boolean onDoubleTap(MotionEvent e) {
                                    if(CURRENT_LIKE[i] == false){
                                        HashMap usersMap = new HashMap();
                                        usersMap.put(recosRef.child(getRef(i).getKey()).child("likeUsersUid").push().getKey(), mAuth.getCurrentUser().getUid());
                                        recosRef.child(getRef(i).getKey()).child("likeUsersUid").updateChildren(usersMap);
                                        recosRef.child(getRef(i).getKey()).child("likeUsersUid").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                                    if(ds.getValue().equals(mAuth.getCurrentUser().getUid())){
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
                                        CURRENT_LIKE[i] =true;

                                    } else if(CURRENT_LIKE[i] == true){
                                        recosRef.child(getRef(i).getKey()).child("likeUsersUid").child(keyLike[i]).removeValue();
                                        recosViewHolder.getLikeButton().setImageResource(R.drawable.heart);
                                        //follow.setText("Suivre");
                                        CURRENT_LIKE[i] =false;

                                    }
                                    return true;
                                }

                                public boolean onSingleTapConfirmed(MotionEvent e) {

                                    String link ="";
                                    Log.e("testest",""+model.getId());
                                    if (model.getType().equals("track")){
                                        link="https://www.deezer.com/fr/track/"+model.getId();
                                    } else if (model.getType().equals("album")){
                                        link="https://www.deezer.com/fr/album/"+model.getId();
                                    }else if (model.getType().equals("artist")){
                                        link="https://www.deezer.com/fr/artist/"+model.getId();
                                    } else{
                                        link="https://www.imdb.com/title/"+model.getId();
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
                                    if(CURRENT_BOOKMARK[i] == false){
                                        HashMap usersMap = new HashMap();
                                        usersMap.put(usersRef.child(mAuth.getCurrentUser().getUid()).child("bookmarks").push().getKey(), getRef(i).getKey());
                                        usersRef.child(mAuth.getCurrentUser().getUid()).child("bookmarks").updateChildren(usersMap);
                                        usersRef.child(mAuth.getCurrentUser().getUid()).child("bookmarks").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                                    if(ds.getValue().equals(b.getString("key"))){
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
                                        CURRENT_BOOKMARK[i] =true;

                                    } else if(CURRENT_BOOKMARK[i] == true){
                                        usersRef.child(mAuth.getCurrentUser().getUid()).child("bookmarks").child(keyBookmark[i]).removeValue();
                                        recosViewHolder.getBookButton().setImageResource(R.drawable.bookmark);
                                        //follow.setText("Suivre");
                                        CURRENT_BOOKMARK[i] =false;

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


                            Picasso.with(getContext()).load("https://firebasestorage.googleapis.com/v0/b/share-hit.appspot.com/o/"+model.getUserRecoUid()+"?alt=media&token=1d93f69f-a530-455a-83d2-929ce42c3667").fit().centerInside().into(recosViewHolder.getImgProfil());

                            usersRef.child(model.getUserRecoUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    final String pseudo = dataSnapshot.child("pseudo").getValue().toString();
                                    String typeReco="";
                                    if (model.getType().equals("track")) {
                                        typeReco = "un morceau";
                                    } else if (model.getType().equals("artist")){
                                        typeReco = "un artiste";
                                    } else if (model.getType().equals("album")){
                                        typeReco="un album";
                                    } else if (model.getType().equals("movie")){
                                        typeReco="un film";
                                    } else if (model.getType().equals("serie")){
                                        typeReco="une série";
                                    } else if (model.getType().equals("game")){
                                        typeReco="un jeu vidéo";
                                    }
                                    final String sourceString = "<b>"+pseudo+"</b>"+ " a recommandé " +"<b>"+typeReco+"</b>";

                                    recosViewHolder.setTitre(Html.fromHtml(sourceString));

                                    long currentTimestamp = System.currentTimeMillis();
                                    double searchTimestampD = model.getTimestamp();
                                    long searchTimestamp = (long)searchTimestampD;
                                    long difference = Math.abs(currentTimestamp - searchTimestamp);
                                    if(TimeUnit.MILLISECONDS.toSeconds(currentTimestamp)==TimeUnit.MILLISECONDS.toSeconds(searchTimestamp)) {
                                        recosViewHolder.setTime("À l'instant");
                                    }else {
                                        recosViewHolder.setTime("Il y a " + convertTimeStampToBelleHeureSaMere(difference));
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            recosViewHolder.getImgProfil().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(mAuth.getCurrentUser().getUid().equals(model.getUserRecoUid())){
                                        Fragment fragment = new ProfilFragment();
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
                                    String link ="";
                                    Log.e("testest",""+model.getId());
                                    if (model.getType().equals("track")){
                                        link="https://www.deezer.com/fr/track/"+model.getId();
                                    } else if (model.getType().equals("album")){
                                        link="https://www.deezer.com/fr/album/"+model.getId();
                                    }else if (model.getType().equals("artiste")){
                                        link="https://www.deezer.com/fr/artist/"+model.getId();
                                    } else{
                                        link="https://www.imdb.com/title/"+model.getId();
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
                                    if(dataSnapshot.exists()){
                                        if (!dataSnapshot.getValue().toString().equals("")) {
                                            recosViewHolder.playButton.setVisibility(View.VISIBLE);
                                            recosViewHolder.circle.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                                @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
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
                                    if (lecteur.getVisibility()==View.INVISIBLE) {
                                        lecteur.setVisibility(View.VISIBLE);
                                        ViewGroup.LayoutParams params = lecteur.getLayoutParams();
                                        params.height = ActionBar.LayoutParams.WRAP_CONTENT;
                                        lecteur.setLayoutParams(params);
                                    }
                                    try{
                                        Log.e("testest", ""+model.getUrlPreview() );mp.setDataSource(model.getUrlPreview());
                                    }
                                    catch (IOException ex){
                                        Log.e("testest", "Can't found data:"+model.getUrlPreview());
                                    }


                                    if(model.getType().equals("track"))
                                        nameLect.setText(model.getTrack());
                                    else if(model.getType().equals("artist"))
                                        nameLect.setText(model.getArtist());
                                    else if(model.getType().equals("album"))
                                        nameLect.setText(model.getAlbum());
                        /*recosViewHolder.playButton.setVisibility(View.INVISIBLE);
                        recosViewHolder.player.setVisibility(View.VISIBLE);*/


                                    Picasso.with(getContext()).load(model.getUrlImage()).fit().centerInside().into(musicImg);
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
                                            params.height=0;
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

                                            }
                                            else {
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

                        } else {
                            recosViewHolder.layout_hide();

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
            return true;
        }
        return false;
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

    public interface MyListenerFollow{
        public void onSwipeLeftFollow();
        public void onSwipeRightFollow();
    }
}