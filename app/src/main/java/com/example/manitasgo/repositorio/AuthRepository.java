package com.example.manitasgo.repositorio;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.manitasgo.BuildConfig;
import com.example.manitasgo.api.AuthService;
import com.example.manitasgo.api.SupabaseClient;
import com.example.manitasgo.api.UsuarioService;
import com.example.manitasgo.modelo.AuthRequest;
import com.example.manitasgo.modelo.AuthResponse;
import com.example.manitasgo.modelo.Usuario;
import com.example.manitasgo.util.SessionManager;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthRepository {

    private static final String TAG = "AUTH_DEBUG";

    private final AuthService authService;
    private final UsuarioService usuarioService;
    private final SessionManager session;

    public AuthRepository(Context context) {
        session = new SessionManager(context);

        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit authRetrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SUPABASE_URL + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        authService    = authRetrofit.create(AuthService.class);
        usuarioService = SupabaseClient.getInstance().createService(UsuarioService.class);

        Log.d(TAG, "AuthRepository creado. URL base: " + BuildConfig.SUPABASE_URL);
    }

    // ── REGISTRO ────────────────────────────────────────────────────────────
    public void registrar(String email, String password,
                          String nombre, String telefono, String tipo,
                          MutableLiveData<String>  error,
                          MutableLiveData<Boolean> exito) {

        Log.d(TAG, "registrar() llamado con email: " + email + ", tipo: " + tipo);

        AuthRequest req = new AuthRequest(email, password);
        authService.registrar(BuildConfig.SUPABASE_ANON_KEY, req)
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> resp) {
                        Log.d(TAG, "registrar onResponse - código: " + resp.code());

                        try {
                            String rawError = resp.errorBody() != null ? resp.errorBody().string() : "sin errorBody";
                            Log.d(TAG, "registrar errorBody: " + rawError);
                        } catch (Exception e) {
                            Log.e(TAG, "Error leyendo errorBody: " + e.getMessage());
                        }

                        if (!resp.isSuccessful() || resp.body() == null) {
                            Log.e(TAG, "registrar - respuesta no exitosa o body null");
                            error.postValue("Error en registro: " + resp.code());
                            return;
                        }

                        AuthResponse body  = resp.body();
                        String userId      = body.user != null ? body.user.id : null;
                        String accessToken = body.accessToken;

                        Log.d(TAG, "registrar - userId: " + userId + ", accessToken null? " + (accessToken == null));

                        if (userId == null || accessToken == null) {
                            error.postValue("Respuesta de registro incompleta.");
                            return;
                        }

                        SupabaseClient.getInstance().setAccessToken(accessToken);
                        session.guardarSesion(accessToken, userId, tipo, nombre);

                        Usuario u = new Usuario(userId, nombre, telefono, tipo);
                        Log.d(TAG, "registrar - insertando usuario en tabla usuarios");

                        usuarioService.crear(u).enqueue(new Callback<List<Usuario>>() {
                            @Override
                            public void onResponse(Call<List<Usuario>> call2,
                                                   Response<List<Usuario>> r2) {
                                Log.d(TAG, "crear usuario onResponse - código: " + r2.code());
                                if (r2.isSuccessful()) {
                                    exito.postValue(true);
                                } else {
                                    try {
                                        String err = r2.errorBody() != null ? r2.errorBody().string() : "sin errorBody";
                                        Log.e(TAG, "crear usuario error body: " + err);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error leyendo errorBody crear: " + e.getMessage());
                                    }
                                    error.postValue("Error creando perfil: " + r2.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Usuario>> call2, Throwable t) {
                                Log.e(TAG, "crear usuario onFailure: " + t.getMessage(), t);
                                error.postValue("Error de red al guardar perfil: " + t.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        Log.e(TAG, "registrar onFailure: " + t.getMessage(), t);
                        error.postValue("Error de red: " + t.getMessage());
                    }
                });
    }

    // ── LOGIN ────────────────────────────────────────────────────────────────
    public void login(String email, String password,
                      MutableLiveData<String> error,
                      MutableLiveData<String> tipoUsuario) {

        Log.d(TAG, "login() llamado con email: " + email);

        AuthRequest req = new AuthRequest(email, password);
        authService.login(BuildConfig.SUPABASE_ANON_KEY, req)
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> resp) {
                        Log.d(TAG, "login onResponse - código: " + resp.code());

                        try {
                            String rawError = resp.errorBody() != null ? resp.errorBody().string() : "sin errorBody";
                            Log.d(TAG, "login errorBody: " + rawError);
                        } catch (Exception e) {
                            Log.e(TAG, "Error leyendo errorBody login: " + e.getMessage());
                        }

                        if (!resp.isSuccessful() || resp.body() == null) {
                            Log.e(TAG, "login - respuesta no exitosa o body null");
                            error.postValue("Email o contraseña incorrectos.");
                            return;
                        }

                        AuthResponse body  = resp.body();
                        String userId      = body.user != null ? body.user.id : null;
                        String accessToken = body.accessToken;

                        Log.d(TAG, "login - userId: " + userId + ", accessToken null? " + (accessToken == null));

                        if (userId == null || accessToken == null) {
                            error.postValue("Respuesta de login incompleta.");
                            return;
                        }

                        SupabaseClient.getInstance().setAccessToken(accessToken);

                        usuarioService.getPerfil("eq." + userId)
                                .enqueue(new Callback<List<Usuario>>() {
                                    @Override
                                    public void onResponse(Call<List<Usuario>> c2,
                                                           Response<List<Usuario>> r2) {
                                        Log.d(TAG, "getPerfil onResponse - código: " + r2.code());
                                        if (r2.isSuccessful() && r2.body() != null
                                                && !r2.body().isEmpty()) {
                                            Usuario u = r2.body().get(0);
                                            session.guardarSesion(accessToken, userId,
                                                    u.tipo, u.nombre);
                                            tipoUsuario.postValue(u.tipo);
                                        } else {
                                            Log.e(TAG, "getPerfil - lista vacía o error");
                                            error.postValue("No se encontró el perfil de usuario.");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<Usuario>> c2, Throwable t) {
                                        Log.e(TAG, "getPerfil onFailure: " + t.getMessage(), t);
                                        error.postValue("Error cargando perfil: " + t.getMessage());
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        Log.e(TAG, "login onFailure: " + t.getMessage(), t);
                        error.postValue("Error de red: " + t.getMessage());
                    }
                });
    }

    // ── LOGOUT ───────────────────────────────────────────────────────────────
    public void logout() {
        String token = session.getToken();
        if (token != null) {
            authService.logout(BuildConfig.SUPABASE_ANON_KEY, "Bearer " + token)
                    .enqueue(new Callback<Void>() {
                        @Override public void onResponse(Call<Void> c, Response<Void> r) {}
                        @Override public void onFailure(Call<Void> c, Throwable t) {}
                    });
        }
        SupabaseClient.getInstance().clearToken();
        session.cerrarSesion();
    }

    public SessionManager getSession() { return session; }
}