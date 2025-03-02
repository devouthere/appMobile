package com.example.app;

import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

    private CheckBox chkCorte, chkBarba, chkSobrancelha;
    private CheckBox chkDia1, chkDia2, chkDia3, chkDia4, chkDia5, chkDia6, chkDia7;
    private MaterialButton btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_store);

            // Inicializando Firebase
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            // Inicializando views
            inicializarViews();

            // Configurando o botão de salvar
            btnSalvar.setOnClickListener(v -> salvarRespostas());

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao inicializar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("CreateStoreActivity", "Erro no onCreate", e);
        }
    }

    private void inicializarViews() {
        // Referências para os Checkboxes de serviços
        chkCorte = findViewById(R.id.chkCorte);
        chkBarba = findViewById(R.id.chkBarba);
        chkSobrancelha = findViewById(R.id.chkSobrancelha);

        // Referências para os Checkboxes de dias disponíveis
        chkDia1 = findViewById(R.id.chkDia1);
        chkDia2 = findViewById(R.id.chkDia2);
        chkDia3 = findViewById(R.id.chkDia3);
        chkDia4 = findViewById(R.id.chkDia4);
        chkDia5 = findViewById(R.id.chkDia5);
        chkDia6 = findViewById(R.id.chkDia6);
        chkDia7 = findViewById(R.id.chkDia7);

        // Referência para o botão
        btnSalvar = findViewById(R.id.btnSalvar);
    }

    private void salvarRespostas() {
        // Verificar se o usuário está autenticado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Você precisa estar logado para salvar as configurações.", Toast.LENGTH_LONG).show();
            return;
        }

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

        // Verificação se ao menos um serviço e um dia foram selecionados
        if (servicosEscolhidos.isEmpty() || diasDisponiveis.isEmpty()) {
            Toast.makeText(this, "Por favor, selecione ao menos um serviço e um dia.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obter o ID do usuário atual
        String userId = currentUser.getUid();

        // Criar um objeto BarbeiroResposta
        BarbeiroResposta respostas = new BarbeiroResposta(servicosEscolhidos, diasDisponiveis);

        // Desabilitar o botão para evitar cliques múltiplos
        btnSalvar.setEnabled(false);

        // Exibir feedback visual de carregamento
        btnSalvar.setText("Salvando...");

        // Salvando no Firestore
        db.collection("barbeiros_respostas")
                .document(userId)
                .set(respostas)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CreateStoreActivity.this, "Configurações salvas com sucesso!", Toast.LENGTH_SHORT).show();
                    // Opcionalmente, fechar a activity após o sucesso
                    // finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CreateStoreActivity.this, "Erro ao salvar as configurações: " + e.getMessage(), Toast.LENGTH_LONG).show();
                })
                .addOnCompleteListener(task -> {
                    // Reabilitar o botão e restaurar o texto original
                    btnSalvar.setEnabled(true);
                    btnSalvar.setText("Salvar");
                });
    }
}