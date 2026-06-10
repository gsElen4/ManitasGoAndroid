package com.example.manitasgo.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manitasgo.R;
import com.example.manitasgo.modelo.Producto;

import java.util.List;

public class ProductoVendedorAdapter extends RecyclerView.Adapter<ProductoVendedorAdapter.ViewHolder> {

    public interface OnProductoAction {
        void onAction(Producto producto);
    }

    private List<Producto> data;
    private final OnProductoAction onEditar;
    private final OnProductoAction onEliminar;

    public ProductoVendedorAdapter(List<Producto> data,
                                    OnProductoAction onEditar,
                                    OnProductoAction onEliminar) {
        this.data       = data;
        this.onEditar   = onEditar;
        this.onEliminar = onEliminar;
    }

    public void setData(List<Producto> nuevos) {
        this.data = nuevos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto_vendedor, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto p = data.get(position);
        holder.tvNombre.setText(p.nombre);
        holder.tvPrecio.setText(p.precio != null ? String.format("%.2f €", p.precio) : "—");
        holder.tvStock.setText("Stock: " + p.stock);
        holder.tvDisponible.setText(p.disponible ? "Disponible" : "Sin stock");

        holder.btnEditar.setOnClickListener(v -> onEditar.onAction(p));

        holder.btnEliminar.setOnClickListener(v ->
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Eliminar producto")
                        .setMessage("¿Seguro que quieres eliminar \"" + p.nombre + "\"?")
                        .setPositiveButton("Eliminar", (d, w) -> onEliminar.onAction(p))
                        .setNegativeButton("Cancelar", null)
                        .show()
        );
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvPrecio, tvStock, tvDisponible;
        ImageButton btnEditar, btnEliminar;

        ViewHolder(View itemView) {
            super(itemView);
            tvNombre     = itemView.findViewById(R.id.tv_vprod_nombre);
            tvPrecio     = itemView.findViewById(R.id.tv_vprod_precio);
            tvStock      = itemView.findViewById(R.id.tv_vprod_stock);
            tvDisponible = itemView.findViewById(R.id.tv_vprod_disponible);
            btnEditar    = itemView.findViewById(R.id.ibtn_editar);
            btnEliminar  = itemView.findViewById(R.id.ibtn_eliminar);
        }
    }
}
