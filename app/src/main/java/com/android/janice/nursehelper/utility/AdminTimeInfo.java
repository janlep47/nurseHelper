package com.android.janice.nursehelper.utility;

import android.content.Context;

import com.android.janice.nursehelper.R;

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

    public String getDisplayableTime(Context context) {
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
            String dateFormat = context.getString(R.string.format_admin_date);
            dateTimeString = new SimpleDateFormat(dateFormat, Locale.US).format(date) + " ";
        }

        String minsString = String.valueOf(mins);
        // add leading zero for minutes, if any
        if (mins < 10) minsString = "0"+minsString;
        dateTimeString += String.valueOf(hrs)+((mins != 0) ?
                ":"+minsString : "");
        if (pm) {
            String pmString = context.getString(R.string.pm_string);
            dateTimeString += " "+pmString;
        } else {
            String amString = context.getString(R.string.am_string);
            dateTimeString += " "+amString;
        }
        return dateTimeString;
    }
}
