package com.example.car_rent.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.car_rent.api.ApiService; // Importez votre interface
import com.example.car_rent.models.Car;    // Importez votre modèle de données

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    // 1. Création d'un LiveData pour la liste de voitures
    private final MutableLiveData<List<Car>> mCars = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Liste des véhicules");

        // 2. Lancer la récupération des données au démarrage
        fetchCarsFromServer();
    }

    private void fetchCarsFromServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://votre-api.com/") // REMPLACEZ PAR VOTRE URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getCars().enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(@NonNull Call<List<Car>> call, Response<List<Car>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 3. Mettre à jour le LiveData avec les données reçues
                    mCars.setValue(response.body());
                } else {
                    errorMessage.setValue("Erreur serveur : " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Car>> call, Throwable t) {
                errorMessage.setValue("Erreur réseau : " + t.getMessage());
            }
        });
    }

    // Getters pour que le Fragment puisse observer les données
    public LiveData<List<Car>> getCars() { return mCars; }
    public LiveData<String> getText() { return mText; }
    public LiveData<String> getError() { return errorMessage; }
}