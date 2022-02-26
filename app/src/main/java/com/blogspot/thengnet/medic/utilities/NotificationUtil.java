package com.blogspot.thengnet.medic.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.blogspot.thengnet.medic.MainActivity;
import com.blogspot.thengnet.medic.R;
import com.blogspot.thengnet.medic.sync.AlarmReminderIntentService;
import com.blogspot.thengnet.medic.sync.NotifierTasks;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class NotificationUtil {
    private static final int MEDS_REMINDER_NOTIFICATION_ID = 5;

    private static final int ACTION_TAKE_MEDS_PENDING_INTENT_ID = 6;

    private static final int ACTION_REMIND_LATER_PENDING_INTENT_ID = 7;

    public static void clearNotification (Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    /**
     * This method creates a notification to remind the user to take their scheduled medicine.
     * @param context of the app
     */
    public static void notifyUserToTakeMeds (Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel theChannel = new NotificationChannel(
                    String.valueOf(MEDS_REMINDER_NOTIFICATION_ID),
                    "Primary",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(theChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                context, String.valueOf(MEDS_REMINDER_NOTIFICATION_ID))
                .setColor(ContextCompat.getColor(context, R.color.primaryColor))
                .setSmallIcon(R.drawable.twotone_alarm_white_20)
//                .setLargeIcon()
                .setContentTitle("Have you taken your meds?")
                .setContentText("It is vital that you take your medicine on time. GWS!")
//                .setStyle()
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(alarmContentIntent(context))
                .addAction(takeMedsAction(context))
                .addAction(remindLaterAction(context))
                .setAutoCancel(true);

        // Backwards compatibility --  If the build version is greater than or equal to JELLY_BEAN
        // and less than OREO, set the notification's priority to PRIORITY_HIGH.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

        // Pass in a unique ID of choosing for the notification and notificationBuilder.build()
        notificationManager.notify(MEDS_REMINDER_NOTIFICATION_ID, notificationBuilder.build());
    }

    /**
     * This methods creates the {@link PendingIntent} which will trigger when the notification is
     * pressed.
     *
     * @param context of the app
     * @return {@link PendingIntent} that should open up the MainActivity.
     */
    private static PendingIntent alarmContentIntent (Context context) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, MEDS_REMINDER_NOTIFICATION_ID,
                startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static NotificationCompat.Action takeMedsAction (Context context) {
        // Create an Intent to launch AlarmReminderIntentService
        Intent stopAlarmIntent = new Intent(context, AlarmReminderIntentService.class);
        // Set the action of the intent to designate dismissing the notification
        stopAlarmIntent.setAction(NotifierTasks.ACTION_DISMISS_NOTIFICATION);

        // Create a PendingIntent from the intent to launch AlarmReminderIntentService
        PendingIntent stopAlarmPendingIntent = PendingIntent.getService(
                context,
                ACTION_TAKE_MEDS_PENDING_INTENT_ID,
                stopAlarmIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        // Create an Action for the user to ignore the notification (and dismiss it)
        NotificationCompat.Action stopAlarmAction = new NotificationCompat.Action(
                R.drawable.twotone_local_hospital_24,
                "I have taken medicine!",
                stopAlarmPendingIntent
        );
        return stopAlarmAction;
    }

    public static NotificationCompat.Action remindLaterAction(Context context) {
        Intent remindLaterIntent = new Intent(context, AlarmReminderIntentService.class);
        remindLaterIntent.setAction(NotifierTasks.ACTION_DISMISS_NOTIFICATION);
        PendingIntent remindLaterPendingIntent = PendingIntent.getService(context,
                ACTION_REMIND_LATER_PENDING_INTENT_ID,
                remindLaterIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action remindLaterAction = new NotificationCompat.Action(
                R.drawable.twotone_alarm_24,
                "Remind Later.",
                remindLaterPendingIntent);
        return remindLaterAction;
    }
}
