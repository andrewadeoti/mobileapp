/**
 * ShoppingListItem class represents an item in the user's shopping list.
 * It contains information about the item including name, quantity, unit,
 * and whether it has been checked off the list.
 */
package com.example.recipe_app;

public class ShoppingListItem {
    // Item identification
    private String id;          // Unique identifier for the item
    private String name;        // Name of the item
    private String recipeId;    // ID of the recipe this item belongs to (if applicable)
    
    // Item details
    private boolean checked;     // Whether the item has been checked off
    private String quantity;    // Amount of the item
    private String unit;        // Unit of measurement (e.g., kg, pieces)

    /**
     * Default constructor
     */
    public ShoppingListItem() {
        // Default constructor
    }

    /**
     * Constructor for items associated with a recipe
     * @param name Name of the item
     * @param recipeId ID of the associated recipe
     */
    public ShoppingListItem(String name, String recipeId) {
        this.name = name;
        this.recipeId = recipeId;
        this.checked = false;
        this.quantity = "1";
        this.unit = "";
    }

    /**
     * Constructor for manually added items
     * @param name Name of the item
     * @param quantity Amount of the item
     * @param unit Unit of measurement
     */
    public ShoppingListItem(String name, String quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.checked = false;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}