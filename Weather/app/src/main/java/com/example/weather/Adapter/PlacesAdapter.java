package com.example.weather.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.Api.RecyclerViewInterface;
import com.example.weather.R;
import com.example.weather.Utils.WeatherUtils;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlacesViewHolder> {
    private List<String> places;
    private RecyclerViewInterface item;
    public PlacesAdapter(List<String> places) {
        this.places = places;
    }

    public void setRecyclerViewInterface(RecyclerViewInterface item) {
        this.item = item;
    }
    public void updateData(List<String> newPlaces) {
        this.places = newPlaces;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_items, parent,false);
        return new PlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder holder, int position) {
        String place = places.get(position);
        if(place == null){
            return;
        }
        holder.location.setText(place);
        WeatherUtils.callApiManager(holder.itemView.getContext(), place, holder.temp, holder.imgWeather);

        holder.itemView.setOnClickListener(x -> {
            if(item != null){
                item.onClickItem(places.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        if(places != null){
            return  places.size();
        }
        return 0;
    }

    public  class PlacesViewHolder extends RecyclerView.ViewHolder{
        TextView location, temp;
        ImageView imgWeather;
        public PlacesViewHolder(@NonNull View itemView) {
            super(itemView);
            location = itemView.findViewById(R.id.txt_placeName);
            imgWeather = itemView.findViewById(R.id.img_icon_weather);
            temp = itemView.findViewById(R.id.txt_temp);
        }
    }
}
