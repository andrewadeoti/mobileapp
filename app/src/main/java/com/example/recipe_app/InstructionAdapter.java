/**
 * InstructionAdapter handles the display of recipe cooking instructions.
 * It provides a simple text-based layout for displaying numbered
 * cooking steps in a RecyclerView.
 */
package com.example.recipe_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for displaying cooking instruction items in a RecyclerView.
 * Uses a simple list item layout to show numbered instruction steps.
 */
public class InstructionAdapter extends RecyclerView.Adapter<InstructionAdapter.ViewHolder> {
    private List<String> instructions;

    public InstructionAdapter(List<String> instructions) {
        this.instructions = instructions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_instruction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String instruction = instructions.get(position);
        holder.stepNumber.setText(String.format("%d.", position + 1));
        holder.instructionText.setText(instruction);
    }

    @Override
    public int getItemCount() {
        return instructions != null ? instructions.size() : 0;
    }

    /**
     * Updates the list of instructions and refreshes the display
     * @param newInstructions New list of cooking instructions to display
     */
    public void updateInstructions(List<String> newInstructions) {
        this.instructions = newInstructions;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for instruction items
     * Handles the display of cooking step text in a simple list item layout
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView stepNumber;
        TextView instructionText;

        /**
         * Constructor for ViewHolder
         * @param itemView The view for this item
         */
        ViewHolder(View itemView) {
            super(itemView);
            stepNumber = itemView.findViewById(R.id.stepNumber);
            instructionText = itemView.findViewById(R.id.instructionText);
        }
    }
}