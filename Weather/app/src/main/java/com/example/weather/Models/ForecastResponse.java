package com.example.weather.Models;

import java.util.List;

public class ForecastResponse {
    public String cod;
    public int message;
    public int cnt;
    public List<ForecastListItem> list;
    public City city;
    public String country;
    public int population;
    public int timezone;
    public long sunrise;
    public long sunset;
}
