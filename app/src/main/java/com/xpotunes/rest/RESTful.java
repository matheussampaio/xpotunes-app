package com.xpotunes.rest;

import com.xpotunes.pojo.Music;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RESTful {

    public static final String URL_BASE = "http://xpotunes.herokuapp.com/api/";
    private static ApiService instance = null;

    private RESTful() {}

    public static ApiService getInstance() {
        if (instance == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL_BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            instance = retrofit.create(ApiService.class);
        }

        return instance;
    }

    public static void addView(String id) {
        Call<Music> musicCall = RESTful.getInstance().addView(id);

        musicCall.enqueue(new Callback<Music>() {
            @Override
            public void onResponse(Call<Music> call, Response<Music> response) {
            }

            @Override
            public void onFailure(Call<Music> call, Throwable t) {

            }
        });
    }

    public static void addTrailerView(String id) {
        Call<Music> musicCall = RESTful.getInstance().addTrailerView(id);

        musicCall.enqueue(new Callback<Music>() {
            @Override
            public void onResponse(Call<Music> call, Response<Music> response) {
            }

            @Override
            public void onFailure(Call<Music> call, Throwable t) {

            }
        });
    }
}
