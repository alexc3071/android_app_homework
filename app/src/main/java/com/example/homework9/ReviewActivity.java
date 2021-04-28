package com.example.homework9;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReviewActivity extends AppCompatActivity {

    String author=null;
    String content=null;
    String raw_date=null;
    String rating=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Homework9);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        //Get parameters passed in
        author = getIntent().getStringExtra("author");
        content = getIntent().getStringExtra("content");
        raw_date = getIntent().getStringExtra("date");
        rating = getIntent().getStringExtra("rating");

        String first_line = "by " + author;
        if(raw_date != null){
            String[] split_date = raw_date.split("-");
            Log.d("year", split_date[0]);
            Date s_raw_date = new Date(Integer.valueOf(split_date[0]) - 1900, Integer.valueOf(split_date[1]) - 1, Integer.valueOf(split_date[2]));
            DateFormat df = new SimpleDateFormat("E, MMM dd yyyy");
            String my_date = df.format(s_raw_date);
            first_line += " on " +  my_date;
        }
        TextView first_view = (TextView) findViewById(R.id.r_first_line);
        first_view.setText(first_line);
        if(rating != null){
            TextView rating_view = (TextView) findViewById(R.id.r_rating_val);
            rating_view.setText(rating + "/5");
        }
        if(content != null){
            TextView content_view = (TextView) findViewById(R.id.r_content);
            content_view.setText(content);
        }
    }
}