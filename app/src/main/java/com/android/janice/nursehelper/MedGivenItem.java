package com.android.janice.nursehelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.android.janice.nursehelper.data.ResidentContract;
import com.android.janice.nursehelper.utility.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by janicerichards on 2/10/17.
 */

public class MedGivenItem {
    private String roomNumber;
    private String genericName;
    private float dosage;
    private String dosageUnits;
    private boolean givenOrRefused;
    private String nurseName;
    private long timeGiven;

    public final static int COL_ROOM_NUMBER = 0;
    public final static int COL_GENERIC_NAME = 1;
    public final static int COL_DOSAGE = 2;
    public final static int COL_DOSAGE_UNITS = 3;
    public final static int COL_GIVEN = 4;
    public final static int COL_NURSE_NAME = 5;
    public final static int COL_TIME_GIVEN = 6;


    public final static String TAG = MedicationItem.class.getSimpleName();


    public String getRoomNumber() { return roomNumber; }

    public String getGenericName() {
        return genericName;
    }

    public float getDosage() {
        return dosage;
    }

    public String getDosageUnits() { return dosageUnits; }

    public boolean getGivenOrRefused() { return givenOrRefused; }

    public String getNurseName() { return nurseName; }

    public long getTimeGiven() { return timeGiven; }

    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public void setDosage(float dosage) {
        this.dosage = dosage;
    }

    public void setDosageUnits(String dosageUnits) {
        this.dosageUnits = dosageUnits;
    }

    public void setGivenOrRefused(boolean given) { this.givenOrRefused = given; }

    public void setNurseName(String nurseName) { this.nurseName = nurseName; }

    public void setTimestamp(long timeGiven) { this.timeGiven = timeGiven; }


    public MedGivenItem() {}

    public MedGivenItem(Cursor cursor) {
        roomNumber = cursor.getString(COL_ROOM_NUMBER);
        genericName = cursor.getString(COL_GENERIC_NAME);
        dosage = cursor.getFloat(COL_DOSAGE);
        dosageUnits = cursor.getString(COL_DOSAGE_UNITS);
        givenOrRefused = (cursor.getShort(COL_GIVEN) == 0 ? false: true);
        nurseName = cursor.getString(COL_NURSE_NAME);
        timeGiven = cursor.getLong(COL_TIME_GIVEN);
    }


    public String getReadableTimestamp(Context context) {
        return Utility.getReadableTimestamp(context, timeGiven);
    }


    public static void askUndoMedGiven(String roomNumber, String genericName, float dosage) {
        Log.e(TAG,"  UNDO Med given: "+roomNumber+"  name:"+genericName+"   dosage: "+String.valueOf(dosage));
    }



    public static void askUndoMedRefused(String roomNumber, String genericName, float dosage) {
        Log.e(TAG,"  UNDO Med refused: "+roomNumber+"  name:"+genericName+"   dosage: "+String.valueOf(dosage));
    }

}
