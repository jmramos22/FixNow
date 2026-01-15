package com.example.FixNow;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// --- IMPORTS NUEVOS (Retrofit) ---
import com.example.FixNow.model.api.ApiResponse;
import com.example.FixNow.model.incidencia.Incidencia;
import com.example.FixNow.model.oferta.Oferta;
import com.example.FixNow.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdenOfertaActivity extends AppCompatActivity {

    private static final String TAG = "OrdenOferta";

    private TextView tvTitulo;
    private TextView tvDescripcion;
    private TextView tvTipo;
    private TextView tvDireccion;
    private EditText etMensaje;
    private Button btnOfertar;

    private int idIncidenciaActual = -1;
    private int idSolucionadorActual = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orden_oferta);

        initViews();
        cargarDatosSesion();
        cargarDatosIncidencia();

        btnOfertar.setOnClickListener(v -> realizarOferta());
    }

    private void initViews() {
        tvTitulo = findViewById(R.id.tvTitulo);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        tvTipo = findViewById(R.id.tvTipo);
        tvDireccion = findViewById(R.id.tvDireccion);
        etMensaje = findViewById(R.id.etMensaje);
        btnOfertar = findViewById(R.id.btnOfertar);
    }

    private void cargarDatosSesion() {
        SharedPreferences sp = getSharedPreferences("sesion", MODE_PRIVATE);
        // Validamos que sea un Solucionador
        idSolucionadorActual = sp.getInt("idSolucionador", -1);

        if (idSolucionadorActual == -1) {
            Toast.makeText(this, "Error de sesión: ID Solucionador no encontrado.", Toast.LENGTH_LONG).show();
            finish(); // Cerramos porque no puede ofertar sin ID
        }
    }

    private void cargarDatosIncidencia() {
        if (getIntent().getExtras() != null) {
            idIncidenciaActual = getIntent().getIntExtra("INCIDENCIA_ID", -1);

            if (idIncidenciaActual != -1) {

                // Usamos el "Truco de Filtrado" para asegurar que funcione sin cambios extra en PHP
                // (Descarga todas y busca la que coincida con el ID)
                RetrofitClient.getApiService().listarIncidencias().enqueue(new Callback<ApiResponse<List<Incidencia>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Incidencia>>> call, Response<ApiResponse<List<Incidencia>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Incidencia> lista = response.body().getData();
                            if (lista != null) {
                                for (Incidencia inc : lista) {
                                    if (inc.getId() == idIncidenciaActual) {
                                        // ¡La encontramos! Llenamos la vista
                                        mostrarDatos(inc);
                                        return;
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(OrdenOfertaActivity.this, "No se pudo cargar la info de la incidencia", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Incidencia>>> call, Throwable t) {
                        Log.e(TAG, "Error red: " + t.getMessage());
                        Toast.makeText(OrdenOfertaActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(this, "Error: ID de incidencia inválido.", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Error: No se recibió la incidencia.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void mostrarDatos(Incidencia incidencia) {
        tvTitulo.setText(incidencia.getTitulo() != null ? incidencia.getTitulo() : "Error de título");
        tvDescripcion.setText(incidencia.getDescripcion() != null ? incidencia.getDescripcion() : "Error de descripción");
        tvTipo.setText(incidencia.getTipo() != null ? incidencia.getTipo() : "Tipo desconocido");
        String direccionTexto = "Dirección: " + (incidencia.getDireccion() != null ? incidencia.getDireccion() : "N/A");
        tvDireccion.setText(direccionTexto);
    }

    private void realizarOferta() {
        String mensaje = etMensaje.getText().toString().trim();

        if (idIncidenciaActual == -1 || idSolucionadorActual == -1) {
            Toast.makeText(this, "Error de datos. Reinicie la sesión.", Toast.LENGTH_LONG).show();
            return;
        }

        if (mensaje.isEmpty()) {
            Toast.makeText(this, "Por favor, escribe un mensaje en la oferta.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Bloquear botón
        btnOfertar.setEnabled(false);
        btnOfertar.setText("Enviando...");

        // 1. Crear Objeto Oferta
        Oferta nuevaOferta = new Oferta();
        nuevaOferta.setMensaje(mensaje);
        // El estado "Enviada" lo pone el PHP o constructor, pero podemos mandarlo
        nuevaOferta.setEstado("Enviada");
        nuevaOferta.setIdIncidencia(idIncidenciaActual);
        nuevaOferta.setIdSolucionador(idSolucionadorActual);

        // 2. Enviar a la API
        RetrofitClient.getApiService().crearOferta(nuevaOferta).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                btnOfertar.setEnabled(true);
                btnOfertar.setText("Ofertar");

                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {
                        Toast.makeText(OrdenOfertaActivity.this, "¡Oferta enviada con éxito!", Toast.LENGTH_LONG).show();
                        finish(); // Regresamos a la lista
                    } else {
                        Toast.makeText(OrdenOfertaActivity.this, "Error: " + response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(OrdenOfertaActivity.this, "Error del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                btnOfertar.setEnabled(true);
                btnOfertar.setText("Ofertar");
                Log.e(TAG, "Error al enviar: " + t.getMessage());
                Toast.makeText(OrdenOfertaActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}