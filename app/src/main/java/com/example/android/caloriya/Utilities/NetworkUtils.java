package com.example.android.caloriya.Utilities;

import android.net.Uri;

import com.example.android.caloriya.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String apiKey = BuildConfig.THE_GUARDIAN_API_KEY;
    private static final String appId = BuildConfig.THE_GUARDIAN_APP_ID;


    public static URL buildUrl(String recipeUrl, String filterStr){
        Uri buildUri = Uri.parse(recipeUrl).buildUpon()
                .appendQueryParameter("_app_id", appId)
                .appendQueryParameter("_app_key",apiKey)
                .appendQueryParameter("maxTotalTimeInSeconds", filterStr)
                .appendQueryParameter("requirePictures", "true")
                .build();
        URL url = null;
        try {
            url = new URL(buildUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    //Fetch http response and return its contents
    public static String getResponseFromHttpUrl(URL url)throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
