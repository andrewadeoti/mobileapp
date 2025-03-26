/**
 * RecipeHistory - Remembers What Recipes You've Looked At
 * 
 * This class is like a diary that keeps track of all the recipes
 * you've viewed in the app. It helps:
 * - Show you recipes you looked at before
 * - Keep track of your favorite recipes
 * - Remember what you like to cook
 * 
 * It saves this information on your phone so you can find
 * your favorite recipes again easily.
 */
package com.example.recipe_app;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecipeHistory {
    // How many recipes to remember (maximum)
    private static final int MAX_HISTORY_SIZE = 50;
    
    // Where we save the history
    private static final String PREFS_NAME = "RecipeHistoryPrefs";
    private static final String HISTORY_KEY = "recipe_history";
    
    // Tools we use to save and load recipes
    private final SharedPreferences preferences;
    private final Gson gson;
    
    /**
     * Creates a new recipe history tracker
     * 
     * This sets up everything we need to remember which recipes
     * you've looked at.
     */
    public RecipeHistory(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }
    
    /**
     * Gets the list of recipes you've looked at
     * 
     * This is like opening your recipe diary to see what you've
     * cooked before.
     */
    public List<Recipe> getHistory() {
        String historyJson = preferences.getString(HISTORY_KEY, "");
        if (historyJson.isEmpty()) {
            return new ArrayList<>();
        }
        
        Type type = new TypeToken<List<Recipe>>(){}.getType();
        return gson.fromJson(historyJson, type);
    }
    
    /**
     * Adds a recipe to your history
     * 
     * This is like writing down a new recipe in your diary.
     * If you've already seen this recipe, it moves it to
     * the front of the list.
     */
    public void addToHistory(Recipe recipe) {
        List<Recipe> history = getHistory();
        
        // Remove the recipe if it's already in history
        history.removeIf(r -> r.getId().equals(recipe.getId()));
        
        // Add the recipe to the start of the list
        history.add(0, recipe);
        
        // Keep only the most recent recipes
        if (history.size() > MAX_HISTORY_SIZE) {
            history = history.subList(0, MAX_HISTORY_SIZE);
        }
        
        // Save the updated history
        saveHistory(history);
    }
    
    /**
     * Saves your recipe history
     * 
     * This is like closing your recipe diary after writing in it.
     */
    private void saveHistory(List<Recipe> history) {
        String historyJson = gson.toJson(history);
        preferences.edit().putString(HISTORY_KEY, historyJson).apply();
    }
    
    /**
     * Clears your recipe history
     * 
     * This is like starting a new recipe diary from scratch.
     */
    public void clearHistory() {
        preferences.edit().remove(HISTORY_KEY).apply();
    }
} 