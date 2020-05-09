package com.example.m_feelm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class WriteReview extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_review);

        String title=getIntent().getStringExtra("title");
        String rating=getIntent().getStringExtra("rating");
        String director=getIntent().getStringExtra("director");
        String actor=getIntent().getStringExtra("actor");
        String date=getIntent().getStringExtra("date");
        String poster=getIntent().getStringExtra("poster");

        TextView m_title = findViewById(R.id.title);
        TextView m_director = findViewById(R.id.director);
        TextView m_actor = findViewById(R.id.actor);
        RatingBar m_rating= findViewById(R.id.user_rating);
        TextView m_date = findViewById(R.id.pubDate);
        ImageView m_poster=findViewById(R.id.moviePoster);

        m_title.setText(title);
        m_director.setText(director);
        m_actor.setText(actor);
        m_rating.setRating(Float.parseFloat(rating) / 2);
        m_date.setText(date);

        Glide.with(this)
                .load(poster)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(m_poster);

    }
}
