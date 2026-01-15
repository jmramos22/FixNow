package com.example.FixNow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

// --- IMPORTS NUEVOS PARA RETROFIT ---
import com.example.FixNow.model.api.ApiResponse;
import com.example.FixNow.model.cliente.Cliente;
import com.example.FixNow.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginClienteActivity extends AppCompatActivity {

    private Button btnIrRegistrar;
    private Button btnInicioSesion;
    private EditText etMail;
    private EditText etContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_cliente);

        initView();
    }

    private void initView() {
        btnInicioSesion = findViewById(R.id.btn_Iniciar_Secion_Cliente);
        btnIrRegistrar = findViewById(R.id.btn_Ir_Registro_Cliente);
        etMail = findViewById(R.id.edt_Mail_Cliente_login);
        etContrasena = findViewById(R.id.edt_Password_Cliente_login);

        // Usamos expresiones lambda para los clicks
        btnInicioSesion.setOnClickListener(v -> validarYLogin());
        btnIrRegistrar.setOnClickListener(this::registroCliente);
    }

    private void validarYLogin() {
        String email = etMail.getText().toString().trim();
        String password = etContrasena.getText().toString().trim();

        // --- VALIDACIONES ---
        if (TextUtils.isEmpty(email)) {
            etMail.setError("El correo es obligatorio");
            etMail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etMail.setError("Ingresa un correo válido");
            etMail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etContrasena.setError("La contraseña es obligatoria");
            etContrasena.requestFocus();
            return;
        }

        // --- INICIO DEL LOGIN ---
        // Deshabilitamos botón para evitar doble click
        btnInicioSesion.setEnabled(false);
        btnInicioSesion.setText("Verificando...");

        // 1. Preparamos el objeto con los datos a enviar
        Cliente credenciales = new Cliente();
        credenciales.setMail(email);
        credenciales.setContrasena(password);

        // 2. Llamada a la API PHP con Retrofit
        RetrofitClient.getApiService().loginCliente(credenciales).enqueue(new Callback<ApiResponse<Cliente>>() {
            @Override
            public void onResponse(Call<ApiResponse<Cliente>> call, Response<ApiResponse<Cliente>> response) {
                // Reactivamos el botón pase lo que pase
                btnInicioSesion.setEnabled(true);
                btnInicioSesion.setText("Iniciar Sesión");

                // Verificamos si hubo respuesta del servidor (HTTP 200)
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Cliente> respuestaApi = response.body();

                    // Verificamos si la lógica de PHP dijo "success"
                    if ("success".equals(respuestaApi.getStatus())) {

                        Cliente clienteEncontrado = respuestaApi.getData();

                        if (clienteEncontrado != null) {
                            // --- LOGIN EXITOSO ---
                            guardarSesion(clienteEncontrado);

                            Toast.makeText(LoginClienteActivity.this, "¡Bienvenido " + clienteEncontrado.getNombre() + "!", Toast.LENGTH_SHORT).show();

                            // Navegar al Dashboard
                            Intent i = new Intent(LoginClienteActivity.this, MisIncidenciasActivity.class);
                            startActivity(i);
                            finish();
                        }
                    } else {
                        // El servidor respondió, pero dijo "error" (ej. contraseña mal)
                        Toast.makeText(LoginClienteActivity.this, "Error: " + respuestaApi.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Error HTTP (ej. 404, 500)
                    Toast.makeText(LoginClienteActivity.this, "Error en el servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Cliente>> call, Throwable t) {
                // Error de conexión (Sin internet, XAMPP apagado, IP incorrecta)
                btnInicioSesion.setEnabled(true);
                btnInicioSesion.setText("Iniciar Sesión");

                Log.e("LOGIN_ERROR", "Fallo: " + t.getMessage());
                Toast.makeText(LoginClienteActivity.this, "Fallo de conexión. Revisa tu internet o XAMPP.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void guardarSesion(Cliente cliente) {
        SharedPreferences sp = getSharedPreferences("sesion", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        // NOTA: En el nuevo modelo Java, el método es getId(), no getIdCliente()
        editor.putInt("idCliente", cliente.getId());

        // Validamos nulos para evitar cierre inesperado
        editor.putString("nombreCliente", cliente.getNombre() != null ? cliente.getNombre() : "");
        editor.putString("mailCliente", cliente.getMail() != null ? cliente.getMail() : "");

        // Bandera para saber que está logueado
        editor.putBoolean("estado", true);

        editor.apply();
    }

    public void registroCliente(View view) {
        Intent i = new Intent(this, RegistroClienteActivity.class);
        startActivity(i);
    }
}