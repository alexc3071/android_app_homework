package com.example.homework9.ui.search;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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
import com.example.homework9.SliderAdapter;
import com.example.homework9.ui.home.HomeFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class SearchFragment extends Fragment {

    private SearchViewModel searchViewModel;
    private SearchData search_data;

    //Recycler variables
    private RecyclerView s_result_box;
    SearchAdapter s_adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Initialize card recycler views and associated items
        init_recyclers(view);
        // A method for setting up the search view
        init_search_view(view);

    }

    //Data types
    class SearchData{
        ArrayList<Map<String, String>> data;
    }

    public void init_recyclers(View view){
        // Initialize data
        search_data = new SearchData();
        search_data.data = new ArrayList<Map<String, String>>();

        //Set recycler view adapter and manager for search results
        // Lookup the recyclerview in activity layout
        s_result_box = (RecyclerView) view.findViewById(R.id.search_recycler);
        s_result_box.setNestedScrollingEnabled(true);

        // Create adapter passing in the empty data object
        s_adapter = new SearchAdapter(search_data.data, getContext(), new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Map<String, String> item) {
                switchDetails(item);
            }
        });
        // Attach the adapter to the recyclerview to populate items
        s_result_box.setAdapter(s_adapter);
        // Set layout manager to position the items
        s_result_box.setLayoutManager(new LinearLayoutManager(getContext()));
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


    private void updateQueryResults(String response){
        //Parse response
        Gson gson = new Gson();
        try{
            search_data = gson.fromJson(response, SearchData.class);
        } catch (Exception error){
            Log.e("GSON error", error.toString());
        }
        if(search_data.data.size() == 0){
            getActivity().findViewById(R.id.s_no_result).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.search_recycler).setVisibility(View.GONE);
        }
        else{
            getActivity().findViewById(R.id.s_no_result).setVisibility(View.GONE);
            getActivity().findViewById(R.id.search_recycler).setVisibility(View.VISIBLE);
        }
        s_adapter.updateItems(search_data.data);

    }

    private void handle_query(String query_text){
        // If length of text is greater than 0, then we send query to backend
        if(query_text.length() > 0){
            RequestQueue queue = Volley.newRequestQueue(getContext());
            String url ="http://10.0.2.2:8080/multisearch/";
            url += Uri.encode(query_text);

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            updateQueryResults(response);
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

    public void init_search_view(View view){
        SearchView mySearchView = (SearchView) view.findViewById(R.id.search_view);
        mySearchView.setQueryHint("Search movies and TV");
        mySearchView.setIconifiedByDefault(false);
        mySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // do something when text changes
                handle_query(newText);
                return false;
            }
        });
        mySearchView.requestFocus();
    }




}