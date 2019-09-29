package com.example.sharehit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewFragement extends Fragment {

    private EditText editText;
    private CharSequence recherche;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragement_profil, null);

        editText = root.findViewById(R.id.recherche);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://deezerdevs-deezer.p.rapidapi.com/artist/eminem")
                .get()
                .addHeader("x-rapidapi-host", "deezerdevs-deezer.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "c68313b3dbmsh19d74264b8efb3dp1f6240jsne6ba3b2715a5")
                .build();


        try {
            Response response = client.newCall(request).execute();
            Log.d("TAG", response.body().toString());
        } catch (IOException e) {
            System.out.println("Problème");
        }

        return inflater.inflate(R.layout.fragment_new, null );
    }

}
