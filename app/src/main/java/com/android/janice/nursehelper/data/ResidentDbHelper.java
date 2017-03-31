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
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_RESIDENTS_TABLE =
                "CREATE TABLE " + ResidentContract.ResidentEntry.TABLE_NAME +
                        "( " + ResidentContract.ResidentEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER + " TEXT KEY, " +
                        ResidentContract.ResidentEntry.COLUMN_PORTRAIT_FILEPATH + " TEXT, " +
                        ResidentContract.ResidentEntry.COLUMN_CAREPLAN_FILEPATH + " TEXT);";

        db.execSQL(SQL_CREATE_RESIDENTS_TABLE);

        final String SQL_CREATE_MEDICATIONS_TABLE =
                "CREATE TABLE " + ResidentContract.MedicationEntry.TABLE_NAME +
                        "( " + ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER + " TEXT KEY, " +
                        ResidentContract.MedicationEntry.COLUMN_NAME_GENERIC + " TEXT NOT NULL, " +
                        ResidentContract.MedicationEntry.COLUMN_NAME_TRADE + " TEXT NOT NULL, " +
                        ResidentContract.MedicationEntry.COLUMN_DOSAGE + " REAL, " +
                        ResidentContract.MedicationEntry.COLUMN_DOSAGE_UNITS + " TEXT NOT NULL, " +
                        ResidentContract.MedicationEntry.COLUMN_DOSAGE_ROUTE + " TEXT NOT NULL, " +
                        ResidentContract.MedicationEntry.COLUMN_FREQUENCY + " TEXT NOT NULL, " +
                        ResidentContract.MedicationEntry.COLUMN_TIMES + " TEXT, " +
                        ResidentContract.MedicationEntry.COLUMN_LAST_GIVEN + " TIMESTAMP, " +
                        ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME + " TEXT, " +
                        ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME_LONG + " BIGINT);";

        db.execSQL(SQL_CREATE_MEDICATIONS_TABLE);

        final String SQL_CREATE_ASSESSMENTS_TABLE =
                "CREATE TABLE " + ResidentContract.AssessmentEntry.TABLE_NAME +
                        "( " + ResidentContract.AssessmentEntry.COLUMN_ROOM_NUMBER + " TEXT KEY, " +
                        ResidentContract.AssessmentEntry.COLUMN_BLOOD_PRESSURE + " TEXT, " +
                        ResidentContract.AssessmentEntry.COLUMN_TEMPERATURE + " TEXT, " +
                        ResidentContract.AssessmentEntry.COLUMN_PULSE + " INTEGER, " +
                        ResidentContract.AssessmentEntry.COLUMN_RR + " INTEGER, " +
                        ResidentContract.AssessmentEntry.COLUMN_EDEMA + " TEXT, " +
                        ResidentContract.AssessmentEntry.COLUMN_EDEMA_LOCN + " TEXT, " +
                        ResidentContract.AssessmentEntry.COLUMN_EDEMA_PITTING + " BOOLEAN, " +
                        ResidentContract.AssessmentEntry.COLUMN_PAIN + " INTEGER, " +
                        ResidentContract.AssessmentEntry.COLUMN_SIGNIFICANT_FINDINGS + " TEXT, " +
                        ResidentContract.AssessmentEntry.COLUMN_TIME + " TIMESTAMP);";

        db.execSQL(SQL_CREATE_ASSESSMENTS_TABLE);

        final String SQL_CREATE_MEDS_GIVEN_TABLE =
                "CREATE TABLE " + ResidentContract.MedsGivenEntry.TABLE_NAME +
                        "( " + ResidentContract.MedsGivenEntry.COLUMN_ROOM_NUMBER + " TEXT KEY, " +
                        ResidentContract.MedsGivenEntry.COLUMN_NAME_GENERIC + " TEXT, " +
                        ResidentContract.MedsGivenEntry.COLUMN_DOSAGE + " REAL, " +
                        ResidentContract.MedsGivenEntry.COLUMN_DOSAGE_UNITS + " TEXT, " +
                        ResidentContract.MedsGivenEntry.COLUMN_GIVEN + " BOOLEAN, " +
                        ResidentContract.MedsGivenEntry.COLUMN_NURSE + " TEXT, " +
                        ResidentContract.MedsGivenEntry.COLUMN_TIME_GIVEN + " TIMESTAMP);";

        db.execSQL(SQL_CREATE_MEDS_GIVEN_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        // just delete all tables if structure changes:
        db.execSQL("DROP TABLE IF EXISTS " + ResidentContract.ResidentEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ResidentContract.MedicationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ResidentContract.AssessmentEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ResidentContract.MedsGivenEntry.TABLE_NAME);
        onCreate(db);
        return;
    }
}
