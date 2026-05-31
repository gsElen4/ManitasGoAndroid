package com.example.manitasgo.api;

import com.example.manitasgo.model.AuthRequest;
import com.example.manitasgo.model.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**Llamadas a Supabase*/
public interface AuthService {

    @POST("auth/v1/signup")
    Call<AuthResponse> registrar(
            @Header("apikey") String apiKey,
            @Body AuthRequest body
    );

    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> login(
            @Header("apikey") String apiKey,
            @Body AuthRequest body
    );

    @POST("auth/v1/logout")
    Call<Void> logout(
            @Header("apikey") String apiKey,
            @Header("Authorization") String bearer
    );
}
