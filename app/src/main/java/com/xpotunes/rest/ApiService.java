package com.xpotunes.rest;

import com.xpotunes.pojo.Music;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("music/random")
    Call<List<Music>> getRandomMusic();

    @POST("music/{id}/view")
    Call<Music> addView(@Path("id") String musicId);

    @POST("music/{id}/trailer")
    Call<Music> addTrailerView(@Path("id") String musicId);

    @POST("music/{id}/like")
    Call<Music> addLike(@Path("id") String musicId);

    @POST("music/{id}/dislike")
    Call<Music> addDislike(@Path("id") String musicId);

    @GET("music/random")
    Call<List<Music>> getRandomMusic(@Query("genre") String selectedGenre);
}