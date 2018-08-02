package com.example.android.bakingapp.widgets.appWidgetProvider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.example.android.bakingapp.model.dto.Ingredient;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.widgets.services.GridIngredientsWidgetService;
import com.example.android.bakingapp.widgets.services.GridRecipesWidgetService;
import com.example.android.bakingapp.utils.JsonUtils;

import org.json.JSONException;

import java.util.ArrayList;

public class RecipesWidgetProvider extends AppWidgetProvider {

    private static final String LOG_TAG = RecipesWidgetProvider.class.getSimpleName();

    public static final String PRESS_ACTION = "com.example.android.bakingapp.PRESS_RECIPE";
    private static final String LAUNCH_ACTION = "com.example.android.bakingapp.LAUNCH_ACTION";
    public static final String INGREDIENTS_EXTRA = "com.example.android.bakingapp.INGREDIENTS_EXTRA";
    private static final String mOnClick = "On click";

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mOnClick.equals(intent.getAction())){
            Intent i = new Intent();
            i.setClassName("com.example.android.bakingapp", "com.example.android.bakingapp.MainActivity");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        if (intent.getAction().equals(LAUNCH_ACTION)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            String ingredientsJsonStr = intent.getStringExtra(PRESS_ACTION);
            try {
                ArrayList<String> allIngredients = getIngredientsFromArrayList(ingredientsJsonStr);
                Intent newIntent = new Intent(context, GridIngredientsWidgetService.class);
                newIntent.setAction(""+System.currentTimeMillis());
                newIntent.putStringArrayListExtra(INGREDIENTS_EXTRA, allIngredients);
                newIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                newIntent.setData(Uri.parse(newIntent.toUri(Intent.URI_INTENT_SCHEME)));
                RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);
                rv.setRemoteAdapter(appWidgetId, R.id.widget_ingredients_grid_view, newIntent);
                AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, RecipesWidgetProvider.class),rv);
                context.stopService(newIntent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the widgets with the remote adapter
        for (int appWidgetId : appWidgetIds) {

            Intent intent = new Intent(context, GridRecipesWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);
            rv.setRemoteAdapter(appWidgetId, R.id.widget_recipes_grid_view, intent);
            rv.setOnClickPendingIntent(R.id.baking_recipes_widget_button, getPendingSelfIntent(context, mOnClick));

            Intent toastIntent = new Intent(context, RecipesWidgetProvider.class);
            toastIntent.setAction(RecipesWidgetProvider.LAUNCH_ACTION);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.widget_recipes_grid_view, toastPendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private ArrayList<String> getIngredientsFromArrayList(String ingredientsJson) throws JSONException {
        ArrayList<Ingredient> ingredients = JsonUtils.getIngredientsRecipe(ingredientsJson);
        ArrayList<String> ingredientsList = new ArrayList<>();
        for(int i=0;i<ingredients.size();i++){
               ingredientsList.add(ingredients.get(i).toString());
        }
        return ingredientsList;
    }

    private PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

}
