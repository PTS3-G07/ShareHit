package com.example.sharehit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sharehit.Model.Comment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentPage extends AppCompatActivity {

    private ImageButton sendButton;
    private EditText sendText;

    private DatabaseReference comRef, usersRef;
    private FirebaseAuth mAuth;
    private String current_user_id;

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



        sendButton = (ImageButton) findViewById(R.id.sendComment);
        sendText = (EditText) findViewById(R.id.textComment);

        Bundle b = getIntent().getExtras();

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        comRef = FirebaseDatabase.getInstance().getReference().child("recos").child(b.getString("key")).child("Coms");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sendText.equals("")){
                    Comment comment = new Comment(
                            sendText.getText().toString(),
                            new Timestamp(System.currentTimeMillis()).getTime(),
                            mAuth.getCurrentUser().getUid()
                    );
                    String key = comRef.push().getKey();
                    HashMap usersMap = new HashMap();
                    usersMap.put(key, comment);
                    comRef.updateChildren(usersMap);
                    sendText.setText("");
                }
            }
        });
        
        displayAllComment();


    }

    private void displayAllComment() {
        FirebaseRecyclerAdapter<Comment, CommentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>
                (
                        Comment.class,
                        R.layout.comment_display,
                        CommentViewHolder.class,
                        comRef
                ) {
            @Override
            protected void populateViewHolder(final CommentViewHolder commentViewHolder, Comment comment, int i) {
                commentViewHolder.setMessage(comment.getCom());

                Date date=new Date(comment.getTimestamp());
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"+" à "+"H-mm");
                commentViewHolder.setTime("Le "+dateFormat.format(date));

                usersRef.child(comment.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.child("pdpUrl").exists()){
                            Picasso.with(getApplicationContext()).load(dataSnapshot.child("pdpUrl").getValue().toString()).fit().centerInside().into(commentViewHolder.getImgProfilComment());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        };
        commentList.setAdapter(firebaseRecyclerAdapter);

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
    }
}
