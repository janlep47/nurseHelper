package com.android.janice.nursehelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.android.janice.nursehelper.data.ResidentContract;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by janicerichards on 2/3/17.
 */

public class ResidentItem {
    private String roomNumber;
    private String portraitFilepath;

    public ResidentItem() {
    }

    public ResidentItem(String roomNumber, String portraitFilepath) {
        this.roomNumber = roomNumber;
        this.portraitFilepath = portraitFilepath;
    }


    public String getRoomNumber() { return roomNumber; }

    public String getPortraitFilepath() { return portraitFilepath; }

    protected void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    protected void setPortraitFilepath(String portraitFilepath) { this.portraitFilepath = portraitFilepath; }

    public static void deleteResidentFromDb(Context context, String roomNumber) {
        int rowsDeleted = context.getContentResolver().delete(
                ResidentContract.ResidentEntry.CONTENT_URI,
                ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER + " = ?",
                new String[]{roomNumber});
    }

    /*
    public static void addResidentToDb(Context context, Resident resident) {
        ContentValues ResidentValues = getResidentValues(context,resident);
        Uri uri = context.getContentResolver().insert(ResidentContract.ResidentEntry.CONTENT_URI, ResidentValues);
        return;
    }
*/


    public static void putInDummyData(Context context, DatabaseReference database, String userId) {
        // First see if any data in already; if so, just return
        Cursor cursor = context.getContentResolver().query(ResidentContract.ResidentEntry.CONTENT_URI,
                new String[]{ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER},
                null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                cursor.close();
                return;
            } else {
                cursor.close();
            }
        }

        String[] files = null;
        AssetManager assetManager = context.getAssets();
        //assetManager.
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("RESIDENTITEM","  IOException "+e.toString());
        }
        if (files == null) {
            Log.e("RESIDENTITEM"," ERROR files are NULL");
            return;
        }
        if (files.length < 1) {
            Log.e("RESIDENTITEM"," NO FILES READ FROM ASSETS");
            return;
        }
        for (int i = files.length - 1; i >= 0; i--) {
            String fileName = files[i];
            if (!fileName.startsWith("av")) continue;
            String roomNumber = String.valueOf(200+i);

            ContentValues residentValues = new ContentValues();

            // Update the local device database
            residentValues.put(ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER, roomNumber);
            residentValues.put(ResidentContract.ResidentEntry.COLUMN_PORTRAIT_FILEPATH, fileName);
            Uri uri = context.getContentResolver().insert(ResidentContract.ResidentEntry.CONTENT_URI, residentValues);

            // Now update the central Firebase database
            String residentId = database.child("users").child(userId).child("residents").push().getKey();
            database.child("users").child(userId).child("residents").child(residentId).child("roomNumber").setValue(roomNumber);
            database.child("users").child(userId).child("residents").child(residentId).child("portraitFilepath").setValue(fileName);

            //database.child("users").child(userId).child("residents").push().setValue(new ResidentItem(roomNumber,fileName));

        }
    }

}
