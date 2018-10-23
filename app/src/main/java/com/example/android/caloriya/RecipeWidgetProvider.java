package com.example.android.caloriya;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class RecipeWidgetProvider extends AppWidgetProvider {

    private static final String MY_RECIPES = "myRecipes";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_listview);

        // onClickListeners
        Intent titleIntent = new Intent(context, MainActivity.class);
        titleIntent.putExtra(MY_RECIPES, 1);

        PendingIntent myRecipePendingIntent = PendingIntent.getActivity(context, 0,
                titleIntent, 0);
        views.setOnClickPendingIntent(R.id.myRecipe_widget_title, myRecipePendingIntent);

        Intent intent = new Intent(context, WidgetRemoteViewsService.class);
        views.setRemoteAdapter(R.id.widget_listview, intent);

        // Instruct the widget manager to update the widget
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_listview);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // function not used but must be implemented as part of AppWidgetProvider
    }

    @Override
    public void onDisabled(Context context) {
        // function not used but must be implemented as part of AppWidgetProvider
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            // refresh all widgets
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, RecipeWidgetProvider.class);
            onUpdate(context, mgr, mgr.getAppWidgetIds(cn));
        }
        super.onReceive(context,intent);
    }

    // Send a brodcast to widgetProvider.
    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, RecipeWidgetProvider.class));
        context.sendBroadcast(intent);
    }
}

