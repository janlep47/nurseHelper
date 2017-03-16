package com.android.janice.nursehelper.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by janicerichards on 2/1/17.
 */

public class ResidentContract {
    // The "Content authority" is a name for the entire content provider.
    public static final String CONTENT_AUTHORITY = "com.android.janice.nursehelper";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // e.g. content://com.android.stocks/stocks/ is a valid path for at the stock data.
    public static final String PATH_RESIDENTS = "residents";
    public static final String PATH_MEDS = "medications";
    public static final String PATH_ASSESSMENTS = "assessments";
    public static final String PATH_MEDS_GIVEN = "medsGiven";


    /* Inner class that defines the table contents of the Residents table */
    public static final class ResidentEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RESIDENTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RESIDENTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RESIDENTS;

        public static final String TABLE_NAME = "residents";


        //_id INTEGER PRIMARY KEY AUTOINCREMENT
        public static final String COLUMN_ID = "_id";

        // primary key:  resident's room number:
        public static final String COLUMN_ROOM_NUMBER = "roomNumber";

        // long name
        public static final String COLUMN_PORTRAIT_FILEPATH = "residentPictureFile";


        public static Uri buildResidentsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildResidentInfoWithRoomNumber(String roomNumber) {
            return CONTENT_URI.buildUpon()
                    .appendPath(roomNumber).build();
        }

        public static String getRoomNumberFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }



    /* Inner class that defines the table contents of the Medications table */
    public static final class MedicationEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MEDS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MEDS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MEDS;

        public static final String TABLE_NAME = "medications";


        // PRIMARY KEY
        public static final String COLUMN_ROOM_NUMBER = "roomNumber";

        // generic medication name
        public static final String COLUMN_NAME_GENERIC = "medGenericName";

        // generic medication name
        public static final String COLUMN_NAME_TRADE = "medTradeName";

        // amount of med to give (float value)
        public static final String COLUMN_DOSAGE = "dosage";

        public static final String COLUMN_DOSAGE_UNITS = "dosageUnits";

        public static final String COLUMN_DOSAGE_ROUTE = "route";

        public static final String COLUMN_FREQUENCY = "frequency";  // e.g. "BID", etc.

        public static final String COLUMN_TIMES = "adminTimes";   // THIS IS AN ARRAY OF TIMES (hr:min)

        public static final String COLUMN_LAST_GIVEN = "lastGivenTime";

        public static final String COLUMN_NEXT_DOSAGE_TIME = "nextTimeToGive";

        public static final String COLUMN_NEXT_DOSAGE_TIME_LONG = "nextTimeToGiveLong"; // easy to sort


        public static Uri buildMedsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMedsWithRoomNumber(String roomNumber) {
            return CONTENT_URI.buildUpon()
                    .appendPath(roomNumber).build();
        }

        public static String getRoomNumberFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
        public static String getMedNameFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }



    /* Inner class that defines the table contents of the Assessments table */
    public static final class AssessmentEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ASSESSMENTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ASSESSMENTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ASSESSMENTS;

        public static final String TABLE_NAME = "assessments";


        //_id INTEGER PRIMARY KEY AUTOINCREMENT
        public static final String COLUMN_ID = "_id";

        // primary key:  room Number
        public static final String COLUMN_ROOM_NUMBER = "roomNumber";

        // Time, stored as long in milliseconds (when this assessment was done)
        public static final String COLUMN_TIME = "time";

        // ###/###
        public static final String COLUMN_BLOOD_PRESSURE = "bp";

        // String: ###.# <degree symbol>F / C  <route>
        public static final String COLUMN_TEMPERATURE = "temp";

        public static final String COLUMN_PULSE = "pulse";

        public static final String COLUMN_RR = "respiratoryRate";

        // edema: either "n/N" or "Stage I/II/III/IV".  If staged, give location in
        //  edemaLocn.  Additional information could also be entered in narrative
        //  "significantFindings"
        public static final String COLUMN_EDEMA = "edema";

        public static final String COLUMN_EDEMA_LOCN = "edemaLocn";

        public static final String COLUMN_EDEMA_PITTING = "edemaPitting";

        public static final String COLUMN_PAIN = "pain";

        public static final String COLUMN_SIGNIFICANT_FINDINGS = "significantFindings";


        public static Uri buildAssessmentsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildAssessmentsWithRoomNumber(String roomNumber) {
            return CONTENT_URI.buildUpon()
                    .appendPath(roomNumber).build();
        }

        public static String getRoomNumberFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }




    /* Inner class that defines the table contents of the Medications table */
    public static final class MedsGivenEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MEDS_GIVEN).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MEDS_GIVEN;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MEDS_GIVEN;

        public static final String TABLE_NAME = "medsGiven";


        // PRIMARY KEY
        public static final String COLUMN_ROOM_NUMBER = "roomNumber";

        // generic medication name (secondary key)
        public static final String COLUMN_NAME_GENERIC = "medGenericName";

        // amount of med to give (float value)
        public static final String COLUMN_DOSAGE = "dosage";

        public static final String COLUMN_DOSAGE_UNITS = "dosageUnits";

        public static final String COLUMN_GIVEN = "givenOrRefused";

        public static final String COLUMN_NURSE = "nurseName";

        public static final String COLUMN_TIME_GIVEN = "timeGiven";


        public static Uri buildMedsGivenWithRoomNumber(String roomNumber) {
            return CONTENT_URI.buildUpon()
                    .appendPath(roomNumber).build();
        }

        //SELECT * FROM [table] WHERE [column] = [value]; (Selectors: <, >, !=; combine multiple selectors with AND, OR)
        public static Uri buildMedsGivenWithRoomNumberAndMed(String roomNumber, String genericName) {
            return CONTENT_URI.buildUpon()
                    .appendPath(roomNumber).appendPath(genericName).build();
        }

        public static String getRoomNumberFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
        public static String getMedNameFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }


}
