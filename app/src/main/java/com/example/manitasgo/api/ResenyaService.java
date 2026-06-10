package com.example.manitasgo.api;

import com.example.manitasgo.modelo.Resenya;
import com.example.manitasgo.modelo.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ResenyaService {

    @GET("resenyas")
    Call<List<Resenya>> getByComercio(
            @Query("comercio_id") String comercioFilter,
            @Query("select") String select,
            @Query("order") String order
    );

    @POST("resenyas")
    Call<List<Resenya>> crear(@Body Resenya resenya);

    @PATCH("resenyas")
    Call<List<Resenya>> actualizar(
            @Query("id") String idFilter,
            @Body Resenya cambios
    );
}
