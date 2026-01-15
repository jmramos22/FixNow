package com.example.FixNow;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.FixNow.model.api.ApiResponse;
import com.example.FixNow.model.incidencia.Incidencia;
import com.example.FixNow.model.solucionador.Solucionador;
import com.example.FixNow.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncidenciaProcesoActivity extends AppCompatActivity {

    // Vistas del XML
    private TextView tvTitulo, tvDesc, tvDireccion, tvEstado, tvTipo, tvAtendidaPor;
    private Button btnCalificar;
    private LinearLayout layoutCalificacionHecha;
    private RatingBar ratingBarMostrar;
    private CardView cardEstadoBadge;

    // Datos
    private int idIncidencia = -1;
    private String nombreTecnicoActual = "";
    private String estadoGlobal = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidencia_en_proceso); // Asegúrate que este sea el nombre de TU XML

        idIncidencia = getIntent().getIntExtra("ID_INCIDENCIA", -1);

        initViews();

        if (idIncidencia != -1) {
            cargarDatosIncidencia();
            cargarDatosTecnico();
        } else {
            Toast.makeText(this, "Error de ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        // Vinculación con los IDs de TU XML
        tvTitulo = findViewById(R.id.tvTituloProceso);
        tvDesc = findViewById(R.id.tvDescProceso);
        tvDireccion = findViewById(R.id.tvDireccionProceso);

        tvEstado = findViewById(R.id.tvEstadoProceso);
        tvTipo = findViewById(R.id.tvTipoProceso);
        cardEstadoBadge = findViewById(R.id.tvEstadoProceso).getParent() instanceof CardView ? (CardView) findViewById(R.id.tvEstadoProceso).getParent() : null;

        tvAtendidaPor = findViewById(R.id.tvAtendidaPor);

        btnCalificar = findViewById(R.id.btnCalificar);
        layoutCalificacionHecha = findViewById(R.id.layoutCalificacionHecha);
        ratingBarMostrar = findViewById(R.id.ratingBarMostrar);

        btnCalificar.setOnClickListener(v -> {
            Intent intent = new Intent(IncidenciaProcesoActivity.this, CalificarActivity.class);
            intent.putExtra("ID_INCIDENCIA", idIncidencia);
            intent.putExtra("NOMBRE_TECNICO", nombreTecnicoActual);
            startActivity(intent);
        });
    }

    private void cargarDatosIncidencia() {
        RetrofitClient.getApiService().obtenerIncidencia(idIncidencia).enqueue(new Callback<ApiResponse<Incidencia>>() {
            @Override
            public void onResponse(Call<ApiResponse<Incidencia>> call, Response<ApiResponse<Incidencia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Incidencia inc = response.body().getData();
                    if (inc != null) {
                        tvTitulo.setText(inc.getTitulo());
                        tvDesc.setText(inc.getDescripcion());
                        tvDireccion.setText("Dirección: " + inc.getDireccion());
                        tvTipo.setText(inc.getTipo());

                        estadoGlobal = (inc.getStatus() != null) ? inc.getStatus() : "En proceso";
                        tvEstado.setText(estadoGlobal);

                        actualizarUIsegunEstado(estadoGlobal);
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Incidencia>> call, Throwable t) {
                Toast.makeText(IncidenciaProcesoActivity.this, "Error cargando datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarDatosTecnico() {
        RetrofitClient.getApiService().obtenerSolucionadorAsignado(idIncidencia).enqueue(new Callback<ApiResponse<Solucionador>>() {
            @Override
            public void onResponse(Call<ApiResponse<Solucionador>> call, Response<ApiResponse<Solucionador>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Solucionador sol = response.body().getData();
                    if (sol != null) {
                        nombreTecnicoActual = sol.getNombre() + " " + sol.getApPaterno();
                        tvAtendidaPor.setText(nombreTecnicoActual);

                        // Si ya tiene calificación (porque la API ahora lo devuelve), mostramos las estrellas
                        if (sol.getCalificacionOrden() > 0) {
                            mostrarEstrellas(sol.getCalificacionOrden());
                        }
                    }
                } else {
                    tvAtendidaPor.setText("Esperando asignación...");
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Solucionador>> call, Throwable t) {
                tvAtendidaPor.setText("Error al cargar técnico");
            }
        });
    }

    private void actualizarUIsegunEstado(String estado) {
        // Colores del Badge
        if ("En proceso".equalsIgnoreCase(estado)) {
            tvEstado.setTextColor(Color.parseColor("#EF6C00")); // Naranja

            // Ocultamos todo lo de calificar
            btnCalificar.setVisibility(View.GONE);
            layoutCalificacionHecha.setVisibility(View.GONE);

        } else if ("Resuelta".equalsIgnoreCase(estado)) {
            // El técnico ya terminó, mostramos botón para calificar
            tvEstado.setTextColor(Color.parseColor("#2E7D32")); // Verde
            tvEstado.setText("Trabajo Realizado");

            btnCalificar.setVisibility(View.VISIBLE);
            layoutCalificacionHecha.setVisibility(View.GONE);

        } else if ("Finalizada".equalsIgnoreCase(estado)) {
            // Ya se calificó y cerró
            tvEstado.setTextColor(Color.parseColor("#2E7D32")); // Verde

            btnCalificar.setVisibility(View.GONE);
            // Las estrellas se mostrarán cuando cargueDatosTecnico confirme la calificación
        }
    }

    private void mostrarEstrellas(int estrellas) {
        layoutCalificacionHecha.setVisibility(View.VISIBLE);
        ratingBarMostrar.setRating(estrellas);
        btnCalificar.setVisibility(View.GONE); // Aseguramos que el botón se vaya
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (idIncidencia != -1) {
            cargarDatosIncidencia();
            cargarDatosTecnico();
        }
    }
}