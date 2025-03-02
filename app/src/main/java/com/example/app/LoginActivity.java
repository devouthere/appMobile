package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    // Referências aos campos de entrada
    private EditText edtEmail, edtSenha;
    private Button btnLogin;
    private FirebaseFirestore db; // Inicialização do Firestore
    // Instância do FirebaseAuth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        // Inicializando o FirebaseAuth e o Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Inicializando o Firestore

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
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            verificarTipoDeUsuario(userId);
                        }
                        finish();
                    } else {
                        // Caso o login falhe
                        Toast.makeText(LoginActivity.this, "Falha no login. Verifique as credenciais.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verificarTipoDeUsuario(String userId) {
        // Referência ao documento do usuário
        DocumentReference docRef = db.collection("usuarios").document(userId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String tipo = document.getString("tipoUsuario"); // "cliente" ou "barbeiro"

                    // Verifica o tipo de usuário
                    if ("barbeiro".equals(tipo)) {
                        // Redireciona para a Dashboard do Barbeiro
                        startActivity(new Intent(LoginActivity.this, BarberDashboardActivity.class));
                    } else if ("cliente".equals(tipo)) {
                        // Redireciona para a Dashboard do Cliente
                        startActivity(new Intent(LoginActivity.this, ClientesBarbeiroActivity.class));
                    } else {
                        // Caso o tipo seja inválido
                        Toast.makeText(LoginActivity.this, "Tipo de usuário inválido.", Toast.LENGTH_SHORT).show();
                    }
                    finish(); // Encerra a tela de login
                } else {
                    Toast.makeText(LoginActivity.this, "Usuário não encontrado.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Erro ao obter dados.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
