package com.example.car_rent.ui.notifications;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.car_rent.databinding.FragmentStatsBinding;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

public class StatisticsFragment extends Fragment {

    private FragmentStatsBinding binding;
    private StatisticsViewModel statsModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statsModel = new ViewModelProvider(this).get(StatisticsViewModel.class);
        binding = FragmentStatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        statsModel.getText().observe(getViewLifecycleOwner(), binding.textNotifications::setText);
        statsModel.getLoadedData().observe(getViewLifecycleOwner(), entries -> {
            if (entries != null){
                setupBarChart(entries);
            }
        });
        statsModel.loadData();
    }

    private void setupBarChart(List<BarEntry> entries) {
        BarDataSet dataSet = new BarDataSet(entries, "Locations de voitures");

        // Design professionnel : Gris neutre et texte sombre
        dataSet.setColor(Color.LTGRAY);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        binding.chartContent.setData(barData);

        // Personnalisation du graphique
        binding.chartContent.getDescription().setEnabled(false);
        binding.chartContent.getAxisRight().setEnabled(false); // Épuré : on enlève l'axe droit
        binding.chartContent.animateY(1000); // Animation fluide

        binding.chartContent.invalidate(); // Rafraîchir l'affichage
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}