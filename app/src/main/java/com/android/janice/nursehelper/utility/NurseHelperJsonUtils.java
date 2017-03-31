/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.janice.nursehelper.utility;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.android.janice.nursehelper.data.NurseHelperPreferences;
import com.android.janice.nursehelper.data.ResidentContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Utility functions to handle NurseHelper Firebase database JSON data.
 */
public final class NurseHelperJsonUtils {

    public static ContentValues[] getResidentContentValuesFromJson(Context context, String residentJsonStr)
            throws JSONException {

        JSONObject residentJson = new JSONObject(residentJsonStr);

        Iterator<String> iterator = residentJson.keys();
        ArrayList<ContentValues> residentList = new ArrayList<ContentValues>();
        while (iterator.hasNext()) {
            String key = iterator.next();
            JSONObject residentObject = residentJson.getJSONObject(key);
            String roomNumber = (String) residentObject.get(ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER);
            String portraitFilepath = (String) residentObject.get(ResidentContract.ResidentEntry.COLUMN_PORTRAIT_FILEPATH);
            String careplanFilepath;
            if (residentObject.has(ResidentContract.ResidentEntry.COLUMN_CAREPLAN_FILEPATH))
                careplanFilepath = (String) residentObject.get(ResidentContract.ResidentEntry.COLUMN_CAREPLAN_FILEPATH);
            else careplanFilepath = "";

            ContentValues residentValue = new ContentValues();
            residentValue.put(ResidentContract.ResidentEntry.COLUMN_ROOM_NUMBER, roomNumber);
            residentValue.put(ResidentContract.ResidentEntry.COLUMN_PORTRAIT_FILEPATH, portraitFilepath);
            residentValue.put(ResidentContract.ResidentEntry.COLUMN_CAREPLAN_FILEPATH, careplanFilepath);
            residentList.add(residentValue);
        }
        ContentValues[] residentValues = new ContentValues[residentList.size()];
        residentList.toArray(residentValues);
        return residentValues;
    }


    public static ContentValues[] getMedicationContentValuesFromJson(Context context, String medicationJsonStr)
            throws JSONException {

        JSONObject medicationJson = new JSONObject(medicationJsonStr);

        Iterator<String> iterator = medicationJson.keys();
        ArrayList<ContentValues> medicationList = new ArrayList<ContentValues>();
        while (iterator.hasNext()) {
            String key = iterator.next();
            JSONObject medicationObject = medicationJson.getJSONObject(key);
            String roomNumber = (String) medicationObject.get(ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER);
            String genericName = (String) medicationObject.get(ResidentContract.MedicationEntry.COLUMN_NAME_GENERIC);
            String tradeName;
            if (medicationObject.has(ResidentContract.MedicationEntry.COLUMN_NAME_TRADE))
                tradeName = (String) medicationObject.get(ResidentContract.MedicationEntry.COLUMN_NAME_TRADE);
            else tradeName = "";
            double dosage = 0;
            Double val;
            Long lval;
            Integer ival;
            if (medicationObject.has(ResidentContract.MedicationEntry.COLUMN_DOSAGE)) {
                try {
                    val = (Double) medicationObject.get(ResidentContract.MedicationEntry.COLUMN_DOSAGE);
                    dosage = val.doubleValue();
                } catch (Exception e) {
                    try {
                        lval = (Long) medicationObject.get(ResidentContract.MedicationEntry.COLUMN_DOSAGE);
                        dosage = lval.doubleValue();
                    } catch (Exception e1) {

                    }
                }
            }
            String dosageUnits;
            if (medicationObject.has(ResidentContract.MedicationEntry.COLUMN_DOSAGE_UNITS))
                dosageUnits = (String) medicationObject.get(ResidentContract.MedicationEntry.COLUMN_DOSAGE_UNITS);
            else
                dosageUnits = "";
            String dosageRoute;
            if (medicationObject.has(ResidentContract.MedicationEntry.COLUMN_DOSAGE_ROUTE))
                dosageRoute = (String) medicationObject.get(ResidentContract.MedicationEntry.COLUMN_DOSAGE_ROUTE);
            else
                dosageRoute = "";
            String freq;
            if (medicationObject.has(ResidentContract.MedicationEntry.COLUMN_FREQUENCY))
                freq = (String) medicationObject.get(ResidentContract.MedicationEntry.COLUMN_FREQUENCY);
            else
                freq = "";
            String adminTimes;
            if (medicationObject.has(ResidentContract.MedicationEntry.COLUMN_TIMES))
                adminTimes = (String) medicationObject.get(ResidentContract.MedicationEntry.COLUMN_TIMES);
            else
                adminTimes = "";
            long lastGivenTime = 0;
            if (medicationObject.has(ResidentContract.MedicationEntry.COLUMN_LAST_GIVEN)) {
                try {
                    lval = (Long) medicationObject.get(ResidentContract.MedicationEntry.COLUMN_LAST_GIVEN);
                    lastGivenTime = lval.longValue();
                } catch (Exception e) {
                    try {
                        ival = (Integer) medicationObject.get(ResidentContract.MedicationEntry.COLUMN_LAST_GIVEN);
                        lastGivenTime = ival.longValue();
                    } catch (Exception e1) {

                    }
                }
            }
            String nextDosageTime;
            if (medicationObject.has(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME))
                nextDosageTime = (String) medicationObject.get(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME);
            else
                nextDosageTime = "";
            long nextDosageTimeLong = 0;
            if (medicationObject.has(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME_LONG)) {
                try {
                    lval = (Long) medicationObject.get(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME_LONG);
                    nextDosageTimeLong = lval.longValue();
                } catch (Exception e) {
                    try {
                        ival = (Integer) medicationObject.get(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME_LONG);
                        nextDosageTimeLong = ival.longValue();
                    } catch (Exception e1) {

                    }
                }
            }
            ContentValues residentValue = new ContentValues();
            residentValue.put(ResidentContract.MedicationEntry.COLUMN_ROOM_NUMBER, roomNumber);
            residentValue.put(ResidentContract.MedicationEntry.COLUMN_NAME_GENERIC, genericName);
            residentValue.put(ResidentContract.MedicationEntry.COLUMN_NAME_TRADE, tradeName);
            residentValue.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE, dosage);
            residentValue.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE_ROUTE, dosageRoute);
            residentValue.put(ResidentContract.MedicationEntry.COLUMN_DOSAGE_UNITS, dosageUnits);
            residentValue.put(ResidentContract.MedicationEntry.COLUMN_FREQUENCY, freq);
            residentValue.put(ResidentContract.MedicationEntry.COLUMN_TIMES, adminTimes);
            residentValue.put(ResidentContract.MedicationEntry.COLUMN_LAST_GIVEN, lastGivenTime);
            residentValue.put(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME, nextDosageTime);
            residentValue.put(ResidentContract.MedicationEntry.COLUMN_NEXT_DOSAGE_TIME_LONG, nextDosageTimeLong);
            medicationList.add(residentValue);
        }
        ContentValues[] medicationValues = new ContentValues[medicationList.size()];
        medicationList.toArray(medicationValues);
        return medicationValues;
    }
}