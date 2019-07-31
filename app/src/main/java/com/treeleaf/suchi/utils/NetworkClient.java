package com.treeleaf.suchi.utils;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.protobuf.ProtoConverterFactory;

public class NetworkClient {


    private static final String BASE_URL = "https://api.savx.com";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitClient(Context context) {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(ProtoConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
