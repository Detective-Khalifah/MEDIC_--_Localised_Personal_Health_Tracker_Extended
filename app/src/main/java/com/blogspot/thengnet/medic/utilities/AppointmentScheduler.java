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

public class AppointmentScheduler {

    /* TODO: lookup {@link AlarmManager} vs. {@link androidx.core.app.AlarmManagerCompat} */

    public static String scheduleAppointment (Context context, int appointmentId, long timeInMillis) {
        // TODO: Check repeat dates here #repeatDays();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(context, AlarmBroadcastReceiver.class);
        alarmIntent.setAction(FIRE_UP_ALARM);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, appointmentId, alarmIntent,
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
     * Utility method to help stop an appointment properly. Nothing will happen, if appointment
     * reminder is not firing.
     *
     * @param context application context
     * @param appointmentId you are trying to stop
     */
    public static void stopAppointmentReminder (Context context, int appointmentId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent stopAppointmentIntent = new Intent(context, AlarmBroadcastReceiver.class);
        stopAppointmentIntent.setAction(STOP_SCHEDULED_ALARM);

        PendingIntent stopAppointmentPendingIntent = PendingIntent.getBroadcast(context, appointmentId,
                stopAppointmentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(stopAppointmentPendingIntent);
    }

    /**
     * Fetch an {@link ArrayList} of days an appointment is set to repeat.
     *
     * @param context application context
     * @param appointmentId of the assessed {@link Alarm}.
     * @return {@link String} {@link ArrayList} of repeat days.
     */

    public static ArrayList<String> getRepeatDays (Context context, int appointmentId) {
        ArrayList<String> repeatDays = null;
        return repeatDays;
    }

}
