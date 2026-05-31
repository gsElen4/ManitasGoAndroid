package com.example.manitasgo.modelo;

import com.google.gson.annotations.SerializedName;

public class Comercio {

    public String id;

    @SerializedName("vendedor_id")
    public String vendedorId;

    public String nombre;
    public String descripcion;
    public String direccion;
    public String localidad;

    @SerializedName("codigo_postal")
    public String codigoPostal;

    public String telefono;

    @SerializedName("foto_url")
    public String fotoUrl;

    public double latitud;
    public double longitud;

    @SerializedName("valoracion_media")
    public double valoracionMedia;

    public boolean activo;

    @SerializedName("fecha_alta")
    public String fechaAlta;

    // Distancia calculada localmente
    public transient double distanciaKm = -1;

    public Comercio() {}

    /** Texto de horario resumido */
    public transient String horarioResumen;
}
