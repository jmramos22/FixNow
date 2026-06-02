package com.example.FixNow.model;
import com.google.gson.annotations.SerializedName;

public class TokenRequest {
    @SerializedName("id") private int id;
    @SerializedName("fcm_token") private String fcmToken;

    public TokenRequest(int id, String fcmToken) {
        this.id = id;
        this.fcmToken = fcmToken;
    }
}