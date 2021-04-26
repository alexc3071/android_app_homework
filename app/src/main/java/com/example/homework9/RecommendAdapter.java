package com.example.homework9;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Map;

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.ViewHolder>{
    //Adapter variables
    private ArrayList<Map<String, String>> localDataSet;
    private Context app_context;
    private final OnItemClickListener listener;

    //Interface for onclick listener
    public interface OnItemClickListener{
        void onItemClick(Map<String, String> item);
    }

    // Update slider
    public void updateItems(ArrayList<Map<String, String>> recItems) {
        this.localDataSet = recItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ImageView poster_view;

        public ViewHolder(View view){
            super(view);

            poster_view = (ImageView) view.findViewById(R.id.r_poster);
        }

        public ImageView getPoster() {
            return poster_view;
        }

    }

    public RecommendAdapter(ArrayList<Map<String, String>> dataSet,  Context a_context, OnItemClickListener onClickListener) {
        localDataSet = dataSet;
        listener = onClickListener;
        app_context = a_context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recommended_card, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and set the views
        Glide.with(app_context).load(localDataSet.get(position).get("poster_path")).into(viewHolder.getPoster());

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
