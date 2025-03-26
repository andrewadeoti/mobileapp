package com.example.recipe_app;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecipeHistory {
    private static final String PREF_NAME = "RecipeHistoryPrefs";
    private static final String HISTORY_KEY = "recipe_history";
    private static final int MAX_HISTORY_SIZE = 50;

    private final SharedPreferences sharedPreferences;
    private List<Recipe> history;

    public RecipeHistory(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loadHistory();
    }

    private void loadHistory() {
        String jsonHistory = sharedPreferences.getString(HISTORY_KEY, "[]");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Recipe>>() {}.getType();
        history = gson.fromJson(jsonHistory, type);
        if (history == null) {
            history = new ArrayList<>();
        }
    }

    public void addToHistory(Recipe recipe) {
        // Remove if already exists to avoid duplicates
        history.removeIf(r -> r.getId().equals(recipe.getId()));
        
        // Add to beginning of list
        history.add(0, recipe);
        
        // Trim if exceeds max size
        if (history.size() > MAX_HISTORY_SIZE) {
            history = history.subList(0, MAX_HISTORY_SIZE);
        }
        
        // Save updated history
        saveHistory();
    }

    public List<Recipe> getHistory() {
        return new ArrayList<>(history);
    }

    public void clearHistory() {
        history.clear();
        saveHistory();
    }

    private void saveHistory() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonHistory = gson.toJson(history);
        editor.putString(HISTORY_KEY, jsonHistory);
        editor.apply();
    }
} 