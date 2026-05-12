package com.example.car_rent.ui.notifications;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.car_rent.databinding.FragmentStatsBinding;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

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

        // On observe les données (BarEntries) ET les labels (Noms des barres)
        statsModel.getLoadedData().observe(getViewLifecycleOwner(), entries -> {
            if (entries != null && !entries.isEmpty()){
                // On récupère aussi les labels depuis le ViewModel
                List<String> labels = statsModel.getLabels().getValue();
                setupBarChart(entries, labels);
            }
        });

        statsModel.loadData();
    }

    private void setupBarChart(List<BarEntry> entries, List<String> labels) {
        BarDataSet dataSet = new BarDataSet(entries, "Valeur en ariary");

        // Couleurs par barre pour différencier Total, Min, Max
        dataSet.setColors(new int[]{Color.BLUE, Color.GREEN, Color.RED});
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f); // Largeur des barres
        binding.chartContent.setData(barData);

        // --- Configuration de l'axe X (les noms en bas) ---
        XAxis xAxis = binding.chartContent.getXAxis();
        if (labels != null) {
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        }
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Force l'affichage de chaque label
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawGridLines(false); // Design plus propre

        // --- Personnalisation générale ---
        binding.chartContent.getDescription().setEnabled(false);
        binding.chartContent.getAxisRight().setEnabled(false); // Enlever l'axe de droite
        binding.chartContent.getAxisLeft().setAxisMinimum(0f); // Commencer à zéro
        binding.chartContent.animateY(1000); // Animation

        binding.chartContent.invalidate(); // Rafraîchir
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}