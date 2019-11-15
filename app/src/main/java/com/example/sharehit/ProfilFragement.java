package com.example.sharehit;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ContentResolver;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharehit.Model.Recommendation;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.core.provider.FontsContractCompat.FontRequestCallback.RESULT_OK;


public class ProfilFragement extends Fragment {


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
    ImageButton upload;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int PICK_IMAGE_REQUEST = 111;
    String cameraPermissions[];
    String storagePermissions[];
    public boolean CURRENT_LIKE;
    private RecyclerView post;
    private final static MediaPlayer mp = new MediaPlayer();



    public boolean OnCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.singout_menu, menu);
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragement_profil, null);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference().child("users");
        myRef = database.getReference("users");
        pdp=  root.findViewById(R.id.pdp);
        pseudo= root.findViewById(R.id.pseudo);
        upload= root.findViewById(R.id.upload);
        fb = root.findViewById(R.id.fb);
        final String userUID = firebaseAuth.getCurrentUser().getUid();
        recosRef = FirebaseDatabase.getInstance().getReference().child("recos");

        post = (RecyclerView) root.findViewById(R.id.postIudRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        post.setLayoutManager(layoutManager);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        mStorageRef = FirebaseStorage.getInstance().getReference();
        pd = new ProgressDialog(getActivity());
        mStorageRef = FirebaseStorage.getInstance().getReference();

        usersRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pseudo.setText(dataSnapshot.child("pseudo").getValue().toString());
                if (dataSnapshot.child("pdpUrl").exists()) Picasso.with(getContext()).load(dataSnapshot.child("pdpUrl").getValue().toString()).fit().centerInside().into(pdp);

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
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(), LoginPage.class));


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

        String options[] = {"Edit profile picture", "Edit pseudo"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Chose action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    pd.setMessage("Updating profile picture");
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }else {
                        pickFromGallery();
                    }
                }else if (which == 1){
                    pd.setMessage("Edit pseudo");
                    showNameUpdateDialog("name");
                }
            }
        });
        builder.create().show();

    }

    private void showNameUpdateDialog(String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Changer de pseudo");
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setPadding(10,10,10,10);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        final EditText editText = new EditText(getActivity());
        editText.setHint("Pseudo...");
        linearLayout.addView(editText);
        builder.setView(linearLayout);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
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
                  editText.setError("Enter pseudo");
              }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
                        Toast.makeText(getActivity(), "Please enable storage permission", Toast.LENGTH_SHORT);

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
            final StorageReference filepath = mStorageRef.child("Pdp").child(user.getUid());
            filepath.putFile(imguri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    //Picasso.with(getContext()).load(imguri).resize(180, 180).into(pdp);
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            HashMap usersMap = new HashMap();
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                            usersMap.put("pdpUrl", String.valueOf(uri));
                            usersRef.updateChildren(usersMap);
                        }
                    });

                }

            });

        }


    }

    private void displayAllPostUid() {

        final Intent intent1 = new Intent(getContext(), ListLikePage.class);
        final Intent intent2 = new Intent(getContext(), CommentPage.class);
        final Bundle b = new Bundle();

        Query myPost = recosRef.orderByChild("userRecoUid").startAt(user.getUid()).endAt(user.getUid()+"\uf8ff");

        FirebaseRecyclerAdapter<Recommendation, FeedFragement.RecosViewHolder> fireBaseRecyclerAdapter = new FirebaseRecyclerAdapter<Recommendation, FeedFragement.RecosViewHolder>
                (
                        Recommendation.class,
                        R.layout.recommandation_item,
                        FeedFragement.RecosViewHolder.class,
                        myPost
                ) {

            @Override
            protected void populateViewHolder(final FeedFragement.RecosViewHolder recosViewHolder, final Recommendation model, final int i) {

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


                usersRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String pseudo = dataSnapshot.child("pseudo").getValue().toString();
                        recosViewHolder.setTitre(pseudo + " a recommandé " + model.getType());
                        if(dataSnapshot.child("pdpUrl").exists()){
                            Picasso.with(getContext()).load(dataSnapshot.child("pdpUrl").getValue().toString()).fit().centerInside().into(recosViewHolder.getImgProfil());
                        }
                        /*DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date dat








                        e_mini = dateFormat.format(timestamp.getTime());*/

                        Date date=new Date(model.getTimeStamp());
                        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"+" à "+"H"+":"+"mm");
                        recosViewHolder.setTime("Le "+dateFormat.format(date));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });





            }
        };
        post.setAdapter(fireBaseRecyclerAdapter);

    }
}






