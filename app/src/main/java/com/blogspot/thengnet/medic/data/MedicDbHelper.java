package com.blogspot.thengnet.medic.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.blogspot.thengnet.medic.data.AlarmContract.AlarmEntry;
import com.blogspot.thengnet.medic.data.AppointmentContract.AppointmentEntry;

public class MedicDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = MedicDbHelper.class.getName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "medic.db";

    private static final String ALARM_TABLE_CREATION_STATEMENT = "CREATE TABLE " + AlarmContract.TABLE_NAME
            + " ( " + AlarmEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            AlarmEntry.COLUMN_ALARM_TITLE + " TEXT NOT NULL, " +
            AlarmEntry.COLUMN_ALARM_STATE + " INTEGER NOT NULL DEFAULT 1," +
            AlarmEntry.COLUMN_ADMINISTRATION_FORM + " TEXT NOT NULL," +
            AlarmEntry.COLUMN_ALARM_START_DATE + " TEXT NOT NULL, " +
            AlarmEntry.COLUMN_ALARM_STOP_DATE + " TEXT NOT NULL, " +
            AlarmEntry.COLUMN_ALARM_TIME + " TEXT NOT NULL, " + // TODO: Use a default value - current system time + 1 min - here
            AlarmEntry.COLUMN_ADMINISTRATION_PER_DAY + " TEXT NOT NULL," +
            AlarmEntry.COLUMN_ALARM_TONE + " TEXT NOT NULL," + // TODO: Use a default notification tone
            AlarmEntry.COLUMN_ALARM_VIBRATE_OR_NOT + " INTEGER NOT NULL DEFAULT 0," +
            AlarmEntry.COLUMN_ALARM_REPEAT_STATE + " INTEGER NOT NULL DEFAULT 0" +
            ");";

    private static final String APPOINTMENT_TABLE_CREATION_STATEMENT = "CREATE TABLE " + AppointmentContract.TABLE_NAME
            + " ( " + AppointmentEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            AppointmentEntry.COLUMN_APPOINTMENT_TITLE + " TEXT NOT NULL, " +
            AppointmentEntry.COLUMN_REMINDER_STATE + " INTEGER NOT NULL DEFAULT 1," +
            AppointmentEntry.COLUMN_REMINDER_START_DATE + " TEXT NOT NULL, " +
            AppointmentEntry.COLUMN_REMINDER_STOP_DATE + " TEXT NOT NULL, " +
            AppointmentEntry.COLUMN_APPOINTMENT_TIME + " TEXT NOT NULL," + // TODO: Use a default value - current system time + ... - here
            AppointmentEntry.COLUMN_APPOINTMENT_LOCATION + " TEXT NOT NULL, " +
            AppointmentEntry.COLUMN_APPOINTMENT_REMINDER_TIME + " TEXT NOT NULL, " +
            AppointmentEntry.COLUMN_REMINDER_REPEAT_STATE + " INTEGER NOT NULL DEFAULT 0" +
            ");";

    public MedicDbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL(ALARM_TABLE_CREATION_STATEMENT);
        db.execSQL(APPOINTMENT_TABLE_CREATION_STATEMENT);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database. Existing contents will be lost." +
                "[" + oldVersion + "] --> [" + newVersion + "]");
        db.execSQL("DROP TABLE IF EXISTS " + AlarmContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AppointmentContract.TABLE_NAME);
        onCreate(db);
    }
}
