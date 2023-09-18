package com.example.android.caloriya;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.caloriya.Data.Recipe;
import com.example.android.caloriya.Database.RecipesProvider;
import com.example.android.caloriya.Utilities.JsonUtil;
import com.example.android.caloriya.Utilities.NetworkCheck;
import com.example.android.caloriya.Utilities.NetworkUtils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    //for saving menu position key
    private static int mMenuPosition = 0;
    private static final String POSITION_KEY = "menuKey";
    private static final String RECIPE_ARRAY = "recipeArray";
    private static final String CALORIES_TOTAL = "caloriesTotal";
    private static final String COOK_TIME = "cookTime";
    private static final String TEXT_COLOR_POPULAR = "colorPopular";
    private static final String TEXT_COLOR_FAVORITE = "colorFavorite";
    private static final String SCREEN_NAME = "recipes";
    private static final String SAVE_CALORIES = "caloriesSave";
    private static final String LAST_SAVED_CALORIES = "lastSavedCalories";


    //for saving recyclerView scroll position
    private static final String SCROLLED_POSITION = "scrolled_position";
    private int mScrolledPosition = 0;

    private Spinner spinner;
    private RecyclerView mRecyclerView;
    private GridLayoutManager gridManager;
    private RecipeAdapter mRecipeAdapter;
    private TextView mCaloriesTotal;
    private TextView title;
    private TextView myRecipeLink;
    private ProgressBar progressBar;

    private ArrayList<Recipe> mRecipes;
    private String totalCal = "";
    private String totalTime = "";
    private int mColorFavorite;
    private int mColorPopular;
    private Tracker mTracker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSpinner();
        RecipeWidgetProvider.sendRefreshBroadcast(getApplicationContext());

        mCaloriesTotal = findViewById(R.id.calories_value);
        mRecyclerView = findViewById(R.id.recycler_view);
        title = findViewById(R.id.title);
        mRecyclerView.setHasFixedSize(false);
        myRecipeLink = findViewById(R.id.favorites_link);

        // call Google Analytics
        AnalyticsRecipe application = new AnalyticsRecipe();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(SCREEN_NAME);

        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        Intent intent = this.getIntent();

        //check if activity is started from widget
        if(intent.hasExtra("myRecipes")){
            mMenuPosition = 1;
        }

        //clear keyboard on scroll for screen visibility 
        mRecyclerView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                clearKeyboard(v);
                return false;
            }
        } );

        final int columnsNum = getResources().getInteger(R.integer.gallery_columns);
        gridManager = new GridLayoutManager(MainActivity.this, columnsNum);
        mRecyclerView.setLayoutManager(gridManager);
        mRecipeAdapter = new RecipeAdapter(this);
        mRecyclerView.setAdapter(mRecipeAdapter);
        if (savedInstanceState != null) {
            totalCal = savedInstanceState.get(CALORIES_TOTAL).toString();
            totalTime = savedInstanceState.getString(COOK_TIME);
            mMenuPosition = savedInstanceState.getInt(POSITION_KEY);
            mScrolledPosition = savedInstanceState.getInt(SCROLLED_POSITION);
            mRecipes = savedInstanceState.getParcelableArrayList(RECIPE_ARRAY);
            mColorFavorite = savedInstanceState.getInt(TEXT_COLOR_FAVORITE);
            mColorPopular = savedInstanceState.getInt(TEXT_COLOR_POPULAR);
            mRecipeAdapter.setRecipeData(mRecipes);
            mCaloriesTotal.setText(totalCal);
            title.setTextColor(mColorPopular);
            myRecipeLink.setTextColor(mColorFavorite);
        }else{
            loadRecipeData(mMenuPosition, totalTime);
        }
    }

    private void loadRecipeData(int recipeItemKey, String time){
        String recipeUrl = getResources().getString(R.string.recipe_url);
        totalTime = time;
        mMenuPosition = recipeItemKey;
        ArrayList<URL> recipeIdUrl = new ArrayList<URL>();
        switch (mMenuPosition){
            case 0:
                mColorPopular = getColorText(this,R.color.colorAccent);
                title.setTextColor(mColorPopular);
                mColorFavorite = getColorText(this,R.color.colorWhite);
                myRecipeLink.setTextColor(mColorFavorite);
                recipeUrl = getResources().getString(R.string.recipe_url);
                recipeIdUrl.add(NetworkUtils.buildUrl(recipeUrl, time));
                checkNetwork(recipeIdUrl);
                break;
            case 1:
                mColorFavorite = getColorText(this,R.color.colorAccent);
                myRecipeLink.setTextColor(mColorFavorite);
                mColorPopular = getColorText(this,R.color.colorWhite);
                title.setTextColor(mColorPopular);
                getLoaderManager().initLoader(0, null, this);
                break;
            default:
                break;
        }
    }
        
    private void checkNetwork(ArrayList<URL> recipeIdUrl) {
        NetworkCheck check = new NetworkCheck(this,mRecyclerView);
        if (check.isNetworkAvailable() && check.isOnline()){
            new recipeQueryTask().execute(recipeIdUrl);
        }else{
            check.showErrorMessage();
        }
    }

    private static int getColorText(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    private static void clearKeyboard(@NonNull View v){
        InputMethodManager inputMethodManager = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    // used based on studies on https://transformfxfitness.com/calculating-your-calorie-deficit
    // this is just an estimate “quick and easy” method (does not include other factors),
    public void calculateCalories(View view){
        String spinnerSelection;
        String weightText;
        String [] goalChoices = this.getApplication().getResources()
                .getStringArray(R.array.goal_array);
        int weight;
        String caloriesMin;
        String caloriesMax;
        int multiplierMin = 0;
        int multiplierMax = 0;

        EditText mWeight = findViewById(R.id.weight_field);

        weightText = mWeight.getText().toString();
        if(weightText.isEmpty() || weightText.length() > 4){
            weightText = "0";
        }
        weight = Integer.parseInt(weightText);
        spinnerSelection = spinner.getSelectedItem().toString();


        if (!(weight <= 0)) {
            if (spinnerSelection.equals(goalChoices[0])) {
                multiplierMin = 12;
                multiplierMax = 13;
            } else if (spinnerSelection.equals(goalChoices[1])) {
                multiplierMin = 15;
                multiplierMax = 16;
            } else if (spinnerSelection.equals(goalChoices[2])) {
                multiplierMin = 18;
                multiplierMax = 19;
            }
            caloriesMin = Integer.toString(multiplierMin * weight);
            caloriesMax = Integer.toString(multiplierMax * weight);
            totalCal = caloriesMin + " - " + caloriesMax + " Cal (est. for "
                    + weightText + " lbs active adult)";
            mCaloriesTotal.setText(totalCal);
        }else{
            totalCal = getResources().getString(R.string.error_message);
            mCaloriesTotal.setText(totalCal);
            return;
        }

        //clear focus and keyboard
        mWeight.clearFocus();
        clearKeyboard(mWeight);
        // save last calories result
        SharedPreferences sharedPref = this.getSharedPreferences(LAST_SAVED_CALORIES,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SAVE_CALORIES, totalCal);
        editor.commit();
    }

    public void filterResultByTime(View view){

        mMenuPosition = 0;
        int minutesToSeconds;
        int minutes;
        String minutesStr ="";
        EditText timeEdit = findViewById(R.id.calories_field);
        minutesStr = timeEdit.getText().toString();
        if (minutesStr.isEmpty()){
            minutesStr ="0";
        }

        minutes = Integer.parseInt(minutesStr);
        minutesToSeconds = (int)(minutes * 60);
        totalTime = Integer.toString(minutesToSeconds);

        if(!(minutesToSeconds <= 0)){
            loadRecipeData(mMenuPosition, totalTime);
        }

        //clear text and keyboard
        timeEdit.getText().clear();
        clearKeyboard(timeEdit);
    }

    public void displayFavoriteRecipes(View view){

        mMenuPosition = 1;
        String time = "";
        loadRecipeData(mMenuPosition, time);
    }

    public void displayPopularRecipes(View view){

        mMenuPosition = 0;
        String time = "";
        loadRecipeData(mMenuPosition, time);
    }

    private void setSpinner(){
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.goal_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION_KEY, mMenuPosition);
        outState.putInt(SCROLLED_POSITION, ((GridLayoutManager)
                mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition());
        outState.putString(COOK_TIME, totalTime);
        outState.putString(CALORIES_TOTAL, totalCal);
        outState.putInt(TEXT_COLOR_FAVORITE, mColorFavorite);
        outState.putInt(TEXT_COLOR_POPULAR, mColorPopular);
        outState.putParcelableArrayList(RECIPE_ARRAY, mRecipes);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = new CursorLoader(
                this,
                RecipesProvider.CONTENT_URI,
                null,
                null,
                null,
                null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ArrayList<URL> recipeIdUrl = new ArrayList<URL>();
        String recipeUrl;
        String time = "";
        if (cursor.moveToFirst()){
            do{
                String id = cursor.getString(0);
                recipeUrl = getResources().getString(R.string.recipe_detail_url) + id + "?";
                recipeIdUrl.add(NetworkUtils.buildUrl(recipeUrl, time));
            }while (cursor.moveToNext());
        }
        //check network connection
        checkNetwork(recipeIdUrl);
        getLoaderManager().destroyLoader(0);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private class recipeQueryTask extends AsyncTask<ArrayList <URL>, Void,ArrayList<String>>{
        @Override
        protected void onPreExecute() {
            progressBar = findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(ArrayList<URL>... urls) {
            if(urls == null){
                return null;
            }
            ArrayList<String> result = new ArrayList<String>();
            ArrayList<URL>urlArray = urls[0];
            String urlCheck = urls[0].toString();
            String resultStr;
            int num = 1;

            if (urlCheck.contains("/recipes?")){
                num = 0;
            }
            for (URL searchUrl: urlArray) {
                try{
                    if(num == 0){
                        resultStr = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                        result = buildUrlWithId(resultStr,MainActivity.this);
                    }else {
                        result.add(NetworkUtils.getResponseFromHttpUrl(searchUrl));
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<String> resultArray) {
            if(mRecipes != null){
                mRecipes.clear();
            }
            if (resultArray != null && !resultArray.isEmpty()) {
                mRecipes = JsonUtil.parseRecipefromJson(resultArray);
                if(mRecipes.size() != resultArray.size()){
                    mRecipes.clear();
                    mRecipes = JsonUtil.parseRecipefromJson(resultArray);
                }
            }
            progressBar.setVisibility(View.INVISIBLE);
            mRecipeAdapter.setRecipeData(mRecipes);
            if (mScrolledPosition != 0) {
                mRecyclerView.scrollToPosition(mScrolledPosition);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences savedCal = this.getSharedPreferences(LAST_SAVED_CALORIES, 0);
        totalCal = savedCal.getString(SAVE_CALORIES, "");
        mCaloriesTotal.setText(totalCal);
        if(mRecipes != null && mMenuPosition == 1){
            Cursor cursor = getContentResolver().query(
                    RecipesProvider.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
            if(cursor.getCount() != mRecipes.size()){
                loadRecipeData(mMenuPosition, "");
            }
        }
    }

    public static ArrayList<String> buildUrlWithId(String resultStr, Context context){
        URL recipeUrl = null;
        ArrayList<String> returnStr = new ArrayList<>();
        try{
            JSONObject responseObj = new JSONObject(resultStr);
            JSONArray array = new JSONArray();
            array = responseObj.getJSONArray("matches");
            for(int i = 0; i < array.length(); i++){
                String id = array.getJSONObject(i).optString("id");

                String recipeIdUrl = (context.getResources()
                        .getString(R.string.recipe_detail_url)) + id + "?";
                recipeUrl = NetworkUtils.buildUrl(recipeIdUrl, "");
                try{
                    returnStr.add(NetworkUtils.getResponseFromHttpUrl(recipeUrl));
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return returnStr;
    }
}
