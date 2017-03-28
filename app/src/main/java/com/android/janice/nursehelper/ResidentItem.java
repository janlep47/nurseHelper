package com.android.janice.nursehelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.android.janice.nursehelper.data.ResidentContract;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

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

        String[] afiles = null;
        AssetManager assetManager = context.getAssets();
        //assetManager.
        try {
            afiles = assetManager.list("");
        } catch (IOException e) {
            Log.e("RESIDENTITEM","  IOException "+e.toString());
        }
        if (afiles == null) {
            Log.e("RESIDENTITEM"," ERROR files are NULL");
            return;
        }
        if (afiles.length < 1) {
            Log.e("RESIDENTITEM"," NO FILES READ FROM ASSETS");
            return;
        }
        String[] files = new String[10];
        int n = 0;
        HashMap<String,String> careplanFiles = new HashMap<String,String>();
        for (int i = 0; i < afiles.length; i++) {
            if (afiles[i].startsWith("CarePlan1")) {
                careplanFiles.put("1", afiles[i]);
            } else if (afiles[i].startsWith("CarePlan8")) {
                careplanFiles.put("2", afiles[i]);
            } else if (afiles[i].startsWith("CarePlanNONE")) {

            } else {
                files[n] = afiles[i];
                n++;
                if (n >= 10) break;
            }
        }

        String prefix = "file:///android_asset/";  // Using fake images for now ...
        for (int i = files.length - 1; i >= 0; i--) {
            String fileName = files[i];
            if (!fileName.startsWith("av")) continue;
            String roomNumber = String.valueOf(200+i);

            // First verify the resident not already in the Content provider:
            //  (this is just for adding dummy data anyway, though):
            Uri testUri = ResidentContract.ResidentEntry.CONTENT_URI;
            testUri = testUri.buildUpon().appendPath(roomNumber).build();
            Cursor testCursor = context.getContentResolver().query(testUri,
                    new String[]{ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER},
                    null, null, null);

            if (testCursor != null) {
                if (testCursor.moveToFirst()) {
                    testCursor.close();
                    continue;  // This resident ALREADY EXISTS, skip to next one to add
                }
                testCursor.close();
            }


            ContentValues residentValues = new ContentValues();

            // Update the local device database
            residentValues.put(ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER, roomNumber);
            residentValues.put(ResidentContract.ResidentEntry.COLUMN_PORTRAIT_FILEPATH, prefix+fileName);
            if (roomNumber.equals("200"))
                residentValues.put(ResidentContract.ResidentEntry.COLUMN_CAREPLAN_FILEPATH,careplanFiles.get("1"));
            else if (roomNumber.equals("208"))
                residentValues.put(ResidentContract.ResidentEntry.COLUMN_CAREPLAN_FILEPATH,careplanFiles.get("2"));

            Uri uri = context.getContentResolver().insert(ResidentContract.ResidentEntry.CONTENT_URI, residentValues);

            // Now update the central Firebase database
            UpdateResidentTask updateResidentTask = new UpdateResidentTask(context,database,userId);
            updateResidentTask.execute(residentValues);
        }
    }



    private static class UpdateResidentTask extends AsyncTask<ContentValues, Void, Void> {
        protected Context context;
        protected DatabaseReference database;
        protected String userId;

        public UpdateResidentTask(Context context, DatabaseReference database, String userId) {
            this.context = context;
            this.database = database;
            this.userId = userId;
        }

        @Override
        protected Void doInBackground(ContentValues... params) {
            // Add the new resident record to the Firebase database.  This is used here ONLY
            //   for adding fake testing data.  Actual resident data will ONLY be entered at the
            //   Firebase console, and downloaded to this app.

            ContentValues residentValues = params[0];
            //String residentId = database.child("users").child(userId).child("residents").push().getKey();
            String residentId = database.child(ResidentContract.PATH_USERS).child(userId)
                    .child(ResidentContract.ResidentEntry.TABLE_NAME).push().getKey();
            ArrayList<String> keys = new ArrayList<String>(residentValues.keySet());
            for (int i = 0; i < keys.size(); i++) {
                Object value = residentValues.get(keys.get(i));
                //database.child("users").child(userId).child("residents").child(residentId).child(keys.get(i)).setValue(value);
                database.child(ResidentContract.PATH_USERS).child(userId)
                        .child(ResidentContract.ResidentEntry.TABLE_NAME).child(residentId)
                        .child(keys.get(i)).setValue(value);
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

}
