package com.example.FixNow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FixNow.model.api.ApiResponse;
import com.example.FixNow.model.OrdenSolucionador;
import com.example.FixNow.network.RetrofitClient;
import com.example.FixNow.view.OrdenTrabajoAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdenesTrabajoActivity extends AppCompatActivity implements OrdenTrabajoAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    // Usamos el adaptador NUEVO que soporta colores de estado
    private OrdenTrabajoAdapter adapter;
    private ProgressBar progressBar;
    private int idSolucionadorActual = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordenes_trabajo);

        cargarIdSolucionador();

        // Vinculamos con tus IDs originales del XML
        progressBar = findViewById(R.id.progressBar2);
        recyclerView = findViewById(R.id.rvOrdenesTrabajo);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializamos el Adapter pasando 'this' para escuchar los clicks
        adapter = new OrdenTrabajoAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargamos la lista al volver (por si acabas de finalizar una tarea)
        if (idSolucionadorActual != -1) {
            cargarOrdenesDeTrabajo();
        }
    }

    private void cargarIdSolucionador() {
        SharedPreferences sp = getSharedPreferences("sesion", MODE_PRIVATE);
        idSolucionadorActual = sp.getInt("idSolucionador", -1);

        if (idSolucionadorActual == -1) {
            Toast.makeText(this, "Error de Sesión. Inicia de nuevo.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void cargarOrdenesDeTrabajo() {
        if (idSolucionadorActual == -1) return;

        progressBar.setVisibility(View.VISIBLE);

        // Llamada a la API de MySQL
        RetrofitClient.getApiService().listarMisOrdenes(idSolucionadorActual).enqueue(new Callback<ApiResponse<List<OrdenSolucionador>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<OrdenSolucionador>>> call, Response<ApiResponse<List<OrdenSolucionador>>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<OrdenSolucionador> lista = response.body().getData();

                    if (lista != null && !lista.isEmpty()) {
                        adapter.setLista(lista);
                        // Toast.makeText(ordenes_trabajo.this, "Tienes " + lista.size() + " trabajos.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OrdenesTrabajoActivity.this, "No tienes trabajos asignados aún.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(OrdenesTrabajoActivity.this, "Sin datos disponibles.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<OrdenSolucionador>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("API_ORDENES", t.getMessage());
                Toast.makeText(OrdenesTrabajoActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Al hacer click, vamos al detalle NUEVO para poder finalizar el trabajo
    @Override
    public void onItemClick(OrdenSolucionador orden) {
        Intent intent = new Intent(this, DetalleOrdenActivity.class);
        intent.putExtra("ID_ORDEN", orden.getIdOrden());
        intent.putExtra("ID_INCIDENCIA", orden.getIdIncidencia());
        startActivity(intent);
    }
}