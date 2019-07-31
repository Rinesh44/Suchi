package com.treeleaf.suchi.dagger.component.module;

import com.treeleaf.suchi.BuildConfig;
import com.treeleaf.suchi.api.Endpoints;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.protobuf.ProtoConverterFactory;

import static com.treeleaf.suchi.api.Endpoints.API_BASE_URL;

@Module
public class NetModule {

    @Provides
    @Singleton
    OkHttpClient getOkHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BuildConfig.DEBUG ?
                HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request.Builder requestBuilder = chain.request().newBuilder();
                        requestBuilder.header("Content-Type", "application/protobuf");
                        requestBuilder.header("Accept", "application/protobuf");
                        return chain.proceed(requestBuilder.build());
                    }
                })
                .writeTimeout(3, TimeUnit.MINUTES);

        okHttpClient.addInterceptor(logging);
        return okHttpClient.build();
    }

    @Provides
    @Singleton
    Retrofit getRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(ProtoConverterFactory.create())
                .baseUrl(API_BASE_URL)
                .build();
    }

    @Provides
    @Singleton
    Endpoints getWebService(Retrofit retrofit) {
        return retrofit.create(Endpoints.class);
    }
}