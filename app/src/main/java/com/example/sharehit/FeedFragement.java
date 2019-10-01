package com.example.sharehit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharehit.Adapter.AdapterRecs;
import com.example.sharehit.Model.Recommendation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeedFragement extends Fragment {

    FirebaseAuth firebaseAuth;
    Button gotoSearch, deezer;
    RecyclerView recyclerView;
    List<Recommendation> recList;
    AdapterRecs adapterRecs;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragement_feed, null);


        deezer = root.findViewById(R.id.deezer);
        deezer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), DezerApi.class));
            }
        });



        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = root.findViewById(R.id.postRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        recList = new ArrayList<Recommendation>();

        loadPosts();

        return root;
    }

    private void loadPosts() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Recommendations");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Recommendation recommendation = ds.getValue(Recommendation.class);
                    recList.add(recommendation);
                    adapterRecs = new AdapterRecs(getActivity(), recList);
                    recyclerView.setAdapter(adapterRecs);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT);
            }
        });

    }


}
