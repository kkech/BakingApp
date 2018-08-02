package com.example.android.bakingapp.model.dto;

public class WidgetItem {
    private String recipeName;

    public WidgetItem(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }
}
