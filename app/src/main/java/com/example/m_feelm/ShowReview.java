package com.example.m_feelm;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class ShowReview extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_review);

        String mtitle=getIntent().getStringExtra("title");
        String review=getIntent().getStringExtra("review");
        String writeDate=getIntent().getStringExtra("writeDate");
        String watchDate=getIntent().getStringExtra("watchDate");
        String mposter=getIntent().getStringExtra("poster");
        String people=getIntent().getStringExtra("withPeople");
        String mrating=getIntent().getStringExtra("rating");

        TextView m_title = findViewById(R.id.title);
        RatingBar m_rating= findViewById(R.id.user_rating);
        TextView m_review = findViewById(R.id.review);
        TextView m_ratingNum=findViewById(R.id.user_rating_num);
        TextView m_withPeople=findViewById(R.id.withPeople);
        TextView m_watchDate=findViewById(R.id.watchDate);
        TextView m_writeDate=findViewById(R.id.writeDate);

        System.out.println(mrating);
        m_title.setText(mtitle);
        m_review.setText(review);

        m_rating.setRating(Float.parseFloat(mrating) / 2);
        String ratingNum = String.format("%.1f", Float.parseFloat(mrating));

        m_ratingNum.setText(ratingNum);

        m_withPeople.setText(people);
        m_watchDate.setText(watchDate);
        m_writeDate.setText(writeDate);

    }
}
