package com.example.car_rent.api;

import com.example.car_rent.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {

            // 1. On configure OkHttpClient pour Ngrok
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request request = chain.request().newBuilder()
                                // Ce header indique à Ngrok de ne pas afficher sa page d'avertissement
                                .addHeader("ngrok-skip-browser-warning", "true")
                                .build();
                        return chain.proceed(request);
                    })
                    .build();

            // 2. On initialise Retrofit
            retrofit = new Retrofit.Builder()
                    // L'URL vient de gradle.properties via BuildConfig
                    .baseUrl(BuildConfig.BASE_URL + "/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}