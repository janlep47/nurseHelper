package com.android.janice.nursehelper.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by janicerichards on 2/1/17.
 */

public class ResidentDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "charting.db";

    public ResidentDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_RESIDENTS_TABLE =
                "CREATE TABLE "+ ResidentContract.ResidentEntry.TABLE_NAME +
                        "( " + ResidentContract.ResidentEntry.COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER +" TEXT KEY, " +
                        ResidentContract.ResidentEntry.COLUMN_PORTRAIT + " BLOB);";

        db.execSQL(SQL_CREATE_RESIDENTS_TABLE);

        final String SQL_CREATE_MEDICATIONS_TABLE =
                "CREATE TABLE "+ ResidentContract.MedicationEntry.TABLE_NAME +
                        "( " + ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER + " TEXT KEY, " +
                        ResidentContract.MedicationEntry.COLUMN_NAME_GENERIC + " TEXT NOT NULL, " +
                        ResidentContract.MedicationEntry.COLUMN_NAME_TRADE + " TEXT NOT NULL, " +
                        ResidentContract.MedicationEntry.COLUMN_DOSAGE + " REAL, " +
                        ResidentContract.MedicationEntry.COLUMN_DOSAGE_UNITS + " TEXT NOT NULL, " +
                        ResidentContract.MedicationEntry.COLUMN_DOSAGE_ROUTE + " TEXT NOT NULL, " +
                        ResidentContract.MedicationEntry.COLUMN_FREQUENCY + " TEXT NOT NULL, " +
                        ResidentContract.MedicationEntry.COLUMN_TIMES + " TEXT);";

        db.execSQL(SQL_CREATE_MEDICATIONS_TABLE);

        final String SQL_CREATE_ASSESSMENTS_TABLE =
                "CREATE TABLE "+ ResidentContract.AssessmentEntry.TABLE_NAME +
                        "( " + ResidentContract.AssessmentEntry.COLUMN_ROOM_NUMBER + " TEXT KEY, " +
                        ResidentContract.AssessmentEntry.COLUMN_BLOOD_PRESSURE + " TEXT, " +
                        ResidentContract.AssessmentEntry.COLUMN_TEMPERATURE + " TEXT, " +
                        ResidentContract.AssessmentEntry.COLUMN_PULSE + " INTEGER, " +
                        ResidentContract.AssessmentEntry.COLUMN_RR + " INTEGER, " +
                        ResidentContract.AssessmentEntry.COLUMN_EDEMA + " TEXT, " +
                        ResidentContract.AssessmentEntry.COLUMN_SIGNIFICANT_FINDINGS + " TEXT, " +
                        ResidentContract.AssessmentEntry.COLUMN_TIME + " TIME);";  // MAY NOT BE RIGHT!!!!

        db.execSQL(SQL_CREATE_ASSESSMENTS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        // just delete all tables if structure changes:
        db.execSQL("DROP TABLE IF EXISTS " + ResidentContract.ResidentEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ResidentContract.MedicationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ResidentContract.AssessmentEntry.TABLE_NAME);
        onCreate(db);
        return;
    }
}
