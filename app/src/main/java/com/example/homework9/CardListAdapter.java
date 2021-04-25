package com.example.homework9;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.ViewHolder> {


    //Adapter variables
    private ArrayList<Map<String, String>> localDataSet;
    private Context app_context;
    private final OnItemClickListener listener;
    private final WatchHolder watch_holder;

    //Interface for onclick listener
    public interface OnItemClickListener{
        void onItemClick(Map<String, String> item);
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ImageView poster_view;
        private final ImageView m_icon_view;
        private final View gradient_view;

        public ViewHolder(View view){
            super(view);

            poster_view = (ImageView) view.findViewById(R.id.card_poster);
            gradient_view = view.findViewById(R.id.c_gradient);
            m_icon_view = (ImageView) view.findViewById(R.id.menu_icon);
        }

        public ImageView getPoster() {
            return poster_view;
        }
        public View getGradient() {
            return gradient_view;
        }
        public ImageView getMIcon() {
            return m_icon_view;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public CardListAdapter(ArrayList<Map<String, String>> dataSet,  Context a_context, OnItemClickListener onClickListener) {
        localDataSet = dataSet;
        listener = onClickListener;
        app_context = a_context;
        watch_holder = new WatchHolder(a_context);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_list_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and set the views
        Glide.with(app_context).load(localDataSet.get(position).get("poster_path")).into(viewHolder.getPoster());


        //Bind click listener to viewholder view
        viewHolder.getGradient().setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v) {
                listener.onItemClick(localDataSet.get(position));
            }
        });

        viewHolder.getMIcon().setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                // Initializing the popup menu and giving the reference as current context
                PopupMenu popupMenu = new PopupMenu(app_context, viewHolder.getMIcon());

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.card_menu, popupMenu.getMenu());
                Boolean isInWatch = watch_holder.isInWatchList(localDataSet.get(position));
                final String watch_toast;
                if(isInWatch){
                    popupMenu.getMenu().findItem(R.id.add_watchlist).setTitle("Remove from Watchlist");
                    watch_toast = localDataSet.get(position).get("title") +  " was removed from Watchlist";
                }
                else{
                    popupMenu.getMenu().findItem(R.id.add_watchlist).setTitle("Add to Watchlist");
                    watch_toast = localDataSet.get(position).get("title") + " was added to Watchlist";
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch(menuItem.getItemId()){
                            case R.id.share_twitter:
                                postTwitter(localDataSet.get(position).get("tmdb_url"));
                                break;
                            case R.id.open_tmdb:
                                openTMDB(localDataSet.get(position).get("tmdb_url"));
                                break;
                            case R.id.share_facebook:
                                postFacebook(localDataSet.get(position).get("tmdb_url"));
                                break;
                            case R.id.add_watchlist:
                                watch_holder.toggleWatchList(localDataSet.get(position));
                                Toast.makeText(app_context, watch_toast,
                                        Toast.LENGTH_LONG).show();
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();
            }
        });
    }


    //Methods for social media and web access

    public void openTMDB(String tmdb_url){

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tmdb_url));
        app_context.startActivity(browserIntent);
    }
    public void postTwitter(String tmdb_url){
        String my_url = "https://twitter.com/intent/tweet?text=" + Uri.encode("Check this out!") + " %0D%0A";
        my_url += Uri.encode(tmdb_url);

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(my_url));
        app_context.startActivity(browserIntent);
    }
    public void postFacebook(String tmdb_url){
        String my_str = "https://www.facebook.com/sharer/sharer.php?u=";
        my_str += Uri.encode(tmdb_url);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(my_str));
        app_context.startActivity(browserIntent);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}

