package com.example.FixNow;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.FixNow.model.api.ApiResponse;
import com.example.FixNow.model.incidencia.Incidencia;
import com.example.FixNow.network.RetrofitClient;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class NotificacionesWorker extends Worker {

    public NotificacionesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();

        // 1. Preferencias de Sesión (Para saber si hay un solucionador logueado)
        SharedPreferences spSesion = context.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        boolean esSolucionador = spSesion.getBoolean("esSolucionador", false);

        // 2. NUEVO: Preferencias de Notificaciones (¡Estas no se borran al cerrar sesión!)
        SharedPreferences spNotif = context.getSharedPreferences("memoria_notificaciones", Context.MODE_PRIVATE);

        if (esSolucionador) {
            try {
                Response<ApiResponse<List<Incidencia>>> response = RetrofitClient.getApiService().listarIncidencias().execute();

                if (response.isSuccessful() && response.body() != null) {
                    List<Incidencia> lista = response.body().getData();

                    if (lista != null && !lista.isEmpty()) {

                        Incidencia masReciente = lista.get(0);
                        int idMasReciente = masReciente.getId();

                        // Revisamos la memoria de notificaciones (no la de sesión)
                        int ultimoIdGuardado = spNotif.getInt("ULTIMO_ID_VISTO", idMasReciente);

                        if (idMasReciente > ultimoIdGuardado) {

                            NotificationHelper.mostrarNotificacion(
                                    context,
                                    "¡Nueva oportunidad de trabajo!",
                                    "Alguien publicó: " + masReciente.getTitulo()
                            );

                            spNotif.edit().putInt("ULTIMO_ID_VISTO", idMasReciente).apply();

                        } else if (ultimoIdGuardado == idMasReciente && !spNotif.contains("ULTIMO_ID_VISTO")) {

                            spNotif.edit().putInt("ULTIMO_ID_VISTO", idMasReciente).apply();
                        }
                    }
                }
            } catch (IOException e) {
                Log.e("WORKER_ERROR", "No se pudo conectar a XAMPP en segundo plano");
                return Result.retry();
            }
        }

        return Result.success();
    }
}