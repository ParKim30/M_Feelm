package com.example.m_feelm;

public class StatisticMovieItem {
    private int rank;
    private String movieNm;
    public int getRank() {
        return rank;
    }
    public String getMovieNm(){
        return movieNm;
    }
    public void setMovieNm(String title){
        this.movieNm=title;
    }
    public StatisticMovieItem(String title){
        movieNm = title;
    }

}
