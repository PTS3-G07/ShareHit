package com.example.sharehit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.sharehit.ApiManager.EXTRA_ID;
import static com.example.sharehit.ApiManager.EXTRA_LINK;
import static com.example.sharehit.ApiManager.EXTRA_NAME;
import static com.example.sharehit.ApiManager.EXTRA_PREVIEW;
import static com.example.sharehit.ApiManager.EXTRA_TYPE;
import static com.example.sharehit.ApiManager.EXTRA_URL;

import static com.example.sharehit.R.*;

public class FeedPage extends AppCompatActivity {

    Button logout;
    SpaceNavigationView navigationView;
    FirebaseAuth firebaseAuth;
    Dialog myDialog;
    ImageButton artiste;
    ImageButton album;
    ImageButton morceau;
    ImageButton jeuVideo;
    ImageButton serie;
    ImageButton film;

    ImageButton notification;

    private FirebaseAuth mAuth;
    CircleImageView profilePicture;

    public int idSearch;
    private Bundle b;

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key" + "AAAA7jlTwSc:APA91bEri7kGWiI-rXn3iE1LOttF0c8FJWcRr6MJwYUAhhTIjf4flxtr_lMEso12JyidGtZqIu-Gb3R74xU_m2ioLhyRNm-OnqyuW8uOY9DY9UIK4IZdSJVDURFAQd4sfdcVxFWfajp7";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(FeedPage.this, MainActivity.class));
        }
    }





    private boolean loadFragement(Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager().beginTransaction().replace(id.container, fragment)
                    .commit();

            return true;
        }
        return false;
    }

    public static Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(Resources.getSystem(), x);
    }

    private Drawable ImageOperations(Context ctx, String url, String saveFilename) {
        try {
            InputStream is = (InputStream) this.fetch(url);
            Drawable d = Drawable.createFromStream(is, saveFilename);
            return d;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object fetch(String address) throws MalformedURLException,IOException {

        URL url = new URL(address);
        Object content = url.getContent();
        return content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_feed_page);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mAuth = FirebaseAuth.getInstance();


        navigationView = findViewById(id.space);
        profilePicture = (CircleImageView) findViewById(id.itemProfilPicture);
        //Picasso.with(getApplicationContext()).load("https://firebasestorage.googleapis.com/v0/b/share-hit-52071.appspot.com/o/Pdp%2F"+mAuth.getCurrentUser().getUid()+"?alt=media&token=32f03c76-31a8-4ea2-8cac-8fa92bef6667").networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).fit().centerInside().into(profilePicture);

        navigationView.initWithSaveInstanceState(savedInstanceState);
        navigationView.addSpaceItem(new SpaceItem("", drawable.time_icon));
        navigationView.addSpaceItem(new SpaceItem("", drawable.star_icon));
        navigationView.addSpaceItem(new SpaceItem("", drawable.bookmark));
        navigationView.addSpaceItem(new SpaceItem("", drawable.profil));
        navigationView.showIconOnly();

        notification = (ImageButton) findViewById(id.notification_button);


        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(FeedPage.this);
                editText.setHint("Demande...");
                AlertDialog.Builder builder = new AlertDialog.Builder(FeedPage.this);
                builder.setTitle("Demander une recommandation")
                        .setView(editText)
                        .setPositiveButton("Envoyer", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(editText.getText().toString().trim().length() > 2){
                                    TOPIC = "/topics/userABC"; //topic must match with what the receiver subscribed to
                                    NOTIFICATION_TITLE = "ShareHit notification";
                                    NOTIFICATION_MESSAGE = editText.getText().toString();

                                    JSONObject notification = new JSONObject();
                                    JSONObject notifcationBody = new JSONObject();
                                    try {
                                        notifcationBody.put("title", NOTIFICATION_TITLE);
                                        notifcationBody.put("message", NOTIFICATION_MESSAGE);

                                        notification.put("to", TOPIC);
                                        notification.put("data", notifcationBody);
                                    } catch (JSONException e) {
                                        Log.e(TAG, "onCreate: " + e.getMessage() );
                                    }
                                    sendNotification(notification);

                                } else {
                                    Toast.makeText(getApplicationContext(), "Veuillez saisir une demande de plus de 3 caractères", Toast.LENGTH_LONG).show();
                                }


                            }
                        });
                builder.create().show();
            }
        });

        //checkUserStatus();
        /*
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                checkUserStatus();
            }
        });
         */


/*
        myDialog=new Dialog(this, style.DialogTheme);
        Window window = myDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        /*wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;*/

        final Dialog d = new Dialog(this, R.style.DialogTheme);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.new_recommendation);
        ImageButton i = d.findViewById(id.artiste);
        Window window = d.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);


        //window.setAttributes(wlp);
        b = getIntent().getExtras();


        if(b == null){
            Fragment fragment = new FeedFragement();
            loadFragement(fragment);
        } else {
            Fragment fragment = new ProfilFragement();
            loadFragement(fragment);
        }

        navigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            Fragment fragment = null;
            @Override
            public void onCentreButtonClick() {

                final Intent intent = new Intent(FeedPage.this, ApiManager.class);
                final Bundle b = new Bundle();
                d.show();
                artiste = d.findViewById(R.id.artiste);
                artiste.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        b.putInt("key", 1); //Your id
                        intent.putExtras(b); //Put your id to your next Intent
                        startActivity(intent);
                        //finish();
                    }
                });
                album = d.findViewById(id.album);
                album.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        b.putInt("key", 2); //Your id
                        intent.putExtras(b); //Put your id to your next Intent
                        startActivity(intent);
                        //finish();
                        }
                });

                morceau = d.findViewById(id.morceau);
                morceau.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        b.putInt("key", 3);
                        intent.putExtras(b);
                        startActivity(intent);
                        //finish();
                    }
                });

                film = d.findViewById(id.film);
                film.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        b.putInt("key", 4);
                        intent.putExtras(b);
                        startActivity(intent);
                        //finish();
                    }
                });

                jeuVideo = d.findViewById(id.jeuVideo);
                jeuVideo.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        b.putInt("key", 6);
                        intent.putExtras(b);
                        startActivity(intent);
                        //finish();
                    }
                });

                serie = d.findViewById(id.serie);
                serie.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        b.putInt("key", 5);
                        intent.putExtras(b);
                        startActivity(intent);
                        //finish();
                    }
                });
                //int height = (int)(getResources().getDisplayMetrics().heightPixels*0.4);

                /*
                myDialog.setContentView(R.layout.new_recommendation);
                //Toast.makeText(FeedPage.this,"onCentreButtonClick", Toast.LENGTH_SHORT).show();
                myDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                myDialog.show();

                 */
                /*fragment = new NewRecommendationFragment();
                loadFragement(fragment);*/
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                if(itemIndex == 0){
                    fragment = new FeedFragement();
                    loadFragement(fragment);
                }else if (itemIndex == 1){
                    fragment = new SearchFragement();
                    loadFragement(fragment);
                }else if(itemIndex == 2){
                    fragment = new NotifFragement();
                    loadFragement(fragment);
                }else if(itemIndex == 3){
                    fragment = new ProfilFragement();
                    loadFragement(fragment);
                }
               // Toast.makeText(FeedPage.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                //Toast.makeText(FeedPage.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                        /*
                        edtTitle.setText("");
                        edtMessage.setText("");

                         */
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FeedPage.this, "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
