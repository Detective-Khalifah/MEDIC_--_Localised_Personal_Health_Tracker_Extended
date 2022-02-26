package com.blogspot.thengnet.medic.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.blogspot.thengnet.medic.services.AlarmService;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

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
        Intent alarmIntentService = new Intent(context, AlarmService.class);
//        alarmIntentService.putExtra(label, intent.getStringExtra(label));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(alarmIntentService);
        } else {
            context.startService(alarmIntentService);
        }
    }
}
