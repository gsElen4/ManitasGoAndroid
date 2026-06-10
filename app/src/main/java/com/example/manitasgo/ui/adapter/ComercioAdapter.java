package com.example.manitasgo.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manitasgo.R;
import com.example.manitasgo.modelo.Comercio;

import java.util.List;

public class ComercioAdapter extends RecyclerView.Adapter<ComercioAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Comercio comercio);
    }

    private List<Comercio> data;
    private final OnItemClickListener listener;

    public ComercioAdapter(List<Comercio> data, OnItemClickListener listener) {
        this.data     = data;
        this.listener = listener;
    }

    public void setData(List<Comercio> nuevos) {
        this.data = nuevos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comercio, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comercio c = data.get(position);
        holder.tvNombre.setText(c.nombre);
        holder.tvLocalidad.setText(c.localidad);
        holder.tvDireccion.setText(c.direccion);
        holder.tvTelefono.setText(c.telefono != null ? c.telefono : "");
        holder.tvValoracion.setText(String.format("%.1f", c.valoracionMedia));
        holder.ratingBar.setRating((float) c.valoracionMedia);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(c));
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvLocalidad, tvDireccion, tvTelefono, tvValoracion;
        RatingBar ratingBar;

        ViewHolder(View itemView) {
            super(itemView);
            tvNombre     = itemView.findViewById(R.id.tv_item_nombre);
            tvLocalidad  = itemView.findViewById(R.id.tv_item_localidad);
            tvDireccion  = itemView.findViewById(R.id.tv_item_direccion);
            tvTelefono   = itemView.findViewById(R.id.tv_item_telefono);
            tvValoracion = itemView.findViewById(R.id.tv_item_valoracion);
            ratingBar    = itemView.findViewById(R.id.rb_item);
        }
    }
}
