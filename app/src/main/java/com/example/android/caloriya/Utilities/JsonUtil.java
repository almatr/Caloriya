package com.example.android.caloriya.Utilities;

import com.example.android.caloriya.Data.Recipe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class JsonUtil {

    // Define strings to use in recipe object creation
    private static final String ID = "id";
    private static final String RECIPE_NAME = "name";
    private static final String RECIPE_IMAGE = "images";
    private static final String RECIPE_IMAGE_URL = "hostedMediumUrl";
    private static final String PREP_TIME = "totalTime";
    private static final String SOURCE_NAME = "source";
    private static final String SOURCE = "sourceDisplayName";
    private static final String INGREDIENTS = "ingredientLines";
    private static final String SERVINGS = "numberOfServings";
    private static final String CALORIES = "nutritionEstimates";
    private static final String CALORIES_VALUE = "value";
    private static final String STEPS = "sourceRecipeUrl";

    //ArrayList of Recipe class to store recipe objects
    public static ArrayList<Recipe> recipeArray = new ArrayList<>();

    // Function taking JSON ArrayList of string, setting recipe object and adding object into
    // ArrayList of recipe class
    public static ArrayList<Recipe> parseRecipefromJson(ArrayList<String> jsonResults){
        try {
            JSONArray ingredients = new JSONArray();
            JSONArray recipeImg = new JSONArray();
            JSONArray calories = new JSONArray();
            JSONObject source = new JSONObject();

            for(String jsonResult: jsonResults){
                Recipe recipeObj = new Recipe();
                JSONObject recipeJson = new JSONObject(jsonResult);
                source = recipeJson.getJSONObject(SOURCE_NAME);
                ingredients = recipeJson.optJSONArray(INGREDIENTS);
                recipeImg = recipeJson.optJSONArray(RECIPE_IMAGE);
                recipeObj.setId(recipeJson.optString(ID));
                recipeObj.setName(recipeJson.optString(RECIPE_NAME));
                recipeObj.setPrepTime(recipeJson.optString(PREP_TIME));
                recipeObj.setImage(recipeImg.optJSONObject(0).optString(RECIPE_IMAGE_URL));
                recipeObj.setServings(recipeJson.optInt(SERVINGS));
                recipeObj.setSource(source.optString(SOURCE));
                recipeObj.setmRecipeSteps(source.optString(STEPS));
                // Some of the recipe don't have calories set.
                try {
                    calories = recipeJson.optJSONArray(CALORIES);
                    recipeObj.setCalories(calories.getJSONObject(0)
                            .optDouble(CALORIES_VALUE));
                } catch (JSONException e){
                    recipeObj.setCalories(0.0);
                }
                recipeObj.setmRecipeIngredients(createIngredientArray(ingredients));
                recipeArray.add(recipeObj);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return recipeArray;
    }

    public static ArrayList<String> createIngredientArray(JSONArray ingredientsJson){
        ArrayList<String> ingredientList = new ArrayList<String >();
        try{
            for(int i = 0; i < ingredientsJson.length(); i++){
                ingredientList.add(ingredientsJson.getString(i));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return ingredientList;
    }
}
