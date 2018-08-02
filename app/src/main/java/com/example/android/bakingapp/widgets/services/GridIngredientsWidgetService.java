package com.example.android.bakingapp.widgets.services;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.widgets.appWidgetProvider.RecipesWidgetProvider;
import com.example.android.bakingapp.R;

import java.util.ArrayList;


public class GridIngredientsWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent){
        return new IngredientsRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class IngredientsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{
    private final ArrayList<String> items;
    private static final String LOG_TAG = IngredientsRemoteViewsFactory.class.getSimpleName();
    private final Context mContext;

    public IngredientsRemoteViewsFactory(Context context, Intent intent){
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        items = intent.getStringArrayListExtra(RecipesWidgetProvider.INGREDIENTS_EXTRA);
        mContext = context;
    }

    @Override
    public void onCreate() {

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
        return items.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_ingredient_grid_item);
        rv.setTextViewText(R.id.widget_item, items.get(position));
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
}