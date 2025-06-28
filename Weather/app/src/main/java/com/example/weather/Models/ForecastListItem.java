package com.example.weather.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForecastListItem {
    public int dt;
    public Main main;
    public List<Weather> weather;
    public Clouds clouds;
    public Wind wind;
    public int visibility;
    public double pop;
    public Sys sys;
    public String dt_txt;

}
