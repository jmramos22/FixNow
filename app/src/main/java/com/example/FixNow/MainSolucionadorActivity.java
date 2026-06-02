package com.example.FixNow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainSolucionadorActivity extends AppCompatActivity {

    public Button irIn;
    public TextView tvSaludo;
    public Button btCerrar;
    public Button irOr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_solicionador);

        SharedPreferences sp = getSharedPreferences("sesion", MODE_PRIVATE);
        String nombreUsuario = sp.getString("nombreSolucionador", "Solucionador");
        int idSolucionador = sp.getInt("idSolucionador", -1);

        if (idSolucionador == -1) {
            startActivity(new Intent(this, LoginSolucionadorActivity.class));
            finish();
            return;
        }


        // --- PRUEBA DIRECTA INFALIBLE ---
        // Esto lanzará la notificación en la barra superior un segundo después de entrar a esta pantalla
        new android.os.Handler().postDelayed(() -> {
            NotificationHelper.mostrarNotificacion(
                    this,
                    "¡Prueba de Barra!",
                    "Si ves esto, la barra de notificaciones funciona perfectamente."
            );
        }, 2000);


        tvSaludo = findViewById(R.id.tvFuncionaID);
        btCerrar = findViewById(R.id.CerrarS);
        irOr = findViewById(R.id.IrOr);
        irIn = findViewById(R.id.irIn);

        tvSaludo.setText("Hola, " + nombreUsuario);

        // --- PEDIR PERMISO DE NOTIFICACIONES (ANDROID 13+) ---
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        irOr.setOnClickListener(this::irAOrdenesTrabajo);
        irIn.setOnClickListener(this::irAListaIncidencias);
        btCerrar.setOnClickListener(this::cerrarSesion);


        // --- INICIAR EL TRABAJADOR DE NOTIFICACIONES ---
        // Configurar para que revise cada 15 minutos (Es el mínimo permitido por Android para ahorrar batería)
        androidx.work.PeriodicWorkRequest peticionNotificaciones =
                new androidx.work.PeriodicWorkRequest.Builder(NotificacionesWorker.class, 15, java.util.concurrent.TimeUnit.MINUTES)
                        .build();

        // Truco temporal para forzar la revisión inmediatamente al entrar a esta pantalla
        //androidx.work.WorkManager.getInstance(this).enqueue(androidx.work.OneTimeWorkRequest.from(NotificacionesWorker.class));

        // Encolar el trabajo en el sistema de Android
        androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "RevisarIncidenciasNuevas",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                peticionNotificaciones
        );





    }





    public void irAListaIncidencias(View view) {
        Intent i = new Intent(this, ListaIncidenciasActivity.class);
        startActivity(i);
    }

    public void irAOrdenesTrabajo(View view) {
        Intent i = new Intent(this, OrdenesTrabajoActivity.class);
        startActivity(i);
    }

    public void cerrarSesion(View view) {
        SharedPreferences sp = getSharedPreferences("sesion", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear(); // Borrar todos los datos de sesión
        editor.apply();

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}