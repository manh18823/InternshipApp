package com.example.weather.Utils;

import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class WeatherUIComponents {
    public TextView temp, humidity, windSpeed,
            sunRise, sunSet, seaLevel, condition,
            maxTemp, minTemp, weather,
            day, date, location;
    public LottieAnimationView lottieAnimationView;
    public android.view.ViewGroup constraintLayout;

    public WeatherUIComponents(TextView temp, TextView humidity, TextView windSpeed, TextView sunRise, TextView sunSet,
                               TextView seaLevel, TextView condition, TextView maxTemp, TextView minTemp,
                               TextView weather, TextView day, TextView date, TextView location,
                               LottieAnimationView lottieAnimationView, android.view.ViewGroup constraintLayout) {
        this.temp = temp;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.sunRise = sunRise;
        this.sunSet = sunSet;
        this.seaLevel = seaLevel;
        this.condition = condition;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.weather = weather;
        this.day = day;
        this.date = date;
        this.location = location;
        this.lottieAnimationView = lottieAnimationView;
        this.constraintLayout = constraintLayout;
    }
}

