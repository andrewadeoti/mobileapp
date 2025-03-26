/**
 * PopularRecipeAdapter handles the display of popular recipes in a horizontal list.
 * It provides a specialized card layout for featured recipes with additional
 * metadata such as category, difficulty, and author information.
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

import java.util.List;

/**
 * Adapter for displaying popular recipe items in a horizontal RecyclerView.
 * Features a card-based layout with enhanced visual elements and
 * metadata for featured recipes.
 */
public class PopularRecipeAdapter extends RecyclerView.Adapter<PopularRecipeAdapter.ViewHolder> {
    private List<Recipe> recipes;
    private final OnRecipeClickListener listener;

    /**
     * Interface for handling popular recipe click events
     */
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    /**
     * Constructor for PopularRecipeAdapter
     * @param recipes List of popular recipes to display
     * @param listener Callback for recipe click events
     */
    public PopularRecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_popular_recipe, parent, false);
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

    // Add the setRecipes method
    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for popular recipe items
     * Handles the display of enhanced recipe cards with additional metadata
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView recipeImage;     // Featured recipe image
        private final TextView recipeName;        // Recipe name
        private final TextView cookingTime;       // Total cooking time
        private final TextView difficulty;        // Recipe difficulty level

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            recipeName = itemView.findViewById(R.id.recipeName);
            cookingTime = itemView.findViewById(R.id.cookingTime);
            difficulty = itemView.findViewById(R.id.difficulty);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onRecipeClick(recipes.get(position));
                }
            });
        }

        /**
         * Binds popular recipe data to the enhanced card view
         * @param recipe Recipe to display
         * @param listener Callback for recipe click events
         */
        void bind(Recipe recipe, OnRecipeClickListener listener) {
            recipeName.setText(recipe.getName());

            // Format and set cooking time
            String timeText = recipe.getCookingTimeDisplay();
            cookingTime.setText(timeText);

            // Set difficulty based on total time
            difficulty.setText(recipe.getDifficulty());

            // Load recipe image using Glide
            Glide.with(itemView.getContext())
                    .load(recipe.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .centerCrop()
                    .into(recipeImage);
        }
    }
}