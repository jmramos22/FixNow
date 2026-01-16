package com.example.FixNow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.FixNow.model.api.ApiResponse;
import com.example.FixNow.model.OrdenTrabajo;
import com.example.FixNow.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RevisarOfertaActivity extends AppCompatActivity {

    private TextView tvNombreSolucionador, tvMensajeOferta;
    private Button btnAceptar, btnCancelar;

    private int idOferta, idIncidencia, idSolucionador;
    private String nombreSolucionador, mensajeOferta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_revisar_oferta);

        initView();
        recuperarDatos();
        mostrarDatos();

        btnAceptar.setOnClickListener(v -> aceptarOferta());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void initView() {
        tvNombreSolucionador = findViewById(R.id.tvNombreSolucionador);
        tvMensajeOferta = findViewById(R.id.tvMensajeOferta);
        btnAceptar = findViewById(R.id.btnAceptarOferta);
        btnCancelar = findViewById(R.id.btnRechazarOferta);
    }

    private void recuperarDatos() {
        Intent i = getIntent();
        idOferta = i.getIntExtra("ID_OFERTA", -1);
        idIncidencia = i.getIntExtra("ID_INCIDENCIA", -1);
        idSolucionador = i.getIntExtra("ID_SOLUCIONADOR", -1);
        nombreSolucionador = i.getStringExtra("NOMBRE_SOLUCIONADOR");
        mensajeOferta = i.getStringExtra("MENSAJE_OFERTA");
    }

    private void mostrarDatos() {
        if (tvNombreSolucionador != null) tvNombreSolucionador.setText(nombreSolucionador);
        if (tvMensajeOferta != null) tvMensajeOferta.setText(mensajeOferta);
    }

    private void aceptarOferta() {
        if (idIncidencia == -1 || idSolucionador == -1) {
            Toast.makeText(this, "Datos incompletos para crear la orden", Toast.LENGTH_SHORT).show();
            return;
        }

        btnAceptar.setEnabled(false);
        btnAceptar.setText("Procesando...");


        OrdenTrabajo nuevaOrden = new OrdenTrabajo(idIncidencia, idSolucionador);

        RetrofitClient.getApiService().crearOrdenTrabajo(nuevaOrden).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {

                        Toast.makeText(RevisarOfertaActivity.this, "¡Trabajo Asignado! Oferta procesada.", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(RevisarOfertaActivity.this, MisIncidenciasActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    } else {
                        btnAceptar.setEnabled(true);
                        btnAceptar.setText("Aceptar Oferta");
                        Toast.makeText(RevisarOfertaActivity.this, "Error: " + response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    btnAceptar.setEnabled(true);
                    btnAceptar.setText("Aceptar Oferta");
                    Toast.makeText(RevisarOfertaActivity.this, "Error del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                btnAceptar.setEnabled(true);
                btnAceptar.setText("Aceptar Oferta");
                Log.e("API_ORDEN", t.getMessage());
                Toast.makeText(RevisarOfertaActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}