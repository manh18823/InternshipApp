package com.example.weather.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.Api.ApiService;
import com.example.weather.Models.ForecastItem;
import com.example.weather.Models.ForecastResponse;
import com.example.weather.Models.WeatherData;
import com.example.weather.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherUtils {
    public interface WeatherCallback {
        void onResult(String condition, String descriptionWeather);
    }
    public static void callApiUI(Context context, String place, WeatherUIComponents ui) {
        final boolean[] forecastLoaded = {false};
        final boolean[] weatherLoaded = {false};
        final double[] minTemp = {Double.MAX_VALUE};
        final double[] maxTemp = {Double.MIN_VALUE};
        ApiService.apiService.convertForecast(place, "f247b776bb63be4537ae6466fdcb2d1d", "metric")
                .enqueue(new Callback<ForecastResponse>() {

                    @Override
                    public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().list != null) {
                            List<WeatherData> forecastList = response.body().list;
                            if (forecastList.isEmpty()) return;

                            for (WeatherData item : forecastList) {
                                if (item.main.temp_min < minTemp[0]) minTemp[0] = item.main.temp_min;
                                if (item.main.temp_max > maxTemp[0]) maxTemp[0] = item.main.temp_max;
                            }
                            forecastLoaded[0] = true;
                            if (weatherLoaded[0]) {
                                ui.maxTemp.setText(String.format("Max: %.1f°C", maxTemp[0]));
                                ui.minTemp.setText(String.format("Min: %.1f°C", minTemp[0]));
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ForecastResponse> call, Throwable t) {
                        Log.e("WeatherAPI", "Forecast API failed", t);
                    }
                });

        ApiService.apiService.convertWeather(place, "f247b776bb63be4537ae6466fdcb2d1d", "metric")
                .enqueue(new Callback<WeatherData>() {

                    @Override
                    public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            WeatherData responseBody = response.body();
                            ui.temp.setText(String.format("%.1f°C", responseBody.main.temp));
                            ui.humidity.setText(responseBody.main.humidity + "%");
                            ui.windSpeed.setText(responseBody.wind.speed + " m/s");
                            ui.sunRise.setText(timeFormat(responseBody.sys.sunrise));
                            ui.sunSet.setText(timeFormat(responseBody.sys.sunset));
                            ui.seaLevel.setText(responseBody.main.pressure + " hPa");
                            ui.condition.setText(responseBody.weather.get(0).main);
                            ui.weather.setText(responseBody.weather.get(0).main);
                            ui.day.setText(dayName(responseBody.dt));
                            ui.date.setText(dateName(responseBody.dt));
                            ui.location.setText(place);
                            changeImageFromWeatherCondition(ui, ui.condition.getText().toString());

                            weatherLoaded[0] = true;
                            if (forecastLoaded[0]) {
                                ui.maxTemp.setText(String.format("Max: %.1f°C", maxTemp[0]));
                                ui.minTemp.setText(String.format("Min: %.1f°C", minTemp[0]));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherData> call, Throwable t) {
                        Log.e("WeatherAPI", "Weather API failed", t);
                    }
                });

    }

//    public static void callForecastList(String place, Consumer<List<ForecastItem>> callback) {
//        ApiService.apiService.convertForecast(place, "f247b776bb63be4537ae6466fdcb2d1d", "metric")
//                .enqueue(new Callback<ForecastResponse>() {
//                    @Override
//                    public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
//                        if (response.isSuccessful() && response.body() != null) {
//                            List<> result = new ArrayList<>();
//                            Map<String, List<WeatherData>> dailyMap = new LinkedHashMap<>();
//
//                            for (WeatherData data : response.body().list) {
//                                String date = data.dt_txt.split(" ")[0];
//                                dailyMap.computeIfAbsent(date, k -> new ArrayList<>()).add(data);
//                            }
//
//                            List<Map.Entry<String, List<WeatherData>>> entries = new ArrayList<>(dailyMap.entrySet());
//                            if (!entries.isEmpty()) entries.remove(0); // bỏ hôm nay
//
//                            for (int i = 0; i < Math.min(3, entries.size()); i++) {
//                                List<WeatherData> dayList = entries.get(i).getValue();
//                                WeatherData chosen = dayList.stream()
//                                        .filter(d -> d.dt_txt.contains("12:00:00"))
//                                        .findFirst()
//                                        .orElse(dayList.get(0));
//
//                                String dayName = WeatherUtils.dayName(chosen.dt);
//                                result.add(new ForecastItem(
//                                        dayName,
//                                        chosen.main.temp_min,
//                                        chosen.main.temp_max,
//                                        chosen.weather.get(0).main
//                                ));
//                            }
//
//                            callback.accept(result);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ForecastResponse> call, Throwable t) {
//                        Log.e("ForecastList", "Failed", t);
//                    }
//                });
//    }

    public static void callApiReceiver(Context context, String place, WeatherCallback callback) {
        ApiService.apiService.convertForecast(place, "f247b776bb63be4537ae6466fdcb2d1d", "metric")
                .enqueue(new Callback<ForecastResponse>() {
                    @Override
                    public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().list != null) {
                            List<WeatherData> forecastList = response.body().list;
                            if (forecastList.isEmpty()) return;

                            WeatherData current = forecastList.get(0);
                            String condition = current.weather.get(0).main;
                            String descriptionWeather = current.weather.get(0).description;

                            callback.onResult(condition, descriptionWeather);
                        }
                    }

                    @Override
                    public void onFailure(Call<ForecastResponse> call, Throwable t) {
                        callback.onResult("Unclear", "Connection error");
                    }
                });
    }

    public static void callApiManager(Context context, String place, TextView tempResult, ImageView weatherIcon) {
        ApiService.apiService.convertForecast(place, "f247b776bb63be4537ae6466fdcb2d1d", "metric")
                .enqueue(new Callback<ForecastResponse>() {
                    @Override
                    public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().list != null) {
                            List<WeatherData> forecastList = response.body().list;
                            if (forecastList.isEmpty()) return;

                            WeatherData current = forecastList.get(0);
                            double temperature = current.main.temp;
                            tempResult.setText(String.format(Locale.US, "%.1f°C", temperature));

                            String weatherMain = current.weather.get(0).main;
                            int iconResId = getWeatherIconResource(weatherMain);
                            weatherIcon.setImageResource(iconResId);

                        } else {
                            Log.e("WeatherAPI", "Unsuccessful response or empty body");
                        }
                    }

                    @Override
                    public void onFailure(Call<ForecastResponse> call, Throwable t) {
                        Log.e("WeatherAPI", "Error fetching weather", t);
                    }
                });
    }

    private static int getWeatherIconResource(String weatherMain) {
        switch (weatherMain) {
            case "Clear":
                return R.drawable.clear_sky;
            case "Rain":
                return R.drawable.heavy_rain;
            case "Clouds":
                return R.drawable.cloud;
            case "Snow":
                return R.drawable.snowy;
            case "Thunderstorm":
                return R.drawable.thunderstorm;
            default:
                return R.drawable.sunny;
        }
    }
    private static void changeImageFromWeatherCondition(WeatherUIComponents ui, String condition) {
        condition = condition.toLowerCase();

        if (condition.contains("clear") || condition.contains("sunny")) {
            ui.constraintLayout.setBackgroundResource(R.drawable.sunny_background);
            ui.lottieAnimationView.setAnimation(R.raw.sun);
        } else if (condition.contains("cloud") || condition.contains("mist") || condition.contains("fog")) {
            ui.constraintLayout.setBackgroundResource(R.drawable.colud_background);
            ui.lottieAnimationView.setAnimation(R.raw.cloud);
        } else if (condition.contains("rain") || condition.contains("drizzle")) {
            ui.constraintLayout.setBackgroundResource(R.drawable.rain_background);
            ui.lottieAnimationView.setAnimation(R.raw.rain);
        } else if (condition.contains("snow")) {
            ui.constraintLayout.setBackgroundResource(R.drawable.snow_background);
            ui.lottieAnimationView.setAnimation(R.raw.snow);
        }else {
            ui.constraintLayout.setBackgroundResource(R.drawable.sunny_background);
            ui.lottieAnimationView.setAnimation(R.raw.sun);
        }
        ui.lottieAnimationView.playAnimation();
    }

    public static void changeIconFromWeatherCondition(ImageView imgForecast, String condition) {
        condition = condition.toLowerCase();

        if (condition.contains("clear") || condition.contains("sunny")) {
            imgForecast.setImageResource(R.drawable.clear_sky);
        } else if (condition.contains("cloud") || condition.contains("mist") || condition.contains("fog")) {
            imgForecast.setImageResource(R.drawable.cloud);
        } else if (condition.contains("rain") || condition.contains("drizzle")) {
            imgForecast.setImageResource(R.drawable.heavy_rain);
        } else if (condition.contains("snow")) {
            imgForecast.setImageResource(R.drawable.snowy);
        }else {
            imgForecast.setImageResource(R.drawable.clear_sky);
        }
    }

    private static String dayName(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        return sdf.format(new Date(timeStamp * 1000));
    }
    private static String dateName(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(new Date(timeStamp * 1000));
    }
    private static String timeFormat(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp * 1000));
    }


}
