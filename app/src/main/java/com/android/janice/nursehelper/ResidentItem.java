package com.android.janice.nursehelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.android.janice.nursehelper.data.ResidentContract;
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
    private Bitmap portrait;


    public String getRoomNumber() { return roomNumber; }

    public Bitmap getPortrait() { return portrait; }

    protected void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    protected void setPortrait(Bitmap portrait) { this.portrait = portrait; }

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

    public static void putInDummyData(Context context) {
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
        for (int i = 0; i < files.length; i++){
            String fileName = files[i];
            if (!fileName.startsWith("av")) continue;
            String roomNumber = String.valueOf(200+i);

            ContentValues residentValues = new ContentValues();

            residentValues.put(ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER, roomNumber);
            residentValues.put(ResidentContract.ResidentEntry.COLUMN_PORTRAIT_FILEPATH, fileName);
            Log.i("ResidentItem", "   ok room#="+roomNumber+"  filename = "+fileName);
            Uri uri = context.getContentResolver().insert(ResidentContract.ResidentEntry.CONTENT_URI, residentValues);

            /*
            try {
                InputStream input = assetManager.open(imgName);
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                byte[] bytes;
                reader.read
            } catch (IOException e) {
                Log.e("RESIDENTITEM"," Can't open asset file: "+imgName);
            }
            */
        }
        /*
        InputStream is = getAssets().open("contacts.csv");
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(is);
        HttpContext localContext = new BasicHttpContext();
        HttpResponse response =httpClient.execute(httpGet, localContext);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
*/

    }

}
