package com.example.homework9;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WatchHolder {

    private static final String PREF_KEY = "MyPref";
    private static final String STR_KEY = "watchlist";
    private static final String FIELD_DELIMITER = "&_&";
    private static final String ITEM_DELIMITER = "#_#";
    private Context app_context;

    public WatchHolder(Context my_context){
        app_context = my_context;
    }

    private ArrayList<String> getShared(){
        SharedPreferences pref = app_context.getSharedPreferences(PREF_KEY, 0);
        String stringified_data = pref.getString(STR_KEY, null);
        if(stringified_data != null && stringified_data.length() > 0){
            String split_data[] = stringified_data.split(ITEM_DELIMITER);
            ArrayList<String> data_list = new ArrayList<String>();
            data_list = new ArrayList<String>(Arrays.asList(split_data));
            return data_list;
        }
        else{
            return new ArrayList<String>();
        }
    }

    private void setShared(ArrayList<String> data_list){
        SharedPreferences pref = app_context.getSharedPreferences(PREF_KEY, 0);
        SharedPreferences.Editor editor = pref.edit();
        if(data_list.size() > 0){
            String stringified_data = String.join(ITEM_DELIMITER, data_list);
            editor.putString(STR_KEY, stringified_data);
            editor.commit();
        }
        else{
            editor.putString(STR_KEY, "");
            editor.commit();
        }
    }

    public void toggleWatchList(Map<String, String> item){
        String item_key = getWatchKey(item);
        ArrayList<String> cur_watchlist = getShared();
        int index = cur_watchlist.indexOf(item_key);

        //If the item is currently in the watchlist
        if(index >= 0){
            cur_watchlist.remove(index);
            setShared(cur_watchlist);
        }
        //If the item is not currently in the watchlist
        else{
            cur_watchlist.add(item_key);
            setShared(cur_watchlist);
        }
    }

    public void toggleWatchList(String item_key){
        ArrayList<String> cur_watchlist = getShared();
        int index = cur_watchlist.indexOf(item_key);

        //If the item is currently in the watchlist
        if(index >= 0){
            cur_watchlist.remove(index);
            setShared(cur_watchlist);
        }
        //If the item is not currently in the watchlist
        else{
            cur_watchlist.add(item_key);
            setShared(cur_watchlist);
        }
    }

    public boolean isInWatchList(Map<String, String> item){
        String item_key = getWatchKey(item);
        ArrayList<String> cur_watchlist = getShared();
        int index = cur_watchlist.indexOf(item_key);

        return (index >= 0);
    }

    public boolean isInWatchList(String item_key){
        ArrayList<String> cur_watchlist = getShared();
        int index = cur_watchlist.indexOf(item_key);
        return (index >= 0);
    }

    public ArrayList<Map<String, String>> getCurrentWatchlist(){
        ArrayList<String> cur_str_watchlist = getShared();
        String[] map_keys = {"media_type", "id", "title", "poster_path"};
        ArrayList<Map<String, String>> cur_watchlist = new ArrayList<Map<String, String>>();
        for(int i = 0; i < cur_str_watchlist.size(); i++){
            String[] split_item = cur_str_watchlist.get(i).split(FIELD_DELIMITER);
            Map<String, String> item = new HashMap<String, String>();
            Log.d("what", cur_str_watchlist.get(i));
            item.put(map_keys[0], split_item[0]);
            item.put(map_keys[1], split_item[1]);
            item.put(map_keys[2], split_item[2]);
            item.put(map_keys[3], split_item[3]);
            cur_watchlist.add(item);
        }
        return cur_watchlist;

    }

    public String getWatchKey(Map<String, String> item){
        String item_key = item.get("media_type") + FIELD_DELIMITER + item.get("id") + FIELD_DELIMITER +
                item.get("title") + FIELD_DELIMITER +  item.get("poster_path");
        return item_key;
    }

}
