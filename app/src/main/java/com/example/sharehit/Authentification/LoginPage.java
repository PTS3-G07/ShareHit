package com.example.sharehit.Authentification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharehit.PageFixe.FeedPage;
import com.example.sharehit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {

    private EditText emailLog, passLog;
    private Button loginButt;
    private FirebaseAuth mAuth;
    private TextView textView5;
    private TextView textView2;


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(LoginPage.this, FeedPage.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        ActionBar actionBar = getSupportActionBar();
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Chargement en cours");
        actionBar.hide();

        mAuth = FirebaseAuth.getInstance();
        emailLog = (EditText) findViewById(R.id.emailLog);
        passLog = (EditText) findViewById(R.id.passLog);
        loginButt = (Button) findViewById(R.id.loginButt);
        textView5 = (TextView) findViewById(R.id.textView5);
        textView2 = (TextView) findViewById(R.id.textView2);

        textView5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(LoginPage.this, SignUp.class));
            }
        });

        textView2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(LoginPage.this, PasswordForget.class));
            }
        });

        loginButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailLog.getText().toString();
                final String password = passLog.getText().toString();


                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailLog.setError("Invalid email");
                    emailLog.setFocusable(true);
                }else if(password.length()<6){
                    passLog.setError("Password lenght at least 6 charachters");
                    passLog.setFocusable(true);
                }else {
                    progress.show();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginPage.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        startActivity(new Intent(LoginPage.this, FeedPage.class));
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        progress.dismiss();
                                        finish();
                                    }else {
                                        Toast.makeText(LoginPage.this, "Authentication failed." + task.getException(), Toast.LENGTH_LONG).show();
                                          progress.dismiss();

                                    }
                                }
                            });
                }


                    }




        });
    }
}
