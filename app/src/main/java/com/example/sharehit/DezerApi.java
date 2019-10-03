package com.example.sharehit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.SearchView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sharehit.Adapter.ArtistAdapter;
import com.example.sharehit.Model.Artist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DezerApi extends AppCompatActivity implements ArtistAdapter.OnItemclickListener {

    public static final String EXTRA_URL = "imgUrl";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_FAN = "nbFan";

    private RecyclerView mRecyclerView;
    private ArtistAdapter mExampleAdapter;
    private ArrayList<Artist> mExampleList;
    private RequestQueue mRequestQueue;
    Context c;
    private SearchView search;
    public int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dezer_api);

        Bundle b = getIntent().getExtras();
        if(b != null)
            type = b.getInt("key");

        search = (SearchView) findViewById(R.id.searchv);

        mRecyclerView = findViewById(R.id.recycler_view_d);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mExampleList = new ArrayList<Artist>();
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
                parseJSON(newText);
                
                // Toast.makeText(DezerApi.this, "Result: "+newText, Toast.LENGTH_LONG).show();
                return false;
            }
        });


    }

    private Map<String, String> parseJSON(String artistName) {

        String url = "http://api.deezer.com/2.0/search/artist/?q="+ artistName +"&index=0&nb_items=20&output=json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for(int i = 0 ; jsonArray.length() > i; i++){
                        JSONObject data = jsonArray.getJSONObject(i);
                        String name = data.getString("name");
                        String nbFan = data.getString("nb_fan");
                        String imgUrl = data.getString("picture");
                        mExampleList.add(new Artist(name, nbFan, imgUrl));
                    }

                    mExampleAdapter = new ArtistAdapter(DezerApi.this, mExampleList);
                    mRecyclerView.setAdapter(mExampleAdapter);
                    mExampleAdapter.setOnItemClickListener(DezerApi.this);


                     */
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

    @Override
    public void onItemClick(int position) {
        Intent postRec = new Intent(this, PostRec.class);
        Artist clickedItem = mExampleList.get(position);
        postRec.putExtra(EXTRA_URL, clickedItem.getImgUrl());
        postRec.putExtra(EXTRA_NAME, clickedItem.getName());
        postRec.putExtra(EXTRA_FAN, clickedItem.getNbFans());
        startActivity(postRec);
    }
}
