package com.example.car_rent.models;

import com.google.gson.annotations.SerializedName;

public class Stat {
    @SerializedName("loyer_total")
    private String loyerTotal;

    @SerializedName("loyer_minimal")
    private float loyerMinimal;

    @SerializedName("loyer_maximal")
    private float loyerMaximal;

    // Getters
    public float getLoyerTotal() {
        return loyerTotal != null ? Float.parseFloat(loyerTotal) : 0;
    }
    public float getLoyerMinimal() { return loyerMinimal; }
    public float getLoyerMaximal() { return loyerMaximal; }
}