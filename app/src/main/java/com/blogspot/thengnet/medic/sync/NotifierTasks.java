package com.blogspot.thengnet.medic.sync;

import android.content.Context;

import com.blogspot.thengnet.medic.utilities.NotificationUtil;

public class NotifierTasks {
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";
    public static final String ACTION_RESCHEDULE_ALARM = "reschedule-alarm";

    public static void executeTask (Context context, String action) {
        switch (action) {
            case ACTION_DISMISS_NOTIFICATION:
                NotificationUtil.clearNotification(context);
                break;
            case ACTION_RESCHEDULE_ALARM:
                break;
            default:
        }
    }
}
