package com.android.janice.nursehelper.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by janicerichards on 2/1/17.
 */

public class ResidentProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ResidentDbHelper mOpenHelper;

    static final int RESIDENTS = 100;              // PATH  residents path (DIR)
    static final int MEDICATIONS_WITH_ROOM_NUMBER = 201;  // PATH/*  medications path followed by a String (ITEM)
    static final int MEDICATIONS_WITH_ROOM_NUMBER_AND_MED = 202;  // PATH/*  medications path followed by a String (ITEM)
    static final int ASSESSMENTS_WITH_ROOM_NUMBER = 301;  // PATH/*  assessments path followed by a String (ITEM)
    static final int MEDS_GIVEN_WITH_ROOM_NUMBER = 401;  // PATH/*/* medsGiven path followed by 2 strings (ITEMs)
    static final int MEDS_GIVEN_WITH_ROOM_NUMBER_AND_MED = 402;  // PATH/*/* medsGiven path followed by 2 strings (ITEMs)

    public static final String LOG_TAG = ResidentProvider.class.getSimpleName();


    // get list of all medications by room# (or patient id)
    private static final String sMedsByResidentSelection =
            ResidentContract.MedicationEntry.TABLE_NAME+
                    "." + ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER + " = ? ";

    // get list of all medications by room# (or patient id)
    public static final String sMedsByResidentAndMedSelection =
            ResidentContract.MedicationEntry.TABLE_NAME+
                    "." + ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER + " = ? AND "+
                    ResidentContract.MedicationEntry.TABLE_NAME+
                    "." + ResidentContract.MedicationEntry.COLUMN_NAME_GENERIC + " = ? ";

    // get most recent assessment by room# (or patient id)
    private static final String sRecentAssessmentByResidentSelection =
            ResidentContract.AssessmentEntry.TABLE_NAME+
                    "." + ResidentContract.AssessmentEntry.COLUMN_ROOM_NUMBER + " = ? ";

    private static final String sMedsGivenByResidentSelection =
            ResidentContract.MedsGivenEntry.TABLE_NAME+
                    "." + ResidentContract.MedsGivenEntry.COLUMN_ROOM_NUMBER + " = ? ";

    private static final String sMedsGivenByResidentAndMedSelection =
            ResidentContract.MedsGivenEntry.TABLE_NAME+
                    "." + ResidentContract.MedsGivenEntry.COLUMN_ROOM_NUMBER + " = ? AND "+
                    ResidentContract.MedsGivenEntry.TABLE_NAME+
                    "." + ResidentContract.MedsGivenEntry.COLUMN_NAME_GENERIC + " = ? ";

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ResidentContract.CONTENT_AUTHORITY;

        // Create a corresponding code.
        matcher.addURI(authority, ResidentContract.PATH_RESIDENTS, RESIDENTS);
        matcher.addURI(authority, ResidentContract.PATH_MEDS + "/*", MEDICATIONS_WITH_ROOM_NUMBER);
        matcher.addURI(authority, ResidentContract.PATH_MEDS + "/*/*", MEDICATIONS_WITH_ROOM_NUMBER_AND_MED);
        matcher.addURI(authority, ResidentContract.PATH_ASSESSMENTS + "/*", ASSESSMENTS_WITH_ROOM_NUMBER);
        matcher.addURI(authority, ResidentContract.PATH_MEDS_GIVEN + "/*", MEDS_GIVEN_WITH_ROOM_NUMBER);
        matcher.addURI(authority, ResidentContract.PATH_MEDS_GIVEN + "/*/*", MEDS_GIVEN_WITH_ROOM_NUMBER_AND_MED);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ResidentDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case RESIDENTS:
                return ResidentContract.ResidentEntry.CONTENT_TYPE;        // DIR
            case MEDICATIONS_WITH_ROOM_NUMBER:
                return ResidentContract.MedicationEntry.CONTENT_ITEM_TYPE;   // ITEM
            case MEDICATIONS_WITH_ROOM_NUMBER_AND_MED:
                return ResidentContract.MedicationEntry.CONTENT_ITEM_TYPE;   // ITEM
            case ASSESSMENTS_WITH_ROOM_NUMBER:
                return ResidentContract.AssessmentEntry.CONTENT_ITEM_TYPE;   // ITEM
            case MEDS_GIVEN_WITH_ROOM_NUMBER:
                return ResidentContract.MedsGivenEntry.CONTENT_ITEM_TYPE;    // ITEM
            case MEDS_GIVEN_WITH_ROOM_NUMBER_AND_MED:
                return ResidentContract.MedsGivenEntry.CONTENT_ITEM_TYPE;    // ITEM
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private Cursor getResidents(Uri uri) {
        Cursor cursor = mOpenHelper.getReadableDatabase().query(
                ResidentContract.ResidentEntry.TABLE_NAME,
                new String[] {ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER},
                null,null,
                null, null, "ASC");
        return cursor;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "residents"
            case RESIDENTS:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ResidentContract.ResidentEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null, sortOrder);
                break;
            }

            // "meds/*"
            case MEDICATIONS_WITH_ROOM_NUMBER:
            {
                retCursor = getMedicationsByPatient(uri, projection, sortOrder);
                break;
            }

            case MEDICATIONS_WITH_ROOM_NUMBER_AND_MED:
            {
                retCursor = getMedicationsByPatientAndMed(uri, projection, sortOrder);
                break;
            }

            // "assessments/*"
            case ASSESSMENTS_WITH_ROOM_NUMBER:
            {
                retCursor = getAssessmentByPatient(uri, projection, sortOrder);
                break;
            }

            // "medsGiven/*"
            case MEDS_GIVEN_WITH_ROOM_NUMBER:
            {
                retCursor = getMedsGivenByPt(uri, projection, sortOrder);
                break;
            }

            // "medsGiven/*/*"
            case MEDS_GIVEN_WITH_ROOM_NUMBER_AND_MED:
            {
                retCursor = getMedsGivenByPtAndMedname(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    private Cursor getMedicationsByPatient(Uri uri, String[] projection, String sortOrder) {
        String roomNumber = ResidentContract.MedicationEntry.getRoomNumberFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(ResidentContract.MedicationEntry.TABLE_NAME,
                projection, sMedsByResidentSelection, new String[] {roomNumber}, null, null, sortOrder);
    }


    private Cursor getMedicationsByPatientAndMed(Uri uri, String[] projection, String sortOrder) {
        String roomNumber = ResidentContract.MedicationEntry.getRoomNumberFromUri(uri);
        String medName = ResidentContract.MedicationEntry.getMedNameFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(ResidentContract.MedicationEntry.TABLE_NAME,
                projection, sMedsByResidentAndMedSelection, new String[] {roomNumber,medName}, null, null, sortOrder);
    }


    private Cursor getAssessmentByPatient(Uri uri, String[] projection, String sortOrder) {
        String roomNumber = ResidentContract.AssessmentEntry.getRoomNumberFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(ResidentContract.AssessmentEntry.TABLE_NAME,
                projection, sRecentAssessmentByResidentSelection, new String[] {roomNumber}, null, null, sortOrder);
    }

    private Cursor getMedsGivenByPt(Uri uri, String[] projection, String sortOrder) {
        String roomNumber = ResidentContract.MedsGivenEntry.getRoomNumberFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(ResidentContract.MedsGivenEntry.TABLE_NAME,
                projection, sMedsGivenByResidentSelection, new String[] {roomNumber},
                null, null, sortOrder);
    }

    private Cursor getMedsGivenByPtAndMedname(Uri uri, String[] projection, String sortOrder) {
        String roomNumber = ResidentContract.MedsGivenEntry.getRoomNumberFromUri(uri);
        String medName = ResidentContract.MedsGivenEntry.getMedNameFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(ResidentContract.MedsGivenEntry.TABLE_NAME,
                projection, sMedsGivenByResidentAndMedSelection, new String[] {roomNumber, medName},
                null, null, sortOrder);
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case RESIDENTS: {
                long _id = db.insert(ResidentContract.ResidentEntry.TABLE_NAME, null, values);
                if ( _id > 0 ) returnUri = ResidentContract.ResidentEntry.buildResidentsUri(_id);
                else throw new android.database.SQLException("Failed to insert row into (residents)" + uri);
                break;
            }
            case MEDICATIONS_WITH_ROOM_NUMBER: {
                String roomNumber = values.getAsString("ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER");
                long _id = db.insert(ResidentContract.MedicationEntry.TABLE_NAME, null, values);
                if ( _id > 0) returnUri = ResidentContract.MedicationEntry.buildMedsWithRoomNumber(roomNumber);
                else throw new android.database.SQLException("Failed to insert row into (meds)" + uri);
                break;
            }
            case MEDICATIONS_WITH_ROOM_NUMBER_AND_MED: {
                String roomNumber = values.getAsString("ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER");
                long _id = db.insert(ResidentContract.MedicationEntry.TABLE_NAME, null, values);
                if ( _id > 0) returnUri = ResidentContract.MedicationEntry.buildMedsWithRoomNumber(roomNumber);
                else throw new android.database.SQLException("Failed to insert row into (meds)" + uri);
                break;
            }
            case ASSESSMENTS_WITH_ROOM_NUMBER: {
                String roomNumber = values.getAsString("ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER");
                long _id = db.insert(ResidentContract.AssessmentEntry.TABLE_NAME, null, values);
                if ( _id > 0) returnUri = ResidentContract.AssessmentEntry.buildAssessmentsWithRoomNumber(roomNumber);
                else throw new android.database.SQLException("Failed to insert row into (assessments)" + uri);
                break;
            }
            case MEDS_GIVEN_WITH_ROOM_NUMBER: {
                String roomNumber = values.getAsString("ResidentContract.MedsGivenEntry.COLUMN_ROOM_NUMBER");
                long _id = db.insert(ResidentContract.MedsGivenEntry.TABLE_NAME, null, values);
                Log.e(LOG_TAG, "  HERE in insert: values is "+values.toString());
                if ( _id > 0) returnUri = ResidentContract.MedsGivenEntry.buildMedsGivenWithRoomNumber(roomNumber);
                else throw new android.database.SQLException("Failed to insert row into (medsGiven)" + uri);
                break;
            }
            case MEDS_GIVEN_WITH_ROOM_NUMBER_AND_MED: {
                String roomNumber = values.getAsString("ResidentContract.MedsGivenEntry.COLUMN_ROOM_NUMBER");
                String medName = values.getAsString("ResidentContract.MedsGivenEntry.COLUMN_NAME_GENERIC");
                long _id = db.insert(ResidentContract.MedsGivenEntry.TABLE_NAME, null, values);
                if ( _id > 0) returnUri = ResidentContract.MedsGivenEntry.buildMedsGivenWithRoomNumberAndMed(roomNumber,medName);
                else throw new android.database.SQLException("Failed to insert row into (medsGiven)" + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // (don't use returnUri in call below, or else won't notify the Cursor(s) of the change.
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case RESIDENTS:
                rowsDeleted = db.delete(
                        ResidentContract.ResidentEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MEDICATIONS_WITH_ROOM_NUMBER:
                rowsDeleted = db.delete(
                        ResidentContract.MedicationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MEDICATIONS_WITH_ROOM_NUMBER_AND_MED:
                rowsDeleted = db.delete(
                        ResidentContract.MedicationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ASSESSMENTS_WITH_ROOM_NUMBER:
                rowsDeleted = db.delete(
                        ResidentContract.AssessmentEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MEDS_GIVEN_WITH_ROOM_NUMBER:
                rowsDeleted = db.delete(
                        ResidentContract.MedsGivenEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MEDS_GIVEN_WITH_ROOM_NUMBER_AND_MED:
                rowsDeleted = db.delete(
                        ResidentContract.MedsGivenEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) getContext().getContentResolver().notifyChange(uri, null);
        Log.e(LOG_TAG,String.valueOf(rowsDeleted) + " DB ROWS DELETED ...");
        return rowsDeleted;
    }


    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case RESIDENTS:
                rowsUpdated = db.update(ResidentContract.ResidentEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MEDICATIONS_WITH_ROOM_NUMBER:
                rowsUpdated = db.update(ResidentContract.MedicationEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MEDICATIONS_WITH_ROOM_NUMBER_AND_MED:
                rowsUpdated = db.update(ResidentContract.MedicationEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ASSESSMENTS_WITH_ROOM_NUMBER:
                rowsUpdated = db.update(ResidentContract.AssessmentEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MEDS_GIVEN_WITH_ROOM_NUMBER:
                rowsUpdated = db.update(ResidentContract.MedsGivenEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MEDS_GIVEN_WITH_ROOM_NUMBER_AND_MED:
                rowsUpdated = db.update(ResidentContract.MedsGivenEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case RESIDENTS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ResidentContract.ResidentEntry.TABLE_NAME, null, value);
                        if (_id != -1) returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case MEDICATIONS_WITH_ROOM_NUMBER:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ResidentContract.MedicationEntry.TABLE_NAME, null, value);
                        if (_id != -1) returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case MEDICATIONS_WITH_ROOM_NUMBER_AND_MED:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ResidentContract.MedicationEntry.TABLE_NAME, null, value);
                        if (_id != -1) returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case ASSESSMENTS_WITH_ROOM_NUMBER:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ResidentContract.AssessmentEntry.TABLE_NAME, null, value);
                        if (_id != -1) returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case MEDS_GIVEN_WITH_ROOM_NUMBER:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ResidentContract.MedsGivenEntry.TABLE_NAME, null, value);
                        if (_id != -1) returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case MEDS_GIVEN_WITH_ROOM_NUMBER_AND_MED:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ResidentContract.MedsGivenEntry.TABLE_NAME, null, value);
                        if (_id != -1) returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                throw new UnsupportedOperationException(" (bulkInsert) unknown uri: "+uri);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

}
