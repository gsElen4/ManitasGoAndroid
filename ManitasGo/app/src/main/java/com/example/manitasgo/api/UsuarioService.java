package com.example.manitasgo.api;

import com.example.manitasgo.model.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UsuarioService {

    @GET("usuarios")
    Call<List<Usuario>> getPerfil(@Query("id") String idFilter); // "eq.UUID"

    @POST("usuarios")
    Call<List<Usuario>> crear(@Body Usuario usuario);

    @PATCH("usuarios")
    Call<List<Usuario>> actualizar(
            @Query("id") String idFilter,
            @Body Usuario cambios
    );
}
