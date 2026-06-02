package com.example.FixNow;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    private static final String CHANNEL_ID = "FIXNOW_LOCAL_CHANNEL";
    private static final String CHANNEL_NAME = "Alertas FixNow";
    private static final String CHANNEL_DESC = "Notificaciones de estado de incidencias";

    // 1. Crear el Canal de Notificaciones (Obligatorio en Android 8+)
    public static void crearCanal(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH // IMPORTANCE_HIGH hace que suene y salga una tarjeta flotante
            );
            channel.setDescription(CHANNEL_DESC);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    // 2. Método para lanzar la notificación
    // 2. Método para lanzar la notificación
    public static void mostrarNotificacion(Context context, String titulo, String mensaje) {

        // --- NUEVO: Nos aseguramos de que el canal exista antes de lanzar la alerta ---
        crearCanal(context);

        // Al tocar la notificación, abrimos el MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // Se borra cuando el usuario la toca

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Verificamos si tenemos permiso en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify((int) System.currentTimeMillis(), builder.build());
            }
        } else {
            // En Android 12 o inferior no se ocupa pedir permiso
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}