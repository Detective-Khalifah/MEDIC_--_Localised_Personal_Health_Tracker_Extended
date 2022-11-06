package com.blogspot.thengnet.medic.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AlarmProvider extends ContentProvider {

    private final static int ALARMS = 257;
    private final static int ALARM_ID = 254;
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AlarmContract.CONTENT_AUTHORITY, AlarmContract.ALARMS_PATH, ALARMS);
        sUriMatcher.addURI(AlarmContract.CONTENT_AUTHORITY, AlarmContract.ALARMS_PATH + "/#", ALARM_ID);
    }

    private MedicDbHelper mMedicDbHelper;

    /**
     * Initialize the {@link AlarmProvider} and {@link MedicDbHelper} object.
     *
     * @return the status.
     */
    @Override
    public boolean onCreate () {
        mMedicDbHelper = new MedicDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query (@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor table;
        SQLiteDatabase theDb = mMedicDbHelper.getReadableDatabase();

        int UriType = sUriMatcher.match(uri);
        switch (UriType) {
            case ALARMS:
                table = theDb.query(AlarmContract.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                // Register notifier on Cursor Object
                table.setNotificationUri(getContext().getContentResolver(), uri);

                return table;
            case ALARM_ID:
                selection = AlarmContract.AlarmEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                table = theDb.query(AlarmContract.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);

                // Register notifier on Cursor Object
                table.setNotificationUri(getContext().getContentResolver(), uri);

                return table;
            default:
                throw new IllegalArgumentException("Content URI \"" + uri + " is unknown!");
        }
    }

    @Nullable
    @Override
    public String getType (@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert (Uri uri, ContentValues contentValues) {
        String alarmTitle = contentValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ALARM_TITLE);
        int alarmState = contentValues.getAsInteger(AlarmContract.AlarmEntry.COLUMN_ALARM_STATE);
        String alarmStartDate = contentValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ALARM_START_DATE);
        String alarmStopDate = contentValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ALARM_STOP_DATE);
        String alarmTime = contentValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ALARM_TIME);
        String administrationPerDay = contentValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ADMINISTRATION_PER_DAY);
        String alarmTone = contentValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ALARM_TONE);
        int alarmVibrateState = contentValues.getAsInteger(AlarmContract.AlarmEntry.COLUMN_ALARM_VIBRATE_OR_NOT);
        int alarmRepeatState = contentValues.getAsInteger(AlarmContract.AlarmEntry.COLUMN_ALARM_REPEAT_STATE);

        if (alarmTitle == null || alarmTitle.equals(""))
            throw new IllegalArgumentException("Alarm Title is missing!");
        if (alarmState != 0 && alarmState != 1)
            throw new IllegalArgumentException("Alarm State is missing!");
        if (alarmStartDate == null || alarmStartDate.equals(""))
            throw new IllegalArgumentException("Alarm Start Date missing!");
        if (alarmStopDate == null || alarmStopDate.equals(""))
            throw new IllegalArgumentException("Alarm Stop Date missing!");
        if (alarmTime == null || alarmTime.equals(""))
            throw new IllegalArgumentException("Alarm time missing!");
        if (administrationPerDay == null || administrationPerDay.equals(""))
            throw new IllegalArgumentException("Administration Per Day missing!");
        if (alarmTone == null || alarmTone.equals(""))
            throw new IllegalArgumentException("Alarm Tone missing!");
        if (alarmVibrateState != 0 && alarmVibrateState != 1)
            throw new IllegalArgumentException("Alarm Vibration State is missing!");
        if (alarmRepeatState != 0 && alarmRepeatState != 1)
            throw new IllegalArgumentException("Alarm Repeat State is missing!");

        final int UriType = sUriMatcher.match(uri);
        switch (UriType) {
            case ALARMS:
                return addAlarm(uri, contentValues);
            default:
                throw new IllegalArgumentException("INSERT Content uri unknown!");
        }
    }

    @Override
    public int delete (@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted;
        final int deleteType = sUriMatcher.match(uri);
        switch (deleteType) {
            case ALARMS:
                rowsDeleted = mMedicDbHelper.getWritableDatabase().delete(AlarmContract.TABLE_NAME, selection, selectionArgs);
                break;
            case ALARM_ID:
                selection = AlarmContract.AlarmEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowsDeleted = mMedicDbHelper.getWritableDatabase().delete(AlarmContract.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("DELETE Content uri unknown!");
        }
        if (rowsDeleted > 0) {
            // Notify the CursorLoader when the Cursor data has changed due to a delete query
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update (@NonNull Uri uri, ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int UriType = sUriMatcher.match(uri);
        switch (UriType) {
            case ALARMS:
                return updateAlarm(uri, contentValues, selection, selectionArgs);
            case ALARM_ID:
                selection = AlarmContract.AlarmEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateAlarm(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("UPDATE Content URI unknown!");
        }
    }

    private Uri addAlarm (Uri uri, ContentValues values) {
        // Notify the CursorLoader when the Cursor data has changed when an Alarm is added
        getContext().getContentResolver().notifyChange(uri, null);

        long id = mMedicDbHelper.getWritableDatabase().insert(AlarmContract.TABLE_NAME,
                null, values);
        return ContentUris.withAppendedId(uri, id);
    }

    private int updateAlarm (Uri uri, ContentValues updateValues, String whereClause,
                             String[] whereArg) {
        if (updateValues.containsKey(AlarmContract.AlarmEntry.COLUMN_ALARM_TITLE)) {
            String alarmTitle = updateValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ALARM_TITLE);
            if (alarmTitle == null || alarmTitle.equals(""))
                throw new IllegalArgumentException("Alarm Title passed but not updated!");
        }
        if (updateValues.containsKey(AlarmContract.AlarmEntry.COLUMN_ALARM_STATE)) {
            int alarmState = updateValues.getAsInteger(AlarmContract.AlarmEntry.COLUMN_ALARM_STATE);
            if (!(alarmState == 0 || alarmState == 1))
                throw new IllegalArgumentException("Alarm Active State passed, but as a null value!");
        }
        if (updateValues.containsKey(AlarmContract.AlarmEntry.COLUMN_ALARM_START_DATE)) {
            String alarmStartDate = updateValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ALARM_START_DATE);
            if (alarmStartDate == null || alarmStartDate.equals(""))
                throw new IllegalArgumentException("Alarm Start Date passed but not updated!");
        }
        if (updateValues.containsKey(AlarmContract.AlarmEntry.COLUMN_ALARM_STOP_DATE)) {
            String alarmStopDate = updateValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ALARM_STOP_DATE);
            if (alarmStopDate == null || alarmStopDate.equals(""))
                throw new IllegalArgumentException("Alarm Stop Date passed but not updated!");
        }
        if (updateValues.containsKey(AlarmContract.AlarmEntry.COLUMN_ALARM_TIME)) {
            String alarmTime = updateValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ALARM_TIME);
            if (alarmTime == null || alarmTime.equals(""))
                throw new IllegalArgumentException("Alarm Time passed, but as a null value!");
        }
        if (updateValues.containsKey(AlarmContract.AlarmEntry.COLUMN_ADMINISTRATION_PER_DAY)) {
            String administrationPerDay = updateValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ADMINISTRATION_PER_DAY);
            if (administrationPerDay == null || administrationPerDay.equals(""))
                throw new IllegalArgumentException("Alarm Administration Per Day passed, but as a null value!");
        }
        if (updateValues.containsKey(AlarmContract.AlarmEntry.COLUMN_ALARM_TONE)) {
            String alarmTone = updateValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ALARM_TONE);
            if (alarmTone == null || alarmTone.equals(""))
                throw new IllegalArgumentException("Alarm Tone passed, but as a null value!");
        }
        if (updateValues.containsKey(AlarmContract.AlarmEntry.COLUMN_ALARM_VIBRATE_OR_NOT)) {
            int alarmVibrateState = updateValues.getAsInteger(AlarmContract.AlarmEntry.COLUMN_ALARM_VIBRATE_OR_NOT);
            if (!(alarmVibrateState == 0 || alarmVibrateState == 1))
                throw new IllegalArgumentException("Alarm Vibration state passed, but as a null value!");
        }
        if (updateValues.containsKey(AlarmContract.AlarmEntry.COLUMN_ALARM_REPEAT_STATE)) {
            int alarmRepeatState = updateValues.getAsInteger(AlarmContract.AlarmEntry.COLUMN_ALARM_REPEAT_STATE);
            if (!(alarmRepeatState == 0 || alarmRepeatState == 1))
                throw new IllegalArgumentException("Alarm Repeat State passed, but as a null value!");
        }
        if (updateValues.containsKey(AlarmContract.AlarmEntry.COLUMN_ALARM_TRIGGER_REPEAT_TIMES)) {
            String alarmRepeatTimes = updateValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ALARM_TRIGGER_REPEAT_TIMES);
            if (alarmRepeatTimes == null || alarmRepeatTimes.equals(""))
                throw new IllegalArgumentException("Alarm Repeat Times passed, but as a null value!");
        }

        int rowsUpdated = mMedicDbHelper.getWritableDatabase().update(AlarmContract.TABLE_NAME,
                updateValues, whereClause, whereArg);
        if (rowsUpdated > 0) {
            // Notify the CursorLoader when the Cursor data has changed when an Alarm is updated
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

}
