/**
 * RecipeAdapter handles the display of recipes in a RecyclerView.
 * It provides a customizable way to show recipe cards with images,
 * titles, descriptions, cooking times, and dietary tags.
 */
package com.example.recipe_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;
import java.text.DecimalFormat;

/**
 * Adapter for displaying recipe items in a RecyclerView.
 * Supports click handling for recipe selection and displays
 * recipe information in a card format.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private List<Recipe> recipes;
    private final OnRecipeClickListener listener;
    private final DecimalFormat ratingFormat = new DecimalFormat("#.#");

    /**
     * Interface for handling recipe click events
     */
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    /**
     * Constructor for RecipeAdapter
     * @param recipes List of recipes to display
     * @param listener Callback for recipe click events
     */
    public RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe, listener);
    }

    @Override
    public int getItemCount() {
        return recipes != null ? recipes.size() : 0;
    }

    /**
     * Updates the list of recipes and refreshes the display
     * @param recipes New list of recipes to display
     */
    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for recipe items
     * Handles the display of recipe information in the card layout
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView recipeImage;      // Recipe thumbnail
        private final TextView titleText;         // Recipe title
        private final TextView descriptionText;   // Recipe description
        private final TextView cookingTime;       // Cooking time
        private final TextView ratingText;        // Recipe rating
        private final Chip cuisineChip;           // Cuisine chip
        private final Chip difficultyChip;        // Difficulty chip

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            cookingTime = itemView.findViewById(R.id.cookingTime);
            ratingText = itemView.findViewById(R.id.ratingText);
            cuisineChip = itemView.findViewById(R.id.cuisineChip);
            difficultyChip = itemView.findViewById(R.id.difficultyChip);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onRecipeClick(recipes.get(position));
                }
            });
        }

        /**
         * Binds recipe data to the view holder
         * @param recipe Recipe to display
         */
        public void bind(Recipe recipe, OnRecipeClickListener listener) {
            titleText.setText(recipe.getName());
            descriptionText.setText(recipe.getDescription());
            cookingTime.setText(recipe.getCookingTimeDisplay());
            
            // Load image using Glide
            if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
                Glide.with(recipeImage.getContext())
                    .load(recipe.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .centerCrop()
                    .into(recipeImage);
            } else {
                recipeImage.setImageResource(R.drawable.placeholder_image);
            }

            // Set rating
            ratingText.setText(ratingFormat.format(recipe.getRating()) + "★");

            // Set cuisine chip if available
            if (recipe.getCuisine() != null && !recipe.getCuisine().isEmpty()) {
                cuisineChip.setVisibility(View.VISIBLE);
                cuisineChip.setText(recipe.getCuisine());
            } else {
                cuisineChip.setVisibility(View.GONE);
            }

            // Set difficulty chip if available
            if (recipe.getDifficulty() != null && !recipe.getDifficulty().isEmpty()) {
                difficultyChip.setVisibility(View.VISIBLE);
                difficultyChip.setText(recipe.getDifficulty());
                
                // Set chip color based on difficulty
                int colorRes;
                switch (recipe.getDifficulty().toLowerCase()) {
                    case "easy":
                        colorRes = R.color.difficulty_easy;
                        break;
                    case "medium":
                        colorRes = R.color.difficulty_medium;
                        break;
                    case "hard":
                        colorRes = R.color.difficulty_hard;
                        break;
                    default:
                        colorRes = R.color.difficulty_medium;
                }
                difficultyChip.setChipBackgroundColorResource(colorRes);
            } else {
                difficultyChip.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRecipeClick(recipe);
                }
            });
        }
    }
}