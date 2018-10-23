package com.example.android.caloriya.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;

public class NetworkCheck {
    private Context mContext;
    private View myView;

    public NetworkCheck (Context context, View view){
        mContext = context;
        myView = view;
    }

    //verify network availability
    public Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    //Check if online
    public Boolean isOnline() {
        try {
            Process processing =
                    java.lang.Runtime.getRuntime().exec("ping -c 1 www.edamam.com");
            int returnVal = processing.waitFor();
            return (returnVal == 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //simple Snackbar to inform the user of the network connectivity
    public void showErrorMessage() {
        int snackBarDuration = 8000;
        Snackbar snackbar = Snackbar
                .make(myView.getRootView(), "You are not connected," +
                        " please check your connection", snackBarDuration);
        snackbar.show();
    }


}
