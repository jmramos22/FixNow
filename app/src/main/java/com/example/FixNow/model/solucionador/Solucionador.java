package com.example.FixNow.model.solucionador;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Solucionador implements Serializable {

    @SerializedName("id")
    private int idSolucionador;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("ap_paterno")
    private String apPaterno;

    @SerializedName("ap_materno")
    private String apMaterno;

    @SerializedName("mail")
    private String mail;

    @SerializedName("contrasena")
    private String contrasena;
    @SerializedName("calificacion")
    private int calificacionOrden; // Campo nuevo



    // Getters
    public int getIdSolucionador() { return idSolucionador; }
    public String getNombre() { return nombre; }
    public String getApPaterno() { return apPaterno; }
    public String getApMaterno() { return apMaterno; }
    public String getMail() { return mail; }
    public String getContrasena() { return contrasena; } // Si tienes campo contraseña en el modelo

    public int getCalificacionOrden() { return calificacionOrden; }
    // ... dentro de Solucionador.java ...

    // Setters necesarios para el Login y Registro
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApPaterno(String apPaterno) { this.apPaterno = apPaterno; }
    public void setApMaterno(String apMaterno) { this.apMaterno = apMaterno; }
    public void setMail(String mail) { this.mail = mail; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; } // Si tienes campo contraseña en el modelo

}