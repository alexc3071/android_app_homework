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
import com.example.homework9.ui.search.SearchAdapter;
import com.example.homework9.ui.search.SearchFragment;
import com.google.gson.Gson;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener{

    String media_type;
    String s_id;
    String poster_path;
    String title;
    Map<String, String> my_item;
    DetailsData details_data=null;
    CastData cast_data=null;
    ReviewData review_data=null;
    ReviewAdapter rv_adapter= null;
    WatchHolder watch_holder=null;
    int watch_state;

    //Recommended variables
    private RecommendedData rec_data;
    private RecyclerView rec_box;
    RecommendAdapter rec_adapter;

    //Variable for youtube player
    YouTubePlayerView youTubePlayerView;

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
        String backdrop_path;
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

    //Data type for recommended
    class RecommendedData{
        ArrayList<Map<String, String>> data;
    }


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
        title = getIntent().getStringExtra("title");


        //Set lifecycle observer for youtube video

        youTubePlayerView = findViewById(R.id.youtube_player_view);


        //Set up Watch Holder object to access watchlist in shared preferences
        watch_holder = new WatchHolder(DetailsActivity.this);

        //Initialize card recycler views and associated items for reviews
        init_review_recyclers();

        //Initialize card recycler views and associated items for recommended
        init_rec_recyclers();

        //Initialize watchlist button
        initWatchButton();

        //Set the content for the layout
        initializeDetails();
    }

    public void init_review_recyclers(){
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
    }

    // A method to switch to the details activity
    public void switchDetails(Map<String, String> item){
        String media_type = item.get("media_type");
        String id = item.get("id");
        String poster_path = item.get("poster_path");
        String title = item.get("title");
        Intent dIntent = new Intent(DetailsActivity.this, DetailsActivity.class);
        dIntent.putExtra("media_type", media_type);
        dIntent.putExtra("id", id);
        dIntent.putExtra("poster_path", poster_path);
        dIntent.putExtra("title", title);
        startActivity(dIntent);
    }

    public void init_rec_recyclers(){
        // Initialize data
        rec_data = new RecommendedData();
        rec_data.data = new ArrayList<Map<String, String>>();

        //Set recycler view adapter and manager for search results
        // Lookup the recyclerview in activity layout
        rec_box = (RecyclerView) findViewById(R.id.recommended_recycler);
        rec_box.setNestedScrollingEnabled(true);

        // Create adapter passing in the empty data object
        rec_adapter = new RecommendAdapter(rec_data.data, DetailsActivity.this, new RecommendAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Map<String, String> item) {
                switchDetails(item);
            }
        });
        // Attach the adapter to the recyclerview to populate items
        rec_box.setAdapter(rec_adapter);
        // Set layout manager to position the items
        rec_box.setLayoutManager(new LinearLayoutManager(DetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
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
        my_item = new HashMap<String, String>();
        my_item.put("id", s_id);
        my_item.put("media_type", media_type);
        my_item.put("poster_path", poster_path);
        my_item.put("title", title);

        Boolean isInWatch = watch_holder.isInWatchList(my_item);
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
            watch_holder.toggleWatchList(my_item);
            watch_state = 1;
        }
        else{
            i_view.setImageResource(R.drawable.ic_baseline_add_circle_outline_24);
            Toast.makeText(DetailsActivity.this, details_data.title + " was removed from Watchlist",
                    Toast.LENGTH_LONG).show();
            watch_holder.toggleWatchList(my_item);
            watch_state = 0;
        }
    }

    //Methods for social media access
    public void postTwitter(){
        String my_url = "https://twitter.com/intent/tweet?text=" + Uri.encode("Check this out!") + " %0D%0A";
        if(details_data.video_name != null) {
            my_url += "&url=https://www.youtube.com/watch?v=" + Uri.encode(details_data.video_key) + " %0D%0A";
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(my_url));
        startActivity(browserIntent);
    }
    public void postFacebook(){
        if(details_data.video_name != null ){
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


    protected void initializeVideo(String video_key) {
        getLifecycle().addObserver(youTubePlayerView);
        YouTubePlayerView you_view = findViewById(R.id.youtube_player_view);
        ImageView backdrop_view = findViewById(R.id.d_backdrop);
        if(details_data.video_name != null){
            you_view.setVisibility(View.VISIBLE);
            backdrop_view.setVisibility(View.GONE);
            you_view.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    youTubePlayer.cueVideo(video_key, 0);
                }
            });
        }
        else{
            you_view.setVisibility(View.GONE);
            backdrop_view.setVisibility(View.VISIBLE);
            Glide.with(DetailsActivity.this).load(details_data.backdrop_path).into(backdrop_view);
        }

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

        //Set youtube video
        initializeVideo(details_data.video_key);

        // Set item title
        TextView title_view = findViewById(R.id.item_title);
        title_view.setText(details_data.title);
        // set item overview
        TextView overview_view = findViewById(R.id.overview_content);
        TextView overview_word = findViewById(R.id.overview_word);
        if(details_data.overview != null){
            overview_view.setText(details_data.overview);
        }
        else{
            overview_view.setVisibility(View.GONE);
            overview_word.setVisibility(View.GONE);
        }
        // Set item genres
        TextView genres_view = findViewById(R.id.genres_content);
        TextView genres_word = findViewById(R.id.genres_word);
        if(details_data.genres != null){
            genres_view.setText(details_data.genres);
        }
        else{
            genres_view.setVisibility(View.GONE);
            genres_word.setVisibility(View.GONE);
        }

        // set item year
        TextView year_view = findViewById(R.id.year_content);
        TextView year_word = findViewById(R.id.year_word);
        if(details_data.year != null){
            year_view.setText(details_data.year);
        }
        else{
            year_view.setVisibility(View.GONE);
            year_word.setVisibility(View.GONE);
        }

        findViewById(R.id.d_loading_screen).setVisibility(View.GONE);
        findViewById(R.id.d_content).setVisibility(View.VISIBLE);
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

    private void setRecommendations(String response){
        //Parse response
        Gson gson = new Gson();
        try{
            rec_data = gson.fromJson(response, RecommendedData.class);
        } catch (Exception error){
            Log.e("GSON error", error.toString());
        }
        if(rec_data.data.size() > 0){
            findViewById(R.id.recommended_word).setVisibility(View.VISIBLE);
        }
        rec_adapter.updateItems(rec_data.data);

    }

    protected void initializeDetails() {
        RequestQueue queue = Volley.newRequestQueue(DetailsActivity.this);
        String root_url = "http://10.0.2.2:8080/";
        String details_url = root_url + "details/" + media_type + "/" + s_id;
        String cast_url = root_url + "cast/" +  media_type + "/" + s_id;
        String review_url = root_url + "reviews/" + media_type + "/" + s_id;
        String rec_url = root_url + "recommended/" + media_type + "/" + s_id;
        Log.d("url", rec_url);

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

        // Request a string response from the provided URL.
        StringRequest recRequest = new StringRequest(Request.Method.GET, rec_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        setRecommendations(response);
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
        queue.add(recRequest);
    }

}