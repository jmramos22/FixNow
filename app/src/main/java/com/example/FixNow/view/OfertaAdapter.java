package com.example.FixNow.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.FixNow.R;
import com.example.FixNow.RevisarOfertaActivity;
import com.example.FixNow.model.oferta.Oferta;
import com.example.FixNow.model.solucionador.Solucionador;

import java.util.ArrayList;
import java.util.List;

public class OfertaAdapter extends RecyclerView.Adapter<OfertaAdapter.OfertaViewHolder> {

    private final List<Oferta> listaOfertas = new ArrayList<>();

    public OfertaAdapter() {}

    public void setDatos(List<Oferta> nuevasOfertas) {
        listaOfertas.clear();
        if (nuevasOfertas != null) {
            listaOfertas.addAll(nuevasOfertas);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OfertaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_oferta, parent, false);
        return new OfertaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfertaViewHolder holder, int position) {
        Oferta oferta = listaOfertas.get(position);
        Context context = holder.itemView.getContext();

        String mensaje = oferta.getMensaje() != null ? oferta.getMensaje() : "Oferta sin mensaje";
        holder.tvMensaje.setText(mensaje);

        Solucionador sol = oferta.getSolucionador();
        String nombreCompleto = "Desconocido";

        int idSolucionador = oferta.getIdSolucionador();

        if (sol != null) {
            nombreCompleto = sol.getNombre() + " " + sol.getApPaterno();
        }

        holder.tvNombre.setText(nombreCompleto);

        double prom = oferta.getPromedio();
        if (prom > 0) {
            holder.tvCalificacion.setText(String.format("⭐ %.1f", prom));
        } else {
            holder.tvCalificacion.setText("⭐ Nuevo");
        }
        String finalNombreCompleto = nombreCompleto;
        int finalIdSolucionador = idSolucionador;

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RevisarOfertaActivity.class);

            intent.putExtra("ID_OFERTA", oferta.getId());
            intent.putExtra("ID_INCIDENCIA", oferta.getIdIncidencia());
            intent.putExtra("ID_SOLUCIONADOR", finalIdSolucionador);
            intent.putExtra("NOMBRE_SOLUCIONADOR", finalNombreCompleto);
            intent.putExtra("MENSAJE_OFERTA", mensaje);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaOfertas.size();
    }

    public static class OfertaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvMensaje, tvCalificacion;

        public OfertaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreOferta);
            tvMensaje = itemView.findViewById(R.id.tvMensajeOferta);
            tvCalificacion = itemView.findViewById(R.id.tvCalificacionOferta);
        }
    }
}