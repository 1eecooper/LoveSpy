package com.example.skyspy.app.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStatus {

    private static NetworkStatus instance = new NetworkStatus();
    static Context mContext;
    ConnectivityManager connectivityManager;
    boolean connected = false;

    public static NetworkStatus getInstance(Context ctx) {
        mContext = ctx;
        return instance;
    }

    public boolean isOnline() {
        try {
            connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected =
                    networkInfo != null &&
                    networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connected;
    }
}
