package com.example.sharehit.PageFixe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharehit.Model.Comment;
import com.example.sharehit.R;
import com.example.sharehit.Utilities.DividerItemDecorator;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentPage extends AppCompatActivity {

    private TextView sendButton;
    private EditText sendText;

    private DatabaseReference comRef, usersRef;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private StorageReference mStorageRef;

    private RecyclerView commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_page);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        commentList = (RecyclerView) findViewById(R.id.commentRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(false);
        commentList.setLayoutManager(layoutManager);



        sendButton = (TextView) findViewById(R.id.sendComment);
        sendText = (EditText) findViewById(R.id.textComment);

        Bundle b = getIntent().getExtras();

        sendButton.setEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        comRef = FirebaseDatabase.getInstance().getReference().child("recos").child(b.getString("key")).child("Coms");
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        sendText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0 && !s.toString().trim().equals("")){
                    sendButton.setEnabled(true);
                    sendButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    sendButton.setEnabled(false);
                    sendButton.setTextColor(getResources().getColor(R.color.grisStyle));
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(sendText.getText().toString())){
                    Comment comment = new Comment(
                            sendText.getText().toString(),
                            currentTimeSecsUTC(),
                            mAuth.getCurrentUser().getUid()
                    );
                    String key = comRef.push().getKey();
                    HashMap usersMap = new HashMap();
                    usersMap.put(key, comment);
                    comRef.updateChildren(usersMap);
                    sendText.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "Le message est vide", Toast.LENGTH_LONG).show();
                }
            }
        });
        
        displayAllComment();




    }

    public static long currentTimeSecsUTC() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                .getTimeInMillis() / 1000;
    }

    private void displayAllComment() {
        final Intent intent2 = new Intent(getApplicationContext(), FeedPage.class);
        final Intent intent3 = new Intent(getApplicationContext(), ProfilPage.class);
        final Bundle b = new Bundle();
        FirebaseRecyclerAdapter<Comment, CommentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>
                (
                        Comment.class,
                        R.layout.comment_display,
                        CommentViewHolder.class,
                        comRef
                ) {
            @Override
            protected void populateViewHolder(final CommentViewHolder commentViewHolder, final Comment comment, int i) {
                commentViewHolder.setMessage(comment.getCom());

                Calendar cl = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                cl.setTimeInMillis(comment.getTimestamp() * 1000);
                DateFormat dateFormat = new SimpleDateFormat("dd"+"/"+"MM"+"/"+"yyyy"+" à "+"H"+":"+"mm");
                commentViewHolder.setTime("Le "+dateFormat.format(cl.getTime()));

                final StorageReference filepath = mStorageRef;

                filepath.child(comment.getUid()).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        Picasso.with(getApplicationContext()).load("https://firebasestorage.googleapis.com/v0/b/share-hit.appspot.com/o/"+comment.getUid()+"?alt=media").fit().centerInside().into(commentViewHolder.getImgProfilComment());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        commentViewHolder.getImgProfilComment().setImageResource(R.drawable.ic_baby);
                    }
                });

                usersRef.child(comment.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            commentViewHolder.setPseudoComment(dataSnapshot.child("pseudo").getValue().toString());
                        } else {
                            commentViewHolder.setPseudoComment("Compte supprimé");
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                commentViewHolder.getImgProfilComment().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("UserID", comment.getUid());
                        if(mAuth.getCurrentUser().getUid().equals(comment.getUid())){
                            b.putInt("key", 1);
                            intent2.putExtras(b);
                            startActivity(intent2);
                        } else {
                            b.putString("key", comment.getUid());
                            intent3.putExtras(b);
                            startActivity(intent3);
                        }
                    }
                });

            }
        };
        commentList.setAdapter(firebaseRecyclerAdapter);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(getApplicationContext(), R.drawable.recycler_view_divider));
        commentList.addItemDecoration(dividerItemDecoration);

    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mView = itemView;
        }

        public void setMessage(String name){
            TextView tx = (TextView) mView.findViewById(R.id.comment);
            tx.setText(name);
        }

        public CircleImageView getImgProfilComment(){
            CircleImageView img = (CircleImageView) mView.findViewById(R.id.imageProfilComment);
            return img;
        }

        public void setTime(String time){
            TextView tx = (TextView) mView.findViewById(R.id.timeStamp);
            tx.setText(time);
        }

        public void setPseudoComment(String name){
            TextView tx = (TextView) mView.findViewById(R.id.pseudoComment);
            tx.setText(name);
        }
    }
}
