package com.example.sharehit;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharehit.Model.Recommandation;
import com.example.sharehit.Utilities.OnSwipeTouchListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class ProfilFragment extends Fragment {


    FirebaseAuth firebaseAuth, mAuth;
    FirebaseUser user;
    DatabaseReference myRef, usersRef, recosRef;
    FirebaseDatabase database;
    private StorageReference mStorageRef;
    public Uri imguri;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    ImageView pdp;
    TextView pseudo;
    FloatingActionButton fb;
    ProgressDialog pd;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int PICK_IMAGE_REQUEST = 111;
    String cameraPermissions[];
    String storagePermissions[];
    public boolean CURRENT_LIKE;
    private RecyclerView post;
    private final static MediaPlayer mp = new MediaPlayer();

    private MyListenerProfil callBack;
    private ProgressBar mSeekBarPlayer;
    private ImageButton stop;
    private ImageButton btnPause;
    private LinearLayout lecteur;
    private TextView nameLect;
    private ImageView musicImg;

    private Animation buttonClick;


    public boolean OnCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.singout_menu, menu);
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        mp.pause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragement_profil, null);

        callBack=(MyListenerProfil)getActivity();

        root.setOnTouchListener(new OnSwipeTouchListener(getContext()) {

            public void onSwipeRight() {
                callBack.onSwipeRightProfil();
            }

        });

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference().child("users");
        myRef = database.getReference("users");
        pdp=  root.findViewById(R.id.pdp);
        pseudo= root.findViewById(R.id.pseudo);
        fb = root.findViewById(R.id.fb);
        final String userUID = firebaseAuth.getCurrentUser().getUid();
        recosRef = FirebaseDatabase.getInstance().getReference().child("recos");

        post = (RecyclerView) root.findViewById(R.id.postIudRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        post.setLayoutManager(layoutManager);

        buttonClick = AnimationUtils.loadAnimation(getContext(), R.anim.click);

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

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        mStorageRef = FirebaseStorage.getInstance().getReference();
        pd = new ProgressDialog(getActivity());
        mStorageRef = FirebaseStorage.getInstance().getReference();

        Picasso.with(getContext()).load("https://firebasestorage.googleapis.com/v0/b/share-hit.appspot.com/o/"+mAuth.getCurrentUser().getUid()+"?alt=media&token=1d93f69f-a530-455a-83d2-929ce42c3667").into(pdp);


        usersRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pseudo.setText(dataSnapshot.child("pseudo").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String options[] = {"Changer de photo de profil","Changer de pseudo", "Se déconnecter", };
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Paramètres");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            pd.setMessage("Changer de photo de profil");
                            if(!checkStoragePermission()){
                                requestStoragePermission();
                            }else {
                                pickFromGallery();
                            }
                        }
                        else if (which==1){
                            pd.setMessage("Changer de pseudo");
                            showNameUpdateDialog();
                        } else if (which == 2){
                            pd.setMessage("Se déconnecter");
                            firebaseAuth.signOut();
                            startActivity(new Intent(getActivity(), LoginPage.class));
                        }
                    }
                });
                builder.create().show();




                /*

                Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(), "AIzaSyBYORHRvvvzDdhukXCf24orxzFXhoIURr8", "aJ7BoNG-r2c", 0, true, false);
                startActivity(intent);

                 */


            }
        });

        displayAllPostUid();


        return root;
    }






    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return  result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(getActivity(), storagePermissions, STORAGE_REQUEST_CODE);

    }

    private boolean checkCameraPermission(){

        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return  result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(getActivity(), cameraPermissions, CAMERA_REQUEST_CODE);

    }

    private void showEditProfileDialog() {

        String options[] = {"Changer de photo de profil", "Changer de pseudo"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edition du profil");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    pd.setMessage("Changement de la photo de profil");
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }else {
                        pickFromGallery();
                    }
                }else if (which == 1){
                    pd.setMessage("Changement de pseudo");
                    showNameUpdateDialog();
                }
            }
        });
        builder.create().show();

    }

    private void showNameUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Changer de pseudo");
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setPadding(10,10,10,10);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        final EditText editText = new EditText(getActivity());
        editText.setHint("Nouveau pseudo...");
        linearLayout.addView(editText);
        builder.setView(linearLayout);
        builder.setPositiveButton("Changer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              String value = editText.getText().toString().trim();
              if(!TextUtils.isEmpty(value)){
                 //  pd.show();
                 HashMap reslt = new HashMap();
                  reslt.put("pseudo", value);
                  DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                  usersRef.updateChildren(reslt);
              }else {
                  editText.setError("Entrer un pseudo");
              }
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case STORAGE_REQUEST_CODE:{


                if(grantResults.length >0){
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        pickFromGallery();
                    }else {
                        Toast.makeText(getActivity(), "Veuillez autoriser l'accès à la galerie", Toast.LENGTH_SHORT);

                    }
                }

            }


        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    private void pickFromGallery() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imguri = data.getData();
            /*File compressedImageFile = new File(imguri.getPath());
            try {
                OutputStream os = new FileOutputStream(compressedImageFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }*/
            final StorageReference filepath = mStorageRef.child(user.getUid());
            filepath.putFile(imguri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    //Picasso.with(getContext()).load(imguri).resize(180, 180).into(pdp);
                    /*
                    filepath.getDownloadUrl().addOnSuccessListener(    new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            HashMap usersMap = new HashMap();
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                            usersMap.put("pdpUrl", String.valueOf(uri));
                            usersRef.updateChildren(usersMap);
                        }
                    });

                     */
                    Toast.makeText(getContext(),"Photo de profil changé", Toast.LENGTH_LONG).show();
                    Picasso.with(getContext()).load("https://firebasestorage.googleapis.com/v0/b/share-hit.appspot.com/o/"+mAuth.getCurrentUser().getUid()+"?alt=media&token=1d93f69f-a530-455a-83d2-929ce42c3667").networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).fit().centerInside().into(pdp);


                }

            });

        }


    }


    private void displayAllPostUid() {

        final Intent intent1 = new Intent(getContext(), ListLikePage.class);
        final Intent intent2 = new Intent(getContext(), CommentPage.class);
        final Bundle b = new Bundle();

        Query myPost = recosRef.orderByChild("userRecoUid").startAt(user.getUid()).endAt(user.getUid()+"\uf8ff");

        final int[] tailleTableau = new int[1];
        myPost.addValueEventListener(new ValueEventListener() {
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
                        myPost
                ) {

            @Override
            protected void populateViewHolder(final FeedFragment.RecosViewHolder recosViewHolder, final Recommandation model, final int i) {
                final String[] keyBookmark = new String[tailleTableau[0]];
                final boolean[] CURRENT_BOOKMARK = new boolean[tailleTableau[0]];
                final String[] keyLike = new String[tailleTableau[0]];
                final boolean[] CURRENT_LIKE = new boolean[tailleTableau[0]];


                Picasso.with(getContext()).load(model.getUrlImage()).fit().centerInside().into(recosViewHolder.getImg());

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


                //BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
                Picasso.with(getContext()).load("https://firebasestorage.googleapis.com/v0/b/share-hit-52071.appspot.com/o/Pdp%2F" + model.getUserRecoUid() + "?alt=media&token=32f03c76-31a8-4ea2-8cac-8fa92bef6667").fit().centerInside().into(recosViewHolder.getImgProfil());

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
                        double searchTimestampD = model.getTimestamp();
                        long searchTimestamp = (long)searchTimestampD;
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

    private boolean loadFragement(Fragment fragment){
        if(fragment != null){
            getFragmentManager().beginTransaction().replace(R.id.container, fragment)
                    .commit();

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
    public interface MyListenerProfil{
        public void onSwipeRightProfil();
    }

}






