package com.example.weather;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.weather.Utils.SearchHistoryManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    private SearchHistoryManager searchHistoryManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                checkGPSStatus();
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    private void checkGPSStatus() {
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
            client.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if(location != null){
                            double lat = 21.0167;
                            double lng = 105.5333;
//                            double lat = location.getLatitude();
//                            double lng = location.getLongitude();
                            callApiWithLatAndLng(lat, lng);
                        }
                    });
        }else if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This app requires GPS functionality to accurately update the current weather at your location.")
                    .setTitle("Permission required")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(SplashActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()));
            builder.show();
        }else{
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                } else {
                    checkGPSStatus();
                }
            }
        }
    }

    public void callApiWithLatAndLng(double lat, double lng){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        searchHistoryManager = new SearchHistoryManager(this);
        try{
            List<Address> address = geocoder.getFromLocation(lat,  lng, 1);
            Log.d("DEBUG_LOCATION", "Latitude: " + lat + ", Longitude: " + lng);
            if(address != null && !address.isEmpty()){
                String name = address.get(0).getAdminArea();
                searchHistoryManager.saveSearchQuery(name);
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra("place_name", name);
                startActivity(intent);
                finish();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}