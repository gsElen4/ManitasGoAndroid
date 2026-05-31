package com.example.manitasgo.modelo;

import com.google.gson.annotations.SerializedName;

public class Resenya {

    public String id;

    @SerializedName("comercio_id")
    public String comercioId;

    @SerializedName("cliente_id")
    public String clienteId;

    public int puntuacion;
    public String comentario;
    public String fecha;

    // Nombre del cliente
    public transient String clienteNombre;

    public Resenya() {}

    public Resenya(String comercioId, String clienteId, int puntuacion, String comentario) {
        this.comercioId = comercioId;
        this.clienteId = clienteId;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
    }
}
