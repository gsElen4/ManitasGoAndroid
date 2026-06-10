package com.example.manitasgo.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;

import com.example.manitasgo.R;
import com.example.manitasgo.modelo.Comercio;
import com.example.manitasgo.repositorio.ComercioRepository;
import com.example.manitasgo.util.SessionManager;

import java.util.List;

public class EditarComercioActivity extends AppCompatActivity {

    private ComercioRepository comercioRepo;
    private SessionManager session;

    private EditText etNombre, etDescripcion, etDireccion, etLocalidad, etCodPostal, etTelefono;
    private Button btnGuardar;
    private ProgressBar progress;

    private String comercioId; // null = crear nuevo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_comercio);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        comercioId   = getIntent().getStringExtra(VendedorActivity.EXTRA_COMERCIO_ID);
        comercioRepo = new ComercioRepository();
        session      = new SessionManager(this);

        etNombre      = findViewById(R.id.et_com_nombre);
        etDescripcion = findViewById(R.id.et_com_descripcion);
        etDireccion   = findViewById(R.id.et_com_direccion);
        etLocalidad   = findViewById(R.id.et_com_localidad);
        etCodPostal   = findViewById(R.id.et_com_cod_postal);
        etTelefono    = findViewById(R.id.et_com_telefono);
        btnGuardar    = findViewById(R.id.btn_guardar_comercio);
        progress      = findViewById(R.id.progress_editar_comercio);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(comercioId == null ? "Crear comercio" : "Editar perfil");
        }

        btnGuardar.setOnClickListener(v -> guardar());
    }

    private void guardar() {
        String nombre    = etNombre.getText().toString().trim();
        String desc      = etDescripcion.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String localidad = etLocalidad.getText().toString().trim();
        String cp        = etCodPostal.getText().toString().trim();
        String telefono  = etTelefono.getText().toString().trim();

        if (nombre.isEmpty() || direccion.isEmpty() || localidad.isEmpty()) {
            Toast.makeText(this, "Nombre, dirección y localidad son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        Comercio c = new Comercio();
        c.vendedorId  = session.getUserId();
        c.nombre      = nombre;
        c.descripcion = desc.isEmpty() ? null : desc;
        c.direccion   = direccion;
        c.localidad   = localidad;
        c.codigoPostal= cp.isEmpty() ? null : cp;
        c.telefono    = telefono.isEmpty() ? null : telefono;
        c.activo      = true;

        progress.setVisibility(View.VISIBLE);
        btnGuardar.setEnabled(false);

        MutableLiveData<List<Comercio>> resultado = new MutableLiveData<>();
        MutableLiveData<String>         error     = new MutableLiveData<>();

        resultado.observe(this, lista -> {
            progress.setVisibility(View.GONE);
            Toast.makeText(this, "Comercio guardado", Toast.LENGTH_SHORT).show();
            finish();
        });

        error.observe(this, msg -> {
            progress.setVisibility(View.GONE);
            btnGuardar.setEnabled(true);
            Toast.makeText(this, "Error: " + msg, Toast.LENGTH_LONG).show();
        });

        if (comercioId == null) {
            comercioRepo.crear(c, resultado, error);
        } else {
            comercioRepo.actualizar(comercioId, c, resultado, error);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
