package com.example.FixNow.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class OrdenTrabajo implements Serializable {

    @SerializedName("id_incidencia")
    private int idIncidencia;

    @SerializedName("id_solucionador")
    private int idSolucionador;
    @SerializedName("calificacion")
    private int calificacion;

    public OrdenTrabajo(int idIncidencia, int idSolucionador) {
        this.idIncidencia = idIncidencia;
        this.idSolucionador = idSolucionador;
    }
    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }
}