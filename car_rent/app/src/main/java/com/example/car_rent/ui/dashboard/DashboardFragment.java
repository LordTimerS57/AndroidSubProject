package com.example.car_rent.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.car_rent.R;
import com.example.car_rent.databinding.FragmentDashboardBinding;
import com.example.car_rent.models.Car; // Assurez-vous d'avoir ce modèle

import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel dashboardViewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Écouter le signal de modification réussie
        getParentFragmentManager().setFragmentResultListener("rental_updated", getViewLifecycleOwner(), (requestKey, result) -> {
            if (result.getBoolean("refresh")) {
                dashboardViewModel.refreshData(); // Force le ViewModel à recharger les données de l'API
            }
        });

        // Écouter le signal d'ajout réussi
        getParentFragmentManager().setFragmentResultListener("rental_added", getViewLifecycleOwner(), (requestKey, result) -> {
            if (result.getBoolean("refresh")) {
                dashboardViewModel.refreshData();
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 1. Observer le texte du titre
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), binding.textDashboard::setText);

        // 2. Observer la liste des voitures/locations venant du serveur
        dashboardViewModel.getCars().observe(getViewLifecycleOwner(), this::updateTable);

        // 3. Observer les erreurs
        dashboardViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });

        return root;
    }

    // Méthode pour remplir le tableau dynamiquement
    private void updateTable(List<Car> cars) {
        // On vide le tableau actuel (sauf si vous avez mis l'entête dans un autre TableLayout)
        binding.tableRentals.removeAllViews();

        for (Car car : cars) {
            // Création de la ligne
            TableRow row = new TableRow(getContext());
            row.setPadding(0, 10, 0, 10);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);

            // Colonne Id
            row.addView(createCell(String.valueOf(car.getId()), 0.7f));
            // Colonne Locataire
            row.addView(createCell(car.getLocataire(), 1f));
            // Colonne Voiture
            row.addView(createCell(car.getVoiture(), 1f));
            // Colonne Jours
            row.addView(createCell(String.valueOf(car.getJours()), 0.5f));
            // Colonne Loyer par jour
            row.addView(createCell(String.valueOf(car.getPrix()), 0.7f));
            // Colonne Loyer
            row.addView(createCell(String.valueOf(car.getLoyer()), 1f));

            // Colonne ACTIONS (Boutons)
            row.addView(createActionButtons(car));

            // Ajouter la ligne au TableLayout
            binding.tableRentals.addView(row);

            // Ajouter une ligne de séparation (optionnel)
            View divider = new View(getContext());
            divider.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
            divider.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black)); // ou une couleur grise
            binding.tableRentals.addView(divider);
        }
    }

    // Fonction utilitaire pour créer une cellule de texte
    private TextView createCell(String text, float weight) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight));
        tv.setTextSize(13);
        return tv;
    }

    // Fonction pour créer les boutons Modifier et Supprimer pour chaque ligne
    private View createActionButtons(Car car) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(android.view.Gravity.CENTER);
        layout.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.8f));

        // Bouton MODIFIER
        ImageButton btnEdit = new ImageButton(getContext());
        btnEdit.setImageResource(android.R.drawable.ic_menu_edit);
        btnEdit.setBackgroundResource(android.R.color.transparent);
        btnEdit.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_500));
        btnEdit.setOnClickListener(v -> {
            // On passe l'ID de la voiture à l'écran de modification
            Bundle bundle = new Bundle();
            bundle.putInt("num_loc", car.getId());
            Navigation.findNavController(v).navigate(R.id.action_navigation_dashboard_to_navigation_edit_rental, bundle);
        });

        // Bouton SUPPRIMER
        ImageButton btnDelete = new ImageButton(getContext());
        btnDelete.setImageResource(android.R.drawable.ic_menu_delete);
        btnDelete.setBackgroundResource(android.R.color.transparent);
        btnDelete.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));
        btnDelete.setOnClickListener(v -> confirmDelete(car, car.getId()));

        layout.addView(btnEdit);
        layout.addView(btnDelete);

        return layout;
    }

    private void confirmDelete(Car car, int id) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Suppression")
                .setMessage("Supprimer la location de " + car.getLocataire() + " ?")
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    // Ici, appelez une méthode de votre ViewModel pour supprimer sur le serveur
                    dashboardViewModel.deleteCar(car.getId());
                    Toast.makeText(getContext(), "Suppression de l'ID : " + car.getId(), Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}