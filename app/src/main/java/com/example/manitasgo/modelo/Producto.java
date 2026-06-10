package com.example.manitasgo.modelo;

import com.google.gson.annotations.SerializedName;


public class Producto {

    public String id;

    @SerializedName("comercio_id")
    public String comercioId;

    @SerializedName("categoria_id")
    public Integer categoriaId;

    public String nombre;
    public String descripcion;
    public String codigo;
    public Double precio;
    public int stock;

    @SerializedName("imagen_url")
    public String imagenUrl;

    public boolean disponible;

    @SerializedName("fecha_modificacion")
    public String fechaModificacion;

    public Producto() {}
}
