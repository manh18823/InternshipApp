package com.example.weather.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.Models.ForecastItem;
import com.example.weather.R;
import com.example.weather.Utils.WeatherUtils;

import java.util.ArrayList;
import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>{

    private List<ForecastItem> forecastList = new ArrayList<>();


    public void setData(List<ForecastItem> list) {
        this.forecastList = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_forecast, parent,false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        ForecastItem item = forecastList.get(position);
        if (item == null) return;

        holder.nextDay.setText(item.nextDay);
        holder.tempMin.setText(String.format("Min: %.1f°C", item.tempMin));
        holder.tempMax.setText(String.format("Max: %.1f°C", item.tempMax));
        WeatherUtils.changeIconFromWeatherCondition(holder.imgForecastWeather, item.condition);

    }

    @Override
    public int getItemCount() {
        if(forecastList != null){
            return forecastList.size();
        }
        return 0;
    }

    public  class ForecastViewHolder extends RecyclerView.ViewHolder{
        TextView nextDay, tempMax, tempMin;
        ImageView imgForecastWeather;
        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            nextDay = itemView.findViewById(R.id.day_next);
            tempMax = itemView.findViewById(R.id.tv_max);
            tempMin = itemView.findViewById(R.id.tv_min);
            imgForecastWeather = itemView.findViewById(R.id.img_forecast_weather);
        }
    }
}
