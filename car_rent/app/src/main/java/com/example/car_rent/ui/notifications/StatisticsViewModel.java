package com.example.car_rent.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.car_rent.api.ApiService;
import com.example.car_rent.api.RetrofitClient;
import com.example.car_rent.models.Stat;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsViewModel extends ViewModel {

    private final MutableLiveData<String> mText = new MutableLiveData<>();
    private final MutableLiveData<List<BarEntry>> mEntries = new MutableLiveData<>();

    // NOUVEAU : LiveData pour les noms des barres (Labels)
    private final MutableLiveData<List<String>> mLabels = new MutableLiveData<>();

    private final MutableLiveData<String> errorMsg = new MutableLiveData<>();

    public StatisticsViewModel() {
        mText.setValue("Statistiques des prix de locations");
    }

    public void loadData() {
        ApiService apiService = RetrofitClient.getApiService();

        apiService.getStats().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Stat>> call, Response<List<Stat>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {

                    Stat stat = response.body().get(0);

                    List<BarEntry> entries = new ArrayList<>();
                    List<String> labels = new ArrayList<>();

                    // 1. Barre Total
                    entries.add(new BarEntry(0f, stat.getLoyerTotal()));
                    labels.add("Total"); // Nom spécifique

                    // 2. Barre Minimal
                    entries.add(new BarEntry(1f, stat.getLoyerMinimal()));
                    labels.add("Minimal"); // Nom spécifique

                    // 3. Barre Maximal
                    entries.add(new BarEntry(2f, stat.getLoyerMaximal()));
                    labels.add("Maximal"); // Nom spécifique

                    mLabels.setValue(labels);   // On envoie les noms
                    mEntries.setValue(entries); // On envoie les valeurs
                } else {
                    errorMsg.setValue("Erreur : Données statistiques vides");
                }
            }

            @Override
            public void onFailure(Call<List<Stat>> call, Throwable t) {
                errorMsg.setValue("Erreur API : " + t.getMessage());
            }
        });
    }

    public LiveData<List<BarEntry>> getLoadedData() { return mEntries; }

    // NOUVEAU : Getter pour récupérer les noms dans le Fragment
    public LiveData<List<String>> getLabels() { return mLabels; }

    public LiveData<String> getText() { return mText; }
    public LiveData<String> getErrorMsg() { return errorMsg; }
}