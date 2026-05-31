package com.example.manitasgo.modelo;

import com.google.gson.annotations.SerializedName;

public class Usuario {

    public String id;
    public String nombre;
    public String telefono;
    public String tipo;   // cliente | vendedor
    public boolean activo;

    @SerializedName("fecha_registro")
    public String fechaRegistro;

    public Usuario() {}

    public Usuario(String id, String nombre, String telefono, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.tipo = tipo;
    }
}
