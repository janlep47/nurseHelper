package com.android.janice.nursehelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.text.format.Time;

import com.android.janice.nursehelper.data.ResidentContract;
import com.android.janice.nursehelper.data.ResidentProvider;
import com.android.janice.nursehelper.utility.AdminTimeInfo;
import com.android.janice.nursehelper.utility.Utility;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by janicerichards on 2/4/17.
 */

public class MedicationItem {
    //private String roomNumber;
    //private Bitmap portrait;

    private String roomNumber;
    private String genericName;
    private String tradeName;

    public final static int COL_ROOM_NUMBER = 0;
    public final static int COL_GENERIC_NAME = 1;
    public final static int COL_TRADE_NAME = 2;

    public final static String TAG = MedicationItem.class.getSimpleName();


    public String getRoomNumber() { return roomNumber; }

    public String getGenericName() {
        return genericName;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }


    public MedicationItem(Cursor cursor) {
        roomNumber = cursor.getString(COL_ROOM_NUMBER);
        genericName = cursor.getString(COL_GENERIC_NAME);
        tradeName = cursor.getString(COL_TRADE_NAME);

    }




    public static void putInDummyData(Context context) {

        String roomNumber = "200";
        Uri uriMeds = ResidentContract.MedicationEntry.CONTENT_URI;
        uriMeds = uriMeds.buildUpon().appendPath(roomNumber).build();
        long time= System.currentTimeMillis();

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
        medValues.put(ResidentContract.MedicationEntry.COLUMN_LAST_GIVEN, time);

        Uri medUri = context.getContentResolver().insert(uriMeds, medValues);


        // Med #2
        medValues = new ContentValues();

        medValues.put(ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER, roomNumber);
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NAME_GENERIC, "metformin HCL");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NAME_TRADE, "Glucophage");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE, 300);
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE_UNITS, "mg");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE_ROUTE, "oral");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_TIMES, "7 AM / 3 PM / 11 PM");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_FREQUENCY, "");  // also tried "TID"
        medValues.put(ResidentContract.MedicationEntry.COLUMN_LAST_GIVEN, time);

        medUri = context.getContentResolver().insert(uriMeds, medValues);

        // Med #3
        medValues = new ContentValues();

        medValues.put(ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER, roomNumber);
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NAME_GENERIC, "metaprolol tartrate");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_NAME_TRADE, "Lopressor");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE, 150);
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE_UNITS, "mg");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE_ROUTE, "oral");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_TIMES, "9 AM / 9PM");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_FREQUENCY, "BID");
        medValues.put(ResidentContract.MedicationEntry.COLUMN_LAST_GIVEN, time);

        medUri = context.getContentResolver().insert(uriMeds, medValues);

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
        medValues.put(ResidentContract.MedicationEntry.COLUMN_LAST_GIVEN, time);
        medUri = context.getContentResolver().insert(uriMeds, medValues);
    }

    // if "given" false, med was refused.
    public static void medGiven(Context context, Cursor cursor, String roomNumber, String nurseName, boolean given) {
        String genericName = cursor.getString(MedicationsFragment.COL_GENERIC);
        float dosage = cursor.getFloat(MedicationsFragment.COL_DOSAGE);
        String dosageUnits = cursor.getString(MedicationsFragment.COL_DOSAGE_UNITS);

        String adminTimes = cursor.getString(MedicationsFragment.COL_ADMIN_TIMES);
        String freq = cursor.getString(MedicationsFragment.COL_FREQUENCY);

        Uri uriMeds = ResidentContract.MedsGivenEntry.CONTENT_URI;
        uriMeds = uriMeds.buildUpon().appendPath(roomNumber).build();

        short given_db;
        if (given) given_db = 1;
        else given_db = 0;

        long time= System.currentTimeMillis();
        AdminTimeInfo info = Utility.calculateNextDueTime(context, adminTimes, freq, time);
        String nextAdminTime = info.getDisplayableTime(context);
        long nextAdminTimeLong = info.getTime();

        ContentValues medGivenValues = new ContentValues();
        medGivenValues.put(ResidentContract.MedsGivenEntry.COLUMN_ROOM_NUMBER, roomNumber);
        medGivenValues.put(ResidentContract.MedsGivenEntry.COLUMN_NAME_GENERIC, genericName);
        medGivenValues.put(ResidentContract.MedsGivenEntry.COLUMN_DOSAGE, dosage);
        medGivenValues.put(ResidentContract.MedsGivenEntry.COLUMN_DOSAGE_UNITS, dosageUnits);
        medGivenValues.put(ResidentContract.MedsGivenEntry.COLUMN_GIVEN, given_db);
        medGivenValues.put(ResidentContract.MedsGivenEntry.COLUMN_NURSE, nurseName);
        medGivenValues.put(ResidentContract.MedsGivenEntry.COLUMN_TIME_GIVEN, time);
        //medGivenValues.put(ResidentContract.Meds)

        Uri medGivenUri = context.getContentResolver().insert(uriMeds, medGivenValues);

        ContentValues meds = new ContentValues();
        meds.put(ResidentContract.MedicationEntry.COLUMN_LAST_GIVEN, time);
        meds.put(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME, nextAdminTime);
        meds.put(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME_LONG, nextAdminTimeLong);

        uriMeds = ResidentContract.MedicationEntry.CONTENT_URI;
        uriMeds = uriMeds.buildUpon().appendPath(roomNumber).appendPath(genericName).build();
        int rowsUpdated = context.getContentResolver().update(uriMeds, meds,
                ResidentProvider.sMedsByResidentAndMedSelection,
                new String[]{roomNumber, genericName});
    }


    public static void askUndoMedGiven(Cursor cursor, String roomNumber, String nurseName) {
        String genericName = cursor.getString(MedicationsFragment.COL_GENERIC);
        float dosage = cursor.getFloat(MedicationsFragment.COL_DOSAGE);
        Log.e(TAG,"  UNDO Med given: "+roomNumber+"  name:"+genericName+"   dosage: "+String.valueOf(dosage));
    }



    public static void askUndoMedRefused(Cursor cursor, String roomNumber, String nurseName) {
        String genericName = cursor.getString(MedicationsFragment.COL_GENERIC);
        float dosage = cursor.getFloat(MedicationsFragment.COL_DOSAGE);
        Log.e(TAG,"  UNDO Med refused: "+roomNumber+"  name:"+genericName+"   dosage: "+String.valueOf(dosage));
    }

}
