package com.example.recipe_app;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {
    private RecyclerView historyRecyclerView;
    private TextView emptyView;
    private RecipeAdapter adapter;
    private RecipeHistory recipeHistory;
    private MaterialButton clearHistoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        emptyView = findViewById(R.id.emptyView);
        clearHistoryButton = findViewById(R.id.clearHistoryButton);

        // Initialize history
        recipeHistory = new RecipeHistory(this);

        // Set up RecyclerView
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(new ArrayList<>(), this); // Initialize with empty list
        historyRecyclerView.setAdapter(adapter);

        // Load and display history
        loadHistory();

        // Set up clear history button
        clearHistoryButton.setOnClickListener(v -> {
            recipeHistory.clearHistory();
            loadHistory();
        });
    }

    private void loadHistory() {
        List<Recipe> history = recipeHistory.getHistory();
        adapter.setRecipes(history);
        
        // Update empty state
        if (history.isEmpty()) {
            historyRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            clearHistoryButton.setVisibility(View.GONE);
        } else {
            historyRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            clearHistoryButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        startActivity(RecipeDetailActivity.newIntent(this, recipe.getId()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}