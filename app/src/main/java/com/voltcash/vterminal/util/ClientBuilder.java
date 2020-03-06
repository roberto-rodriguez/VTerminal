package com.voltcash.vterminal.util;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by roberto.rodriguez on 3/6/2020.
 */

public class ClientBuilder {

    public static Retrofit build(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

         return new Retrofit
                .Builder()
                .baseUrl(Settings.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }
}
