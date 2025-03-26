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
    private List<ShoppingListItem> items = new ArrayList<>();
    private final OnItemCheckedListener checkedListener;
    private final OnItemRemoveListener removeListener;

    public interface OnItemCheckedListener {
        void onItemChecked(ShoppingListItem item, boolean isChecked);
    }

    public interface OnItemRemoveListener {
        void onItemRemove(int position);
    }

    public ShoppingListAdapter(List<ShoppingListItem> items, OnItemCheckedListener listener, OnItemRemoveListener removeListener) {
        this.items = items;
        this.checkedListener = listener;
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public ShoppingListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping_list, parent, false);
        return new ShoppingListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListItemViewHolder holder, int position) {
        ShoppingListItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<ShoppingListItem> newItems) {
        this.items = new ArrayList<>(newItems);
        notifyDataSetChanged();
    }

    public void addItem(ShoppingListItem item) {
        this.items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < items.size()) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    class ShoppingListItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemText;
        private final ImageButton removeButton;

        public ShoppingListItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.itemText);
            removeButton = itemView.findViewById(R.id.removeButton);

            removeButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && removeListener != null) {
                    removeListener.onItemRemove(position);
                }
            });
        }

        public void bind(ShoppingListItem item) {
            String displayText = item.getName();
            if (item.getQuantity() != null && !item.getQuantity().isEmpty() && 
                item.getUnit() != null && !item.getUnit().isEmpty()) {
                displayText += " (" + item.getQuantity() + " " + item.getUnit() + ")";
            } else if (item.getQuantity() != null && !item.getQuantity().isEmpty()) {
                displayText += " (" + item.getQuantity() + ")";
            }
            itemText.setText(displayText);
        }
    }
}