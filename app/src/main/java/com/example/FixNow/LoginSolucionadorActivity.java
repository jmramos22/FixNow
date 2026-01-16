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

// --- IMPORTS NUEVOS ---
import com.example.FixNow.model.api.ApiResponse;
import com.example.FixNow.model.solucionador.Solucionador;
import com.example.FixNow.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginSolucionadorActivity extends AppCompatActivity {

    private Button btnIrSolucionador;
    private Button btnIrRegistrar;
    private EditText etMail;
    private EditText etContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_solucionador);

        initView();
    }

    private void initView() {
        btnIrSolucionador = findViewById(R.id.btn_Iniciar_Secion_Solucionador);
        btnIrRegistrar = findViewById(R.id.btn_Ir_Registro_Solucionador);
        etMail = findViewById(R.id.edt_Mail_Solucionador_login);
        etContrasena = findViewById(R.id.edt_Password_Solucionador_login);

        btnIrSolucionador.setOnClickListener(v -> validarYLogin());
        btnIrRegistrar.setOnClickListener(this::registroSolucionador);
    }

    private void validarYLogin() {
        String email = etMail.getText().toString().trim();
        String password = etContrasena.getText().toString().trim();

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

        btnIrSolucionador.setEnabled(false);
        btnIrSolucionador.setText("Verificando...");


        Solucionador credenciales = new Solucionador();
        credenciales.setMail(email);
        credenciales.setContrasena(password);

        RetrofitClient.getApiService().loginSolucionador(credenciales).enqueue(new Callback<ApiResponse<Solucionador>>() {
            @Override
            public void onResponse(Call<ApiResponse<Solucionador>> call, Response<ApiResponse<Solucionador>> response) {
                btnIrSolucionador.setEnabled(true);
                btnIrSolucionador.setText("Iniciar Sesión");

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Solucionador> respuesta = response.body();

                    if ("success".equals(respuesta.getStatus())) {
                        Solucionador solucionadorEncontrado = respuesta.getData();

                        if (solucionadorEncontrado != null) {
                            // Guardar sesión y entrar
                            guardarSesion(solucionadorEncontrado);
                            Toast.makeText(LoginSolucionadorActivity.this, "¡Bienvenido " + solucionadorEncontrado.getNombre() + "!", Toast.LENGTH_SHORT).show();

                            // Navegar al Main del Solucionador
                            Intent i = new Intent(LoginSolucionadorActivity.this, MainSolucionadorActivity.class); // Asegúrate que esta clase exista
                            startActivity(i);
                            finish();
                        }
                    } else {
                        Toast.makeText(LoginSolucionadorActivity.this, "Error: " + respuesta.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginSolucionadorActivity.this, "Error en el servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Solucionador>> call, Throwable t) {
                btnIrSolucionador.setEnabled(true);
                btnIrSolucionador.setText("Iniciar Sesión");
                Log.e("LOGIN_SOLUCIONADOR", t.getMessage());
                Toast.makeText(LoginSolucionadorActivity.this, "Fallo de conexión. Revisa internet o XAMPP.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void guardarSesion(Solucionador solucionador) {
        SharedPreferences sp = getSharedPreferences("sesion", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (solucionador.getIdSolucionador() > 0) {
            editor.putInt("idSolucionador", solucionador.getIdSolucionador());
        }

        editor.putString("nombreSolucionador", solucionador.getNombre() != null ? solucionador.getNombre() : "");
        editor.putString("mailSolucionador", solucionador.getMail() != null ? solucionador.getMail() : "");

        editor.putBoolean("esSolucionador", true);

        editor.apply();
    }

    public void registroSolucionador(View view) {
        Intent i = new Intent(this, RegistroSolucionadorActivity.class);
        startActivity(i);
    }
}