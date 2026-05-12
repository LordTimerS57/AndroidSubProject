package com.example.car_rent.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.car_rent.api.ApiService;
import com.example.car_rent.api.RetrofitClient; // Utilisez le client centralisé
import com.example.car_rent.models.Car;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<List<Car>> mCars = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Liste des véhicules");
        refreshData();
    }

    private void fetchCarsFromServer() {
        ApiService apiService = RetrofitClient.getApiService();

        apiService.getCars().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Car>> call, @NonNull Response<List<Car>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Retrofit a déjà transformé le JSON en List<Car>
                    mCars.setValue(response.body());
                } else {
                    errorMessage.setValue("Erreur : " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Car>> call, @NonNull Throwable t) {
                // Si ça arrive ici avec Ngrok, vérifiez que le tunnel est toujours ouvert
                errorMessage.setValue("Erreur réseau : " + t.getMessage());
            }
        });
    }

    public LiveData<List<Car>> getCars() {
        if (mCars.getValue() == null) {
            refreshData(); // Charger la première fois
        }
        return mCars;
    }

    public void refreshData() {
        fetchCarsFromServer();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getError() {
        return errorMessage;
    }

    public void deleteCar(int id) {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.deleteCar(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    // Si la suppression est réussie, on met à jour la liste
                    fetchCarsFromServer();
                } else {
                    errorMessage.setValue("Erreur : " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Si ça arrive ici avec Ngrok, vérifiez que le tunnel est toujours ouvert
                errorMessage.setValue("Erreur réseau : " + t.getMessage());
            }
        });
    }
}