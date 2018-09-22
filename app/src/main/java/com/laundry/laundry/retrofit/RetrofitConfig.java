package com.laundry.laundry.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Aditya Kulkarni
 */

public class RetrofitConfig {

    public Retrofit config() {

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.
                addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);

        Gson gson = new GsonBuilder().create();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(URLConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = retrofitBuilder
                .client(clientBuilder.build())
                .build();

        return retrofit;
    }
}
