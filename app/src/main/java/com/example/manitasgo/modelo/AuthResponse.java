package com.example.manitasgo.modelo;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {


    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("refresh_token")
    public String refreshToken;

    @SerializedName("token_type")
    public String tokenType;

    @SerializedName("expires_in")
    public int expiresIn;

    @SerializedName("user")
    public UserData user;

    public static class UserData {
        @SerializedName("id")
        public String id;

        @SerializedName("email")
        public String email;

        @SerializedName("role")
        public String role;
    }
}