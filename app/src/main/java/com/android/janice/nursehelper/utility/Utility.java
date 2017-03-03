package com.android.janice.nursehelper.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import java.text.ParseException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


//import com.android.janice.nursehelper.sync.NurseHelperSyncAdapter;
import com.android.janice.nursehelper.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by janicerichards on 2/26/17.
 */

public class Utility {
    public static final String AM_STRING = "AM";
    public static final String PM_STRING = "PM";
    public static final String EVERY_CHARS = "Q";
    public static final String HOUR_MIN_SEPARATOR = ":";
    public static final String[] ALLOWED_FREQUENCIES = {
            "QD", "BID", "TID", "QID"
    };

    public static final int QD = 0;
    public static final int BID = 1;
    public static final int TID = 2;
    public static final int QID = 3;

    public static final String[] ALLOWED_TIME_PERIODS = {
            "MINUTES", "HOURS", "DAYS", "WEEKS", "MONTHS"
    };

    public static final String[] ALLOWED_TIME_PERIODS_ABBREV = {
            "MINS", "HRS", "DAYS", "WKS", "MNTHS"
    };

    public static final int MINS = 0;
    public static final int HOURS = 1;
    public static final int DAYS = 2;
    public static final int WEEKS = 3;
    public static final int MONTHS = 4;

