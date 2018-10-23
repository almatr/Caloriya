package com.example.android.caloriya;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.example.android.caloriya.Data.Recipe;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Recipe> mRecipeList;
    private static final String RECIPE_OBJECT = "recipe";

    public RecipeAdapter(Context context){
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView mRecipeImage;
        private TextView mRecipeName;
        private TextView mRecipeTime;

        public ViewHolder (View view){
            super(view);
            mRecipeImage = view.findViewById(R.id.recipe_image);
            mRecipeName = view.findViewById(R.id.recipe_name);
            mRecipeTime = view.findViewById(R.id.recipe_time);
        }
    }

    @NonNull
    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.ViewHolder holder, final int position) {
        String imageUrl = mRecipeList.get(position).getImage();
        String recipeName = mRecipeList.get(position).getName();
        recipeName = recipeName.replace("\u0092", "\'");
        String prepTime = mRecipeList.get(position).getPrepTime();
        if(prepTime.isEmpty()){
            prepTime = mContext.getString(R.string.unknown);
        }
        Picasso.with(mContext).load(imageUrl).error(R.drawable.android_error)
                .into(holder.mRecipeImage);
        holder.mRecipeName.setText(recipeName);
        holder.mRecipeTime.setText("Prep Time: "+ prepTime);
        holder.mRecipeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mContext, DetailRecipe.class);
                mIntent.putExtra(RECIPE_OBJECT, mRecipeList.get(position));
                if(mIntent.resolveActivity(mContext.getPackageManager()) != null){
                    mContext.startActivity(mIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mRecipeList == null) return 0;
        return mRecipeList.size();
    }

    //Setting recipe data
    public void setRecipeData(ArrayList<Recipe> recipes){
        mRecipeList = recipes;
        notifyDataSetChanged();
    }
}
