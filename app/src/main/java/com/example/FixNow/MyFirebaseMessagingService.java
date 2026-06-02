package com.example.FixNow;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM_TOKEN", "Mi nuevo token es: " + token);
        // Aquí no llamamos a XAMPP todavía, lo haremos desde el Login.
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        if (message.getNotification() != null) {
            String titulo = message.getNotification().getTitle();
            String cuerpo = message.getNotification().getBody();
            NotificationHelper.mostrarNotificacion(this, titulo, cuerpo);
        }
    }
}