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
import com.example.FixNow.model.solucionador.Solucionador;
import com.example.FixNow.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroSolucionadorActivity extends AppCompatActivity {

    private Button btnRegitro_Solucionador;
    private EditText etNombre, etPaterno, etMaterno, etMail, etContrasena, etContrasenaConf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_solucionador);

        initView();

        btnRegitro_Solucionador.setOnClickListener(view -> {
            String nombre = etNombre.getText().toString().trim();
            String paterno = etPaterno.getText().toString().trim();
            String materno = etMaterno.getText().toString().trim();
            String mail = etMail.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();
            String contrasenaConf = etContrasenaConf.getText().toString().trim();

            if(nombre.isEmpty() || paterno.isEmpty() || materno.isEmpty() || mail.isEmpty() || contrasena.isEmpty()){
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!contrasena.equals(contrasenaConf)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            saveSolucionador(nombre, paterno, materno, mail, contrasena);
        });
    }

    private void initView(){
        btnRegitro_Solucionador = findViewById(R.id.btnRegistro_solucionador);
        etNombre = findViewById(R.id.edt_Nombre_solucionador);
        etPaterno = findViewById(R.id.edt_ApPaterno_solucionador);
        etMaterno = findViewById(R.id.edt_ApMaterno_solucionador);
        etMail = findViewById(R.id.edt_Mail_solucionador);
        etContrasena = findViewById(R.id.edt_Password_solucionador);
        etContrasenaConf = findViewById(R.id.edt_Confirmar_Password_solucionador);
    }

    private void saveSolucionador(String nombre, String paterno, String materno, String mail, String contrasena) {

        btnRegitro_Solucionador.setEnabled(false);
        btnRegitro_Solucionador.setText("Registrando...");

        // 1. Crear el objeto Solucionador
        Solucionador nuevoSol = new Solucionador();
        nuevoSol.setNombre(nombre);
        nuevoSol.setApPaterno(paterno);
        nuevoSol.setApMaterno(materno);
        nuevoSol.setMail(mail);
        nuevoSol.setContrasena(contrasena);

        // 2. Enviar a la API
        RetrofitClient.getApiService().registrarSolucionador(nuevoSol).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                btnRegitro_Solucionador.setEnabled(true);
                btnRegitro_Solucionador.setText("Registrar");

                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {
                        // --- ÉXITO ---
                        Toast.makeText(RegistroSolucionadorActivity.this, "Registro Completado Correctamente", Toast.LENGTH_SHORT).show();
                        showLoginSolucionador();
                    } else {
                        // Error (ej. correo duplicado)
                        Toast.makeText(RegistroSolucionadorActivity.this, "Error: " + response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegistroSolucionadorActivity.this, "Error en el servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                btnRegitro_Solucionador.setEnabled(true);
                btnRegitro_Solucionador.setText("Registrar");
                Log.e("REGISTRO_SOL", t.getMessage());
                Toast.makeText(RegistroSolucionadorActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoginSolucionador() {
        Intent intent = new Intent(RegistroSolucionadorActivity.this, LoginSolucionadorActivity.class);
        startActivity(intent);
        finish();
    }
}