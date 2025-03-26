/**
 * RecipeApp - The Heart of Our Recipe App
 * 
 * This class starts up our app and gets everything ready.
 * It's like turning on the kitchen lights and making sure
 * all our cooking tools are ready to use.
 * 
 * It helps:
 * - Connect to Firebase (where we store recipes)
 * - Set up notifications
 * - Get the app ready for you to use
 */
package com.example.recipe_app;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class RecipeApp extends Application {
    /**
     * Gets the app ready when it first starts
     * 
     * This is like opening the kitchen for the first time each day:
     * - Turn on the lights (start Firebase)
     * - Get everything ready to cook (set up the app)
     */
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
} 