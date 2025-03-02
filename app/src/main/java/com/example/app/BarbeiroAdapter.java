package com.example.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BarbeiroAdapter extends RecyclerView.Adapter<BarbeiroAdapter.BarbeiroViewHolder> {
    private List<Barbeiro> listaBarbeiros;

    public BarbeiroAdapter(List<Barbeiro> listaBarbeiros) {
        this.listaBarbeiros = listaBarbeiros;
    }

    @NonNull
    @Override
    public BarbeiroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_barbeiro, parent, false);
        return new BarbeiroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarbeiroViewHolder holder, int position) {
        Barbeiro barbeiro = listaBarbeiros.get(position);
        holder.txtNomeBarbeiro.setText(barbeiro.getNome());
        holder.txtEmailBarbeiro.setText("Email: " + barbeiro.getEmail());
        holder.txtEnderecoBarbeiro.setText("Endereço: " + barbeiro.getEndereco());

        holder.txtServicos.setText("Serviços: " + String.join(", ", barbeiro.getServicos()));
        holder.txtDiasDisponiveis.setText("Dias Disponíveis: " + String.join(", ", barbeiro.getDiasDisponiveis()));
    }

    @Override
    public int getItemCount() {
        return listaBarbeiros.size();
    }

    public static class BarbeiroViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeBarbeiro, txtEmailBarbeiro, txtEnderecoBarbeiro, txtServicos, txtDiasDisponiveis;

        public BarbeiroViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomeBarbeiro = itemView.findViewById(R.id.txtNomeBarbeiro);
            txtEmailBarbeiro = itemView.findViewById(R.id.txtEmailBarbeiro);
            txtEnderecoBarbeiro = itemView.findViewById(R.id.txtEnderecoBarbeiro);
            txtServicos = itemView.findViewById(R.id.txtServicos);
            txtDiasDisponiveis = itemView.findViewById(R.id.txtDiasDisponiveis);
        }
    }
}
