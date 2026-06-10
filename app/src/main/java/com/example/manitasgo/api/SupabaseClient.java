package com.example.manitasgo.api;

import com.example.manitasgo.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SupabaseClient {

    private static SupabaseClient instance;
    private final Retrofit retrofit;
    private String accessToken = null; // se asigna tras login

    private SupabaseClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder()
                            .header("apikey", BuildConfig.SUPABASE_ANON_KEY)
                            .header("Content-Type", "application/json")
                            .header("Prefer", "return=representation");

                    if (accessToken != null) {
                        builder.header("Authorization", "Bearer " + accessToken);
                    } else {
                        builder.header("Authorization", "Bearer " + BuildConfig.SUPABASE_ANON_KEY);
                    }
                    return chain.proceed(builder.build());
                })
                .build();

        Gson gson = new GsonBuilder().setLenient().create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SUPABASE_URL + "/rest/v1/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();
    }

    public static synchronized SupabaseClient getInstance() {
        if (instance == null) instance = new SupabaseClient();
        return instance;
    }

    public <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }

    /** Llamar tras un login para incluir el JWT en las peticiones. */
    public void setAccessToken(String token) {
        this.accessToken = token;
    }

    public void clearToken() {
        this.accessToken = null;
    }

    public String getBaseUrl() {
        return BuildConfig.SUPABASE_URL;
    }
}
