package com.blogspot.thengnet.medic.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.blogspot.thengnet.medic.data.AlarmContract;
import com.blogspot.thengnet.medic.services.AlarmService;
import com.blogspot.thengnet.medic.utilities.AlarmScheduler;
import com.blogspot.thengnet.medic.utilities.TimeConverter;

import java.util.Calendar;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = AlarmBroadcastReceiver.class.getName();

    public static final String FIRE_UP_ALARM = "fire-up";
    public static final String STOP_SCHEDULED_ALARM = "stop-scheduled-alarm";
    public static final String DISMISS_ALARM = "dismiss-alarm";
    public static final String RESCHEDULE_ENABLED_ALARMS = "reschedule-enabled-alarms";
    public static final String STOP_SCHEDULED_APPOINTMENT_REMINDER = "stop-scheduled-appointment";
    public static final String DISMISS_APPOINTMENT_REMINDER = "dismiss-appointment-reminder";

    @Override
    public void onReceive (Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_BOOT_COMPLETED:
            case Intent.ACTION_LOCKED_BOOT_COMPLETED:
            case Intent.ACTION_TIME_CHANGED: // android.intent.action.TIME_SET
                // TODO: Schedule alarm using AlarmScheduler, only if Alarm is ON; let
                //  AlarmScheduler decide to schedule or turn OFF depending on Alarm time.
                Log.v(LOG_TAG, "onReceive calling assessAlarms()");
                AlarmScheduler.assessAlarms(context);
                Log.v(LOG_TAG, "assessAlarms() done!");
                break;

            // TODO: Design logic in case of app re-installs, force stop,... It should comprise
            //  trivial but important tasks such as checking if alarm tone's exist; brings to mind
            //  case where app is installed in multiple devices -- offline SQLite db would have to
            //  be used to store such trifles as alarm tone.
            case RESCHEDULE_ENABLED_ALARMS:
                break;
            case FIRE_UP_ALARM:
                startAlarmService(context, intent);
                break;
            case STOP_SCHEDULED_ALARM:
            default:
        }
    }

    private void startAlarmService(Context context, Intent intent) {
        Intent alarmServiceIntent = new Intent(context, AlarmService.class);
//        alarmServiceIntent.putExtra(label, intent.getStringExtra(label));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(alarmServiceIntent);
        } else {
            context.startService(alarmServiceIntent);
        }
    }

    private boolean checkIfAlarmDue(){
//        String startDate = theAlarm.getString(theAlarm.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ALARM_START_DATE));
//        String stopDate = theAlarm.getString(theAlarm.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ALARM_STOP_DATE));
//        String startTime = theAlarm.getString(theAlarm.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ALARM_TIME));
//        String adminPerDay = theAlarm.getString(theAlarm.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ADMINISTRATION_PER_DAY));
//
//        Log.v(LOG_TAG, String.format("Start Date: %s\nStop Date: %s\n1st Time: %s\nDosing: %s", startDate, stopDate, startTime, %s));
//
//        long now = Calendar.getInstance().getTimeInMillis();
//        long alarmTime = TimeConverter.getTimeInMillis(startDate, startTime);
//        if (now < alarmTime) {
//
//        }
        return true;
    }
}
