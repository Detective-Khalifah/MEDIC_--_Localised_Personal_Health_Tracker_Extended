package com.blogspot.thengnet.medic.sync;

import android.content.Context;

import com.blogspot.thengnet.medic.utilities.NotificationUtil;

public class NotifierTasks {
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";
    public static final String ACTION_DISMISS_NOTIFICATIONS = "dismiss-notifications";

    public static void executeTask (Context context, String action) {
        if (action.equals(ACTION_DISMISS_NOTIFICATION))
            NotificationUtil.clearNotification(context);
    }
}
