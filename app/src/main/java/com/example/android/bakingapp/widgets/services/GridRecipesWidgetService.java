package com.example.android.bakingapp.widgets.services;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.widgets.appWidgetProvider.RecipesWidgetProvider;
import com.example.android.bakingapp.model.dto.Recipe;
import com.example.android.bakingapp.model.dto.WidgetItem;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.utils.NetworkUtils;
import com.example.android.bakingapp.utils.JsonUtils;

import java.util.ArrayList;


public class GridRecipesWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent){
        return new RecipesRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class RecipesRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{

    private static final String LOG_TAG = RecipesRemoteViewsFactory.class.getSimpleName();

    private ArrayList<Recipe> recipes;
    private final ArrayList<WidgetItem> items = new ArrayList<>();
    private final Context mContext;

    public RecipesRemoteViewsFactory(Context context, Intent intent){
        mContext = context;
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        try {
            getRecipes();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i=0;i<recipes.size();i++){
            items.add(new WidgetItem(recipes.get(i).getName()));
        }
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
        items.clear();
    }

    @Override
    public int getCount() {
        return recipes.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_recipe_grid_item);
        rv.setTextViewText(R.id.widget_item, items.get(position).getRecipeName());
        Bundle extras = new Bundle();
        extras.putString(RecipesWidgetProvider.PRESS_ACTION,recipes.get(position).getRecipeIngredients());
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void getRecipes() throws InterruptedException {
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    final String jsonResponse = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildUrl());
                    recipes = JsonUtils.JSONtoArrayList(jsonResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
        th.join();
    }
}