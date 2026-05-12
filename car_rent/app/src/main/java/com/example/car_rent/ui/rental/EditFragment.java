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
import com.example.car_rent.databinding.EditFragmentBinding;
import com.example.car_rent.models.Car;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditFragment extends DialogFragment {

    private EditFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = EditFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            // Utilisation de "num_loc" partout pour être cohérent
            int carId = getArguments().getInt("num_loc");
            fetchCarDetails(carId);
        }
    }

    private void fetchCarDetails(int id) {
        ApiService apiService = RetrofitClient.getApiService();

        apiService.getCarById(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Car>> call, @NonNull Response<List<Car>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Car car = response.body().get(0);

                    binding.etEditTenantName.setText(car.getLocataire());
                    binding.etEditCarModel.setText(car.getVoiture());
                    binding.etEditDays.setText(String.valueOf(car.getJours()));
                    binding.etEditPricePerDay.setText(String.valueOf(car.getPrix()));

                    calculateTotal();
                } else {
                    Toast.makeText(getContext(), "Données non trouvées", Toast.LENGTH_SHORT).show();
                }
                setupListeners();
            }

            @Override
            public void onFailure(@NonNull Call<List<Car>> call, @NonNull Throwable t) { // Changé Car en List<Car>
                Toast.makeText(getContext(), "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Configure les listeners pour le calcul auto et les boutons
     */
    private void setupListeners() {
        TextWatcher calculator = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { calculateTotal(); }
        };

        binding.etEditDays.addTextChangedListener(calculator);
        binding.etEditPricePerDay.addTextChangedListener(calculator);

        binding.btnEditSave.setOnClickListener(v -> updateRental());
        binding.btnEditCancel.setOnClickListener(v -> dismiss());
    }

    private void calculateTotal() {
        try {
            int days = Integer.parseInt(binding.etEditDays.getText().toString());
            long price = Long.parseLong(binding.etEditPricePerDay.getText().toString());
            binding.etEditTotalPrice.setText(String.valueOf(days * price));
        } catch (Exception e) {
            binding.etEditTotalPrice.setText("0");
        }
    }


    private void updateRental() {
        if (getArguments() != null){
            int carId = getArguments().getInt("num_loc");

            String idStr = String.valueOf(carId);
            String tenant = binding.etEditTenantName.getText().toString();
            String car = binding.etEditCarModel.getText().toString();
            String daysStr = binding.etEditDays.getText().toString();
            String priceStr = binding.etEditPricePerDay.getText().toString();
            String totalPriceStr = binding.etEditTotalPrice.getText().toString();

            if (tenant.isEmpty() || car.isEmpty() || daysStr.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Confirmation")
                    .setMessage("Voulez-vous modifier cette location ?")
                    .setNegativeButton("Non", null)
                    .setPositiveButton("Oui", (dialog, which) -> {
                        performSave(idStr, tenant, car, daysStr, priceStr, totalPriceStr);
                    })
                    .show();
        }
    }


    // Nouvelle méthode qui contient la logique d'envoi final
    private void performSave(String tag, String tenant, String carModel, String daysStr, String priceStr, String totalPriceStr) {
        try {
            // 1. Conversion des données
            int id = Integer.parseInt(tag);
            int days = Integer.parseInt(daysStr);
            long price = Long.parseLong(priceStr);
            long total = Long.parseLong(totalPriceStr);

            // 2. Création de l'objet Car
            Car newCar = new Car();
            newCar.setId(id);
            newCar.setLocataire(tenant);
            newCar.setVoiture(carModel);
            newCar.setJours(days);
            newCar.setPrix(price);
            newCar.setLoyer(total);

            // 3. Appel API
            ApiService apiService = RetrofitClient.getApiService();
            apiService.updateCar(Integer.parseInt(tag), newCar).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<Car> call, @NonNull Response<Car> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Location modifiée avec succès !", Toast.LENGTH_SHORT).show();

                        Bundle result = new Bundle();
                        result.putBoolean("refresh", true);
                        getParentFragmentManager().setFragmentResult("rental_updated", result);

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
