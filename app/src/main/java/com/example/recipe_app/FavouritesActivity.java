/**
 * FavouritesActivity displays the user's favorite recipes.
 * It shows a list of recipes that have been marked as favorites,
 * with options to view recipe details or remove from favorites.
 */
package com.example.recipe_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavouritesActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {
    // UI Components
    private RecyclerView favouritesRecyclerView;  // List of favorite recipes
    private ProgressBar progressBar;              // Loading indicator
    private TextView emptyView;                   // Empty state message
    
    // Data
    private RecipeAdapter recipeAdapter;          // Adapter for favorites list
    private List<Recipe> favouritesList;          // List of favorite recipes
    private static final String PREFS_NAME = "FavouritesPrefs";
    private static final String FAVOURITES_KEY = "favourites";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        // Initialize views
        initializeViews();

        // Setup toolbar
        setupToolbar();

        // Load favorites from Firebase
        loadFavorites();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites(); // Reload favorites when returning to this screen
    }

    /**
     * Initializes view references from layout
     */
    private void initializeViews() {
        favouritesRecyclerView = findViewById(R.id.favouritesRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
    }

    /**
     * Sets up the RecyclerView with adapter and layout manager
     */
    private void setupRecyclerView() {
        favouritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipeAdapter = new RecipeAdapter(favouritesList, this);
        favouritesRecyclerView.setAdapter(recipeAdapter);
    }

    /**
     * Loads favorite recipes from data source
     */
    private void loadFavorites() {
        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);
        
        // Load favorites from Firebase
        FirebaseManager.getInstance().getFavorites(new FirebaseManager.FirebaseCallback<List<Recipe>>() {
            @Override
            public void onSuccess(List<Recipe> recipes) {
                // Hide loading indicator
                progressBar.setVisibility(View.GONE);
                
                if (recipes.isEmpty()) {
                    // Show empty state
                    emptyView.setVisibility(View.VISIBLE);
                    favouritesRecyclerView.setVisibility(View.GONE);
                } else {
                    // Show recipes
                    emptyView.setVisibility(View.GONE);
                    favouritesRecyclerView.setVisibility(View.VISIBLE);
                    
                    // Update adapter
                    favouritesList.clear();
                    favouritesList.addAll(recipes);
                    recipeAdapter.notifyDataSetChanged();
                }
            }
            
            @Override
            public void onFailure(Exception e) {
                // Hide loading indicator
                progressBar.setVisibility(View.GONE);
                
                // Show error message
                Toast.makeText(FavouritesActivity.this, 
                    "Error loading favorites: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                
                // Show empty state
                emptyView.setVisibility(View.VISIBLE);
                favouritesRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Handles recipe click events from favorites list
     * @param recipe The recipe that was clicked
     */
    @Override
    public void onRecipeClick(Recipe recipe) {
        // Add to history
        FirebaseManager.getInstance().addToHistory(recipe.getId(), new FirebaseManager.FirebaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d("Favorites", "Added to history: " + recipe.getName());
            }
            
            @Override
            public void onFailure(Exception e) {
                Log.e("Favorites", "Error adding to history: " + e.getMessage());
            }
        });
        
        // Open recipe details
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("recipe_id", recipe.getId());
        intent.putExtra("recipe_name", recipe.getName());
        intent.putExtra("recipe_description", recipe.getDescription());
        intent.putExtra("recipe_image_url", recipe.getImageUrl());
        intent.putExtra("recipe_prep_time", recipe.getPrepTime());
        intent.putExtra("recipe_cook_time", recipe.getCookTime());
        intent.putExtra("recipe_servings", recipe.getServings());
        intent.putExtra("recipe_difficulty", recipe.getDifficulty());
        intent.putExtra("recipe_cuisine", recipe.getCuisine());
        intent.putExtra("recipe_category", recipe.getCategory());
        intent.putStringArrayListExtra("recipe_ingredients", new ArrayList<>(recipe.getIngredients()));
        intent.putStringArrayListExtra("recipe_instructions", new ArrayList<>(recipe.getInstructions()));
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets up the toolbar with a back button
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Favourites");
        }
    }
}