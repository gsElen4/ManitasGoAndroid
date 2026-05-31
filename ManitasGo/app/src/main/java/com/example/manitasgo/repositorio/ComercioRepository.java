package com.example.manitasgo.repositorio;

import androidx.lifecycle.MutableLiveData;

import com.example.manitasgo.api.ComercioService;
import com.example.manitasgo.api.SupabaseClient;
import com.example.manitasgo.model.Comercio;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComercioRepository {

    private final ComercioService service;

    public ComercioRepository() {
        service = SupabaseClient.getInstance().createService(ComercioService.class);
    }

    public void getTodos(MutableLiveData<List<Comercio>> liveData,
                         MutableLiveData<String> error) {
        service.getAll("eq.true", "*", "nombre.asc")
                .enqueue(callback(liveData, error));
    }

    public void getByLocalidad(String localidad,
                                MutableLiveData<List<Comercio>> liveData,
                                MutableLiveData<String> error) {
        service.getByLocalidad("eq." + localidad, "eq.true", "*", "nombre.asc")
                .enqueue(callback(liveData, error));
    }

    public void getMiComercio(String vendedorId,
                               MutableLiveData<List<Comercio>> liveData,
                               MutableLiveData<String> error) {
        service.getMiComercio("eq." + vendedorId)
                .enqueue(callback(liveData, error));
    }

    public void crear(Comercio comercio,
                      MutableLiveData<List<Comercio>> liveData,
                      MutableLiveData<String> error) {
        service.crear(comercio).enqueue(callback(liveData, error));
    }

    public void actualizar(String id, Comercio cambios,
                            MutableLiveData<List<Comercio>> liveData,
                            MutableLiveData<String> error) {
        service.actualizar("eq." + id, cambios).enqueue(callback(liveData, error));
    }

    private <T> Callback<T> callback(MutableLiveData<T> liveData,
                                      MutableLiveData<String> error) {
        return new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    liveData.postValue(response.body());
                } else {
                    error.postValue("Error " + response.code());
                }
            }
            @Override
            public void onFailure(Call<T> call, Throwable t) {
                error.postValue("Error de red: " + t.getMessage());
            }
        };
    }
}
