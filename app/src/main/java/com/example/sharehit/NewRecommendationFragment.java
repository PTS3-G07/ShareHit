package com.example.sharehit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NewRecommendationFragment extends Fragment {

    ImageButton artiste;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.new_recommendation, null );
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.new_recommendation, null);
        artiste = root.findViewById(R.id.artiste);
        artiste.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_LONG).show();
            }
        });
        return root;
    }
}
