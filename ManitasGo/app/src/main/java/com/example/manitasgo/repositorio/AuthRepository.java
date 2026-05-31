package com.example.manitasgo.repositorio;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.manitasgo.BuildConfig;
import com.example.manitasgo.api.AuthService;
import com.example.manitasgo.api.SupabaseClient;
import com.example.manitasgo.api.UsuarioService;
import com.example.manitasgo.model.AuthRequest;
import com.example.manitasgo.model.AuthResponse;
import com.example.manitasgo.model.Usuario;
import com.example.manitasgo.util.SessionManager;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthRepository {

    private final AuthService authService;
    private final UsuarioService usuarioService;
    private final SessionManager session;

    public AuthRepository(Context context) {
        session = new SessionManager(context);

        // AuthService apunta a la raíz del proyecto, no a /rest/v1/
        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit authRetrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SUPABASE_URL + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        authService = authRetrofit.create(AuthService.class);
        usuarioService = SupabaseClient.getInstance().createService(UsuarioService.class);
    }

    // REGISTRO
    public void registrar(String email, String password,
                          String nombre, String telefono, String tipo,
                          MutableLiveData<String> error,
                          MutableLiveData<Boolean> exito) {

        AuthRequest req = new AuthRequest(email, password);
        authService.registrar(BuildConfig.SUPABASE_ANON_KEY, req)
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            AuthResponse body = resp.body();
                            String userId = body.user != null ? body.user.id : null;
                            SupabaseClient.getInstance().setAccessToken(body.accessToken);

                            // Crear perfil en tabla usuarios
                            Usuario u = new Usuario(userId, nombre, telefono, tipo);
                            usuarioService.crear(u).enqueue(new Callback<List<Usuario>>() {
                                @Override
                                public void onResponse(Call<List<Usuario>> call2, Response<List<Usuario>> r2) {
                                    session.guardarSesion(body.accessToken, userId, tipo, nombre);
                                    exito.postValue(true);
                                }
                                @Override
                                public void onFailure(Call<List<Usuario>> call2, Throwable t) {
                                    error.postValue("Error guardando perfil: " + t.getMessage());
                                }
                            });
                        } else {
                            error.postValue("Error en registro: " + resp.code());
                        }
                    }
                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        error.postValue("Error de red: " + t.getMessage());
                    }
                });
    }

    // LOGIN
    public void login(String email, String password,
                      MutableLiveData<String> error,
                      MutableLiveData<String> tipoUsuario) {

        AuthRequest req = new AuthRequest(email, password);
        authService.login(BuildConfig.SUPABASE_ANON_KEY, req)
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            AuthResponse body = resp.body();
                            String userId = body.user != null ? body.user.id : null;
                            SupabaseClient.getInstance().setAccessToken(body.accessToken);

                            // Cargar perfil para saber el tipo
                            usuarioService.getPerfil("eq." + userId)
                                    .enqueue(new Callback<List<Usuario>>() {
                                        @Override
                                        public void onResponse(Call<List<Usuario>> c2, Response<List<Usuario>> r2) {
                                            if (r2.isSuccessful() && r2.body() != null && !r2.body().isEmpty()) {
                                                Usuario u = r2.body().get(0);
                                                session.guardarSesion(body.accessToken, userId, u.tipo, u.nombre);
                                                tipoUsuario.postValue(u.tipo);
                                            } else {
                                                error.postValue("No se encontró el perfil de usuario.");
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<List<Usuario>> c2, Throwable t) {
                                            error.postValue("Error cargando perfil: " + t.getMessage());
                                        }
                                    });
                        } else {
                            error.postValue("Email o contraseña incorrectos.");
                        }
                    }
                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        error.postValue("Error de red: " + t.getMessage());
                    }
                });
    }

    // LOGOUT
    public void logout() {
        String token = session.getToken();
        if (token != null) {
            authService.logout(BuildConfig.SUPABASE_ANON_KEY, "Bearer " + token).enqueue(new Callback<Void>() {
                @Override public void onResponse(Call<Void> call, Response<Void> response) {}
                @Override public void onFailure(Call<Void> call, Throwable t) {}
            });
        }
        SupabaseClient.getInstance().clearToken();
        session.cerrarSesion();
    }

    public SessionManager getSession() { return session; }
}