    public static final long DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1);
    public static final long WEEK_IN_MILLIS = DAY_IN_MILLIS*7;
    public static final long HOUR_IN_MILLIS = TimeUnit.HOURS.toMillis(1);
    public static final long MINUTE_IN_MILLIS = TimeUnit.MINUTES.toMillis(1);





    public static String getReadableTimestamp(Context context, long timestamp) {
        //String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        //String formattedDate = new SimpleDateFormat("MM-DD-YY HH:mm:ss").format(timestamp);
        java.util.Date date = new java.util.Date(timestamp);
        String dateFormatter = context.getString(R.string.format_admin_date_time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (calendar.get(Calendar.HOUR) > 12) {
            calendar.set(Calendar.HOUR, Calendar.HOUR - 12);
            calendar.set(Calendar.AM_PM, Calendar.PM);
        } else {
            calendar.set(Calendar.AM_PM, Calendar.AM);
        }

        String formattedDate = new SimpleDateFormat(dateFormatter, Locale.US).format(calendar.getTime());
        return formattedDate;
    }


    public static AdminTimeInfo calculateNextDueTime(Context context, String adminTimes, String freq, long timeLastGiven) {
        if (aPrnMed(context, adminTimes, freq)) return null;

        // Get current time:
        Calendar calendar = Calendar.getInstance();
        AdminTimeInfo adminTimeInfo = null;

        if (anyAdminTimes(adminTimes)) {
            if (!anyFreq(freq)) {
                adminTimeInfo = getNextAdminTime(context, adminTimes, timeLastGiven, calendar);
            } else {
                AdminTimeInfo adminTimeInfo1 = getNextAdminTime(context, adminTimes, timeLastGiven, calendar);
                AdminTimeInfo adminTimeInfo2 = getNextFreqTime(context, freq, timeLastGiven, calendar);
                adminTimeInfo = chooseBestTime(adminTimeInfo1, adminTimeInfo2, timeLastGiven, calendar);
            }
        } else {
            // Assuming frequency is an assigned string!
            adminTimeInfo = getNextFreqTime(context, freq, timeLastGiven, calendar);
        }
        if (adminTimeInfo == null) return null;
        else return adminTimeInfo;
    }

    private static AdminTimeInfo chooseBestTime(AdminTimeInfo nextAdminTime, AdminTimeInfo nextFreqTime,
                                                long timeLastGiven, Calendar today) {
        // FOR NOW, we'll just return nextFreqTime.  Later, we may need to do some fancier
        //  processing ...
        return nextFreqTime;
    }

    private static boolean aPrnMed(Context context, String adminTimes, String freq) {
        String prnString = context.getString(R.string.prn_string);
        if (adminTimes.indexOf(prnString) >= 0) return true;
        if (freq.indexOf(prnString) >= 0) return true;
        return false;
    }

    private static boolean anyAdminTimes(String adminTimes) {
        if (adminTimes.trim().length() == 0) return false;
        return true;
    }

    private static boolean anyFreq(String freq) {
        if (freq.trim().length() == 0) return false;
        return true;
    }


    private static AdminTimeInfo getNextAdminTime(Context context, String adminTimes, long timeLastGiven,
                                            Calendar today) {
        /*
            <!-- the first match for this, will be hours ONLY -->
            <string name="format_admin_hours">\\d+</string>
            <!-- mins must be in ":nn" format, if any -->
            <string name="format_admin_mins">:\\d\\d</string>
            <!-- am/pm/a/p  will also catch ":", if any -->
            <!-- if NO am/pm/a/p, we'll assume military time -->
            <string name="format_admin_am_or_pm">\\D*</string>
        */
        //
        ArrayList<AdminTimeInfo> timesToAdmin = new ArrayList<AdminTimeInfo>();
        AdminTimeInfo adminTimeInfo = null;

        int year, month, day, hour, min, sec;
        year = today.get(Calendar.YEAR) - 1900;
        month = today.get(Calendar.MONTH);
        day = today.get(Calendar.DAY_OF_YEAR);
        hour = today.get(Calendar.HOUR);
        min = today.get(Calendar.MINUTE);
        sec = today.get(Calendar.SECOND);
        if (sec > 30) min += 1;

        while (true) {

            Pattern patternHrs = Pattern.compile(context.getString(R.string.format_admin_hours));
            Matcher matcherHrs = patternHrs.matcher(adminTimes);
            String hoursString = "";
            if (matcherHrs.find()) {
                hoursString = matcherHrs.group();
                adminTimes = adminTimes.replaceFirst(context.getString(R.string.format_admin_hours),"");
            } else break;

            Pattern patternMins = Pattern.compile(context.getString(R.string.format_admin_mins));
            Matcher matcherMins = patternMins.matcher(adminTimes);
            String minsString = "";
            if (matcherMins.find()) {
                minsString = matcherMins.group();
                // peel off leading ":"
                minsString = minsString.substring(1);
                adminTimes = adminTimes.replaceFirst(context.getString(R.string.format_admin_mins),"");
            }

            Pattern patternAmOrPm = Pattern.compile(context.getString(R.string.format_admin_am_or_pm));
            Matcher matcherAmOrPm = patternAmOrPm.matcher(adminTimes);
            String amOrPmString = "";
            boolean pm = false;
            if (matcherAmOrPm.find()) {
                amOrPmString = matcherAmOrPm.group();
                if (amOrPmString.equals(":")) {
                    amOrPmString = "";
                    if (matcherAmOrPm.find()) {
                        amOrPmString = matcherAmOrPm.group();
                        if (isPM(amOrPmString)) pm = true;
                        adminTimes = adminTimes.replaceFirst(context.getString(R.string.format_admin_am_or_pm),"");
                    }
                } else {
                    if (isPM(amOrPmString)) pm = true;
                    adminTimes = adminTimes.replaceFirst(context.getString(R.string.format_admin_am_or_pm),"");
                }
            }
            int scheduledHour = 0, scheduledMin = 0;
            try {
                if (hoursString.length() > 0)
                    scheduledHour = Integer.parseInt(hoursString);
                if (minsString.length() > 0)
                    scheduledMin = Integer.parseInt(minsString);
            } catch (NumberFormatException e) {
                Log.e("UTILITY", "  numberformatexception: " + e.toString() + "\n" +
                        " hours = '" + hoursString + "'  minutes = '" + minsString + "'");
            }

            //int minsFromNow = scheduledHour*60 + scheduledMin;
            //timesToAdmin.add(new Integer(minsFromNow));
            adminTimeInfo = new AdminTimeInfo(scheduledHour, scheduledMin, pm);
            timesToAdmin.add(adminTimeInfo);
        }

        // THIS CASE NEEDS MORE PLANNING, AT THE SERVER-SIDE ....
        if (timesToAdmin.size() == 0) {
            // Must be a special admin time, an actual date
            //  For now, assume must be in this format:
            //        "MM-dd-yyyy HH:mm"
            String adminTimeFormat = context.getString(R.string.format_admin_date_time);
            //DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm");
            DateFormat formatter = new SimpleDateFormat(adminTimeFormat);

            try {
                Date date = (Date) formatter.parse(adminTimes);
                Calendar scheduledTime = Calendar.getInstance();
                scheduledTime.setTime(date);
                adminTimeInfo = new AdminTimeInfo(scheduledTime);
                return adminTimeInfo;
            } catch (ParseException e) {
                Log.e("UTILITY"," !!!! PROBLEM ... PARSE EXCEPTION  adminTimes="+adminTimes);
                return null;
            }
        }
        return getNearestTimeFromNow(today, timesToAdmin);
    }


    private static AdminTimeInfo getNearestTimeFromNow(Calendar today, ArrayList<AdminTimeInfo>timesToAdmin) {
        int year, month, day, hour, min, sec;
        year = today.get(Calendar.YEAR) - 1900;
        month = today.get(Calendar.MONTH);
        day = today.get(Calendar.DAY_OF_YEAR);
        hour = today.get(Calendar.HOUR);
        min = today.get(Calendar.MINUTE);
        sec = today.get(Calendar.SECOND);
        if (sec > 30) min += 1;

        int diff = 30000;
        int minimumScheduledHour = 99;
        AdminTimeInfo minimumAdminTimeInfo = null;

        Calendar newTime = Calendar.getInstance();
        if (timesToAdmin.size() == 0) return null;
        int scheduledHour = 0;
        int scheduledMin = 0;
        AdminTimeInfo bestTime = null;
        for (int i = 0; i < timesToAdmin.size(); i++) {
            AdminTimeInfo timeInfo = timesToAdmin.get(i);
            scheduledHour = timeInfo.getHrs();
            scheduledMin = timeInfo.getMins();
            if (scheduledHour <= 0 && scheduledMin <= 0) continue;

            boolean pm = timeInfo.getIsPM();
            newTime.set(Calendar.HOUR, scheduledHour);
            newTime.set(Calendar.MINUTE, scheduledMin);
            newTime.set(Calendar.SECOND, 0);
            newTime.set(Calendar.AM_PM,(pm ? Calendar.PM : Calendar.AM));
            if (today.before(newTime)) {
                int minDiff = (scheduledHour * 60 + scheduledMin) - (hour * 60 + min);
                if (minDiff < diff) {
                    diff = minDiff;
                    bestTime = timeInfo;
                }
            } else {
                if (scheduledHour < minimumScheduledHour) {
                    minimumScheduledHour = scheduledHour;
                    minimumAdminTimeInfo = timeInfo;
                    minimumAdminTimeInfo.setCalendar(newTime);
                }
            }
        }
        // If we're at the end of the day, and no scheduled times for today, start with tomorrow's time
        if (bestTime == null) {
            if (minimumAdminTimeInfo == null) {
                Log.e("UTILITY", "ERROR: no sheduled times found!!!");
                return null;
            }
            Calendar newTimeCalendar = minimumAdminTimeInfo.getCalendar();
            newTimeCalendar.set(Calendar.DAY_OF_YEAR,day+1);
            return minimumAdminTimeInfo;
        }
        return bestTime;
    }

    private static boolean isPM(String amOrPmString) {
        if (amOrPmString.trim().toUpperCase().equals("PM") ||
            amOrPmString.trim().toUpperCase().equals("P")) {
            return true;
        }
        return false;
    }





    // IMPORTANT: for now this assumes ONLY Q<#><time-period>, where # even specified if it's = 1!!
    //
    private static AdminTimeInfo getNextFreqTime(Context context, String freq, long timeLastGiven,
                                                 Calendar today) {
        Calendar adminTime = Calendar.getInstance();
        adminTime.setTimeInMillis(timeLastGiven);
        int year = adminTime.get(Calendar.YEAR) - 1900;
        int month = adminTime.get(Calendar.MONTH);
        int week = adminTime.get(Calendar.WEEK_OF_YEAR);
        int day = adminTime.get(Calendar.DAY_OF_YEAR);
        int hour = adminTime.get(Calendar.HOUR);
        int min = adminTime.get(Calendar.MINUTE);

        AdminTimeInfo adminTimeInfo = null;

        // First see if freq is one of the common ones
        int freqType = getFreqType(freq);
        if (freqType >= 0) {
            switch (freqType){
                case QD:
                    // Select lastTimeGiven + 24Hours
                    adminTime.set(Calendar.DAY_OF_YEAR,day+1);
                    break;
                case BID:
                    // Select lastTimeGiven + 12Hours
                    adminTime.set(Calendar.HOUR,hour+12);
                    break;
                case TID:
                    // Select lastTimeGiven + 8Hours
                    adminTime.set(Calendar.HOUR,hour+8);
                    break;
                case QID:
                    // Select lastTimeGiven + 6Hours
                    adminTime.set(Calendar.HOUR,hour+6);
                    break;
                default:
                    Log.e("UTILITY", "ERROR added another frequency type, but not handled!");
                    return null;
            }
            adminTimeInfo = new AdminTimeInfo(adminTime);
            return adminTimeInfo;
        }
        // Otherwise, this is a more unusual frequency, e.g. in form Q<#><mins/hours/days/weeks/months>
        // Note: pattern is \d+ for #    and   pattern is \D+ for <time-period>
        // The starting "Q" is optional, and ignored
        //   .... so for example: 'Q5days', 'Q 1 WEEK', or '2hours', is valid

        //format_frequency_value
        //        format_frequency_time_period

        Pattern patternNumber = Pattern.compile(context.getString(R.string.format_frequency_value));
        Matcher matcherNumber = patternNumber.matcher(freq);
        String numberString = "";
        String timePeriodString = "";
        if (matcherNumber.find()) {
            numberString = matcherNumber.group();
        }
        Pattern patternTimePeriod = Pattern.compile(context.getString(R.string.format_frequency_time_period));
        Matcher matcherTimePeriod = patternTimePeriod.matcher(freq);
        if (matcherTimePeriod.find()) {
            timePeriodString = matcherTimePeriod.group();
            // skip over "Q", if any:
            if (timePeriodString.toUpperCase().indexOf(EVERY_CHARS) >= 0) {
                timePeriodString = "";
                if (matcherTimePeriod.find()) {
                    timePeriodString = matcherTimePeriod.group();
                }
            }
        }
        if (numberString.length() == 0) {
            Log.e("UTILITY"," !!!! PROBLEM ... numberString is EMPTY");
            return null;
        }
        int number = 0;
        try {
            number = Integer.parseInt(numberString);
        } catch (NumberFormatException e) {
            Log.e("UTILITY"," !!!! PROBLEM ... PARSE EXCEPTION  freq="+freq+
                    "\n  numberString = '"+numberString+"'");
            return null;
        }

        int timePeriod = getTimePeriod(timePeriodString);
        if (timePeriod >= 0) {
            switch (timePeriod) {
                case MINS:
                    // Select lastTimeGiven + number (in minutes)
                    adminTime.set(Calendar.MINUTE,min+number);
                    break;
                case HOURS:
                    // Select lastTimeGiven + number (in hours)
                    adminTime.set(Calendar.HOUR,hour+number);
                    break;
                case DAYS:
                    // Select lastTimeGiven + number (in days)
                    adminTime.set(Calendar.DAY_OF_YEAR,day+number);
                    break;
                case WEEKS:
                    // Select lastTimeGiven + number (in weeks)
                    adminTime.set(Calendar.WEEK_OF_YEAR,week+number);
                    break;
                case MONTHS:
                    // Select lastTimeGiven + number (in months)
                    adminTime.set(Calendar.MONTH,month+number);
                    break;
                default:
                    Log.e("UTILITY", "ERROR added another frequency/time-period type, but not handled!");
                    return null;
            }
        }
        adminTimeInfo = new AdminTimeInfo(adminTime);
        return adminTimeInfo;
    }




    private static int getFreqType(String freq) {
        for (int i = 0; i < ALLOWED_FREQUENCIES.length; i++) {
            if (freq.equals(ALLOWED_FREQUENCIES[i])) return i;
        }
        return -1;
    }

    private static int getTimePeriod(String timePeriod) {
        timePeriod = timePeriod.trim();
        for (int i = 0; i < ALLOWED_TIME_PERIODS.length && i < ALLOWED_TIME_PERIODS_ABBREV.length; i++) {
            if (ALLOWED_TIME_PERIODS[i].indexOf(timePeriod) >= 0) return i;
            else if (ALLOWED_TIME_PERIODS_ABBREV[i].indexOf(timePeriod) >= 0) return i;
        }
        return -1;
    }


    public static boolean timeIsNull(long dateTime) {
        long daysSinceEpoch = elapsedDaysSinceEpoch(dateTime);
        if (daysSinceEpoch == 0) return true;
        return false;
    }
    private static long elapsedDaysSinceEpoch(long utcDate) {
        return TimeUnit.MILLISECONDS.toDays(utcDate);
    }


    /**
     * Returns true if the network is available or about to become available.
     *
     * @param c Context used to get the ConnectivityManager
     * @return true if the network is available
     */
    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }







    /**
     * @param c Context used to get the SharedPreferences
     * @return the status integer type
     */
/*
    @SuppressWarnings("ResourceType")
    static public
    @NurseHelperSyncAdapter.Status
    int getStatus(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_status_key), NurseHelperSyncAdapter.STATUS_UNKNOWN);
    }
*/
    /**
     * Resets the network status.  (Sets it to NurseHelperSyncAdapter.STATUS_UNKNOWN)
     *
     * @param c Context used to get the SharedPreferences
     */
/*
    static public void resetStatus(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_status_key), NurseHelperSyncAdapter.STATUS_UNKNOWN);
        spe.apply();
    }
*/

}
