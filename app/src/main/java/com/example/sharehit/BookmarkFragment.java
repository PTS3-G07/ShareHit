package com.example.sharehit;

import android.app.ActionBar;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharehit.Model.Bookmark;
import com.example.sharehit.Model.Recommandation;
import com.example.sharehit.Utilities.OnSwipeTouchListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BookmarkFragment extends Fragment {

    private DatabaseReference recosRef, bookRef, usersRef;
    private FirebaseAuth mAuth;
    private MyListenerBookmark callBack;

    private RecyclerView recyclerview;

    private List<Bookmark> listBookmark;
    private Bookmark bookmark;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragement_notif, null);


        callBack = (MyListenerBookmark) getActivity();

        root.setOnTouchListener(new OnSwipeTouchListener(getContext()) {

            public void onSwipeLeft() {
                callBack.onSwipeLeftBookmark();
            }
            public void onSwipeRight() {
                callBack.onSwipeRightBookmark();
            }

        });


        mAuth = FirebaseAuth.getInstance();
        recosRef = FirebaseDatabase.getInstance().getReference().child("recos");
        bookRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("bookmarks");
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");




        recyclerview = (RecyclerView) root.findViewById(R.id.postBookmarkRecyclerView);

        listBookmark = new ArrayList<>();



        bookRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot child : dataSnapshot.getChildren()){
                    recosRef.child(child.getValue().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.e("keyBookmark", child.getKey());
                            Log.e("keyReco", child.getValue().toString());
                            bookmark = new Bookmark(child.getKey(), child.getValue().toString(), dataSnapshot.child("type").getValue().toString(), dataSnapshot.child("urlImage").getValue().toString(), dataSnapshot.child("track").getValue().toString(), dataSnapshot.child("artist").getValue().toString());
                            listBookmark.add(bookmark);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return root;
    }




    public interface MyListenerBookmark{
        public void onSwipeLeftBookmark();
        public void onSwipeRightBookmark();
    }
}
