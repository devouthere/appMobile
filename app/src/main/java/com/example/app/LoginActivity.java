package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    // Referências aos campos de entrada
    private EditText edtEmail, edtSenha;
    private Button btnLogin;

    // Instância do FirebaseAuth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        // Inicializando o FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Referências aos campos e botão
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        btnLogin = findViewById(R.id.btnLogin);

        // Definir a ação do botão de login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obter e-mail e senha do usuário
                String email = edtEmail.getText().toString().trim();
                String senha = edtSenha.getText().toString().trim();

                // Verificar se os campos não estão vazios
                if (email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Realizar o login
                loginWithEmailPassword(email, senha);
            }
        });
    }

    private void loginWithEmailPassword(String email, String senha) {
        // Autenticar com Firebase
        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login bem-sucedido
                        Toast.makeText(LoginActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                        // Redirecionar para a tela principal após login
                        Intent intent = new Intent(LoginActivity.this, MainMenu.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Caso o login falhe
                        Toast.makeText(LoginActivity.this, "Falha no login. Verifique as credenciais.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
