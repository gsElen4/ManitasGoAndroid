package com.example.manitasgo.repositorio;

import androidx.lifecycle.MutableLiveData;

import com.example.manitasgo.api.ProductoService;
import com.example.manitasgo.api.SupabaseClient;
import com.example.manitasgo.model.Producto;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductoRepository {

    private final ProductoService service;
    private static final String SELECT_FULL = "*,categorias(nombre,icono)";

    public ProductoRepository() {
        service = SupabaseClient.getInstance().createService(ProductoService.class);
    }

    public void getByComercio(String comercioId,
                               MutableLiveData<List<Producto>> liveData,
                               MutableLiveData<String> error) {
        service.getByComercio("eq." + comercioId, "eq.true", SELECT_FULL, "nombre.asc")
                .enqueue(callback(liveData, error));
    }

    public void buscar(String query,
                       MutableLiveData<List<Producto>> liveData,
                       MutableLiveData<String> error) {
        String filtro = "ilike.*" + query.trim() + "*";
        service.buscarPorNombre(filtro, "eq.true", SELECT_FULL, "nombre.asc")
                .enqueue(callback(liveData, error));
    }

    public void getByCategoria(int categoriaId,
                                MutableLiveData<List<Producto>> liveData,
                                MutableLiveData<String> error) {
        service.getByCategoria("eq." + categoriaId, "eq.true", SELECT_FULL, "nombre.asc")
                .enqueue(callback(liveData, error));
    }

    public void crear(Producto producto,
                      MutableLiveData<Producto> liveData,
                      MutableLiveData<String> error) {
        service.crear(producto).enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    liveData.postValue(response.body().get(0));
                } else {
                    error.postValue("Error " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                error.postValue("Error de red: " + t.getMessage());
            }
        });
    }

    public void actualizar(String id, Producto cambios,
                            MutableLiveData<Producto> liveData,
                            MutableLiveData<String> error) {
        service.actualizar("eq." + id, cambios).enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    liveData.postValue(response.body().get(0));
                } else {
                    error.postValue("Error " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                error.postValue("Error de red: " + t.getMessage());
            }
        });
    }

    public void eliminar(String id, MutableLiveData<Boolean> exito, MutableLiveData<String> error) {
        service.eliminar("eq." + id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                exito.postValue(response.isSuccessful());
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.postValue(t.getMessage());
            }
        });
    }

    private <T> Callback<T> callback(MutableLiveData<T> liveData, MutableLiveData<String> error) {
        return new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) liveData.postValue(response.body());
                else error.postValue("Error " + response.code());
            }
            @Override
            public void onFailure(Call<T> call, Throwable t) {
                error.postValue("Error de red: " + t.getMessage());
            }
        };
    }
}
