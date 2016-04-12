package com.xpotunes.rest;

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
}
