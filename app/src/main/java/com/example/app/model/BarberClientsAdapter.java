package com.example.app.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app.R;
import com.example.app.model.Agendamento;
import java.util.List;

public class BarberClientsAdapter extends RecyclerView.Adapter<BarberClientsAdapter.ViewHolder> {

    public interface OnClientActionListener {
        void onConfirm(Agendamento agendamento);
        void onCancel(Agendamento agendamento);
    }

    private List<Agendamento> clients;
    private OnClientActionListener listener;

    public BarberClientsAdapter(List<Agendamento> clients, OnClientActionListener listener) {
        this.clients = clients;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_barber_client, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Agendamento agendamento = clients.get(position);
        
        holder.tvClientName.setText(agendamento.getClienteNome());
        holder.tvService.setText(agendamento.getServico());
        holder.tvDateTime.setText(String.format("%s às %s", agendamento.getDia(), agendamento.getHorario()));
        
        holder.btnConfirm.setOnClickListener(v -> {
            if (listener != null) listener.onConfirm(agendamento);
        });
        
        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) listener.onCancel(agendamento);
        });
    }

    @Override
    public int getItemCount() {
        return clients.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvClientName, tvService, tvDateTime;
        public Button btnConfirm, btnCancel;

        public ViewHolder(View itemView) {
            super(itemView);
            tvClientName = itemView.findViewById(R.id.tv_client_name);
            tvService = itemView.findViewById(R.id.tv_service);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            btnConfirm = itemView.findViewById(R.id.btn_confirm);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
        }
    }
}