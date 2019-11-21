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
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import com.example.sharehit.Model.Recommandation;
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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
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

        Picasso.with(getContext()).load("https://firebasestorage.googleapis.com/v0/b/share-hit-52071.appspot.com/o/Pdp%2F"+mAuth.getCurrentUser().getUid()+"?alt=media&token=32f03c76-31a8-4ea2-8cac-8fa92bef6667").fit().centerInside().into(pdp);


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
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String options[] = {"Un truc", "Se déconnecter"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Paramètres");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            pd.setMessage("Un truc");
                        }else if (which == 1){
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
            final StorageReference filepath = mStorageRef.child("Pdp").child(user.getUid());
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
                    Picasso.with(getContext()).load("https://firebasestorage.googleapis.com/v0/b/share-hit-52071.appspot.com/o/Pdp%2F"+mAuth.getCurrentUser().getUid()+"?alt=media&token=32f03c76-31a8-4ea2-8cac-8fa92bef6667").networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).centerInside().into(pdp);


                }

            });

        }


    }


    private void displayAllPostUid() {

        final Intent intent1 = new Intent(getContext(), ListLikePage.class);
        final Intent intent2 = new Intent(getContext(), CommentPage.class);
        final Bundle b = new Bundle();

        Query myPost = recosRef.orderByChild("userRecoUid").startAt(user.getUid()).endAt(user.getUid()+"\uf8ff");

        FirebaseRecyclerAdapter<Recommandation, FeedFragement.RecosViewHolder> fireBaseRecyclerAdapter = new FirebaseRecyclerAdapter<Recommandation, FeedFragement.RecosViewHolder>
                (
                        Recommandation.class,
                        R.layout.recommandation_item,
                        FeedFragement.RecosViewHolder.class,
                        myPost
                ) {

            @Override
            protected void populateViewHolder(final FeedFragement.RecosViewHolder recosViewHolder, final Recommandation model, final int i) {







            }
        };
        post.setAdapter(fireBaseRecyclerAdapter);

    }

}






