package com.example.m_feelm;

import java.util.HashMap;
import java.util.Map;

public class Review {
    public String id;
    public String title;
    public String watch_date;
    public float rating;
    public String user_review;
    //public boolean feedYN;
    public String write_date;
    public String movieCode;
    public String posterUrl;
    public String withPeople;
    public String time;


    public Review(){
        // Default constructor required for calls to DataSnapshot.getValue(FirebasePost.class)
    }

    public Review(String id, String title,String watch_date,float rating,String user_review,String write_date,String movieCode,String posterUrl,String withPeople,String time) {
        this.id = id;
        this.title = title;
        this.watch_date = watch_date;
        this.rating = rating;
        this.user_review = user_review;
        //this.feedYN = feedYN;
        this.write_date = write_date;
        this.movieCode=movieCode;
        this.posterUrl=posterUrl;
        this.withPeople=withPeople;
        this.time=time;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("title", title);
        result.put("watch_date", watch_date);
        result.put("rating", rating);
        result.put("user_review", user_review);
        //result.put("feedYN", feedYN);
        result.put("write_date", write_date);
        result.put("movieCode", movieCode);
        result.put("posterUrl",posterUrl);
        return result;
    }
}

