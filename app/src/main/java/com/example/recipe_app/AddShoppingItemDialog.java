/**
 * AddShoppingItemDialog provides a dialog interface for adding new items
 * to the shopping list. It includes fields for item name, quantity, and unit selection.
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
    // Callback interface for handling item addition
    private OnItemAddedListener listener;

    /**
     * Sets the listener for item addition events
     * @param listener The listener to be notified when an item is added
     */
    public void setOnItemAddedListener(OnItemAddedListener listener) {
        this.listener = listener;
    }

    /**
     * Creates and returns a dialog for adding shopping items
     * @param savedInstanceState The last saved instance state of the Fragment
     * @return A new Dialog instance to be displayed by the Fragment
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_shopping_item, null);

        EditText nameInput = view.findViewById(R.id.item_name_input);
        EditText quantityInput = view.findViewById(R.id.item_quantity_input);
        EditText unitInput = view.findViewById(R.id.item_unit_input);

        builder.setView(view)
                .setTitle("Add Shopping Item")
                .setPositiveButton("Add", (dialog, id) -> {
                    String name = nameInput.getText().toString().trim();
                    String quantity = quantityInput.getText().toString().trim();
                    String unit = unitInput.getText().toString().trim();

                    if (!name.isEmpty()) {
                        ShoppingListItem item = new ShoppingListItem(name, quantity.isEmpty() ? "1" : quantity, unit);
                        if (listener != null) {
                            listener.onItemAdded(item);
                        }
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        return builder.create();
    }

    /**
     * Interface for handling item addition events
     */
    public interface OnItemAddedListener {
        /**
         * Called when a new item is added to the shopping list
         * @param item The newly added shopping item
         */
        void onItemAdded(ShoppingListItem item);
    }
}