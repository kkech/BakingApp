package com.example.android.bakingapp.view.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.android.bakingapp.model.dto.DetailRecipe;
import com.example.android.bakingapp.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailRecipeAdapter extends RecyclerView.Adapter<DetailRecipeAdapter.ViewHolder> {

    private static final String LOG_TAG = RecipeAdapter.class.getSimpleName();

    private final Context mContext;
    private ArrayList<DetailRecipe> mDetailRecipes;
    private final DetailRecipeAdapterOnClickHandler mClickHandler;

    public DetailRecipeAdapter(Context context, ArrayList<DetailRecipe> detailRecipes,DetailRecipeAdapterOnClickHandler clickHandler){
        mContext = context;
        mDetailRecipes = detailRecipes;
        mClickHandler = clickHandler;
    }

    public interface DetailRecipeAdapterOnClickHandler {
        void onClick(int position);
    }

    @Override
    public DetailRecipeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v;
        if(viewType == 1){
            v = LayoutInflater.from(mContext).inflate(R.layout.list_item_details_ingredients,parent,false);
        }else{
            v = LayoutInflater.from(mContext).inflate(R.layout.list_item_details,parent,false);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        if(position>0) {
            String recipeStepTitle = mDetailRecipes.get(position-1).getDetailTitle();
            holder.stepRecipe.setText(recipeStepTitle);
        }
    }

    @Override
    public int getItemCount() {
        if(mDetailRecipes == null)
            return 0;
        return mDetailRecipes.size()+1;
    }

    public void setDetailRecipeData(ArrayList<DetailRecipe> detailRecipeData) {
        mDetailRecipes = detailRecipeData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 1;
        else return 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Nullable
        @BindView(R.id.recipe_detail_item) Button stepRecipe;

        ViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickHandler.onClick(position);
        }
    }
}