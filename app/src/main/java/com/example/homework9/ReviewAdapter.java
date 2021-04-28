package com.example.homework9;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    //Interface for onclick listener
    interface OnItemClickListener{
        void onItemClick(Map<String, String> item);
    }

    private ArrayList<Map<String, String>> localDataSet;
    private final OnItemClickListener listener;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView first_line;
        private final TextView content;
        private final TextView rating;
        private final ImageView star_icon;

        public ViewHolder(View view){
            super(view);

            first_line = (TextView) view.findViewById(R.id.first_line);
            content = (TextView) view.findViewById(R.id.review_content);
            rating = (TextView) view.findViewById(R.id.rating_val);
            star_icon = (ImageView) view.findViewById(R.id.star_icon);
        }

        public TextView getFirstLine() {
            return first_line;
        }
        public TextView getContent() {
            return content;
        }
        public ImageView getStar() {
            return star_icon;
        }
        public TextView getRating() {
            return rating;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public ReviewAdapter(ArrayList<Map<String, String>> dataSet, OnItemClickListener onClickListener) {
        localDataSet = dataSet;
        listener = onClickListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.review_card, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // Set text for first line
        String first_line = "by " + localDataSet.get(position).get("author");
        String raw_date =  localDataSet.get(position).get("date");
        if(raw_date != null){
            String[] split_date = raw_date.split("-");
            Date s_raw_date = new Date(Integer.valueOf(split_date[0]) - 1900, Integer.valueOf(split_date[1])-1, Integer.valueOf(split_date[2]));
            DateFormat df = new SimpleDateFormat("E, MMM dd yyyy");
            String my_date = df.format(s_raw_date);
            first_line += " on " +  my_date;
        }

        viewHolder.getFirstLine().setText(first_line);

        //Set text for rating
        String rating = localDataSet.get(position).get("rating");
        if(rating != null) {
            String my_rating = rating + "/5";
            viewHolder.getRating().setText(my_rating);
        }
        else{
            viewHolder.getRating().setVisibility(View.GONE);
            viewHolder.getStar().setVisibility(View.GONE);
        }

        //Set text for content
        String content = localDataSet.get(position).get("content");
        if(content != null) {
            viewHolder.getContent().setText(content);
        }
        else{
            viewHolder.getContent().setVisibility(View.GONE);
        }
        //Bind click listener to viewholder view
        viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v) {
                listener.onItemClick(localDataSet.get(position));
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
