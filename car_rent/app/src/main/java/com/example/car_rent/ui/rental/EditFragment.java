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
import com.example.car_rent.databinding.EditFragmentBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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

        // 1. RÉCUPÉRER les données envoyées par le Dashboard (Cohésion)
        if (getArguments() != null) {
            binding.etEditTenantName.setText(getArguments().getString("tenant_name"));
            binding.etEditCarModel.setText(getArguments().getString("car_model"));
            binding.etEditDays.setText(String.valueOf(getArguments().getInt("days")));
            binding.etEditPricePerDay.setText(String.valueOf(getArguments().getDouble("price_per_day")));
            calculateTotal(); // Affiche le total initial
        }

        // 2. CALCUL AUTOMATIQUE (TextWatcher)
        TextWatcher calculator = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { calculateTotal(); }
        };
        binding.etEditDays.addTextChangedListener(calculator);
        binding.etEditPricePerDay.addTextChangedListener(calculator);

        // 3. BOUTONS
        binding.btnEditSave.setOnClickListener(v -> updateRental());
        binding.btnEditCancel.setOnClickListener(v -> dismiss());
    }

    private void calculateTotal() {
        try {
            double days = Double.parseDouble(binding.etEditDays.getText().toString());
            double price = Double.parseDouble(binding.etEditPricePerDay.getText().toString());
            binding.etEditTotalPrice.setText(String.valueOf(days * price));
        } catch (Exception e) {
            binding.etEditTotalPrice.setText("0.0");
        }
    }

    // Remplacez votre ancienne méthode updateRental par celle-ci
    private void updateRental() {
        // 1. Extraction et Validation (on garde la logique actuelle)
        String tenant = binding.etEditTenantName.getText().toString();
        String car = binding.etEditCarModel.getText().toString();
        String daysStr = binding.etEditDays.getText().toString();
        String priceStr = binding.etEditPricePerDay.getText().toString();

        if (tenant.isEmpty() || car.isEmpty() || daysStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Création de la boîte de dialogue de confirmation
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Confirmation")
                .setMessage("Voulez-vous vraiment enregistrer ces modifications ?")
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
        result.putDouble("total_price", Double.parseDouble(binding.etEditTotalPrice.getText().toString()));

        getParentFragmentManager().setFragmentResult("edit_rental_request", result);

        Toast.makeText(getContext(), "Location mise à jour", Toast.LENGTH_SHORT).show();
        dismiss();
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
