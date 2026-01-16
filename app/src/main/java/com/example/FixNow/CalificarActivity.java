package com.example.FixNow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// --- IMPORTS NUEVOS RETROFIT ---
import com.example.FixNow.model.api.ApiResponse;
import com.example.FixNow.model.OrdenTrabajo;
import com.example.FixNow.network.RetrofitClient;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalificarActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private Button btnEnviar;
    private TextView tvTecnico;
    private int idIncidencia;
    private String nombreTecnico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar);

        idIncidencia = getIntent().getIntExtra("ID_INCIDENCIA", -1);
        nombreTecnico = getIntent().getStringExtra("NOMBRE_TECNICO");

        ratingBar = findViewById(R.id.rbProviderRating);
        btnEnviar = findViewById(R.id.button2);
        tvTecnico = findViewById(R.id.textView4);


        if(nombreTecnico != null && !nombreTecnico.isEmpty()) {
            tvTecnico.setText(nombreTecnico);
        } else {
            tvTecnico.setText("Solucionador");
        }

        btnEnviar.setOnClickListener(v -> enviarCalificacion());
    }

    private void enviarCalificacion() {
        int estrellas = (int) ratingBar.getRating();

        if (estrellas == 0) {
            Toast.makeText(this, "Por favor selecciona al menos 1 estrella", Toast.LENGTH_SHORT).show();
            return;
        }

        btnEnviar.setEnabled(false);
        btnEnviar.setText("Enviando...");


        OrdenTrabajo orden = new OrdenTrabajo(idIncidencia, 0);

        orden.setCalificacion(estrellas);


        RetrofitClient.getApiService().calificarOrden(orden).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {

                        Toast.makeText(CalificarActivity.this, "¡Gracias por tu opinión!", Toast.LENGTH_SHORT).show();


                        Intent intent = new Intent(CalificarActivity.this, MisIncidenciasActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    } else {
                        restaurarBoton("Error: " + response.body().getMessage());
                    }
                } else {
                    restaurarBoton("Error del servidor");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e("API_CALIFICAR", t.getMessage());
                restaurarBoton("Fallo de conexión. Verifica Internet o XAMPP.");
            }
        });
    }

    private void restaurarBoton(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        btnEnviar.setEnabled(true);
        btnEnviar.setText("Enviar");
    }
}