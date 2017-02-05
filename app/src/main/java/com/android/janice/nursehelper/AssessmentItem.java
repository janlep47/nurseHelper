package com.android.janice.nursehelper;

//import java.sql.Time;
import android.database.Cursor;

import java.util.Date;

/**
 * Created by janicerichards on 2/5/17.
 */

public class AssessmentItem {

    private String roomNumber;
    private String bloodPressure;
    private String temperature;
    private int pulse;
    private int respiratoryRate;
    private String edema;
    private String significantFindings;
    private Date dateTime;


    //boolean mAddProblem = false;
    public final static int COL_ROOM_NUMBER = 0;
    public final static int COL_BLOOD_PRESSURE = 1;
    public final static int COL_TEMPERATURE = 2;
    public final static int COL_PULSE = 3;
    public final static int COL_RR = 4;
    public final static int COL_EDEMA = 5;
    public final static int COL_SIGNIFICANT_FINDINGS = 6;
    public final static int COL_DATE_TIME = 7;


    public String getRoomNumber() { return roomNumber; }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public String getTemperature() {
        return temperature;
    }

    public int getPulse() { return pulse; }

    public int getRespiratoryRate() { return respiratoryRate; }

    public String getEdema() { return edema; }

    public String getSignificantFindings() { return significantFindings; }

    public Date getDateTime() { return dateTime; }

    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public void setBloodPressure(String bp) {
        this.bloodPressure = bp;
    }

    public void setTemperature(String temp) { this.temperature = temp; }

    public void setPulse(int pulse) { this.pulse = pulse; }

    public void setRespiratoryRate(int rr) { this.respiratoryRate = rr; }

    public void setEdema(String edema) { this.edema = edema; }

    public void setSignificantFindings(String findings) { this.significantFindings = findings; }

    public void setDateTime(Date dateTime) { this.dateTime = dateTime; }

    public AssessmentItem(Cursor cursor) {
        roomNumber = cursor.getString(COL_ROOM_NUMBER);
        bloodPressure = cursor.getString(COL_BLOOD_PRESSURE);
        temperature = cursor.getString(COL_TEMPERATURE);
        pulse = cursor.getInt(COL_PULSE);
        respiratoryRate = cursor.getInt(COL_RR);
        edema = cursor.getString(COL_EDEMA);
        significantFindings = cursor.getString(COL_SIGNIFICANT_FINDINGS);
        // formate the sql Time type into util Date type:  (later)
        //long time = cursor.getLong(COL_DATE_TIME);
        //dateTime = Date.
    }

}
