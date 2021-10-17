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

    private final static int ALARMS = 1;
    private final static int ALARM_ID = 11;
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
                // Register notifier on cursor object
                table.setNotificationUri(getContext().getContentResolver(), uri);

                return table;
            case ALARM_ID:
                selection = AlarmContract.AlarmEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                table = theDb.query(AlarmContract.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);

                // Register notifier on cursor object
                table.setNotificationUri(getContext().getContentResolver(), uri);

                return table;
            default:
                throw new IllegalArgumentException("Content URI is unknown!" + uri);
        }
    }

    @Nullable
    @Override
    public String getType (@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert (Uri uri, ContentValues contentValues) {
        String alarmLabel = contentValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ALARM_LABEL);
        String alarmStartDate = contentValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ALARM_START_DATE);
        String alarmEndDate = contentValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ALARM_STOP_DATE);
        int alarmStatus = contentValues.getAsInteger(AlarmContract.AlarmEntry.COLUMN_ALARM_STATUS);
        String alarmTime = contentValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ALARM_TIME);
        int alarmRepeatStatus = contentValues.getAsInteger(AlarmContract.AlarmEntry.COLUMN_ALARM_REPEAT_STATUS);
        String alarmRepeatDates = contentValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ALARM_REPEAT_DATES);
        int alarmVibrateStatus = contentValues.getAsInteger(AlarmContract.AlarmEntry.COLUMN_ALARM_VIBRATE_OR_NOT);
        String alarmTone =  contentValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ALARM_TONE);

        if (alarmLabel == null)
            throw new IllegalArgumentException("Alarm label is missing!");
        if (alarmStartDate == null)
            throw new IllegalArgumentException("Alarm start date missing!");
        if (alarmEndDate == null)
            throw new IllegalArgumentException("Alarm end date missing!");
        if (alarmTime == null)
            throw new IllegalArgumentException("Alarm time missing!");

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
            // Notify the cursor loader when the cursor data has changed due to a delete query
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
        // Notify the cursor loader when the cursor data has changed when a note is added
        getContext().getContentResolver().notifyChange(uri, null);

        long id = mMedicDbHelper.getWritableDatabase().insert(AlarmContract.TABLE_NAME,
                null, values);
        return ContentUris.withAppendedId(uri, id);
    }

    private int updateAlarm (Uri uri, ContentValues updateValues, String whereClause,
                            String[] whereArg) {
        if (updateValues.containsKey(AlarmContract.AlarmEntry.COLUMN_ALARM_START_DATE)) {
            String alarmStartDate = updateValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ALARM_START_DATE);
            if (alarmStartDate == null || alarmStartDate.equals(""))
                throw new IllegalArgumentException("Alarm start date passed but not updated!");
        }
        if (updateValues.containsKey(AlarmContract.AlarmEntry.COLUMN_ALARM_STOP_DATE)) {
            String alarmEndDate = updateValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ALARM_STOP_DATE);
            if (alarmEndDate == null || alarmEndDate.equals(""))
                throw new IllegalArgumentException("Alarm end date passed but not updated!");
        }
        if (updateValues.containsKey(AlarmContract.AlarmEntry.COLUMN_ALARM_LABEL)) {
            String alarmLabel = updateValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ALARM_LABEL);
            if (alarmLabel == null || alarmLabel.equals(""))
                throw new IllegalArgumentException("Alarm label passed but not updated!");
        }
        if (updateValues.containsKey(AlarmContract.AlarmEntry.COLUMN_ALARM_TIME)) {
            String alarmTime = updateValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ALARM_TIME);
            if (alarmTime == null || alarmTime.equals(""))
                throw new IllegalArgumentException("Alarm Time passed, but as a null value!");
        }
        if (updateValues.containsKey(AlarmContract.AlarmEntry.COLUMN_ALARM_STATUS)) {
            int alarmStatus = updateValues.getAsInteger(AlarmContract.AlarmEntry.COLUMN_ALARM_STATUS);
            if (!(alarmStatus == 0 || alarmStatus == 1))
                throw new IllegalArgumentException("Alarm Active Status passed, but as a null value!");
        }
        if (updateValues.containsKey(AlarmContract.AlarmEntry.COLUMN_ALARM_REPEAT_STATUS)) {
            int alarmRepeatStatus = updateValues.getAsInteger(AlarmContract.AlarmEntry.COLUMN_ALARM_REPEAT_STATUS);
            if (!(alarmRepeatStatus == 0 || alarmRepeatStatus == 1))
                throw new IllegalArgumentException("Alarm Repeat Status passed, but as a null value!");
        }
        if (updateValues.containsKey(AlarmContract.AlarmEntry.COLUMN_ALARM_REPEAT_DATES)) {
            String[] alarmRepeatDates = (String[]) updateValues.get(AlarmContract.AlarmEntry.COLUMN_ALARM_REPEAT_DATES);
            if (alarmRepeatDates == null || alarmRepeatDates.length == 0)
                throw new IllegalArgumentException("Alarm Repeat Date(s) passed, but as an empty/null value!");
        }
        if (updateValues.containsKey(AlarmContract.AlarmEntry.COLUMN_ALARM_VIBRATE_OR_NOT)) {
            int alarmVibrateStatus = updateValues.getAsInteger(AlarmContract.AlarmEntry.COLUMN_ALARM_VIBRATE_OR_NOT);
            if (!(alarmVibrateStatus == 0 || alarmVibrateStatus == 1))
                throw new IllegalArgumentException("Alarm Vibration status passed, but as a null value!");
        }
        if (updateValues.containsKey(AlarmContract.AlarmEntry.COLUMN_ALARM_TONE)) {
            String alarmTone = updateValues.getAsString(AlarmContract.AlarmEntry.COLUMN_ALARM_TONE);
            if (alarmTone == null || alarmTone.equals(""))
                throw new IllegalArgumentException("Alarm Tone passed, but as a null value!");
        }

        int rowsUpdated = mMedicDbHelper.getWritableDatabase().update(AlarmContract.TABLE_NAME,
                updateValues, whereClause, whereArg);
        if (rowsUpdated > 0) {
            // Notify the cursor loader when the cursor data has changed when an alarm is updated
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

}
