package com.example.sharehit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {


    private EditText email_id, pass_id, nom_y, pren_y, pseudo_y;
    private Button regButt;
    private FirebaseAuth mAuth;
    DatabaseReference myRef;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Wait while loading...");
        actionBar.hide();
        email_id = (EditText) findViewById(R.id.email_y);
        pass_id = (EditText) findViewById(R.id.pass_y);
        regButt = (Button) findViewById(R.id.regButt);
        pseudo_y = (EditText) findViewById(R.id.pseudo);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();

        regButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = email_id.getText().toString();
                final String nom = nom_y.getText().toString();
                final String prenom = pren_y.getText().toString();
                final String pseudo = pseudo_y.getText().toString();
                final String password = pass_id.getText().toString();

                if(nom.isEmpty()) {
                    nom_y.setError("Should not be empty");
                    nom_y.setFocusable(true);
                }else if (prenom.isEmpty()){
                    pren_y.setError("Should not be empty");
                    pren_y.setFocusable(true);
                }else if (pseudo.isEmpty()){
                    pseudo_y.setError("Should not be empty");
                    pseudo_y.setFocusable(true);
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    email_id.setError("Invalid email");
                    email_id.setFocusable(true);
                }else if(password.length()<6){
                    pass_id.setError("Should not be empty");
                    pass_id.setFocusable(true);
                } else {
                    progress.show();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        mAuth.signInWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if(task.isSuccessful()){

                                                            startActivity(new Intent(SignUp.this, FeedPage.class));
                                                            FirebaseUser user = mAuth.getCurrentUser();
                                                            progress.dismiss();
                                                            finish();
                                                        }else {
                                                            Toast.makeText(SignUp.this, "Authentication failed." + task.getException(), Toast.LENGTH_LONG).show();
                                                            progress.dismiss();

                                                        }
                                                    }
                                                });
                                    }else {

                                    }
                                }
                            });
                }



            }


        });


    }


}
