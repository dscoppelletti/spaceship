package it.scoppelletti.spaceship.bluetooth.sample;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    public SettingsFragment() {
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Preference pref;

        setPreferencesFromResource(R.xml.settings, rootKey);

        if (BluetoothAdapter.getDefaultAdapter() == null) {
            pref = findPreference(getString(R.string.pref_btConfig));
            pref.setEnabled(false);
        }
    }
}
