package com.example.sharehit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordForget extends AppCompatActivity {

    private EditText emailLog;
    private FirebaseAuth mAuth;
    private Button sendButt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_forget_page);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        emailLog = (EditText) findViewById(R.id.emailLog);
        sendButt = (Button) findViewById(R.id.sendButt);
        mAuth = FirebaseAuth.getInstance();

        sendButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailLog.getText().toString();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailLog.setError("Invalid email");
                    emailLog.setFocusable(true);
                } else {
                    mAuth.sendPasswordResetEmail(emailLog.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PasswordForget.this, "Email envoyé", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(PasswordForget.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });


    }
}
