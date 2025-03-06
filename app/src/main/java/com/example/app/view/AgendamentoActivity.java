package com.example.app.view;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.model.Agendamento;
import com.example.app.model.Barbeiro;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AgendamentoActivity extends AppCompatActivity {

    private LinearLayout llServicos, llHorarios; // Adicionando LinearLayout para os horários
    private Spinner spinnerDias;
    private Button btnConfirmar;
    private Button btnHorarioSelecionado;
    private String barbeiroId, nomeBarbeiro, horarioSelecionado;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendamento);

        // Inicializando os componentes
        llServicos = findViewById(R.id.llServicos);
        spinnerDias = findViewById(R.id.spinnerDias);
        btnConfirmar = findViewById(R.id.btnConfirmar);
        db = FirebaseFirestore.getInstance();

        // Receber o objeto barbeiro do Intent
        Barbeiro barbeiro = (Barbeiro) getIntent().getSerializableExtra("barbeiro");

        // Verificar se o barbeiro é nulo antes de continuar
        if (barbeiro == null) {
            Toast.makeText(this, "Erro ao carregar dados do barbeiro.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Extrair os dados do barbeiro
        barbeiroId = barbeiro.getId();
        nomeBarbeiro = barbeiro.getNome();
        String[] servicos = barbeiro.getServicos().toArray(new String[0]);
        String[] diasDisponiveis = barbeiro.getDiasDisponiveis().toArray(new String[0]);

        // Garantir que os arrays não sejam nulos
        if (servicos == null || servicos.length == 0) {
            servicos = new String[]{"Nenhum serviço disponível"};
        }
        if (diasDisponiveis == null || diasDisponiveis.length == 0) {
            diasDisponiveis = new String[]{"Nenhum dia disponível"};
        }

        // Configurar os Checkboxes dinamicamente para os serviços
        for (String servico : servicos) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(servico);
            llServicos.addView(checkBox);
        }

        // Configurar Spinner para dias
        spinnerDias.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, diasDisponiveis));

        // Criar os botões de horários fixos
        criarBotoesDeHorario();

        // Botão para confirmar o agendamento
        btnConfirmar.setOnClickListener(v -> salvarAgendamento());
    }

    private void criarBotoesDeHorario() {
        // Lista de horários fixos
        String[] horarios = new String[]{"08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30",
                "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"};

        GridLayout gridLayoutHorarios = findViewById(R.id.gridLayoutHorarios);
        gridLayoutHorarios.removeAllViews();
        gridLayoutHorarios.setColumnCount(4); // Define 4 colunas no GridLayout

        // Buscar agendamentos do barbeiro
        db.collection("agendamentos")
                .whereEqualTo("barbeiroId", barbeiroId)
                .whereIn("status", List.of("pendente", "confirmado"))  // Apenas status pendente ou confirmado
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Lista de horários já reservados
                    List<String> horariosReservados = new ArrayList<>();

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Agendamento agendamento = document.toObject(Agendamento.class);
                        if (agendamento != null && agendamento.getHorario() != null) {
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

                            // Evento de clique para selecionar o horário
                            btnHorario.setOnClickListener(v -> {
                                // Restaurar a cor do botão anteriormente selecionado (se existir)
                                if (btnHorarioSelecionado != null) {
                                    btnHorarioSelecionado.setBackgroundResource(R.drawable.botao_disponivel); // Volta ao padrão
                                    btnHorarioSelecionado.setTextColor(getResources().getColor(android.R.color.black));
                                }

                                // Atualizar o botão selecionado
                                btnHorarioSelecionado = btnHorario;
                                horarioSelecionado = horario;

                                // Mudar a cor do botão selecionado para branco
                                btnHorarioSelecionado.setBackgroundResource(R.drawable.botao_disponivel);
                                btnHorarioSelecionado.setTextColor(getResources().getColor(android.R.color.white));

                                // Mensagem de confirmação
                                Toast.makeText(AgendamentoActivity.this, "Horário selecionado: " + horarioSelecionado, Toast.LENGTH_SHORT).show();
                            });
                        }

                        // Adicionar botão ao GridLayout
                        gridLayoutHorarios.addView(btnHorario);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AgendamentoActivity.this, "Erro ao carregar horários", Toast.LENGTH_SHORT).show();
                });
    }




    private void salvarAgendamento() {
        // Pegar os serviços selecionados
        List<String> servicosSelecionados = new ArrayList<>();
        for (int i = 0; i < llServicos.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) llServicos.getChildAt(i);
            if (checkBox.isChecked()) {
                servicosSelecionados.add(checkBox.getText().toString());
            }
        }

        // Verificar se o horário foi selecionado
        if (horarioSelecionado == null || horarioSelecionado.isEmpty()) {
            Toast.makeText(this, "Por favor, selecione um horário.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Pegar o dia selecionado
        String diaSelecionado = spinnerDias.getSelectedItem().toString();

        // Obter ID do cliente logado
        String clienteId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Criar o objeto Agendamento com serviços selecionados
        Agendamento agendamento = new Agendamento(barbeiroId, nomeBarbeiro, clienteId, diaSelecionado, horarioSelecionado, String.join(", ", servicosSelecionados), "pendente");

        // Salvar o agendamento no Firebase
        db.collection("agendamentos")
                .add(agendamento)  // Firestore irá adicionar a data automaticamente
                .addOnSuccessListener(documentReference -> {
                    // Atribuir o ID gerado pelo Firestore ao objeto agendamento
                    agendamento.setId(documentReference.getId());

                    // Atualizar o agendamento no Firestore com o ID atribuído
                    documentReference.update("id", documentReference.getId());

                    // Mostrar mensagem de sucesso
                    Toast.makeText(this, "Agendamento confirmado!", Toast.LENGTH_SHORT).show();
                    finish(); // Fecha a tela após confirmar
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao agendar", Toast.LENGTH_SHORT).show());
    }
}
