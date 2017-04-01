package com.android.janice.nursehelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.android.janice.nursehelper.data.ResidentContract;
import com.android.janice.nursehelper.utility.Utility;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;

/*
 * Created by janicerichards on 2/5/17.
 */

public class AssessmentItem {

    private String roomNumber;
    private String bloodPressure;
    private String temperature;
    private int pulse;
    private int respiratoryRate;
    private String edema;
    private String edemaLocn;
    private boolean edemaPitting;
    private int pain;
    private String significantFindings;
    private long timestamp;


    private final static int COL_ROOM_NUMBER = 0;
    private final static int COL_BLOOD_PRESSURE = 1;
    private final static int COL_TEMPERATURE = 2;
    private final static int COL_PULSE = 3;
    private final static int COL_RR = 4;
    private final static int COL_EDEMA = 5;
    private final static int COL_EDEMA_LOCN = 6;
    private final static int COL_EDEMA_PITTING = 7;
    private final static int COL_PAIN = 8;
    private final static int COL_SIGNIFICANT_FINDINGS = 9;
    private final static int COL_TIMESTAMP = 10;


    public String getRoomNumber() {
        return roomNumber;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public String getTemperature() {
        return temperature;
    }

    public int getPulse() {
        return pulse;
    }

    public int getRespiratoryRate() {
        return respiratoryRate;
    }

    public String getEdema() {
        return edema;
    }

    public String getEdemaLocn() {
        return edemaLocn;
    }

    public boolean getPitting() {
        return edemaPitting;
    }

    public int getPain() {
        return pain;
    }

    public String getSignificantFindings() {
        return significantFindings;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setBloodPressure(String bp) {
        this.bloodPressure = bp;
    }

    public void setTemperature(String temp) {
        this.temperature = temp;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    public void setRespiratoryRate(int rr) {
        this.respiratoryRate = rr;
    }

    public void setEdema(String edema) {
        this.edema = edema;
    }

    public void setEdemaLocn(String edemaLocn) {
        this.edemaLocn = edemaLocn;
    }

    public void setPitting(boolean pitting) {
        this.edemaPitting = pitting;
    }

    public void setPain(int pain) {
        this.pain = pain;
    }

    public void setSignificantFindings(String findings) {
        this.significantFindings = findings;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public AssessmentItem(Cursor cursor) {
        roomNumber = cursor.getString(COL_ROOM_NUMBER);
        bloodPressure = cursor.getString(COL_BLOOD_PRESSURE);
        temperature = cursor.getString(COL_TEMPERATURE);
        pulse = cursor.getInt(COL_PULSE);
        respiratoryRate = cursor.getInt(COL_RR);
        edema = cursor.getString(COL_EDEMA);
        edemaLocn = cursor.getString(COL_EDEMA_LOCN);
        short pitting = cursor.getShort(COL_EDEMA_PITTING);
        edemaPitting = (pitting != 0);
        pain = cursor.getInt(COL_PAIN);
        significantFindings = cursor.getString(COL_SIGNIFICANT_FINDINGS);
        timestamp = cursor.getLong(COL_TIMESTAMP);
    }


    public String getReadableTimestamp(Context context) {
        return Utility.getReadableTimestamp(context, timestamp);
    }


    public static void putInDummyData(Context context, DatabaseReference database, String userId) {
        // First see if any data in already; if so, just return
        Uri uri = ResidentContract.AssessmentEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath("200").build();
        Cursor cursor = context.getContentResolver().query(uri,
                new String[]{ResidentContract.AssessmentEntry.COLUMN_ROOM_NUMBER},
                null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                cursor.close();
                return;
            } else {
                cursor.close();
            }
        }

        // Add some (past) assessment data for room 200
        String roomNumber = "200";
        uri = ResidentContract.AssessmentEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(roomNumber).build();


        long time = System.currentTimeMillis();

        // Assessment #1
        ContentValues aValues = new ContentValues();

        aValues.put(ResidentContract.AssessmentEntry.COLUMN_ROOM_NUMBER, roomNumber);
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_BLOOD_PRESSURE, "140/90");
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_TEMPERATURE, "98.5 F");
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_PULSE, 84);
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_RR, 15);
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_EDEMA, "stage I");
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_EDEMA_LOCN, "RLE");
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_EDEMA_PITTING, true);  // true
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_PAIN, 2);
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_SIGNIFICANT_FINDINGS, "C/O stiffness in R ankle. \n" +
                "Very slight headache.");
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_TIME, time);

        context.getContentResolver().insert(uri, aValues);
        //saveFirebaseAssessment(aValues, database, userId);
        new UpdateAssessmentTask(context, database, userId).execute(aValues);


        // Med #2
        aValues = new ContentValues();

        aValues.put(ResidentContract.AssessmentEntry.COLUMN_ROOM_NUMBER, roomNumber);
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_BLOOD_PRESSURE, "145/92");
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_TEMPERATURE, "98.7 F");
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_PULSE, 92);
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_RR, 17);
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_EDEMA, "0");
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_EDEMA_LOCN, "");
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_EDEMA_PITTING, false);  // false
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_PAIN, 6);
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_SIGNIFICANT_FINDINGS, "Pt. says moderate pain in lower back.");
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_TIME, time);

        context.getContentResolver().insert(uri, aValues);
        //saveFirebaseAssessment(aValues, database, userId);
        new UpdateAssessmentTask(context, database, userId).execute(aValues);

    }


    public static void saveAssessment(Context context, String roomNumber, int systolicBP, int diastolicBP,
                                      String tempString,
                                      int pulse, int rr, String edema, String edemaLocn, boolean edemaPitting,
                                      int pain, String findings,
                                      DatabaseReference database, String userId) {
        Uri uri = ResidentContract.AssessmentEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(roomNumber).build();

        ContentValues aValues = new ContentValues();

        long time = System.currentTimeMillis();

        aValues.put(ResidentContract.AssessmentEntry.COLUMN_ROOM_NUMBER, roomNumber);
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_BLOOD_PRESSURE, String.valueOf(systolicBP) + "/" +
                String.valueOf(diastolicBP));

        //String tempString = String.format(context.getString(R.string.format_temperature), temp) +
        //        (metricUnits ? " C" : " F");

        aValues.put(ResidentContract.AssessmentEntry.COLUMN_TEMPERATURE, tempString);
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_PULSE, pulse);
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_RR, rr);
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_EDEMA, edema);
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_EDEMA_LOCN, edemaLocn);
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_EDEMA_PITTING, edemaPitting);
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_PAIN, pain);
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_SIGNIFICANT_FINDINGS, findings);
        aValues.put(ResidentContract.AssessmentEntry.COLUMN_TIME, time);

        context.getContentResolver().insert(uri, aValues);
        //saveFirebaseAssessment(aValues, database, userId);
        UpdateAssessmentTask updateAssessmentTask = new UpdateAssessmentTask(context, database, userId);
        updateAssessmentTask.execute(aValues);

        TrimAssessmentDataTask trimAssessmentDataTask = new TrimAssessmentDataTask(context, roomNumber);
        trimAssessmentDataTask.execute();

    }


    private static class UpdateAssessmentTask extends AsyncTask<ContentValues, Void, Void> {
        private final Context context;
        private final DatabaseReference database;
        private final String userId;

        public UpdateAssessmentTask(Context context, DatabaseReference database, String userId) {
            this.context = context;
            this.database = database;
            this.userId = userId;
        }

        @Override
        protected Void doInBackground(ContentValues... params) {
            // Add the new "Assessment" record to the Firebase database.

            ContentValues assessmentValues = params[0];
            //String assessmentId = database.child("users").child(userId).child("assessments").push().getKey();
            String assessmentId = database.child(ResidentContract.PATH_USERS).child(userId)
                    .child(ResidentContract.AssessmentEntry.TABLE_NAME).push().getKey();
            ArrayList<String> keys = new ArrayList<>(assessmentValues.keySet());
            for (int i = 0; i < keys.size(); i++) {
                Object value = assessmentValues.get(keys.get(i));
                //database.child("users").child(userId).child("assessments").child(assessmentId).child(keys.get(i)).setValue(value);
                database.child(ResidentContract.PATH_USERS).child(userId)
                        .child(ResidentContract.AssessmentEntry.TABLE_NAME).child(assessmentId)
                        .child(keys.get(i)).setValue(value);
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }


    private static class TrimAssessmentDataTask extends AsyncTask<Void, Void, Void> {
        private final Context context;
        private final String roomNumber;

        public TrimAssessmentDataTask(Context context, String roomNumber) {
            this.context = context;
            this.roomNumber = roomNumber;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Bundle bundle = context.getContentResolver().call(ResidentContract.AssessmentEntry.CONTENT_URI,
                    "countAssessments", roomNumber, null);
            long numberRecs = 0;
            if (bundle != null)
                numberRecs = bundle.getLong(MainActivity.ITEM_COUNT);

            int maxNumberRecords = context.getResources().getInteger(R.integer.max_number_assessment_records);

            if (numberRecs > maxNumberRecords) {
                int numberToDelete = context.getResources().getInteger(R.integer.number_assessment_records_to_delete);
                Bundle input = new Bundle();
                input.putInt(MainActivity.ITEM_DELETE_AMT, numberToDelete);
                context.getContentResolver().call(ResidentContract.AssessmentEntry.CONTENT_URI,
                        "deleteOldestAssessments", roomNumber, input);
            }
            return null;
        }
    }
}
