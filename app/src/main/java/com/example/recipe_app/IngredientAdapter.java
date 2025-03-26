/**
 * IngredientAdapter handles the display of recipe ingredients in a list.
 * It provides a simple text-based layout for displaying ingredient
 * names and quantities in a RecyclerView.
 */
package com.example.recipe_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying ingredient items in a RecyclerView.
 * Uses a simple list item layout to show ingredient text.
 */
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {
    private List<String> ingredients;

    public IngredientAdapter(List<String> ingredients) {
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String ingredient = ingredients.get(position);
        holder.textView.setText("• " + ingredient);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    /**
     * Updates the list of ingredients and refreshes the display
     * @param newIngredients New list of ingredients to display
     */
    public void updateIngredients(List<String> newIngredients) {
        this.ingredients = newIngredients != null ? newIngredients : new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for ingredient items
     * Handles the display of ingredient text in a simple list item layout
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View view) {
            super(view);
            textView = view.findViewById(android.R.id.text1);
        }
    }
}