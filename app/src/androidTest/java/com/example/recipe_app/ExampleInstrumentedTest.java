/**
 * ExampleInstrumentedTest - Tests Our App on a Real Phone
 * 
 * This class helps us test our app on actual phones or tablets.
 * It's like having a friend try out the app to make sure everything works
 * the way it should in real life.
 * 
 * For example, it can test:
 * - If the app starts up properly
 * - If buttons work when tapped
 * - If recipes show up correctly on the screen
 */
package com.example.recipe_app;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Tests that run on an Android device or emulator
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    /**
     * Makes sure our app's package name is correct
     */
    @Test
    public void useAppContext() {
        // Get the app's test environment
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // Check if the package name matches our app
        assertEquals("com.example.recipe_app", appContext.getPackageName());
    }
}