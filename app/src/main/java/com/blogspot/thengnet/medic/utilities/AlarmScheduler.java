package com.blogspot.thengnet.medic.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.blogspot.thengnet.medic.data.Alarm;
import com.blogspot.thengnet.medic.receivers.AlarmBroadcastReceiver;

import java.util.ArrayList;

import static com.blogspot.thengnet.medic.receivers.AlarmBroadcastReceiver.FIRE_UP_ALARM;
import static com.blogspot.thengnet.medic.receivers.AlarmBroadcastReceiver.STOP_SCHEDULED_ALARM;

public class AlarmScheduler {


    /* TODO: lookup {@link AlarmManager} vs. {@link androidx.core.app.AlarmManagerCompat} */

    public static String scheduleAlarm (Context context, int alarmId, long timeInMillis) {
        // TODO: Check repeat dates here #repeatDays();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(context, AlarmBroadcastReceiver.class);
        alarmIntent.setAction(FIRE_UP_ALARM);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        int type = AlarmManager.RTC_WAKEUP;

        // Schedule an exact alarm that will trigger at the specified time -- in a non-lax,
        // non-lenient timely manner
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(type, timeInMillis, alarmPendingIntent);
            return "Alarm set on an Android of API level >= M";
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(type, timeInMillis, alarmPendingIntent);
            return "Alarm set on an Android of API level >= KITKAT";
        } else {
            alarmManager.set(type, timeInMillis, alarmPendingIntent);
            return "Alarm set on an Android of API level < KITKAT";
        }
    }

    /**
     * Utility method to help stop an alarm properly. Nothing will happen, if reminder is not firing.
     *
     * @param context application context
     * @param alarmId you are trying to stop
     */
    public static void stopAlarmReminder (Context context, int alarmId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent stopAlarmIntent = new Intent(context, AlarmBroadcastReceiver.class);
        stopAlarmIntent.setAction(STOP_SCHEDULED_ALARM);

        PendingIntent stopAppointmentPendingIntent = PendingIntent.getBroadcast(context, alarmId,
                stopAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(stopAppointmentPendingIntent);
    }

    /**
     * Fetch an {@link ArrayList} of days an appointment is set to repeat.
     *
     * @param context application context
     * @param alarmtId of the assessed {@link Alarm}.
     * @return {@link String} {@link ArrayList} of repeat days.
     */

    public static ArrayList<String> getRepeatDays (Context context, int alarmtId) {
        ArrayList<String> repeatDays = null;
        return repeatDays;
    }

}
