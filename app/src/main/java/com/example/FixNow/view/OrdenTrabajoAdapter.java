package com.example.FixNow.view;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FixNow.R;
import com.example.FixNow.model.OrdenSolucionador;

import java.util.ArrayList;
import java.util.List;

public class OrdenTrabajoAdapter extends RecyclerView.Adapter<OrdenTrabajoAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(OrdenSolucionador orden);
    }

    private List<OrdenSolucionador> lista = new ArrayList<>();
    private final OnItemClickListener listener;

    public OrdenTrabajoAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setLista(List<OrdenSolucionador> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_incidencia_abierta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrdenSolucionador orden = lista.get(position);

        holder.tvTitulo.setText(orden.getTituloIncidencia());

        String estado = orden.getStatus();
        holder.tvEstado.setText(estado);


        if ("En proceso".equalsIgnoreCase(estado)) {
            // Naranja
            holder.tvEstado.setTextColor(Color.parseColor("#EF6C00"));
        } else if ("Resuelta".equalsIgnoreCase(estado) || "Finalizada".equalsIgnoreCase(estado)) {
            // Verde
            holder.tvEstado.setTextColor(Color.parseColor("#2E7D32"));
            if (orden.getCalificacion() > 0) {
                holder.tvEstado.setText("Finalizada (" + orden.getCalificacion() + "★)");
            }
        }

        // Click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(orden);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvEstado = itemView.findViewById(R.id.tvBadgeNueva);
        }
    }
}