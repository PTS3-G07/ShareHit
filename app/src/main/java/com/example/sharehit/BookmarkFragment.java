package com.example.sharehit;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sharehit.Adapter.BookmarkAdapter;
import com.example.sharehit.Model.Bookmark;
import com.example.sharehit.Model.Recommandation;
import com.example.sharehit.Utilities.OnSwipeTouchListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private FloatingActionButton typeListBookmark;

    private boolean tri;
    private int idx;

    public BookmarkAdapter adapter;
    private SwipeRefreshLayout swipeContainer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragement_notif, null);
        swipeContainer = (SwipeRefreshLayout) root.findViewById(R.id.swipeContainer);
        recyclerview = (RecyclerView) root.findViewById(R.id.postBookmarkRecyclerView);
        typeListBookmark = (FloatingActionButton) root.findViewById(R.id.typeBookmarkList);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                if(!tri){
                    chargerRecyclerView(chargerListBookmark());
                } else {
                    chargerRecyclerView(trierList(chargerListBookmark(), idx));
                }
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


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


        typeListBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String options[] = {"Artiste","Album","Morceau","Serie","Film","Jeux vidéos", "Aucun tri"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Trier par :");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            idx = 0;
                            chargerRecyclerView(trierList(chargerListBookmark(), 0));
                        } else if (which==1){
                            idx = 1;
                            chargerRecyclerView(trierList(chargerListBookmark(), 1));
                        } else if (which == 2){
                            Log.e("Test", chargerListBookmark().size() + "");
                            idx = 2;
                            chargerRecyclerView(trierList(chargerListBookmark(), 2));
                        } else if (which == 3){
                            idx = 3;
                            chargerRecyclerView(trierList(chargerListBookmark(), 3));
                        } else if (which == 4){
                            idx = 4;
                            chargerRecyclerView(trierList(chargerListBookmark(), 4));
                        } else if (which == 5){
                            idx = 5;
                            chargerRecyclerView(trierList(chargerListBookmark(), 5));
                        } else if (which == 6){
                            Log.e("Test", chargerListBookmark().size() + "");
                            chargerRecyclerView(chargerListBookmark());
                            tri = false;
                        }
                    }
                });
                builder.create().show();
            }
        });

        chargerRecyclerView(chargerListBookmark());


        return root;
    }




    public interface MyListenerBookmark{
        public void onSwipeLeftBookmark();
        public void onSwipeRightBookmark();
    }

    public List<Bookmark> trierList(List<Bookmark> list, int i){
        tri = true;
        final List<Bookmark> listBookmark = new ArrayList<>();
        final String type;
        Log.e("Test", "test");
        switch (i){
            case 0:
                type = "artiste";
                break;
            case 1:
                type = "album";
                break;
            case 2:
                Log.e("Test", "track");
                type = "track";
                break;
            case 3:
                type = "serie";
                break;
            case 4:
                type = "movie";
                break;
            case 5:
                type = "game";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + i);
        }

        Log.e("Test", list.size() + "");
        for(Bookmark bookmark : list){
            Log.e("BookType", bookmark.getType());
            Log.e("Test", "boucle1");
            if(bookmark.getType()== type){
                Log.e("Test", "boucle2");
                listBookmark.add(bookmark);
            }
        }

        return listBookmark;

    }

    public final List<Bookmark> chargerListBookmark(){
        final List<Bookmark> listBookmark = new ArrayList<>();
        bookRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot child : dataSnapshot.getChildren()){
                    recosRef.child(child.getValue().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Bookmark bookmark = new Bookmark(child.getKey(), child.getValue().toString(), dataSnapshot.child("type").getValue().toString(), dataSnapshot.child("urlImage").getValue().toString(), dataSnapshot.child("track").getValue().toString(), dataSnapshot.child("artist").getValue().toString());
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

        return listBookmark;
    }

    public void chargerRecyclerView(List<Bookmark> list){
        Log.e("Test", list.size() + " //");
        adapter = new BookmarkAdapter(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerview.setLayoutManager(layoutManager);
        adapter.notifyDataSetChanged();
        recyclerview.setAdapter(adapter);
        recyclerview.setVisibility(View.VISIBLE);
    }
}
