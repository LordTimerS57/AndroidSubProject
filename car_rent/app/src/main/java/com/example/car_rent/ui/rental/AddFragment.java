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

import com.example.car_rent.databinding.AddFragmentBinding;

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
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
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
                double days = Double.parseDouble(daysStr);
                double price = Double.parseDouble(priceStr);
                double total = days * price;
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
                    performSave(tenant, car, daysStr, priceStr);
                })
                .show();
    }

    // Nouvelle méthode qui contient la logique d'envoi final
    private void performSave(String tenant, String car, String daysStr, String priceStr) {
        Bundle result = new Bundle();
        result.putString("tenant_name", tenant);
        result.putString("car_model", car);
        result.putInt("days", Integer.parseInt(daysStr));
        result.putDouble("price_per_day", Double.parseDouble(priceStr));
        result.putDouble("total_price", Double.parseDouble(binding.etAddTotalPrice.getText().toString()));

        getParentFragmentManager().setFragmentResult("edit_rental_request", result);

        Toast.makeText(getContext(), "Location mise à jour", Toast.LENGTH_SHORT).show();
        dismiss();
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