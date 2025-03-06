package com.example.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
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

        // Inicializando os componentes
        llServicos = findViewById(R.id.llServicos);
        spinnerDias = findViewById(R.id.spinnerDias);
        tvBarbeiroNome = findViewById(R.id.tvBarbeiroNome);
        btnConfirmar = findViewById(R.id.btnConfirmar);
        db = FirebaseFirestore.getInstance();

        // Definir título
        setTitle("Alterar Agendamento");

        // Receber os dados passados pelo Intent
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

    private String horarioSelecionado; // Add this as a class-level variable

    private void configurarHorariosDisponiveis() {
        // Lista de horários fixos
        String[] horarios = new String[]{"08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30",
                "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"};

        GridLayout gridLayoutHorarios = findViewById(R.id.gridLayoutHorarios);
        gridLayoutHorarios.removeAllViews();
        gridLayoutHorarios.setColumnCount(4); // Define 4 colunas no GridLayout

        // Verificar se temos um barbeiro válido
        if (agendamentoAtual == null || agendamentoAtual.getBarbeiroId() == null) {
            Toast.makeText(this, "Erro: Barbeiro não encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buscar agendamentos do barbeiro no dia selecionado
        db.collection("agendamentos")
                .whereEqualTo("barbeiroId", agendamentoAtual.getBarbeiroId())
                .whereEqualTo("dia", spinnerDias.getSelectedItem().toString())
                .whereIn("status", Arrays.asList("pendente", "confirmado"))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Lista de horários já reservados
                    List<String> horariosReservados = new ArrayList<>();

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Agendamento agendamento = document.toObject(Agendamento.class);
                        if (agendamento != null && agendamento.getHorario() != null
                                && !agendamento.getId().equals(agendamentoId)) { // Exclude current scheduling
                            horariosReservados.add(agendamento.getHorario());
                        }
                    }

                    // Criar os botões de horário
                    for (String horario : horarios) {
                        Button btnHorario = new Button(this);
                        btnHorario.setText(horario);

                        // Definir margens para espaçamento entre os botões
                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.setMargins(16, 16, 16, 16); // Esquerda, topo, direita, baixo
                        params.width = 200; // Largura fixa do botão
                        params.height = 100; // Altura fixa do botão
                        btnHorario.setLayoutParams(params);

                        // Verificar se o horário está reservado
                        if (horariosReservados.contains(horario)) {
                            btnHorario.setEnabled(false);
                            btnHorario.setBackgroundColor(getResources().getColor(android.R.color.background_light));
                            btnHorario.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        } else {
                            btnHorario.setBackgroundResource(R.drawable.botao_disponivel); // Cor para horário disponível
                            btnHorario.setTextColor(getResources().getColor(android.R.color.black));
                        }

                        // Evento de clique para selecionar o horário
                        btnHorario.setOnClickListener(v -> {
                            // Restaurar o estilo de todos os botões
                            for (int i = 0; i < gridLayoutHorarios.getChildCount(); i++) {
                                Button btn = (Button) gridLayoutHorarios.getChildAt(i);

                                if (horariosReservados.contains(btn.getText().toString())) {
                                    btn.setBackgroundColor(getResources().getColor(android.R.color.background_light));
                                    btn.setTextColor(getResources().getColor(android.R.color.darker_gray));
                                } else {
                                    btn.setBackgroundResource(R.drawable.botao_disponivel);
                                    btn.setTextColor(getResources().getColor(android.R.color.black));
                                }
                            }

                            // Definir o horário selecionado
                            horarioSelecionado = horario;

                            // Destacar o botão selecionado
                            v.setBackgroundResource(R.drawable.botao_disponivel);
                            ((Button)v).setTextColor(getResources().getColor(android.R.color.white));
                        });

                        // Marcar o botão do horário atual do agendamento
                        if (horario.equals(agendamentoAtual.getHorario())) {
                            btnHorario.setBackgroundResource(R.drawable.botao_disponivel);
                            btnHorario.setTextColor(getResources().getColor(android.R.color.white));

                            // Definir o horário selecionado inicialmente
                            horarioSelecionado = horario;
                        }

                        // Adicionar botão ao GridLayout
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

        // Verificar se um horário foi selecionado
        if (horarioSelecionado == null || horarioSelecionado.isEmpty()) {
            Toast.makeText(this, "Por favor, selecione um horário", Toast.LENGTH_SHORT).show();
            return;
        }

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
                                        // Após salvar com sucesso, ir para DashboardClientActivity
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