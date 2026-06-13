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
import com.example.manitasgo.modelo.Comercio;
import com.example.manitasgo.modelo.Producto;
import com.example.manitasgo.modelo.Resenya;
import com.example.manitasgo.repositorio.ComercioRepository;
import com.example.manitasgo.repositorio.ProductoRepository;
import com.example.manitasgo.repositorio.ResenyaRepository;
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
    private Comercio comercioActual;

    // Vistas
    private TextView tvNombre, tvDireccion, tvLocalidad, tvTelefono, tvValoracion, tvDescripcion;
    private RatingBar ratingBar;
    private RecyclerView rvProductos, rvResenyas;
    private Button btnLlamar, btnMapa, btnValorar;
    private ProductoAdapter productoAdapter;
    private ResenyaAdapter resenyaAdapter;

    // LiveData de instancia
    private final MutableLiveData<List<Comercio>> ldComercio     = new MutableLiveData<>();
    private final MutableLiveData<List<Producto>> ldProductos    = new MutableLiveData<>();
    private final MutableLiveData<List<Resenya>>  ldResenyas     = new MutableLiveData<>();
    private final MutableLiveData<String>         ldError        = new MutableLiveData<>();
    private final MutableLiveData<Boolean>        ldResenyaExito = new MutableLiveData<>();

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
        ldComercio.removeObservers(this);
        ldError.removeObservers(this);

        ldComercio.observe(this, lista -> {
            if (lista == null || lista.isEmpty()) return;
            comercioActual = lista.get(0);
            mostrarComercio(comercioActual.valoracionMedia);
        });

        ldError.observe(this, msg -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
        comercioRepo.getById(comercioId, ldComercio, ldError);
    }

    private void mostrarComercio(double valoracion) {
        if (comercioActual == null) return;
        tvNombre.setText(comercioActual.nombre);
        tvDireccion.setText(comercioActual.direccion);
        tvLocalidad.setText(comercioActual.localidad);
        tvTelefono.setText(comercioActual.telefono != null ? comercioActual.telefono : "No disponible");
        tvDescripcion.setText(comercioActual.descripcion != null ? comercioActual.descripcion : "");
        tvValoracion.setText(String.format("%.1f / 5", valoracion));
        ratingBar.setRating((float) valoracion);

        if (comercioActual.telefono != null) {
            btnLlamar.setOnClickListener(v -> {
                Intent call = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + comercioActual.telefono.replaceAll(" ", "")));
                startActivity(call);
            });
        } else {
            btnLlamar.setEnabled(false);
        }

        btnMapa.setOnClickListener(v -> {
            Uri gmmUri = Uri.parse("geo:" + comercioActual.latitud + "," + comercioActual.longitud
                    + "?q=" + Uri.encode(comercioActual.nombre));
            startActivity(new Intent(Intent.ACTION_VIEW, gmmUri));
        });
    }

    private void actualizarValoracionDesdeResenyas(List<Resenya> resenyas) {
        if (resenyas == null || resenyas.isEmpty()) {
            mostrarComercio(0);
            return;
        }
        double suma = 0;
        for (Resenya r : resenyas) {
            suma += r.puntuacion;
        }
        double media = suma / resenyas.size();
        // Redondear a 1 decimal
        media = Math.round(media * 10.0) / 10.0;
        mostrarComercio(media);
    }

    private void cargarProductos() {
        ldProductos.removeObservers(this);
        ldProductos.observe(this, lista ->
                productoAdapter.setData(lista != null ? lista : new ArrayList<>()));
        productoRepo.getByComercio(comercioId, ldProductos, ldError);
    }

    private void cargarResenyas() {
        ldResenyas.removeObservers(this);
        ldResenyas.observe(this, lista -> {
            List<Resenya> resenyas = lista != null ? lista : new ArrayList<>();
            resenyaAdapter.setData(resenyas);
            // Calcular la media localmente y actualizar las estrellas
            actualizarValoracionDesdeResenyas(resenyas);
        });
        resenyaRepo.getByComercio(comercioId, ldResenyas, ldError);
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
                    ldResenyaExito.removeObservers(this);
                    ldError.removeObservers(this);

                    ldResenyaExito.observe(this, ok -> {
                        if (ok == null || !ok) return;
                        Toast.makeText(this, "¡Valoración enviada!", Toast.LENGTH_SHORT).show();
                        ldResenyas.setValue(null);
                        cargarResenyas();
                    });

                    ldError.observe(this, msg ->
                            Toast.makeText(this, "Error: " + msg, Toast.LENGTH_LONG).show());

                    resenyaRepo.crear(r, ldResenyaExito, ldError);
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