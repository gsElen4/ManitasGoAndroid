package com.example.manitasgo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.manitasgo.api.SupabaseClient;
import com.example.manitasgo.util.SessionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager session = new SessionManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (session.haySesion()) {
                // Restaurar token en el cliente HTTP
                SupabaseClient.getInstance().setAccessToken(session.getToken());
                if (session.esVendedor()) {
                    intent = new Intent(this, VendedorActivity.class);
                } else {
                    intent = new Intent(this, ClienteActivity.class);
                }
            } else {
                intent = new Intent(this, AuthActivity.class);
            }
            startActivity(intent);
            finish();
        }, 1200);
    }
}
