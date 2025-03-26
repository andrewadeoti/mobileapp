/**
 * FirebaseManager - The Backend of Our Recipe App
 * 
 * This class handles all the behind-the-scenes work of saving and loading recipes.
 * Think of it as the app's memory - it keeps track of:
 * - All recipes in the app
 * - Who likes which recipes
 * - Who created which recipes
 * - What recipes people have looked at
 * - Who is using the app
 */
package com.example.recipe_app;

import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseManager {
    // A tag to help us find messages in the app's logs
    private static final String TAG = "FirebaseManager";
    
    // The main tools we use to talk to Firebase
    private final FirebaseFirestore db;      // For saving and loading data
    private final FirebaseAuth auth;         // For handling user accounts
    private final FirebaseStorage storage;   // For saving pictures
    
    // Different places where we store our data
    private final CollectionReference usersRef;      // Where we keep user information
    private final CollectionReference recipesRef;    // Where we keep all recipes
    private final CollectionReference favoritesRef;  // Where we keep favorite recipes
    private final CollectionReference historyRef;    // Where we keep recipe viewing history
    
    // We only want one copy of this class running at a time
    private static FirebaseManager instance;
    
    // A way to tell the app when something is done
    public interface FirebaseCallback<T> {
        void onSuccess(T result);  // Called when something works
        void onFailure(Exception e); // Called when something goes wrong
    }
    
    /**
     * Creates a new FirebaseManager
     * 
     * This sets up our connection to Firebase and gets ready to save and load data.
     * We only want one of these, so the constructor is private.
     */
    private FirebaseManager() {
        // Connect to Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        
        // Set up places to store different types of data
        usersRef = db.collection("users");      // For user information
        recipesRef = db.collection("recipes");  // For all recipes
        favoritesRef = db.collection("favorites"); // For favorite recipes
        historyRef = db.collection("history");  // For recipe viewing history
    }
    
    /**
     * Gets the one and only FirebaseManager
     * 
     * This makes sure we only have one copy of this class running at a time.
     */
    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }
    
    /**
     * Gets the person who is currently using the app
     */
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }
    
    /**
     * Gets the ID of the person who is currently using the app
     */
    public String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }
    
    /**
     * Gets where we store information about the current user
     */
    private DocumentReference getUserDocRef() {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            return usersRef.document(user.getUid());
        }
        return null;
    }
    
    /**
     * Saves information about the current user
     * 
     * This is like updating someone's profile with new information.
     */
    public void saveUserProfile(Map<String, Object> userData, FirebaseCallback<Void> callback) {
        DocumentReference userRef = getUserDocRef();
        if (userRef == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }
        
        userRef.set(userData)
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(callback::onFailure);
    }
    
    /**
     * Loads information about the current user
     * 
     * This is like looking up someone's profile to see their information.
     */
    public void getUserProfile(FirebaseCallback<Map<String, Object>> callback) {
        DocumentReference userRef = getUserDocRef();
        if (userRef == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }
        
        userRef.get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    callback.onSuccess(documentSnapshot.getData());
                } else {
                    callback.onSuccess(new HashMap<>());
                }
            })
            .addOnFailureListener(callback::onFailure);
    }
    
    /**
     * Saves a recipe to the database
     * 
     * This is like adding a new recipe card to our collection.
     * If the recipe already exists, it updates it instead.
     */
    public void saveRecipe(Recipe recipe, FirebaseCallback<String> callback) {
        // Create a map of all the recipe information
        Map<String, Object> recipeMap = new HashMap<>();
        recipeMap.put("id", recipe.getId());
        recipeMap.put("name", recipe.getName());
        recipeMap.put("description", recipe.getDescription());
        recipeMap.put("imageUrl", recipe.getImageUrl());
        recipeMap.put("prepTime", recipe.getPrepTime());
        recipeMap.put("cookTime", recipe.getCookTime());
        recipeMap.put("servings", recipe.getServings());
        recipeMap.put("ingredients", recipe.getIngredients());
        recipeMap.put("instructions", recipe.getInstructions());
        recipeMap.put("difficulty", recipe.getDifficulty());
        recipeMap.put("cuisine", recipe.getCuisine());
        recipeMap.put("category", recipe.getCategory());
        recipeMap.put("timestamp", System.currentTimeMillis());
        
        // If the recipe already exists, update it
        if (recipe.getId() != null && !recipe.getId().isEmpty()) {
            recipesRef.document(recipe.getId())
                .set(recipeMap)
                .addOnSuccessListener(aVoid -> callback.onSuccess(recipe.getId()))
                .addOnFailureListener(callback::onFailure);
        } else {
            // If it's a new recipe, add it
            recipesRef.add(recipeMap)
                .addOnSuccessListener(documentReference -> callback.onSuccess(documentReference.getId()))
                .addOnFailureListener(callback::onFailure);
        }
    }
    
    /**
     * Loads a specific recipe from the database
     * 
     * This is like looking up a specific recipe card by its number.
     */
    public void getRecipe(String recipeId, FirebaseCallback<Recipe> callback) {
        recipesRef.document(recipeId).get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Recipe recipe = documentSnapshotToRecipe(documentSnapshot);
                    checkIfFavorite(recipe, callback);
                } else {
                    callback.onFailure(new Exception("Recipe not found"));
                }
            })
            .addOnFailureListener(callback::onFailure);
    }
    
    /**
     * Loads all recipes from the database
     * 
     * This is like getting all recipe cards from our collection.
     */
    public void getAllRecipes(FirebaseCallback<List<Recipe>> callback) {
        recipesRef.get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Recipe> recipes = new ArrayList<>();
                List<Recipe> tempRecipes = new ArrayList<>();
                
                // Convert each document into a recipe
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Recipe recipe = documentSnapshotToRecipe(document);
                    tempRecipes.add(recipe);
                }
                
                // If there are no recipes, return an empty list
                if (tempRecipes.isEmpty()) {
                    callback.onSuccess(recipes);
                    return;
                }

                // Check if each recipe is a favorite
                final int[] count = {0};
                for (Recipe recipe : tempRecipes) {
                    checkIfFavorite(recipe, new FirebaseCallback<Recipe>() {
                        @Override
                        public void onSuccess(Recipe checkedRecipe) {
                            recipes.add(checkedRecipe);
                            count[0]++;
                            if (count[0] == tempRecipes.size()) {
                                callback.onSuccess(recipes);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            count[0]++;
                            if (count[0] == tempRecipes.size()) {
                                callback.onSuccess(recipes);
                            }
                        }
                    });
                }
            })
            .addOnFailureListener(callback::onFailure);
    }
    
    /**
     * Loads recipes of a specific type (like all desserts)
     */
    public void getRecipesByCategory(String category, FirebaseCallback<List<Recipe>> callback) {
        recipesRef.whereEqualTo("category", category)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Recipe> recipes = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Recipe recipe = documentSnapshotToRecipe(document);
                    recipes.add(recipe);
                }
                callback.onSuccess(recipes);
            })
            .addOnFailureListener(callback::onFailure);
    }
    
    /**
     * Loads recipes from a specific cuisine (like all Italian recipes)
     */
    public void getRecipesByCuisine(String cuisine, FirebaseCallback<List<Recipe>> callback) {
        recipesRef.whereEqualTo("cuisine", cuisine)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Recipe> recipes = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Recipe recipe = documentSnapshotToRecipe(document);
                    recipes.add(recipe);
                }
                callback.onSuccess(recipes);
            })
            .addOnFailureListener(callback::onFailure);
    }
    
    /**
     * Converts a database document into a Recipe object
     * 
     * This is like reading a recipe card and making a copy of it.
     */
    private Recipe documentSnapshotToRecipe(DocumentSnapshot document) {
        // Get all the basic information
        String id = document.getId();
        String name = document.getString("name");
        String description = document.getString("description");
        String imageUrl = document.getString("imageUrl");
        int prepTime = document.getLong("prepTime") != null ? document.getLong("prepTime").intValue() : 0;
        int cookTime = document.getLong("cookTime") != null ? document.getLong("cookTime").intValue() : 0;
        int servings = document.getLong("servings") != null ? document.getLong("servings").intValue() : 4;
        String difficulty = document.getString("difficulty");
        String cuisine = document.getString("cuisine");
        String category = document.getString("category");
        
        // Get the lists of ingredients and instructions
        List<String> ingredients = new ArrayList<>();
        List<String> instructions = new ArrayList<>();
        
        // Handle ingredients list
        Object ingredientsObj = document.get("ingredients");
        if (ingredientsObj instanceof List<?>) {
            for (Object item : ((List<?>) ingredientsObj)) {
                if (item instanceof String) {
                    ingredients.add((String) item);
                }
            }
        }
        
        // Handle instructions list
        Object instructionsObj = document.get("instructions");
        if (instructionsObj instanceof List<?>) {
            for (Object item : ((List<?>) instructionsObj)) {
                if (item instanceof String) {
                    instructions.add((String) item);
                }
            }
        }

        // Create a new recipe with all the information
        Recipe recipe = new Recipe(name, description, imageUrl);
        recipe.setId(id);
        recipe.setPrepTime(prepTime);
        recipe.setCookTime(cookTime);
        recipe.setServings(servings);
        recipe.setIngredients(ingredients);
        recipe.setInstructions(instructions);
        recipe.setDifficulty(difficulty);
        recipe.setCuisine(cuisine);
        recipe.setCategory(category);
        
        // Set who created the recipe
        String userId = getCurrentUserId();
        if (userId != null) {
            recipe.setUserId(userId);
        }
        
        return recipe;
    }
    
    /**
     * Checks if the current user likes a recipe
     */
    private void checkIfFavorite(Recipe recipe, FirebaseCallback<Recipe> callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onSuccess(recipe);
            return;
        }

        favoritesRef.document(userId)
            .collection("recipes")
            .document(recipe.getId())
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                recipe.setFavorite(documentSnapshot.exists());
                callback.onSuccess(recipe);
            })
            .addOnFailureListener(e -> callback.onSuccess(recipe));
    }
    
    /**
     * Adds a recipe to the user's favorites
     */
    public void addFavorite(String recipeId, FirebaseCallback<Void> callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }
        
        Map<String, Object> favoriteMap = new HashMap<>();
        favoriteMap.put("timestamp", System.currentTimeMillis());
        
        favoritesRef.document(userId)
            .collection("recipes")
            .document(recipeId)
            .set(favoriteMap)
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(callback::onFailure);
    }
    
    /**
     * Removes a recipe from the user's favorites
     */
    public void removeFavorite(String recipeId, FirebaseCallback<Void> callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }
        
        favoritesRef.document(userId)
            .collection("recipes")
            .document(recipeId)
            .delete()
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(callback::onFailure);
    }
    
    /**
     * Checks if a recipe is in the user's favorites
     */
    public void isFavorite(String recipeId, FirebaseCallback<Boolean> callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onSuccess(false);
            return;
        }

        favoritesRef.document(userId)
            .collection("recipes")
            .document(recipeId)
            .get()
            .addOnSuccessListener(documentSnapshot -> callback.onSuccess(documentSnapshot.exists()))
            .addOnFailureListener(callback::onFailure);
    }
    
    /**
     * Gets all the recipes the user has marked as favorites
     */
    public void getFavorites(FirebaseCallback<List<Recipe>> callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }
        
        favoritesRef.document(userId)
            .collection("recipes")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<String> recipeIds = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    recipeIds.add(document.getId());
                }
                
                if (recipeIds.isEmpty()) {
                    callback.onSuccess(new ArrayList<>());
                    return;
                }
                
                List<Recipe> favoriteRecipes = new ArrayList<>();
                final int[] count = {0};
                for (String recipeId : recipeIds) {
                    getRecipe(recipeId, new FirebaseCallback<Recipe>() {
                        @Override
                        public void onSuccess(Recipe recipe) {
                            if (recipe != null) {
                                recipe.setFavorite(true);
                                favoriteRecipes.add(recipe);
                            }
                            count[0]++;
                            if (count[0] == recipeIds.size()) {
                                callback.onSuccess(favoriteRecipes);
                            }
                        }
                        
                        @Override
                        public void onFailure(Exception e) {
                            count[0]++;
                            if (count[0] == recipeIds.size()) {
                                callback.onSuccess(favoriteRecipes);
                            }
                        }
                    });
                }
            })
            .addOnFailureListener(callback::onFailure);
    }
    
    /**
     * Records that a user has looked at a recipe
     */
    public void addToHistory(String recipeId, FirebaseCallback<Void> callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }

        Map<String, Object> historyMap = new HashMap<>();
        historyMap.put("userId", userId);
        historyMap.put("recipeId", recipeId);
        historyMap.put("timestamp", System.currentTimeMillis());

        historyRef.document(userId)
            .collection("recipes")
            .document(recipeId)
            .set(historyMap)
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(callback::onFailure);
    }
    
    /**
     * Gets all the recipes the user has looked at recently
     */
    public void getHistory(FirebaseCallback<List<Recipe>> callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }

        historyRef.document(userId)
            .collection("recipes")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<String> recipeIds = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    String recipeId = document.getString("recipeId");
                    if (recipeId != null && !recipeIds.contains(recipeId)) {
                        recipeIds.add(recipeId);
                    }
                }

                if (recipeIds.isEmpty()) {
                    callback.onSuccess(new ArrayList<>());
                    return;
                }

                List<Recipe> historyRecipes = new ArrayList<>();
                final int[] count = {0};
                for (String recipeId : recipeIds) {
                    getRecipe(recipeId, new FirebaseCallback<Recipe>() {
                        @Override
                        public void onSuccess(Recipe recipe) {
                            if (recipe != null) {
                                historyRecipes.add(recipe);
                            }
                            count[0]++;
                            if (count[0] == recipeIds.size()) {
                                callback.onSuccess(historyRecipes);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            count[0]++;
                            if (count[0] == recipeIds.size()) {
                                callback.onSuccess(historyRecipes);
                            }
                        }
                    });
                }
            })
            .addOnFailureListener(callback::onFailure);
    }
    
    /**
     * Creates a new user account
     */
    public void registerUser(String email, String password, FirebaseCallback<FirebaseUser> callback) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    callback.onSuccess(user);
                } else {
                    callback.onFailure(task.getException());
                }
            });
    }
    
    /**
     * Logs in an existing user
     */
    public void loginUser(String email, String password, FirebaseCallback<FirebaseUser> callback) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    callback.onSuccess(user);
                } else {
                    callback.onFailure(task.getException());
                }
            });
    }
    
    /**
     * Logs out the current user
     */
    public void logoutUser() {
        auth.signOut();
    }
    
    /**
     * Sends a password reset email to a user
     */
    public void resetPassword(String email, FirebaseCallback<Void> callback) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(callback::onFailure);
    }
    
    /**
     * Gets a place to store files (like recipe pictures)
     */
    public StorageReference getStorageReference(String path) {
        return storage.getReference().child(path);
    }
} 