package com.example.FixNow.model.oferta;

import com.example.FixNow.model.solucionador.Solucionador;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Oferta implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("estado")
    private String estado;

    @SerializedName("id_incidencia")
    private int idIncidencia;

    @SerializedName("id_solucionador")
    private int idSolucionador;

    // --- NUEVO: Campo para el promedio ---
    @SerializedName("promedio")
    private double promedio;

    @SerializedName("solucionador")
    private Solucionador solucionador;

    // Getters
    public int getId() { return id; }
    public String getMensaje() { return mensaje; }
    public String getEstado() { return estado; }
    public int getIdIncidencia() { return idIncidencia; }
    public int getIdSolucionador() { return idSolucionador; }
    public Solucionador getSolucionador() { return solucionador; }

    // --- NUEVO GETTER ---
    public double getPromedio() { return promedio; }

    // Setters
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setIdIncidencia(int idIncidencia) { this.idIncidencia = idIncidencia; }
    public void setIdSolucionador(int idSolucionador) { this.idSolucionador = idSolucionador; }
    public void setId(int id) { this.id = id; }
    // --- NUEVO SETTER ---
    public void setPromedio(double promedio) { this.promedio = promedio; }
}