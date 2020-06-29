package com.example.m_feelm;

import java.util.HashMap;
import java.util.Map;

public class Review {
    public String id;
    public String title;
    public String watchDate;
    public float rating;
    public String userReview;
    //public boolean feedYN;
    public String writeDate;
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
        this.watchDate = watch_date;
        this.rating = rating;
        this.userReview = user_review;
        //this.feedYN = feedYN;
        this.writeDate = write_date;
        this.movieCode=movieCode;
        this.posterUrl=posterUrl;
        this.withPeople=withPeople;
        this.time=time;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("title", title);
        result.put("watchDate", watchDate);
        result.put("rating", rating);
        result.put("userReview", userReview);
        //result.put("feedYN", feedYN);
        result.put("writeDate", writeDate);
        result.put("movieCode", movieCode);
        result.put("posterUrl",posterUrl);
        return result;
    }
}

