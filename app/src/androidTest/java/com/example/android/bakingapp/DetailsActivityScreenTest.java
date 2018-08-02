package com.example.android.bakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class DetailsActivityScreenTest {

    private static final String RECIPE_INTRODUCTION = "Recipe Introduction";

    private static final String INGREDIENT = "Ingredient";


    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void init(){
        onView(withId(R.id.content)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    @Test
    public void clickRecyclerView_opensIngredientsFragment() {
        onView(withId(R.id.content_details)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.content_ingredients)).check(matches(hasDescendant(withText(INGREDIENT))));
    }

    @Test
    public void clickRecyclerView_opensRecipeStepsFragment() {
        onView(withId(R.id.content_details)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.instructions_step_recipe)).check(matches(withText(RECIPE_INTRODUCTION)));
    }
}
