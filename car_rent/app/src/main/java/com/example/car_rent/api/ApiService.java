package com.example.car_rent.api;

import com.example.car_rent.models.Car;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("cars")
    Call<List<Car>> getCars();
}
