package com.example.homework9;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.example.homework9.ui.home.BlurTransformation;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder> {

    // list for storing urls of images.
    private ArrayList<Map<String, String>> mSliderItems;
    private Context app_context;
    private final SliderAdapter.OnItemClickListener listener;

    //Interface for onclick listener
    public interface OnItemClickListener{
        void onItemClick(Map<String, String> item);
    }

    // Constructor
    public SliderAdapter(Context context, ArrayList<Map<String, String>> sliderDataArrayList, SliderAdapter.OnItemClickListener onClickListener) {
        this.mSliderItems = sliderDataArrayList;
        this.app_context = context;
        listener = onClickListener;
    }

    // Update slider
    public void updateItems(ArrayList<Map<String, String>> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    // We are inflating the slider_layout
    // inside on Create View Holder method.
    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout, null);
        return new SliderAdapterViewHolder(inflate);
    }

    // Inside on bind view holder we will
    // set data to item of Slider View.
    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, final int position) {

        final Map<String, String> sliderItem = mSliderItems.get(position);

        // Glide is use to load image
        // from url in your imageview.
        Glide.with(viewHolder.itemView)
                .load(sliderItem.get("poster_path"))
                .fitCenter()
                .into(viewHolder.mainImageView);

        // Set blurred image
        Glide.with(viewHolder.itemView)
                .load(sliderItem.get("poster_path"))
                .fitCenter()
                .transform(new BlurTransformation(app_context))
                .into(viewHolder.blurredImageView);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v) {
                listener.onItemClick(sliderItem);
            }
        });
    }

    // this method will return
    // the count of our list.
    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    static class SliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {
        // Adapter class for initializing
        // the views of our slider view.
        View itemView;
        ImageView mainImageView;
        ImageView blurredImageView;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            mainImageView= itemView.findViewById(R.id.myimage);
            blurredImageView=itemView.findViewById(R.id.blurred_image);
            this.itemView = itemView;
        }
    }
}