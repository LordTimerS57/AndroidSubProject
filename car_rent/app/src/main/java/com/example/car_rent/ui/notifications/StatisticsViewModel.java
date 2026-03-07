package com.example.car_rent.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;


public class StatisticsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<List<BarEntry>> dataEntry;

    public StatisticsViewModel() {
        dataEntry = new MutableLiveData<>();
        mText = new MutableLiveData<>();
        mText.setValue("Statistiques");
    }

    public void loadData() {
        List<BarEntry> data = new ArrayList<>();
        data.add(new BarEntry(1, 15)); // Mois 1, 15 voitures
        data.add(new BarEntry(2, 28));
        data.add(new BarEntry(3, 22));
        dataEntry.setValue(data);
    }

    public LiveData<List<BarEntry>> getLoadedData() { return dataEntry; }

    public LiveData<String> getText() {
        return mText;
    }
}