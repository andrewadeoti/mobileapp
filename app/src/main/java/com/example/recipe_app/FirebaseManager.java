package com.example.recipe_app;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FirebaseManager serves as a centralized place for all Firebase operations in the app.
 * It handles Firestore database operations, Firebase Authentication, and Firebase Storage.
 */
public class FirebaseManager {
    private static final String TAG = "FirebaseManager";
    
    // Firebase instances
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final FirebaseStorage storage;
    
    // Collection references
    private final CollectionReference usersRef;
    private final CollectionReference recipesRef;
    private final CollectionReference favoritesRef;
    private final CollectionReference historyRef;
    
    // Singleton instance
    private static FirebaseManager instance;
    
    // Interface for callbacks
    public interface FirebaseCallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }
    
    // Private constructor for Singleton pattern
    private FirebaseManager() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        
        // Initialize collection references
        usersRef = db.collection("users");
        recipesRef = db.collection("recipes");
        favoritesRef = db.collection("favorites");
        historyRef = db.collection("history");
    }
    
    // Get singleton instance
    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }
    
    /**
     * Get current user from Firebase Auth
     */
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }
    
    /**
     * Get user-specific document reference
     */
    private DocumentReference getUserDocRef() {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            return usersRef.document(user.getUid());
        }
        return null;
    }
    
    /**
     * User Profile Operations
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
     * Recipe Operations
     */
    
    public void saveRecipe(Recipe recipe, FirebaseCallback<String> callback) {
        // Create recipe map
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
        
        if (recipe.getId() != null && !recipe.getId().isEmpty()) {
            // Update existing recipe
            recipesRef.document(recipe.getId())
                .set(recipeMap)
                .addOnSuccessListener(aVoid -> callback.onSuccess(recipe.getId()))
                .addOnFailureListener(callback::onFailure);
        } else {
            // Add new recipe
            recipesRef.add(recipeMap)
                .addOnSuccessListener(documentReference -> callback.onSuccess(documentReference.getId()))
                .addOnFailureListener(callback::onFailure);
        }
    }
    
    public void getRecipe(String recipeId, FirebaseCallback<Recipe> callback) {
        recipesRef.document(recipeId).get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Recipe recipe = documentSnapshotToRecipe(documentSnapshot);
                    callback.onSuccess(recipe);
                } else {
                    callback.onFailure(new Exception("Recipe not found"));
                }
            })
            .addOnFailureListener(callback::onFailure);
    }
    
    public void getAllRecipes(FirebaseCallback<List<Recipe>> callback) {
        recipesRef.get()
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
    
    private Recipe documentSnapshotToRecipe(DocumentSnapshot document) {
        String id = document.getId();
        String name = document.getString("name");
        String description = document.getString("description");
        String imageUrl = document.getString("imageUrl");
        int prepTime = document.getLong("prepTime") != null ? document.getLong("prepTime").intValue() : 0;
        int cookTime = document.getLong("cookTime") != null ? document.getLong("cookTime").intValue() : 0;
        int servings = document.getLong("servings") != null ? document.getLong("servings").intValue() : 0;
        String difficulty = document.getString("difficulty");
        String cuisine = document.getString("cuisine");
        String category = document.getString("category");
        
        // Get ingredients and instructions as lists with proper type checking
        List<String> ingredients = new ArrayList<>();
        List<String> instructions = new ArrayList<>();
        
        Object ingredientsObj = document.get("ingredients");
        if (ingredientsObj instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof String) {
                    ingredients.add((String) item);
                }
            }
        }
        
        Object instructionsObj = document.get("instructions");
        if (instructionsObj instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof String) {
                    instructions.add((String) item);
                }
            }
        }
        
        Recipe recipe = new Recipe(id, name, description, prepTime, cookTime, imageUrl, ingredients);
        recipe.setInstructions(instructions);
        recipe.setCuisine(cuisine);
        recipe.setCategory(category);
        recipe.setDifficulty(difficulty);
        recipe.setServings(servings);
        
        return recipe;
    }
    
    /**
     * Favorites Operations
     */
    
    public void addFavorite(String recipeId, FirebaseCallback<Void> callback) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }
        
        Map<String, Object> favoriteMap = new HashMap<>();
        favoriteMap.put("userId", user.getUid());
        favoriteMap.put("recipeId", recipeId);
        favoriteMap.put("timestamp", System.currentTimeMillis());
        
        favoritesRef.document(user.getUid() + "_" + recipeId)
            .set(favoriteMap)
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(callback::onFailure);
    }
    
    public void removeFavorite(String recipeId, FirebaseCallback<Void> callback) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }
        
        favoritesRef.document(user.getUid() + "_" + recipeId)
            .delete()
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(callback::onFailure);
    }
    
    public void getFavorites(FirebaseCallback<List<Recipe>> callback) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }
        
        favoritesRef.whereEqualTo("userId", user.getUid())
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<String> recipeIds = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    String recipeId = document.getString("recipeId");
                    if (recipeId != null) {
                        recipeIds.add(recipeId);
                    }
                }
                
                if (recipeIds.isEmpty()) {
                    callback.onSuccess(new ArrayList<>());
                    return;
                }
                
                // Get all favorite recipes
                List<Recipe> favoriteRecipes = new ArrayList<>();
                final int[] count = {0};
                for (String recipeId : recipeIds) {
                    getRecipe(recipeId, new FirebaseCallback<Recipe>() {
                        @Override
                        public void onSuccess(Recipe recipe) {
                            favoriteRecipes.add(recipe);
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
    
    public void isFavorite(String recipeId, FirebaseCallback<Boolean> callback) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }

        favoritesRef.document(user.getUid() + "_" + recipeId)
            .get()
            .addOnSuccessListener(documentSnapshot -> callback.onSuccess(documentSnapshot.exists()))
            .addOnFailureListener(callback::onFailure);
    }
    
    /**
     * Recipe History Operations
     */
    
    public void addToHistory(String recipeId, FirebaseCallback<Void> callback) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }
        
        Map<String, Object> historyMap = new HashMap<>();
        historyMap.put("userId", user.getUid());
        historyMap.put("recipeId", recipeId);
        historyMap.put("timestamp", System.currentTimeMillis());
        
        String historyId = user.getUid() + "_" + recipeId + "_" + System.currentTimeMillis();
        historyRef.document(historyId)
            .set(historyMap)
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(callback::onFailure);
    }
    
    public void getHistory(FirebaseCallback<List<Recipe>> callback) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }
        
        historyRef.whereEqualTo("userId", user.getUid())
            .orderBy("timestamp")
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
                
                // Get all history recipes
                List<Recipe> historyRecipes = new ArrayList<>();
                final int[] count = {0};
                for (String recipeId : recipeIds) {
                    getRecipe(recipeId, new FirebaseCallback<Recipe>() {
                        @Override
                        public void onSuccess(Recipe recipe) {
                            historyRecipes.add(recipe);
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
     * Authentication Operations
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
    
    public void logoutUser() {
        auth.signOut();
    }
    
    public void resetPassword(String email, FirebaseCallback<Void> callback) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(callback::onFailure);
    }
    
    /**
     * Storage Operations
     */
    
    public StorageReference getStorageReference(String path) {
        return storage.getReference().child(path);
    }
} 