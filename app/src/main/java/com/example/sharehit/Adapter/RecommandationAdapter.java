package com.example.sharehit.Adapter;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharehit.Model.Recommandation;
import com.example.sharehit.Model.User;
import com.example.sharehit.ProfilFragment;
import com.example.sharehit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecommandationAdapter extends
        RecyclerView.Adapter<RecommandationAdapter.ViewHolder> {

    Context context;
    private DatabaseReference recosRef, usersRef;
    private FirebaseAuth mAuth;
    private int[] tailleTableau;
    List<Recommandation> mRecommandation;

    public RecommandationAdapter(List<Recommandation> recommandations) {
        this.mRecommandation = recommandations;
    }

    @Override
    public RecommandationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        mAuth = FirebaseAuth.getInstance();

        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        recosRef = FirebaseDatabase.getInstance().getReference().child("recos");

        View recommandationView = inflater.inflate(R.layout.recommandation_item, parent, false);

        RecommandationAdapter.ViewHolder viewHolder = new RecommandationAdapter.ViewHolder(recommandationView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecommandationAdapter.ViewHolder viewHolder, final int position) {
        final Recommandation recommandation = mRecommandation.get(position);

        //Initialisation des item de la recommandation
        /*TextView descRecommandation = viewHolder.descRecommandation;
        TextView nbrlike = viewHolder.nbrlike;
        TextView nbrCom = viewHolder.nbrCom;
        TextView pseudoCom = viewHolder.pseudoCom;
        TextView autreComment = viewHolder.autreComment;
        ImageButton commentButton = viewHolder.commentButton;
        ImageButton bookmarkButton = viewHolder.bookmarkButton;
        ImageButton likeButton = viewHolder.likeButton;
        ImageButton pictureRecommandation = viewHolder.pictureRecommandation;
        TextView timeRecommandation = viewHolder.timeRecommandation;
        TextView nomRecommandation = viewHolder.nomRecommandation;
        CircleImageView pictureUserRecommandation = viewHolder.pictureUserRecommandation;
        ImageView playButton =viewHolder.playButton;
        ImageView circle = viewHolder.circle;*/

        String idReco = recommandation.getCleReco();

        String desc = "";
        if(recommandation.getType().equals("track")){
            desc ="<b>"+recommandation.getTrack()+"</b>"+" de "+"<b>"+recommandation.getArtist()+"</b>";
        } else if(recommandation.getType().equals("album")){
            desc ="<b>"+recommandation.getAlbum()+"</b>"+" de "+"<b>"+recommandation.getArtist()+"</b>";
        } else {
            desc ="<b>"+recommandation.getArtist()+"</b>";
        }
        viewHolder.setDesc(Html.fromHtml(desc));

        Picasso.with(context).load(recommandation.getUrlImage()).fit().centerInside().into(viewHolder.pictureRecommandation);
        
        //c'est la merde


        recosRef.child(idReco).child("Coms").limitToLast(1).addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("letesta", ""+dataSnapshot );
                if (!dataSnapshot.hasChildren()){
                    viewHolder.setPseudoCom("Aucun commentaire");
                    viewHolder.setAutreComment("0");
                    viewHolder.setNbrCom("");
                }
                else {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.child("com").exists()) {

                            viewHolder.setNbrCom(child.child("com").getValue().toString());
                        }
                        final String index = child.getKey();
                        String idUsr = dataSnapshot.child(index).child("uid").getValue().toString();
                        usersRef.child(idUsr).child("pseudo").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                viewHolder.setPseudoCom(dataSnapshot.getValue().toString() + " :");
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
                    viewHolder.setAutreComment("" + dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recosRef.child(idReco).child("likeUsersUid").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                viewHolder.setNbrLike(Long.toString(dataSnapshot.getChildrenCount()));
            }
            @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


        Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/share-hit.appspot.com/o/"+recommandation.getUserRecoUid()+"?alt=media&token=1d93f69f-a530-455a-83d2-929ce42c3667").fit().centerInside().into(viewHolder.getImgProfil());

        usersRef.child(recommandation.getUserRecoUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String pseudo = dataSnapshot.child("pseudo").getValue().toString();
                String typeReco="";
                if (recommandation.getType().equals("track")) {
                    typeReco = "un morceau";
                } else if (recommandation.getType().equals("artist")){
                    typeReco = "un artiste";
                } else if (recommandation.getType().equals("album")){
                    typeReco="un album";
                } else if (recommandation.getType().equals("movie")){
                    typeReco="un film";
                } else if (recommandation.getType().equals("serie")){
                    typeReco="une série";
                } else if (recommandation.getType().equals("game")){
                    typeReco="un jeu vidéo";
                }
                final String sourceString = "<b>"+pseudo+"</b>"+ " a recommandé " +"<b>"+typeReco+"</b>";

                viewHolder.setTitre(Html.fromHtml(sourceString));

                long currentTimestamp = System.currentTimeMillis();
                double searchTimestampD = recommandation.getTimestamp();
                long searchTimestamp = (long)searchTimestampD;
                long difference = Math.abs(currentTimestamp - searchTimestamp);
                if(TimeUnit.MILLISECONDS.toSeconds(currentTimestamp)==TimeUnit.MILLISECONDS.toSeconds(searchTimestamp)) {
                    viewHolder.setTime("À l'instant");
                }else {
                    viewHolder.setTime("Il y a " + convertTimeStampToHour(difference));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recosRef.child(idReco).child("urlPreview").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if (!dataSnapshot.getValue().toString().equals("") && viewHolder.playButton!=null) {
                        viewHolder.playButton.setVisibility(View.VISIBLE);
                        viewHolder.circle.setVisibility(View.VISIBLE);
                    }
                    else {
                        viewHolder.playButton = null;
                        viewHolder.playButton = null;
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        //INTREACTION AVEC LA RECOMMANDATION

        /*usersRef.child(mAuth.getCurrentUser().getUid()).child("bookmarks").addValueEventListener(new ValueEventListener() {
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
                    viewHolder.getBookButton().setImageResource(R.drawable.bookmark_ok);
                    CURRENT_BOOKMARK[i] = true;
                } else {
                    CURRENT_BOOKMARK[i] = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        /*recosRef.child(getRef(i).getKey()).child("likeUsersUid").addValueEventListener(new ValueEventListener() {
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
                    viewHolder.getLikeButton().setImageResource(R.drawable.red_heart);
                    CURRENT_LIKE[i] = true;
                } else {
                    viewHolder.getLikeButton().setImageResource(R.drawable.heart);
                    CURRENT_LIKE[i] = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/



        /*viewHolder.getLikeButton().setOnClickListener(new View.OnClickListener() {
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
                    viewHolder.getLikeButton().setImageResource(R.drawable.red_heart);
                    CURRENT_LIKE[i] =true;

                } else if(CURRENT_LIKE[i] == true){
                    recosRef.child(getRef(i).getKey()).child("likeUsersUid").child(keyLike[i]).removeValue();
                    viewHolder.getLikeButton().setImageResource(R.drawable.heart);
                    //follow.setText("Suivre");
                    CURRENT_LIKE[i] =false;

                }

            }

        });

        /*

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
                    viewHolder.getLikeButton().setImageResource(R.drawable.red_heart);
                    CURRENT_LIKE[i] =true;

                } else if(CURRENT_LIKE[i] == true){
                    recosRef.child(getRef(i).getKey()).child("likeUsersUid").child(keyLike[i]).removeValue();
                    viewHolder.getLikeButton().setImageResource(R.drawable.heart);
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

        viewHolder.getImg().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        });

        /*viewHolder.getBookButton().setOnClickListener(new View.OnClickListener() {
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
                    viewHolder.getBookButton().setImageResource(R.drawable.bookmark_ok);
                    CURRENT_BOOKMARK[i] =true;

                } else if(CURRENT_BOOKMARK[i] == true){
                    usersRef.child(mAuth.getCurrentUser().getUid()).child("bookmarks").child(keyBookmark[i]).removeValue();
                    viewHolder.getBookButton().setImageResource(R.drawable.bookmark);
                    //follow.setText("Suivre");
                    CURRENT_BOOKMARK[i] =false;

                }


            }
        });

        viewHolder.autreComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.putString("key", getRef(i).getKey());
                intent2.putExtras(b);
                startActivity(intent2);
            }
        });

        viewHolder.getListLike().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.putString("key", getRef(i).getKey());
                intent1.putExtras(b);
                startActivity(intent1);

            }
        });

        viewHolder.getCommentButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.putString("key", getRef(i).getKey());
                intent2.putExtras(b);
                startActivity(intent2);

            }
        });*/



        /*viewHolder.getImgProfil().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser().getUid().equals(model.getUserRecoUid())){
                    Fragment fragment = new ProfilFragment();
                    callBack.onProfilClicked();
                    loadFragement(fragment);

                } else {
                    b.putString("key", model.getUserRecoUid());
                    intent3.putExtras(b);
                    startActivity(intent3);
                }

            }
        });*/

        /*viewHolder.getDesc().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link ="";
                Log.e("testest",""+recommandation.getId());
                if (recommandation.getType().equals("track")){
                    link="https://www.deezer.com/fr/track/"+recommandation.getId();
                } else if (recommandation.getType().equals("album")){
                    link="https://www.deezer.com/fr/album/"+recommandation.getId();
                }else if (recommandation.getType().equals("artiste")){
                    link="https://www.deezer.com/fr/artist/"+recommandation.getId();
                } else{
                    link="https://www.imdb.com/title/"+recommandation.getId();
                }
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse(link));
                startActivity(viewIntent);
            }
        });*/


                /*Log.e("jhsvhx", viewHolder.getDesc().getText().toString().equals(nameLect.getText().toString())+"");

                if(viewHolder.getDesc().getText().toString().equals(nameLect.getText().toString())){
                    Log.e("jhsvhx", ""+viewHolder.getDesc().getText());
                    viewHolder.playButton.setVisibility(View.INVISIBLE);
                    viewHolder.player.setVisibility(View.VISIBLE);
                }else{
                    viewHolder.playButton.setVisibility(View.VISIBLE);
                    viewHolder.player.setVisibility(View.INVISIBLE);
                }*/

                /*viewHolder.playButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        viewHolder.playButton.startAnimation(buttonClick);
                        return true;
                    }
                });*/

        /*if (viewHolder.playButton!=null) {
            viewHolder.playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mp.seekTo(mp.getDuration());
                    mp.reset();
                    if (lecteur.getVisibility() == View.INVISIBLE) {
                        lecteur.setVisibility(View.VISIBLE);
                        ViewGroup.LayoutParams params = lecteur.getLayoutParams();
                        params.height = ActionBar.LayoutParams.WRAP_CONTENT;
                        lecteur.setLayoutParams(params);
                    }
                    try {
                        Log.d("testest", "" + model.getUrlPreview());
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
                        //viewHolder.playButton.setVisibility(View.INVISIBLE);
                        //viewHolder.player.setVisibility(View.VISIBLE);


                    Picasso.with(context).load(model.getUrlImage()).fit().centerInside().into(musicImg);
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

                    viewHolder.playButton.startAnimation(buttonClick);

                    stop.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            mp.stop();
                            mp.reset();
                            lecteur.setVisibility(View.INVISIBLE);



                                //viewHolder.playButton.setVisibility(View.VISIBLE);
                                //viewHolder.playButton.setImageResource(R.drawable.ic_play);
                                //viewHolder.player.setVisibility(View.INVISIBLE);

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
                                    //viewHolder.playButton.setVisibility(View.VISIBLE);
                                    //viewHolder.playButton.setImageResource(R.drawable.ic_pause);
                                    //viewHolder.player.setVisibility(View.INVISIBLE);

                            } else {
                                btnPause.setImageResource(R.drawable.ic_pause);
                                    //viewHolder.playButton.setVisibility(View.INVISIBLE);
                                    //viewHolder.player.setVisibility(View.VISIBLE);
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
        }*/

    }

    @Override
    public int getItemCount() {
        return mRecommandation.size();
    }

    public void clear() {
        mRecommandation.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Recommandation> list) {
        mRecommandation.addAll(list);
        notifyDataSetChanged();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nbrlike;
        TextView nbrCom;

        TextView pseudoCom;
        TextView autreComment;

        ImageButton commentButton;
        ImageButton bookmarkButton;
        ImageButton likeButton;

        ImageButton pictureRecommandation;

        TextView timeRecommandation;
        TextView descRecommandation;
        TextView nomRecommandation;
        CircleImageView pictureUserRecommandation;

        ImageView playButton;
        ImageView circle;

        final LinearLayout layout;
        final LinearLayout.LayoutParams params;

        public ViewHolder(View itemView) {
            super(itemView);

            nbrlike = (TextView) itemView.findViewById(R.id.nbrLike);
            nbrCom = (TextView) itemView.findViewById(R.id.nbrComment);

            pseudoCom = itemView.findViewById(R.id.pseudoComment);
            autreComment = itemView.findViewById(R.id.autreComment);

            commentButton = (ImageButton) itemView.findViewById(R.id.commentButton);
            bookmarkButton = (ImageButton) itemView.findViewById(R.id.bookButton);
            likeButton = (ImageButton) itemView.findViewById(R.id.likeButton);


            pictureRecommandation = (ImageButton) itemView.findViewById(R.id.img_ar);

            timeRecommandation = (TextView) itemView.findViewById(R.id.time);
            descRecommandation = (TextView) itemView.findViewById(R.id.desc);
            nomRecommandation = (TextView) itemView.findViewById(R.id.name);
            pictureUserRecommandation = (CircleImageView) itemView.findViewById(R.id.imgProfil);

            playButton = itemView.findViewById(R.id.playButton);
            circle = itemView.findViewById(R.id.circle);


            playButton.setVisibility(View.INVISIBLE);
            circle.setVisibility(View.INVISIBLE);
            layout =(LinearLayout)itemView.findViewById(R.id.linearLayoutReco);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        public void setAutreComment(String autreComment1){
            autreComment.setText(autreComment1);
        }

        public void setTime(String timeText){
            TextView time = (TextView) itemView.findViewById(R.id.time);
            time.setText(timeText);
        }

        public void setPseudoCom(String pseudo){
            pseudoCom.setText(pseudo);
        }

        public void setDesc(Spanned desc){
            descRecommandation.setText(desc);
        }

        public TextView getDesc(){
            return descRecommandation;
        }

        public void setTitre(Spanned text){
            TextView nameR = (TextView) itemView.findViewById(R.id.name);
            nameR.setText(text);
        }

        public ImageButton getImg() {
            ImageButton imgR = (ImageButton) itemView.findViewById(R.id.img_ar);
            return imgR;
        }

        public CircleImageView getImgProfil(){
            CircleImageView imgProfil = (CircleImageView) itemView.findViewById(R.id.imgProfil);
            return imgProfil;
        }

        public ImageButton getLikeButton(){
            ImageButton imgButton = (ImageButton) itemView.findViewById(R.id.likeButton);
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
            TextView nbrLikeBut = (TextView) itemView.findViewById(R.id.nbrLike);
            return  nbrLikeBut;
        }

        public ImageButton getCommentButton(){
            ImageButton button = (ImageButton) itemView.findViewById(R.id.commentButton);
            return button;
        }

        public ImageButton getBookButton(){
            ImageButton img = (ImageButton) itemView.findViewById(R.id.bookButton);
            return img;
        }

        public void layout_hide() {
            layout.setVisibility(View.GONE);

        }

    }

    private String convertTimeStampToHour(long millis) {
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
}
