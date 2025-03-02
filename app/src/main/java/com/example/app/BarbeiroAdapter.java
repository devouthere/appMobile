package com.example.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class BarbeiroAdapter extends RecyclerView.Adapter<BarbeiroAdapter.BarbeiroViewHolder> {
    private List<Barbeiro> listaBarbeiros;
    private BarbeiroClickListener clickListener;

    public BarbeiroAdapter(List<Barbeiro> listaBarbeiros) {
        this.listaBarbeiros = listaBarbeiros != null ? listaBarbeiros : new ArrayList<>();
    }

    public interface BarbeiroClickListener {
        void onBarbeiroClick(Barbeiro barbeiro, int position);
    }

    public void setClickListener(BarbeiroClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void updateData(List<Barbeiro> novaLista) {
        this.listaBarbeiros = novaLista != null ? novaLista : new ArrayList<>();
        notifyDataSetChanged();
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

        // Verificar valores nulos
        holder.txtNomeBarbeiro.setText(barbeiro.getNome() != null ? barbeiro.getNome() : "");
        holder.txtEmailBarbeiro.setText("Email: " + (barbeiro.getEmail() != null ? barbeiro.getEmail() : ""));
        holder.txtEnderecoBarbeiro.setText("Endereço: " + (barbeiro.getEndereco() != null ? barbeiro.getEndereco() : ""));

        // Verificar se as listas não são nulas antes de usar join
        List<String> servicos = barbeiro.getServicos();
        if (servicos != null && !servicos.isEmpty()) {
            holder.txtServicos.setText("Serviços: " + String.join(", ", servicos));
        } else {
            holder.txtServicos.setText("Serviços: Nenhum");
        }

        List<String> diasDisponiveis = barbeiro.getDiasDisponiveis();
        if (diasDisponiveis != null && !diasDisponiveis.isEmpty()) {
            holder.txtDiasDisponiveis.setText("Dias Disponíveis: " + String.join(", ", diasDisponiveis));
        } else {
            holder.txtDiasDisponiveis.setText("Dias Disponíveis: Nenhum");
        }
    }

    @Override
    public int getItemCount() {
        return listaBarbeiros.size();
    }

    public class BarbeiroViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeBarbeiro, txtEmailBarbeiro, txtEnderecoBarbeiro, txtServicos, txtDiasDisponiveis;

        public BarbeiroViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomeBarbeiro = itemView.findViewById(R.id.txtNomeBarbeiro);
            txtEmailBarbeiro = itemView.findViewById(R.id.txtEmailBarbeiro);
            txtEnderecoBarbeiro = itemView.findViewById(R.id.txtEnderecoBarbeiro);
            txtServicos = itemView.findViewById(R.id.txtServicos);
            txtDiasDisponiveis = itemView.findViewById(R.id.txtDiasDisponiveis);

            // Configurar clique do item
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        clickListener.onBarbeiroClick(listaBarbeiros.get(position), position);
                    }
                }
            });
        }
    }
}