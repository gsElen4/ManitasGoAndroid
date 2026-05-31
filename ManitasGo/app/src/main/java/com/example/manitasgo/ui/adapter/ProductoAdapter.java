package com.example.manitasgo.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manitasgo.R;
import com.example.manitasgo.model.Producto;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private List<Producto> data;

    public ProductoAdapter(List<Producto> data) {
        this.data = data;
    }

    public void setData(List<Producto> nuevos) {
        this.data = nuevos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto p = data.get(position);
        holder.tvNombre.setText(p.nombre);
        holder.tvDescripcion.setText(p.descripcion != null ? p.descripcion : "");
        holder.tvPrecio.setText(p.precio != null ? String.format("%.2f €", p.precio) : "Precio no disponible");
        holder.tvStock.setText("Stock: " + p.stock);
        holder.tvDisponible.setText(p.disponible ? "Disponible" : "Sin stock");
        holder.tvDisponible.setTextColor(holder.itemView.getContext().getResources()
                .getColor(p.disponible ? R.color.verde_disponible : R.color.rojo_sin_stock, null));
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvPrecio, tvStock, tvDisponible;

        ViewHolder(View itemView) {
            super(itemView);
            tvNombre      = itemView.findViewById(R.id.tv_prod_nombre);
            tvDescripcion = itemView.findViewById(R.id.tv_prod_descripcion);
            tvPrecio      = itemView.findViewById(R.id.tv_prod_precio);
            tvStock       = itemView.findViewById(R.id.tv_prod_stock);
            tvDisponible  = itemView.findViewById(R.id.tv_prod_disponible);
        }
    }
}
