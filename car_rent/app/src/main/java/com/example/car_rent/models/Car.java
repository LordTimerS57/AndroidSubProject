package com.example.car_rent.models;

import com.google.gson.annotations.SerializedName;

public class Car {

    @SerializedName("num_loc")
    private int id;

    @SerializedName("nom_loc")
    private String locataire;

    @SerializedName("design_voiture")
    private String voiture;

    @SerializedName("nombre_de_jour")
    private int jours;

    @SerializedName("taux_journalier")
    private long prix; // Prix par jour

    @SerializedName("loyer")
    private long loyer; // Total (jours * prix)

    // Constructeur vide (Obligatoire pour Gson)
    public Car() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocataire() {
        return locataire;
    }

    public void setLocataire(String locataire) {
        this.locataire = locataire;
    }

    public String getVoiture() {
        return voiture;
    }

    public void setVoiture(String voiture) {
        this.voiture = voiture;
    }

    public int getJours() {
        return jours;
    }

    public void setJours(int jours) {
        this.jours = jours;
    }

    public long getPrix() {
        return prix;
    }

    public void setPrix(long prix) {
        this.prix = prix;
    }

    public long getLoyer() {
        return loyer;
    }

    public void setLoyer(long loyer) {
        this.loyer = loyer;
    }
}