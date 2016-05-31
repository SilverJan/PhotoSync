package de.jbi.photosync.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import de.jbi.photosync.R;
import de.jbi.photosync.activities.MainActivity;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    Activity activity;

    public static final String KEY_PREF_SERVER_IP = "pref_server_ip";
    public static final String KEY_PREF_SERVER_PORT = "pref_server_port";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_PREF_SERVER_IP) || key.equals(KEY_PREF_SERVER_PORT)) {
            MainActivity mainActivity = (MainActivity) activity;
//            mainActivity.handleHttps();
        }
    }

}
