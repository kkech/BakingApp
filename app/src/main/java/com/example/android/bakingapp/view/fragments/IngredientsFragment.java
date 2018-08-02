package com.example.android.bakingapp.view.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.view.adapters.IngredientsAdapter;
import com.example.android.bakingapp.DetailsActivity;
import com.example.android.bakingapp.model.dto.Ingredient;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.utils.ItemDecoration;
import com.example.android.bakingapp.utils.JsonUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Ingredient>> {

    private static final String LOG_TAG = IngredientsFragment.class.getSimpleName();

    @BindView(R.id.content_ingredients)
    RecyclerView mRecyclerView;

    private IngredientsAdapter ingredientsAdapter;

    private static final int INGREDIENTS_LOADER_ID = 3;

    private String ingredientsJSON;

    private static final String RECIPE_INGREDIENTS_EXTRA = "Recipe Ingredients";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((DetailsActivity) getActivity()).setActionBarTitle(getContext().getResources().getString(R.string.recipe_ingredients_activity));
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            ingredientsJSON = bundle.getString(RECIPE_INGREDIENTS_EXTRA);
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredients, container, false);
        ButterKnife.bind(this, view);

        ArrayList<Ingredient> mIngredients = new ArrayList<>();
        ingredientsAdapter = new IngredientsAdapter(getContext(), mIngredients);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_view_margin_value);
        mRecyclerView.addItemDecoration(new ItemDecoration(spacingInPixels));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(ingredientsAdapter);
        LoaderManager.LoaderCallbacks<ArrayList<Ingredient>> callback = this;
        getActivity().getSupportLoaderManager().initLoader(INGREDIENTS_LOADER_ID, null, callback);

        return view;
    }

    @Override
    public void onDestroy() {
        ((DetailsActivity) getActivity()).setActionBarTitle(getContext().getResources().getString(R.string.details_activity));
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((DetailsActivity) getActivity()).setActionBarTitle(getContext().getResources().getString(R.string.recipe_ingredients_activity));

        if(RecipeStepFragment.changed){
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
            mRecyclerView.setPadding(0, ((DetailsActivity)getActivity()).getActionBarHeight() + 3 * padding, 0, 0);
        }
    }

    @NonNull
    @Override
    public Loader<ArrayList<Ingredient>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<ArrayList<Ingredient>>(getContext()) {

            ArrayList<Ingredient> ingredients = null;

            @Override
            protected void onStartLoading() {
                if (ingredients != null) {
                    ingredients = null;
                } else {
                    forceLoad();
                }
            }

            @Override
            public ArrayList<Ingredient> loadInBackground() {
                try {
                    return JsonUtils.getIngredientsRecipe(ingredientsJSON);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Ingredient>> loader, ArrayList<Ingredient> data) {
        ingredientsAdapter.setIngredientData(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Ingredient>> loader) {
        ingredientsAdapter.setIngredientData(null);
    }
}
