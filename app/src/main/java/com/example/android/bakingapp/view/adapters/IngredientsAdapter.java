package com.example.android.bakingapp.view.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.model.dto.Ingredient;
import com.example.android.bakingapp.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    private static final String LOG_TAG = IngredientsAdapter.class.getSimpleName();

    private final Context mContext;
    private ArrayList<Ingredient> mIgredients;

    public IngredientsAdapter(Context context, ArrayList<Ingredient> ingredients){
        mContext = context;
        mIgredients = ingredients;
    }

    @Override
    public IngredientsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v;
        if (viewType == 1) {
            //inflate with header item
            v = LayoutInflater.from(mContext).inflate(R.layout.list_item_ingredients_header,parent,false);
        } else {
            //inflate with ingredient item
            v = LayoutInflater.from(mContext).inflate(R.layout.list_item_ingredient,parent,false);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        if(position > 0) {
            String ingredient = mIgredients.get(position-1).getIngredient();
            String measure = mIgredients.get(position-1).getMeasure();
            double quantity = mIgredients.get(position-1).getQuantity();
            String quantityStr = String.valueOf(quantity);
            String clearQuantityStr = !quantityStr.contains(".") ? quantityStr : quantityStr.replaceAll("0*$", "").replaceAll("\\.$", "");
            holder.ingredientTextView.setText(ingredient);
            holder.measureTextView.setText(measure);
            holder.quantityTextView.setText(clearQuantityStr);
        }
    }

    @Override
    public int getItemCount() {
        if(mIgredients == null)
            return 0;
        return mIgredients.size()+1;
    }

    public void setIngredientData(ArrayList<Ingredient> ingredients) {
        mIgredients = ingredients;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 1;
        else return 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Nullable
        @BindView(R.id.ingredient_text_view) TextView ingredientTextView;
        @Nullable
        @BindView(R.id.measure_text_view) TextView measureTextView;
        @Nullable
        @BindView(R.id.quantity_text_view) TextView quantityTextView;

        ViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}