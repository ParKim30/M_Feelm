package com.example.m_feelm;

import com.example.m_feelm.model.Movie;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ApiInterface {
    @Headers({"X-Naver-Client-Id:_AAJI05zUe8pmg6pQ7bF", "X-Naver-Client-Secret:UO3vEbCloP"})
    @GET("movie.json")
    Call<Movie> getMovies(@Query("query") String title,
                          @Query("display") int displaySize,
                          @Query("start") int startPosition);
}
