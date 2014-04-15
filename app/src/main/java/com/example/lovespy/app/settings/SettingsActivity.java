package com.example.lovespy.app.settings;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {

    public static String KEY_PREF_ADDRESSEE = "pref_addressee";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
