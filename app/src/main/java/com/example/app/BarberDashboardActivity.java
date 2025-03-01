package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class BarberDashboardActivity extends AppCompatActivity {

    private TextView txtNome, txtEndereco, tvUserId;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Button btnCriarLoja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_dashboard);

        // Referência para os TextViews
        txtNome = findViewById(R.id.txtNome);
        txtEndereco = findViewById(R.id.txtEndereco);
        tvUserId = findViewById(R.id.tvUserId);

        // Inicializando Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnCriarLoja = findViewById(R.id.btnCriarLoja);

        btnCriarLoja.setOnClickListener(v -> {
            // Criando uma Intent para navegar para a Activity CreateStore
            Intent intent = new Intent(BarberDashboardActivity.this, CreateStoreActivity.class);
            startActivity(intent);  // Iniciando a Activity
        });

        // Buscar UID e exibir na tela
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            tvUserId.setText("ID: " + uid);
            Log.d("FirebaseAuth", "UID do usuário: " + uid);
            carregarDadosBarbeiro(uid);
        } else {
            tvUserId.setText("Nenhum usuário logado");
            Log.d("FirebaseAuth", "Nenhum usuário logado.");
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show();
        }
    }

    private void carregarDadosBarbeiro(String userId) {
        DocumentReference docRef = db.collection("usuarios").document(userId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String nome = document.getString("nome");
                    String endereco = document.getString("endereco");

                    txtNome.setText("Nome: " + nome);
                    txtEndereco.setText("Endereço: " + endereco);

                    Log.d("Firestore", "Nome: " + nome + ", Endereço: " + endereco);
                } else {
                    Log.w("Firestore", "Documento não encontrado para UID: " + userId);
                    Toast.makeText(this, "Dados do barbeiro não encontrados!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("Firestore", "Erro ao buscar documento", task.getException());
                Toast.makeText(this, "Erro ao buscar dados: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
