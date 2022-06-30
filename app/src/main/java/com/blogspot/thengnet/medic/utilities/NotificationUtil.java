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
import com.blogspot.thengnet.medic.google.alarms.AlarmActivity;
import com.blogspot.thengnet.medic.sync.AlarmReminderIntentService;
import com.blogspot.thengnet.medic.sync.NotifierTasks;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class NotificationUtil {
    private static final String ALARMS_NOTIFICATION_CHANNEL_ID = "alarm-notification-channel";
    private static final String APPOINTMENTS_NOTIFICATION_CHANNEL_ID = "appointment-notification-channel";
    private static final String MAPS_NOTIFICATION_CHANNEL_ID = "map-notification-channel";

    private static final int ACTION_TAKE_MEDS_PENDING_INTENT_ID = 6;
    private static final int ACTION_REMIND_LATER_PENDING_INTENT_ID = 7;

    public static String createNotificationChannel (Context context, String channelName) {
        String channelId;
        switch (channelName) {
            case "alarm":
                channelId = ALARMS_NOTIFICATION_CHANNEL_ID;
                break;
            case "appointment":
                channelId = APPOINTMENTS_NOTIFICATION_CHANNEL_ID;
                break;
            case "map":
                channelId = MAPS_NOTIFICATION_CHANNEL_ID;
                break;
            default:
                channelId = "";
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel theChannel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            );
            // Register the channel with the system; importance and/or other notification behaviors
            // can't change after this
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(theChannel);

            return channelId;
        } else {
            // Return null for pre-O (26) devices.
            return null;
        }
    }

    public static void clearNotification (Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    /**
     * This method builds a notification to remind the user to take their scheduled medicine.
     *
     * @param context of the app
     * @return the {@link Notification} object.
     */
    public static Notification buildMedicationNotification (Context context) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                context, ALARMS_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.primaryColor))
                .setSmallIcon(R.drawable.twotone_alarm_white_20)
                .setContentTitle("Have you taken your meds?")
                .setContentText("It is vital that you take your medicine on time. GWS!")
//                .setStyle()
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(alarmContentIntent(context))
                .setFullScreenIntent(alarmActivityIntent(context), true)
                .addAction(takeMedsAction(context))
                .addAction(remindLaterAction(context))
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true);

        // Backwards compatibility --  If the build version is greater than or equal to JELLY_BEAN
        // and less than OREO, set the notification's priority to PRIORITY_HIGH.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

        return notificationBuilder.build();
    }

    /**
     * This methods creates the {@link PendingIntent} which will trigger when the notification is
     * pressed.
     *
     * @param context of the app
     * @return {@link PendingIntent} that should open up the {@link MainActivity}
     */
    private static PendingIntent alarmContentIntent (Context context) {
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        // Set the MainActivity to start in a new, empty task; preserve the user's expected
        // navigation experience after they open app via the notification (https://developer.android.com/training/notify-user/build-notification#click)
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(context, 0, mainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * This methods opens the {@link AlarmActivity} which will trigger at the specified time.
     *
     * @param context of the app
     * @return {@link PendingIntent} that should open up the {@link AlarmActivity}
     */
    private static PendingIntent alarmActivityIntent (Context context) {
        Intent startAlarmActivityIntent = new Intent(context, AlarmActivity.class);
        // Set the AlarmActivity to start in a new, empty task;
        startAlarmActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(context, 0, startAlarmActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
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

    public static NotificationCompat.Action remindLaterAction (Context context) {
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
