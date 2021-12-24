package org.dd1929.apod.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

    public static boolean hasInternet(Context context){
        boolean internet = true;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null){
            internet = false;
        } else if (!activeNetwork.isConnectedOrConnecting()){
            internet = false;
        }
        return internet;
    }

}
