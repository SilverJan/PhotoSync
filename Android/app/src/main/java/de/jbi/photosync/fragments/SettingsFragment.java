package de.jbi.photosync.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import de.jbi.photosync.R;
import de.jbi.photosync.utils.AndroidUtil;
import de.jbi.photosync.utils.Logger;

import static de.jbi.photosync.utils.BackupAgent.requestAppBackup;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    Activity activity;

    public static final String KEY_PREF_SERVER_IP = "pref_server_ip_edit_text";
    public static final String KEY_PREF_SERVER_PORT = "pref_server_port_edit_text";
    public static final String KEY_PREF_FILE_VALIDATION = "pref_switch_file_validation_switch";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        Preference backupBtn = findPreference(getString(R.string.prefs_backup_btn));
        backupBtn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                requestAppBackup();
                Logger.getInstance().appendLog("Backup requested", true);
                return true;
            }
        });

        findPreference(KEY_PREF_SERVER_IP).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (AndroidUtil.isIpInvalid((String) newValue)) {
                    Logger.getInstance().appendLog("Invalid IP: " + newValue + "\nDefault IP address set", true);
                    preference.getEditor().putString(KEY_PREF_SERVER_IP, getString(R.string.default_ip)).commit();
                    return false;
                }
                return true;
            }
        });

        findPreference(KEY_PREF_SERVER_PORT).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (AndroidUtil.isPortInvalid((String) newValue)) {
                    Logger.getInstance().appendLog("Invalid port: " + newValue + "\nDefault port set", true);
                    preference.getEditor().putString(KEY_PREF_SERVER_PORT, getString(R.string.default_port)).commit();
                    return false;
                }
                return true;
            }
        });

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
        }
    }

}
