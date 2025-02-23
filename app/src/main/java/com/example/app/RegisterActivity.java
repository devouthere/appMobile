package com.example.app;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_register);

        // Referência para os campos
        EditText edtNome = findViewById(R.id.edtNome);
        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtSenha = findViewById(R.id.edtSenha);
        EditText edtEndereco = findViewById(R.id.edtEndereco); // Campo de endereço

        // Obter a informação passada da tela anterior
        boolean isBarbeiro = getIntent().getBooleanExtra("isBarbeiro", false);

        // Se o usuário for barbeiro, mostra o campo de endereço
        if (isBarbeiro) {
            edtEndereco.setVisibility(View.VISIBLE);
        } else {
            edtEndereco.setVisibility(View.GONE);
        }

        // Configurar a visibilidade dos outros campos
        if (!isBarbeiro) {
            // Aqui você pode manter os campos de "Nome", "Email" e "Senha" visíveis
            // Como são os campos comuns entre barbeiro e cliente, não precisa fazer nada
        }
    }
}
