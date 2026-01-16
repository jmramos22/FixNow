package com.example.FixNow.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FixNow.R;
import com.example.FixNow.model.incidencia.Incidencia;

import java.util.ArrayList;
import java.util.List;

public class ListaIncidenciasAdapter extends RecyclerView.Adapter<ListaIncidenciasAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int idIncidencia);
    }

    private final OnItemClickListener listener;
    private List<Incidencia> listaIncidencias;
    private Context context;

    public ListaIncidenciasAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listaIncidencias = new ArrayList<>();
        this.listener = listener;
    }

    public void setLista(List<Incidencia> nuevaLista) {
        this.listaIncidencias = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_incidencia_abierta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Incidencia incidencia = listaIncidencias.get(position);

        String titulo = incidencia.getTitulo();
        String status = incidencia.getStatus();

        String cliente = "Cliente: " + (incidencia.getNombreCliente() != null ? incidencia.getNombreCliente() : "Desconocido");

        if (titulo == null) titulo = "Sin título";
        if (status == null) status = "Disponible";

        holder.tvTitulo.setText(titulo);
        holder.tvBadgeNueva.setText(status);
        holder.tvCliente.setText(cliente);

        holder.itemView.setOnClickListener(v -> {

            if (incidencia.getId() != 0 && listener != null) {
                listener.onItemClick(incidencia.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaIncidencias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvBadgeNueva, tvCliente;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvBadgeNueva = itemView.findViewById(R.id.tvBadgeNueva);
        }
    }
}
