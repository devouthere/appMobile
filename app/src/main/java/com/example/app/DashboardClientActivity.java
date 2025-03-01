package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class DashboardClientActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvClientName, tvClientEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_client);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicializa os TextViews para mostrar informações do cliente
        tvClientName = findViewById(R.id.tvClientName);
        tvClientEmail = findViewById(R.id.tvClientEmail);

        // Verifica se o usuário está logado
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            carregarDadosCliente(userId);
        } else {
            // Se não houver usuário autenticado, exibe mensagem ou redireciona
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show();
            // Redireciona para a tela de login
            startActivity(new Intent(DashboardClientActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void carregarDadosCliente(String userId) {
        DocumentReference docRef = db.collection("usuarios").document(userId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String nome = document.getString("nome");
                    String email = document.getString("email");

                    // Exibe os dados do cliente na UI
                    tvClientName.setText("Nome: " + nome);
                    tvClientEmail.setText("E-mail: " + email);
                } else {
                    Toast.makeText(DashboardClientActivity.this, "Documento não encontrado!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(DashboardClientActivity.this, "Erro ao carregar dados!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
