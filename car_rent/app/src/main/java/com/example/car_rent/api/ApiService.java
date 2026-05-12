package com.example.car_rent.api;

import com.example.car_rent.models.Car;
import com.example.car_rent.models.Stat;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/location")
    Call<List<Car>> getCars();

    @PUT("api/location/{id}")
    Call<Car> updateCar(@Path("id") int id, @Body Car car);

    @DELETE("api/location/{id}")
    Call<Void> deleteCar(@Path("id") int id);

    @POST("api/location")
    Call<Car> addCar(@Body Car car);

    @GET("api/location/{id}")
    Call<List<Car>> getCarById(@Path("id") int id);

    @GET("api/location/stat")
    Call<List<Stat>> getStats();

}
