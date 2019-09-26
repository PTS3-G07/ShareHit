package com.example.sharehit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Uploadpdp extends AppCompatActivity {

    Button upload;
    ImageView img;
    public Uri imguri;
    private StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadpdp);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        upload = findViewById(R.id.upload);
        img = findViewById(R.id.img);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, 1);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imguri = data.getData();
            img.setImageURI(imguri);
            StorageReference filepath = mStorageRef.child("Pdp").child(imguri.getLastPathSegment());
            filepath.putFile(imguri);
        }
    }
}
