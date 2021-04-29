package com.example.homework9.ui.watchlist;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homework9.DetailsActivity;
import com.example.homework9.R;
import com.example.homework9.WatchHolder;
import com.example.homework9.ui.search.SearchAdapter;
import com.example.homework9.ui.search.SearchFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class WatchlistFragment extends Fragment {

    private WatchlistViewModel watchlistViewModel;
    private WatchData watch_data;
    private WatchHolder watch_holder;
    private ItemTouchHelper itemTouchHelper;

    //Recycler variables
    private RecyclerView watch_box;
    WatchCardAdapter w_adapter;

    //Data types
    class WatchData {
        ArrayList<Map<String, String>> data;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        watchlistViewModel =
                new ViewModelProvider(this).get(WatchlistViewModel.class);
        View root = inflater.inflate(R.layout.fragment_watchlist, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Initialize card recycler views and associated items
        init_recyclers(view);
    }


    //Set up simple callback
            ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
            ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            w_adapter.moveItems(fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    @Override
    public void onResume(){
        super.onResume();
        //Initialize watchlist content
        init_watch_content();
    }

    private void init_recyclers(View view) {
        // Initialize data
        watch_data = new WatchData();
        watch_data.data = new ArrayList<Map<String, String>>();

        //Set recycler view adapter and manager for search results
        // Lookup the recyclerview in activity layout
        watch_box = (RecyclerView) view.findViewById(R.id.watch_recycler);
        watch_box.setNestedScrollingEnabled(true);

        // Create adapter passing in the empty data object
        w_adapter = new WatchCardAdapter(watch_data.data, getContext(), new WatchCardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Map<String, String> item) {
                switchDetails(item);
            }
        },
                new WatchCardAdapter.OnNumClickListener() {
                    @Override
                    public void onItemClick(int num) {
                        if (num == 0) {
                            view.findViewById(R.id.w_empty_message).setVisibility(View.VISIBLE);
                        } else {
                            view.findViewById(R.id.w_empty_message).setVisibility(View.GONE);
                        }
                    }
                });
        // Attach the adapter to the recyclerview to populate items
        watch_box.setAdapter(w_adapter);
        // Set layout manager to position the items
        watch_box.setLayoutManager(new GridLayoutManager(getContext(), 3));

        // Set up item touch helper for drag and drop feature
        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(watch_box);

    }

    // A method to switch to the details activity
    public void switchDetails(Map<String, String> item) {
        String media_type = item.get("media_type");
        String id = item.get("id");
        String poster_path = item.get("poster_path");
        String title = item.get("title");
        Intent dIntent = new Intent(getActivity(), DetailsActivity.class);
        dIntent.putExtra("media_type", media_type);
        dIntent.putExtra("id", id);
        dIntent.putExtra("poster_path", poster_path);
        dIntent.putExtra("title", title);
        startActivity(dIntent);
    }


    private void init_watch_content() {
        watch_holder = new WatchHolder(getContext());
        w_adapter.updateItems(watch_holder.getCurrentWatchlist());
    }

}