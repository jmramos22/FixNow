package com.example.FixNow.model.api;

import com.google.gson.annotations.SerializedName;

// Esta clase sirve para leer LO QUE SEA que responda tu PHP
public class ApiResponse<T> {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    // La "T" significa que el data puede ser un Cliente, una Incidencia, o lo que sea
    @SerializedName("data")
    private T data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}