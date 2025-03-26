/**
 * SearchActivity handles recipe search functionality.
 * It provides a search interface where users can search for recipes
 * and displays the results in a list format.
 */
package com.example.recipe_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements RecipeSearchAdapter.OnRecipeClickListener {
    // UI Components
    private EditText searchInput;           // Search input field
    private RecyclerView searchResults;     // List of search results
    private ProgressBar progressBar;        // Loading indicator
    private TextView noResultsText;         // Empty state text
    
    // Data
    private RecipeSearchAdapter adapter;    // Adapter for search results
    private List<Recipe> allRecipes;        // Complete list of recipes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize UI components
        initializeViews();
        setupRecyclerView();
        setupSearchListener();

        // Load initial data
        loadRecipes();
    }

    /**
     * Initializes view references from layout
     */
    private void initializeViews() {
        searchInput = findViewById(R.id.searchInput);
        searchResults = findViewById(R.id.searchResults);
        progressBar = findViewById(R.id.progressBar);
        noResultsText = findViewById(R.id.noResultsText);
    }

    /**
     * Sets up the RecyclerView with adapter and layout manager
     */
    private void setupRecyclerView() {
        searchResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeSearchAdapter(this);
        searchResults.setAdapter(adapter);
    }

    /**
     * Configures the search input text change listener
     */
    private void setupSearchListener() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                performSearch(s.toString());
            }
        });
    }

    /**
     * Loads all recipes from the data source
     */
    private void loadRecipes() {
        // TODO: Implement recipe loading from local storage
        allRecipes = new ArrayList<>();
        performSearch("");
    }

    /**
     * Performs search on recipes based on query
     * @param query The search query string
     */
    private void performSearch(String query) {
        progressBar.setVisibility(View.VISIBLE);
        noResultsText.setVisibility(View.GONE);

        // Filter recipes based on query
        List<Recipe> results = filterRecipes(query);
        
        // Update UI with results
        adapter.updateRecipes(results);
        progressBar.setVisibility(View.GONE);
        noResultsText.setVisibility(results.isEmpty() ? View.VISIBLE : View.GONE);
    }

    /**
     * Filters recipes based on search query
     * @param query The search query string
     * @return Filtered list of recipes
     */
    private List<Recipe> filterRecipes(String query) {
        List<Recipe> filteredList = new ArrayList<>();
        String lowercaseQuery = query.toLowerCase().trim();

        for (Recipe recipe : allRecipes) {
            if (recipe.getName().toLowerCase().contains(lowercaseQuery)) {
                filteredList.add(recipe);
            }
        }

        return filteredList;
    }

    /**
     * Handles recipe click events from search results
     * @param recipe The recipe that was clicked
     */
    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("RECIPE_ID", recipe.getId());
        startActivity(intent);
    }
}