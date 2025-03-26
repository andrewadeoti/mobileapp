/**
 * HomeActivity serves as the main dashboard of the application.
 * It displays popular recipes and user's recipe history, with a search functionality
 * and tab-based navigation between different sections.
 */
package com.example.recipe_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements PopularRecipeAdapter.OnRecipeClickListener {
    // UI Components
    private RecyclerView popularRecipesRecyclerView;  // Displays list of popular recipes
    private RecyclerView historyRecyclerView;         // Displays user's recipe history
    private TextInputEditText searchInput;            // Search input field
    private TabLayout tabLayout;                      // Tab navigation
    private TextView seeAllHistory;                   // See all history button

    // Data
    private PopularRecipeAdapter popularRecipeAdapter;
    private RecipeAdapter recentRecipeAdapter;        // Added declaration
    private List<Recipe> popularRecipes;              // List of popular recipes
    private List<Recipe> historyRecipes;              // List of recently viewed recipes
    private String userName;                          // Current user's name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get user name from intent, or use default
        userName = getIntent().getStringExtra("USER_NAME");
        if (userName == null || userName.isEmpty()) {
            userName = "User";
        }

        // Initialize views and setup UI components
        initializeViews();
        setupToolbar();
        setupRecyclerViews();
        setupSearchInput();
        setupTabLayout();
        setupClickListeners();

        // Load initial data
        loadRecipes();
    }

    /**
     * Initializes all view references from the layout
     */
    private void initializeViews() {
        popularRecipesRecyclerView = findViewById(R.id.popularRecipesRecyclerView);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        searchInput = findViewById(R.id.searchInput);
        tabLayout = findViewById(R.id.tabLayout);
        seeAllHistory = findViewById(R.id.seeAllHistory);
    }

    /**
     * Sets up the toolbar with the app name
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
    }

    /**
     * Sets up click listeners for various UI components
     */
    private void setupClickListeners() {
        seeAllHistory.setOnClickListener(v -> {
            // Navigate to history screen
            Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Sets up RecyclerViews with their respective adapters and layouts
     */
    private void setupRecyclerViews() {
        popularRecipesRecyclerView.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        popularRecipeAdapter = new PopularRecipeAdapter(new ArrayList<>(), this);

        historyRecipes = new ArrayList<>();
        recentRecipeAdapter = new RecipeAdapter(historyRecipes, this::onRecipeClick);

        popularRecipesRecyclerView.setAdapter(popularRecipeAdapter);
        historyRecyclerView.setAdapter(recentRecipeAdapter);
    }

    /**
     * Configures the search input with text change listener
     */
    private void setupSearchInput() {
        searchInput.setOnClickListener(v -> {
            // Navigate to search screen
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(intent);
        });
        searchInput.setFocusable(false);
    }

    /**
     * Sets up the TabLayout for navigation between sections
     */
    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // Favorites
                        Intent favoritesIntent = new Intent(HomeActivity.this, FavouritesActivity.class);
                        startActivity(favoritesIntent);
                        break;
                    case 1: // Suggestions
                        // TODO: Implement suggestions screen
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    /**
     * Loads popular recipes from the data source
     */
    private void loadRecipes() {
        // Create sample recipes with correct parameter types
        popularRecipes = new ArrayList<>();
        
        // Add recipes with correct constructor parameters (String id, String name, String description, int prepTime, int cookTime, String imageUrl, List<String> ingredients)
        popularRecipes.add(new Recipe(
            "1",
            "Italian Pasta",
            "Classic Italian pasta with tomato sauce",
            20,  // prepTime in minutes
            30,  // cookTime in minutes
            "https://example.com/pasta.jpg",
            Arrays.asList("Pasta", "Tomato Sauce", "Garlic", "Basil")
        ));

        popularRecipes.add(new Recipe(
            "2",
            "Chicken Curry",
            "Spicy Indian chicken curry",
            25,
            45,
            "https://example.com/curry.jpg",
            Arrays.asList("Chicken", "Curry Sauce", "Onions", "Spices")
        ));

        popularRecipes.add(new Recipe(
            "3",
            "Caesar Salad",
            "Fresh and crispy Caesar salad",
            15,
            0,
            "https://example.com/salad.jpg",
            Arrays.asList("Lettuce", "Croutons", "Parmesan", "Caesar Dressing")
        ));

        // Set the recipes to adapters
        popularRecipeAdapter.setRecipes(popularRecipes);
        historyRecipes.addAll(popularRecipes);
        recentRecipeAdapter.setRecipes(historyRecipes);
    }

    /**
     * Handles recipe click events from the popular recipes list
     * @param recipe The recipe that was clicked
     */
    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("RECIPE_ID", recipe.getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            startActivity(new Intent(this, RecipeRecommendationActivity.class));
            return true;
        } else if (id == R.id.action_favorites) {
            startActivity(new Intent(this, FavouritesActivity.class));
            return true;
        } else if (id == R.id.action_shopping_list) {
            startActivity(new Intent(this, ShoppingListActivity.class));
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        } else if (id == R.id.action_contact) {
            startActivity(new Intent(this, ContactActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}