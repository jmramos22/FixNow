package com.example.FixNow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

// --- IMPORTS NUEVOS ---
import com.example.FixNow.model.api.ApiResponse;
import com.example.FixNow.model.cliente.Cliente;
import com.example.FixNow.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroClienteActivity extends AppCompatActivity {

    private Button btnRegitro_Cliente;
    private EditText etNombre;
    private EditText etPaterno;
    private EditText etMaterno;
    private EditText etMail;
    private EditText etContrasena;
    private EditText etContrasenaConf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_cliente);

        initView();

        btnRegitro_Cliente.setOnClickListener(view -> {
            String nombre = etNombre.getText().toString().trim();
            String paterno = etPaterno.getText().toString().trim();
            String materno = etMaterno.getText().toString().trim();
            String mail = etMail.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();
            String contrasenaConf = etContrasenaConf.getText().toString().trim();

            // --- VALIDACIONES ---
            if(nombre.isEmpty() || paterno.isEmpty() || materno.isEmpty() || mail.isEmpty() || contrasena.isEmpty()){
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!contrasena.equals(contrasenaConf)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                etContrasenaConf.setError("No coincide");
                return;
            }

            // Si todo está bien, guardamos
            saveCliente(nombre, paterno, materno, mail, contrasena);
        });
    }

    private void initView() {
        btnRegitro_Cliente = findViewById(R.id.btnRegistro_Incidencia); // Ojo: tu ID en XML parece de incidencia, pero lo respeto
        etNombre = findViewById(R.id.edt_Nombre_cliente);
        etPaterno = findViewById(R.id.edt_ApPaterno_cliente);
        etMaterno = findViewById(R.id.edt_ApMaterno_cliente);
        etMail = findViewById(R.id.edt_Mail_cliente);
        etContrasena = findViewById(R.id.edt_Password_cliente);
        etContrasenaConf = findViewById(R.id.edt_Confirmar_Password_cliente);
    }

    private void saveCliente(String nombre, String paterno, String materno, String mail, String contrasena) {

        // Deshabilitar botón para evitar doble registro
        btnRegitro_Cliente.setEnabled(false);
        btnRegitro_Cliente.setText("Guardando...");

        // 1. Crear el objeto Cliente (Modelo Java)
        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setNombre(nombre);
        nuevoCliente.setApPaterno(paterno);
        nuevoCliente.setApMaterno(materno);
        nuevoCliente.setMail(mail);
        nuevoCliente.setContrasena(contrasena);

        // 2. Enviar a la API PHP usando Retrofit
        RetrofitClient.getApiService().registrarCliente(nuevoCliente).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                // Volver a habilitar el botón
                btnRegitro_Cliente.setEnabled(true);
                btnRegitro_Cliente.setText("Registrar");

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> respuesta = response.body();

                    if ("success".equals(respuesta.getStatus())) {
                        // --- ÉXITO ---
                        Toast.makeText(RegistroClienteActivity.this, "¡Registro Exitoso!", Toast.LENGTH_SHORT).show();

                        // Ir al Login para que inicie sesión
                        Intent intent = new Intent(RegistroClienteActivity.this, LoginClienteActivity.class);
                        startActivity(intent);
                        finish(); // Cerramos registro para que no pueda volver atrás
                    } else {
                        // Error lógico (ej. Correo duplicado)
                        Toast.makeText(RegistroClienteActivity.this, "Error: " + respuesta.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Error del servidor (500, 404)
                    Toast.makeText(RegistroClienteActivity.this, "Error en el servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                // Error de conexión
                btnRegitro_Cliente.setEnabled(true);
                btnRegitro_Cliente.setText("Registrar");
                Log.e("REGISTRO_ERROR", t.getMessage());
                Toast.makeText(RegistroClienteActivity.this, "Fallo de conexión: Revisa tu internet o XAMPP", Toast.LENGTH_LONG).show();
            }
        });
    }
}