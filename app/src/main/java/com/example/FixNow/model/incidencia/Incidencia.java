package com.example.FixNow.model.incidencia;


import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Incidencia implements Serializable {

    @SerializedName("id")
    private int id; // Usamos int primitivo, o Integer si puede ser nulo

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("tipo")
    private String tipo;

    @SerializedName("direccion")
    private String direccion;

    @SerializedName("status")
    private String status;

    @SerializedName("id_cliente")
    private int idCliente;

    @SerializedName("nombre_cliente")
    private String nombreCliente;

    public Incidencia() {}

    // Constructor para CREAR una nueva (sin ID ni status, eso lo pone la base de datos)
    public Incidencia(String titulo, String descripcion, String tipo, String direccion, int idCliente, String nombreCliente) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.direccion = direccion;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.status = "Abierta"; // Valor por defecto en App
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
}