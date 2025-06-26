package com.example.weather.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SearchHistoryManager {
    private static final String PREF_NAME = "search_history";
    private static final String KEY_HISTORY = "history_list";
    private SharedPreferences sharedPreferences;

    public SearchHistoryManager(Context context) {
        sharedPreferences= context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    public void saveSearchQuery(String query) {
        List<String> history = getSearchHistory();
        if (!history.contains(query)) {
            history.add(0, query);
        }
        if (history.size() > 10) {
            history = history.subList(0, 10);
        }
        sharedPreferences.edit().putString(KEY_HISTORY, new Gson().toJson(history)).apply();
    }
    public List<String> getSearchHistory() {
        String json = sharedPreferences.getString(KEY_HISTORY, "[]");
        Type type = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    public void clearItems(int position) {
        List<String> history = getSearchHistory();
        if (position >= 0 && position < history.size()) {
            history.remove(position);
            sharedPreferences.edit().putString(KEY_HISTORY, new Gson().toJson(history)).apply();
        }
    }
}
