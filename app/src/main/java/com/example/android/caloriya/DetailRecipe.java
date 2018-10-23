package com.example.android.caloriya;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.caloriya.Data.Recipe;
import com.example.android.caloriya.Database.RecipesProvider;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailRecipe extends AppCompatActivity{

    private static final String RECIPE_OBJECT = "recipe";
    private Recipe mRecipe;
    private String mRecipeName;
    private String imageUrl;
    private String mStepsLink;
    private String mSource;
    private String caloriesStr;
    private int servingCalories;
    private ArrayList<String> mIngredients;

    private TextView recipeName;
    private TextView ingredients;
    private ImageView recipeImage;
    private TextView recipeLink;
    private TextView recipeSource;
    private TextView calories;
    private AdView mAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_detail);

        recipeImage = findViewById(R.id.detail_image);
        recipeName = findViewById(R.id.detail_recipe_name);
        ingredients = findViewById(R.id.ingredients_detail);
        recipeLink = findViewById(R.id.steps_website);
        recipeSource = findViewById(R.id.website_source);
        calories = findViewById(R.id.serving_calories);
        mAdView = findViewById(R.id.adView);

        Intent intent = getIntent();
        if(intent != null){
            if(intent.hasExtra(RECIPE_OBJECT)){
                mRecipe = (Recipe) getIntent().getParcelableExtra(RECIPE_OBJECT);
                imageUrl = mRecipe.getImage();
                mRecipeName = mRecipe.getName().replace("\u0092", "\'");
                Picasso.with(this).load(imageUrl).error(R.drawable.android_error)
                        .into(recipeImage);
                recipeName.setText(mRecipeName);
                mIngredients = mRecipe.getmRecipeIngredients();
                for(String ingredient : mIngredients){
                    ingredients.append(ingredient + "\n");
                }
                mStepsLink = mRecipe.getmRecipeSteps();
                mSource = mRecipe.getSource();
                servingCalories = (int)(mRecipe.getCalories()/mRecipe.getServings());
                if(servingCalories <= 0){
                    caloriesStr = this.getResources().getString(R.string.unspecified);
                }else{
                    caloriesStr = Integer.toString(servingCalories);
                }
                calories.append(caloriesStr);
                recipeLink.setText(mStepsLink);
                recipeSource.append(mSource);
            }
            DealWithFavoriteButton();
        }

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

    public void DealWithFavoriteButton() {
        final ContentValues values = new ContentValues();
        values.put(RecipesProvider.RECIPE_ID, mRecipe.getId());
        values.put(RecipesProvider.RECIPE_NAME, mRecipe.getName());

        final Uri SingleUri = Uri.parse(RecipesProvider.CONTENT_URI.toString()).buildUpon()
                .appendPath(mRecipe.getId())
                .build();

        Cursor cursor = getContentResolver()
                .query(SingleUri, null,null,null, null);

        final FloatingActionButton floatButton = findViewById(R.id.fab);
        if (cursor.moveToFirst()) {
            floatButton.setImageResource(R.drawable.baseline_thumb_down_black_18dp);
        }else {
            floatButton.setImageResource(R.drawable.baseline_thumb_up_black_18dp);
        }
        floatButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    Cursor cursor = getContentResolver().query(SingleUri, null,
                            null,null, null);
                    if (cursor.moveToFirst()) {
                        getContentResolver().delete(SingleUri, null,null);
                        floatButton.setImageResource(R.drawable.baseline_thumb_up_black_18dp);
                    } else {
                        getContentResolver().insert(RecipesProvider.CONTENT_URI, values);
                        floatButton.setImageResource(R.drawable.baseline_thumb_down_black_18dp);
                    }
                    cursor.close();
                    RecipeWidgetProvider.sendRefreshBroadcast(getApplicationContext());
                } catch (Exception e) {
                    System.out.println("Error:"+e);
                }
            }
        });
        cursor.close();
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
