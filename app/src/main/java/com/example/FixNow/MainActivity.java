package com.example.FixNow;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button BtnCliente;
    private Button BtnSolucionador;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        initView();

    }
    private void initView(){

        BtnCliente = findViewById(R.id.btn_Cliente);
        BtnSolucionador= findViewById(R.id.btn_Solucionador);
    }

    public void irInicioCliente(View view){
        Intent i = new Intent(this, LoginClienteActivity.class);
        startActivity(i);
    }


    public void irInicioSolucionador(View view){
       Intent i = new Intent(this, LoginSolucionadorActivity.class);
       startActivity(i);
   }


}