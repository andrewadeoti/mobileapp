/**
 * AddShoppingItemDialog - Helps Add New Items to Your Shopping List
 * 
 * This class shows a small window where you can add your own items
 * to your shopping list. It's like having a helper that asks:
 * - What do you want to buy?
 * - How much do you need?
 * - What unit of measurement? (like cups, pieces, etc.)
 * 
 * It makes it easy to add anything you need to buy,
 * even if it's not from a recipe.
 */
package com.example.recipe_app;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class AddShoppingItemDialog extends DialogFragment {
    // Who to tell when a new item is added
    private OnItemAddedListener listener;

    /**
     * Interface for telling the app about new items
     */
    public interface OnItemAddedListener {
        void onItemAdded(String name, String quantity, String unit);
    }

    /**
     * Creates a new dialog for adding items
     * 
     * @param listener Who to tell when an item is added
     */
    public static AddShoppingItemDialog newInstance(OnItemAddedListener listener) {
        AddShoppingItemDialog dialog = new AddShoppingItemDialog();
        dialog.listener = listener;
        return dialog;
    }

    /**
     * Shows the dialog with fields for the new item
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_shopping_item, null);

        // Get the input fields
        EditText nameInput = view.findViewById(R.id.nameInput);
        EditText quantityInput = view.findViewById(R.id.quantityInput);
        EditText unitInput = view.findViewById(R.id.unitInput);

        // Set up the dialog buttons
        builder.setView(view)
               .setTitle("Add Shopping Item")
               .setPositiveButton("Add", (dialog, id) -> {
                   // Get what was typed in
                   String name = nameInput.getText().toString().trim();
                   String quantity = quantityInput.getText().toString().trim();
                   String unit = unitInput.getText().toString().trim();

                   // Only add if there's a name
                   if (!name.isEmpty() && listener != null) {
                       listener.onItemAdded(name, quantity, unit);
                   }
               })
               .setNegativeButton("Cancel", (dialog, id) -> {
                   // Just close the dialog
                   dialog.cancel();
               });

        return builder.create();
    }
}