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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_choose);

        // Configuração do padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referências para os botões
        Button btnBarbeiro = findViewById(R.id.btnBarbeiro);
        Button btnCliente = findViewById(R.id.btnCliente);  // Novo botão para cliente

        // Ação de clique para abrir a tela de Registro como Barbeiro
        btnBarbeiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseOption.this, RegisterActivity.class);
                intent.putExtra("isBarbeiro", true); // Passa a informação de que é barbeiro
                startActivity(intent);
            }
        });

        // Ação de clique para abrir a tela de Registro como Cliente
        btnCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseOption.this, RegisterActivity.class);
                intent.putExtra("isBarbeiro", false); // Passa a informação de que é cliente
                startActivity(intent);
            }
        });
    }
}
