package com.example.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app.R;
import com.example.app.controller.BarberClientsAdapter;
import com.example.app.model.Agendamento;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class BarberClientsActivity extends AppCompatActivity {

    private static final String TAG = "BarberClientsActivity";
    private RecyclerView recyclerView;
    private BarberClientsAdapter adapter;
    private List<Agendamento> clientList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_clients);

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(BarberClientsActivity.this, BarberDashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        initializeFirebase();
        setupRecyclerView();
        loadBarberClients();
    }

    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.rv_barber_clients);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new BarberClientsAdapter(clientList, new BarberClientsAdapter.OnClientActionListener() {
            @Override
            public void onConfirm(Agendamento agendamento) {
                updateAppointmentStatus(agendamento, "confirmado");
            }

            @Override
            public void onCancel(Agendamento agendamento) {
                updateAppointmentStatus(agendamento, "cancelado");
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadBarberClients() {
        String barberId = auth.getCurrentUser().getUid();
        Log.d(TAG, "Carregando clientes para o barbeiro: " + barberId);

        db.collection("agendamentos")
                .whereEqualTo("barbeiroId", barberId)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Erro ao escutar agendamentos", error);
                        Toast.makeText(this, "Erro ao carregar agendamentos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        clientList.clear();
                        Log.d(TAG, "Total de agendamentos encontrados: " + queryDocumentSnapshots.size());

                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            processAppointmentDocument(doc);
                        }

                        // Ordena os agendamentos manualmente após carregar
                        clientList.sort((a1, a2) -> {
                            // Comparando primeiro por dia e depois por horário
                            int compareDia = a1.getDia().compareToIgnoreCase(a2.getDia());
                            if (compareDia == 0) {
                                return a1.getHorario().compareTo(a2.getHorario());
                            } else {
                                return compareDia;
                            }
                        });

                        adapter.notifyDataSetChanged();

                    } else {
                        Log.d(TAG, "Nenhum agendamento encontrado");
                        Toast.makeText(this, "Nenhum agendamento encontrado", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void processAppointmentDocument(DocumentSnapshot doc) {
        Agendamento agendamento = doc.toObject(Agendamento.class);
        if (agendamento != null) {
            agendamento.setId(doc.getId());
            Log.d(TAG, "Processando agendamento: " + agendamento.toString());

            db.collection("usuarios").document(agendamento.getClienteId())
                    .get()
                    .addOnSuccessListener(clientDoc -> {
                        if (clientDoc.exists()) {
                            agendamento.setClienteNome(clientDoc.getString("nome"));
                            Log.d(TAG, "Cliente encontrado: " + agendamento.getClienteNome());
                        } else {
                            agendamento.setClienteNome("Cliente não encontrado");
                            Log.w(TAG, "Documento do cliente não encontrado para ID: " + agendamento.getClienteId());
                        }

                        clientList.add(agendamento);
                        adapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Erro ao buscar cliente", e);
                        agendamento.setClienteNome("Erro ao carregar");
                        clientList.add(agendamento);
                        adapter.notifyDataSetChanged();
                    });
        }
    }

    private void updateAppointmentStatus(Agendamento agendamento, String status) {
        db.collection("agendamentos")
                .document(agendamento.getId())
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Status atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                    int position = clientList.indexOf(agendamento);
                    if (position != -1) {
                        clientList.get(position).setStatus(status);
                        adapter.notifyItemChanged(position);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao atualizar status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Erro ao atualizar status", e);
                });
    }
}