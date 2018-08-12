package com.jubi.ai.chatbot.networking;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class APIClient {

    private static APIService apiService;

    public static APIService getAPIService(String host) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .addHeader("Accept", "application/json");
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };

        if (apiService == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.interceptors().add(interceptor);
            httpClient.interceptors().add(httpLoggingInterceptor);
            Retrofit.Builder builder =
                    new Retrofit.Builder()
                            .baseUrl(host)
                            .addConverterFactory(
                                    GsonConverterFactory.create()
                            );
            Retrofit retrofit = builder.client(httpClient.build()).build();
            apiService = retrofit.create(APIService.class);
        }
        return apiService;
    }
}