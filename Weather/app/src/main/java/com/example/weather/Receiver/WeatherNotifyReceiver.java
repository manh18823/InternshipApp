package com.example.weather.Receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.weather.MyApplication;
import com.example.weather.R;
import com.example.weather.Utils.WeatherUtils;

public class WeatherNotifyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String placeName = intent.getStringExtra("place_name");
        if(placeName != null && !placeName.isEmpty()){
            WeatherUtils.callApiReceiver(context, placeName, new WeatherUtils.WeatherCallback() {
                @Override
                public void onResult(String condition, String descriptionWeather) {
                    Notification notification = new NotificationCompat.Builder(context, MyApplication.CHANNEL_ID)
                            .setSmallIcon(R.drawable.clear_sky)
                            .setContentTitle(String.format("%s | %s", placeName, condition))
                            .setContentText(descriptionWeather)
                            .setColor(ContextCompat.getColor(context, R.color.icon))
                            .setAutoCancel(true)
                            .build();
                    NotificationManager manager = (NotificationManager)
                            context.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.notify(1, notification);
                    }
                }
            });
        }
    }
}
