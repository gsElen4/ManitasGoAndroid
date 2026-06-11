package com.example.manitasgo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.manitasgo.R;
import com.example.manitasgo.modelo.Comercio;
import com.example.manitasgo.modelo.Producto;
import com.example.manitasgo.repositorio.AuthRepository;
import com.example.manitasgo.repositorio.ComercioRepository;
import com.example.manitasgo.repositorio.ProductoRepository;
import com.example.manitasgo.ui.adapter.ComercioAdapter;
import com.example.manitasgo.ui.adapter.ProductoAdapter;
import com.example.manitasgo.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ClienteActivity extends AppCompatActivity {

    private ComercioRepository comercioRepo;
    private ProductoRepository productoRepo;
    private AuthRepository authRepo;
    private SessionManager session;

    private EditText etBuscar;
    private Spinner spinnerLocalidad;
    private RecyclerView rvComercios, rvProductos;
    private ProgressBar progress;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvSinResultados;

    private ComercioAdapter comercioAdapter;
    private ProductoAdapter productoAdapter;

    private final MutableLiveData<List<Comercio>> ldComercios = new MutableLiveData<>();
    private final MutableLiveData<List<Producto>> ldProductos = new MutableLiveData<>();
    private final MutableLiveData<String> ldError = new MutableLiveData<>();

    private final String[] LOCALIDADES = {"Todas", "Boiro", "Rianxo", "Ribeira", "A Pobra do Caramiñal", "Castiñeiras"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session      = new SessionManager(this);
        comercioRepo = new ComercioRepository();
        productoRepo = new ProductoRepository();
        authRepo     = new AuthRepository(this);

        etBuscar         = findViewById(R.id.et_buscar);
        spinnerLocalidad = findViewById(R.id.spinner_localidad);
        rvComercios      = findViewById(R.id.rv_comercios);
        rvProductos      = findViewById(R.id.rv_productos);
        progress         = findViewById(R.id.progress_cliente);
        swipeRefresh     = findViewById(R.id.swipe_refresh);
        tvSinResultados  = findViewById(R.id.tv_sin_resultados);

        // Spinner de localidades
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, LOCALIDADES);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocalidad.setAdapter(spinnerAdapter);

        // Adapter comercios
        comercioAdapter = new ComercioAdapter(new ArrayList<>(), comercio -> {
            Intent i = new Intent(this, DetalleComercioActivity.class);
            i.putExtra(DetalleComercioActivity.EXTRA_COMERCIO_ID, comercio.id);
            i.putExtra(DetalleComercioActivity.EXTRA_COMERCIO_NOMBRE, comercio.nombre);
            startActivity(i);
        });
        rvComercios.setLayoutManager(new LinearLayoutManager(this));
        rvComercios.setAdapter(comercioAdapter);

        // Adapter productos (búsqueda) — al pulsar navega al comercio del producto
        productoAdapter = new ProductoAdapter(new ArrayList<>(),
                new ProductoAdapter.OnProductoClickListener() {
                    @Override
                    public void onProductoClick(Producto p) {
                        Intent i = new Intent(ClienteActivity.this, DetalleComercioActivity.class);
                        i.putExtra(DetalleComercioActivity.EXTRA_COMERCIO_ID, p.comercioId);
                        i.putExtra(DetalleComercioActivity.EXTRA_COMERCIO_NOMBRE, "Comercio");
                        startActivity(i);
                    }
                });
        rvProductos.setLayoutManager(new LinearLayoutManager(this));
        rvProductos.setAdapter(productoAdapter);

        // Escucha spinner
        spinnerLocalidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                cargarComercios();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Búsqueda por texto
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int cnt, int af) {}
            @Override public void onTextChanged(CharSequence s, int st, int bf, int cnt) {
                String query = s.toString().trim();
                if (query.length() >= 2) {
                    buscarProductos(query);
                } else {
                    rvProductos.setVisibility(View.GONE);
                    rvComercios.setVisibility(View.VISIBLE);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        swipeRefresh.setOnRefreshListener(() -> {
            cargarComercios();
            swipeRefresh.setRefreshing(false);
        });

        cargarComercios();
    }

    private void cargarComercios() {
        setLoading(true);
        ldComercios.removeObservers(this);
        ldError.removeObservers(this);

        ldComercios.observe(this, lista -> {
            setLoading(false);
            comercioAdapter.setData(lista);
            tvSinResultados.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
        });

        ldError.observe(this, msg -> {
            setLoading(false);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        });

        String localidad = LOCALIDADES[spinnerLocalidad.getSelectedItemPosition()];
        if ("Todas".equals(localidad)) {
            comercioRepo.getTodos(ldComercios, ldError);
        } else {
            comercioRepo.getByLocalidad(localidad, ldComercios, ldError);
        }
    }

    private void buscarProductos(String query) {
        rvProductos.setVisibility(View.VISIBLE);
        rvComercios.setVisibility(View.GONE);

        ldProductos.removeObservers(this);
        ldError.removeObservers(this);

        ldProductos.observe(this, lista -> {
            productoAdapter.setData(lista);
            tvSinResultados.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
        });
        ldError.observe(this, msg -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());

        productoRepo.buscar(query, ldProductos, ldError);
    }

    private void setLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cliente, menu);
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