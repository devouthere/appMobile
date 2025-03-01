package com.example.app;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CreateStoreActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private CheckBox chkCorte, chkBarba, chkSobrancelha;
    private CheckBox chkDia1, chkDia2, chkDia3, chkDia4, chkDia5, chkDia6, chkDia7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_store);

        // Inicializando Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Referências para os Checkboxes
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

        // Botão de salvar
        findViewById(R.id.btnSalvar).setOnClickListener(v -> salvarRespostas());
    }

    private void salvarRespostas() {
        // Coletando os serviços escolhidos
        List<String> servicosEscolhidos = new ArrayList<>();
        if (chkCorte.isChecked()) servicosEscolhidos.add("Corte de Cabelo");
        if (chkBarba.isChecked()) servicosEscolhidos.add("Barba");
        if (chkSobrancelha.isChecked()) servicosEscolhidos.add("Sobrancelha");

        // Coletando os dias disponíveis
        List<String> diasDisponiveis = new ArrayList<>();
        if (chkDia1.isChecked()) diasDisponiveis.add("Segunda-feira");
        if (chkDia2.isChecked()) diasDisponiveis.add("Terça-feira");
        if (chkDia3.isChecked()) diasDisponiveis.add("Quarta-feira");
        if (chkDia4.isChecked()) diasDisponiveis.add("Quinta-feira");
        if (chkDia5.isChecked()) diasDisponiveis.add("Sexta-feira");
        if (chkDia6.isChecked()) diasDisponiveis.add("Sábado");
        if (chkDia7.isChecked()) diasDisponiveis.add("Domingo");

        // Verificando se o usuário está logado
        String userId = mAuth.getCurrentUser().getUid();

        // Criando uma instância de BarbeiroResposta
        if (servicosEscolhidos.isEmpty() || diasDisponiveis.isEmpty()) {
            Toast.makeText(this, "Por favor, selecione ao menos um serviço e um dia.", Toast.LENGTH_SHORT).show();
            return;
        }

        BarbeiroResposta respostas = new BarbeiroResposta(servicosEscolhidos, diasDisponiveis);

        // Salvando no Firestore
        db.collection("barbeiros_respostas")
                .document(userId)
                .set(respostas)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Respostas salvas com sucesso!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao salvar as respostas: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
