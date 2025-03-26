package com.example.recipe_app;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class RecipeApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
} 