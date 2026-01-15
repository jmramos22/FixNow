package com.example.FixNow.model.cliente;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Cliente implements Serializable {

    // @SerializedName asegura que coincida con el JSON de tu API PHP
    @SerializedName("id")
    private int id;

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

    // Constructor vacío
    public Cliente() {}

    // Constructor completo
    public Cliente(String nombre, String apPaterno, String apMaterno, String mail, String contrasena) {
        this.nombre = nombre;
        this.apPaterno = apPaterno;
        this.apMaterno = apMaterno;
        this.mail = mail;
        this.contrasena = contrasena;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApPaterno() { return apPaterno; }
    public void setApPaterno(String apPaterno) { this.apPaterno = apPaterno; }

    public String getApMaterno() { return apMaterno; }
    public void setApMaterno(String apMaterno) { this.apMaterno = apMaterno; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}