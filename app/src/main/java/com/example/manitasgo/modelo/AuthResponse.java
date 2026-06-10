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

    public UserData user;

    public static class UserData {
        public String id;
        public String email;
        public String role;
    }
}
