package com.example.app.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.model.Agendamento;
import com.example.app.view.AlterarAgendamentoActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
public class AgendamentoAdapter extends RecyclerView.Adapter<AgendamentoAdapter.AgendamentoViewHolder> {

    private List<Agendamento> agendamentos;
    private FirebaseFirestore db;

    public AgendamentoAdapter(List<Agendamento> agendamentos) {
        this.agendamentos = agendamentos;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public AgendamentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_agendamento, parent, false);
        return new AgendamentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AgendamentoViewHolder holder, int position) {
        Agendamento agendamento = agendamentos.get(position);
        Context context = holder.itemView.getContext();

        holder.txtBarbeiroNome.setText(agendamento.getBarbeiroNome());
        holder.txtServico.setText(agendamento.getServico());
        holder.txtDiaHorario.setText(agendamento.getDia() + " - " + agendamento.getHorario());
        holder.txtStatus.setText(agendamento.getStatus());

        // Configurar a cor do status
        if ("pendente".equals(agendamento.getStatus().toLowerCase())) {
            holder.txtStatus.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
            holder.layoutBotoes.setVisibility(View.VISIBLE);
        } else if ("confirmado".equals(agendamento.getStatus().toLowerCase())) {
            holder.txtStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            holder.layoutBotoes.setVisibility(View.VISIBLE);
        } else if ("cancelado".equals(agendamento.getStatus().toLowerCase())) {
            holder.txtStatus.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            holder.layoutBotoes.setVisibility(View.GONE); // Esconder botões se cancelado
        } else {
            holder.txtStatus.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }

        // Configurar a data de criação
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        if (agendamento.getDataCriacao() != null) {
            holder.txtDataCriacao.setText("Criado em: " + sdf.format(agendamento.getDataCriacao()));
        } else {
            holder.txtDataCriacao.setText("Criado em: N/A");
        }

        // Configurar ação do botão Cancelar
        holder.btnCancelar.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Cancelar Agendamento")
                    .setMessage("Tem certeza que deseja cancelar este agendamento?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        // Verifica se o ID do agendamento não é nulo
                        if (agendamento.getId() != null) {
                            // Atualiza o status no Firestore
                            db.collection("agendamentos").document(agendamento.getId())
                                    .update("status", "cancelado")
                                    .addOnSuccessListener(aVoid -> {
                                        // Atualiza localmente
                                        agendamento.setStatus("cancelado");
                                        notifyItemChanged(position);
                                        Toast.makeText(context, "Agendamento cancelado com sucesso", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Erro ao cancelar: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(context, "Erro: ID do agendamento não encontrado",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Não", null)
                    .show();
        });

        // Configurar ação do botão Alterar para usar a nova activity
        holder.btnAlterar.setOnClickListener(v -> {
            if (agendamento.getId() != null) {
                // Mostrar uma mensagem de carregamento
                Toast.makeText(context, "Carregando dados para alterar o agendamento...", Toast.LENGTH_SHORT).show();

                // Passar os dados do agendamento para a Activity de alteração
                Intent intent = new Intent(context, AlterarAgendamentoActivity.class);

                // Passando os dados para a nova Activity
                intent.putExtra("id", agendamento.getId()); // Passar o ID do agendamento
                intent.putExtra("servico", agendamento.getServico()); // Passar o serviço
                intent.putExtra("dia", agendamento.getDia()); // Passar o dia
                intent.putExtra("barbeiroId", agendamento.getBarbeiroId()); // Passar o barbeiroId
                intent.putExtra("horario", agendamento.getHorario()); // Passar o horário

                // Iniciar a nova Activity
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Erro: ID do agendamento não encontrado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return agendamentos.size();
    }

    public static class AgendamentoViewHolder extends RecyclerView.ViewHolder {
        TextView txtBarbeiroNome, txtServico, txtDiaHorario, txtStatus, txtDataCriacao;
        Button btnCancelar, btnAlterar;
        LinearLayout layoutBotoes;

        public AgendamentoViewHolder(View itemView) {
            super(itemView);
            txtBarbeiroNome = itemView.findViewById(R.id.txtBarbeiroNome);
            txtServico = itemView.findViewById(R.id.txtServico);
            txtDiaHorario = itemView.findViewById(R.id.txtDiaHorario);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtDataCriacao = itemView.findViewById(R.id.txtDataCriacao);
            btnCancelar = itemView.findViewById(R.id.btnCancelar);
            btnAlterar = itemView.findViewById(R.id.btnAlterar);
            layoutBotoes = itemView.findViewById(R.id.layoutBotoes);
        }
    }
}
