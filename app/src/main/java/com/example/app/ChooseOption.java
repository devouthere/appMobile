package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChooseOption extends AppCompatActivity {

    private boolean isPhoneLogin; // Variável para armazenar o tipo de login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_choose);

        // Recebe a informação do tipo de login
        isPhoneLogin = getIntent().getBooleanExtra("isPhoneLogin", false);

        // Configuração do padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referências para os botões
        Button btnBarbeiro = findViewById(R.id.btnBarbeiro);
        Button btnCliente = findViewById(R.id.btnCliente);

        // Ação de clique para abrir a tela de Registro como Barbeiro
        btnBarbeiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaRegistro(true);
            }
        });

        // Ação de clique para abrir a tela de Registro como Cliente
        btnCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaRegistro(false);
            }
        });
    }

    // Método para abrir a tela de registro e passar os parâmetros necessários
    private void abrirTelaRegistro(boolean isBarbeiro) {
        Intent intent = new Intent(ChooseOption.this, RegisterActivity.class);
        intent.putExtra("isBarbeiro", isBarbeiro);
        intent.putExtra("isPhoneLogin", isPhoneLogin); // Passa se o login é via telefone ou não
        startActivity(intent);
    }
}
