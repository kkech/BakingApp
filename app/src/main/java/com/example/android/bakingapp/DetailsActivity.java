package com.example.android.bakingapp;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.example.android.bakingapp.view.fragments.DetailsFragment;
import com.example.android.bakingapp.view.fragments.IngredientsFragment;
import com.example.android.bakingapp.view.fragments.RecipeStepFragment;
import com.example.android.bakingapp.model.dto.DetailRecipe;
import com.example.android.bakingapp.model.dto.Recipe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements DetailsFragment.OnRecipeDetailSelected,RecipeStepFragment.onRecipeNextPreviousSelected,FragmentManager.OnBackStackChangedListener {

    private static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    @Nullable
    @BindView(R.id.content_details_two_pane)
    LinearLayout twoPaneView;

    private Recipe recipe;

    private static final String RECIPE_EXTRA = "Recipe";

    private static final String RECIPE_STEPS_EXTRA = "Recipe Steps";

    private static final String RECIPE_STEPS_POSITION_EXTRA = "Recipe Step Position";

    private static final String RECIPE_INGREDIENTS_EXTRA = "Recipe Ingredients";

    public static boolean twoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        if(savedInstanceState != null) {
            if (savedInstanceState.get("No video") == null) {
                return;
            }
        }

        getSupportActionBar().setTitle(R.string.details_activity);

        //get recipe's info
        recipe = getIntent().getParcelableExtra(RECIPE_EXTRA);

        twoPane = twoPaneView != null && twoPaneView.getVisibility() == View.VISIBLE;
        // set the orientation of the device
        if (twoPane) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(RECIPE_EXTRA, recipe);

        DetailsFragment frag = new DetailsFragment();
        frag.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (!twoPane) {
            fragmentManager.beginTransaction().add(R.id.content_details_frag, frag).commit();
        } else {
            fragmentManager.beginTransaction().add(R.id.recipe_details, frag).commit();
        }


    }

    public void onRecipeDetailSelected(int position,ArrayList<DetailRecipe> detailRecipes){
        if(!twoPane) {
            if (position > 0) {
                Bundle args = new Bundle();
                args.putParcelableArrayList(RECIPE_STEPS_EXTRA, detailRecipes);
                args.putInt(RECIPE_STEPS_POSITION_EXTRA, position - 1);

                RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
                recipeStepFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_details_frag, recipeStepFragment).addToBackStack(null).commit();
            } else {
                Bundle args = new Bundle();
                args.putString(RECIPE_INGREDIENTS_EXTRA, recipe.getRecipeIngredients());

                IngredientsFragment ingredientsFragment = new IngredientsFragment();
                ingredientsFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_details_frag, ingredientsFragment).addToBackStack(null).commit();
            }
        }else{
            if (position > 0) {
                Bundle args = new Bundle();
                args.putParcelableArrayList(RECIPE_STEPS_EXTRA, detailRecipes);
                args.putInt(RECIPE_STEPS_POSITION_EXTRA, position - 1);

                RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
                recipeStepFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.recipe_info, recipeStepFragment).addToBackStack(null).commit();
            } else {
                Bundle args = new Bundle();
                args.putString(RECIPE_INGREDIENTS_EXTRA, recipe.getRecipeIngredients());

                IngredientsFragment ingredientsFragment = new IngredientsFragment();
                ingredientsFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.recipe_info, ingredientsFragment).addToBackStack(null).commit();
            }
        }
    }

    @Override
    public void onBackStackChanged() {
    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        getSupportFragmentManager().popBackStack();
        boolean hasBackStack = getSupportFragmentManager().getBackStackEntryCount() > 0;
        if(!hasBackStack){
            NavUtils.navigateUpFromSameTask(this);
        }
        return true;
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void showActionBar(){
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }
    }

    public void hideActionBar(){
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void onRecipeNextPreviousSelected(int position,ArrayList<DetailRecipe> detailRecipes){
        Bundle args = new Bundle();
        args.putParcelableArrayList(RECIPE_STEPS_EXTRA, detailRecipes);
        args.putInt(RECIPE_STEPS_POSITION_EXTRA, position);

        RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
        recipeStepFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_details_frag, recipeStepFragment).addToBackStack(null).commit();
    }

    public int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv,
                    true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data, getResources().getDisplayMetrics());
        } else {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                    getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(recipe != null) {
            outState.putString(RECIPE_INGREDIENTS_EXTRA, recipe.getRecipeIngredients());
        }
    }
}