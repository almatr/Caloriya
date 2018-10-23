package com.example.android.caloriya.Data;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class Recipe implements Parcelable {

    private String id;
    private int servings;
    private String name;
    private String image;
    private double calories;
    private String stepsUrl;
    private String prepTime;
    private String source;
    private ArrayList<String> ingredients = new ArrayList<String>();;

    public Recipe(){}

    //setters

    public void setId(String id){ this.id = id;}

    public void setServings(int servings) {
        this.servings = servings;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public void setmRecipeSteps(String stepsUrl) {
        this.stepsUrl = stepsUrl;
    }

    public void setPrepTime(String time){
        this.prepTime = time;
    }

    public void setSource(String source){
        this.source = source;
    }

    public void setmRecipeIngredients(ArrayList<String> mRecipeIngredients) {
        this.ingredients = mRecipeIngredients;
    }

    //getters
    public String getId() { return id;}

    public int getServings() {
        return servings;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public double getCalories() {
        return calories;
    }

    public String getmRecipeSteps() {
        return stepsUrl;
    }

    public String getPrepTime() {return prepTime;}

    public String getSource() {return source; }

    public ArrayList<String> getmRecipeIngredients() {
        return ingredients;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeInt(this.servings);
        parcel.writeString(this.name);
        parcel.writeString(this.image);
        parcel.writeDouble(this.calories);
        parcel.writeString(this.stepsUrl);
        parcel.writeString(this.prepTime);
        parcel.writeString(this.source);
        parcel.writeStringList(this.ingredients);
    }

    protected Recipe(Parcel in){
        this.id = in.readString();
        this.servings = in.readInt();
        this.name = in.readString();
        this.image = in.readString();
        this.calories = in.readDouble();
        this.stepsUrl = in.readString();
        this.prepTime = in.readString();
        this.source = in.readString();
        this.ingredients = in.createStringArrayList();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}