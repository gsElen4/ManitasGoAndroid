package com.example.manitasgo.api;

import com.example.manitasgo.modelo.Producto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ProductoService {

    /** Todos los productos disponibles de un comercio */
    @GET("productos")
    Call<List<Producto>> getByComercio(
            @Query("comercio_id") String comercioFilter,  // "eq.UUID"
            @Query("disponible") String disponible,       // "eq.true"
            @Query("select") String select,
            @Query("order") String order
    );

    /**Búsqueda por nombre usando full-text search de PostgREST.*/
    /** parámetro select incluye relación con categorias*/
    @GET("productos")
    Call<List<Producto>> buscarPorNombre(
            @Query("nombre") String nombreFilter,    // "ilike.*tornillo*"
            @Query("disponible") String disponible,
            @Query("select") String select,
            @Query("order") String order
    );

    /** Buscar por nombre Y comercio */
    @GET("productos")
    Call<List<Producto>> buscarEnComercio(
            @Query("comercio_id") String comercioFilter,
            @Query("nombre") String nombreFilter,
            @Query("disponible") String disponible,
            @Query("select") String select
    );

    /** Buscar por categoría */
    @GET("productos")
    Call<List<Producto>> getByCategoria(
            @Query("categoria_id") String categoriaFilter,
            @Query("disponible") String disponible,
            @Query("select") String select,
            @Query("order") String order
    );

    @POST("productos")
    Call<List<Producto>> crear(@Body Producto producto);

    @PATCH("productos")
    Call<List<Producto>> actualizar(
            @Query("id") String idFilter,
            @Body Producto cambios
    );

    @DELETE("productos")
    Call<Void> eliminar(@Query("id") String idFilter);
}
