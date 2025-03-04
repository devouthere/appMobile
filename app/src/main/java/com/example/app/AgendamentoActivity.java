package com.example.app;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.DocumentReference;
import java.util.ArrayList;
import java.util.List;

public class AgendamentoActivity extends AppCompatActivity {

    private LinearLayout llServicos;
    private Spinner spinnerDias;
    private TimePicker timePicker;
    private Button btnConfirmar;
    private String barbeiroId, nomeBarbeiro;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendamento);

        // Inicializando os componentes
        llServicos = findViewById(R.id.llServicos);
        spinnerDias = findViewById(R.id.spinnerDias);
        timePicker = findViewById(R.id.timePicker);
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

        // Botão para confirmar o agendamento
        btnConfirmar.setOnClickListener(v -> salvarAgendamento());
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

        // Pegar o dia e o horário
        String diaSelecionado = spinnerDias.getSelectedItem().toString();
        int hora = timePicker.getHour();
        int minuto = timePicker.getMinute();

        // Verificar se o horário está dentro do permitido (6h às 20h)
        if (hora < 6 || (hora == 20 && minuto > 0) || hora > 20) {
            Toast.makeText(this, "Horário fora do intervalo permitido (6h - 20h).", Toast.LENGTH_SHORT).show();
            return;
        }

        String horario = String.format("%02d:%02d", hora, minuto);

        // Obter ID do cliente logado
        String clienteId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Criar o objeto Agendamento com serviços selecionados
        Agendamento agendamento = new Agendamento(barbeiroId, nomeBarbeiro, clienteId, diaSelecionado, horario, String.join(", ", servicosSelecionados), "pendente");

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
