package com.example.app;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AgendamentoActivity extends AppCompatActivity {

    private Spinner spinnerServicos, spinnerDias;
    private TimePicker timePicker;
    private Button btnConfirmar;
    private String barbeiroId, nomeBarbeiro;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendamento);

        // Inicializando os componentes
        spinnerServicos = findViewById(R.id.spinnerServicos);
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

        // Configurar Spinners com os dados recebidos
        spinnerServicos.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, servicos));
        spinnerDias.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, diasDisponiveis));

        // Botão para confirmar o agendamento
        btnConfirmar.setOnClickListener(v -> salvarAgendamento());
    }

    private void salvarAgendamento() {
        // Pegar os valores selecionados
        String servicoSelecionado = spinnerServicos.getSelectedItem().toString();
        String diaSelecionado = spinnerDias.getSelectedItem().toString();
        int hora = timePicker.getHour();
        int minuto = timePicker.getMinute();
        String horario = String.format("%02d:%02d", hora, minuto);

        // Obter ID do cliente logado
        String clienteId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Criar o objeto Agendamento
        Agendamento agendamento = new Agendamento(barbeiroId, nomeBarbeiro, clienteId, diaSelecionado, horario, servicoSelecionado, "pendente");

        // Salvar no Firebase
        db.collection("agendamentos")
                .add(agendamento)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Agendamento confirmado!", Toast.LENGTH_SHORT).show();
                    finish(); // Fecha a tela após confirmar
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao agendar", Toast.LENGTH_SHORT).show());
    }
}
