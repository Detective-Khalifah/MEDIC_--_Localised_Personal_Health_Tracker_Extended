package com.blogspot.thengnet.medic.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.blogspot.thengnet.medic.utilities.NotificationUtil;

import androidx.annotation.Nullable;

public class AlarmService extends Service {
    /**
     * AlarmActivity and AlarmService (when unbound) listen for this broadcast intent
     * so that other applications can snooze the alarm (after ALARM_ALERT_ACTION and before
     * ALARM_DONE_ACTION).
     */
    public final static String ALARM_SNOOZE_ACTION = "ALARM_SNOOZE";

    /**
     * AlarmActivity and AlarmService listen for this broadcast intent so that other
     * applications can dismiss the alarm (after ALARM_ALERT_ACTION and before ALARM_DONE_ACTION).
     */
    public final static String ALARM_DISMISS_ACTION = "ALARM_DISMISS";

    /**
     * A public action sent by AlarmService when the alarm has started.
     */
    public final static String ALARM_ALERT_ACTION = "ALARM_ALERT";

    /**
     * A public action sent by AlarmService when the alarm has stopped for any reason.
     */
    public final static String ALARM_DONE_ACTION = "ALARM_DONE";

    /**
     * Private action used to stop an alarm with this service.
     */
    public final static String STOP_ALARM_ACTION = "STOP_ALARM";

    @Nullable
    @Override
    public IBinder onBind (Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        Notification alarmNotification = NotificationUtil.buildMedicationNotification(getApplicationContext());
        startForeground(5, alarmNotification);
        return START_STICKY;
    }

}
