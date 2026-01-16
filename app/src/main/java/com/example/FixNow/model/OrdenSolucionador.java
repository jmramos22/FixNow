package com.example.FixNow.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class OrdenSolucionador implements Serializable {
    @SerializedName("id_orden")
    private int idOrden;

    @SerializedName("id_incidencia")
    private int idIncidencia;

    @SerializedName("status")
    private String status;

    @SerializedName("calificacion")
    private int calificacion;

    @SerializedName("titulo")
    private String tituloIncidencia;

    @SerializedName("direccion")
    private String direccion;

    @SerializedName("tipo")
    private String tipo;

    // Getters
    public int getIdOrden() { return idOrden; }
    public int getIdIncidencia() { return idIncidencia; }
    public String getStatus() { return status; }
    public int getCalificacion() { return calificacion; }
    public String getTituloIncidencia() { return tituloIncidencia; }
    public String getDireccion() { return direccion; }
    public String getTipo() { return tipo; }
}