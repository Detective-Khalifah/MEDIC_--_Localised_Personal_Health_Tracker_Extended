package com.blogspot.thengnet.medic.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.blogspot.thengnet.medic.data.AlarmContract.AlarmEntry;
import com.blogspot.thengnet.medic.data.CalendarContract.CalendarEntry;

public class MedicDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "medic.db";

    private static final String ALARM_TABLE_CREATION_STATEMENT = "CREATE TABLE " + AlarmContract.TABLE_NAME
            + " ( " + AlarmEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            AlarmEntry.COLUMN_ALARM_LABEL + " TEXT NOT NULL, " +
            AlarmEntry.COLUMN_ALARM_TIME + " TEXT NOT NULL, " + // TODO: Use a default value - 30 minutes - here
            AlarmEntry.COLUMN_ALARM_START_DATE + " TEXT NOT NULL, " +
            AlarmEntry.COLUMN_ALARM_STOP_DATE + " TEXT NOT NULL, " +
            AlarmEntry.COLUMN_ALARM_STATUS + " INTEGER NOT NULL DEFAULT 1," +
            AlarmEntry.COLUMN_ALARM_REPEAT_STATUS + " INTEGER NOT NULL DEFAULT 0," +
            AlarmEntry.COLUMN_ALARM_REPEAT_DATES + " TEXT NOT NULL," +
            AlarmEntry.COLUMN_ALARM_VIBRATE_OR_NOT + " INTEGER NOT NULL DEFAULT 0," +
            AlarmEntry.COLUMN_ALARM_TONE + " TEXT NOT NULL " + // TODO: Use a default notification tone
            ");";
    private static final String CALENDAR_TABLE_CREATION_STATEMENT = "CREATE TABLE " + CalendarContract.TABLE_NAME
            + " ( " + CalendarEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            CalendarEntry.COLUMN_CALENDAR_EVENT_LABEL + " TEXT NOT NULL, " +
            CalendarEntry.COLUMN_EVENT_REMINDER_TIME + " TEXT NOT NULL," + // TODO: Use a default value - 30 minutes - here
            CalendarEntry.COLUMN_EVENT_START_DATE + " TEXT NOT NULL, " +
            CalendarEntry.COLUMN_EVENT_STOP_DATE + " TEXT NOT NULL, " +
            CalendarEntry.COLUMN_EVENT_REMINDER_STATUS + " INTEGER NOT NULL DEFAULT 1," +
            CalendarEntry.COLUMN_EVENT_REPEAT_STATUS + " INTEGER NOT NULL DEFAULT 0," +
            CalendarEntry.COLUMN_EVENT_REPEAT_DATES + " TEXT NOT NULL, " +
            CalendarEntry.COLUMN_EVENT_LOCATION + " TEXT NOT NULL" +
            ");";

    public MedicDbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL(ALARM_TABLE_CREATION_STATEMENT);
        db.execSQL(CALENDAR_TABLE_CREATION_STATEMENT);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AlarmContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CalendarContract.TABLE_NAME);
        onCreate(db);
    }
}
