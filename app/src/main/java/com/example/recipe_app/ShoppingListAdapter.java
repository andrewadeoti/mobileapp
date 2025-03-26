/**
 * ShoppingListAdapter - Shows Your Shopping List
 * 
 * This class helps show your shopping list on the screen.
 * It's like having a helper that:
 * - Shows each item in your list
 * - Lets you check off items you've bought
 * - Lets you remove items you don't need anymore
 */
package com.example.recipe_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListItemViewHolder> {
    // The list of items to show
    private List<ShoppingListItem> items = new ArrayList<>();
    
    // People to tell when something changes
    private final OnItemCheckedListener checkedListener;  // When you check off an item
    private final OnItemRemoveListener removeListener;    // When you remove an item

    /**
     * Interface for when you check off an item
     */
    public interface OnItemCheckedListener {
        void onItemChecked(ShoppingListItem item, boolean isChecked);
    }

    /**
     * Interface for when you remove an item
     */
    public interface OnItemRemoveListener {
        void onItemRemove(int position);
    }

    /**
     * Creates a new shopping list adapter
     * 
     * This sets up everything we need to show your shopping list.
     */
    public ShoppingListAdapter(List<ShoppingListItem> items, OnItemCheckedListener listener, OnItemRemoveListener removeListener) {
        this.items = items;
        this.checkedListener = listener;
        this.removeListener = removeListener;
    }

    /**
     * Creates a new view for each item in the list
     */
    @NonNull
    @Override
    public ShoppingListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping_list, parent, false);
        return new ShoppingListItemViewHolder(view);
    }

    /**
     * Shows the item's information in the view
     */
    @Override
    public void onBindViewHolder(@NonNull ShoppingListItemViewHolder holder, int position) {
        ShoppingListItem item = items.get(position);
        holder.bind(item);
    }

    /**
     * Gets how many items are in the list
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Updates the list with new items
     */
    public void updateItems(List<ShoppingListItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    /**
     * Adds a new item to the list
     */
    public void addItem(ShoppingListItem item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    /**
     * Removes an item from the list
     */
    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * Holds the view for each item in the list
     */
    class ShoppingListItemViewHolder extends RecyclerView.ViewHolder {
        // The text showing the item's name
        private final TextView itemText;
        // The button to remove the item
        private final ImageButton removeButton;

        /**
         * Creates a new view holder
         */
        public ShoppingListItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.itemText);
            removeButton = itemView.findViewById(R.id.removeButton);
        }

        /**
         * Shows the item's information
         */
        public void bind(ShoppingListItem item) {
            String displayText = item.getName();
            if (item.getQuantity() != null && !item.getQuantity().isEmpty() && 
                item.getUnit() != null && !item.getUnit().isEmpty()) {
                displayText += " (" + item.getQuantity() + " " + item.getUnit() + ")";
            } else if (item.getQuantity() != null && !item.getQuantity().isEmpty()) {
                displayText += " (" + item.getQuantity() + ")";
            }
            itemText.setText(displayText);
            
            // When you click the remove button
            removeButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    removeListener.onItemRemove(position);
                }
            });
        }
    }
}