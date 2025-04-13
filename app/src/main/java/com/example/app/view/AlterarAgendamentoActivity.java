package com.example.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.model.Agendamento;
import com.example.app.model.Barbeiro;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlterarAgendamentoActivity extends AppCompatActivity {

    private LinearLayout llServicos;
    private Spinner spinnerDias;
    private TimePicker timePicker;
    private TextView tvBarbeiroNome;
    private Button btnConfirmar;
    private FirebaseFirestore db;
    private String agendamentoId;
    private Agendamento agendamentoAtual;
    private final String TAG = "AlterarAgendamento";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_agendamento);

        // Configuração da seta de voltar
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(AlterarAgendamentoActivity.this, DashboardClientActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        llServicos = findViewById(R.id.llServicos);
        spinnerDias = findViewById(R.id.spinnerDias);
        tvBarbeiroNome = findViewById(R.id.tvBarbeiroNome);
        btnConfirmar = findViewById(R.id.btnConfirmar);
        db = FirebaseFirestore.getInstance();

        setTitle("Alterar Agendamento");

        if (getIntent().hasExtra("id")) {
            agendamentoId = getIntent().getStringExtra("id");
            Log.d(TAG, "Agendamento ID recebido: " + agendamentoId);
            carregarAgendamento(agendamentoId);
        } else {
            Toast.makeText(this, "Erro: ID do agendamento não fornecido.", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnConfirmar.setOnClickListener(v -> salvarAlteracoes());
    }

    private void carregarAgendamento(String agendamentoId) {
        db.collection("agendamentos").whereEqualTo("id", agendamentoId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (var documentSnapshot : queryDocumentSnapshots) {
                            agendamentoAtual = documentSnapshot.toObject(Agendamento.class);

                            if (agendamentoAtual != null) {
                                Log.d(TAG, "Agendamento encontrado: " + agendamentoAtual.toString());

                                Log.d(TAG, "BarbeiroId: " + agendamentoAtual.getBarbeiroId());
                                String barbeiroId = agendamentoAtual.getBarbeiroId();

                                if (barbeiroId != null && !barbeiroId.isEmpty()) {
                                    Log.d(TAG, "BarbeiroId encontrado: " + barbeiroId);
                                    carregarBarbeiro(barbeiroId);
                                } else {
                                    Log.e(TAG, "BarbeiroId é nulo ou vazio");
                                    Toast.makeText(AlterarAgendamentoActivity.this,
                                            "Erro: Barbeiro não encontrado", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            } else {
                                Log.e(TAG, "Agendamento não encontrado ou inválido.");
                            }
                        }
                    } else {
                        Toast.makeText(AlterarAgendamentoActivity.this, "Agendamento não encontrado.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Agendamento não encontrado no Firestore.");
                    }
                });
    }

    private void carregarBarbeiro(String barbeiroId) {
        if (barbeiroId == null || barbeiroId.isEmpty()) {
            Log.d(TAG, "Barbeiro ID inválido: " + barbeiroId);
            Toast.makeText(AlterarAgendamentoActivity.this, "Erro: Barbeiro ID inválido.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("barbeiro").document(barbeiroId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Barbeiro barbeiro = documentSnapshot.toObject(Barbeiro.class);

                        if (barbeiro != null) {
                            Log.d(TAG, "Barbeiro encontrado: " + barbeiro.getNome());

                            tvBarbeiroNome.setText(barbeiro.getNome());

                            configurarDiasDisponiveis(barbeiro.getDiasDisponiveis());
                            configurarServicos(barbeiro.getServicos(), agendamentoAtual.getServico());
                            configurarHorariosDisponiveis();
                        }
                    } else {
                        Toast.makeText(AlterarAgendamentoActivity.this, "Barbeiro não encontrado.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Barbeiro não encontrado no Firestore.");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AlterarAgendamentoActivity.this, "Erro ao carregar barbeiro", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Erro ao carregar barbeiro: " + e.getMessage());
                });
    }

    private void configurarDiasDisponiveis(List<String> diasDisponiveis) {
        if (diasDisponiveis != null && !diasDisponiveis.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, diasDisponiveis);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDias.setAdapter(adapter);

            if (agendamentoAtual != null && agendamentoAtual.getDia() != null) {
                int position = diasDisponiveis.indexOf(agendamentoAtual.getDia());
                if (position >= 0) {
                    spinnerDias.setSelection(position);
                }
            }
        } else {
            List<String> diasPadrao = Arrays.asList("Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, diasPadrao);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDias.setAdapter(adapter);

            if (agendamentoAtual != null && agendamentoAtual.getDia() != null) {
                int position = diasPadrao.indexOf(agendamentoAtual.getDia());
                if (position >= 0) {
                    spinnerDias.setSelection(position);
                }
            }
        }
    }

    private void configurarServicos(List<String> servicosBarbeiro, String servicosAgendamento) {
        llServicos.removeAllViews();

        if (servicosBarbeiro == null || servicosBarbeiro.isEmpty()) {
            Log.d(TAG, "Barbeiro não possui serviços cadastrados");
            Toast.makeText(this, "Barbeiro não possui serviços cadastrados", Toast.LENGTH_SHORT).show();
            return;
        }

        Set<String> servicosSelecionados = new HashSet<>();
        if (servicosAgendamento != null && !servicosAgendamento.isEmpty()) {
            String[] servicos = servicosAgendamento.split(", ");
            servicosSelecionados.addAll(Arrays.asList(servicos));
        }

        for (String servico : servicosBarbeiro) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(servico);
            checkBox.setTextSize(16);

            checkBox.setChecked(servicosSelecionados.contains(servico));

            llServicos.addView(checkBox);
        }
    }

    private String horarioSelecionado;
    private void configurarHorariosDisponiveis() {
        String[] horarios = new String[]{"08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30",
                "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"};

        GridLayout gridLayoutHorarios = findViewById(R.id.gridLayoutHorarios);
        gridLayoutHorarios.removeAllViews();
        gridLayoutHorarios.setColumnCount(4);
        if (agendamentoAtual == null || agendamentoAtual.getBarbeiroId() == null) {
            Toast.makeText(this, "Erro: Barbeiro não encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("agendamentos")
                .whereEqualTo("barbeiroId", agendamentoAtual.getBarbeiroId())
                .whereEqualTo("dia", spinnerDias.getSelectedItem().toString())
                .whereIn("status", Arrays.asList("pendente", "confirmado"))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> horariosReservados = new ArrayList<>();

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Agendamento agendamento = document.toObject(Agendamento.class);
                        if (agendamento != null && agendamento.getHorario() != null
                                && !agendamento.getId().equals(agendamentoId)) {
                            horariosReservados.add(agendamento.getHorario());
                        }
                    }

                    for (String horario : horarios) {
                        Button btnHorario = new Button(this);
                        btnHorario.setText(horario);

                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.setMargins(16, 16, 16, 16);
                        params.width = 200;
                        params.height = 100;
                        btnHorario.setLayoutParams(params);

                        if (horariosReservados.contains(horario)) {
                            btnHorario.setEnabled(false);
                            btnHorario.setBackgroundColor(getResources().getColor(android.R.color.background_light));
                            btnHorario.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        } else {
                            btnHorario.setBackgroundResource(R.drawable.btntwo);
                            btnHorario.setTextColor(getResources().getColor(android.R.color.black));
                        }

                        btnHorario.setOnClickListener(v -> {
                            for (int i = 0; i < gridLayoutHorarios.getChildCount(); i++) {
                                Button btn = (Button) gridLayoutHorarios.getChildAt(i);

                                if (horariosReservados.contains(btn.getText().toString())) {
                                    btn.setBackgroundColor(getResources().getColor(android.R.color.background_light));
                                    btn.setTextColor(getResources().getColor(android.R.color.darker_gray));
                                } else {
                                    btn.setBackgroundResource(R.drawable.btntwo);
                                    btn.setTextColor(getResources().getColor(android.R.color.black));
                                }
                            }

                            horarioSelecionado = horario;

                            v.setBackgroundResource(R.drawable.btntwo);
                            ((Button)v).setTextColor(getResources().getColor(android.R.color.white));
                        });

                        if (horario.equals(agendamentoAtual.getHorario())) {
                            btnHorario.setBackgroundResource(R.drawable.btntwo);
                            btnHorario.setTextColor(getResources().getColor(android.R.color.white));

                            horarioSelecionado = horario;
                        }

                        gridLayoutHorarios.addView(btnHorario);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AlterarAgendamentoActivity.this, "Erro ao carregar horários", Toast.LENGTH_SHORT).show();
                });
    }

    private void salvarAlteracoes() {
        if (agendamentoAtual == null) {
            Toast.makeText(this, "Erro: Agendamento não carregado corretamente", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> servicosSelecionados = new ArrayList<>();
        for (int i = 0; i < llServicos.getChildCount(); i++) {
            View view = llServicos.getChildAt(i);
            if (view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()) {
                    servicosSelecionados.add(checkBox.getText().toString());
                }
            }
        }

        if (servicosSelecionados.isEmpty()) {
            Toast.makeText(this, "Por favor, selecione pelo menos um serviço", Toast.LENGTH_SHORT).show();
            return;
        }

        String diaSelecionado = spinnerDias.getSelectedItem().toString();

        if (horarioSelecionado == null || horarioSelecionado.isEmpty()) {
            Toast.makeText(this, "Por favor, selecione um horário", Toast.LENGTH_SHORT).show();
            return;
        }

        agendamentoAtual.setDia(diaSelecionado);
        agendamentoAtual.setHorario(horarioSelecionado);
        agendamentoAtual.setServico(String.join(", ", servicosSelecionados));

        db.collection("agendamentos").whereEqualTo("id", agendamentoId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (var documentSnapshot : queryDocumentSnapshots) {
                            db.collection("agendamentos").document(documentSnapshot.getId())
                                    .update(
                                            "dia", diaSelecionado,
                                            "horario", horarioSelecionado,
                                            "servico", String.join(", ", servicosSelecionados)
                                    )
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(AlterarAgendamentoActivity.this,
                                                "Agendamento atualizado com sucesso", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(AlterarAgendamentoActivity.this, DashboardClientActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AlterarAgendamentoActivity.this,
                                                "Erro ao atualizar agendamento", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Erro ao atualizar agendamento: " + e.getMessage());
                                    });
                            break;
                        }
                    } else {
                        Toast.makeText(AlterarAgendamentoActivity.this,
                                "Agendamento não encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AlterarAgendamentoActivity.this,
                            "Erro ao buscar agendamento", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Erro ao buscar agendamento: " + e.getMessage());
                });
    }
}