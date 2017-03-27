package com.android.janice.nursehelper.alarm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.janice.nursehelper.data.ResidentContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */

/**
 * Created by janicerichards on 3/26/17.
 */

public class NurseHelperSchedulingService extends IntentService {
    public NurseHelperSchedulingService() {
        super("SchedulingService");
    }

    public static final String TAG = "SchedService";

    public static final String ACTION_DATA_UPDATED =
            "com.android.janice.nursehelper.ACTION_DATA_UPDATED";

    static final int COL_ROOM_NUMBER = 0;
    static final int COL_NEXT_GIVE_TIME = 1;
    static final int COL_NEXT_GIVE_TIME_LONG = 2;

    Cursor previousCursor;
    //roomNumber = 200
    //nextDosageTime = 03-25-17 9:17 AM
    //earliestMed = 1490447831803

    @Override
    protected void onHandleIntent(Intent intent) {
        // Find the time of soonest times medications are due, for each resident in the 'residents' table:
        Cursor nextMedsCursor = getContentResolver().query(ResidentContract.MedicationEntry.CONTENT_URI, null,
                null, null, null);
        // See if any changes to med-due times since last check; if so, notify widget and app (if running)
        if (previousCursor != null) {
            if (previousCursor.moveToFirst() && nextMedsCursor.moveToFirst()) {
                //for (int i = 0; i < nextMedsCursor.getCount(); i++) {
                //    Log.e(TAG, " " + nextMedsCursor.getColumnName(i));
                //}
                if (anyChanges(nextMedsCursor)) {
                    updateWidgets();
                    getContentResolver().notifyChange(ResidentContract.ResidentEntry.CONTENT_URI, null);
                }
            }
        } else {
            previousCursor = nextMedsCursor;
            updateWidgets();
            getContentResolver().notifyChange(ResidentContract.ResidentEntry.CONTENT_URI, null);
        }

        // Release the wake lock provided by the BroadcastReceiver.
        NurseHelperAlarmReceiver.completeWakefulIntent(intent);
        // END_INCLUDE(service_onhandle)
    }

    private boolean anyChanges(Cursor currentMedsDue) {
        do {
            if (!previousCursor.getString(COL_ROOM_NUMBER).equals(currentMedsDue.getString(COL_ROOM_NUMBER)))
                return true;
            if (!previousCursor.getString(COL_NEXT_GIVE_TIME).equals(currentMedsDue.getString(COL_NEXT_GIVE_TIME)))
                return true;
            if (previousCursor.getLong(COL_NEXT_GIVE_TIME_LONG) != currentMedsDue.getLong(COL_NEXT_GIVE_TIME_LONG))
                return true;
        } while (previousCursor.moveToNext() && currentMedsDue.moveToNext());
        return false;
    }


    private void updateWidgets() {
        /*
        Context context = getContext();
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
        */
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(getPackageName());
        sendBroadcast(dataUpdatedIntent);

    }


}
