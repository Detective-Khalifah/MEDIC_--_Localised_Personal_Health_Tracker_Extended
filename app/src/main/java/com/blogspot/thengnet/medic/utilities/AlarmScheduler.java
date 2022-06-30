package com.blogspot.thengnet.medic.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.blogspot.thengnet.medic.data.Alarm;
import com.blogspot.thengnet.medic.data.AlarmContract;
import com.blogspot.thengnet.medic.receivers.AlarmBroadcastReceiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static com.blogspot.thengnet.medic.receivers.AlarmBroadcastReceiver.FIRE_UP_ALARM;
import static com.blogspot.thengnet.medic.receivers.AlarmBroadcastReceiver.STOP_SCHEDULED_ALARM;

public class AlarmScheduler {

    private static final String LOG_TAG = AlarmScheduler.class.getName();

    /**
     *
     */
    private static final long SECOND = 1000; // 1,000 Milliseconds = 1 second
    private static final long MINUTE = SECOND * 60; // 60 seconds = 1 minute
    private static final long HOUR = MINUTE * 60; // 60 minutes = 1 hour

    /**
     * Dosing Times in milliseconds.
     */
    private static final long Q24H = HOUR * 24;
    private static final long Q12H = HOUR * 12;
    private static final long Q8H = HOUR * 8;
    private static final long Q6H = HOUR * 6;
    private static final long Q4H = HOUR * 4;

    /** The repeat interval of reminder in milliseconds since Unix Epoch Time. */
    private static long REPEAT_INTERVAL;
    /** First reminder time in milliseconds since Unix Epoch Time. */
    private static long START_TIME_IN_MILLIS;
    /** Last reminder time in milliseconds since Unix Epoch Time. */
    private static long STOP_TIME_IN_MILLIS;


    /* TODO: lookup {@link AlarmManager} vs. {@link androidx.core.app.AlarmManagerCompat} */

