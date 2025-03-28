/**
 * RecipeDetailActivity displays detailed information about a specific recipe.
 * It shows the recipe image, title, description, ingredients, instructions,
 * and provides options to save to favorites, share, and add ingredients to shopping list.
 */
package com.example.recipe_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class RecipeDetailActivity extends AppCompatActivity {
    // Constants
    private static final String EXTRA_RECIPE_ID = "recipe_id";

    // Data
    private String recipeId;
    private Recipe recipe;

    // UI Components
    private ImageView recipeImage;                  // Recipe header image
    private TextView recipeName;                    // Recipe title
    private TextView recipeDescription;             // Recipe description
    private ChipGroup dietaryTagsGroup;            // Dietary tags (e.g., vegetarian, gluten-free)
    private TextView prepTime;                      // Preparation time
    private TextView cookTime;                      // Cooking time
    private TextView servings;                      // Number of servings
    private RecyclerView ingredientsRecyclerView;   // List of ingredients
    private RecyclerView instructionsRecyclerView;  // List of cooking instructions
    private MaterialButton saveToFavoritesButton;   // Save to favorites button
    private MaterialButton shareButton;             // Share recipe button
    private View progressBar;                       // Loading indicator
    private TextView noIngredientsText;            // Text shown when no ingredients
    private TextView noInstructionsText;           // Text shown when no instructions
    private Recipe currentRecipe;                  // Current recipe being displayed
    
    // Adapters
    private IngredientAdapter ingredientAdapter;    // Adapter for ingredients list
    private InstructionAdapter instructionAdapter;  // Adapter for instructions list

    /**
     * Creates a new intent to start this activity
     * @param context The context to create the intent from
     * @param recipeId The ID of the recipe to display
     * @return Intent configured to start this activity
     */
    public static Intent newIntent(Context context, String recipeId) {
        Intent intent = new Intent(context, RecipeDetailActivity.class);
        intent.putExtra(EXTRA_RECIPE_ID, recipeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Initialize views
        initializeViews();

        // Setup toolbar
        setupToolbar();

        // Get and display recipe details
        displayRecipeDetails();
    }

    private void initializeViews() {
        try {
            recipeImage = findViewById(R.id.recipeImage);
            recipeName = findViewById(R.id.recipeName);
            recipeDescription = findViewById(R.id.recipeDescription);
            dietaryTagsGroup = findViewById(R.id.dietaryTagsGroup);
            prepTime = findViewById(R.id.prepTime);
            cookTime = findViewById(R.id.cookTime);
            servings = findViewById(R.id.servings);
            ingredientsRecyclerView = findViewById(R.id.ingredientsRecyclerView);
            instructionsRecyclerView = findViewById(R.id.instructionsRecyclerView);
            saveToFavoritesButton = findViewById(R.id.saveToFavoritesButton);
            shareButton = findViewById(R.id.shareButton);
            progressBar = findViewById(R.id.progressBar);
            noIngredientsText = findViewById(R.id.noIngredientsText);
            noInstructionsText = findViewById(R.id.noInstructionsText);

            // Set up RecyclerViews
            ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            instructionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } catch (Exception e) {
            Log.e("RecipeDetail", "Error initializing views: " + e.getMessage());
            showErrorAndFinish();
        }
    }

    private void setupToolbar() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Recipe Details");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } catch (Exception e) {
            Log.e("RecipeDetail", "Error setting up toolbar: " + e.getMessage());
        }
    }

    private void displayRecipeDetails() {
        String recipeId = getIntent().getStringExtra(EXTRA_RECIPE_ID);
        if (recipeId == null) {
            Toast.makeText(this, "Error: Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);

        // Load recipe from Firebase
        FirebaseManager.getInstance().getRecipe(recipeId, new FirebaseManager.FirebaseCallback<Recipe>() {
            @Override
            public void onSuccess(Recipe recipe) {
                // Hide loading indicator
                progressBar.setVisibility(View.GONE);

                if (recipe != null) {
                    currentRecipe = recipe;
                    
                    // Set basic information
                    recipeName.setText(recipe.getName());
                    recipeDescription.setText(recipe.getDescription());
                    prepTime.setText(String.format("Prep: %d min", recipe.getPrepTime()));
                    cookTime.setText(String.format("Cook: %d min", recipe.getCookTime()));
                    servings.setText(String.format("Serves: %d", recipe.getServings()));

                    // Load image using Glide
                    if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
                        Glide.with(RecipeDetailActivity.this)
                            .load(recipe.getImageUrl())
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error_image)
                            .centerCrop()
                            .into(recipeImage);
                    }

                    // Set up ingredients
                    if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
                        ingredientAdapter = new IngredientAdapter(recipe.getIngredients());
                        ingredientsRecyclerView.setAdapter(ingredientAdapter);
                        ingredientsRecyclerView.setVisibility(View.VISIBLE);
                        noIngredientsText.setVisibility(View.GONE);
                    } else {
                        ingredientsRecyclerView.setVisibility(View.GONE);
                        noIngredientsText.setVisibility(View.VISIBLE);
                    }

                    // Set up instructions
                    if (recipe.getInstructions() != null && !recipe.getInstructions().isEmpty()) {
                        instructionAdapter = new InstructionAdapter(recipe.getInstructions());
                        instructionsRecyclerView.setAdapter(instructionAdapter);
                        instructionsRecyclerView.setVisibility(View.VISIBLE);
                        noInstructionsText.setVisibility(View.GONE);
                    } else {
                        instructionsRecyclerView.setVisibility(View.GONE);
                        noInstructionsText.setVisibility(View.VISIBLE);
                    }

                    // Set up dietary tags
                    if (recipe.getDietaryTags() != null && !recipe.getDietaryTags().isEmpty()) {
                        dietaryTagsGroup.removeAllViews();
                        for (String tag : recipe.getDietaryTags()) {
                            Chip chip = new Chip(RecipeDetailActivity.this);
                            chip.setText(tag);
                            chip.setClickable(false);
                            dietaryTagsGroup.addView(chip);
                        }
                        dietaryTagsGroup.setVisibility(View.VISIBLE);
                    } else {
                        dietaryTagsGroup.setVisibility(View.GONE);
                    }

                    // Update favorite button state
                    updateFavoriteButton(recipe.isFavorite());

                    // Setup button listeners
                    setupButtonListeners();

                    // Add to history
                    FirebaseManager.getInstance().addToHistory(recipe.getId(), new FirebaseManager.FirebaseCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Log.d("RecipeDetail", "Added to history: " + recipe.getName());
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("RecipeDetail", "Error adding to history: " + e.getMessage());
                        }
                    });
                } else {
                    showErrorAndFinish();
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Hide loading indicator
                progressBar.setVisibility(View.GONE);
                showErrorAndFinish();
            }
        });
    }

    private void setupButtonListeners() {
        saveToFavoritesButton.setOnClickListener(v -> toggleFavorite());
        shareButton.setOnClickListener(v -> shareRecipe());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showErrorAndFinish() {
        Toast.makeText(this, "Error loading recipe", Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * Loads recipe details from data source and updates UI
     */
    private void loadRecipeDetails() {
        String recipeId = getIntent().getStringExtra("RECIPE_ID");
        switch (recipeId) {
            case "1":
                recipe = new Recipe(
                    "1",                   // id
                    "Spaghetti Carbonara", // name
                    "Classic Italian pasta dish with eggs, cheese, pancetta, and black pepper", // description
                    15,                    // prepTime (int)
                    20,                    // cookTime (int)
                    "https://example.com/carbonara.jpg", // imageUrl
                    Arrays.asList(         // ingredients
                        "400g spaghetti",
                        "200g pancetta or guanciale",
                        "4 large eggs",
                        "100g Pecorino Romano cheese",
                        "100g Parmigiano Reggiano",
                        "Black pepper",
                        "Salt"
                    )
                );
                recipe.setServings(4);     // int value for servings
                recipe.setDietaryTags(Arrays.asList("Italian", "Pasta"));
                recipe.setInstructions(Arrays.asList(
                    "Bring a large pot of salted water to boil",
                    "Cook spaghetti according to package instructions",
                    "Meanwhile, cook diced pancetta until crispy",
                    "Beat eggs with grated cheese and pepper",
                    "Drain pasta, reserving some pasta water",
                    "Mix hot pasta with egg mixture and pancetta",
                    "Add pasta water if needed for creaminess",
                    "Serve immediately with extra cheese and pepper"
                ));
                break;

            case "2":
                recipe = new Recipe(
                    "2",                   // id
                    "Chicken Stir Fry",    // name
                    "Quick and healthy Asian dish with tender chicken and crisp vegetables", // description
                    15,                    // prepTime (int)
                    10,                    // cookTime (int)
                    "https://example.com/stirfry.jpg", // imageUrl
                    Arrays.asList(         // ingredients
                        "500g chicken breast, sliced",
                        "Mixed vegetables",
                        "3 tbsp soy sauce",
                        "2 cloves garlic, minced",
                        "1 inch ginger, grated",
                        "2 tbsp vegetable oil"
                    )
                );
                recipe.setServings(4);     // int value for servings
                recipe.setDietaryTags(Arrays.asList("Asian", "Quick", "Healthy"));
                recipe.setInstructions(Arrays.asList(
                    "Slice chicken and vegetables",
                    "Heat oil in a wok or large pan",
                    "Stir-fry chicken until golden",
                    "Add vegetables and stir-fry",
                    "Add sauce and seasonings",
                    "Cook until vegetables are crisp-tender",
                    "Serve hot with rice"
                ));
                break;

            case "3":
                recipe = new Recipe(
                    "3",                   // id
                    "Vegetable Curry",     // name
                    "Spicy Indian curry with aromatic spices and fresh vegetables", // description
                    20,                    // prepTime (int)
                    30,                    // cookTime (int)
                    "https://example.com/curry.jpg", // imageUrl
                    Arrays.asList(         // ingredients
                        "Mixed vegetables",
                        "2 tbsp curry powder",
                        "1 can coconut milk",
                        "1 onion, diced",
                        "3 cloves garlic, minced",
                        "Rice for serving"
                    )
                );
                recipe.setServings(4);     // int value for servings
                recipe.setDietaryTags(Arrays.asList("Indian", "Vegetarian", "Spicy"));
                recipe.setInstructions(Arrays.asList(
                    "Chop all vegetables",
                    "Sauté onion and garlic",
                    "Add curry powder and cook briefly",
                    "Add vegetables and coconut milk",
                    "Simmer until vegetables are tender",
                    "Season to taste",
                    "Serve with rice"
                ));
                break;

            case "4":
                recipe = new Recipe(
                    "4",                   // id
                    "Caesar Salad",        // name
                    "Fresh and crispy salad with homemade Caesar dressing", // description
                    15,                    // prepTime (int)
                    0,                     // cookTime (int)
                    "https://example.com/salad.jpg", // imageUrl
                    Arrays.asList(         // ingredients
                        "2 heads Romaine lettuce",
                        "Croutons",
                        "Parmesan cheese",
                        "Caesar dressing",
                        "Fresh black pepper"
                    )
                );
                recipe.setServings(4);     // int value for servings
                recipe.setDietaryTags(Arrays.asList("Salad", "Fresh"));
                recipe.setInstructions(Arrays.asList(
                    "Wash and chop lettuce",
                    "Make or prepare Caesar dressing",
                    "Toss lettuce with dressing",
                    "Add croutons and cheese",
                    "Season with black pepper",
                    "Serve immediately"
                ));
                break;


        }

        // Add recipe to history
        RecipeHistory history = new RecipeHistory(this);
        history.addToHistory(recipe);

        // Update UI
        updateUI();
    }

    /**
     * Updates UI with recipe details
     */
    private void updateUI() {
        if (recipe == null) return;

        // Set basic recipe information
            recipeName.setText(recipe.getName());
            recipeDescription.setText(recipe.getDescription());

        // Format time values as strings for display
        prepTime.setText(String.format("%d min", recipe.getPrepTime()));
        cookTime.setText(String.format("%d min", recipe.getCookTime()));
        servings.setText(String.format("%d servings", recipe.getServings()));

        // Load recipe image
        if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(recipe.getImageUrl())
                    .centerCrop()
                    .into(recipeImage);
        }

        // Update dietary tags
        updateDietaryTags();

        // Update lists
            ingredientAdapter.updateIngredients(recipe.getIngredients());
            instructionAdapter.updateInstructions(recipe.getInstructions());

        // Update favorite button state
        updateFavoriteButtonState();
    }

    /**
     * Updates the dietary tags display
     */
    private void updateDietaryTags() {
        dietaryTagsGroup.removeAllViews();
            for (String tag : recipe.getDietaryTags()) {
                Chip chip = new Chip(this);
                chip.setText(tag);
                dietaryTagsGroup.addView(chip);
        }
    }

    /**
     * Updates the favorite button state based on recipe status
     */
    private void updateFavoriteButtonState() {
        if (recipe != null) {
            saveToFavoritesButton.setIconResource(
                recipe.isFavorite() ? 
                R.drawable.ic_favorite_filled : 
                R.drawable.ic_favorite_border
            );
            saveToFavoritesButton.setText(
                recipe.isFavorite() ? 
                "Remove from Favorites" : 
                "Add to Favorites"
            );
        }
    }

    private void updateFavoriteButton(boolean isFavorite) {
        if (saveToFavoritesButton != null) {
            saveToFavoritesButton.setIconResource(
                isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border
            );
            saveToFavoritesButton.setText(
                isFavorite ? "Remove from Favorites" : "Add to Favorites"
            );
        }
    }

    private void toggleFavorite() {
        if (currentRecipe == null) return;

        // Disable button while processing
        saveToFavoritesButton.setEnabled(false);

        if (currentRecipe.isFavorite()) {
            // Remove from favorites
            FirebaseManager.getInstance().removeFavorite(currentRecipe.getId(), new FirebaseManager.FirebaseCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    currentRecipe.setFavorite(false);
                    updateFavoriteButton(false);
                    saveToFavoritesButton.setEnabled(true);
                    Toast.makeText(RecipeDetailActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception e) {
                    saveToFavoritesButton.setEnabled(true);
                    Toast.makeText(RecipeDetailActivity.this, "Error removing from favorites", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Add to favorites
            FirebaseManager.getInstance().addFavorite(currentRecipe.getId(), new FirebaseManager.FirebaseCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    currentRecipe.setFavorite(true);
                    updateFavoriteButton(true);
                    saveToFavoritesButton.setEnabled(true);
                    Toast.makeText(RecipeDetailActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception e) {
                    saveToFavoritesButton.setEnabled(true);
                    Toast.makeText(RecipeDetailActivity.this, "Error adding to favorites", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void shareRecipe() {
        if (currentRecipe == null) return;

        String shareText = String.format(
            "%s\n\nPrep Time: %d min\nCook Time: %d min\nServings: %d\n\nIngredients:\n%s\n\nInstructions:\n%s",
            currentRecipe.getName(),
            currentRecipe.getPrepTime(),
            currentRecipe.getCookTime(),
            currentRecipe.getServings(),
            String.join("\n", currentRecipe.getIngredients()),
            String.join("\n", currentRecipe.getInstructions())
        );

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this recipe!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share Recipe"));
    }
}