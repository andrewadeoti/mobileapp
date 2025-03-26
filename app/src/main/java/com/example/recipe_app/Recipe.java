/**
 * Recipe class represents a recipe in the application.
 * It contains all the necessary information about a recipe including
 * basic details, ingredients, instructions, and metadata.
 */
package com.example.recipe_app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Recipe implements Serializable {
    private String id;
    private String name;
    private String description;
    private int prepTime;
    private int cookTime;
    private int servings;
    private String imageUrl;
    private List<String> ingredients;
    private List<String> instructions;
    private String cuisine;
    private String difficulty;
    private List<String> dietaryTags;
    private String category;
    private double rating;
    private boolean isFavorite;
    private String userId;

    public Recipe(String id, String name, String description, int prepTime, int cookTime, String imageUrl, List<String> ingredients) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.imageUrl = imageUrl;
        this.ingredients = ingredients;
        this.instructions = new ArrayList<>();
        this.dietaryTags = new ArrayList<>();
        this.servings = 4; // Default servings
        this.rating = 0.0;
        this.isFavorite = false;
        this.userId = null;
    }

    // Getters and setters
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

    public String getDifficulty() {
        if (difficulty != null) {
            return difficulty;
        }
        // Fallback to calculated difficulty if not set
        int totalTime = prepTime + cookTime;
        if (totalTime <= 30) return "Easy";
        if (totalTime <= 60) return "Medium";
        return "Hard";
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public List<String> getDietaryTags() { return dietaryTags; }
    public void setDietaryTags(List<String> dietaryTags) { this.dietaryTags = dietaryTags; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { this.isFavorite = favorite; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCookingTimeDisplay() {
        int totalTime = prepTime + cookTime;
        if (totalTime >= 60) {
            int hours = totalTime / 60;
            int minutes = totalTime % 60;
            return hours + "h " + (minutes > 0 ? minutes + "m" : "");
        }
        return totalTime + " min";
    }

    public String getShareText() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n\n");
        sb.append("Category: ").append(category).append("\n");
        sb.append("Cuisine: ").append(cuisine).append("\n");
        sb.append("Cooking Time: ").append(getCookingTimeDisplay()).append("\n");
        sb.append("Difficulty: ").append(difficulty).append("\n\n");
        
        sb.append("Ingredients:\n");
        for (String ingredient : ingredients) {
            sb.append("- ").append(ingredient).append("\n");
        }
        
        if (instructions != null && !instructions.isEmpty()) {
            sb.append("\nInstructions:\n");
            for (int i = 0; i < instructions.size(); i++) {
                sb.append(i + 1).append(". ").append(instructions.get(i)).append("\n");
            }
        }
        
        return sb.toString();
    }
}