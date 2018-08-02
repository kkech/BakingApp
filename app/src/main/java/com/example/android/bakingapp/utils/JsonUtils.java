package com.example.android.bakingapp.utils;

import android.util.Log;

import com.example.android.bakingapp.model.dto.DetailRecipe;
import com.example.android.bakingapp.model.dto.Ingredient;
import com.example.android.bakingapp.model.dto.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class JsonUtils {

    private static final String LOG_TAG = JsonUtils.class.getSimpleName();

    private static final String NAME_TAG = "name";
    private static final String IMAGE_TAG = "image";
    private static final String INGREDIENTS_TAG = "ingredients";
    private static final String STEPS_TAG = "steps";

    /**
     * Get @{@link ArrayList} of @{@link Recipe} from @{@link String} formatted as JSON
     *
     * @param recipesJSONStr - The @{@link String} formatted as JSON
     * @return - An {@link ArrayList} of {@link Recipe}
     * @throws JSONException
     */
    public static ArrayList<Recipe> JSONtoArrayList(String recipesJSONStr) throws JSONException {
        Log.i(LOG_TAG,"Parsing Recipe from JSON");
        ArrayList<Recipe> recipes = new ArrayList<>();
        JSONArray mainObject = new JSONArray(recipesJSONStr);
        for(int i=0;i<mainObject.length();i++){
            JSONObject recipeObject = mainObject.getJSONObject(i);
            String name = recipeObject.getString(NAME_TAG);
            String image = recipeObject.getString(IMAGE_TAG);
            JSONArray ingredients = recipeObject.getJSONArray(INGREDIENTS_TAG);
            JSONArray steps = recipeObject.getJSONArray(STEPS_TAG);

            Recipe recipe = new Recipe(name,image,ingredients.toString(),steps.toString());
            recipes.add(recipe);
        }
        return recipes;
    }

    private static final String STEP_TITLE_TAG = "shortDescription";
    private static final String STEP_DESCRIPTION_TAG = "description";
    private static final String STEP_VIDEO_TAG = "videoURL";

    /**
     * Get @{@link ArrayList} of @{@link DetailRecipe} from @{@link String} formatted as JSON.
     *
     * @param stepsJSONStr - The @{@link String} formatted as JSON
     * @return - An {@link ArrayList} of {@link DetailRecipe}
     * @throws JSONException
     */
    public static ArrayList<DetailRecipe> getStepsRecipe(String stepsJSONStr) throws JSONException {
        Log.i(LOG_TAG,"Parsing Recipe Steps from JSON");
        ArrayList<DetailRecipe> detailRecipes = new ArrayList<>();
        JSONArray mainObject = new JSONArray(stepsJSONStr);
        for(int i=0;i<mainObject.length();i++){
            JSONObject recipeObject = mainObject.getJSONObject(i);
            String stepTitle = recipeObject.getString(STEP_TITLE_TAG);
            String stepInstructions = recipeObject.getString(STEP_DESCRIPTION_TAG);
            String stepVideo = recipeObject.getString(STEP_VIDEO_TAG);

            DetailRecipe detail = new DetailRecipe(stepTitle,stepVideo,stepInstructions);
            detailRecipes.add(detail);
        }
        return detailRecipes;
    }

    private static final String INGREDIENT_NAME_TAG = "ingredient";
    private static final String INGREDIENT_MEASURE_TAG = "measure";
    private static final String INGREDIENT_QUANTITY_TAG = "quantity";

    /**
     * Get @{@link ArrayList} of @{@link Ingredient} from @{@link String} formatted as JSON.
     *
     * @param ingredientsJSONStr - The @{@link String} formatted as JSON
     * @return - An {@link ArrayList} of {@link Ingredient}
     * @throws JSONException
     */
    public static ArrayList<Ingredient> getIngredientsRecipe(String ingredientsJSONStr) throws JSONException {
        Log.i(LOG_TAG,"Parsing Recipe Ingredients from JSON");
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        JSONArray mainObject = new JSONArray(ingredientsJSONStr);
        for(int i=0;i<mainObject.length();i++){
            JSONObject ingredientObject = mainObject.getJSONObject(i);
            String ingredientName = ingredientObject.getString(INGREDIENT_NAME_TAG);
            String measure = ingredientObject.getString(INGREDIENT_MEASURE_TAG);
            double quantity = ingredientObject.getDouble(INGREDIENT_QUANTITY_TAG);

            Ingredient ingredient = new Ingredient(ingredientName,measure,quantity);
            ingredients.add(ingredient);
        }
        return ingredients;
    }
}
