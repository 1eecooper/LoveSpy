package com.example.lovespy.app.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.lovespy.app.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
