package com.example.lovespy.app.sms;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsHelper {

    public static final String ALL = "ALL";
    public static final String INBOX = "INBOX";
    public static final String SENT = "SENT";
    public static final String DRAFT = "DRAFT";
    public static final String OUTBOX = "OUTBOX";
    public static final String FAILED = "FAILED";
    public static final String QUEUED = "QUEUED";

    private Context mContext;
    public SmsHelper(Context context) {
        mContext = context;
    }

    public String removeNewLine(String str){
        str = str.replace("\n"," ");
        return str;
    }

    public String convertDate(Long timeMs) {
        Date date = new Date(timeMs);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return df.format(date);
    }

    public String getSmsTypeName(Integer type) {
        String str = "";
        if (type == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_ALL) {
            str = ALL;
        } else if (type == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_INBOX) {
            str = INBOX;
        } else if (type == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT) {
            str = SENT;
        } else if (type == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_DRAFT) {
            str = DRAFT;
        } else if (type == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_OUTBOX) {
            str = OUTBOX;
        } else if (type == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_FAILED) {
            str = FAILED;
        } else if (type == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_QUEUED) {
            str = QUEUED;
        }
        return str;
    }

    public String getNumber(String thread_id) {
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
}
