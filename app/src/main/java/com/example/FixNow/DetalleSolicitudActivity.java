package com.example.FixNow;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Imports de Retrofit y Modelos Java
import com.example.FixNow.model.api.ApiResponse;
import com.example.FixNow.model.incidencia.Incidencia;
import com.example.FixNow.model.oferta.Oferta;
import com.example.FixNow.network.RetrofitClient;
import com.example.FixNow.view.OfertaAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleSolicitudActivity extends AppCompatActivity {

    private TextView tvTitulo, tvDescripcion, tvDireccion, tvEstado, tvTipo;
    private CardView cardEstado, cardTipo;
    private RecyclerView rvOfertas;
    private OfertaAdapter ofertaAdapter;
    private int idIncidenciaActual = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_solicitud);

        // Recuperar ID
        idIncidenciaActual = getIntent().getIntExtra("ID_INCIDENCIA", -1);

        initView();

        if (idIncidenciaActual != -1) {
            cargarDatosDeApi(idIncidenciaActual);
            cargarOfertasApi(idIncidenciaActual);
        } else {
            Toast.makeText(this, "Error: ID inválido", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (idIncidenciaActual != -1) {
            cargarDatosDeApi(idIncidenciaActual);
            cargarOfertasApi(idIncidenciaActual);
        }
    }

    private void initView() {
        tvTitulo = findViewById(R.id.titleIncidencias);
        tvDescripcion = findViewById(R.id.descripcionIncidencias);
        tvDireccion = findViewById(R.id.tvDireccion);
        tvEstado = findViewById(R.id.tvEstadoBadge);
        tvTipo = findViewById(R.id.tvTipoBadge);
        cardEstado = findViewById(R.id.cardEstadoBadge);
        cardTipo = findViewById(R.id.cardTipoBadge);

        rvOfertas = findViewById(R.id.rvOfertas);
        rvOfertas.setLayoutManager(new LinearLayoutManager(this));

        ofertaAdapter = new OfertaAdapter();
        rvOfertas.setAdapter(ofertaAdapter);
    }

    private void cargarDatosDeApi(int id) {
        RetrofitClient.getApiService().obtenerIncidencia(id).enqueue(new Callback<ApiResponse<Incidencia>>() {
            @Override
            public void onResponse(Call<ApiResponse<Incidencia>> call, Response<ApiResponse<Incidencia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Incidencia inc = response.body().getData();
                    if (inc != null) {
                        llenarInformacion(inc);
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Incidencia>> call, Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
            }
        });
    }

    // --- 2. CARGAR OFERTAS (Retrofit) ---
    private void cargarOfertasApi(int idIncidencia) {
        RetrofitClient.getApiService().listarOfertas(idIncidencia).enqueue(new Callback<ApiResponse<List<Oferta>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Oferta>>> call, Response<ApiResponse<List<Oferta>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Oferta> listaOfertas = response.body().getData();


                    ofertaAdapter.setDatos(listaOfertas);

                    if (listaOfertas == null || listaOfertas.isEmpty()) {

                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Oferta>>> call, Throwable t) {
                Log.e("API_OFERTAS", "Error: " + t.getMessage());
                Toast.makeText(DetalleSolicitudActivity.this, "Error cargando ofertas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void llenarInformacion(Incidencia inc) {
        if (inc == null) return;

        if (tvTitulo != null) tvTitulo.setText(inc.getTitulo() != null ? inc.getTitulo() : "Sin título");
        if (tvDescripcion != null) tvDescripcion.setText(inc.getDescripcion() != null ? inc.getDescripcion() : "Sin descripción");

        String direccion = inc.getDireccion() != null ? inc.getDireccion() : "No especificada";
        if (tvDireccion != null) tvDireccion.setText(direccion);
        String estado = inc.getStatus() != null ? inc.getStatus() : "Desconocido";
        String tipo = inc.getTipo() != null ? inc.getTipo() : "En Domicilio";

        colorearBadges(estado, tipo);
    }

    private void colorearBadges(String estado, String tipo) {
        if (cardEstado == null || cardTipo == null) return;

        // COLOREAR ESTADO
        if (estado != null) {
            tvEstado.setText(estado);
            if (estado.equalsIgnoreCase("Abierta") || estado.contains("Ofertas")) {
                cardEstado.setCardBackgroundColor(Color.parseColor("#FFF3E0"));
                tvEstado.setTextColor(Color.parseColor("#E65100"));
            } else if (estado.equalsIgnoreCase("En proceso")) {
                cardEstado.setCardBackgroundColor(Color.parseColor("#E3F2FD"));
                tvEstado.setTextColor(Color.parseColor("#1565C0"));
            } else if (estado.equalsIgnoreCase("Finalizada")) {
                cardEstado.setCardBackgroundColor(Color.parseColor("#E8F5E9"));
                tvEstado.setTextColor(Color.parseColor("#2E7D32"));
            } else {
                cardEstado.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                tvEstado.setTextColor(Color.parseColor("#616161"));
            }
        }

        // COLOREAR TIPO
        if (tipo != null) {
            tvTipo.setText(tipo);
            if (tipo.equalsIgnoreCase("Carretera")) {
                cardTipo.setCardBackgroundColor(Color.parseColor("#FFEBEE"));
                tvTipo.setTextColor(Color.parseColor("#C62828"));
            } else {
                cardTipo.setCardBackgroundColor(Color.parseColor("#E3F2FD"));
                tvTipo.setTextColor(Color.parseColor("#1565C0"));
            }
        }
    }
}