package com.example.sharehit.PageFixe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import com.example.sharehit.Adapter.RecommandationAdapter;
import com.example.sharehit.Adapter.SearchUserAdapter;
import com.example.sharehit.Model.Recommandation;
import com.example.sharehit.Model.User;
import com.example.sharehit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class SearchProfilPage extends AppCompatActivity {

    RecyclerView recyclerViewSearchProfil;
    SearchView searchProfilBar;

    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;

    private SearchUserAdapter searchUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_profil_page);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        recyclerViewSearchProfil = (RecyclerView) findViewById(R.id.recyclerViewSearchProfil);
        searchProfilBar = (SearchView) findViewById(R.id.searchProfilBar);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        searchProfilBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                chargerRecyclerView(chargerListUser(query));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    public void chargerRecyclerView(List<User> list){
        searchUserAdapter = new SearchUserAdapter(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerViewSearchProfil.setLayoutManager(layoutManager);
        recyclerViewSearchProfil.setAdapter(searchUserAdapter);
    }

    public List<User> chargerListUser(String text){
        final List<User> mUser = new ArrayList<>();
        Query query = usersRef.orderByChild("pseudo").startAt(text).endAt(text + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    User user = new User(
                            child.getValue().toString(),
                            child.getKey()
                    );
                    mUser.add(user);
                    chargerRecyclerView(mUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return mUser;
    }
}
