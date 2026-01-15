package com.example.FixNow;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.FixNow.model.api.ApiResponse;
import com.example.FixNow.model.incidencia.Incidencia;
import com.example.FixNow.model.FinalizarRequest;
import com.example.FixNow.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleOrdenActivity extends AppCompatActivity {

    // Vistas
    private TextView tvTitulo, tvDescripcion, tvTipo, tvDireccion, tvCliente;
    private Button btnFinalizar;

    // Datos
    private int idOrden;
    private int idIncidencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_orden);

        // 1. Recibir IDs del Intent (Solo necesitamos estos dos)
        idOrden = getIntent().getIntExtra("ID_ORDEN", -1);
        idIncidencia = getIntent().getIntExtra("ID_INCIDENCIA", -1);

        // 2. Vincular Vistas (Usando tus IDs originales)
        tvTitulo = findViewById(R.id.tvDetalleTitulo);
        tvDescripcion = findViewById(R.id.tvDetalleDescripcion);
        tvTipo = findViewById(R.id.tvDetalleTipo);
        tvDireccion = findViewById(R.id.tvDetalleDireccion);
        tvCliente = findViewById(R.id.tvDetalleCliente); // Asegúrate de que este ID exista en tu XML
        btnFinalizar = findViewById(R.id.btnFinalizarOrden);

        // 3. Configurar Botón
        btnFinalizar.setOnClickListener(v -> finalizarTrabajo());

        // 4. Cargar datos frescos de la BD
        if (idIncidencia != -1) {
            cargarDetalles();
        } else {
            Toast.makeText(this, "Error: ID de incidencia no válido", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void cargarDetalles() {
        // Reutilizamos el endpoint de 'obtenerIncidencia' para llenar la pantalla
        RetrofitClient.getApiService().obtenerIncidencia(idIncidencia).enqueue(new Callback<ApiResponse<Incidencia>>() {
            @Override
            public void onResponse(Call<ApiResponse<Incidencia>> call, Response<ApiResponse<Incidencia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Incidencia inc = response.body().getData();
                    if (inc != null) {
                        // Llenamos los textos
                        tvTitulo.setText(inc.getTitulo());
                        tvDescripcion.setText(inc.getDescripcion());
                        tvTipo.setText(inc.getTipo());
                        tvDireccion.setText("Dirección: " + inc.getDireccion());

                        // Si tu modelo Incidencia tiene nombreCliente, úsalo. Si no, pon "Cliente"
                        String nombreC = (inc.getNombreCliente() != null) ? inc.getNombreCliente() : "Cliente";
                        tvCliente.setText("Abierta por: " + nombreC);

                        // --- LÓGICA VITAL DEL BOTÓN ---
                        String estado = inc.getStatus();

                        // Si ya está finalizada o resuelta, ocultamos el botón
                        if ("Finalizada".equalsIgnoreCase(estado) || "Resuelta".equalsIgnoreCase(estado)) {
                            btnFinalizar.setVisibility(View.GONE);
                            // Opcional: Mostrar un texto que diga "Completado"
                        } else {
                            // Si está "En proceso", mostramos el botón
                            btnFinalizar.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Incidencia>> call, Throwable t) {
                Toast.makeText(DetalleOrdenActivity.this, "Error al cargar detalles", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void finalizarTrabajo() {
        if (idOrden == -1 || idIncidencia == -1) {
            Toast.makeText(this, "Error en los datos de la orden", Toast.LENGTH_SHORT).show();
            return;
        }

        btnFinalizar.setEnabled(false);
        btnFinalizar.setText("Finalizando...");

        // Preparamos la petición
        FinalizarRequest req = new FinalizarRequest(idOrden, idIncidencia);

        // Llamamos a la API
        RetrofitClient.getApiService().finalizarTrabajo(req).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {

                        Toast.makeText(DetalleOrdenActivity.this, "Orden finalizada correctamente", Toast.LENGTH_SHORT).show();
                        finish(); // Regresamos a la lista

                    } else {
                        restaurarBoton("Error: " + response.body().getMessage());
                    }
                } else {
                    restaurarBoton("Error del servidor");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                restaurarBoton("Error de conexión");
            }
        });
    }

    private void restaurarBoton(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        btnFinalizar.setEnabled(true);
        btnFinalizar.setText("Finalizar Orden");
    }
}