package com.example.app.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app.R;
import com.example.app.view.RegisterActivity;
import com.example.app.view.SendCodeActivity;

public class ChooseOption extends AppCompatActivity {

    private boolean isPhoneLogin; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_choose);


        isPhoneLogin = getIntent().getBooleanExtra("isPhoneLogin", false);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        CardView btnBarbeiro = findViewById(R.id.btnBarbeiro);
        CardView btnCliente = findViewById(R.id.btnCliente);


        btnBarbeiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaRegistro(true);
            }
        });


        btnCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaRegistro(false);
            }
        });
    }


    private void abrirTelaRegistro(boolean isBarbeiro) {
        Intent intent;

        if (isPhoneLogin) {

            intent = new Intent(ChooseOption.this, SendCodeActivity.class);
        } else {

            intent = new Intent(ChooseOption.this, RegisterActivity.class);
        }

        intent.putExtra("isBarbeiro", isBarbeiro);
        intent.putExtra("isPhoneLogin", isPhoneLogin);
        startActivity(intent);
    }
}