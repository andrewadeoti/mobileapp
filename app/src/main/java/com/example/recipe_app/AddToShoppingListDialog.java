package com.example.recipe_app;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AddToShoppingListDialog extends DialogFragment {
    private static final String ARG_INGREDIENTS = "ingredients";
    private static final String ARG_RECIPE_ID = "recipe_id";
    private List<CheckBox> ingredientCheckboxes;
    private OnIngredientsSelectedListener listener;

    public interface OnIngredientsSelectedListener {
        void onIngredientsSelected(List<String> selectedIngredients, String recipeId);
    }

    public static AddToShoppingListDialog newInstance(List<String> ingredients, String recipeId) {
        AddToShoppingListDialog dialog = new AddToShoppingListDialog();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_INGREDIENTS, new ArrayList<>(ingredients));
        args.putString(ARG_RECIPE_ID, recipeId);
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnIngredientsSelectedListener(OnIngredientsSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_to_shopping_list, null);

        // Get ingredients and recipe ID from arguments
        ArrayList<String> ingredients = requireArguments().getStringArrayList(ARG_INGREDIENTS);
        String recipeId = requireArguments().getString(ARG_RECIPE_ID);

        // Create checkboxes for each ingredient
        LinearLayout container = view.findViewById(R.id.ingredientsContainer);
        ingredientCheckboxes = new ArrayList<>();
        for (String ingredient : ingredients) {
            CheckBox checkBox = new CheckBox(requireContext());
            checkBox.setText(ingredient);
            checkBox.setChecked(true);
            container.addView(checkBox);
            ingredientCheckboxes.add(checkBox);
        }

        // Setup buttons
        MaterialButton cancelButton = view.findViewById(R.id.cancelButton);
        MaterialButton addButton = view.findViewById(R.id.addButton);

        cancelButton.setOnClickListener(v -> dismiss());
        addButton.setOnClickListener(v -> {
            if (listener != null) {
                List<String> selectedIngredients = new ArrayList<>();
                for (CheckBox checkBox : ingredientCheckboxes) {
                    if (checkBox.isChecked()) {
                        selectedIngredients.add(checkBox.getText().toString());
                    }
                }
                listener.onIngredientsSelected(selectedIngredients, recipeId);
            }
            dismiss();
        });

        builder.setView(view);
        return builder.create();
    }
}