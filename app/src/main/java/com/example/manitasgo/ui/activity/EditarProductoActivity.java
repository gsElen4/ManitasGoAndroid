package com.example.manitasgo.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;

import com.example.manitasgo.R;
import com.example.manitasgo.modelo.Producto;
import com.example.manitasgo.repositorio.ProductoRepository;

public class EditarProductoActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCTO_ID = "producto_id";

    private ProductoRepository productoRepo;

    private EditText etNombre, etDescripcion, etPrecio, etStock, etCodigo;
    private CheckBox cbDisponible;
    private Button btnGuardar;
    private ProgressBar progress;

    private String comercioId;
    private String productoId; // null = creación
    private Producto productoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_producto);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        comercioId  = getIntent().getStringExtra(VendedorActivity.EXTRA_COMERCIO_ID);
        productoId  = getIntent().getStringExtra(EXTRA_PRODUCTO_ID);
        productoRepo = new ProductoRepository();

        etNombre      = findViewById(R.id.et_prod_nombre);
        etDescripcion = findViewById(R.id.et_prod_descripcion);
        etPrecio      = findViewById(R.id.et_prod_precio);
        etStock       = findViewById(R.id.et_prod_stock);
        etCodigo      = findViewById(R.id.et_prod_codigo);
        cbDisponible  = findViewById(R.id.cb_disponible);
        btnGuardar    = findViewById(R.id.btn_guardar_producto);
        progress      = findViewById(R.id.progress_editar_producto);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(productoId == null ? "Añadir producto" : "Editar producto");
        }

        if (productoId != null) {
            cargarProducto();
        }

        btnGuardar.setOnClickListener(v -> guardar());
    }

    private void cargarProducto() {
    }

    private void guardar() {
        String nombre = etNombre.getText().toString().trim();
        String desc   = etDescripcion.getText().toString().trim();
        String precio = etPrecio.getText().toString().trim();
        String stock  = etStock.getText().toString().trim();
        String codigo = etCodigo.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombre.setError("El nombre es obligatorio");
            return;
        }

        Producto p = new Producto();
        p.comercioId  = comercioId;
        p.nombre      = nombre;
        p.descripcion = desc.isEmpty() ? null : desc;
        p.codigo      = codigo.isEmpty() ? null : codigo;
        p.disponible  = cbDisponible.isChecked();

        if (!precio.isEmpty()) {
            try { p.precio = Double.parseDouble(precio); } catch (NumberFormatException ignored) {}
        }
        if (!stock.isEmpty()) {
            try { p.stock = Integer.parseInt(stock); } catch (NumberFormatException ignored) {}
        }

        progress.setVisibility(View.VISIBLE);
        btnGuardar.setEnabled(false);

        MutableLiveData<Producto> resultado = new MutableLiveData<>();
        MutableLiveData<String>   error     = new MutableLiveData<>();

        resultado.observe(this, prod -> {
            progress.setVisibility(View.GONE);
            Toast.makeText(this, "Producto guardado", Toast.LENGTH_SHORT).show();
            finish();
        });

        error.observe(this, msg -> {
            progress.setVisibility(View.GONE);
            btnGuardar.setEnabled(true);
            Toast.makeText(this, "Error: " + msg, Toast.LENGTH_LONG).show();
        });

        if (productoId == null) {
            productoRepo.crear(p, resultado, error);
        } else {
            productoRepo.actualizar(productoId, p, resultado, error);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
