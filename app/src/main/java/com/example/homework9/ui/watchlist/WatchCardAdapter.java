package com.example.homework9.ui.watchlist;


import android.content.Context;
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
import java.util.Map;


public class WatchCardAdapter extends RecyclerView.Adapter<WatchCardAdapter.ViewHolder> {
    //Adapter variables
    private ArrayList<Map<String, String>> localDataSet;
    private Context app_context;
    private final OnItemClickListener listener;
    private WatchHolder watch_holder;

    //Interface for onclick listener
    public interface OnItemClickListener {
        void onItemClick(Map<String, String> item);
    }

    // Update slider
    public void updateItems(ArrayList<Map<String, String>> watchItems) {
        this.localDataSet = watchItems;
        notifyDataSetChanged();
    }

    // Update slider
    public void updateItems() {
        notifyDataSetChanged();
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

    public WatchCardAdapter(ArrayList<Map<String, String>> dataSet, Context a_context, OnItemClickListener onClickListener) {
        localDataSet = dataSet;
        listener = onClickListener;
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
                listener.onItemClick(localDataSet.get(position));
            }
        });

        //Bind click listener to watchbutton view
        viewHolder.getRemove().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Remove item from watchlist in shared preferences
                String watch_key = watch_holder.getWatchKey(localDataSet.get(position));
                watch_holder.toggleWatchList(watch_key);
                // Remove item from data arraylist in adapter
                localDataSet.remove(position);
                updateItems();

                //Display toast
                String watch_toast = localDataSet.get(position).get("title") +  " was removed from favorites";
                Toast.makeText(app_context, watch_toast, Toast.LENGTH_LONG).show();
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}

