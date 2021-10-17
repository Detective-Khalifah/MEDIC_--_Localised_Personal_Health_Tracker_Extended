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

public class CalendarProvider extends ContentProvider {

    private final static int EVENTS = 2;
    private final static int EVENT_ID = 22;
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(CalendarContract.CONTENT_AUTHORITY, CalendarContract.CALENDAR_EVENTS_PATH, EVENTS);
        sUriMatcher.addURI(CalendarContract.CONTENT_AUTHORITY, CalendarContract.CALENDAR_EVENTS_PATH + "/#", EVENT_ID);
    }

    private MedicDbHelper mMedicDbHelper;

    /**
     * Initialize the {@link CalendarProvider} and {@link MedicDbHelper} object.
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
            case EVENTS:
                table = theDb.query(CalendarContract.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                // Register notifier on cursor object
                table.setNotificationUri(getContext().getContentResolver(), uri);

                return table;
            case EVENT_ID:
                selection = CalendarContract.CalendarEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                table = theDb.query(CalendarContract.TABLE_NAME, projection, selection, selectionArgs,
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
        String eventLabel = contentValues.getAsString(CalendarContract.CalendarEntry.COLUMN_CALENDAR_EVENT_LABEL);
        String eventStartDate = contentValues.getAsString(CalendarContract.CalendarEntry.COLUMN_EVENT_START_DATE);
        String eventEndDate = contentValues.getAsString(CalendarContract.CalendarEntry.COLUMN_EVENT_STOP_DATE);
        int eventReminderStatus = contentValues.getAsInteger(CalendarContract.CalendarEntry.COLUMN_EVENT_REMINDER_STATUS);
        String eventReminderTime = contentValues.getAsString(CalendarContract.CalendarEntry.COLUMN_EVENT_REMINDER_TIME);
        int eventReminderRepeatStatus = contentValues.getAsInteger(CalendarContract.CalendarEntry.COLUMN_EVENT_REPEAT_STATUS);
        String eventReminderRepeatDates = contentValues.getAsString(CalendarContract.CalendarEntry.COLUMN_EVENT_REPEAT_DATES);
        String eventLocation = contentValues.getAsString(CalendarContract.CalendarEntry.COLUMN_EVENT_LOCATION);

        if (eventLabel == null)
            throw new IllegalArgumentException("Event label is missing!");
        if (eventStartDate == null)
            throw new IllegalArgumentException("Event reminder start date missing!");
        if (eventEndDate == null)
            throw new IllegalArgumentException("Event reminder end date missing!");
        if (eventReminderStatus == 1 && eventReminderTime == null)
            throw new IllegalArgumentException("Event reminder set active, but time missing!");
        if (eventReminderRepeatStatus == 1 && eventReminderRepeatDates == null)
            throw new IllegalArgumentException("Event set to repeat, but repeat dates missing!");
        if (eventLocation == null)
            throw new IllegalArgumentException("Event location missing!");

        final int UriType = sUriMatcher.match(uri);
        switch (UriType) {
            case EVENTS:
                return addEvent(uri, contentValues);
            default:
                throw new IllegalArgumentException("INSERT Content uri unknown!");
        }
    }

    @Override
    public int delete (@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted;
        final int deleteType = sUriMatcher.match(uri);
        switch (deleteType) {
            case EVENTS:
                rowsDeleted = mMedicDbHelper.getWritableDatabase().delete(CalendarContract.TABLE_NAME, selection, selectionArgs);
                break;
            case EVENT_ID:
                selection = CalendarContract.CalendarEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowsDeleted = mMedicDbHelper.getWritableDatabase().delete(CalendarContract.TABLE_NAME, selection, selectionArgs);
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
            case EVENTS:
                return updateEvent(uri, contentValues, selection, selectionArgs);
            case EVENT_ID:
                selection = CalendarContract.CalendarEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateEvent(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("UPDATE Content URI unknown!");
        }
    }

    private Uri addEvent (Uri uri, ContentValues values) {
        // Notify the cursor loader when the cursor data has changed when a note is added
        getContext().getContentResolver().notifyChange(uri, null);

        long id = mMedicDbHelper.getWritableDatabase().insert(CalendarContract.TABLE_NAME,
                null, values);
        return ContentUris.withAppendedId(uri, id);
    }

    private int updateEvent (Uri uri, ContentValues updateValues, String whereClause,
                             String[] whereArg) {
        if (updateValues.containsKey(CalendarContract.CalendarEntry.COLUMN_EVENT_START_DATE)) {
            String eventStartDate = updateValues.getAsString(CalendarContract.CalendarEntry.COLUMN_EVENT_START_DATE);
            if (eventStartDate == null || eventStartDate.equals(""))
                throw new IllegalArgumentException("Event reminder start date passed but not updated!");
        }
        if (updateValues.containsKey(CalendarContract.CalendarEntry.COLUMN_EVENT_STOP_DATE)) {
            String eventEndDate = updateValues.getAsString(CalendarContract.CalendarEntry.COLUMN_EVENT_STOP_DATE);
            if (eventEndDate == null || eventEndDate.equals(""))
                throw new IllegalArgumentException("Event reminder end date passed but not updated!");
        }
        if (updateValues.containsKey(CalendarContract.CalendarEntry.COLUMN_CALENDAR_EVENT_LABEL)) {
            String eventLabel = updateValues.getAsString(CalendarContract.CalendarEntry.COLUMN_CALENDAR_EVENT_LABEL);
            if (eventLabel == null || eventLabel.equals(""))
                throw new IllegalArgumentException("Event label passed but not updated!");
        }
        if (updateValues.containsKey(CalendarContract.CalendarEntry.COLUMN_EVENT_REPEAT_STATUS)) {
            int eventReminderStatus = updateValues.getAsInteger(CalendarContract.CalendarEntry.COLUMN_EVENT_REPEAT_STATUS);
            if (!(eventReminderStatus == 0 || eventReminderStatus == 1))
                throw new IllegalArgumentException("Event Reminder Status passed, but as a null value!");
        }
        if (updateValues.containsKey(CalendarContract.CalendarEntry.COLUMN_EVENT_REMINDER_TIME)) {
            String eventReminderTime = updateValues.getAsString(CalendarContract.CalendarEntry.COLUMN_EVENT_REMINDER_TIME);
            if (eventReminderTime == null || eventReminderTime.equals(""))
                throw new IllegalArgumentException("Event Reminder Time passed, but as a null value!");
        }
        if (updateValues.containsKey(CalendarContract.CalendarEntry.COLUMN_EVENT_REPEAT_STATUS)) {
            int eventReminderRepeatStatus = updateValues.getAsInteger(CalendarContract.CalendarEntry.COLUMN_EVENT_REPEAT_STATUS);
            if (!(eventReminderRepeatStatus == 0 || eventReminderRepeatStatus == 1))
                throw new IllegalArgumentException("Event Repeat Status passed, but as an invalid value!");
        }
        if (updateValues.containsKey(CalendarContract.CalendarEntry.COLUMN_EVENT_REPEAT_DATES)) {
            String[] eventReminderRepeatDates = (String[]) updateValues.get(CalendarContract.CalendarEntry.COLUMN_EVENT_REPEAT_DATES);
            if (eventReminderRepeatDates == null || eventReminderRepeatDates.length == 0)
                throw new IllegalArgumentException("Event Repeat Date(s) passed, but as an empty/null value!");
        }
        if (updateValues.containsKey(CalendarContract.CalendarEntry.COLUMN_EVENT_LOCATION)) {
            String eventLocation = updateValues.getAsString(CalendarContract.CalendarEntry.COLUMN_EVENT_LOCATION);
            if (eventLocation == null || eventLocation.equals(""))
                throw new IllegalArgumentException("Event Location passed, but as a null value!");
        }

        int rowsUpdated = mMedicDbHelper.getWritableDatabase().update(CalendarContract.TABLE_NAME,
                updateValues, whereClause, whereArg);
        if (rowsUpdated > 0) {
            // Notify the cursor loader when the cursor data has changed when an alarm is updated
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

}
