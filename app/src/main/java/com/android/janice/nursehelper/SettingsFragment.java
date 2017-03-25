package com.android.janice.nursehelper;

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;

import com.android.janice.nursehelper.data.NurseHelperPreferences;
import com.android.janice.nursehelper.data.ResidentContract;
import com.android.janice.nursehelper.sync.MedCheckSyncAdapter;
import com.android.janice.nursehelper.utility.Utility;
//import com.android.janice.nursehelper.sync.NurseHelperSyncUtils;

/**
 * The SettingsFragment serves as the display for all of the user's settings. In NurseHelper, the
 * user will be able to change their preference for the time window within which the user should be alerted
 * of the next-med-admin time for each patient, and indicate whether or not they'd like to see
 * medication next-admin times from the Residentlist screen.  If the 'give-alerts' flag is set to false,
 * the time-window preference is ignored.
 *
 */
public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
    }


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.prefs);
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);
            if (!(p instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // register the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Activity activity = getActivity();

        if (key.equals(getString(R.string.pref_units_key))) {

        } else if (key.equals(getString(R.string.pref_time_intervals_key))) {
            // we've changed the Med-alert Time interval
            int newTimeInterval = NurseHelperPreferences.getPreferredAlertTimeInterval(activity);
            MedCheckSyncAdapter.changeAlertInterval(activity, newTimeInterval);
            MedCheckSyncAdapter.syncImmediately(activity);
        } else if (key.equals(getString(R.string.pref_enable_med_alerts_key))) {
            // turn med-alerts on/off
            boolean enableAlerts = NurseHelperPreferences.areMedAlertsEnabled(activity);
            if (enableAlerts) {
                int alertInterval = NurseHelperPreferences.getPreferredAlertTimeInterval(activity);
                MedCheckSyncAdapter.setAlertsOn();
                MedCheckSyncAdapter.changeAlertInterval(activity, alertInterval);
                MedCheckSyncAdapter.syncImmediately(activity);
            } else {
                MedCheckSyncAdapter.setAlertsOff(activity);
            }
        }
        Preference preference = findPreference(key);
        if (null != preference) {
            if (!(preference instanceof CheckBoxPreference)) {
                setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
            }
        }
    }
}
