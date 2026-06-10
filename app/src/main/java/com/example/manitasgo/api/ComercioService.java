package com.example.manitasgo.api;

import com.example.manitasgo.modelo.Comercio;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**Operaciones sobre la tabla "comercios*/
public interface ComercioService {

    /** Devuelve todos comercios activos con valoración media*/
    @GET("comercios")
    Call<List<Comercio>> getAll(
            @Query("activo") String activo,         // "eq.true"
            @Query("select") String select,         // "*"
            @Query("order") String order            // "nombre.asc"
    );

    /** Buscar por localidad */
    @GET("comercios")
    Call<List<Comercio>> getByLocalidad(
            @Query("localidad") String localidadFilter, // "eq.Boiro"
            @Query("activo") String activo,
            @Query("select") String select,
            @Query("order") String order
    );

    /** Comercio de un vendedor concreto */
    @GET("comercios")
    Call<List<Comercio>> getMiComercio(
            @Query("vendedor_id") String vendedorFilter  // "eq.UUID"
    );

    /** Comercio por su id */
    @GET("comercios")
    Call<List<Comercio>> getById(
            @Query("id") String idFilter  // "eq.UUID"
    );

    @POST("comercios")
    Call<List<Comercio>> crear(@Body Comercio comercio);

    @PATCH("comercios")
    Call<List<Comercio>> actualizar(
            @Query("id") String idFilter,   // "eq.UUID"
            @Body Comercio cambios
    );

    @DELETE("comercios")
    Call<Void> eliminar(@Query("id") String idFilter);
}
