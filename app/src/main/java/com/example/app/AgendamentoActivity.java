package com.example.app;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

public class AgendamentoActivity extends AppCompatActivity {

    private LinearLayout llServicos, llHorarios; // Adicionando LinearLayout para os horários
    private Spinner spinnerDias;
    private Button btnConfirmar;
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
        // Criar botões de horários fixos, como 8:00, 8:30, 9:00, etc.
        String[] horarios = new String[]{"08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30",
                "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"};

        GridLayout gridLayoutHorarios = findViewById(R.id.gridLayoutHorarios);

        gridLayoutHorarios.removeAllViews();

        gridLayoutHorarios.setColumnCount(4);

        for (String horario : horarios) {
            Button btnHorario = new Button(this);
            btnHorario.setText(horario);
            btnHorario.setLayoutParams(new GridLayout.LayoutParams());

            // Definir o clique para salvar o horário selecionado
            btnHorario.setOnClickListener(v -> {
                horarioSelecionado = horario;
                Toast.makeText(AgendamentoActivity.this, "Horário selecionado: " + horarioSelecionado, Toast.LENGTH_SHORT).show();
            });

            // Adicionar o botão ao GridLayout
            gridLayoutHorarios.addView(btnHorario);
        }
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
