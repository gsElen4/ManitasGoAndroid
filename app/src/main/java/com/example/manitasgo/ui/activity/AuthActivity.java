package com.example.manitasgo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import com.google.android.material.textfield.TextInputLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.example.manitasgo.R;
import com.example.manitasgo.repositorio.AuthRepository;

public class AuthActivity extends AppCompatActivity {

    private AuthRepository authRepo;
    private boolean modoLogin = true;

    // Vistas login
    private EditText etEmail, etPassword;
    private Button btnAcceder;
    private TextView tvCambiarModo;
    private ProgressBar progress;

    // LiveData de instancia
    private final MutableLiveData<String>  ldError       = new MutableLiveData<>();
    private final MutableLiveData<String>  ldTipoUsuario = new MutableLiveData<>();
    private final MutableLiveData<Boolean> ldExito       = new MutableLiveData<>();

    // Vistas registro (ocultas en modo login)
    private EditText etNombre, etTelefono;
    private TextInputLayout tilNombre, tilTelefono;
    private RadioGroup rgTipo;
    private TextView tvTituloForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        authRepo = new AuthRepository(this);

        etEmail      = findViewById(R.id.et_email);
        etPassword   = findViewById(R.id.et_password);
        tilNombre    = findViewById(R.id.til_nombre);
        tilTelefono  = findViewById(R.id.til_telefono);
        etNombre     = findViewById(R.id.et_nombre);
        etTelefono   = findViewById(R.id.et_telefono);
        rgTipo       = findViewById(R.id.rg_tipo);
        btnAcceder   = findViewById(R.id.btn_acceder);
        tvCambiarModo= findViewById(R.id.tv_cambiar_modo);
        tvTituloForm = findViewById(R.id.tv_titulo_form);
        progress     = findViewById(R.id.progress_auth);

        actualizarUI();

        btnAcceder.setOnClickListener(v -> {
            if (modoLogin) hacerLogin();
            else hacerRegistro();
        });

        tvCambiarModo.setOnClickListener(v -> {
            modoLogin = !modoLogin;
            actualizarUI();
        });
    }

    private void actualizarUI() {
        if (modoLogin) {
            tvTituloForm.setText(R.string.login_titulo);
            btnAcceder.setText(R.string.login_btn);
            tvCambiarModo.setText(R.string.login_cambiar_registro);
            tilNombre.setVisibility(View.GONE);
            tilTelefono.setVisibility(View.GONE);
            rgTipo.setVisibility(View.GONE);
        } else {
            tvTituloForm.setText(R.string.registro_titulo);
            btnAcceder.setText(R.string.registro_btn);
            tvCambiarModo.setText(R.string.registro_cambiar_login);
            tilNombre.setVisibility(View.VISIBLE);
            tilTelefono.setVisibility(View.VISIBLE);
            rgTipo.setVisibility(View.VISIBLE);
        }
    }

    private void hacerLogin() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Rellena email y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        ldError.removeObservers(this);
        ldTipoUsuario.removeObservers(this);

        ldError.observe(this, msg -> {
            setLoading(false);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        });

        ldTipoUsuario.observe(this, tipo -> {
            setLoading(false);
            Intent intent = "vendedor".equals(tipo)
                    ? new Intent(this, VendedorActivity.class)
                    : new Intent(this, ClienteActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        authRepo.login(email, password, ldError, ldTipoUsuario);
    }

    private void hacerRegistro() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String nombre   = etNombre.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();

        int selectedId = rgTipo.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Selecciona el tipo de cuenta", Toast.LENGTH_SHORT).show();
            return;
        }
        String tipo = (selectedId == R.id.rb_cliente) ? "cliente" : "vendedor";

        if (email.isEmpty() || password.isEmpty() || nombre.isEmpty()) {
            Toast.makeText(this, "Nombre, email y contraseña son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        ldError.removeObservers(this);
        ldExito.removeObservers(this);

        ldError.observe(this, msg -> {
            setLoading(false);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        });

        ldExito.observe(this, ok -> {
            setLoading(false);
            if (ok) {
                Intent intent = "vendedor".equals(tipo)
                        ? new Intent(this, VendedorActivity.class)
                        : new Intent(this, ClienteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        authRepo.registrar(email, password, nombre, telefono, tipo, ldError, ldExito);
    }

    private void setLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnAcceder.setEnabled(!loading);
    }
}
