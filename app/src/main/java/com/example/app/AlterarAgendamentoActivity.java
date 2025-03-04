package com.example.app;

import static java.sql.DriverManager.println;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

        // Inicializando os componentes
        llServicos = findViewById(R.id.llServicos);
        spinnerDias = findViewById(R.id.spinnerDias);
        timePicker = findViewById(R.id.timePicker);
        tvBarbeiroNome = findViewById(R.id.tvBarbeiroNome);
        btnConfirmar = findViewById(R.id.btnConfirmar);
        db = FirebaseFirestore.getInstance();

        // Definir título
        setTitle("Alterar Agendamento");

        // Logo após this.timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        // Receber os dados passados pelo Intent
        if (getIntent().hasExtra("id")) {
            agendamentoId = getIntent().getStringExtra("id");
            Log.d(TAG, "Agendamento ID recebido: " + agendamentoId);
            carregarAgendamento(agendamentoId);
        } else {
            Toast.makeText(this, "Erro: ID do agendamento não fornecido.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Configurar o botão de confirmar
        btnConfirmar.setOnClickListener(v -> salvarAlteracoes());
    }

    private void carregarAgendamento(String agendamentoId) {
        // Buscar o Agendamento na coleção "agendamentos" com o campo 'id'
        db.collection("agendamentos").whereEqualTo("id", agendamentoId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Como podemos ter vários documentos com o mesmo 'id', vamos pegar o primeiro
                        for (var documentSnapshot : queryDocumentSnapshots) {
                            agendamentoAtual = documentSnapshot.toObject(Agendamento.class);

                            if (agendamentoAtual != null) {
                                Log.d(TAG, "Agendamento encontrado: " + agendamentoAtual.toString());

                                // Verificando os campos do Agendamento
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
                                break; // Encontramos o agendamento, não precisamos continuar o loop
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
        // Verifique se o barbeiroId não é nulo ou vazio
        if (barbeiroId == null || barbeiroId.isEmpty()) {
            Log.d(TAG, "Barbeiro ID inválido: " + barbeiroId);
            Toast.makeText(AlterarAgendamentoActivity.this, "Erro: Barbeiro ID inválido.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buscar os dados do Barbeiro na coleção "barbeiros"
        db.collection("barbeiro").document(barbeiroId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Barbeiro barbeiro = documentSnapshot.toObject(Barbeiro.class);

                        if (barbeiro != null) {
                            Log.d(TAG, "Barbeiro encontrado: " + barbeiro.getNome());

                            // Preencher os dados do Barbeiro na UI
                            tvBarbeiroNome.setText(barbeiro.getNome());

                            // Agora, configurar os dias disponíveis e serviços
                            configurarDiasDisponiveis(barbeiro.getDiasDisponiveis());
                            configurarServicos(barbeiro.getServicos(), agendamentoAtual.getServico());
                            configurarHorario(agendamentoAtual.getHorario());
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
            // Configurar o Spinner com os dias disponíveis
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, diasDisponiveis);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDias.setAdapter(adapter);

            // Selecionar o dia atual do agendamento, se estiver na lista
            if (agendamentoAtual != null && agendamentoAtual.getDia() != null) {
                int position = diasDisponiveis.indexOf(agendamentoAtual.getDia());
                if (position >= 0) {
                    spinnerDias.setSelection(position);
                }
            }
        } else {
            // Caso não tenha dias disponíveis, usar opções padrão
            List<String> diasPadrao = Arrays.asList("Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, diasPadrao);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDias.setAdapter(adapter);

            // Selecionar o dia atual do agendamento
            if (agendamentoAtual != null && agendamentoAtual.getDia() != null) {
                int position = diasPadrao.indexOf(agendamentoAtual.getDia());
                if (position >= 0) {
                    spinnerDias.setSelection(position);
                }
            }
        }
    }

    private void configurarServicos(List<String> servicosBarbeiro, String servicosAgendamento) {
        // Limpar o layout antes de adicionar os serviços
        llServicos.removeAllViews();

        // Se não temos serviços do barbeiro, não podemos continuar
        if (servicosBarbeiro == null || servicosBarbeiro.isEmpty()) {
            Log.d(TAG, "Barbeiro não possui serviços cadastrados");
            Toast.makeText(this, "Barbeiro não possui serviços cadastrados", Toast.LENGTH_SHORT).show();
            return;
        }

        // Converter os serviços do agendamento em uma lista para facilitar a verificação
        Set<String> servicosSelecionados = new HashSet<>();
        if (servicosAgendamento != null && !servicosAgendamento.isEmpty()) {
            String[] servicos = servicosAgendamento.split(", ");
            servicosSelecionados.addAll(Arrays.asList(servicos));
        }

        // Adicionar cada serviço como um checkbox
        for (String servico : servicosBarbeiro) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(servico);
            checkBox.setTextSize(16);

            // Marcar o checkbox se o serviço estiver no agendamento atual
            checkBox.setChecked(servicosSelecionados.contains(servico));

            llServicos.addView(checkBox);
        }
    }

    private void configurarHorario(String horario) {
        if (horario != null && !horario.isEmpty()) {
            String[] partes = horario.split(":");
            if (partes.length == 2) {
                try {
                    int hora = Integer.parseInt(partes[0]);
                    int minuto = Integer.parseInt(partes[1]);
                    timePicker.setHour(hora);
                    timePicker.setMinute(minuto);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Erro ao converter horário: " + e.getMessage());
                }
            }
        }
    }

    private void salvarAlteracoes() {
        if (agendamentoAtual == null) {
            Toast.makeText(this, "Erro: Agendamento não carregado corretamente", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obter serviços selecionados
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

        // Obter dia selecionado
        String diaSelecionado = spinnerDias.getSelectedItem().toString();

        // Obter horário selecionado
        String horarioSelecionado = String.format("%02d:%02d", timePicker.getHour(), timePicker.getMinute());

        // Atualizar objeto Agendamento
        agendamentoAtual.setDia(diaSelecionado);
        agendamentoAtual.setHorario(horarioSelecionado);
        agendamentoAtual.setServico(String.join(", ", servicosSelecionados));

        // Salvar alterações no Firestore
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
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AlterarAgendamentoActivity.this,
                                                "Erro ao atualizar agendamento", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Erro ao atualizar agendamento: " + e.getMessage());
                                    });
                            break; // Atualizamos o primeiro documento, não precisamos continuar
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