package com.example.manitasgo.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manitasgo.R;
import com.example.manitasgo.modelo.Resenya;

import java.util.List;

public class ResenyaAdapter extends RecyclerView.Adapter<ResenyaAdapter.ViewHolder> {

    private List<Resenya> data;

    public ResenyaAdapter(List<Resenya> data) {
        this.data = data;
    }

    public void setData(List<Resenya> nuevos) {
        this.data = nuevos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resenya, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Resenya r = data.get(position);
        holder.rbResenya.setRating((float) r.puntuacion);
        holder.tvComentario.setText(r.comentario != null ? r.comentario : "Sin comentario");
        holder.tvFecha.setText(r.fecha != null ? r.fecha.substring(0, 10) : "");
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RatingBar rbResenya;
        TextView tvComentario, tvFecha;

        ViewHolder(View itemView) {
            super(itemView);
            rbResenya    = itemView.findViewById(R.id.rb_resenya);
            tvComentario = itemView.findViewById(R.id.tv_resenya_comentario);
            tvFecha      = itemView.findViewById(R.id.tv_resenya_fecha);
        }
    }
}