/**
 * RecipeSearchAdapter handles the display of recipe search results.
 * It provides a specialized layout for search results with additional
 * information and filtering capabilities.
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

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying recipe search results in a RecyclerView.
 * Supports dynamic updates of search results and click handling
 * for recipe selection.
 */
public class RecipeSearchAdapter extends RecyclerView.Adapter<RecipeSearchAdapter.RecipeViewHolder> {
    private List<Recipe> recipes = new ArrayList<>();
    private final OnRecipeClickListener listener;

    /**
     * Interface for handling recipe click events in search results
     */
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    /**
     * Constructor for RecipeSearchAdapter
     * @param listener Callback for recipe click events
     */
    public RecipeSearchAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_search, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.bind(recipes.get(position));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    /**
     * Updates the list of recipes with search results
     * @param newRecipes New list of recipes to display
     */
    public void updateRecipes(List<Recipe> newRecipes) {
        this.recipes = newRecipes;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for search result items
     * Handles the display of recipe information in the search result layout
     */
    class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final ImageView recipeImage;       // Recipe thumbnail
        private final TextView recipeName;         // Recipe name
        private final TextView recipeDescription;  // Recipe description
        private final ChipGroup tagsGroup;        // Dietary tags
        private final TextView cookingTime;        // Cooking time

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            recipeName = itemView.findViewById(R.id.recipeName);
            recipeDescription = itemView.findViewById(R.id.recipeDescription);
            tagsGroup = itemView.findViewById(R.id.tagsGroup);
            cookingTime = itemView.findViewById(R.id.cookingTime);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onRecipeClick(recipes.get(position));
                }
            });
        }

        /**
         * Binds recipe data to the search result view
         * @param recipe Recipe to display
         */
        public void bind(Recipe recipe) {
            recipeName.setText(recipe.getName());
            recipeDescription.setText(recipe.getDescription());
            cookingTime.setText(recipe.getCookingTimeDisplay());
            
            // Load recipe image with placeholder and error handling
            Glide.with(itemView.getContext())
                    .load(recipe.getImageUrl())
                    .placeholder(R.drawable.placeholder_recipe)
                    .error(R.drawable.error_recipe)
                    .into(recipeImage);

            // Clear existing chips
            tagsGroup.removeAllViews();

            // Add dietary tags with custom styling
            List<String> dietaryTags = recipe.getDietaryTags();
            if (dietaryTags != null) {
                for (String tag : dietaryTags) {
                    Chip chip = new Chip(tagsGroup.getContext());
                    chip.setText(tag);
                    chip.setChipBackgroundColorResource(R.color.chip_background);
                    tagsGroup.addView(chip);
                }
            }
        }
    }
}