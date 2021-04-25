package com.example.homework9;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.homework9.ui.home.HomeFragment;
import com.example.homework9.ui.home.HomeViewModel;
import com.google.gson.Gson;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener{

    String media_type;
    String s_id;
    String poster_path;
    DetailsData details_data=null;
    CastData cast_data=null;
    ReviewData review_data=null;
    ReviewAdapter rv_adapter= null;
    WatchHolder watch_holder=null;
    String watch_key = null;
    int watch_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Switch theme from splash screen to main theme
        setTheme(R.style.Theme_Homework9);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Set click listeners
        findViewById(R.id.watch_button).setOnClickListener(this);
        findViewById(R.id.facebook_button).setOnClickListener(this);
        findViewById(R.id.twitter_button).setOnClickListener(this);

        //Get the media_type and id passed in
        media_type = getIntent().getStringExtra("media_type");
        s_id = getIntent().getStringExtra("id");
        poster_path = getIntent().getStringExtra("poster_path");

        //Set lifecycle observer for youtube video
        /**
        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);
        YouTubePlayerView you_view = findViewById(R.id.youtube_player_view);
        you_view.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                initializeVideo(youTubePlayer);
            }
        });
         **/

        //Set up Watch Holder object to access watchlist in shared preferences
        watch_holder = new WatchHolder(DetailsActivity.this);

        //Set recycler view adapter and manager
        // Lookup the recyclerview in activity layout
        RecyclerView review_box = (RecyclerView) findViewById(R.id.review_box);
        review_box.setNestedScrollingEnabled(false);

        // Initialize data
        review_data = new ReviewData();
        review_data.data = new ArrayList<Map<String, String>>();
        // Create adapter passing in the empty data object
        rv_adapter = new ReviewAdapter(review_data.data, new ReviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Map<String, String> item) {
                switchReview(item);
            }
        });
        // Attach the adapter to the recyclerview to populate items
        review_box.setAdapter(rv_adapter);
        // Set layout manager to position the items
        review_box.setLayoutManager(new LinearLayoutManager(this));

        //Initialize watchlist button
        initWatchButton();

        //Set the content for the layout
        initializeDetails();
    }


    // A method to switch to the details activity
    public void switchReview(Map<String, String> item){
        Intent dIntent = new Intent(DetailsActivity.this, ReviewActivity.class);
        dIntent.putExtra("author", item.get("author"));
        if(item.get("content")!= null){
            dIntent.putExtra("content", item.get("content"));
        }
        if(item.get("date")!= null){
            dIntent.putExtra("date", item.get("date"));
        }
        if(item.get("rating")!= null){
            dIntent.putExtra("rating", item.get("rating"));
        }


        startActivity(dIntent);
    }

    // A method to initialize the watchlist button and watch state
    public void initWatchButton(){
        watch_key = media_type + "_" + s_id + "_" + poster_path;
        Boolean isInWatch = watch_holder.isInWatchList(watch_key);
        if(isInWatch){
            ImageView watch_button = (ImageView) findViewById(R.id.watch_button);
            watch_button.setImageResource(R.drawable.ic_baseline_remove_circle_outline_24);
            watch_state = 1;
        }
        else{
            ImageView watch_button = (ImageView) findViewById(R.id.watch_button);
            watch_button.setImageResource(R.drawable.ic_baseline_add_circle_outline_24);
            watch_state = 0;
        }
    }

    // Methods for watchlist control
    public void toggleWatch(View view){
        ImageView i_view = (ImageView) view;
        if(watch_state == 0){
            i_view.setImageResource(R.drawable.ic_baseline_remove_circle_outline_24);
            Toast.makeText(DetailsActivity.this, details_data.title + " was added to Watchlist",
                    Toast.LENGTH_LONG).show();
            watch_holder.toggleWatchList(watch_key);
            watch_state = 1;
        }
        else{
            i_view.setImageResource(R.drawable.ic_baseline_add_circle_outline_24);
            Toast.makeText(DetailsActivity.this, details_data.title + " was removed from Watchlist",
                    Toast.LENGTH_LONG).show();
            watch_holder.toggleWatchList(watch_key);
            watch_state = 0;
        }
    }

    //Methods for social media access
    public void postTwitter(){
        String my_url = "https://twitter.com/intent/tweet?text=" + Uri.encode("Check this out!") + " %0D%0A";
        if(!"NOT AVAILABLE".equals(details_data.video_name)) {
            my_url += "&url=https://www.youtube.com/watch?v=" + Uri.encode(details_data.video_key) + " %0D%0A";
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(my_url));
        startActivity(browserIntent);
    }
    public void postFacebook(){
        if(!"NOT_AVAILABLE".equals(details_data.video_name)){
            String my_str = "https://www.facebook.com/sharer/sharer.php?u=";
            my_str += Uri.encode("https://www.youtube.com/watch?v=" + details_data.video_key);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(my_str));
            startActivity(browserIntent);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.watch_button:
                toggleWatch(view);
                break;
            case R.id.facebook_button:
                postFacebook();
                break;
            case R.id.twitter_button:
                postTwitter();
                break;
            default:
                break;
        }
    }


    //Data type for home screen data
    class DetailsData {
        String id;
        String media_type;
        String title;
        String genres;
        String spoken_languages;
        String year;
        String release_date;
        String runtime;
        String overview;
        String vote_average;
        String tagline;
        String poster_path;
        String video_key;
        String video_name;
    }

    //Data type for home screen data
    class VideoData {
        String video_name;
        String video_key;
    }

    //Data type for cast
    class CastData{
        Map<String, String>[] data;
    }

    //Data type for review
    class ReviewData{
        ArrayList<Map<String, String>> data;
    }



    protected void initializeVideo(YouTubePlayer player) {
        RequestQueue queue = Volley.newRequestQueue(DetailsActivity.this);
        String root_url = "http://10.0.2.2:8080/";
        String details_url = root_url + "video/" + media_type + "/" + s_id;

        // Request a string response from the provided URL.
        StringRequest detailsRequest = new StringRequest(Request.Method.GET, details_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        VideoData v_data = null;
                        try {
                            v_data = gson.fromJson(response, VideoData.class);
                            Log.e("1",  v_data.video_key);
                            player.cueVideo(v_data.video_key, 0);
                        } catch (Exception error) {
                            Log.e("GSON error", error.toString());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", "That didn't work!");
            }
        });
    }


    protected void setDetails(String response) {

        //Parse response
        Gson gson = new Gson();
        try {
            details_data = gson.fromJson(response, DetailsData.class);
        } catch (Exception error) {
            Log.e("GSON error", error.toString());
        }

        //Create home content
        // Set item video key


        // Set item title
        TextView title_view = findViewById(R.id.item_title);
        title_view.setText(details_data.title);
        // set item overview
        TextView overview_view = findViewById(R.id.overview_content);
        overview_view.setText(details_data.overview);
        // Set item genres
        TextView genres_view = findViewById(R.id.genres_content);
        genres_view.setText(details_data.genres);
        // set item year
        TextView year_view = findViewById(R.id.year_content);
        year_view.setText(details_data.year);

    }

    protected void setCast(String response) {

        //Parse response
        Gson gson = new Gson();
        try {
            cast_data = gson.fromJson(response, CastData.class);
        } catch (Exception error) {
            Log.e("GSON error", error.toString());
        }

        if(cast_data.data != null){
            if(cast_data.data.length > 0){
                findViewById(R.id.cast_row1).setVisibility(View.VISIBLE);
                findViewById(R.id.cast_word).setVisibility(View.VISIBLE);
            }
            if(cast_data.data.length > 3){
                findViewById(R.id.cast_row2).setVisibility(View.VISIBLE);
            }
            for(int i = 0; i < cast_data.data.length; i++){

                int cast_box_id= getResources().getIdentifier("cast_box" + (i+1), "id", getPackageName());
                findViewById(cast_box_id).setVisibility(View.VISIBLE);
                int cast_name_id= getResources().getIdentifier("cast_name" + (i+1), "id", getPackageName());
                TextView cast_name = (TextView) findViewById(cast_name_id);
                cast_name.setText(cast_data.data[i].get("name"));
                int prof_image_id= getResources().getIdentifier("profile_image" + (i+1), "id", getPackageName());
                CircleImageView image_view = (CircleImageView) findViewById(prof_image_id);
                Glide.with(DetailsActivity.this)
                        .load(cast_data.data[i].get("profile_path"))
                        .fitCenter()
                        .into(image_view);
            }
        }
    }

    protected void setReviews(String response){
        //Parse response
        ReviewData my_data = null;
        Gson gson = new Gson();
        try {
            my_data = gson.fromJson(response, ReviewData.class);
        } catch (Exception error) {
            Log.e("GSON error", error.toString());
        }

        for(int i = 0; i < my_data.data.size(); i++){
            review_data.data.add(my_data.data.get(i));
        }
        if(review_data.data.size() == 0){
            findViewById(R.id.review_word).setVisibility(View.GONE);
        }
        Log.d("review_data size", String.valueOf(review_data.data.size()));
        rv_adapter.notifyDataSetChanged();


    }

    protected void initializeDetails() {
        RequestQueue queue = Volley.newRequestQueue(DetailsActivity.this);
        String root_url = "http://10.0.2.2:8080/";
        String details_url = root_url + "details/" + media_type + "/" + s_id;
        String cast_url = root_url + "cast/" +  media_type + "/" + s_id;
        String review_url = root_url + "reviews/" + media_type + "/" + s_id;
        Log.d("what", review_url);

        // Request a string response from the provided URL.
        StringRequest detailsRequest = new StringRequest(Request.Method.GET, details_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        setDetails(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", "That didn't work!");
            }
        });
        // Request a string response from the provided URL.
        StringRequest castRequest = new StringRequest(Request.Method.GET, cast_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        setCast(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", "That didn't work!");
            }
        });
        // Request a string response from the provided URL.
        StringRequest reviewRequest = new StringRequest(Request.Method.GET, review_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        setReviews(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", "That didn't work!");
            }
        });

        queue.add(detailsRequest);
        queue.add(castRequest);
        queue.add(reviewRequest);
    }

}