package com.android.janice.nursehelper.utility;

import android.content.Context;
import android.util.Log;

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
    private long time = 0;

    public AdminTimeInfo(int hrs, int mins, boolean pm) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, hrs);
        calendar.set(Calendar.MINUTE, mins);
        calendar.set(Calendar.AM_PM,pm ? Calendar.PM : Calendar.AM);
        time = calendar.getTimeInMillis();
        this.hrs = hrs;
        this.mins = mins;
        this.pm = pm;
    }

    public AdminTimeInfo(int year, int month, int day, int hrs, int mins, boolean pm) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_YEAR,day);
        calendar.set(Calendar.HOUR, hrs);
        calendar.set(Calendar.MINUTE, mins);
        calendar.set(Calendar.AM_PM,pm ? Calendar.PM : Calendar.AM);
        time = calendar.getTimeInMillis();
        this.hrs = hrs;
        this.mins = mins;
        this.pm = pm;
        this.adminCalendarTime = calendar;
    }

    public AdminTimeInfo(Calendar calendar) {
        adminCalendarTime = calendar;
        time = calendar.getTimeInMillis();
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
        time = calendar.getTimeInMillis();
    }

    public Calendar getCalendar() {
        return adminCalendarTime;
    }

    public long getTime() {
        return time;
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
        if (mins < 10 && mins > 0) minsString = "0"+minsString;
        dateTimeString += String.valueOf(hrs)+((mins != 0) ?
                ":"+minsString : "");
        if (pm) {
            dateTimeString += " "+context.getString(R.string.pm_string);
        } else {
            dateTimeString += " "+context.getString(R.string.am_string);
        }
        return dateTimeString;
    }
}
