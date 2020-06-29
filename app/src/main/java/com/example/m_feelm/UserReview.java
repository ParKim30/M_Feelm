package com.example.m_feelm;

public class UserReview implements Comparable<UserReview>{
    private String title;
    private float rating;
    private String id;
    //private boolean FeedYN;
    private String UserReview;
    private String watchDate;
    private String writeDate;
    private String movieCode;
    private String posterUrl;
    private String withPeople;

    public UserReview(){}
    public UserReview(String title, float rating, String Uid, boolean feedYN, String UserReview, String watchDate, String writeDate,String movieCode,String posterUrl,String withPeople){
        this.title=title;
        this.rating=rating;
        this.id=Uid;
        //this.FeedYN=feedYN;
        this.UserReview=UserReview;
        this.watchDate=watchDate;
        this.writeDate=writeDate;
        this.movieCode=movieCode;
        this.posterUrl=posterUrl;
        this.withPeople=withPeople;
    }

    public void setMovieCd(String movieCd) {
        this.movieCode = movieCode;
    }

    public String getMovieCode() {
        return movieCode;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    /*public void setFeedYN(boolean feedYN) {
        FeedYN = feedYN;
    }*/

    public void setWithPeople(String withPeople) {
        this.withPeople = withPeople;
    }

    public String getWithPeople() {
        return withPeople;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUserReview(String userReview) {
        UserReview = userReview;
    }

    public void setWatch_Date(String watch_Date) {
        this.watchDate = watch_Date;
    }

    public void setWrite_Date(String write_Date) {
        this.writeDate = write_Date;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public float getRating() {
        return rating;
    }

    public String getUserReview() {
        return UserReview;
    }

    public String getWatchDate() {
        return watchDate;
    }

    public String getWriteDate() {
        return writeDate;
    }

    @Override
    public int compareTo(UserReview review) {
        float targetAge = review.getRating();
        if(rating == targetAge) return 0;
        else if(rating > targetAge) return 1;
        else return -1;
        //return Float.compare(rating, targetAge);
    }
}
