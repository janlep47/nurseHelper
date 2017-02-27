package com.android.janice.nursehelper.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import java.text.ParseException;
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
            "MIN", "HOUR", "DAY", "WEEK", "MONTH"
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



    public static long calculateNextDueTime(Context context, String adminTimes, String freq, long timeLastGiven) {
        if (aPrnMed(context, adminTimes, freq)) return 0;
        if (anyAdminTimes(adminTimes)) {
            if (!anyFreq(freq)) {
                long junk = getNextAdminTime(context, adminTimes, timeLastGiven);
                Log.i("UTILS","  returned best adminTime:"+
                  DateAndTimeUtils.getFormattedDateFromLong(context,junk));
                return junk;
                //return getNextAdminTime(context, adminTimes, timeLastGiven);
            } else {
                long nextAdminTime = getNextAdminTime(context, adminTimes, timeLastGiven);
                Log.i("UTILS","  ... best adminTime:"+
                        DateAndTimeUtils.getFormattedDateFromLong(context,nextAdminTime));
                long nextFreqTime = getNextFreqTime(context, freq, timeLastGiven);
                return chooseBestTime(nextAdminTime, nextFreqTime, timeLastGiven);
            }
        } else {
            // Assuming frequencey is an assigned string!
            return getNextFreqTime(context, freq, timeLastGiven);
        }
    }

    private static long chooseBestTime(long nextAdminTime, long nextFreqTime, long timeLastGiven) {
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
        Log.i("UTILS","  any frequency is TRUE");
        return true;
    }


    private static long getNextAdminTime(Context context, String adminTimes, long timeLastGiven) {
        /*
            <!-- the first match for this, will be hours ONLY -->
    <string name="format_admin_hours">\\d+</string>
    <!-- mins must be in ":nn" format, if any -->
    <string name="format_admin_mins">:\\d\\d</string>
    <!-- am/pm/a/p  will also catch ":", if any -->
    <!-- if NO am/pm/a/p, we'll assume military time -->
    <string name="format_admin_am_or_pm">\\D*</string>

         */
        long today = System.currentTimeMillis();
        long reallyToday = DateAndTimeUtils.getNormalizedUtcDateForToday();
        long reallyToday2 = DateAndTimeUtils.normalizeDate(today);
        String todayString = DateAndTimeUtils.getFormattedDateFromLong(context,today);
        long now = DateAndTimeUtils.getLongFromFormattedDate(context,todayString);
        String nowString = DateAndTimeUtils.getFormattedDateFromLong(context,now);
        String todayMidnightString = todayString.substring(0,8)+" 00:00";
        //Log.i("UTIL"," today: "+todayString+"  todayMidnightString: "+todayMidnightString);
        //Log.i("UTIL"," today: "+todayString+"  today1: "+String.valueOf(reallyToday)+
        //"   today2: "+String.valueOf(reallyToday2));
        Log.i("UTIL"," now: "+String.valueOf(now)+"   now string: "+nowString+"  vs. todayString: "+
            todayString);
        //String nowish = "02-26-17 20:26";
        //long nowishVal = DateAndTimeUtils.getLongFromFormattedDate(context,nowish);
        //        Log.i("UTIL"," todayVal: "+String.valueOf(today)+"  02-26-17 20:26 "+String.valueOf(nowishVal));
        String nineAmString = "02-26-17 09:00";
        String tenPmString = "02-26-17 22:00";
        long nineAmVal = DateAndTimeUtils.getLongFromFormattedDate(context, nineAmString);
        long tenPmVal = DateAndTimeUtils.getLongFromFormattedDate(context, tenPmString);
        Log.e("UTILS", "  IMPORTANT  nineAmVal="+nineAmVal+"  tenPmVal="+tenPmVal);
        long todayMidnight = DateAndTimeUtils.getLongFromFormattedDate(context,todayMidnightString);
        ArrayList<Long> timesToAdmin = new ArrayList<Long>();

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
            //Log.i("UTILITY", "  from adminTimes: '" + adminTimes + "'  get: ");
            //Log.i("UTILITY", "  hours: '" + hoursString + "'  mins: '" + minsString +
            //        "'   amOrPm: '" + amOrPmString + "'");

            // convert hours and minutes to longs
            long hours = 0, minutes = 0;
            try {
                if (hoursString.length() > 0)
                    hours = Long.parseLong(hoursString);
                if (minsString.length() > 0)
                    minutes = Long.parseLong(minsString);
            } catch (NumberFormatException e) {
                Log.e("UTILITY", "  numberformatexception: " + e.toString() + "\n" +
                        " hours = '" + hoursString + "'  minutes = '" + minsString + "'");
            }
            //Log.i("UTILITY", " minutes val = " + String.valueOf(minutes) + "  hours val = " +
            //        String.valueOf(hours));
            if (pm) hours += 12;  // convert to military time
            //long time = DateAndTimeUtils.getTimestampForHoursAndMins(hours, minutes);
            long time = todayMidnight + hours*HOUR_IN_MILLIS + minutes*MINUTE_IN_MILLIS;
            Log.i("UTILS"," hours = "+String.valueOf(hours)+"  hours*HOUR_IN_MILLIS="+
                    String.valueOf(hours*HOUR_IN_MILLIS)+ "  todayMidnight="+todayMidnight +
                    "  time="+time);
            timesToAdmin.add(new Long(time));
        }

        if (timesToAdmin.size() == 0) {
            // Must be a special admin time, an actual date
            //  For now, assume must be in this format:
            //        "MM-dd-yyyy HH:mm"
            String adminTimeFormat = context.getString(R.string.format_admin_date_time);
            //DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm");
            DateFormat formatter = new SimpleDateFormat(adminTimeFormat);

            try {
                Date date = (Date) formatter.parse(adminTimes);
                long time = date.getTime();
                timesToAdmin.add(time);
            } catch (ParseException e) {
                Log.e("UTILITY"," !!!! PROBLEM ... PARSE EXCEPTION  adminTimes="+adminTimes);
                return 0;
            }
        }
        return DateAndTimeUtils.getNearestTimeFromNow(now, timesToAdmin);
    }

    private static boolean isPM(String amOrPmString) {
        if (amOrPmString.trim().toUpperCase().equals("PM") ||
            amOrPmString.trim().toUpperCase().equals("P")) {
            //Log.i("UTILITY","   PM is TRUE for: "+amOrPmString);
            return true;
        }
        //Log.i("UTILITY","   PM is FALSE for: "+amOrPmString);
        return false;
    }



    private static long OLDgetNextAdminTime(Context context, String adminTimes, long timeLastGiven) {
        ArrayList<Long> timesToAdmin = new ArrayList<Long>();
        Pattern pattern = Pattern.compile(context.getString(R.string.format_admin_times));
        Matcher matcher = pattern.matcher(adminTimes);
        boolean found = false;
        // Note: pattern is this
        // (\d\d*(:\d\d)? (\w\w)?,?)+
        //   .... so for example: 9:30 AM, 10 PM is valid
        //    ..... so is military time: 0900, 2200.
        //  Already checked for adminTimes empty string, so must be at least one time in string.
        while (matcher.find()) {
            found = true;
            String timeString = matcher.group().trim();
            Log.i("UTILITY", "  time: "+timeString+"   ... from adminTimes: "+adminTimes);
            // Now convert the time string to a long time
            if (timeString.endsWith(",")) timeString.replace(',',' ');
            boolean pm = false;
            if (timeString.indexOf(PM_STRING) >= 0) {
                timeString = timeString.substring(0,timeString.indexOf(PM_STRING));
                pm = true;
            } else if (timeString.indexOf(AM_STRING) != 0) {
                timeString = timeString.substring(0,timeString.indexOf(AM_STRING));
            }
            Log.i("UTILITY", "  ... now timeString = "+timeString+" and pm is "+String.valueOf(pm));
            //String hours = timeString;
            String hoursString;
            String minutesString = "";
            if (timeString.indexOf(HOUR_MIN_SEPARATOR) >= 0) {
                minutesString = timeString.substring(timeString.indexOf(HOUR_MIN_SEPARATOR));
                hoursString = timeString.substring(0,timeString.indexOf(HOUR_MIN_SEPARATOR));
                Log.i("UTILITY", " minutes = "+minutesString+"  hours = "+hoursString);
            } else {
                hoursString = timeString;
                Log.i("UTILITY", " minutes = "+minutesString+"  hours = "+hoursString);
            }
            // convert hours and minutes to longs
            long hours = 0, minutes = 0;
            try {
                hours = Long.parseLong(hoursString);
                minutes = Long.parseLong(minutesString);
            } catch (NumberFormatException e) {
                Log.e("UTILITY","  numberformatexception: "+e.toString()+"\n"+
                        " hours = '"+hoursString+"'  minutes = '"+minutesString+"'");
            }
            Log.i("UTILITY", " minutes val = "+String.valueOf(minutes)+"  hours val = "+
                    String.valueOf(hours));
            long time = DateAndTimeUtils.getTimestampForHoursAndMins(hours,minutes);
            timesToAdmin.add(new Long(time));
        }
        if (!found) {
            // Must be a special admin time, an actual date
            //  For now, assume must be in this format:
            //        "MM-dd-yyyy HH:mm"
            String adminTimeFormat = context.getString(R.string.format_admin_date_time);
            //DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm");
            DateFormat formatter = new SimpleDateFormat(adminTimeFormat);

            try {
                Date date = (Date) formatter.parse(adminTimes);
                long time = date.getTime();
                timesToAdmin.add(time);
            } catch (ParseException e) {
                Log.i("UTILITY"," !!!! PROBLEM ... PARSE EXCEPTION  adminTimes="+adminTimes);
                return 0;
            }
        }
        return DateAndTimeUtils.getNearestTimeFromNow(0,timesToAdmin);
    }





    // IMPORTANT: for now this assumes ONLY Q<#><time-period>, where # even specified if it's = 1!!
    //
    private static long getNextFreqTime(Context context, String freq, long timeLastGiven) {
        ArrayList<Long> timesToAdmin = new ArrayList<Long>();
        // First see if freq is one of the common ones
        int freqType = getFreqType(freq);
        if (freqType >= 0) {
            switch (freqType){
                case QD:
                    // Select lastTimeGiven + 24Hours
                    return DateAndTimeUtils.getTimeWithAddedHrsAndMins(timeLastGiven, 24, 0);
                case BID:
                    // Select lastTimeGiven + 12Hours
                    return DateAndTimeUtils.getTimeWithAddedHrsAndMins(timeLastGiven, 12, 0);
                case TID:
                    // Select lastTimeGiven + 8Hours
                    return DateAndTimeUtils.getTimeWithAddedHrsAndMins(timeLastGiven, 8, 0);
                case QID:
                    // Select lastTimeGiven + 6Hours
                    return DateAndTimeUtils.getTimeWithAddedHrsAndMins(timeLastGiven, 6, 0);
                default:
                    Log.e("UTILITY", "ERROR added another frequency type, but not handled!");
                    return 0;
            }
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
        Log.i("UTILITY", "  numberString="+numberString+"  timePeriod="+timePeriodString);
        if (numberString.length() == 0) return 0;
        long number = 0;
        try {
            number = Long.parseLong(numberString);
        } catch (NumberFormatException e) {
            Log.i("UTILITY"," !!!! PROBLEM ... PARSE EXCEPTION  freq="+freq+
                    "\n  numberString = '"+numberString+"'");
            return 0;
        }

        int timePeriod = getTimePeriod(timePeriodString);
        if (timePeriod >= 0) {
            switch (timePeriod) {
                case MINS:
                    // Select lastTimeGiven + number (in minutes)
                    return DateAndTimeUtils.getTimeWithAddedHrsAndMins(timeLastGiven, 0, number);
                case HOURS:
                    // Select lastTimeGiven + number (in hours)
                    return DateAndTimeUtils.getTimeWithAddedHrsAndMins(timeLastGiven, number, 0);
                case DAYS:
                    // Select lastTimeGiven + number (in days)
                    return DateAndTimeUtils.getTimeWithAddedWeeksAndDays(timeLastGiven, 0, number);
                case WEEKS:
                    // Select lastTimeGiven + number (in weeks)
                    return DateAndTimeUtils.getTimeWithAddedWeeksAndDays(timeLastGiven, number, 0);
                case MONTHS:
                    // Select lastTimeGiven + number (in months)
                    return DateAndTimeUtils.getTimeWithAddedMonths(timeLastGiven, number);
                default:
                    Log.e("UTILITY", "ERROR added another frequency/time-period type, but not handled!");
                    return 0;
            }
        }
        return 0;
    }




    private static int getFreqType(String freq) {
        for (int i = 0; i < ALLOWED_FREQUENCIES.length; i++) {
            if (freq.equals(ALLOWED_FREQUENCIES[i])) return i;
        }
        return -1;
    }

    private static int getTimePeriod(String timePeriod) {
        for (int i = 0; i < ALLOWED_TIME_PERIODS.length; i++) {
            if (timePeriod.equals(ALLOWED_TIME_PERIODS[i])) return i;
        }
        return -1;
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
