package com.example.manitasgo.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manitasgo.R;
import com.example.manitasgo.model.Comercio;
import com.example.manitasgo.model.Producto;
import com.example.manitasgo.model.Resenya;
import com.example.manitasgo.repository.ComercioRepository;
import com.example.manitasgo.repository.ProductoRepository;
import com.example.manitasgo.repository.ResenyaRepository;
import com.example.manitasgo.ui.adapter.ProductoAdapter;
import com.example.manitasgo.ui.adapter.ResenyaAdapter;
import com.example.manitasgo.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class DetalleComercioActivity extends AppCompatActivity {

    public static final String EXTRA_COMERCIO_ID     = "comercio_id";
    public static final String EXTRA_COMERCIO_NOMBRE = "comercio_nombre";

    private ComercioRepository comercioRepo;
    private ProductoRepository productoRepo;
    private ResenyaRepository  resenyaRepo;
    private SessionManager session;

    private String comercioId;

    // Vistas
    private TextView tvNombre, tvDireccion, tvLocalidad, tvTelefono, tvValoracion, tvDescripcion;
    private RatingBar ratingBar;
    private RecyclerView rvProductos, rvResenyas;
    private Button btnLlamar, btnMapa, btnValorar;
    private ProductoAdapter productoAdapter;
    private ResenyaAdapter resenyaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_comercio);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        comercioId = getIntent().getStringExtra(EXTRA_COMERCIO_ID);
        String nombre = getIntent().getStringExtra(EXTRA_COMERCIO_NOMBRE);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(nombre);

        comercioRepo = new ComercioRepository();
        productoRepo = new ProductoRepository();
        resenyaRepo  = new ResenyaRepository();
        session      = new SessionManager(this);

        tvNombre      = findViewById(R.id.tv_detalle_nombre);
        tvDireccion   = findViewById(R.id.tv_detalle_direccion);
        tvLocalidad   = findViewById(R.id.tv_detalle_localidad);
        tvTelefono    = findViewById(R.id.tv_detalle_telefono);
        tvValoracion  = findViewById(R.id.tv_detalle_valoracion);
        tvDescripcion = findViewById(R.id.tv_detalle_descripcion);
        ratingBar     = findViewById(R.id.rating_bar_detalle);
        btnLlamar     = findViewById(R.id.btn_llamar);
        btnMapa       = findViewById(R.id.btn_mapa);
        btnValorar    = findViewById(R.id.btn_valorar);
        rvProductos   = findViewById(R.id.rv_detalle_productos);
        rvResenyas    = findViewById(R.id.rv_detalle_resenyas);

        productoAdapter = new ProductoAdapter(new ArrayList<>());
        rvProductos.setLayoutManager(new LinearLayoutManager(this));
        rvProductos.setAdapter(productoAdapter);

        resenyaAdapter = new ResenyaAdapter(new ArrayList<>());
        rvResenyas.setLayoutManager(new LinearLayoutManager(this));
        rvResenyas.setAdapter(resenyaAdapter);

        cargarComercio();
        cargarProductos();
        cargarResenyas();

        btnValorar.setOnClickListener(v -> mostrarDialogoValorar());
    }

    private void cargarComercio() {
        MutableLiveData<List<Comercio>> datos = new MutableLiveData<>();
        MutableLiveData<String> error = new MutableLiveData<>();

        datos.observe(this, lista -> {
            if (lista != null && !lista.isEmpty()) {
                Comercio c = lista.get(0);
                tvNombre.setText(c.nombre);
                tvDireccion.setText(c.direccion);
                tvLocalidad.setText(c.localidad);
                tvTelefono.setText(c.telefono != null ? c.telefono : "No disponible");
                tvDescripcion.setText(c.descripcion != null ? c.descripcion : "");
                tvValoracion.setText(String.format("%.1f / 5", c.valoracionMedia));
                ratingBar.setRating((float) c.valoracionMedia);

                if (c.telefono != null) {
                    btnLlamar.setOnClickListener(v -> {
                        Intent call = new Intent(Intent.ACTION_DIAL,
                                Uri.parse("tel:" + c.telefono.replaceAll(" ", "")));
                        startActivity(call);
                    });
                } else {
                    btnLlamar.setEnabled(false);
                }

                btnMapa.setOnClickListener(v -> {
                    Uri gmmUri = Uri.parse("geo:" + c.latitud + "," + c.longitud
                            + "?q=" + Uri.encode(c.nombre));
                    startActivity(new Intent(Intent.ACTION_VIEW, gmmUri));
                });
            }
        });

        error.observe(this, msg -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());

        // getMiComercio filtra por vendedor, usamos getAll y filtramos por id
        comercioRepo.getMiComercio(comercioId, datos, error);
        // Realmente necesitamos un endpoint por id; en PostgREST: /comercios?id=eq.UUID
        // El ComercioRepository.getMiComercio se puede reusar pasando el id directamente:
        // comercioRepo.getById(comercioId, datos, error);  — ver nota en repositorio
    }

    private void cargarProductos() {
        MutableLiveData<List<Producto>> datos = new MutableLiveData<>();
        MutableLiveData<String> error = new MutableLiveData<>();
        datos.observe(this, lista -> productoAdapter.setData(lista != null ? lista : new ArrayList<>()));
        error.observe(this, msg -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
        productoRepo.getByComercio(comercioId, datos, error);
    }

    private void cargarResenyas() {
        MutableLiveData<List<Resenya>> datos = new MutableLiveData<>();
        MutableLiveData<String> error = new MutableLiveData<>();
        datos.observe(this, lista -> resenyaAdapter.setData(lista != null ? lista : new ArrayList<>()));
        error.observe(this, msg -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
        resenyaRepo.getByComercio(comercioId, datos, error);
    }

    private void mostrarDialogoValorar() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_valorar, null);
        RatingBar rb = dialogView.findViewById(R.id.rb_dialog);
        EditText etComentario = dialogView.findViewById(R.id.et_comentario_dialog);

        new AlertDialog.Builder(this)
                .setTitle("Valorar comercio")
                .setView(dialogView)
                .setPositiveButton("Enviar", (dialog, which) -> {
                    int puntuacion = (int) rb.getRating();
                    String comentario = etComentario.getText().toString().trim();

                    if (puntuacion == 0) {
                        Toast.makeText(this, "Selecciona una puntuación", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Resenya r = new Resenya(comercioId, session.getUserId(), puntuacion, comentario);
                    MutableLiveData<Boolean> exito = new MutableLiveData<>();
                    MutableLiveData<String> error = new MutableLiveData<>();

                    exito.observe(this, ok -> {
                        if (ok) {
                            Toast.makeText(this, "¡Valoración enviada!", Toast.LENGTH_SHORT).show();
                            cargarResenyas();
                            cargarComercio();
                        }
                    });
                    error.observe(this, msg -> Toast.makeText(this, "Error: " + msg, Toast.LENGTH_LONG).show());

                    resenyaRepo.crear(r, exito, error);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
