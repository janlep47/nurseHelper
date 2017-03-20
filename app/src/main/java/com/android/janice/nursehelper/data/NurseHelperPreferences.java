package com.android.janice.nursehelper.data;
/**
 * Created by janicerichards on 2/25/17.
 */

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


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.janice.nursehelper.R;

public final class NurseHelperPreferences {



    /**
     * Returns the time-interval in minutes the user prefers before checking for MedAlerts in NurseHelper.
     * This preference can be changed by the user within the SettingsFragment.
     *
     * @param context Used to access SharedPreferences
     * @return int the number of minutes the user prefers to wait to check for meds-due
     */

    public static int getPreferredAlertTimeInterval(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int iVal = context.getResources().getInteger(R.integer.pref_time_interval_default);
        String val = prefs.getString(context.getString(R.string.pref_time_intervals_key),
                String.valueOf(iVal));
        try {
            iVal = Integer.parseInt(val);
        } catch (NumberFormatException e) {
            Log.e(" NurseHelperPreferences","  ERROR: couldn't convert alert time to integer!");
        }
        return iVal;
    }


    /**
     * Returns true if the user prefers to see MedAlerts in NurseHelper, false otherwise. This
     * preference can be changed by the user within the SettingsFragment.
     *
     * @param context Used to access SharedPreferences
     * @return true if the user prefers to see MedAlerts, false otherwise
     */
    public static boolean areMedAlertsEnabled(Context context) {
        /* Key for accessing the preference for showing MedAlerts */
        String displayMedAlertsKey = context.getString(R.string.pref_enable_med_alerts_key);

        /*
         * In Sunshine, the user has the ability to say whether she would like MedAlerts
         * enabled or not. If no preference has been chosen, we want to be able to determine
         * whether or not to show them. To do this, we reference a bool stored in bools.xml.
         */
        boolean shouldDisplayMedAlertsByDefault = context
                .getResources()
                .getBoolean(R.bool.show_med_alerts_by_default);

        /* As usual, we use the default SharedPreferences to access the user's preferences */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        /* If a value is stored with the key, we extract it here. If not, use a default. */
        boolean shouldDisplayMedAlerts = sp
                .getBoolean(displayMedAlertsKey, shouldDisplayMedAlertsByDefault);


        Log.i("PREF","   preferred med alerts on = "+shouldDisplayMedAlerts);
        return shouldDisplayMedAlerts;
    }

    /**
     * Returns the last time that a med alert was shown (in UNIX time)
     *
     * @param context Used to access SharedPreferences
     * @return UNIX time of when the last med-alert was shown
     */
    public static long getLastMedAlertTimeInMillis(Context context) {
        /* Key for accessing the time at which NurseHelper last displayed a med alert */
        String lastMedAlertKey = context.getString(R.string.pref_last_med_alert);

        /* As usual, we use the default SharedPreferences to access the user's preferences */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        /*
         * Here, we retrieve the time in milliseconds when the last MedAlert was shown. If
         * SharedPreferences doesn't have a value for lastMedAlertKey, we return 0. The reason
         * we return 0 is because we compare the value returned from this method to the current
         * system time. If the difference between the last MedAlert time and the current time
         * is greater than one day, we will show a MedAlert again. When we compare the two
         * values, we subtract the last MedAlert time from the current system time. If the
         * time of the last MedAlert was 0, the difference will always be greater than the
         * number of milliseconds in a day and we will show another MedAlert.
         */
        long lastMedAlertTime = sp.getLong(lastMedAlertKey, 0);

        return lastMedAlertTime;
    }

    /**
     * Returns the elapsed time in milliseconds since the last MedAlert was shown. This is used
     * as part of our check to see if we should show another MedAlert when the weather is
     * updated.
     *
     * @param context Used to access SharedPreferences as well as use other utility methods
     * @return Elapsed time in milliseconds since the last MedAlert was shown
     */
    public static long getEllapsedTimeSinceLastMedAlert(Context context) {
        long lastMedAlertTimeMillis =
                NurseHelperPreferences.getLastMedAlertTimeInMillis(context);
        long timeSinceLastMedAlert = System.currentTimeMillis() - lastMedAlertTimeMillis;
        return timeSinceLastMedAlert;
    }

    /**
     * Saves the time that a MedAlert is shown. This will be used to get the ellapsed time
     * since a MedAlert was shown.
     *
     * @param context Used to access SharedPreferences
     * @param timeOfMedAlert Time of last MedAlert to save (in UNIX time)
     */
    public static void saveLastMedAlertTime(Context context, long timeOfMedAlert) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        String lastMedAlertKey = context.getString(R.string.pref_last_med_alert);
        editor.putLong(lastMedAlertKey, timeOfMedAlert);
        editor.apply();
    }
}
