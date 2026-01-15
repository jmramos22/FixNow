package com.example.FixNow;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

// --- IMPORTS NUEVOS ---
import com.example.FixNow.model.api.ApiResponse;
import com.example.FixNow.model.incidencia.Incidencia;
import com.example.FixNow.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NuevaIncidenciaActivity extends AppCompatActivity {

    private Button btnRegistroIncidencia;
    private EditText etTitulo, etDescripcion, etDireccion;
    private RadioGroup rgTipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_incidencia);
        initView();

        btnRegistroIncidencia.setOnClickListener(v -> guardarDatos());
    }

    private void initView(){
        btnRegistroIncidencia = findViewById(R.id.btnRegistro_Incidencia);
        etTitulo = findViewById(R.id.edt_Titulo_Incidencia);
        etDescripcion = findViewById(R.id.edt_Descripcion_Incidencia);
        etDireccion = findViewById(R.id.edt_Direccion_Incidencia);
        rgTipo = findViewById(R.id.radioGroupLocation);
    }

    private void guardarDatos() {
        // 1. Recuperamos datos de sesión
        SharedPreferences sp = getSharedPreferences("sesion", MODE_PRIVATE);
        String nombreCliente = sp.getString("nombreCliente", "Cliente");
        int idCliente = sp.getInt("idCliente", 0);

        if (idCliente == 0) {
            Toast.makeText(this, "Error de sesión. Vuelve a loguearte.", Toast.LENGTH_LONG).show();
            return;
        }

        // 2. Recuperamos datos del formulario
        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();

        String tipoSeleccionado = "";
        int selectedId = rgTipo.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton rb = findViewById(selectedId);
            tipoSeleccionado = rb.getText().toString();
        } else {
            Toast.makeText(this, "Selecciona el tipo de incidencia", Toast.LENGTH_SHORT).show();
            return;
        }

        if (titulo.isEmpty() || descripcion.isEmpty() || direccion.isEmpty()) {
            Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- CONEXIÓN CON API ---

        // Bloquear botón para evitar doble envío
        btnRegistroIncidencia.setEnabled(false);
        btnRegistroIncidencia.setText("Guardando...");

        // 3. Crear el Objeto Incidencia (Modelo Java)
        Incidencia nuevaIncidencia = new Incidencia();
        nuevaIncidencia.setTitulo(titulo);
        nuevaIncidencia.setDescripcion(descripcion);
        nuevaIncidencia.setTipo(tipoSeleccionado);
        nuevaIncidencia.setDireccion(direccion);
        nuevaIncidencia.setIdCliente(idCliente);
        nuevaIncidencia.setNombreCliente(nombreCliente);
        // El status se pone por defecto en 'Abierta' en el servidor o constructor

        // 4. Enviar a PHP
        RetrofitClient.getApiService().crearIncidencia(nuevaIncidencia).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                btnRegistroIncidencia.setEnabled(true);
                btnRegistroIncidencia.setText("Registrar Incidencia");

                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {
                        Toast.makeText(NuevaIncidenciaActivity.this, "¡Incidencia creada correctamente!", Toast.LENGTH_SHORT).show();
                        finish(); // Cerramos la pantalla SOLO si fue exitoso
                    } else {
                        Toast.makeText(NuevaIncidenciaActivity.this, "Error: " + response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(NuevaIncidenciaActivity.this, "Error en el servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                btnRegistroIncidencia.setEnabled(true);
                btnRegistroIncidencia.setText("Registrar Incidencia");
                Log.e("API_INCIDENCIA", t.getMessage());
                Toast.makeText(NuevaIncidenciaActivity.this, "Fallo de conexión. Verifica tu internet o XAMPP.", Toast.LENGTH_LONG).show();
            }
        });
    }
}