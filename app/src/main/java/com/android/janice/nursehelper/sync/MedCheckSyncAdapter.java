package com.android.janice.nursehelper.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.Time;
import android.util.Log;

import com.android.janice.nursehelper.R;
import com.android.janice.nursehelper.data.ResidentDbHelper;
import com.android.janice.nursehelper.utility.Utility;
import com.android.janice.nursehelper.data.ResidentContract;
import com.android.janice.nursehelper.data.ResidentProvider;

import java.util.HashMap;


/**
 * Created by janicerichards on 3/1/17.
 */

public class MedCheckSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String LOG_TAG = com.android.janice.nursehelper.sync.MedCheckSyncAdapter.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED =
            "com.example.android.sunshine.app.ACTION_DATA_UPDATED";
    // Interval at which to check which medications due, in seconds.
    // This class will look at the "next-admin-time" of all the medications.  For each patient, the medication-due time
    //  which is due the soonest, will be displayed in the widget.  Also, if the due-time falls within a shared-preference
    //  alert-time-interval, the app will notify the nurse.  (Note that notifications may also be turned off).
    //  (1800 = 30 min)  ( 30*60 = 1800 )
    private static int SYNC_INTERVAL;
    public static int SYNC_FLEXTIME;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int MEDS_NOTIFICATION_ID = 1234;  //
    private Context mContext;

    public MedCheckSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        Log.i(LOG_TAG," Here in MedCheckSyncAdapter constructor");
        mContext = context;
        int interval = Utility.getPreferredAlertTimeInterval(mContext);
        SYNC_INTERVAL = interval*60;  // 'interval' is in minutes
        SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Cursor nextMedsCursor = mContext.getContentResolver().query(ResidentContract.MedicationEntry.CONTENT_URI, null,
                null, null, null);

        // list query stuff:
        if (nextMedsCursor.moveToFirst()) {
            do {
                Log.e(LOG_TAG, "room# "+nextMedsCursor.getString(0)+
                "  next time to admin: "+Utility.getReadableTimestamp(mContext,nextMedsCursor.getLong(1)));
            } while (nextMedsCursor.moveToNext());
        }

        // Next, verify that none of these next-time-to-admin date/times are ALREADY PASSED!!  If so,
        //  update them:
    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Log.e(LOG_TAG, "here in syncImmediately");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        Log.e(LOG_TAG, " ..... here in onAccountCreated() ...");
        /*
         * Since we've created an account
         */
        com.android.janice.nursehelper.sync.MedCheckSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}
