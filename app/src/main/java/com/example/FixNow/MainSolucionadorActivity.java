package com.example.FixNow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainSolucionadorActivity extends AppCompatActivity {

    public Button irIn;
    public TextView tvSaludo;
    public Button btCerrar;
    public Button irOr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_solicionador);

        SharedPreferences sp = getSharedPreferences("sesion", MODE_PRIVATE);
        String nombreUsuario = sp.getString("nombreSolucionador", "Solucionador");
        int idSolucionador = sp.getInt("idSolucionador", -1);

        if (idSolucionador == -1) {
            startActivity(new Intent(this, LoginSolucionadorActivity.class));
            finish();
            return;
        }

        tvSaludo = findViewById(R.id.tvFuncionaID);
        btCerrar = findViewById(R.id.CerrarS);
        irOr = findViewById(R.id.IrOr);
        irIn = findViewById(R.id.irIn);

        tvSaludo.setText("Hola, " + nombreUsuario);

        irOr.setOnClickListener(this::irAOrdenesTrabajo);
        irIn.setOnClickListener(this::irAListaIncidencias);
        btCerrar.setOnClickListener(this::cerrarSesion);
    }

    public void irAListaIncidencias(View view) {
        Intent i = new Intent(this, ListaIncidenciasActivity.class);
        startActivity(i);
    }

    public void irAOrdenesTrabajo(View view) {
        Intent i = new Intent(this, OrdenesTrabajoActivity.class);
        startActivity(i);
    }

    public void cerrarSesion(View view) {
        SharedPreferences sp = getSharedPreferences("sesion", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear(); // Borrar todos los datos de sesión
        editor.apply();

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}