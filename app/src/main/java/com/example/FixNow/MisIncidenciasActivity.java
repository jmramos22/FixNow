package com.example.FixNow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// --- IMPORTS NUEVOS (Retrofit) ---
import com.example.FixNow.model.api.ApiResponse;
import com.example.FixNow.model.incidencia.Incidencia;
import com.example.FixNow.network.RetrofitClient;
import com.example.FixNow.view.IncidenciaAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MisIncidenciasActivity extends AppCompatActivity {

    private RecyclerView rvIncidencias;
    private FloatingActionButton fabAgregar;
    private IncidenciaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_incidencias);

        initView();
        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosApi(); // Cambiado a método de API
    }

    private void initView() {
        rvIncidencias = findViewById(R.id.rvIncidencias);
        fabAgregar = findViewById(R.id.fabAgregarIncidencia);

        fabAgregar.setOnClickListener(view -> {
            Intent i = new Intent(this, NuevaIncidenciaActivity.class);
            startActivity(i);
        });
    }

    private void setupRecyclerView() {
        rvIncidencias.setLayoutManager(new LinearLayoutManager(this));

        adapter = new IncidenciaAdapter(new IncidenciaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Incidencia incidencia) {
                int idToSend = (incidencia.getId() != 0) ? incidencia.getId() : -1;

                // Usamos una variable temporal para el estado, asegurando que no sea null
                String status = (incidencia.getStatus() != null) ? incidencia.getStatus() : "Abierta";

                Intent intent;

                // --- LÓGICA CORREGIDA ---

                // CASO A: Aún estamos buscando técnico (Ver Ofertas)
                if ("Abierta".equalsIgnoreCase(status)) {
                    intent = new Intent(MisIncidenciasActivity.this, DetalleSolicitudActivity.class);
                }
                // CASO B: Ya tenemos técnico o ya acabó (Ver Seguimiento/Calificación)
                // Esto abarca: "En proceso", "Resuelta" y "Finalizada"
                else {
                    intent = new Intent(MisIncidenciasActivity.this, IncidenciaProcesoActivity.class);
                }

                intent.putExtra("ID_INCIDENCIA", idToSend);
                startActivity(intent);
            }
        });

        rvIncidencias.setAdapter(adapter);
    }

    private void cargarDatosApi() {
        // 1. Obtener ID del cliente logueado
        SharedPreferences sp = getSharedPreferences("sesion", MODE_PRIVATE);
        int idClienteLogueado = sp.getInt("idCliente", 0);

        if (idClienteLogueado == 0) {
            Toast.makeText(this, "Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Llamar a la API PHP
        RetrofitClient.getApiService().listarIncidencias().enqueue(new Callback<ApiResponse<List<Incidencia>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Incidencia>>> call, Response<ApiResponse<List<Incidencia>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Incidencia> todasLasIncidencias = response.body().getData();

                    if (todasLasIncidencias != null) {
                        // 3. FILTRADO LOCAL:
                        // Como la API trae todas, filtramos aquí solo las de este cliente
                        List<Incidencia> misIncidencias = new ArrayList<>();
                        for (Incidencia inc : todasLasIncidencias) {
                            if (inc.getIdCliente() == idClienteLogueado) {
                                misIncidencias.add(inc);
                            }
                        }

                        if (misIncidencias.isEmpty()) {
                            Toast.makeText(MisIncidenciasActivity.this, "No tienes incidencias registradas", Toast.LENGTH_SHORT).show();
                        }

                        // Actualizamos el RecyclerView
                        adapter.setDatos(misIncidencias);
                    }
                } else {
                    Toast.makeText(MisIncidenciasActivity.this, "Error al cargar datos del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Incidencia>>> call, Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(MisIncidenciasActivity.this, "Fallo de conexión con la API", Toast.LENGTH_SHORT).show();
            }
        });
    }
}








