package com.android.janice.nursehelper.alarm;

/**
 * Created by janicerichards on 3/26/17.
 */


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.android.janice.nursehelper.R;

import java.util.Calendar;

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent
 * and then starts the IntentService {@code NurseHelperSchedulingService} to do some work.
 */
public class NurseHelperAlarmReceiver extends WakefulBroadcastReceiver {
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;
    private Intent intent;
    private int mTimeInterval;
    public static String ACTION_ALARM_RECEIVER = "update";

    @Override
    public void onReceive(Context context, Intent intent) {
        // BEGIN_INCLUDE(alarm_onreceive)
        /*
         * If your receiver intent includes extras that need to be passed along to the
         * service, use setComponent() to indicate that the service should handle the
         * receiver's intent. For example:
         *
         * ComponentName comp = new ComponentName(context.getPackageName(),
         *      MyService.class.getName());
         *
         * // This intent passed in this call will include the wake lock extra as well as
         * // the receiver intent contents.
         * startWakefulService(context, (intent.setComponent(comp)));
         *
         * In this example, we simply create a new intent to deliver to the service.
         * This intent holds an extra identifying the wake lock.
         */
        Intent service = new Intent(context, NurseHelperSchedulingService.class);

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, service);
        // END_INCLUDE(alarm_onreceive)
    }

    public void setTimeInterval(int timeInterval) {
        this.mTimeInterval = timeInterval;
    }

    // BEGIN_INCLUDE(set_alarm)
    /**
     * Sets a repeating alarm that runs once a day at approximately 8:30 a.m. When the
     * alarm fires, the app broadcasts an Intent to this WakefulBroadcastReceiver.
     * @param context
     */
    public void setAlarm(Context context) {
        if (mTimeInterval == 0) {
            mTimeInterval = context.getResources().getInteger(R.integer.pref_time_interval_default);
        }

        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(context, NurseHelperAlarmReceiver.class);
        // NEW:
        intent.setAction(ACTION_ALARM_RECEIVER);//my custom string action name
        //alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        // NEW:
        alarmIntent = PendingIntent.getBroadcast(context, 1001,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);//used unique ID as 1001

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // Set the alarm's trigger time to 8:30 a.m.  ...5:05PM
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 29);

        /*
         * If you don't have precise time requirements, use an inexact repeating alarm
         * the minimize the drain on the device battery.
         *
         * The call below specifies the alarm type, the trigger time, the interval at
         * which the alarm is fired, and the alarm's associated PendingIntent.
         * It uses the alarm type RTC_WAKEUP ("Real Time Clock" wake up), which wakes up
         * the device and triggers the alarm according to the time of the device's clock.
         *
         * Alternatively, you can use the alarm type ELAPSED_REALTIME_WAKEUP to trigger
         * an alarm based on how much time has elapsed since the device was booted. This
         * is the preferred choice if your alarm is based on elapsed time--for example, if
         * you simply want your alarm to fire every 60 minutes. You only need to use
         * RTC_WAKEUP if you want your alarm to fire at a particular date/time. Remember
         * that clock-based time may not translate well to other locales, and that your
         * app's behavior could be affected by the user changing the device's time setting.
         *
         * Here are some examples of ELAPSED_REALTIME_WAKEUP:
         *
         * // Wake up the device to fire a one-time alarm in one minute.
         * alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
         *         SystemClock.elapsedRealtime() +
         *         60*1000, alarmIntent);
         *
         * // Wake up the device to fire the alarm in 30 minutes, and every 30 minutes
         * // after that.
         * alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
         *         AlarmManager.INTERVAL_HALF_HOUR,
         *         AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);
         *
         * // Set the alarm to fire at approximately 8:30 a.m., according to the device's
         * // clock, and to repeat once a day.
         * alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
         *        calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
         *
         */

        // Set the alarm to fire every <mTimeInterval> minutes
        //   after that; mTimeInterval can only be 15 min, 30 min, or 1 hour:
        long timeInterval = (long) mTimeInterval;
        switch (mTimeInterval) {
            case (int) AlarmManager.INTERVAL_FIFTEEN_MINUTES:
            case (int) AlarmManager.INTERVAL_HALF_HOUR:
            case (int) AlarmManager.INTERVAL_HOUR:
                break;
            default:
                timeInterval = AlarmManager.INTERVAL_HOUR;
        }
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                timeInterval, timeInterval, alarmIntent);

        // Enable {@code NurseHelperBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, NurseHelperBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
    // END_INCLUDE(set_alarm)

    /**
     * Cancels the alarm.
     * @param context
     */
    // BEGIN_INCLUDE(cancel_alarm)
    public void cancelAlarm(Context context) {


        //Intent intent = new Intent(getActivity(), MyReceiver.class);//the same as up
        //intent.setAction(MyReceiver.ACTION_ALARM_RECEIVER);//the same as up
        PendingIntent pendingIntent;
        if (intent != null) {

            pendingIntent = PendingIntent.getBroadcast(context, 1001, intent, PendingIntent.FLAG_CANCEL_CURRENT);//the same as up
        } else {
            intent = new Intent(context, NurseHelperAlarmReceiver.class);
            // NEW:
            intent.setAction(ACTION_ALARM_RECEIVER);//my custom string action name
            pendingIntent = PendingIntent.getBroadcast(context, 1001, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        //alarmMgr.cancel(alarmIntent);//important
        //pendingIntent.cancel();//important

        // If the alarm has been set, cancel it.
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
        if (pendingIntent != null) {
            pendingIntent.cancel();
        }

        // Disable {@code NurseHelperBootReceiver} so that it doesn't automatically restart the
        // alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, NurseHelperBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
    // END_INCLUDE(cancel_alarm)
}
