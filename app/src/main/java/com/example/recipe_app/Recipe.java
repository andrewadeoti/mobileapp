/**
 * Recipe Class - The Core of Our Recipe App
 * 
 * This class is like a digital recipe card that stores all the information
 * needed to make a delicious dish. It keeps track of:
 * - Basic recipe info (name, description, cooking time)
 * - Ingredients needed
 * - Step-by-step instructions
 * - How hard it is to make
 * - What type of food it is
 * - How many people it serves
 * - How good it tastes (rating)
 */
package com.example.recipe_app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Recipe implements Serializable {
    // Basic information about the recipe
    private String id;          // A unique number to identify this recipe
    private String name;        // What the recipe is called (e.g., "Chocolate Cake")
    private String description; // A short description of what the recipe is
    private int prepTime;       // How long it takes to prepare (in minutes)
    private int cookTime;       // How long it takes to cook (in minutes)
    private int servings;       // How many people this recipe will feed
    private String imageUrl;    // Where to find a picture of the finished dish
    
    // The actual recipe content
    private List<String> ingredients;  // List of all ingredients needed
    private List<String> instructions; // Step-by-step cooking instructions
    
    // How to categorize the recipe
    private String cuisine;     // What type of food it is (e.g., Italian, Chinese)
    private String difficulty;  // How hard it is to make (Easy, Medium, Hard)
    private String category;    // What kind of dish it is (Main Course, Dessert, etc.)
    
    // How good the recipe is
    private double rating;      // How good people think it is (0.0 to 5.0)
    private boolean isFavorite; // Whether the user likes this recipe
    private String userId;      // Who created this recipe
    private List<String> dietaryTags; // Dietary information (e.g., Vegetarian, Gluten-Free)

    /**
     * Creates a new recipe with basic information
     */
    public Recipe(String name, String description, String imageUrl) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.ingredients = new ArrayList<>();
        this.instructions = new ArrayList<>();
        this.dietaryTags = new ArrayList<>();
        this.rating = 0.0;
        this.servings = 4;
        this.difficulty = "Medium";
        this.isFavorite = false;
        this.userId = null;
    }

    /**
     * Creates a new recipe with all details
     */
    public Recipe(String id, String name, String description, int prepTime, int cookTime, String imageUrl, List<String> ingredients) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.imageUrl = imageUrl;
        this.ingredients = new ArrayList<>(ingredients);
        this.instructions = new ArrayList<>();
        this.dietaryTags = new ArrayList<>();
        this.rating = 0.0;
        this.servings = 4;
        this.difficulty = "Medium";
        this.isFavorite = false;
        this.userId = null;
    }

    // Simple getters and setters to access and change recipe information
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPrepTime() { return prepTime; }
    public void setPrepTime(int prepTime) { this.prepTime = prepTime; }

    public int getCookTime() { return cookTime; }
    public void setCookTime(int cookTime) { this.cookTime = cookTime; }

    public int getServings() { return servings; }
    public void setServings(int servings) { this.servings = servings; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public List<String> getInstructions() { return instructions; }
    public void setInstructions(List<String> instructions) { this.instructions = instructions; }

    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }

    /**
     * Figures out how hard the recipe is to make
     * 
     * If we haven't set a difficulty, it calculates one based on total cooking time:
     * - Less than 30 minutes = Easy
     * - 30-60 minutes = Medium
     * - More than 60 minutes = Hard
     */
    public String getDifficulty() {
        if (difficulty != null) {
            return difficulty;
        }
        // Calculate difficulty based on total time
        int totalTime = prepTime + cookTime;
        if (totalTime <= 30) return "Easy";
        if (totalTime <= 60) return "Medium";
        return "Hard";
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { this.isFavorite = favorite; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<String> getDietaryTags() { return dietaryTags; }
    public void setDietaryTags(List<String> tags) { this.dietaryTags = new ArrayList<>(tags); }

    /**
     * Shows the total cooking time in a nice format
     * 
     * If it takes more than an hour, shows hours and minutes
     * Otherwise just shows minutes
     */
    public String getCookingTimeDisplay() {
        int totalTime = prepTime + cookTime;
        if (totalTime >= 60) {
            int hours = totalTime / 60;
            int minutes = totalTime % 60;
            return hours + "h " + (minutes > 0 ? minutes + "m" : "");
        }
        return totalTime + " min";
    }

    /**
     * Creates a text version of the recipe that can be shared
     * 
     * This is like writing down the recipe to give to someone else.
     * It includes all the important information in a nice format.
     */
    public String getShareText() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n\n");
        sb.append("Category: ").append(category).append("\n");
        sb.append("Cuisine: ").append(cuisine).append("\n");
        sb.append("Cooking Time: ").append(getCookingTimeDisplay()).append("\n");
        sb.append("Difficulty: ").append(difficulty).append("\n\n");
        
        // Add all ingredients
        sb.append("Ingredients:\n");
        for (String ingredient : ingredients) {
            sb.append("- ").append(ingredient).append("\n");
        }
        
        // Add all instructions
        if (instructions != null && !instructions.isEmpty()) {
            sb.append("\nInstructions:\n");
            for (int i = 0; i < instructions.size(); i++) {
                sb.append(i + 1).append(". ").append(instructions.get(i)).append("\n");
            }
        }
        
        return sb.toString();
    }
}