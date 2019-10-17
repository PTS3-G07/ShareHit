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

import com.example.sharehit.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {


    private EditText email_id, pass_id, pseudo_y;
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
                final String pseudo = pseudo_y.getText().toString();
                final String password = pass_id.getText().toString();

               if (pseudo.isEmpty()){
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
                                                            User user = new User(
                                                                    pseudo,
                                                                    email
                                                            );
                                                            String userUID = mAuth.getCurrentUser().getUid();
                                                            HashMap usersMap = new HashMap();
                                                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                                                            usersMap.put(userUID, user);
                                                            usersRef.updateChildren(usersMap);
                                                            usersRef.child(userUID).child("pdpUrl").setValue("https://firebasestorage.googleapis.com/v0/b/share-hit-52071.appspot.com/o/Pdp%2Fman.png?alt=media&token=c8267070-0673-406b-a564-e9642afb6666");
                                                            progress.dismiss();
                                                            startActivity(new Intent(SignUp.this, FeedPage.class));
                                                            finish();
                                                        }else {
                                                            Toast.makeText(SignUp.this, "Authentication failed." + task.getException(), Toast.LENGTH_LONG).show();
                                                            progress.dismiss();

                                                        }
                                                    }
                                                });
                                    }else {
                                        progress.dismiss();
                                        Toast.makeText(SignUp.this, "Authentication failed." + task.getException(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }



            }


        });


    }


}
