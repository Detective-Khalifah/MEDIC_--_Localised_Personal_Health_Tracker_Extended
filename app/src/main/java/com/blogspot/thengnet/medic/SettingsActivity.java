package com.blogspot.thengnet.medic;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreatePreferences (Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings, rootKey);

            // Find the {@link Preference}s in settings.xml
            Preference alarmVolume = findPreference(getString(R.string.settings_alarm_volume_key));
            Preference alarmRingDuration = findPreference(getString(R.string.settings_alarm_ring_duration_key));

            Preference reminderVolume = findPreference(getString(R.string.settings_reminder_volume_key));
            Preference reminderDuration = findPreference(getString(R.string.settings_calendar_reminder_duration_key));

            Preference language = findPreference(getString(R.string.settings_language_key));
            Preference snoozeLength = findPreference(getString(R.string.settings_snooze_length_key));
            Preference hourFormat = findPreference(getString(R.string.settings_hour_format_key));

//            setSummary(alarmVolume);
            setSummary(alarmRingDuration);
//            setSummary(reminderVolume);
            setSummary(reminderDuration);
            setSummary(language);
            setSummary(snoozeLength);
            setSummary(hourFormat);
        }

        /**
         * Register a {@link androidx.preference.Preference.OnPreferenceChangeListener} to trigger
         * when a preference is changed by the user, so a corresponding label ('summary') can be
         * displayed.
         * @param changedPreference -- the preference that got modified by the user
         */
        private void setSummary (Preference changedPreference) {
            changedPreference.setOnPreferenceChangeListener(this);
            if (changedPreference instanceof ListPreference) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(changedPreference.getContext());
                String preferenceString = preferences.getString(changedPreference.getKey(), "");
                assert preferenceString != null;
                onPreferenceChange(changedPreference, preferenceString);
            }
        }

        @Override
        public boolean onPreferenceChange (Preference preference, Object newValue) {
            // parse the newly-selected preference as a String
            String preferenceValue = newValue.toString();
            // get the index of newly-selected preference item and set its summary only if the
            // preference that got changed is a ListPreference; return true if successful
            if (preference instanceof ListPreference) {
                ListPreference listPref = (ListPreference) preference;
                final int indexOfSelectedValue = listPref.findIndexOfValue(preferenceValue);
                if (indexOfSelectedValue >= 0) {
                    CharSequence[] prefLabels = listPref.getEntries();
                    preference.setSummary(prefLabels[indexOfSelectedValue]);
                }
                return true;
            } else
                return false;
        }
    }
}