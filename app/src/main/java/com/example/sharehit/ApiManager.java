package com.example.sharehit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SearchView;

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
import com.example.sharehit.Model.Type;
import com.example.sharehit.Model.Video;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
                /*
                if (TextUtils.isEmpty(newText)) {
                    mExampleList.clear();
                } else {
                    parseJSON(newText);
                }
*/

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



                // Toast.makeText(DezerApi.this, "Result: "+newText, Toast.LENGTH_LONG).show();
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
                        String imgUrl = data.getString("picture_medium");
                        String link = data.getString("link");
                        mExampleList.add(new Artist(name, nbFan, imgUrl, link));
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
                        mExampleList.add(new Album(name, artistName, imgUrl, link));
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
                        String imgUrl = artist.getString("picture_medium");
                        String previewUrl = data.getString("preview");
                        String link = data.getString("link");
                        Log.e("abcde", title+previewUrl);
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
                    Log.e("testest", "response");
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
        Intent postRec = new Intent(this, PostRec.class);
        boolean isPLAYING = false;
        /*if(mExampleList.get(position) instanceof Morceau){
            Morceau clickedItem = (Morceau) mExampleList.get(position);
            MediaPlayer mp = new MediaPlayer();
            try {
                mp.setDataSource(clickedItem.getSongUrl());
                mp.prepareAsync();
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
            } catch (IOException e) {
                Log.e("pa2chance", "prepare() failed");
            }

        }
        else{*/
            Type clickedItem = mExampleList.get(position);
            postRec.putExtra(EXTRA_URL, clickedItem.getImgUrl());
            postRec.putExtra(EXTRA_NAME, clickedItem.getName());
            postRec.putExtra(EXTRA_TYPE, typeRecom);
            postRec.putExtra(EXTRA_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
            postRec.putExtra(EXTRA_LINK, clickedItem.getLink());
            if(clickedItem instanceof Morceau) {
                clickedItem = (Morceau) clickedItem;
                postRec.putExtra(EXTRA_PREVIEW, ((Morceau) clickedItem).getSongUrl());
            }
            startActivity(postRec);
        //}
    }

        /*postRec.putExtra(EXTRA_URL, clickedItem.getImgUrl());
        postRec.putExtra(EXTRA_NAME, clickedItem.getName());
        postRec.putExtra(EXTRA_FAN, clickedItem.getNbFans());
        startActivity(postRec);*/

}
