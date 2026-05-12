package com.example.car_rent.ui.rental;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.car_rent.api.ApiService;
import com.example.car_rent.api.RetrofitClient;
import com.example.car_rent.databinding.AddFragmentBinding;
import com.example.car_rent.models.Car;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFragment extends DialogFragment {

    private AddFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // COHÉSION : Liaison directe avec add_fragment.xml via ViewBinding
        binding = AddFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- LOGIQUE DE CALCUL AUTOMATIQUE ---
        TextWatcher autoCalculator = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                calculateTotal();
            }
        };

        // On écoute les changements sur les jours et le prix
        binding.etAddDays.addTextChangedListener(autoCalculator);
        binding.etAddPricePerDay.addTextChangedListener(autoCalculator);

        // --- ACTIONS DES BOUTONS ---
        binding.btnAddSave.setOnClickListener(v -> saveRental());
        binding.btnAddCancel.setOnClickListener(v -> dismiss());
    }

    /**
     * Calcule dynamiquement le prix total en fonction des jours et du prix journalier
     */
    private void calculateTotal() {
        try {
            String daysStr = binding.etAddDays.getText().toString();
            String priceStr = binding.etAddPricePerDay.getText().toString();

            if (!daysStr.isEmpty() && !priceStr.isEmpty()) {
                int days = Integer.parseInt(daysStr);
                long price = Long.parseLong(priceStr);
                long total = days * price;
                binding.etAddTotalPrice.setText(String.valueOf(total));
            } else {
                binding.etAddTotalPrice.setText("");
            }
        } catch (NumberFormatException e) {
            binding.etAddTotalPrice.setText("0.0");
        }
    }

    /**
     * Récupère les données et les envoie au DashboardFragment
     */

    // Remplacez votre ancienne méthode updateRental par celle-ci
    private void saveRental() {
        // 1. Extraction et Validation (on garde la logique actuelle)
        String tenant = binding.etAddTenantName.getText().toString();
        String car = binding.etAddCarModel.getText().toString();
        String daysStr = binding.etAddDays.getText().toString();
        String priceStr = binding.etAddPricePerDay.getText().toString();
        String totalPriceStr = binding.etAddTotalPrice.getText().toString();

        if (tenant.isEmpty() || car.isEmpty() || daysStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Création de la boîte de dialogue de confirmation
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Confirmation")
                .setMessage("Voulez-vous vraiment confirmer cette location ?")
                .setNegativeButton("Non", null) // Ne fait rien, ferme juste le dialogue
                .setPositiveButton("Oui", (dialog, which) -> {
                    // 3. Si l'utilisateur confirme, on exécute l'envoi
                    performSave(tenant, car, daysStr, priceStr, totalPriceStr);
                })
                .show();
    }

    private void performSave(String tenant, String carModel, String daysStr, String priceStr, String totalPriceStr) {
        try {
            int days = Integer.parseInt(daysStr);
            long price = (long) Double.parseDouble(priceStr);
            long total = (long) Double.parseDouble(totalPriceStr);

            // 2. Création de l'objet Car
            Car newCar = new Car();
            newCar.setLocataire(tenant);
            newCar.setVoiture(carModel);
            newCar.setJours(days);
            newCar.setPrix(price);
            newCar.setLoyer(total);

            // 3. Appel API
            ApiService apiService = RetrofitClient.getApiService();
            apiService.addCar(newCar).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<Car> call, @NonNull Response<Car> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Location ajoutée avec succès !", Toast.LENGTH_SHORT).show();

                        Bundle result = new Bundle();
                        result.putBoolean("refresh", true);
                        getParentFragmentManager().setFragmentResult("rental_added", result);

                        dismiss(); // Fermer le formulaire
                    } else {
                        Toast.makeText(getContext(), "Erreur serveur : " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Car> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), "Échec de la connexion : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Format de nombre invalide", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Ajustement de la taille du dialogue pour qu'il soit élégant
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}