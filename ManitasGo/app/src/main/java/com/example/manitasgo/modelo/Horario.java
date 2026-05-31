package com.example.manitasgo.modelo;

import com.google.gson.annotations.SerializedName;

public class Horario {

    public String id;

    @SerializedName("comercio_id")
    public String comercioId;

    public String dia;
    public String apertura;
    public String cierre;
    public String apertura2;
    public String cierre2;
    public boolean cerrado;

    public Horario() {}

    public String toTexto() {
        if (cerrado || (apertura == null && apertura2 == null)) return "Cerrado";
        StringBuilder sb = new StringBuilder();
        if (apertura != null) sb.append(apertura).append(" – ").append(cierre);
        if (apertura2 != null) {
            if (sb.length() > 0) sb.append(" / ");
            sb.append(apertura2).append(" – ").append(cierre2);
        }
        return sb.toString();
    }
}
