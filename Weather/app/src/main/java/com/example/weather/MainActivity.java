package com.example.weather;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.Adapter.ForecastAdapter;
import com.example.weather.Adapter.PlacesAdapter;
import com.example.weather.Models.Sys;
import com.example.weather.Receiver.WeatherNotifyReceiver;
import com.example.weather.Utils.SearchHistoryManager;
import com.example.weather.Utils.WeatherUIComponents;
import com.example.weather.Utils.WeatherUtils;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout layout;
    private RecyclerView resLocation;
    private PlacesAdapter placesAdapter;
    private SearchHistoryManager searchHistoryManager;
    SearchView searchView;
    private WeatherUIComponents ui;
    private String placeName;

//    private ForecastAdapter forecastAdapter;
//    private RecyclerView resForecast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ui = new WeatherUIComponents(
                findViewById(R.id.tv_temp),
                findViewById(R.id.humidity),
                findViewById(R.id.wind),
                findViewById(R.id.sunrise),
                findViewById(R.id.sunset),
                findViewById(R.id.sea),
                findViewById(R.id.conditions),
                findViewById(R.id.max_temp),
                findViewById(R.id.min_temp),
                findViewById(R.id.weather),
                findViewById(R.id.day),
                findViewById(R.id.date),
                findViewById(R.id.tv_location),
                findViewById(R.id.lottieAnimationView),
                findViewById(R.id.main)
        );
        String incomingPlace = getIntent().getStringExtra("place_name");
        if (incomingPlace != null && !incomingPlace.isEmpty()) {
            placeName = incomingPlace;
        }else{
            placeName = "Nam Dinh";
        }
        WeatherUtils.callApiUI(this, placeName, ui);
        setDailyAlarm();
        searchView = findViewById(R.id.search_view);
        layout = findViewById(R.id.drawer_layout);
        searchLocation();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);

        resLocation = headerView.findViewById(R.id.res_location);
        resLocation.setLayoutManager(new LinearLayoutManager(this));

        searchHistoryManager = new SearchHistoryManager(this);
        placesAdapter = new PlacesAdapter(searchHistoryManager.getSearchHistory());
        resLocation.setAdapter(placesAdapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        resLocation.addItemDecoration(itemDecoration);

        placesAdapter.setRecyclerViewInterface((place) -> {
//            Log.d("PLACES", "Clicked place: " + place);
            WeatherUtils.callApiUI(this, place, ui);
            layout.closeDrawer(GravityCompat.START);
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        searchHistoryManager.clearItems(position);
                        placesAdapter.updateData(searchHistoryManager.getSearchHistory());
                    }
                });
        itemTouchHelper.attachToRecyclerView(resLocation);


    }

    private void searchLocation() {
      searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
          @Override
          public boolean onQueryTextSubmit(String query) {
              searchHistoryManager.saveSearchQuery(query);
              placesAdapter.updateData(searchHistoryManager.getSearchHistory());
              placeName = query;
              WeatherUtils.callApiUI(MainActivity.this, placeName, ui);
              setDailyAlarm();
              return true;
          }

          @Override
          public boolean onQueryTextChange(String newText) {
              return true;
          }
      });
    }

    public void clickMenu(View view) {
        layout.openDrawer(GravityCompat.START);
    }

    private List<Calendar> randomTimes(){
        HashSet<String> usedTimes = new HashSet<>();
        List<Calendar> times = new ArrayList<>();
        Random random = new Random();
       while (times.size() < 3){
            int hour = random.nextInt(24);
            int minute = random.nextInt(60);

            String timeKey = hour + ":" + minute;
            if(!usedTimes.contains(timeKey)){
                usedTimes.add(timeKey);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                }
                times.add(calendar);
            }
        }
        return  times;
    }
    private void setDailyAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                return;
            }
        }

        List<Calendar> times = randomTimes();

        for(int i = 0; i < times.size(); i++){
            Calendar calendar = times.get(i);
            Log.d("CALENDER", "notification" + calendar.getTime());

            Intent intent = new Intent(this, WeatherNotifyReceiver.class);
            intent.putExtra("place_name", placeName);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(!searchView.isIconified()){
            searchView.setIconified(true);
            return;
        }
        if(layout.isDrawerOpen(GravityCompat.START)) {
            layout.closeDrawer(GravityCompat.START);
        }
        super.onBackPressed();
    }
}