package com.example.m_feelm;

public class StatisticMovieItem {
    private String title;
    private String posters;

    public String getMovieNm(){
        return title;
    }
    public String getImage(){return posters;}
    public void setMovieNm(String title){
        this.title=title;
    }
//    public StatisticMovieItem(String title){
//        movieNm = title;
//    }
}
