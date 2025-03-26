package com.example.recipe_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import androidx.appcompat.widget.SearchView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.core.app.ShareCompat;
import android.net.Uri;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;
import android.util.Log;

public class RecipeRecommendationActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {
    private RecyclerView recipeRecyclerView;
    private RecipeAdapter recipeAdapter;
    private ProgressBar progressBar;
    private List<Recipe> allRecipes;
    private List<Recipe> filteredRecipes;
    private ChipGroup cuisineFilterChips;
    private ChipGroup difficultyFilterChips;
    private SearchView searchView;
    private Spinner sortSpinner;
    private String currentCuisineFilter = "";
    private String currentDifficultyFilter = "";
    private String currentSearchQuery = "";
    private ChipGroup categoryFilterChips;
    private SeekBar cookingTimeSeekBar;
    private TextView cookingTimeText;
    private final int maxCookingTime = 120; // 2 hours
    private String currentCategory = "";
    private int currentMaxCookingTime = maxCookingTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_recommendation);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Recipe Recommendations");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        initializeViews();

        // Initialize lists
        allRecipes = new ArrayList<>();
        filteredRecipes = new ArrayList<>();

        // Set up RecyclerView and adapter
        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipeAdapter = new RecipeAdapter(filteredRecipes, this);
        recipeRecyclerView.setAdapter(recipeAdapter);

        // Setup UI components
        setupRecipeList();
        setupSearchView();
        setupFilterChips();
        setupSortSpinner();
        setupCategoryFilter();
        setupCookingTimeFilter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the recipe list when returning to this activity
        filterRecipes();
    }

    private void initializeViews() {
        recipeRecyclerView = findViewById(R.id.recipeRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        cuisineFilterChips = findViewById(R.id.cuisineFilterChips);
        difficultyFilterChips = findViewById(R.id.difficultyFilterChips);
        searchView = findViewById(R.id.searchView);
        sortSpinner = findViewById(R.id.sortSpinner);
        categoryFilterChips = findViewById(R.id.categoryFilterChips);
        cookingTimeSeekBar = findViewById(R.id.cookingTimeSeekBar);
        cookingTimeText = findViewById(R.id.cookingTimeText);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchQuery = newText.toLowerCase();
                filterRecipes();
                return true;
            }
        });
    }

    private void setupFilterChips() {
        // Setup cuisine filter chips
        String[] cuisines = {"All", "Italian", "Indian", "Thai", "Japanese", "Mediterranean", "American"};
        for (String cuisine : cuisines) {
            Chip chip = new Chip(this);
            chip.setText(cuisine);
            chip.setCheckable(true);
            chip.setChecked(cuisine.equals("All"));
            cuisineFilterChips.addView(chip);
        }

        cuisineFilterChips.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = group.findViewById(checkedId);
            currentCuisineFilter = chip != null ? (chip.getText().toString().equals("All") ? "" : chip.getText().toString()) : "";
            filterRecipes();
        });

        // Setup difficulty filter chips
        String[] difficulties = {"All", "Easy", "Medium", "Hard"};
        for (String difficulty : difficulties) {
            Chip chip = new Chip(this);
            chip.setText(difficulty);
            chip.setCheckable(true);
            chip.setChecked(difficulty.equals("All"));
            difficultyFilterChips.addView(chip);
        }

        difficultyFilterChips.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = group.findViewById(checkedId);
            currentDifficultyFilter = chip != null ? (chip.getText().toString().equals("All") ? "" : chip.getText().toString()) : "";
            filterRecipes();
        });
    }

    private void setupSortSpinner() {
        String[] sortOptions = {"Default", "Rating (High to Low)", "Time (Low to High)", "Name (A to Z)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortRecipes(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupCategoryFilter() {
        String[] categories = {"All", "Breakfast", "Lunch", "Dinner", "Dessert", "Snacks", "Appetizers"};
        for (String category : categories) {
            Chip chip = new Chip(this);
            chip.setText(category);
            chip.setCheckable(true);
            chip.setChecked(category.equals("All"));
            categoryFilterChips.addView(chip);
        }

        categoryFilterChips.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = group.findViewById(checkedId);
            currentCategory = chip != null ? (chip.getText().toString().equals("All") ? "" : chip.getText().toString()) : "";
            filterRecipes();
        });
    }

    private void setupCookingTimeFilter() {
        cookingTimeSeekBar.setMax(maxCookingTime);
        cookingTimeSeekBar.setProgress(maxCookingTime);
        updateCookingTimeText(maxCookingTime);

        cookingTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentMaxCookingTime = progress;
                updateCookingTimeText(progress);
                if (fromUser) {
                    filterRecipes();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateCookingTimeText(int minutes) {
        if (minutes >= 60) {
            int hours = minutes / 60;
            int mins = minutes % 60;
            cookingTimeText.setText(String.format("Max cooking time: %dh %dm", hours, mins));
        } else {
            cookingTimeText.setText(String.format("Max cooking time: %dm", minutes));
        }
    }

    private void filterRecipes() {
        filteredRecipes = allRecipes.stream()
            .filter(recipe -> currentCuisineFilter.isEmpty() || 
                    (recipe.getCuisine() != null && recipe.getCuisine().equals(currentCuisineFilter)))
            .filter(recipe -> currentDifficultyFilter.isEmpty() || 
                    (recipe.getDifficulty() != null && recipe.getDifficulty().equals(currentDifficultyFilter)))
            .filter(recipe -> currentCategory.isEmpty() ||
                    (recipe.getCategory() != null && recipe.getCategory().equals(currentCategory)))
            .filter(recipe -> (recipe.getPrepTime() + recipe.getCookTime()) <= currentMaxCookingTime)
            .filter(recipe -> currentSearchQuery.isEmpty() || 
                    recipe.getName().toLowerCase().contains(currentSearchQuery) ||
                    recipe.getDescription().toLowerCase().contains(currentSearchQuery) ||
                    (recipe.getCuisine() != null && recipe.getCuisine().toLowerCase().contains(currentSearchQuery)))
            .collect(Collectors.toList());

        sortRecipes(sortSpinner.getSelectedItemPosition());
    }

    private void sortRecipes(int sortOption) {
        switch (sortOption) {
            case 1: // Rating (High to Low)
                filteredRecipes.sort((r1, r2) -> Double.compare(r2.getRating(), r1.getRating()));
                break;
            case 2: // Time (Low to High)
                filteredRecipes.sort(Comparator.comparingInt(r -> r.getPrepTime() + r.getCookTime()));
                break;
            case 3: // Name (A to Z)
                filteredRecipes.sort(Comparator.comparing(Recipe::getName));
                break;
        }
        recipeAdapter.setRecipes(filteredRecipes);
    }

    private void setupRecipeList() {
        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);

        // Initialize lists if not already initialized
        if (allRecipes == null) allRecipes = new ArrayList<>();
        if (filteredRecipes == null) filteredRecipes = new ArrayList<>();

        // Load recipes from Firebase
        FirebaseManager.getInstance().getAllRecipes(new FirebaseManager.FirebaseCallback<List<Recipe>>() {
            @Override
            public void onSuccess(List<Recipe> recipes) {
                // Hide loading indicator
                progressBar.setVisibility(View.GONE);

                if (recipes.isEmpty()) {
                    // If no recipes exist, add sample recipes
                    addSampleRecipes();
                } else {
                    // Update the recipes list
                    allRecipes.clear();
                    allRecipes.addAll(recipes);
                    filterRecipes();
                    
                    // Update adapter
                    if (recipeAdapter != null) {
                        recipeAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Hide loading indicator
                progressBar.setVisibility(View.GONE);
                
                // Show error message
                Toast.makeText(RecipeRecommendationActivity.this, 
                    "Error loading recipes: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
                
                // Load sample recipes as fallback
                addSampleRecipes();
            }
        });
    }

    private void addSampleRecipes() {
        List<Recipe> sampleRecipes = new ArrayList<>();

        // Spaghetti Carbonara
        Recipe carbonara = new Recipe(
            "1",
            "Spaghetti Carbonara",
            "A classic Italian pasta dish with eggs, cheese, pancetta, and black pepper",
            15,
            20,
            "https://firebasestorage.googleapis.com/v0/b/recipe-app-c5aad.appspot.com/o/recipes%2Fcarbonara.jpg?alt=media",
            Arrays.asList(
                "400g spaghetti",
                "200g pancetta or guanciale",
                "4 large eggs",
                "100g Pecorino Romano cheese",
                "100g Parmigiano Reggiano",
                "Black pepper",
                "Salt"
            )
        );
        carbonara.setInstructions(Arrays.asList(
            "Bring a large pot of salted water to boil",
            "Cook spaghetti according to package instructions",
            "Meanwhile, cook diced pancetta until crispy",
            "Beat eggs with grated cheese and pepper",
            "Drain pasta, reserving some pasta water",
            "Mix hot pasta with egg mixture and pancetta",
            "Add pasta water if needed for creaminess",
            "Serve immediately with extra cheese and pepper"
        ));
        carbonara.setCuisine("Italian");
        carbonara.setCategory("Dinner");
        carbonara.setDifficulty("Medium");
        carbonara.setServings(4);
        carbonara.setDietaryTags(Arrays.asList("Italian", "Pasta"));
        sampleRecipes.add(carbonara);

        // Chicken Stir Fry
        Recipe stirFry = new Recipe(
            "2",
            "Chicken Stir Fry",
            "Quick and healthy Asian stir fry with tender chicken and crisp vegetables",
            15,
            20,
            "https://firebasestorage.googleapis.com/v0/b/recipe-app-c5aad.appspot.com/o/recipes%2Fstirfry.jpg?alt=media",
            Arrays.asList(
                "500g chicken breast, sliced",
                "2 cups mixed vegetables",
                "3 cloves garlic, minced",
                "1 inch ginger, grated",
                "3 tbsp soy sauce",
                "2 tbsp vegetable oil",
                "Salt and pepper to taste"
            )
        );
        stirFry.setInstructions(Arrays.asList(
            "Slice chicken into thin strips",
            "Heat oil in a large wok or pan",
            "Add garlic and ginger, stir-fry until fragrant",
            "Add chicken and cook until golden",
            "Add vegetables and stir-fry until crisp-tender",
            "Add soy sauce and seasonings",
            "Cook for 2-3 more minutes",
            "Serve hot with rice"
        ));
        stirFry.setCuisine("Asian");
        stirFry.setCategory("Dinner");
        stirFry.setDifficulty("Easy");
        stirFry.setServings(4);
        stirFry.setDietaryTags(Arrays.asList("Asian", "Healthy", "Quick"));
        sampleRecipes.add(stirFry);

        // Chocolate Chip Cookies
        Recipe cookies = new Recipe(
            "3",
            "Classic Chocolate Chip Cookies",
            "Soft and chewy chocolate chip cookies with crispy edges",
            20,
            12,
            "https://firebasestorage.googleapis.com/v0/b/recipe-app-c5aad.appspot.com/o/recipes%2Fcookies.jpg?alt=media",
            Arrays.asList(
                "2 1/4 cups all-purpose flour",
                "1 cup butter, softened",
                "3/4 cup sugar",
                "3/4 cup brown sugar",
                "2 large eggs",
                "2 cups chocolate chips",
                "1 tsp vanilla extract",
                "1 tsp baking soda",
                "1/2 tsp salt"
            )
        );
        cookies.setInstructions(Arrays.asList(
            "Preheat oven to 375°F (190°C)",
            "Cream together butter and sugars",
            "Beat in eggs and vanilla",
            "Mix in flour, baking soda, and salt",
            "Stir in chocolate chips",
            "Drop rounded tablespoons onto baking sheets",
            "Bake for 10-12 minutes until golden",
            "Cool on wire racks"
        ));
        cookies.setCuisine("American");
        cookies.setCategory("Dessert");
        cookies.setDifficulty("Easy");
        cookies.setServings(24);
        cookies.setDietaryTags(Arrays.asList("Dessert", "Baking"));
        sampleRecipes.add(cookies);

        // Save sample recipes to Firebase
        final int[] savedCount = {0};
        for (Recipe recipe : sampleRecipes) {
            FirebaseManager.getInstance().saveRecipe(recipe, new FirebaseManager.FirebaseCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.d("RecipeRecommendation", "Sample recipe saved: " + recipe.getName());
                    savedCount[0]++;
                    if (savedCount[0] == sampleRecipes.size()) {
                        // All recipes saved, update UI
                        allRecipes.clear();
                        allRecipes.addAll(sampleRecipes);
                        filterRecipes();
                        if (recipeAdapter != null) {
                            recipeAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("RecipeRecommendation", "Error saving sample recipe: " + e.getMessage());
                    savedCount[0]++;
                    if (savedCount[0] == sampleRecipes.size()) {
                        // Even if some failed, update UI with what we have
                        allRecipes.clear();
                        allRecipes.addAll(sampleRecipes);
                        filterRecipes();
                        if (recipeAdapter != null) {
                            recipeAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }

    private void shareRecipe(Recipe recipe) {
        String shareText = String.format(
            "%s\n\nCooking Time: %d mins\nDifficulty: %s\n\nIngredients:\n%s\n\nCheck out this delicious recipe in our app!",
            recipe.getName(),
            recipe.getPrepTime() + recipe.getCookTime(),
            recipe.getDifficulty(),
            String.join("\n", recipe.getIngredients())
        );

        ShareCompat.IntentBuilder.from(this)
            .setType("text/plain")
            .setSubject("Check out this recipe!")
            .setText(shareText)
            .startChooser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_recommendation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // Show exit confirmation only if this is the root activity
            if (isTaskRoot()) {
                showExitDialog();
            } else {
                super.onBackPressed();
            }
            return true;
        } else if (id == R.id.action_favorites) {
            startActivity(new Intent(this, FavouritesActivity.class));
            return true;
        } else if (id == R.id.action_history) {
            startActivity(new Intent(this, HistoryActivity.class));
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

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            showExitDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void showExitDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes", (dialog, which) -> {
                finish();
            })
            .setNegativeButton("No", null)
            .show();
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        // Add to history
        FirebaseManager.getInstance().addToHistory(recipe.getId(), new FirebaseManager.FirebaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d("RecipeRecommendation", "Added to history: " + recipe.getName());
                // Start detail activity after successfully adding to history
                Intent intent = RecipeDetailActivity.newIntent(RecipeRecommendationActivity.this, recipe.getId());
                startActivity(intent);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("RecipeRecommendation", "Error adding to history: " + e.getMessage());
                // Still start detail activity even if adding to history fails
                Intent intent = RecipeDetailActivity.newIntent(RecipeRecommendationActivity.this, recipe.getId());
                startActivity(intent);
            }
        });
    }
}