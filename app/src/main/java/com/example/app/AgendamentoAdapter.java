package com.example.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AgendamentoAdapter extends RecyclerView.Adapter<AgendamentoAdapter.AgendamentoViewHolder> {

    private List<Agendamento> agendamentos;

    public AgendamentoAdapter(List<Agendamento> agendamentos) {
        this.agendamentos = agendamentos;
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

        holder.txtBarbeiroNome.setText(agendamento.getBarbeiroNome());
        holder.txtServico.setText(agendamento.getServico());
        holder.txtDiaHorario.setText(agendamento.getDia() + " - " + agendamento.getHorario());
        holder.txtStatus.setText(agendamento.getStatus());

        // Alterar a cor do status (exemplo de como personalizar o status)
        if ("pendente".equals(agendamento.getStatus())) {
            holder.txtStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
        } else if ("confirmado".equals(agendamento.getStatus())) {
            holder.txtStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.txtStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray));
        }
    }

    @Override
    public int getItemCount() {
        return agendamentos.size();
    }

    public class AgendamentoViewHolder extends RecyclerView.ViewHolder {
        TextView txtBarbeiroNome, txtServico, txtDiaHorario, txtStatus;

        public AgendamentoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtBarbeiroNome = itemView.findViewById(R.id.txtBarbeiroNome);
            txtServico = itemView.findViewById(R.id.txtServico);
            txtDiaHorario = itemView.findViewById(R.id.txtDiaHorario);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }
    }
}
