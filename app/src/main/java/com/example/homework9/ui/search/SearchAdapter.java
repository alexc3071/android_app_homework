package com.example.homework9.ui.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homework9.R;

import java.util.ArrayList;
import java.util.Map;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{
    //Adapter variables
    private ArrayList<Map<String, String>> localDataSet;
    private Context app_context;
    private final OnItemClickListener listener;

    //Interface for onclick listener
    public interface OnItemClickListener{
        void onItemClick(Map<String, String> item);
    }

    // Update slider
    public void updateItems(ArrayList<Map<String, String>> searchItems) {
        this.localDataSet = searchItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ImageView backdrop_view;
        private final ImageView star_view;
        private final View gradient_view;
        private final TextView rating_view;
        private final TextView title_view;
        private final TextView type_year_view;

        public ViewHolder(View view){
            super(view);

            backdrop_view = (ImageView) view.findViewById(R.id.s_backdrop);
            star_view = (ImageView) view.findViewById(R.id.s_star_icon);
            gradient_view = view.findViewById(R.id.s_gradient);
            rating_view = (TextView) view.findViewById(R.id.s_rating);
            title_view = (TextView) view.findViewById(R.id.s_title);
            type_year_view = (TextView) view.findViewById(R.id.s_type_year);
        }

        public ImageView getBackdrop() {
            return backdrop_view;
        }
        public ImageView getStar() {
            return star_view;
        }
        public View getGradient() {
            return gradient_view;
        }
        public TextView getRating() {
            return rating_view;
        }
        public TextView getTitle() {
            return title_view;
        }
        public TextView getTypeYear() {
            return type_year_view;
        }
    }

    public SearchAdapter(ArrayList<Map<String, String>> dataSet,  Context a_context, OnItemClickListener onClickListener) {
        localDataSet = dataSet;
        listener = onClickListener;
        app_context = a_context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.search_result_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and set the views
        Glide.with(app_context).load(localDataSet.get(position).get("backdrop_path")).into(viewHolder.getBackdrop());
        //Set text for rating
        String rating = localDataSet.get(position).get("rating");
        if(rating != null) {
            viewHolder.getRating().setText(rating);
        }
        else{
            viewHolder.getRating().setVisibility(View.GONE);
            viewHolder.getStar().setVisibility(View.GONE);
        }

        String title = localDataSet.get(position).get("name");
        if(title != null) {
            viewHolder.getTitle().setText(title);
        }
        else{
            viewHolder.getTitle().setVisibility(View.GONE);
        }

        String type_year = localDataSet.get(position).get("media_type");
        String year = localDataSet.get(position).get("year");
        if(year != null) {
            type_year += " " + year;
        }
        viewHolder.getTypeYear().setText(type_year);

        //Bind click listener to viewholder view
        viewHolder.getGradient().setOnClickListener(new View.OnClickListener(){
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
