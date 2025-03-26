/**
 * ExampleUnitTest - Makes Sure Our App Works Right
 * 
 * This class helps us test parts of our app to make sure they work correctly.
 * It's like having a helper that checks our work before we show it to users.
 * 
 * For example, it can test:
 * - If recipes are saved correctly
 * - If ingredients are added properly
 * - If cooking times are calculated right
 */
package com.example.recipe_app;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    /**
     * A simple test to make sure our testing setup works
     */
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}