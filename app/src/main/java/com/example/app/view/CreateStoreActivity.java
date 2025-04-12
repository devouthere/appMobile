package com.example.app.view;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.model.BarbeiroResposta;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateStoreActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageView backArrow; // Adicione esta linha

    private CheckBox chkCorte, chkBarba, chkSobrancelha;
    private CheckBox chkDia1, chkDia2, chkDia3, chkDia4, chkDia5, chkDia6, chkDia7;
    private MaterialButton btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_store);

            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            inicializarViews();

            backArrow.setOnClickListener(v -> {
                Intent intent = new Intent(CreateStoreActivity.this, BarberDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });

            btnSalvar.setOnClickListener(v -> salvarRespostas());

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao inicializar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("CreateStoreActivity", "Erro no onCreate", e);
        }
    }

    private void inicializarViews() {
        backArrow = findViewById(R.id.backArrow);

        chkCorte = findViewById(R.id.chkCorte);
        chkBarba = findViewById(R.id.chkBarba);
        chkSobrancelha = findViewById(R.id.chkSobrancelha);

        chkDia1 = findViewById(R.id.chkDia1);
        chkDia2 = findViewById(R.id.chkDia2);
        chkDia3 = findViewById(R.id.chkDia3);
        chkDia4 = findViewById(R.id.chkDia4);
        chkDia5 = findViewById(R.id.chkDia5);
        chkDia6 = findViewById(R.id.chkDia6);
        chkDia7 = findViewById(R.id.chkDia7);

        btnSalvar = findViewById(R.id.btnSalvar);
    }

    private void salvarRespostas() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Você precisa estar logado para salvar as configurações.", Toast.LENGTH_LONG).show();
            return;
        }

        List<String> servicosEscolhidos = new ArrayList<>();
        if (chkCorte.isChecked()) servicosEscolhidos.add("Corte de Cabelo");
        if (chkBarba.isChecked()) servicosEscolhidos.add("Barba");
        if (chkSobrancelha.isChecked()) servicosEscolhidos.add("Sobrancelha");

        List<String> diasDisponiveis = new ArrayList<>();
        if (chkDia1.isChecked()) diasDisponiveis.add("Segunda-feira");
        if (chkDia2.isChecked()) diasDisponiveis.add("Terça-feira");
        if (chkDia3.isChecked()) diasDisponiveis.add("Quarta-feira");
        if (chkDia4.isChecked()) diasDisponiveis.add("Quinta-feira");
        if (chkDia5.isChecked()) diasDisponiveis.add("Sexta-feira");
        if (chkDia6.isChecked()) diasDisponiveis.add("Sábado");
        if (chkDia7.isChecked()) diasDisponiveis.add("Domingo");

        if (servicosEscolhidos.isEmpty() || diasDisponiveis.isEmpty()) {
            Toast.makeText(this, "Por favor, selecione ao menos um serviço e um dia.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        db.collection("usuarios").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nome = documentSnapshot.getString("nome");
                        String email = documentSnapshot.getString("email");
                        String endereco = documentSnapshot.getString("endereco");

                        BarbeiroResposta respostas = new BarbeiroResposta(servicosEscolhidos, diasDisponiveis);

                        Map<String, Object> barbeiroData = new HashMap<>();
                        barbeiroData.put("userId", userId);
                        barbeiroData.put("nome", nome);
                        barbeiroData.put("email", email);
                        barbeiroData.put("endereco", endereco);
                        barbeiroData.put("servicos", servicosEscolhidos);
                        barbeiroData.put("diasDisponiveis", diasDisponiveis);

                        db.collection("barbeiro").document(userId)
                                .set(barbeiroData)
                                .addOnSuccessListener(aVoid -> {
                                    // Redireciona para a tela de confirmação
                                    Intent intent = new Intent(CreateStoreActivity.this, ConfirmationActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(CreateStoreActivity.this, "Erro ao salvar as configurações: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    } else {
                        Toast.makeText(CreateStoreActivity.this, "Dados do usuário não encontrados.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CreateStoreActivity.this, "Erro ao recuperar dados do usuário: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}