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

public class AppointmentProvider extends ContentProvider {

    private final static int APPOINTMENTS = 277;
    private final static int APPOINTMENT_ID = 274;
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AppointmentContract.CONTENT_AUTHORITY, AppointmentContract.APPOINTMENTS_PATH, APPOINTMENTS);
        sUriMatcher.addURI(AppointmentContract.CONTENT_AUTHORITY, AppointmentContract.APPOINTMENTS_PATH + "/#", APPOINTMENT_ID);
    }

    private MedicDbHelper mMedicDbHelper;

    /**
     * Initialize the {@link AppointmentProvider} and {@link MedicDbHelper} object.
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
            case APPOINTMENTS:
                table = theDb.query(AppointmentContract.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                // Register notifier on Cursor Object
                table.setNotificationUri(getContext().getContentResolver(), uri);

                return table;
            case APPOINTMENT_ID:
                selection = AppointmentContract.AppointmentEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                table = theDb.query(AppointmentContract.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);

                // Register notifier on Cursor Object
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
        String appointmentTitle = contentValues.getAsString(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_TITLE);
        int appointmentReminderState = contentValues.getAsInteger(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_STATE);
        String reminderStartDate = contentValues.getAsString(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_START_DATE);
        String reminderStopDate = contentValues.getAsString(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_STOP_DATE);
        String appointmentTime = contentValues.getAsString(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_TIME);
        String appointmentLocation = contentValues.getAsString(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_LOCATION);
        String appointmentReminderTime = contentValues.getAsString(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_REMINDER_TIME);
        int appointmentReminderRepeatStatus = contentValues.getAsInteger(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_REPEAT_STATE);

        if (appointmentTitle == null || appointmentTitle.equals(""))
            throw new IllegalArgumentException("Appointment Title is missing!");
        if (appointmentReminderState != 0 && appointmentReminderState != 1)
            throw new IllegalArgumentException("Appointment Reminder State not set!");
        if (reminderStartDate == null || reminderStartDate.equals(""))
            throw new IllegalArgumentException("Appointment Reminder Start Date missing!");
        if (reminderStopDate == null || reminderStopDate.equals(""))
            throw new IllegalArgumentException("Appointment Reminder Stop Date missing!");
        if (appointmentTime == null || appointmentTime.equals(""))
            throw new IllegalArgumentException("Appointment Time not set!");
        if (appointmentLocation == null || appointmentLocation.equals(""))
            throw new IllegalArgumentException("Appointment Location missing!");
        if (appointmentReminderTime == null || appointmentReminderTime.equals(""))
            throw new IllegalArgumentException("Appointment Reminder Time missing!");
        if (appointmentReminderRepeatStatus != 0 && appointmentReminderRepeatStatus != 1)
            throw new IllegalArgumentException("Appointment Reminder Repeat Status not set!");

        final int UriType = sUriMatcher.match(uri);
        switch (UriType) {
            case APPOINTMENTS:
                return addAppointment(uri, contentValues);
            default:
                throw new IllegalArgumentException("INSERT Content uri unknown!");
        }
    }

    @Override
    public int delete (@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted;
        final int deleteType = sUriMatcher.match(uri);
        switch (deleteType) {
            case APPOINTMENTS:
                rowsDeleted = mMedicDbHelper.getWritableDatabase().delete(AppointmentContract.TABLE_NAME, selection, selectionArgs);
                break;
            case APPOINTMENT_ID:
                selection = AppointmentContract.AppointmentEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowsDeleted = mMedicDbHelper.getWritableDatabase().delete(AppointmentContract.TABLE_NAME, selection, selectionArgs);
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
            case APPOINTMENTS:
                return updateAppointment(uri, contentValues, selection, selectionArgs);
            case APPOINTMENT_ID:
                selection = AppointmentContract.AppointmentEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateAppointment(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("UPDATE Content URI unknown!");
        }
    }

    private Uri addAppointment (Uri uri, ContentValues values) {
        // Notify the CursorLoader when the Cursor data has changed when an appointment is added
        getContext().getContentResolver().notifyChange(uri, null);

        long id = mMedicDbHelper.getWritableDatabase().insert(AppointmentContract.TABLE_NAME,
                null, values);
        return ContentUris.withAppendedId(uri, id);
    }

    private int updateAppointment (Uri uri, ContentValues updateValues, String whereClause,
                                   String[] whereArg) {
        if (updateValues.containsKey(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_TITLE)) {
            String appointmentTitle = updateValues.getAsString(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_TITLE);
            if (appointmentTitle == null || appointmentTitle.equals(""))
                throw new IllegalArgumentException("Appointment Title passed but not updated!");
        }
        if (updateValues.containsKey(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_STATE)) {
            int appointmentReminderState = updateValues.getAsInteger(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_STATE);
            if (!(appointmentReminderState == 0 || appointmentReminderState == 1))
                throw new IllegalArgumentException("Appointment Reminder State passed, but as a null value!");
        }
        if (updateValues.containsKey(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_START_DATE)) {
            String reminderStartDate = updateValues.getAsString(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_START_DATE);
            if (reminderStartDate == null || reminderStartDate.equals(""))
                throw new IllegalArgumentException("Appointment Reminder Start Date passed but not updated!");
        }
        if (updateValues.containsKey(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_STOP_DATE)) {
            String reminderStopDate = updateValues.getAsString(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_STOP_DATE);
            if (reminderStopDate == null || reminderStopDate.equals(""))
                throw new IllegalArgumentException("Appointment Reminder Stop Date passed but not updated!");
        }
        if (updateValues.containsKey(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_TIME)) {
            String appointmentReminderTime = updateValues.getAsString(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_TIME);
            if (appointmentReminderTime == null || appointmentReminderTime.equals(""))
                throw new IllegalArgumentException("Appointment Reminder Time passed, but as a null value!");
        }
        if (updateValues.containsKey(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_LOCATION)) {
            String appointmentLocation = updateValues.getAsString(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_LOCATION);
            if (appointmentLocation == null || appointmentLocation.equals(""))
                throw new IllegalArgumentException("Appointment Location passed, but as a null value!");
        }
        if (updateValues.containsKey(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_REMINDER_TIME)) {
            String appointmentReminderTime = updateValues.getAsString(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_REMINDER_TIME);
            if (appointmentReminderTime == null || appointmentReminderTime.equals(""))
                throw new IllegalArgumentException("Appointment Reminder Time passed, but as empty/null value!");
        }
        if (updateValues.containsKey(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_REPEAT_STATE)) {
            int appointmentReminderRepeatState = updateValues.getAsInteger(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_REPEAT_STATE);
            if (!(appointmentReminderRepeatState == 0 || appointmentReminderRepeatState == 1))
                throw new IllegalArgumentException("Appointment Repeat State passed, but as an invalid value!");
        }

        int rowsUpdated = mMedicDbHelper.getWritableDatabase().update(AppointmentContract.TABLE_NAME,
                updateValues, whereClause, whereArg);
        if (rowsUpdated > 0) {
            // Notify the CursorLoader when the Cursor data has changed when an appointment is updated
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

}
