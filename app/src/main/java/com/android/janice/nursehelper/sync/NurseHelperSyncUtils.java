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

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.janice.nursehelper.alarm.NurseHelperSchedulingService;
import com.android.janice.nursehelper.data.ResidentContract;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.concurrent.TimeUnit;

public class NurseHelperSyncUtils {

    /*
     * Interval at which to check for and central (Firebase) database changes to either
     *   'residents' table or 'medications' table
     */
    private static final int SYNC_INTERVAL_HOURS = 8;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    //private static final int SYNC_INTERVAL_SECONDS = 300; // every 5 min (5*60)
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    private static boolean sInitialized;

    private static final String NURSEHELPER_SYNC_TAG = "nursehelper-sync";


    /**
     * Schedules a repeating check of NurseHelper's meds next-due data using FirebaseJobDispatcher.
     * @param context Context used to create the GooglePlayDriver that powers the
     *                FirebaseJobDispatcher
     */
    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        /* Create the Job to periodically check for meds next-due times (for app and widget) */
        Job syncNurseHelperJob = dispatcher.newJobBuilder()
                /* The Service that will be used to check NurseHelper's meds-due data */
                .setService(NurseHelperFirebaseJobService.class)
                /* Set the UNIQUE tag used to identify this Job */
                .setTag(NURSEHELPER_SYNC_TAG)
                /*
                 * Network constraints on which this Job should run. We choose to run on any
                 * network, but you can also choose to run only on un-metered networks or when the
                 * device is charging. It might be a good idea to include a preference for this,
                 * as some users may not want to download any data on their mobile plan. ($$$)
                 *
                 * In general, the med-check will NOT be using any network!  However, it's possible
                 *  in the rare occation, to need to download firebase database info ...  so we'll
                 *  leave this option for now.
                 */
                .setConstraints(Constraint.ON_ANY_NETWORK)
                /*
                 * setLifetime sets how long this job should persist. The options are to keep the
                 * Job "forever" or to have it die the next time the device boots up.
                 */
                .setLifetime(Lifetime.FOREVER)
                /*
                 * We want NurseHelper's meds next-due data to stay up to date, so we tell this Job to recur.
                 */
                .setRecurring(true)
                /*
                 * We want the meds-due data to be checked every 5 min. The first argument for
                 * Trigger's static executionWindow method is the start of the time frame when the
                 * sync should be performed. The second argument is the latest point in time at
                 * which the data should be synced. Please note that this end time is not
                 * guaranteed, but is more of a guideline for FirebaseJobDispatcher to go off of.
                 */
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                /*
                 * If a Job with the tag with provided already exists, this new job will replace
                 * the old one.
                 */
                .setReplaceCurrent(true)
                /* Once the Job is ready, call the builder's build method to return the Job */
                .build();

        /* Schedule the Job with the dispatcher */
        dispatcher.schedule(syncNurseHelperJob);
    }
    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     *
     * @param context Context that will be passed to other methods and used to access the
     *                ContentResolver
     */
    //synchronized public static void initialize(@NonNull final Context context,
    //                                           GoogleApiClient client) {
    synchronized public static void initialize(@NonNull final Context context) {
        /*
         * Only perform initialization once per app lifetime. If initialization has already been
         * performed, we have nothing to do in this method.
         */
        if (sInitialized) return;


        //sGoogleApiClient = mclient;
        //NurseHelperSyncTask.setGoogleApiClient(client);


        sInitialized = true;

        /*
         * This method call triggers NurseHelper to create its task to synchronize central firebase data
         * periodically.
         */
        scheduleFirebaseJobDispatcherSync(context);

        /*
         * We need to check to see if our ContentProvider has data to display in our forecast
         * list. However, performing a query on the main thread is a bad idea as this may
         * cause our UI to lag. Therefore, we create a thread in which we will run the query
         * to check the contents of our ContentProvider.
         */
        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {

                /* URI for every row of resident data in the 'residents' table*/
                Uri residentQueryUri = ResidentContract.ResidentEntry.CONTENT_URI;

                String[] projectionColumns = {ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER};
                String selectionStatement = null;

                /* Here, we perform the query to check to see if we have any weather data */
                Cursor cursor = context.getContentResolver().query(
                        residentQueryUri,
                        projectionColumns,
                        selectionStatement,
                        null,
                        null);
                if (null == cursor || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }

                /* Make sure to close the Cursor to avoid memory leaks! */
                cursor.close();
            }
        });

        /* Finally, once the thread is prepared, fire it off to perform our checks. */
        checkForEmpty.start();
    }

    /**
     * Helper method to perform a sync immediately using an IntentService for asynchronous
     * execution.
     *
     * @param context The Context used to start the IntentService for the sync.
     */
    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, NurseHelperSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}