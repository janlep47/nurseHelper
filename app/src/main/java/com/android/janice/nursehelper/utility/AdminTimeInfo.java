package com.android.janice.nursehelper.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by janicerichards on 2/27/17.
 */

public class AdminTimeInfo {
    private int hrs = -1;
    private int mins = -1;
    private Calendar adminCalendarTime = null;
    private boolean pm = false;

    public AdminTimeInfo(int hrs, int mins, boolean pm) {
        this.hrs = hrs;
        this.mins = mins;
        this.pm = pm;
    }

    public AdminTimeInfo(Calendar calendar) {
        adminCalendarTime = calendar;
    }

    public int getHrs() {
        return hrs;
    }

    public int getMins() {
        return mins;
    }

    public boolean getIsPM() {
        return pm;
    }

    public void setCalendar(Calendar calendar) {
        adminCalendarTime = calendar;
    }

    public Calendar getCalendar() {
        return adminCalendarTime;
    }

    public String getDisplayableTime() {
        String dateTimeString = "";
        if (adminCalendarTime != null) {
            mins = adminCalendarTime.get(Calendar.MINUTE);
            hrs = adminCalendarTime.get(Calendar.HOUR);
            int amOrPm = adminCalendarTime.get(Calendar.AM_PM);
            if (amOrPm == Calendar.AM) pm = false;
            else pm = true;
            // LATER, allow military time as a sharedPreference ...
            //if (hrs > 12) {
            //    hrs -= 12;
            //    pm = true;
            //}
            Date date = adminCalendarTime.getTime();
            dateTimeString = new SimpleDateFormat("MM-dd-YY", Locale.US).format(date) + " ";
        }

        String minsString = String.valueOf(mins);
        if (mins < 10) minsString = "0"+minsString;
        dateTimeString += String.valueOf(hrs)+((mins != 0) ?
                ":"+minsString : "");
        if (pm) {
            dateTimeString += " PM";
        } else {
            dateTimeString += " AM";
        }
        return dateTimeString;
    }
}
