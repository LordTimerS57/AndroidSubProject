package com.example.car_rent.models;

public class Car {
    private int id;
    private String locataire;
    private String voiture;
    private int jours;
    private double prix;
    private double loyer;

    public Car(int id, String locataire, String voiture, int jours, double prix, double loyer) {
        this.id = id;
        this.locataire = locataire;
        this.voiture = voiture;
        this.jours = jours;
        this.prix = prix;
        this.loyer = loyer;
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
    public double getPrix() {
        return prix;
    }
    public void setPrix(double prix) {
        this.prix = prix;
    }
    public double getLoyer() {
        return loyer;
    }
    public void setLoyer(double loyer) {
        this.loyer = loyer;
    }
}
