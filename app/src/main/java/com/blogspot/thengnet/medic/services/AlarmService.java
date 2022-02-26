package com.blogspot.thengnet.medic.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.blogspot.thengnet.medic.google.alarms.AlarmActivity;
import com.blogspot.thengnet.medic.receivers.AlarmBroadcastReceiver;
import com.blogspot.thengnet.medic.utilities.NotificationUtil;

import androidx.annotation.Nullable;

public class AlarmService extends IntentService {
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

    /** A public action sent by AlarmService when the alarm has started.  */
    public final static String  ALARM_ALERT_ACTION = "ALARM_ALERT";

    /** A public action sent by AlarmService when the alarm has stopped for any reason.  */
    public final static String ALARM_DONE_ACTION = "ALARM_DONE";

    /** Private action used to stop an alarm with this service.  */
    public final static String  STOP_ALARM_ACTION = "STOP_ALARM";


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * #name Used to name the worker thread, important only for debugging.
     */
    public AlarmService () {
        super(AlarmService.class.getName());
    }

    @Override
    protected void onHandleIntent (@Nullable Intent intent) {
        NotificationUtil.notifyUserToTakeMeds(getApplicationContext());
        Intent intent1 = new Intent(getBaseContext(), AlarmActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent1);
    }

}
