/**
 * ShoppingListActivity manages the user's shopping list.
 * It displays a list of ingredients needed for recipes,
 * allows adding new items manually, and checking off items.
 */
package com.example.recipe_app;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ShoppingListActivity extends AppCompatActivity {
    private EditText notesEditText;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "ShoppingListPrefs";
    private static final String NOTES_KEY = "shopping_notes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Shopping List");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Initialize views
        notesEditText = findViewById(R.id.notesEditText);

        // Load saved notes
        loadNotes();
    }

    private void loadNotes() {
        String savedNotes = sharedPreferences.getString(NOTES_KEY, "");
        notesEditText.setText(savedNotes);
    }

    private void saveNotes() {
        String notes = notesEditText.getText().toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NOTES_KEY, notes);
        editor.apply();
        Toast.makeText(this, "Notes saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveNotes();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            saveNotes();
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}