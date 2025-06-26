package com.example.weather.Api;

import com.example.weather.Models.ForecastResponse;
import com.example.weather.Models.WeatherData;
import com.example.weather.Models.WeatherData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Currency;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    ApiService apiService = new Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @GET("forecast")
    Call<ForecastResponse> convertForecast(@Query("q") String city,
                                          @Query("appid") String apiKey,
                                          @Query("units") String units);

    @GET("weather")
    Call<WeatherData> convertWeather(@Query("q") String city,
                                          @Query("appid") String apiKey,
                                          @Query("units") String units);
}
