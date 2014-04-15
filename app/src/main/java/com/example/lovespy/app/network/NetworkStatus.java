package com.example.lovespy.app.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStatus {

    private static NetworkStatus instance = new NetworkStatus();
    private static Context mContext;
    private ConnectivityManager connectivityManager;
    private boolean connected = false;

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
