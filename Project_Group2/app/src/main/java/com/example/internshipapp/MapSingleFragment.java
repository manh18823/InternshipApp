package com.example.internshipapp;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapSingleFragment extends Fragment {

    private MapView mapView;
    private String locationName = "", title = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        View view = inflater.inflate(R.layout.fragment_map_single_osm, container, false);
        mapView = view.findViewById(R.id.osmMap);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        if (getArguments() != null) {
            locationName = getArguments().getString("location", "");
            title = getArguments().getString("title", "Internship");
        }

        if (!locationName.isEmpty()) {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            try {
                List<Address> results = geocoder.getFromLocationName(locationName + ", Vietnam", 1);
                if (!results.isEmpty()) {
                    Address address = results.get(0);
                    GeoPoint point = new GeoPoint(address.getLatitude(), address.getLongitude());
                    mapView.getController().setZoom(14.0);
                    mapView.getController().setCenter(point);

                    Marker marker = new Marker(mapView);
                    marker.setPosition(point);
                    marker.setTitle(title);
                    mapView.getOverlays().add(marker);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
