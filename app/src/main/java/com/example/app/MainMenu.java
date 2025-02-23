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

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_menu);

        // Ajuste para compatibilidade com barras de sistema (navegação, status)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referência ao botão "SignWithEmail"
        Button signWithEmailButton = findViewById(R.id.SignWithEmail);

        // Adicionando evento de clique para "SignWithEmail" - vai para ChooseOption
        signWithEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Criando a intent para ir para ChooseOption com login via email
                Intent intent = new Intent(MainMenu.this, ChooseOption.class);
                intent.putExtra("isPhoneLogin", false); // Indica que o login será via e-mail
                startActivity(intent);
            }
        });

        // Referência ao botão "SignWithLogin"
        Button signWithLoginButton = findViewById(R.id.SignWithLogin);

        // Adicionando evento de clique para "SignWithLogin" - vai para LoginActivity
        signWithLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Criando a intent para ir para LoginActivity
                Intent intent = new Intent(MainMenu.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // **Novo botão SignWithTelefone** - Redirecionando para ChooseOption
        Button signWithTelefoneButton = findViewById(R.id.SignWithTelefone);

        // Adicionando evento de clique para "SignWithTelefone" - vai para ChooseOption com login via telefone
        signWithTelefoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Criando a intent para ir para ChooseOption com login via telefone
                Intent intent = new Intent(MainMenu.this, ChooseOption.class);
                intent.putExtra("isPhoneLogin", true); // Indica que o login será via telefone
                startActivity(intent);
            }
        });
    }
}
