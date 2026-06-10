package com.example.manitasgo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manitasgo.R;
import com.example.manitasgo.modelo.Comercio;
import com.example.manitasgo.modelo.Producto;
import com.example.manitasgo.repositorio.AuthRepository;
import com.example.manitasgo.repositorio.ComercioRepository;
import com.example.manitasgo.repositorio.ProductoRepository;
import com.example.manitasgo.ui.adapter.ProductoVendedorAdapter;
import com.example.manitasgo.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class VendedorActivity extends AppCompatActivity {

    public static final String EXTRA_COMERCIO_ID = "comercio_id";

    private ComercioRepository comercioRepo;
    private ProductoRepository productoRepo;
    private AuthRepository authRepo;
    private SessionManager session;

    private TextView tvNombreComercio, tvLocalidad, tvSinComercio;
    private RecyclerView rvProductos;
    private Button btnCrearComercio, btnEditarComercio, btnAnadirProducto;
    private ProgressBar progress;

    private ProductoVendedorAdapter productoAdapter;
    private String miComercioId = null;

    // LiveData de instancia para evitar observers acumulados
    private final MutableLiveData<List<Comercio>> ldComercio = new MutableLiveData<>();
    private final MutableLiveData<List<Producto>> ldProductos = new MutableLiveData<>();
    private final MutableLiveData<String> ldError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> ldExito = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendedor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session      = new SessionManager(this);
        comercioRepo = new ComercioRepository();
        productoRepo = new ProductoRepository();
        authRepo     = new AuthRepository(this);

        tvNombreComercio  = findViewById(R.id.tv_vendedor_nombre_comercio);
        tvLocalidad       = findViewById(R.id.tv_vendedor_localidad);
        tvSinComercio     = findViewById(R.id.tv_sin_comercio);
        rvProductos       = findViewById(R.id.rv_vendedor_productos);
        btnCrearComercio  = findViewById(R.id.btn_crear_comercio);
        btnEditarComercio = findViewById(R.id.btn_editar_comercio);
        btnAnadirProducto = findViewById(R.id.btn_anadir_producto);
        progress          = findViewById(R.id.progress_vendedor);

        productoAdapter = new ProductoVendedorAdapter(new ArrayList<>(),
                this::editarProducto,
                this::eliminarProducto);
        rvProductos.setLayoutManager(new LinearLayoutManager(this));
        rvProductos.setAdapter(productoAdapter);

        btnCrearComercio.setOnClickListener(v ->
                startActivity(new Intent(this, EditarComercioActivity.class)));

        btnEditarComercio.setOnClickListener(v -> {
            if (miComercioId != null) {
                Intent i = new Intent(this, EditarComercioActivity.class);
                i.putExtra(EXTRA_COMERCIO_ID, miComercioId);
                startActivity(i);
            }
        });

        btnAnadirProducto.setOnClickListener(v -> {
            if (miComercioId != null) {
                Intent i = new Intent(this, EditarProductoActivity.class);
                i.putExtra(EXTRA_COMERCIO_ID, miComercioId);
                startActivity(i);
            }
        });

        cargarMiComercio();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarMiComercio();
    }

    private void cargarMiComercio() {
        progress.setVisibility(View.VISIBLE);
        ldComercio.removeObservers(this);
        ldError.removeObservers(this);

        ldComercio.observe(this, lista -> {
            progress.setVisibility(View.GONE);
            if (lista != null && !lista.isEmpty()) {
                Comercio c = lista.get(0);
                miComercioId = c.id;
                tvNombreComercio.setText(c.nombre);
                tvLocalidad.setText(c.localidad);
                tvSinComercio.setVisibility(View.GONE);
                btnCrearComercio.setVisibility(View.GONE);
                btnEditarComercio.setVisibility(View.VISIBLE);
                btnAnadirProducto.setVisibility(View.VISIBLE);
                rvProductos.setVisibility(View.VISIBLE);
                cargarProductos();
            } else {
                tvSinComercio.setVisibility(View.VISIBLE);
                btnCrearComercio.setVisibility(View.VISIBLE);
                btnEditarComercio.setVisibility(View.GONE);
                btnAnadirProducto.setVisibility(View.GONE);
                rvProductos.setVisibility(View.GONE);
            }
        });

        ldError.observe(this, msg -> {
            progress.setVisibility(View.GONE);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        });

        comercioRepo.getMiComercio(session.getUserId(), ldComercio, ldError);
    }

    private void cargarProductos() {
        ldProductos.removeObservers(this);
        ldProductos.observe(this, lista -> productoAdapter.setData(lista != null ? lista : new ArrayList<>()));
        productoRepo.getByComercio(miComercioId, ldProductos, ldError);
    }

    private void editarProducto(Producto p) {
        Intent i = new Intent(this, EditarProductoActivity.class);
        i.putExtra(EXTRA_COMERCIO_ID, miComercioId);
        i.putExtra(EditarProductoActivity.EXTRA_PRODUCTO_ID, p.id);
        startActivity(i);
    }

    private void eliminarProducto(Producto p) {
        ldExito.removeObservers(this);
        ldError.removeObservers(this);
        ldExito.observe(this, ok -> { if (ok) cargarProductos(); });
        ldError.observe(this, msg -> Toast.makeText(this, "Error: " + msg, Toast.LENGTH_SHORT).show());
        productoRepo.eliminar(p.id, ldExito, ldError);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vendedor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            authRepo.logout();
            Intent i = new Intent(this, AuthActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
