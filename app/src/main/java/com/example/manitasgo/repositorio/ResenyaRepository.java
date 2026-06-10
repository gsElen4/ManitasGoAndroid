package com.example.manitasgo.repositorio;

import androidx.lifecycle.MutableLiveData;

import com.example.manitasgo.api.ResenyaService;
import com.example.manitasgo.api.SupabaseClient;
import com.example.manitasgo.modelo.Resenya;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResenyaRepository {

    private final ResenyaService service;

    public ResenyaRepository() {
        service = SupabaseClient.getInstance().createService(ResenyaService.class);
    }

    public void getByComercio(String comercioId,
                               MutableLiveData<List<Resenya>> liveData,
                               MutableLiveData<String> error) {
        service.getByComercio("eq." + comercioId, "*", "fecha.desc")
                .enqueue(new Callback<List<Resenya>>() {
                    @Override
                    public void onResponse(Call<List<Resenya>> call, Response<List<Resenya>> response) {
                        if (response.isSuccessful()) liveData.postValue(response.body());
                        else error.postValue("Error " + response.code());
                    }
                    @Override
                    public void onFailure(Call<List<Resenya>> call, Throwable t) {
                        error.postValue(t.getMessage());
                    }
                });
    }

    public void crear(Resenya resenya,
                      MutableLiveData<Boolean> exito,
                      MutableLiveData<String> error) {
        service.crear(resenya).enqueue(new Callback<List<Resenya>>() {
            @Override
            public void onResponse(Call<List<Resenya>> call, Response<List<Resenya>> response) {
                exito.postValue(response.isSuccessful());
                if (!response.isSuccessful()) error.postValue("Error " + response.code());
            }
            @Override
            public void onFailure(Call<List<Resenya>> call, Throwable t) {
                error.postValue(t.getMessage());
            }
        });
    }
}
