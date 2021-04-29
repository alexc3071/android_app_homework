package com.example.homework9.ui.watchlist;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homework9.R;
import com.example.homework9.WatchHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;


public class WatchCardAdapter extends RecyclerView.Adapter<WatchCardAdapter.ViewHolder> {
    //Adapter variables
    private ArrayList<Map<String, String>> localDataSet;
    private Context app_context;
    private final OnItemClickListener listener_item;
    private final OnNumClickListener listener_num;
    private WatchHolder watch_holder;

    //Interface for onclick listener1
    public interface OnItemClickListener {
        void onItemClick(Map<String, String> item);
    }

    //Interface for onclick listener2
    public interface OnNumClickListener {
        void onItemClick(int num);
    }

    // Update watchlist stored in shared preferences
    public void updatePreferences(){
        watch_holder.saveUpdatedList(localDataSet);
    }

    //A method to swap items for the drag and drop functionality
    public void moveItems(int position1, int position2){
        Map<String, String> item = localDataSet.remove(position1);
        localDataSet.add(position2, item);
        updatePreferences();
    }

    // Update slider
    public void updateItems(ArrayList<Map<String, String>> watchItems) {
        this.localDataSet = watchItems;
        listener_num.onItemClick(localDataSet.size());
        notifyDataSetChanged();
    }

    // Update slider
    public void updateItems() {
        listener_num.onItemClick(localDataSet.size());
        notifyDataSetChanged();
    }

    public int getNumItems(){
        return this.localDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView poster_view;
        private final ImageView remove_view;
        private final View gradient_view;
        private final TextView type_view;

        public ViewHolder(View view) {
            super(view);

            poster_view = (ImageView) view.findViewById(R.id.w_poster);
            remove_view = (ImageView) view.findViewById(R.id.w_remove_icon);
            gradient_view = view.findViewById(R.id.w_gradient);
            type_view = (TextView) view.findViewById(R.id.w_type);
        }

        public ImageView getPoster() {
            return poster_view;
        }

        public ImageView getRemove() {
            return remove_view;
        }

        public View getGradient() {
            return gradient_view;
        }

        public TextView getType() {
            return type_view;
        }
    }

    public WatchCardAdapter(ArrayList<Map<String, String>> dataSet, Context a_context, OnItemClickListener onItemClickListener, OnNumClickListener onNumClickListener) {
        localDataSet = dataSet;
        listener_item = onItemClickListener;
        listener_num = onNumClickListener;
        app_context = a_context;
        watch_holder = new WatchHolder(app_context);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.watch_card, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and set the views
        Glide.with(app_context).load(localDataSet.get(position).get("poster_path")).into(viewHolder.getPoster());
        //Set text for rating
        String media_type = localDataSet.get(position).get("media_type");
        if ("movie".equals(media_type)) {
            viewHolder.getType().setText("Movie");
        } else {
            viewHolder.getType().setText("TV");
        }

        //Bind click listener to viewholder view
        viewHolder.getGradient().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener_item.onItemClick(localDataSet.get(position));
            }
        });

        //Bind click listener to watchbutton view
        viewHolder.getRemove().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Remove item from watchlist in shared preferences
                String watch_key = watch_holder.getWatchKey(localDataSet.get(position));
                watch_holder.toggleWatchList(watch_key);

                //Display toast
                String watch_toast = localDataSet.get(position).get("title") +  " was removed from watchlist";
                Toast.makeText(app_context, watch_toast, Toast.LENGTH_LONG).show();
                localDataSet.remove(position);
                // Remove item from data arraylist in adapter
                updateItems();
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}

