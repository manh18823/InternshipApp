package com.example.weather.Models;

public class ForecastItem {
    public String nextDay;
    public double tempMin;
    public double tempMax;
    public String condition;

    public ForecastItem(String nextDay, double tempMin, double tempMax, String condition) {
        this.nextDay = nextDay;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.condition = condition;
    }
}
