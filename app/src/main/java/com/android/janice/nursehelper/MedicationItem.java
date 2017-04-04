package com.android.janice.nursehelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.android.janice.nursehelper.data.ResidentContract;
import com.android.janice.nursehelper.data.ResidentProvider;
import com.android.janice.nursehelper.utility.AdminTimeInfo;
import com.android.janice.nursehelper.utility.NetworkUtils;
import com.android.janice.nursehelper.utility.NurseHelperJsonUtils;
import com.android.janice.nursehelper.utility.Utility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.util.ArrayList;

/*
 * Created by janicerichards on 2/4/17.
 */

public class MedicationItem {
    //private String roomNumber;
    //private Bitmap portrait;

    private String roomNumber;
    private String genericName;
    private String tradeName;
    private double dosage;
    private String dosageUnits;
    private String dosageRoute;
    private String adminTimes;
    private String frequency;
    private long lastGivenTime;
    private String nextDosageTime;
    private long nextDosageTimeLong;

    private final static int COL_ROOM_NUMBER = 0;
    private final static int COL_GENERIC_NAME = 1;
    private final static int COL_TRADE_NAME = 2;

    public final static String TAG = MedicationItem.class.getSimpleName();


    public MedicationItem() {
    }

    public MedicationItem(Cursor cursor) {
        roomNumber = cursor.getString(COL_ROOM_NUMBER);
        genericName = cursor.getString(COL_GENERIC_NAME);
        tradeName = cursor.getString(COL_TRADE_NAME);
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getGenericName() {
        return genericName;
    }

    public String getTradeName() {
        return tradeName;
    }

    public double getDosage() {
        return dosage;
    }

    public String getDosageUnits() {
        return dosageUnits;
    }

    public String getDosageRoute() {
        return dosageRoute;
    }

    public String getAdminTimes() {
        return adminTimes;
    }

    public String getFrequency() {
        return frequency;
    }

    public long getLastGivenTime() {
        return lastGivenTime;
    }

    public String getNextDosageTime() {
        return nextDosageTime;
    }

    public long getNextDosageTimeLong() {
        return nextDosageTimeLong;
    }


    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public void setDosage(float dosage) {
        this.dosage = dosage;
    }

    public void setDosageUnits(String dosageUnits) {
        this.dosageUnits = dosageUnits;
    }

    public void setDosageRoute(String dosageRoute) {
        this.dosageRoute = dosageRoute;
    }

    public void setAdminTimes(String adminTimes) {
        this.adminTimes = adminTimes;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setLastGivenTime(long lastGivenTime) {
        this.lastGivenTime = lastGivenTime;
    }

    public void setNextDosageTime(String nextDosageTime) {
        this.nextDosageTime = nextDosageTime;
    }

    public void setNextDosageTimeLong(long nextDosageTimeLong) {
        this.nextDosageTimeLong = nextDosageTimeLong;
    }


    public static void putInDummyData(Context context, DatabaseReference database, String userId) {
        // First see if any data in already; if so, just return
        Uri uri = ResidentContract.MedicationEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath("200").build();
        Cursor cursor = context.getContentResolver().query(uri,
                new String[]{ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER},
                null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                cursor.close();
                return;
            } else {
                cursor.close();
            }
        }

        String roomNumber = "200";
        Uri uriMeds = ResidentContract.MedicationEntry.CONTENT_URI;
        uriMeds = uriMeds.buildUpon().appendPath(roomNumber).build();
        long time = System.currentTimeMillis();

        // Med #1
        ContentValues medValues = new ContentValues();

        medValues.put(ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER, roomNumber);
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NAME_GENERIC, "lisinopril");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NAME_TRADE, "Zestril");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE, 30);
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE_UNITS, "mg");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE_ROUTE, "oral");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_TIMES, "9 AM");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_FREQUENCY, "QD");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_LAST_GIVEN, 0);
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME, "");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME_LONG, 0);

        // Write to local device DB
        context.getContentResolver().insert(uriMeds, medValues);

        // Now, right to Firebase DB
        new UpdateMedicationTask(context, database, userId).execute(medValues);


        // Med #2
        medValues = new ContentValues();

        Utility.getReadableTimestamp(context, time + 5);

        medValues.put(ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER, roomNumber);
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NAME_GENERIC, "metformin HCL");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NAME_TRADE, "Glucophage");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE, 300);
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE_UNITS, "mg");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE_ROUTE, "oral");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_TIMES, "7 AM / 3 PM / 11 PM");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_FREQUENCY, "");  // also tried "TID"
        medValues.put(ResidentContract.MedicationEntry.COLUMN_LAST_GIVEN, 0);
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME, "");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME_LONG, 0);

        context.getContentResolver().insert(uriMeds, medValues);
        new UpdateMedicationTask(context, database, userId).execute(medValues);

        // Med #3
        medValues = new ContentValues();

        Utility.getReadableTimestamp(context, time + 2);

        medValues.put(ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER, roomNumber);
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NAME_GENERIC, "metaprolol tartrate");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NAME_TRADE, "Lopressor");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE, 150);
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE_UNITS, "mg");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE_ROUTE, "oral");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_TIMES, "9 AM / 9PM");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_FREQUENCY, "BID");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_LAST_GIVEN, 0);
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME, "");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME_LONG, 0);

        context.getContentResolver().insert(uriMeds, medValues);
        new UpdateMedicationTask(context, database, userId).execute(medValues);

        // Med for ANOTHER PATIENT
        medValues = new ContentValues();

        medValues.put(ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER, "201");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NAME_GENERIC, "aspirin");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NAME_TRADE, "Anacin");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE, 350);
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE_UNITS, "mg");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE_ROUTE, "oral");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_TIMES, "PRN");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_FREQUENCY, "Q4-6 hours for pain");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_LAST_GIVEN, 0);
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME, "");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME_LONG, 0);

        context.getContentResolver().insert(uriMeds, medValues);
        new UpdateMedicationTask(context, database, userId).execute(medValues);


        // Med #1 for later patient
        medValues = new ContentValues();

        Utility.getReadableTimestamp(context, time + 50);

        medValues.put(ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER, "208");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NAME_GENERIC, "atavan");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NAME_TRADE, "Zzzaway");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE, 15);
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE_UNITS, "mg");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE_ROUTE, "oral");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_TIMES, "7 AM / 1 PM / 7 PM / 1 AM");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_FREQUENCY, "QID");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_LAST_GIVEN, 0);
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME, "");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME_LONG, 0);

        context.getContentResolver().insert(uriMeds, medValues);
        new UpdateMedicationTask(context, database, userId).execute(medValues);

        // Med #2 for later patient
        medValues = new ContentValues();

        Utility.getReadableTimestamp(context, time + 25);

        medValues.put(ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER, "208");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NAME_GENERIC, "haldoperitol");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NAME_TRADE, "DePyche");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE, 25);
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE_UNITS, "mg");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE_ROUTE, "oral");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_TIMES, "8 AM / 8 PM");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_FREQUENCY, "BID");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_LAST_GIVEN, 0);
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME, "");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME_LONG, 0);

        context.getContentResolver().insert(uriMeds, medValues);
        new UpdateMedicationTask(context, database, userId).execute(medValues);
    }

    // if "given" false, med was refused.
    public static void medGiven(Context context, Cursor cursor, String roomNumber, String nurseName, boolean given,
                                final DatabaseReference database, final String userId) {
        final String genericName = cursor.getString(MedicationsFragment.COL_GENERIC);
        float dosage = cursor.getFloat(MedicationsFragment.COL_DOSAGE);
        String dosageUnits = cursor.getString(MedicationsFragment.COL_DOSAGE_UNITS);

        String adminTimes = cursor.getString(MedicationsFragment.COL_ADMIN_TIMES);
        String freq = cursor.getString(MedicationsFragment.COL_FREQUENCY);

        long timeLastGiven = cursor.getLong(MedicationsFragment.COL_LAST_GIVEN);

        Uri uriMeds = ResidentContract.MedsGivenEntry.CONTENT_URI;
        uriMeds = uriMeds.buildUpon().appendPath(roomNumber).build();

        long time = System.currentTimeMillis();
        // If this medication was REFUSED, don't actually make any changes to the 'medications' table
        //  (that is, no change to 'last-given', 'next-dosage-time', or 'next-dosage-time-long'.
        //  So, no need to calculate these:
        String nextAdminTime = "";
        long nextAdminTimeLong = 0;
        if (given) {
            //AdminTimeInfo info = Utility.calculateNextDueTime(context, adminTimes, freq, time);
            AdminTimeInfo info = Utility.calculateNextDueTime(context, adminTimes, freq, timeLastGiven);
            if (info != null) {
                nextAdminTime = info.getDisplayableTime(context);
                nextAdminTimeLong = info.getTime();
            } else {
                nextAdminTime = "";
                nextAdminTimeLong = 0;
            }
        }

        ContentValues medGivenValues = new ContentValues();
        medGivenValues.put(ResidentContract.MedsGivenEntry.COLUMN_ROOM_NUMBER, roomNumber);
        medGivenValues.put(ResidentContract.MedsGivenEntry.COLUMN_NAME_GENERIC, genericName);
        medGivenValues.put(ResidentContract.MedsGivenEntry.COLUMN_DOSAGE, dosage);
        medGivenValues.put(ResidentContract.MedsGivenEntry.COLUMN_DOSAGE_UNITS, dosageUnits);
        medGivenValues.put(ResidentContract.MedsGivenEntry.COLUMN_GIVEN, given);
        medGivenValues.put(ResidentContract.MedsGivenEntry.COLUMN_NURSE, nurseName);
        medGivenValues.put(ResidentContract.MedsGivenEntry.COLUMN_TIME_GIVEN, time);

        context.getContentResolver().insert(uriMeds, medGivenValues);
        // Add the new "MedicationGiven" record to the Firebase database also:
        UpdateMedsGivenTask updateMedsGivenTask = new UpdateMedsGivenTask(context, database, userId);
        updateMedsGivenTask.execute(medGivenValues);

        // If this medication was REFUSED, don't actually make any changes to the 'medications' table
        //  (that is, no change to 'last-given', 'next-dosage-time', or 'next-dosage-time-long'
        if (given) {
            ContentValues meds = new ContentValues();
            meds.put(ResidentContract.MedicationEntry.COLUMN_LAST_GIVEN, time);
            meds.put(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME, nextAdminTime);
            meds.put(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME_LONG, nextAdminTimeLong);

            uriMeds = ResidentContract.MedicationEntry.CONTENT_URI;
            uriMeds = uriMeds.buildUpon().appendPath(roomNumber).appendPath(genericName).build();
            context.getContentResolver().update(uriMeds, meds,
                    ResidentProvider.sMedsByResidentAndMedSelection,
                    new String[]{roomNumber, genericName});

            // update the same thing in the central Firebase database
            UpdateMedicationTakenTask updateMedicationTask = new UpdateMedicationTakenTask(context, database, userId);
            updateMedicationTask.setQueryValues(roomNumber, genericName, time, nextAdminTime, nextAdminTimeLong);
            updateMedicationTask.execute();
        }

        TrimMedsGivenDataTask trimMedsGivenDataTask = new TrimMedsGivenDataTask(context, roomNumber);
        trimMedsGivenDataTask.execute();

    }


    public static void undoMedGiven(Context context, Cursor cursor, String roomNumber, String nurseName,
                                    final DatabaseReference database, final String userId) {
        String genericName = cursor.getString(MedicationsFragment.COL_GENERIC);
        String adminTimes = cursor.getString(MedicationsFragment.COL_ADMIN_TIMES);
        String freq = cursor.getString(MedicationsFragment.COL_FREQUENCY);
        // We have already verified that the nurse had ealier selected 'give' by mistake:
        //   we now need to delete that MedGiven record, and change the 'last-given'
        //   time on that Medication record, for that patient, to the PREVIOUS last-given time
        //   (using the latest MedGiven record time, after deleting the most recent false one).
        UndoMedGivenTask undoMedGivenTask = new UndoMedGivenTask(context, database, userId, roomNumber, genericName,
                adminTimes, freq);
        undoMedGivenTask.execute();
    }


    public static void undoMedRefused(Context context, Cursor cursor, String roomNumber, String nurseName,
                                      DatabaseReference database, String userId) {
        String genericName = cursor.getString(MedicationsFragment.COL_GENERIC);
        // We have already verified that the nurse had ealier selected 'give' by mistake:
        //   we now need to delete that MedGiven record.  No changes had been made to the Medication
        //   record, so there's nothing that needs to be done there.
        UndoMedRefusedTask undoMedRefusedTask = new UndoMedRefusedTask(context, database, userId, roomNumber, genericName);
        undoMedRefusedTask.execute();
    }


    // AsyncTask<Params, Progress, Result>
    // Params - what you pass to the AsyncTask
    // Progress - if you have any updates; passed to onProgressUpdate()
    // Result - the output; returned by doInBackground()
    //
    private static class UpdateMedicationTask extends AsyncTask<ContentValues, Void, Void> {
        protected final Context context;
        protected final DatabaseReference database;
        protected final String userId;

        public UpdateMedicationTask(Context context, DatabaseReference database, String userId) {
            this.context = context;
            this.database = database;
            this.userId = userId;
        }

        @Override
        protected Void doInBackground(ContentValues... params) {
            // Add the new "Medication" record to the Firebase database.  This is used here ONLY
            //   for adding fake testing data.  Actual medication data will ONLY be entered at the
            //   Firebase console, and downloaded to this app.

            // ONLY add this stuff, if NO MEDICATIONS currently defined for this userId:
            try {
                URL medicationDataUrl = NetworkUtils.getMedicationsUrl(context,userId);
                String jsonMedicationResponse = NetworkUtils.getResponseFromHttpUrl(medicationDataUrl);
                ContentValues[] medValues = NurseHelperJsonUtils
                        .getMedicationContentValuesFromJson(context, jsonMedicationResponse);
                // if already data here for medications, don't bother adding any of this DUMMY DATA!!
                if (medValues != null && medValues.length != 0) return null;
            } catch (Exception e) {
                // Server probably invalid
                e.printStackTrace();
                return null;
            }

            ContentValues medicationValues = params[0];
            //String medicationId = database.child("users").child(userId).child("medications").push().getKey();
            String medicationId = database.child(ResidentContract.PATH_USERS).child(userId)
                    .child(ResidentContract.MedicationEntry.TABLE_NAME).push().getKey();
            ArrayList<String> keys = new ArrayList<>(medicationValues.keySet());
            for (int i = 0; i < keys.size(); i++) {
                Object value = medicationValues.get(keys.get(i));
                //database.child("users").child(userId).child("medications").child(medicationId).child(keys.get(i)).setValue(value);
                database.child(ResidentContract.PATH_USERS).child(userId)
                        .child(ResidentContract.MedicationEntry.TABLE_NAME).child(medicationId)
                        .child(keys.get(i)).setValue(value);
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }


    // AsyncTask<Params, Progress, Result>
    // Params - what you pass to the AsyncTask
    // Progress - if you have any updates; passed to onProgressUpdate()
    // Result - the output; returned by doInBackground()
    //
    private static class UpdateMedsGivenTask extends AsyncTask<ContentValues, Void, Void> {
        protected final Context context;
        protected final DatabaseReference database;
        protected final String userId;

        public UpdateMedsGivenTask(Context context, DatabaseReference database, String userId) {
            this.context = context;
            this.database = database;
            this.userId = userId;
        }

        @Override
        protected Void doInBackground(ContentValues... params) {
            // Add the new "Med-given" record to the Firebase database

            ContentValues medGivenValues = params[0];
            //String medicationId = database.child("users").child(userId).child("medsGiven").push().getKey();
            String medicationId = database.child(ResidentContract.PATH_USERS).child(userId)
                    .child(ResidentContract.MedsGivenEntry.TABLE_NAME).push().getKey();
            ArrayList<String> keys = new ArrayList<>(medGivenValues.keySet());
            for (int i = 0; i < keys.size(); i++) {
                Object value = medGivenValues.get(keys.get(i));
                //database.child("users").child(userId).child("medsGiven").child(medicationId).child(keys.get(i)).setValue(value);
                database.child(ResidentContract.PATH_USERS).child(userId)
                        .child(ResidentContract.MedsGivenEntry.TABLE_NAME).child(medicationId)
                        .child(keys.get(i)).setValue(value);
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }


    private static class UpdateMedicationTakenTask extends AsyncTask<Void, Void, Void> {
        protected final Context context;
        protected final DatabaseReference database;
        protected final String userId;
        protected String roomNumber, genericName, nextAdminTime;
        protected long time, nextAdminTimeLong;

        public UpdateMedicationTakenTask(Context context, DatabaseReference database, String userId) {
            this.context = context;
            this.database = database;
            this.userId = userId;
        }

        protected void setQueryValues(String roomNumber, String genericName, long time, String nextAdminTime, long nextAdminTimeLong) {
            this.roomNumber = roomNumber;
            this.genericName = genericName;
            this.time = time;
            this.nextAdminTime = nextAdminTime;
            this.nextAdminTimeLong = nextAdminTimeLong;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Modify the current medication record in the Firebase database, to show most recent
            //  time-administered value(s).

            //Query queryMed = database.child("users").child(userId).child("medications").orderByChild("roomNumber").equalTo(roomNumber);
            Query queryMed = database.child(ResidentContract.PATH_USERS).child(userId)
                    .child(ResidentContract.MedicationEntry.TABLE_NAME)
                    .orderByChild(ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER).equalTo(roomNumber);
            queryMed.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot issue : dataSnapshot.getChildren()) {
                            //if (issue.child("genericName").getValue().equals(genericName)) {
                            //    database.child("users").child(userId).child("medications")
                            //            .child(issue.getKey()).child("lastGivenTime").setValue(new Long(time));
                            //    database.child("users").child(userId).child("medications")
                            //            .child(issue.getKey()).child("nextDosageTime").setValue(nextAdminTime);
                            //    database.child("users").child(userId).child("medications")
                            //            .child(issue.getKey()).child("nextDosageTimeLong").setValue(new Long(nextAdminTimeLong));
                            //    return;
                            //}
                            if (issue.child(ResidentContract.MedicationEntry.COLUMN_NAME_GENERIC).getValue()
                                    .equals(genericName)) {
                                database.child(ResidentContract.PATH_USERS).child(userId)
                                        .child(ResidentContract.MedicationEntry.TABLE_NAME)
                                        .child(issue.getKey()).child(ResidentContract.MedicationEntry.COLUMN_LAST_GIVEN)
                                        .setValue(Long.valueOf(time));
                                database.child(ResidentContract.PATH_USERS).child(userId)
                                        .child(ResidentContract.MedicationEntry.TABLE_NAME)
                                        .child(issue.getKey()).child(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME)
                                        .setValue(nextAdminTime);
                                database.child(ResidentContract.PATH_USERS).child(userId)
                                        .child(ResidentContract.MedicationEntry.TABLE_NAME)
                                        .child(issue.getKey()).child(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME_LONG)
                                        .setValue(Long.valueOf(nextAdminTimeLong));
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }


    private static class TrimMedsGivenDataTask extends AsyncTask<Void, Void, Void> {
        protected final Context context;
        protected final String roomNumber;

        public TrimMedsGivenDataTask(Context context, String roomNumber) {
            this.context = context;
            this.roomNumber = roomNumber;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Bundle bundle = context.getContentResolver().call(ResidentContract.MedsGivenEntry.CONTENT_URI,
                    "countMedsGiven", roomNumber, null);
            long numberRecs = 0;
            if (bundle != null)
                numberRecs = bundle.getLong(MainActivity.ITEM_COUNT);

            int maxNumberRecords = context.getResources().getInteger(R.integer.max_number_medsgiven_records);

            if (numberRecs > maxNumberRecords) {
                int numberToDelete = context.getResources().getInteger(R.integer.number_medsgiven_records_to_delete);
                Bundle input = new Bundle();
                input.putInt(MainActivity.ITEM_DELETE_AMT, numberToDelete);
                context.getContentResolver().call(ResidentContract.MedsGivenEntry.CONTENT_URI,
                        "deleteOldestMedsGiven", roomNumber, input);
            }
            return null;
        }
    }


    private static class UndoMedGivenTask extends AsyncTask<Void, Void, Void> {
        protected final Context context;
        protected final DatabaseReference database;
        protected final String userId;
        protected final String roomNumber, genericName;
        protected final String adminTimes, freq;

        public UndoMedGivenTask(Context context, DatabaseReference database, String userId,
                                String roomNumber, String genericName,
                                String adminTimes, String freq) {
            this.context = context;
            this.database = database;
            this.userId = userId;
            this.roomNumber = roomNumber;
            this.genericName = genericName;
            this.adminTimes = adminTimes;
            this.freq = freq;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Bundle input = new Bundle();
            input.putString(MainActivity.ITEM_GENERIC_NAME, genericName);
            // Delete the single most recent med-given record, with the given room# and generic med:
            context.getContentResolver().call(ResidentContract.MedsGivenEntry.CONTENT_URI,
                    "deleteNewestMedsGiven", roomNumber, input);

            input.putString(MainActivity.ITEM_ADMIN_TIMES, adminTimes);
            input.putString(MainActivity.ITEM_FREQ, freq);
            context.getContentResolver().call(ResidentContract.MedsGivenEntry.CONTENT_URI,
                    "undoMostRecentTimestamp", roomNumber, input);

            // Also need to delete it from the Firebase database:
            Query queryMed = database.child(ResidentContract.PATH_USERS).child(userId)
                    .child(ResidentContract.MedsGivenEntry.TABLE_NAME)
                    .orderByChild(ResidentContract.MedsGivenEntry.COLUMN_ROOM_NUMBER).limitToLast(1);
            queryMed.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot issue : dataSnapshot.getChildren()) {
                            // Since this is the last one (most recent timestamp), this is the 'medGiven' record
                            //  that we want to undo.
                            if (issue.child(ResidentContract.MedicationEntry.COLUMN_NAME_GENERIC).getValue()
                                    .equals(genericName)) {
                                issue.getRef().removeValue();
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }


    private static class UndoMedRefusedTask extends AsyncTask<Void, Void, Void> {
        protected final Context context;
        protected final DatabaseReference database;
        protected final String userId;
        protected final String roomNumber, genericName;

        public UndoMedRefusedTask(Context context, DatabaseReference database, String userId,
                                  String roomNumber, String genericName) {
            this.context = context;
            this.database = database;
            this.userId = userId;
            this.roomNumber = roomNumber;
            this.genericName = genericName;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Bundle input = new Bundle();
            input.putString(MainActivity.ITEM_GENERIC_NAME, genericName);
            // Delete the single most recent med-given record, with the given room# and generic med:
            //  ('refused' will be true, but this isn't necessary to check for)
            context.getContentResolver().call(ResidentContract.MedsGivenEntry.CONTENT_URI,
                    "deleteNewestMedsGiven", roomNumber, input);

            // Also need to delete it from the Firebase database:
            Query queryMed = database.child(ResidentContract.PATH_USERS).child(userId)
                    .child(ResidentContract.MedsGivenEntry.TABLE_NAME)
                    .orderByChild(ResidentContract.MedsGivenEntry.COLUMN_ROOM_NUMBER).limitToLast(1);
            queryMed.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot issue : dataSnapshot.getChildren()) {
                            // Since this is the last one (most recent timestamp), this is the 'medGiven' record
                            //  that we want to undo.
                            if (issue.child(ResidentContract.MedicationEntry.COLUMN_NAME_GENERIC).getValue()
                                    .equals(genericName)) {
                                issue.getRef().removeValue();
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }


}
