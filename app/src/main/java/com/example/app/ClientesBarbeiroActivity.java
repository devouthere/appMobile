package com.example.app;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.BarbeiroAdapter;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;

public class ClientesBarbeiroActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BarbeiroAdapter adapter;
    private List<Barbeiro> listaBarbeiros = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes_barbeiro);

        recyclerView = findViewById(R.id.recyclerViewBarbeiros);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BarbeiroAdapter(listaBarbeiros);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        carregarBarbeiros();
    }

    private void carregarBarbeiros() {
        db.collection("barbeiro")  // Mudado para a coleção barbeiro
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String barbeiroId = doc.getId();
                        String nome = doc.getString("nome");
                        String email = doc.getString("email");
                        String endereco = doc.getString("endereco");

                        // Verificando se diasDisponiveis e servicos são nulos e inicializando como listas vazias se necessário
                        List<String> diasDisponiveis = (List<String>) doc.get("diasDisponiveis");
                        if (diasDisponiveis == null) {
                            diasDisponiveis = new ArrayList<>();  // Inicializa com lista vazia se for nulo
                        }

                        List<String> servicos = (List<String>) doc.get("servicos");
                        if (servicos == null) {
                            servicos = new ArrayList<>();  // Inicializa com lista vazia se for nulo
                        }

                        // Criando o objeto Barbeiro e adicionando à lista
                        Barbeiro barbeiro = new Barbeiro(barbeiroId, nome, email, endereco, diasDisponiveis, servicos);
                        listaBarbeiros.add(barbeiro);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Erro ao carregar barbeiros", e));
    }

}
