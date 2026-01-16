package com.example.FixNow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Imports de Retrofit
import com.example.FixNow.model.api.ApiResponse;
import com.example.FixNow.model.incidencia.Incidencia;
import com.example.FixNow.network.RetrofitClient;
import com.example.FixNow.view.ListaIncidenciasAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaIncidenciasActivity extends AppCompatActivity
        implements ListaIncidenciasAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ListaIncidenciasAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_incidencias);


        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.rvIncidencias);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ListaIncidenciasAdapter(this, this);
        recyclerView.setAdapter(adapter);


        cargarIncidenciasApi();
    }

    private void cargarIncidenciasApi() {
        progressBar.setVisibility(View.VISIBLE);


        RetrofitClient.getApiService().listarIncidencias().enqueue(new Callback<ApiResponse<List<Incidencia>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Incidencia>>> call, Response<ApiResponse<List<Incidencia>>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Incidencia> todas = response.body().getData();

                    if (todas != null) {

                        List<Incidencia> soloAbiertas = new ArrayList<>();
                        for (Incidencia inc : todas) {
                            if ("Abierta".equalsIgnoreCase(inc.getStatus())) {
                                soloAbiertas.add(inc);
                            }
                        }

                        if (soloAbiertas.isEmpty()) {
                            Toast.makeText(ListaIncidenciasActivity.this, "No hay trabajos disponibles por ahora", Toast.LENGTH_SHORT).show();
                        } else {
                            adapter.setLista(soloAbiertas);
                        }
                    }
                } else {
                    Toast.makeText(ListaIncidenciasActivity.this, "Error en el servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Incidencia>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("API_INCIDENCIAS", t.getMessage());
                Toast.makeText(ListaIncidenciasActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int idIncidencia) {

        Intent intent = new Intent(ListaIncidenciasActivity.this, OrdenOfertaActivity.class);
        intent.putExtra("INCIDENCIA_ID", idIncidencia);
        startActivity(intent);
    }
}