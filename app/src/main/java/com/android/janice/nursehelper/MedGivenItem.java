package com.android.janice.nursehelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.android.janice.nursehelper.data.ResidentContract;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by janicerichards on 2/10/17.
 */

public class MedGivenItem {
    //private String roomNumber;
    //private Bitmap portrait;

    private String roomNumber;
    private String genericName;
    private float dosage;
    private String dosageUnits;
    private short given;      // if 1, given; if 0, med was refused
    private long timestamp;

    public final static int COL_ROOM_NUMBER = 0;
    public final static int COL_GENERIC_NAME = 1;
    public final static int COL_DOSAGE = 2;
    public final static int COL_DOSAGE_UNITS = 3;
    public final static int COL_GIVEN = 4;
    public final static int COL_TIME_GIVEN = 5;


    public final static String TAG = MedicationItem.class.getSimpleName();


    public String getRoomNumber() { return roomNumber; }

    public String getGenericName() {
        return genericName;
    }

    public float getDosage() {
        return dosage;
    }

    public String getDosageUnits() { return dosageUnits; }

    public short getGiven() { return given; }

    public long getTimestamp() { return timestamp; }

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

    public void setGiven(short given) { this.given = given; }

    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public MedGivenItem(Cursor cursor) {
        roomNumber = cursor.getString(COL_ROOM_NUMBER);
        genericName = cursor.getString(COL_GENERIC_NAME);
        dosage = cursor.getFloat(COL_DOSAGE);
        dosageUnits = cursor.getString(COL_DOSAGE_UNITS);
        given = cursor.getShort(COL_GIVEN);
        timestamp = cursor.getLong(COL_TIME_GIVEN);
    }


    public String getReadableTimestamp() {
        //String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        //String formattedDate = new SimpleDateFormat("MM-DD-YY HH:mm:ss").format(timestamp);
        Date date = new Date(timestamp);
        String formattedDate = new SimpleDateFormat("MM-dd-YY HH:mm:ss").format(date);
        return formattedDate;
    }


    public static void askUndoMedGiven(String roomNumber, String genericName, float dosage) {
        Log.e(TAG,"  UNDO Med given: "+roomNumber+"  name:"+genericName+"   dosage: "+String.valueOf(dosage));
    }



    public static void askUndoMedRefused(String roomNumber, String genericName, float dosage) {
        Log.e(TAG,"  UNDO Med refused: "+roomNumber+"  name:"+genericName+"   dosage: "+String.valueOf(dosage));
    }

}
