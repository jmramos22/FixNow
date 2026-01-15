package com.example.FixNow.model;
import com.google.gson.annotations.SerializedName;

public class FinalizarRequest {
    @SerializedName("id_orden") private int idOrden;
    @SerializedName("id_incidencia") private int idIncidencia;

    public FinalizarRequest(int idOrden, int idIncidencia) {
        this.idOrden = idOrden;
        this.idIncidencia = idIncidencia;
    }
}