package com.blogspot.thengnet.medic.sync;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class AlarmReminderIntentService extends IntentService {

    public AlarmReminderIntentService() {
        super(AlarmReminderIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent (@Nullable Intent intent) {
        String action = intent.getAction();
        NotifierTasks.executeTask(this, action);
    }
}