    /**
     * Utility method to process trigger time(s) of a newly created {@link Alarm}.
     *
     * @param context     {@link Context} of the app
     * @param alarmId     unique id of the new {@link Alarm}; saved serially in dB.
     * @param theAlarmUri of the new {@link Alarm} as saved in the dB.
     * @return
     */
    public static String setupAlarm (Context context, long alarmId, Uri theAlarmUri) {
        // Check trigger time(s) of the Alarm
        try (Cursor theAlarm = context.getContentResolver().query(
                theAlarmUri,
                new String[]{AlarmContract.AlarmEntry.COLUMN_ALARM_START_DATE,
                        AlarmContract.AlarmEntry.COLUMN_ALARM_STOP_DATE,
                        AlarmContract.AlarmEntry.COLUMN_ALARM_TIME,
                        AlarmContract.AlarmEntry.COLUMN_ADMINISTRATION_PER_DAY},
             AlarmContract.AlarmEntry._ID + "=?", new String[]{String.valueOf(alarmId)}, null)) {

            if (theAlarm.moveToFirst()) {
                String startDate = theAlarm.getString(theAlarm.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ALARM_START_DATE));
                String stopDate = theAlarm.getString(theAlarm.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ALARM_STOP_DATE));
                String startTime = theAlarm.getString(theAlarm.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ALARM_TIME));
                String adminPerDay = theAlarm.getString(theAlarm.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ADMINISTRATION_PER_DAY));
                Log.v(LOG_TAG, String.format("The Alarm\nStart Date: %s\nStop Date: %s\n1st Time: %s\nDosing: %s", startDate, stopDate, startTime, adminPerDay));

                START_TIME_IN_MILLIS = TimeConverter.getTimeInMillis(startDate, startTime);
                // TODO: (LATER) -- Consider varying stopTime relative to (total) dosage; inexact compared to startTime.
                STOP_TIME_IN_MILLIS = TimeConverter.getTimeInMillis(stopDate, startTime) + Q24H;
                Log.v(LOG_TAG, "Start Time (millis): " + START_TIME_IN_MILLIS + "\nStop Time (millis): " + STOP_TIME_IN_MILLIS);

                switch (Integer.parseInt(adminPerDay)) {
                    case 1:
                        REPEAT_INTERVAL = Q24H;
                        break;
                    case 2:
                        REPEAT_INTERVAL = Q12H;
                        break;
                    case 3:
                        REPEAT_INTERVAL = Q8H;
                        break;
                    case 4:
                        REPEAT_INTERVAL = Q6H;
                        break;
                    case 6:
                        REPEAT_INTERVAL = Q4H;
                        break;
                    default:
                        REPEAT_INTERVAL = MINUTE * 2;
                }
                Log.v(LOG_TAG, "Repeat Interval: " + REPEAT_INTERVAL);

                // Calculate trigger times.
                ArrayList<Long> triggerTimesList = calculateTriggerTimes();
                // Parse trigger times in form "time0, time1, ...".
                String triggerTimes = triggerTimesList.stream().map(Object::toString)
                        .collect(Collectors.joining(", "));
                Log.v(LOG_TAG, "Trigger Time(s): " + triggerTimes);
                // Add the trigger times to dB.
                ContentValues alarmEnableValues = new ContentValues();
                alarmEnableValues.put(AlarmContract.AlarmEntry.COLUMN_ALARM_TRIGGER_REPEAT_TIMES, triggerTimes);
                alarmEnableValues.put(AlarmContract.AlarmEntry.COLUMN_ALARM_STATE, 1);
                context.getContentResolver().update(
                        theAlarmUri,
                        alarmEnableValues,
                        AlarmContract.AlarmEntry._ID + "=?",
                        new String[]{String.valueOf(alarmId)});

                return scheduleAlarm(context, (int) alarmId, triggerTimesList);

            } else
                throw new CursorIndexOutOfBoundsException("Could not find alarm!");
        } catch (CursorIndexOutOfBoundsException cioobe) {
            return cioobe.getLocalizedMessage();
        }
    }

    /**
     * Utility method to process trigger time(s) of all enabled {@link Alarm}s in the dB, called
     * when device undergoes restarts and/or time changes and device restarts; re-schedules
     * {@link Alarm}s that have trigger time(s) remaining in the future, or disable(s) them
     * gracefully.
     *
     * @param context {@link Context} of the app.
     */
    public static void assessAlarms(Context context) {
        Log.v(LOG_TAG, "assessAlarms() here!");
        Toast.makeText(context, "assessAlarms() here!", Toast.LENGTH_LONG).show();

        // Fetch all enabled alarms
        Uri alarmsUri = AlarmContract.AlarmEntry.CONTENT_URI;
        String[] projection = {
                AlarmContract.AlarmEntry._ID,
                AlarmContract.AlarmEntry.COLUMN_ALARM_TITLE,
                AlarmContract.AlarmEntry.COLUMN_ALARM_STOP_DATE,
                AlarmContract.AlarmEntry.COLUMN_ALARM_TIME,
                AlarmContract.AlarmEntry.COLUMN_ALARM_TRIGGER_REPEAT_TIMES,
        };
        String selection = AlarmContract.AlarmEntry.COLUMN_ALARM_STATE + "=?";
        String[] selectionArgs = {String.valueOf(1)};

        try(Cursor theAlarms = context.getContentResolver().query(alarmsUri, projection, selection,
                selectionArgs, null)) {
            // Check trigger time(s) of each Alarm.
            while (theAlarms.moveToNext()) {
                long id = theAlarms.getLong(theAlarms.getColumnIndexOrThrow(AlarmContract.AlarmEntry._ID));
                String stopDate = theAlarms.getString(theAlarms.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ALARM_STOP_DATE));
                String time = theAlarms.getString(theAlarms.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ALARM_TIME));

                // TODO: (LATER) -- Consider varying stopTime relative to (total) dosage; inexact compared to startTime.
                STOP_TIME_IN_MILLIS = TimeConverter.getTimeInMillis(stopDate, time);
                long now = Calendar.getInstance().getTimeInMillis();
                Log.v(LOG_TAG, "Assessed Alarm Start Time (millis): " + START_TIME_IN_MILLIS + "; Stop Time (millis): " + STOP_TIME_IN_MILLIS);

                // (re-)schedule the Alarm if there remain trigger time(s) in the future; otherwise
                // deactivate the Alarm.
                if (STOP_TIME_IN_MILLIS > now) {
                    String triggerTimes = theAlarms.getString(theAlarms.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ALARM_TRIGGER_REPEAT_TIMES));
                    setupEnabledAlarms(context, id, triggerTimes);
                } else {
                    ContentValues deactivationValue = new ContentValues();
                    deactivationValue.put(AlarmContract.AlarmEntry.COLUMN_ALARM_STATE, 0);
                    context.getContentResolver().update(
                            Uri.withAppendedPath(alarmsUri, String.valueOf(id)),
                            deactivationValue, AlarmContract.AlarmEntry._ID + "=?",
                            new String[]{String.valueOf(id)}
                            );
                    String title = theAlarms.getString(theAlarms.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ALARM_TITLE));
                    Log.v(LOG_TAG, "Disabled alarm " + id + "; titled " + title);
                }

            }

        }
    }

    private static String setupEnabledAlarms (Context context, long alarmId, String triggerTimes) {
        ArrayList<Long> triggerTimesList = parseTriggerTimes(triggerTimes);
        // TODO: define contingency in following case -- missing trigger time(s) [when past alarm is activated]
        if (triggerTimes == null)
            throw new IllegalStateException("No trigger time(s) set!");
        return scheduleAlarm(context, alarmId, triggerTimesList);
    }

    /**
     * Utility method to schedule an alarm to ring at user-specified time.
     *
     * @param context      {@link Context} of the app
     * @param alarmId      unique id of the {@link Alarm} being scheduled, saved serially in dB
     * @param triggerTimes an {@link ArrayList} of {@link Long} trigger times in milliseconds since
     *                     Unix Epoch time
     * @return a status message.
     */
    public static String scheduleAlarm (Context context, long alarmId, ArrayList<Long> triggerTimes) {
        // TODO: Set exact, REPEATING Alarms.
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        int type = AlarmManager.RTC_WAKEUP;

        String message = null;

        // TODO: For Android 12 (api 31) or higher:
        //  https://developer.android.com/training/scheduling/alarms#exact-permission-declare
        // Schedule an exact alarm that will trigger at the specified time(s) -- in a non-lax,
        // non-lenient timely manner
        for (long triggerTime : triggerTimes) {
            Intent alarmIntent = new Intent(context, AlarmBroadcastReceiver.class);
            alarmIntent.setAction(FIRE_UP_ALARM);

            if (triggerTimes.size() > 1) {
                Uri multiDifferentiator =
                        Uri.parse(String.valueOf(AlarmContract.AlarmEntry.CONTENT_URI)).
                                buildUpon().appendPath(String.valueOf(alarmId)).
                                appendPath(String.valueOf(triggerTime)).build();
                Log.v(LOG_TAG, multiDifferentiator.toString());
                alarmIntent.setData(multiDifferentiator);
            }

            PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, (int) alarmId, alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(type, triggerTime, alarmPendingIntent);
                message = "Alarm set on an Android of API level >= M";
                Log.v(LOG_TAG, message);
            } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(type, triggerTime, alarmPendingIntent);
                message = "Alarm set on an Android of API level >= KITKAT";
                Log.v(LOG_TAG, message);
            } else {
                alarmManager.set(type, triggerTime, alarmPendingIntent);
                message = "Alarm set on an Android of API level < KITKAT";
                Log.v(LOG_TAG, message);
            }
        }
        return message;
    }

    /**
     * Utility method to help stop a currently ringing {@link Alarm} properly. Nothing will
     * happen, if reminder is not firing.
     *
     * @param context application context
     * @param alarmId id of the {@link Alarm} being stopped
     */
    public static void stopCurrentAlarmReminder (Context context, int alarmId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent stopAlarmIntent = new Intent(context, AlarmBroadcastReceiver.class);
        stopAlarmIntent.setAction(STOP_SCHEDULED_ALARM);

        PendingIntent stopAlarmPendingIntent = PendingIntent.getBroadcast(context, alarmId,
                stopAlarmIntent, PendingIntent.FLAG_NO_CREATE);

        if (stopAlarmPendingIntent != null && alarmManager != null)
        alarmManager.cancel(stopAlarmPendingIntent);
    }

    public static void stopAlarm (Context context, long alarmId/*, ArrayList<Long> triggerTimes*/) {
        Uri alarmsUri = AlarmContract.AlarmEntry.CONTENT_URI;
        String triggerTimes = null;
        ArrayList<Long> triggerTimesList;

        // Fetch (details of) the Alarm being stopped from dB
        try(Cursor theAlarm = context.getContentResolver().query(ContentUris.withAppendedId(alarmsUri, alarmId), null, null, null, null)) {
            if (theAlarm != null) {
                theAlarm.moveToFirst();
                triggerTimes = theAlarm.getString(theAlarm.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ALARM_TRIGGER_REPEAT_TIMES));
            }

            triggerTimesList = parseTriggerTimes(triggerTimes);

            for (long triggerTime : triggerTimesList) {
                Intent stopAlarmIntent = new Intent(context, AlarmBroadcastReceiver.class);
                stopAlarmIntent.setAction(FIRE_UP_ALARM);

                if (triggerTimesList.size() > 1) {
                    Uri multiDifferentiator =
                            Uri.parse(String.valueOf(AlarmContract.AlarmEntry.CONTENT_URI)).
                                    buildUpon().appendPath(String.valueOf(alarmId)).
                                    appendPath(String.valueOf(triggerTime)).build();
                    Log.v(LOG_TAG, "Cancelling alarm: " + multiDifferentiator.toString());
                    stopAlarmIntent.setData(multiDifferentiator);
                }

                PendingIntent stopAlarmPendingIntent = PendingIntent.getBroadcast(context, (int) alarmId,
                        stopAlarmIntent, PendingIntent.FLAG_NO_CREATE);

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (stopAlarmPendingIntent != null && alarmManager != null)
                    alarmManager.cancel(stopAlarmPendingIntent);

                ContentValues deactivationValue = new ContentValues();
                deactivationValue.put(AlarmContract.AlarmEntry.COLUMN_ALARM_STATE, 0);
                context.getContentResolver().update(
                        Uri.withAppendedPath(alarmsUri, String.valueOf(alarmId)),
                        deactivationValue, AlarmContract.AlarmEntry._ID + "=?",
                        new String[]{String.valueOf(alarmId)}
                );
                String title = theAlarm.getString(theAlarm.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ALARM_TITLE));
                Log.v(LOG_TAG, "Disabled alarm " + alarmId + "; titled " + title + " " + stopAlarmIntent.getDataString());

            }

        } catch (CursorIndexOutOfBoundsException cioobe) {
            cioobe.getLocalizedMessage();
        }

    }


    /**
     * Parse a {@link String} of trigger times of an {@link Alarm} into an {@link ArrayList} of
     * type {@link Long}.
     *
     * @param triggerTimes {@link String} of trigger times from dB.
     * @return {@link Long}{@link ArrayList} of trigger times in milliseconds since Unix Epoch time.
     */
    private static ArrayList<Long> parseTriggerTimes (String triggerTimes) {
        if (triggerTimes == null || triggerTimes.equals(""))
            return null;
        long now = Calendar.getInstance().getTimeInMillis();
        long nextTriggerTime;
        ArrayList<Long> triggerTimesList = new ArrayList<>();

        if (triggerTimes.contains(",")) {
            String[] timestamps = triggerTimes.split(", ");
            List<String> fixedLengthList = Arrays.asList(timestamps);
            ArrayList<String> listOfTimestamps = new ArrayList<>(fixedLengthList);

            for (String time : listOfTimestamps) {
                nextTriggerTime = Long.parseLong(time);
                // TODO: Remove invalid (past) triggerTimes
                // TODO: Replicate logic/algo used in #calculateTriggerTimes; this is buggy,
                //  and causes past Alarm(s) to ring instead of silently deactivating
                if (nextTriggerTime >= now) {
                    triggerTimesList.add(nextTriggerTime);
                    Log.v(LOG_TAG, "Next Trigger Time: " + nextTriggerTime);
                }
            }

        }

        return triggerTimesList;
    }

    private static ArrayList<Long> calculateTriggerTimes () {
        long now = Calendar.getInstance().getTimeInMillis();
        long nextTriggerTime = START_TIME_IN_MILLIS;
        ArrayList<Long> triggerTimesList = new ArrayList<>();

        while (nextTriggerTime < STOP_TIME_IN_MILLIS) {
            if (nextTriggerTime > now) {
                triggerTimesList.add(nextTriggerTime);
                Log.v(LOG_TAG, "Added trigger time: " + nextTriggerTime);
            }
            nextTriggerTime += REPEAT_INTERVAL;
        }

        return triggerTimesList;
    }

}
