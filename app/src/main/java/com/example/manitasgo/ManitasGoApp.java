package com.example.manitasgo;

import android.app.Application;

import com.example.manitasgo.api.SupabaseClient;
import com.example.manitasgo.util.SessionManager;

public class ManitasGoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Si hay sesión guardada, restaurar el token en el cliente HTTP
        SessionManager session = new SessionManager(this);
        if (session.haySesion() && session.getToken() != null) {
            SupabaseClient.getInstance().setAccessToken(session.getToken());
        }
    }
}
