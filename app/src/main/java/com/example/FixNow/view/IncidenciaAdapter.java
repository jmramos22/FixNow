package com.example.FixNow.view;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView; // IMPORTANTE: Importar CardView
import androidx.recyclerview.widget.RecyclerView;

import com.example.FixNow.R;
import com.example.FixNow.model.incidencia.Incidencia;

import java.util.ArrayList;
import java.util.List;

public class IncidenciaAdapter extends RecyclerView.Adapter<IncidenciaAdapter.IncidenciaViewHolder> {

    private final List<Incidencia> listaIncidencias = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Incidencia incidencia);
    }

    public IncidenciaAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setDatos(List<Incidencia> nuevasIncidencias) {
        listaIncidencias.clear();
        if (nuevasIncidencias != null) {
            listaIncidencias.addAll(nuevasIncidencias);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IncidenciaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_incidencia, parent, false);
        return new IncidenciaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncidenciaViewHolder holder, int position) {
        Incidencia incidencia = listaIncidencias.get(position);

        String titulo = (incidencia.getTitulo() != null) ? incidencia.getTitulo() : "Sin título";
        holder.tvTitulo.setText(titulo);

        String status = (incidencia.getStatus() != null) ? incidencia.getStatus() : "Desconocido";
        holder.tvEstado.setText(status);

        // --- CORRECCIÓN AQUÍ ---
        // Ya no usamos GradientDrawable. Usamos setCardBackgroundColor directamente.

        if (status.equalsIgnoreCase("Abierta") || status.contains("Ofertas")) {
            // Azulito
            holder.cardStatus.setCardBackgroundColor(Color.parseColor("#E3F2FD"));
            holder.tvEstado.setTextColor(Color.parseColor("#1565C0"));
        } else if (status.equalsIgnoreCase("En proceso")) {
            // Naranja/Amarillo
            holder.cardStatus.setCardBackgroundColor(Color.parseColor("#FFF3E0"));
            holder.tvEstado.setTextColor(Color.parseColor("#EF6C00"));
        } else if (status.equalsIgnoreCase("Resuelta") || status.equalsIgnoreCase("Finalizada")) {
            // Verde
            holder.cardStatus.setCardBackgroundColor(Color.parseColor("#E8F5E9"));
            holder.tvEstado.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            // Gris por defecto
            holder.cardStatus.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
            holder.tvEstado.setTextColor(Color.parseColor("#616161"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(incidencia);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaIncidencias.size();
    }

    public static class IncidenciaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvEstado;
        CardView cardStatus; // Agregamos la referencia al CardView

        public IncidenciaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloIncidencia);
            tvEstado = itemView.findViewById(R.id.tvEstadoBadge);

            // Buscamos el CardView por su ID en el XML que me pasaste
            cardStatus = itemView.findViewById(R.id.cardStatus);
        }
    }
}