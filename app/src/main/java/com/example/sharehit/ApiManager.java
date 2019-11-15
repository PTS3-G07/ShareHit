package com.example.sharehit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sharehit.Adapter.TypeAdapter;
import com.example.sharehit.Model.Album;
import com.example.sharehit.Model.Artist;
import com.example.sharehit.Model.Morceau;
import com.example.sharehit.Model.Recommendation;
import com.example.sharehit.Model.Type;
import com.example.sharehit.Model.Video;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ApiManager extends AppCompatActivity implements TypeAdapter.OnItemclickListener {

    public static final String EXTRA_URL = "imgUrl";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_ID = "userRecoUid";
    public static final String EXTRA_PREVIEW = "userUrlPreview";
    public static final String EXTRA_LINK = "link";

    private RecyclerView mRecyclerView;
    private TypeAdapter mExampleAdapter;
    private ArrayList<Type> mExampleList;
    private RequestQueue mRequestQueue;
    Context c;
    private SearchView search;
    public int type;
    private String typeRecom;

    public MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dezer_api);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Bundle b = getIntent().getExtras();
        if(b != null)
            type = b.getInt("key");

        search = (SearchView) findViewById(R.id.searchv);

        mRecyclerView = findViewById(R.id.recycler_view_d);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mExampleList = new ArrayList<Type>();
        mRequestQueue = Volley.newRequestQueue(this);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mExampleList.clear();
                if(type == 1){
                    parseJSONartist(newText);
                    typeRecom="un artiste";
                }
                if(type == 2) {
                    parseJSONalbum(newText);
                    typeRecom="un album";
                }
                if(type == 3) {
                    parseJSONtrack(newText);
                    typeRecom="un morceau";
                }
                if(type == 4) {
                    parseJSONomdb(newText, "movie");
                    typeRecom="un film";
                }
                if(type == 5) {
                    parseJSONomdb(newText, "series");
                    typeRecom="une série";
                }
                if(type == 6) {
                    parseJSONomdb(newText,"game");
                    typeRecom="un jeu vidéo";
                }

                return false;
            }
        });


    }

    private Map<String, String> parseJSONartist(String artistName) {

        String url = "http://api.deezer.com/2.0/search/artist/?q="+artistName+"&index=0&nb_items=20&output=json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for(int i = 0 ; jsonArray.length() > i; i++){
                        JSONObject data = jsonArray.getJSONObject(i);
                        String name = data.getString("name");
                        String nbFan = data.getString("nb_fan");
                        String imgUrl = data.getString("picture_big");
                        String link = data.getString("link");
                        String tracklist = data.getString("tracklist");
                        final Artist artiste = new Artist(name, nbFan, imgUrl, link);
                        parseJSONartistPreview(tracklist, artiste);
                        mExampleList.add(artiste);
                    }

                    mExampleAdapter = new TypeAdapter(ApiManager.this, mExampleList);
                    mRecyclerView.setAdapter(mExampleAdapter);
                    mExampleAdapter.setOnItemClickListener(ApiManager.this);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-rapidapi-host", "deezerdevs-deezer.p.rapidapi.com");
                params.put("x-rapidapi-key", "e057a6cddamshcf40c6b8e5a6046p1233eajsnf273df986993");

                return params;
            }
        };

        mRequestQueue.add(request);
        return null;
    }

    private Map<String, String> parseJSONartistPreview(String tracklist, final Artist artiste) {

        if(tracklist.endsWith("50"))
        {
            tracklist = tracklist.substring(0,tracklist.length() - 2);
        tracklist+="1";
    }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, tracklist, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for(int i = 0 ; jsonArray.length() > i; i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        String urlPreview = data.getString("preview");
                        artiste.setSongUrl(urlPreview);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-rapidapi-host", "deezerdevs-deezer.p.rapidapi.com");
                params.put("x-rapidapi-key", "e057a6cddamshcf40c6b8e5a6046p1233eajsnf273df986993");

                return params;
            }
        };

        mRequestQueue.add(request);
        return null;
    }

    private Map<String, String> parseJSONalbum(String albumName) {

        String url = "http://api.deezer.com/2.0/search/album/?q="+albumName+"&index=0&nb_items=20&output=json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for(int i = 0 ; jsonArray.length() > i; i++){
                        JSONObject data = jsonArray.getJSONObject(i);
                        String name = data.getString("title");
                        JSONObject artiste = data.getJSONObject("artist");
                        String artistName = artiste.getString("name");
                        String imgUrl = data.getString("cover_big");
                        String link = data.getString("link");
                        String tracklist = data.getString("tracklist");
                        Album album = new Album(artistName, name, imgUrl, link);
                        parseJSONalbumPreview(tracklist, album);
                        mExampleList.add(album);
                    }

                    mExampleAdapter = new TypeAdapter(ApiManager.this, mExampleList);
                    mRecyclerView.setAdapter(mExampleAdapter);
                    mExampleAdapter.setOnItemClickListener(ApiManager.this);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-rapidapi-host", "deezerdevs-deezer.p.rapidapi.com");
                params.put("x-rapidapi-key", "e057a6cddamshcf40c6b8e5a6046p1233eajsnf273df986993");

                return params;
            }
        };

        mRequestQueue.add(request);
        return null;
    }

    private Map<String, String> parseJSONalbumPreview(String tracklist, final Album album) {

        if(tracklist.endsWith("50"))
        {
            tracklist = tracklist.substring(0,tracklist.length() - 2);
            tracklist+="1";
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, tracklist, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for(int i = 0 ; jsonArray.length() > i; i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        String urlPreview = data.getString("preview");
                        album.setSongUrl(urlPreview);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-rapidapi-host", "deezerdevs-deezer.p.rapidapi.com");
                params.put("x-rapidapi-key", "e057a6cddamshcf40c6b8e5a6046p1233eajsnf273df986993");

                return params;
            }
        };

        mRequestQueue.add(request);
        return null;
    }

    private Map<String, String> parseJSONtrack(String trackName) {

        String url = "http://api.deezer.com/2.0/search/track/?q="+trackName+"&index=0&nb_items=20&output=json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for(int i = 0 ; jsonArray.length() > i; i++){
                        JSONObject data = jsonArray.getJSONObject(i);
                        JSONObject artist = data.getJSONObject("artist");
                        String title = data.getString("title");
                        String albumTitle = artist.getString("name");
                        String imgUrl = artist.getString("picture_big");
                        String previewUrl = data.getString("preview");
                        String link = data.getString("link");
                        Morceau m = new Morceau(title, albumTitle, imgUrl,previewUrl,link);
                        mExampleList.add(m);
                    }


                    mExampleAdapter = new TypeAdapter(ApiManager.this, mExampleList);
                    mRecyclerView.setAdapter(mExampleAdapter);
                    mExampleAdapter.setOnItemClickListener(ApiManager.this);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-rapidapi-host", "deezerdevs-deezer.p.rapidapi.com");
                params.put("x-rapidapi-key", "e057a6cddamshcf40c6b8e5a6046p1233eajsnf273df986993");

                return params;
            }
        };

        mRequestQueue.add(request);
        return null;
    }

    private Map<String, String> parseJSONomdb(String search, final String type) {

        String url = "http://www.omdbapi.com/?s="+search+"&type="+type+"&apikey=a20c2297";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("Search");
                    for(int i = 0 ; jsonArray.length() > i; i++){
                        JSONObject data = jsonArray.getJSONObject(i);
                        String title = data.getString("Title");
                        String year = data.getString("Year");
                        String imgUrl = data.getString("Poster");
                        String imdbID = data.getString("imdbID");
                        String link = "https://www.imdb.com/title/"+imdbID+"/";
                        mExampleList.add(new Video(title, year, imgUrl, link));
                    }

                    mExampleAdapter = new TypeAdapter(ApiManager.this, mExampleList);
                    mRecyclerView.setAdapter(mExampleAdapter);
                    mExampleAdapter.setOnItemClickListener(ApiManager.this);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-rapidapi-host", "deezerdevs-deezer.p.rapidapi.com");
                params.put("x-rapidapi-key", "e057a6cddamshcf40c6b8e5a6046p1233eajsnf273df986993");

                return params;
            }
        };

        mRequestQueue.add(request);
        return null;
    }

    @Override
    public void onItemClick(int position) {
        final Type clickedItem = mExampleList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_postrec,null);

        TextView name = dialogView.findViewById(R.id.name_post);
        name.setText(clickedItem.getName());

        ImageView img = dialogView.findViewById(R.id.picture_post);
        Picasso.with(getApplicationContext()).load(clickedItem.getImgUrl()).fit().centerInside().into(img);

        Button post = dialogView.findViewById(R.id.post_post_button);
        Button cancel = dialogView.findViewById(R.id.cancel_post_button);



        final Type finalClickedItem = clickedItem;
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Recommendation recommendation = new Recommendation(
                        typeRecom,
                        FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        finalClickedItem.getName(),
                        finalClickedItem.getImgUrl(),
                        null,
                        new Timestamp(System.currentTimeMillis()).getTime(),
                        finalClickedItem.getLink()
                );
                if(finalClickedItem instanceof Morceau){
                    recommendation.setUrlPreview(((Morceau)clickedItem).getSongUrl());

                }
                else if(finalClickedItem instanceof Artist){
                    recommendation.setUrlPreview(((Artist)clickedItem).getSongUrl());
                }
                else if(finalClickedItem instanceof Album){
                    recommendation.setUrlPreview(((Album)clickedItem).getSongUrl());

                }

                HashMap usersMap = new HashMap();
                DatabaseReference recomRef = FirebaseDatabase.getInstance().getReference().child("recos");
                String key = recomRef.push().getKey();
                usersMap.put(key, recommendation);
                recomRef.updateChildren(usersMap);
                startActivity(new Intent(ApiManager.this, FeedPage.class));
            }
        });

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }



}
