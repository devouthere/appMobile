package com.example.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtSenha;
    private Button btnLogin;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        btnLogin = findViewById(R.id.btnLogin);
        TextView txtTelefone = findViewById(R.id.txtTelefone);

        txtTelefone.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, LogarPhone.class);
            startActivity(intent);
        });
        
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String senha = edtSenha.getText().toString().trim();

                if (email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                loginWithEmailPassword(email, senha);
            }
        });
    }

    private void loginWithEmailPassword(String email, String senha) {
        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            verificarTipoDeUsuario(userId);
                        }
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Falha no login. Verifique as credenciais.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verificarTipoDeUsuario(String userId) {
        DocumentReference docRef = db.collection("usuarios").document(userId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String tipo = document.getString("tipoUsuario");
                    if ("barbeiro".equals(tipo)) {
                        startActivity(new Intent(LoginActivity.this, BarberDashboardActivity.class));
                    } else if ("cliente".equals(tipo)) {
                        startActivity(new Intent(LoginActivity.this, ClientesBarbeiroActivity.class));
                    } else {
                        Toast.makeText(LoginActivity.this, "Tipo de usuário inválido.", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Usuário não encontrado.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Erro ao obter dados.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
