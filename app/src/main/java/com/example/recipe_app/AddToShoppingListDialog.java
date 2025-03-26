/**
 * AddToShoppingListDialog - Helps Add Items to Your Shopping List
 * 
 * This class shows a small window where you can add ingredients
 * to your shopping list. It's like having a helper that:
 * - Shows you what ingredients you need
 * - Lets you pick which ones to add
 * - Adds them to your shopping list
 * 
 * It makes it easy to remember what to buy at the store
 * when you want to make a recipe.
 */
package com.example.recipe_app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import java.util.ArrayList;
import java.util.List;

public class AddToShoppingListDialog extends DialogFragment {
    // The recipe we're adding ingredients from
    private Recipe recipe;
    // Where to send back the selected ingredients
    private OnIngredientsSelectedListener listener;

    /**
     * Interface for telling the app what ingredients were selected
     */
    public interface OnIngredientsSelectedListener {
        void onIngredientsSelected(List<String> ingredients);
    }

    /**
     * Creates a new dialog for adding ingredients
     * 
     * @param recipe The recipe to get ingredients from
     * @param listener Who to tell when ingredients are selected
     */
    public static AddToShoppingListDialog newInstance(Recipe recipe, OnIngredientsSelectedListener listener) {
        AddToShoppingListDialog dialog = new AddToShoppingListDialog();
        dialog.recipe = recipe;
        dialog.listener = listener;
        return dialog;
    }

    /**
     * Shows the dialog with checkboxes for each ingredient
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_to_shopping_list, null);

        // Create the list of checkboxes
        LinearLayout ingredientList = view.findViewById(R.id.ingredientsContainer);
        List<CheckBox> checkBoxes = new ArrayList<>();

        // Add a checkbox for each ingredient
        for (String ingredient : recipe.getIngredients()) {
            CheckBox checkBox = new CheckBox(requireContext());
            checkBox.setText(ingredient);
            checkBox.setChecked(true);  // Check by default
            ingredientList.addView(checkBox);
            checkBoxes.add(checkBox);
        }

        // Set up the dialog buttons
        builder.setView(view)
               .setTitle("Add to Shopping List")
               .setPositiveButton("Add Selected", (dialog, id) -> {
                   // Get all checked ingredients
                   List<String> selectedIngredients = new ArrayList<>();
                   for (CheckBox checkBox : checkBoxes) {
                       if (checkBox.isChecked()) {
                           selectedIngredients.add(checkBox.getText().toString());
                       }
                   }
                   // Tell the app what was selected
                   if (listener != null) {
                       listener.onIngredientsSelected(selectedIngredients);
                   }
               })
               .setNegativeButton("Cancel", (dialog, id) -> {
                   // Just close the dialog
                   dialog.cancel();
               });

        return builder.create();
    }
}