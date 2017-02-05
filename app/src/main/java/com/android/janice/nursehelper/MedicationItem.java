package com.android.janice.nursehelper;

import android.database.Cursor;
import android.graphics.Bitmap;

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
}
