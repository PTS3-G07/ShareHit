package com.example.sharehit;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharehit.Adapter.AdapterRecs;
import com.example.sharehit.Model.Morceau;
import com.example.sharehit.Model.Recommendation;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.resources.TextAppearance;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.taishi.library.Indicator;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.datatype.Duration;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedFragement extends Fragment {

    public static final String EXTRA_PREVIEW = "userUrlPreview";

    RecyclerView recyclerView;
    private DatabaseReference recosRef, usersRef;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private final static MediaPlayer mp = new MediaPlayer();
    private ProgressBar mSeekBarPlayer;
    private ImageButton stop;
    private ImageButton btnPause;
    private LinearLayout lecteur;
    private TextView nameLect;
    private ImageView musicImg;
    private Bundle b;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragement_feed, null);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        recosRef = FirebaseDatabase.getInstance().getReference().child("recos");
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




        recyclerView = root.findViewById(R.id.postRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);


        displayAllRecos();

        class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
            private static final String DEBUG_TAG = "Gestures";

            @Override
            public boolean onDown(MotionEvent event) {
                Log.d(DEBUG_TAG,"onDown: " + event.toString());
                return true;
            }

            @Override
            public boolean onFling(MotionEvent event1, MotionEvent event2,
                                   float velocityX, float velocityY) {
                Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
                return true;
            }
        }

        return root;
    }


    private void displayAllRecos() {
        final Intent intent1 = new Intent(getContext(), ListLikePage.class);
        final Intent intent2 = new Intent(getContext(), CommentPage.class);
        final Intent intent3 = new Intent(getContext(), ProfilPage.class);
        final Bundle b = new Bundle();
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

        FirebaseRecyclerAdapter<Recommendation, RecosViewHolder> fireBaseRecyclerAdapter = new FirebaseRecyclerAdapter<Recommendation, RecosViewHolder>
                (
                        Recommendation.class,
                        R.layout.recommandation_item,
                        RecosViewHolder.class,
                        recosRef
                ) {

            @Override
            protected void populateViewHolder(final RecosViewHolder recosViewHolder, final Recommendation model, final int i) {

                final String[] keyBookmark = new String[tailleTableau[0]];
                final boolean[] CURRENT_BOOKMARK = new boolean[tailleTableau[0]];
                final String[] keyLike = new String[tailleTableau[0]];
                final boolean[] CURRENT_LIKE = new boolean[tailleTableau[0]];

                Picasso.with(getContext()).load(model.getImg()).fit().centerInside().into(recosViewHolder.getImg());

                recosViewHolder.setDesc(model.getName());
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
                        /*if(dataSnapshot.getChildrenCount()==0){
                            recosViewHolder.setNbrLike("Personne n'aime ça");
                        }
                        else if(dataSnapshot.getChildrenCount()==1){
                            recosViewHolder.setNbrLike("Aimé par 1 personne");
                        }
                        else{
                            recosViewHolder.setNbrLike("Aimé par " + Long.toString(dataSnapshot.getChildrenCount()) + " personnes");
                        }*/
                        recosViewHolder.setNbrLike(Long.toString(dataSnapshot.getChildrenCount()));
                    }
                    @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

                /*
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

                 */


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
                        /*
                        if(CURRENT_LIKE == false){
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
                        return true;
                    }

                    public boolean onSingleTapConfirmed(MotionEvent e) {

                        Log.e("testest",""+model.getLink());
                        Intent viewIntent =
                                new Intent("android.intent.action.VIEW",
                                        Uri.parse(model.getLink()));
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
                                            Log.e("Followed key", ds.getRef().getKey());
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




                Picasso.with(getContext()).load("https://firebasestorage.googleapis.com/v0/b/share-hit-52071.appspot.com/o/Pdp%2F"+model.getUserRecoUid()+"?alt=media&token=32f03c76-31a8-4ea2-8cac-8fa92bef6667").networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).fit().centerInside().into(recosViewHolder.getImgProfil());

                usersRef.child(model.getUserRecoUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String pseudo = dataSnapshot.child("pseudo").getValue().toString();
                        recosViewHolder.setTitre(pseudo + " a recommandé " + model.getType());
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

                if(model.getUrlPreview()!=null ) {
                    recosViewHolder.playButton.setVisibility(View.VISIBLE);
                    recosViewHolder.circle.setVisibility(View.VISIBLE);
                }

                recosViewHolder.playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recosViewHolder.playButton.setVisibility(View.INVISIBLE);
                        recosViewHolder.player.setVisibility(View.VISIBLE);
                        mp.reset();
                        if (lecteur.getVisibility()==View.INVISIBLE) {
                            lecteur.setVisibility(View.VISIBLE);
                            ViewGroup.LayoutParams params = lecteur.getLayoutParams();
                            params.height = ActionBar.LayoutParams.WRAP_CONTENT;
                            lecteur.setLayoutParams(params);
                        }
                        try{
                            Log.e("testest", ""+model.getUrlPreview() );
                            mp.setDataSource(model.getUrlPreview());
                        }
                        catch (IOException ex){
                            Log.e("testest", "Can't found data:"+model.getUrlPreview());
                        }

                        nameLect.setText(model.getName());
                        Picasso.with(getContext()).load(model.getImg()).fit().centerInside().into(musicImg);
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

                        mSeekBarPlayer.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                return false;
                            }
                        });

                        stop.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                mp.stop();
                                mp.reset();
                                lecteur.setVisibility(View.INVISIBLE);

                                recosViewHolder.playButton.setVisibility(View.VISIBLE);
                                recosViewHolder.player.setVisibility(View.INVISIBLE);

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
                                    recosViewHolder.playButton.setVisibility(View.VISIBLE);
                                    recosViewHolder.player.setVisibility(View.INVISIBLE);

                                }
                                else {
                                    btnPause.setImageResource(R.drawable.ic_pause);
                                    recosViewHolder.playButton.setVisibility(View.INVISIBLE);
                                    recosViewHolder.player.setVisibility(View.VISIBLE);
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



    public static class RecosViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView nbrlike;
        TextView nbrCom;
        TextView pseudoCom;
        TextView autreComment;
        TextView descR;
        ImageView pause;
        ImageView playButton;
        Indicator player;
        ImageView circle;

        public RecosViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
            nbrlike = (TextView) mView.findViewById(R.id.nbrLike);
            nbrCom = (TextView) mView.findViewById(R.id.nbrComment);
            pseudoCom = mView.findViewById(R.id.pseudoComment);
            autreComment = mView.findViewById(R.id.autreComment);
            descR = (TextView) mView.findViewById(R.id.desc);
            playButton = mView.findViewById(R.id.playButton);
            player = mView.findViewById(R.id.player);

            circle = mView.findViewById(R.id.circle);
            player.setVisibility(View.INVISIBLE);
            playButton.setVisibility(View.INVISIBLE);
            circle.setVisibility(View.INVISIBLE);
            //pause = mView.findViewById(R.id.pauseButton);
            //progressBar = mView.findViewById(R.id.timeProgressBar);
            //pause.setVisibility(View.INVISIBLE);
            //progressBar.setVisibility(View.INVISIBLE);
        }

        public void setAutreComment(String autreComment1){
            autreComment.setText(autreComment1);
        }

        public void setTime(String timeText){
            TextView time = (TextView) mView.findViewById(R.id.time);
            time.setText(timeText);
        }

        public void setPseudoCom(String pseudo){
            pseudoCom.setText(pseudo);
        }

        public void setDesc(String desc){
            descR.setText(desc);
        }

        public TextView getDesc(){
            return descR;
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
            nbrlike.setText(text);
        }

        public void setNbrCom(String text){
            nbrCom.setText(text);
        }

        public String getNbrLike(){
            return nbrlike.getText().toString();
        }

        public String getNbrComment(){
            return nbrCom.getText().toString();
        }

        public TextView getListLike(){
            TextView nbrLikeBut = (TextView) mView.findViewById(R.id.nbrLike);
            return  nbrLikeBut;
        }

        public ImageButton getCommentButton(){
            ImageButton button = (ImageButton) mView.findViewById(R.id.commentButton);
            return button;
        }

        public ImageButton getBookButton(){
            ImageButton img = (ImageButton) mView.findViewById(R.id.bookButton);
            return img;
        }


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
