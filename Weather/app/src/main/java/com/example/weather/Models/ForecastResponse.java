package com.example.weather.Models;

import java.util.List;

public class ForecastResponse {
    public List<WeatherData> list;
    public City city;

    public static class City {
        public String name;
        public String country;
        public long sunrise;
        public long sunset;
    }
}
