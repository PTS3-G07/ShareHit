package com.example.sharehit;

import android.content.Context;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sharehit.Model.Album;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DezerApiAlbum extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private AlbumAdapter mExampleAdapter;
    private ArrayList<Album> mExampleList;
    private RequestQueue mRequestQueue;
    Context c;
    private SearchView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dezer_api);

        search = (SearchView) findViewById(R.id.searchv);

        mRecyclerView = findViewById(R.id.recycler_view_d);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mExampleList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(this);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mExampleList.clear();
                parseJSON(newText);
                return false;
            }
        });


    }

    private Map<String, String> parseJSON(String albumName) {

        String url = "https://deezerdevs-deezer.p.rapidapi.com/album/" + albumName;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String name = response.getString("name");
                    String imgUrl = response.getString("picture");
                    //Log.d("img", imgUrl);
                    mExampleList.add(new Album(name, imgUrl));
                    mExampleAdapter = new AlbumAdapter(DezerApiAlbum.this, mExampleList);
                    mRecyclerView.setAdapter(mExampleAdapter);

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

    
}
