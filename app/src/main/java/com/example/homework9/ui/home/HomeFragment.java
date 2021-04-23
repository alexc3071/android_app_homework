package com.example.homework9.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.homework9.CardListAdapter;
import com.example.homework9.DetailsActivity;
import com.example.homework9.R;
import com.example.homework9.ReviewAdapter;
import com.example.homework9.SliderAdapter;
import com.example.homework9.SliderData;
import com.google.gson.Gson;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private HomeViewModel homeViewModel;

    //Variables
    public int cur_tab;

    //Slider variables

    //Card variables
    private RecyclerView popular_card_box;
    private CardListAdapter popular_adapter;
    private CardData cardListData;


    //Data type for home screen data
    class HomeData{
        Map<String, Map<String, String>[]> data;
    }

    //Data type for card data
    class CardData{
        ArrayList<Map<String, String>> data;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.loading_screen).bringToFront();

        //Set movie and tv tab buttons
        view.findViewById(R.id.movie_tab_button).setOnClickListener(this);
        view.findViewById(R.id.tv_tab_button).setOnClickListener(this);

        //Set current tab
        cur_tab = 0;

        // Initialize data
        cardListData = new CardData();
        cardListData.data = new ArrayList<Map<String, String>>();

        //Set recycler view adapter and manager for popular items
        // Lookup the recyclerview in activity layout
        popular_card_box = (RecyclerView) view.findViewById(R.id.popular_recycler);
        popular_card_box.setNestedScrollingEnabled(true);

        // Create adapter passing in the empty data object
        popular_adapter = new CardListAdapter(cardListData.data, getContext(), new CardListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Map<String, String> item) {
                switchDetails(item);
            }
        });
        // Attach the adapter to the recyclerview to populate items
        popular_card_box.setAdapter(popular_adapter);
        // Set layout manager to position the items
        popular_card_box.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        //Get Data
        setHomeFragment();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.movie_tab_button:
                toggleMovie(view);
                break;
            case R.id.tv_tab_button:
                toggleTV(view);
                break;
            default:
                break;
        }
    }

    // A method to switch to the details activity
    public void switchDetails(Map<String, String> item){
        String media_type = item.get("media_type");
        String id = item.get("id");
        Intent dIntent = new Intent(getActivity(), DetailsActivity.class);
        dIntent.putExtra("media_type", media_type);
        dIntent.putExtra("id", id);
        startActivity(dIntent);
    }


    // A method to switch to the details activity
    public void switchDetails(){
        String media_type = "movie";
        String id = "587807";
        Intent dIntent = new Intent(getActivity(), DetailsActivity.class);
        dIntent.putExtra("media_type", media_type);
        dIntent.putExtra("id", id);
        startActivity(dIntent);
    }

    public void toggleMovie(View view){
        if(cur_tab == 1){
            TextView m_button = getView().findViewById(R.id.movie_tab_button);
            TextView t_button = getView().findViewById(R.id.tv_tab_button);
            m_button.setTextColor(ContextCompat.getColor(getContext() , R.color.white));
            t_button.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            cur_tab = 0;

            getView().findViewById(R.id.movie_tab_content).setVisibility(View.VISIBLE);
        }

    }
    public void toggleTV(View view){
        if(cur_tab == 0){
            TextView m_button = getView().findViewById(R.id.movie_tab_button);
            TextView t_button = getView().findViewById(R.id.tv_tab_button);
            t_button.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            m_button.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            cur_tab = 1;
            getView().findViewById(R.id.movie_tab_content).setVisibility(View.GONE);
            switchDetails();
        }
    }

    //Debugging method
    public void heavyProcess(){
        try{
            Thread.sleep(2000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    protected void setUpPopularGroup(Map<String, String>[] popular_arr){

        for(int i = 0; i < popular_arr.length; i++){
            cardListData.data.add(popular_arr[i]);
        }
        popular_adapter.notifyDataSetChanged();
    }

    protected void setUpTopGroup(Map<String, String>[] popular_arr){

        for(int i = 0; i < popular_arr.length; i++){
            cardListData.data.add(popular_arr[i]);
        }
        popular_adapter.notifyDataSetChanged();
    }

    protected void setUpSlider(Map<String, String>[] item_arr){
        // we are creating array list for storing our image urls.
        ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();

        // initializing the slider view.
        SliderView sliderView = getView().findViewById(R.id.slider);


        // adding the urls inside array list
        for(int i = 0; i < item_arr.length; i++){
            sliderDataArrayList.add(new SliderData(item_arr[i].get("poster_path")));
        }
        // passing this array list inside our adapter class.
        SliderAdapter adapter = new SliderAdapter(getContext(), sliderDataArrayList);

        // below method is used to set auto cycle direction in left to
        // right direction you can change according to requirement.
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);

        // below method is used to
        // setadapter to sliderview.
        sliderView.setSliderAdapter(adapter);

        // below method is use to set
        // scroll time in seconds.
        sliderView.setScrollTimeInSec(3);

        // to set it scrollable automatically
        // we use below method.
        sliderView.setAutoCycle(true);

        // to start autocycle below method is used.
        sliderView.startAutoCycle();
    }

    protected void setHomeContent(String response){

        //Parse response
        Gson gson = new Gson();
        HomeData home_data=null;
        try{
            home_data = gson.fromJson(response, HomeData.class);
        } catch (Exception error){
            Log.e("GSON error", error.toString());
        }

        //Create home content
        Map<String, String>[] movie_playing= home_data.data.get("movie_playing");
        Map<String, String>[] movie_popular= home_data.data.get("movie_popular");
        Map<String, String>[] movie_top= home_data.data.get("movie_top");
        Map<String, String>[] tv_trending = home_data.data.get("tv_trending");
        Map<String, String>[] tv_popular= home_data.data.get("tv_popular");
        Map<String, String>[] tv_top = home_data.data.get("tv_top");

        setUpSlider(movie_playing);
        setUpPopularGroup(movie_popular);

        //Hide the progress bar when ready to show content
        getView().findViewById(R.id.loading_screen).setVisibility(View.GONE);
    }

    protected void setHomeFragment(){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url ="http://10.0.2.2:8080/homecontent";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        setHomeContent(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", "That didn't work!");
            }
        });

        queue.add(stringRequest);
    }

}