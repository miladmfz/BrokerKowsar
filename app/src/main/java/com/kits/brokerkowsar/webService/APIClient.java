package com.kits.brokerkowsar.webService;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class APIClient {

    private static Retrofit retrofit = null;
    public static String apiBaseUrl = "";

    public static Retrofit getCleint(String BASE_URL) {
        apiBaseUrl=BASE_URL;
        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(apiBaseUrl)
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                    .build();
        } else {
        if (!retrofit.baseUrl().equals(BASE_URL)) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                    .build();
        }
    }
        return retrofit;
    }


}
