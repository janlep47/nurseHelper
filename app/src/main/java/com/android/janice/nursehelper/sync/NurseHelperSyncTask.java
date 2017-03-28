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
package com.android.janice.nursehelper.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.janice.nursehelper.data.NurseHelperPreferences;
import com.android.janice.nursehelper.data.ResidentContract;
import com.android.janice.nursehelper.utility.NurseHelperJsonUtils;
import com.android.janice.nursehelper.utility.NetworkUtils;
import com.android.janice.nursehelper.alarm.NurseHelperSchedulingService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import java.net.URL;

public class NurseHelperSyncTask {

    //private static GoogleApiClient sGoogleApiClient;
    private static final String TAG = "SYNC-ADAPTER";

    /**
     * Performs the network request for current firebase 'resident' and 'medication' data, parses the JSON from that
     * request, and inserts any changes into the ContentProvider.  This function is mostly necessary for updating
     * the widget, when the NurseHelper app is not being run.  The reason for this is that the NurseHelper
     * app listens for Firebase database changes, and processes any changes in an AsyncTask.
     * Notifications therefore are not needed.
     *
     * @param context Used to access utility methods and the ContentResolver
     */
    synchronized public static void syncResidentData(Context context) {
        try {
            URL residentDataUrl = NetworkUtils.getResidentsUrl(context);

            // Use the URL to retrieve the JSON from Firebase database 'residents' table
            String jsonResidentResponse = NetworkUtils.getResponseFromHttpUrl(residentDataUrl);

            // Parse the JSON into a list of resident values
            ContentValues[] residentValues = NurseHelperJsonUtils
                    .getResidentContentValuesFromJson(context, jsonResidentResponse);

            URL medicationDataUrl = NetworkUtils.getMedicationsUrl(context);

            String jsonMedicationResponse = NetworkUtils.getResponseFromHttpUrl(medicationDataUrl);

            ContentValues[] medicationValues = NurseHelperJsonUtils
                    .getMedicationContentValuesFromJson(context, jsonMedicationResponse);

            boolean anyChanges = false;
            // ignore null or empty 'residents' content from Firebase
            if (residentValues != null && residentValues.length != 0) {

                /* Get a handle on the ContentResolver to delete and insert data */
                ContentResolver nursehelperContentResolver = context.getContentResolver();

                // Delete old resident data, IN CASE there have been any changes:
                nursehelperContentResolver.delete(
                        ResidentContract.ResidentEntry.CONTENT_URI,
                        null,
                        null);

                // Insert the new resident data into NurseHelper's ContentProvider
                nursehelperContentResolver.bulkInsert(
                        ResidentContract.ResidentEntry.CONTENT_URI,
                        residentValues);

                anyChanges = true;
            }

            // ignore null or empty 'medications' content from Firebase
            if (medicationValues != null && medicationValues.length != 0) {

                /* Get a handle on the ContentResolver to delete and insert data */
                ContentResolver nursehelperContentResolver = context.getContentResolver();

                // Delete old medications data, IN CASE there have been any changes:
                nursehelperContentResolver.delete(
                        ResidentContract.MedicationEntry.CONTENT_URI,
                        null,
                        null);

                // Insert the new medications data into NurseHelper's ContentProvider
                nursehelperContentResolver.bulkInsert(
                        ResidentContract.MedicationEntry.CONTENT_URI,
                        medicationValues);

                anyChanges = true;
            }

            if (anyChanges) updateWidgets(context);

        } catch (Exception e) {
            // Server probably invalid
            e.printStackTrace();
        }
    }


    private static void updateWidgets(Context context) {
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(NurseHelperSchedulingService.ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }


    //public static void setGoogleApiClient (GoogleApiClient client) {
    //    sGoogleApiClient = client;
    //}
}