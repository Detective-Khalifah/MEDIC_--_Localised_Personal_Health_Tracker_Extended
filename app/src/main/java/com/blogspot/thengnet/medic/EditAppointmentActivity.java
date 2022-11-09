package com.blogspot.thengnet.medic;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.blogspot.thengnet.medic.data.Appointment;
import com.blogspot.thengnet.medic.data.AppointmentContract;
import com.blogspot.thengnet.medic.databinding.ActivityEditAppointmentBinding;
import com.blogspot.thengnet.medic.utilities.AppointmentScheduler;
import com.blogspot.thengnet.medic.utilities.ContextUtils;
import com.blogspot.thengnet.medic.utilities.TimeConverter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

public class EditAppointmentActivity extends AppCompatActivity {


    private final static String LOG_TAG = EditAppointmentActivity.class.getName();

    private static TimePickerDialog mTimePickerDialogFrag;
    private static DatePickerDialog mDatePickerDialogFrag;
    private TextInputEditText mActiveDateSelector, mActiveTimeSelector;
    private Snackbar editAppointmentNotifier;

    private static Uri mAppointmentUri;

    /**
     * Preference Keys
     */
    private static String HOUR_FORMAT_PREFERENCE, HOUR_FORMAT_12, HOUR_FORMAT_24;
    /**
     * Data of new {@link Appointment}
     */
    private static String mNewAppointmentTitle, mNewAppointmentStartDate, mNewAppointmentStopDate,
            mNewAppointmentTime, mNewAppointmentLocation, mNewAppointmentReminderTime;
    /**
     * Data of existing {@link Appointment}
     */
    private static String mSavedAppointmentTitle, mSavedAppointmentStartDate, mSavedAppointmentStopDate,
            mSavedAppointmentTime, mSavedAppointmentLocation, mSavedAppointmentReminderTime;

    private static int mAppointmentId;
    private SharedPreferences configurations;
    private ActivityEditAppointmentBinding binding;
    private Appointment currentAppointment;
    private /*final*/ int mYear, mMonth, mDay, mHours, mMinutes;
    private int mNewAppointmentState, mSavedAppointmentState,
            mNewAppointmentRepeatState, mSavedAppointmentRepeatState;
    private boolean isNewAppointment;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_appointment);

        // Get an instance of the system time
        Calendar now = Calendar.getInstance(Locale.getDefault());
        mYear = now.get(Calendar.YEAR);
        mMonth = now.get(Calendar.MONTH);
        mDay = now.get(Calendar.DAY_OF_MONTH);
        mHours = now.get(Calendar.HOUR_OF_DAY);
        mMinutes = now.get(Calendar.MINUTE);

        // Get link to SharedPreferences
        configurations = PreferenceManager.getDefaultSharedPreferences(this);
        HOUR_FORMAT_12 = getString(R.string.hour_format_12_key);
        HOUR_FORMAT_24 = getString(R.string.hour_format_24_key);
        HOUR_FORMAT_PREFERENCE = getString(R.string.settings_hour_format_key);

        // Find the FrameLayout used for the Snackbar and initialise to empty Snackbar, with text
        // altered at different stages of Activity
        editAppointmentNotifier = Snackbar.make(binding.editorSnackbarFrame, "",
                Snackbar.LENGTH_SHORT);

        Intent existingAppointment = getIntent();
        mAppointmentUri = existingAppointment.getData();

        // Fetch the saved appointment's details if its Uri was passed; do not fetch details of an
        // appointment that does not exist yet, otherwise -- initialise fields instead
        if (mAppointmentUri != null) {
            isNewAppointment = false;
            getAppointmentDetails();
            Log.v(LOG_TAG, "OLD Appointment!");
        } else {
            isNewAppointment = true;
            Log.v(LOG_TAG, "NEW Appointment!");

            // TODO: Initialise schedule title & location

            // Initialise date & time fields with default values
            String hourFormat = configurations.getString(HOUR_FORMAT_PREFERENCE, HOUR_FORMAT_12);
            binding.editStartDate.setText(TimeConverter.getCurrentDate());
            binding.editStopDate.setText(TimeConverter.getCurrentDate());
            binding.editAppointmentTime.setText(hourFormat.equals(HOUR_FORMAT_12)
                    ? TimeConverter.getCurrent12HTime()
                    : TimeConverter.getCurrent24HTime());
            binding.switchScheduleState.setChecked(true);
        }

        // TODO: Set time before first reminder according to date range
        // Set event handlers on Views
        binding.editAppointmentTime.setOnFocusChangeListener(new Dates());
        binding.editStartDate.setOnFocusChangeListener(new Dates());
        binding.editStopDate.setOnFocusChangeListener(new Dates());
    }

    @Override
    public void onBackPressed () {
        super.onBackPressed();
        saveAppointment();
    }

    @Override
    public boolean onSupportNavigateUp () {
        saveAppointment();
        return super.onSupportNavigateUp();
    }

    private void saveAppointment() {
        if (isNewAppointment) {
            processNewAppointment();
        } else {
            updateAppointment();
        }
//                isNewAppointment ? updateAppointment() : processNewAppointment();
        Toast.makeText(EditAppointmentActivity.this, "Required details missing!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Locale localeToSwitchTo = new Locale(SettingsActivity.selectedLanguage);
        ContextWrapper localeUpdatedContext = ContextUtils.updateLocale(newBase, localeToSwitchTo);
        super.attachBaseContext(localeUpdatedContext);
    }

    private boolean isMinimumMetricFilled () {
        // TODO: Define standard using appropriate Helper class -- ValidationUtil -- to check if
        //  fields are 'filled' && 'filled_correctly'
        if (mNewAppointmentTitle.equals("")) {
            Log.v(LOG_TAG, "Appointment Title not set");
            return false;
        }

        if (mNewAppointmentStartDate.equals("")) {
            Log.v(LOG_TAG, "Appointment Reminder Start Date not set!");
            return false;
        }

        if (mNewAppointmentStopDate.equals("")) {
            Log.v(LOG_TAG, "Appointment Reminder Stop Date not set!");
            return false;
        }

        if (mNewAppointmentTime.equals("")) {
            Log.v(LOG_TAG, "Appointment Reminder Time not set!");
            return false;
        }

        if (mNewAppointmentLocation.equals("")) {
            Log.v(LOG_TAG, "Appointment Location not set!");
            return false;
        }

        return true;
    }

    private boolean updateAppointment () {
        Log.v(LOG_TAG, "updateAppointment()");
        mNewAppointmentTitle = binding.editAppointmentTitle.getText().toString().trim();
        mNewAppointmentState = binding.switchScheduleState.isChecked() ? 1 : 0;
        mNewAppointmentStartDate = binding.editStartDate.getText().toString();
        mNewAppointmentStopDate = binding.editStopDate.getText().toString();
        mNewAppointmentTime =
                configurations.getString(HOUR_FORMAT_PREFERENCE, HOUR_FORMAT_12).equals(HOUR_FORMAT_12)
                        ? TimeConverter.convert12To24HTime(binding.editAppointmentTime.getText().toString().trim())
                        : binding.editAppointmentTime.getText().toString().trim();
        Log.v(LOG_TAG, "updated appointment time: " + mNewAppointmentTime);
        mNewAppointmentLocation = binding.editLocation.getText().toString().trim();
        mNewAppointmentReminderTime = binding.spinnerTimeBeforeReminder.getSelectedItem().toString();
        mNewAppointmentRepeatState = binding.checkboxRepeat.isChecked() ? 1 : 0;

        // TODO: Ascertain changes were made to the Appointment's metrics; true if at least one
        //  View got modified
        if (mNewAppointmentTitle.equals(mSavedAppointmentTitle) &&
                mNewAppointmentState == mSavedAppointmentState &&
                mNewAppointmentStartDate.equals(mSavedAppointmentStartDate) &&
                mNewAppointmentStopDate.equals(mSavedAppointmentStopDate) &&
                mNewAppointmentTime.equals(mSavedAppointmentTime) &&
                mNewAppointmentLocation.equals(mSavedAppointmentLocation) &&
                mNewAppointmentReminderTime.equals(mSavedAppointmentReminderTime) &&
                mNewAppointmentRepeatState == mSavedAppointmentRepeatState) {
            Log.v(LOG_TAG, "Found matching appointment!");
            editAppointmentNotifier.setText(getString(R.string.edit_appointment_found_match));
            editAppointmentNotifier.show();
            return false;
        }

        if (!isMinimumMetricFilled()) return false;

        ContentValues updateValues = new ContentValues();
        // Only update Appointment label if it's been modified. NPE is thrown otherwise, due to the check in AppointmentProvider.updateAppointment()
        if (!mNewAppointmentTitle.equals(mSavedAppointmentTitle))
            updateValues.put(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_TITLE, mNewAppointmentTitle);
        updateValues.put(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_STATE, mNewAppointmentState);
        updateValues.put(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_START_DATE, mNewAppointmentStartDate);
        updateValues.put(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_STOP_DATE, mNewAppointmentStopDate);
        updateValues.put(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_TIME, mNewAppointmentTime);
        updateValues.put(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_LOCATION, mNewAppointmentLocation);
        updateValues.put(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_REMINDER_TIME, mNewAppointmentReminderTime);
        updateValues.put(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_REPEAT_STATE, mNewAppointmentRepeatState);

        int numberOfAppointmentsUpdated = getContentResolver()
                .update(mAppointmentUri, updateValues, null, null);
        Log.v(LOG_TAG, "Updated appointment(s): " + numberOfAppointmentsUpdated);
        if (numberOfAppointmentsUpdated == 1) {
            editAppointmentNotifier.setText(getString(R.string.edit_appointment_successful_save));
            editAppointmentNotifier.show();
            return true;
        } else {
            editAppointmentNotifier.setText(getString(R.string.edit_appointment_unsuccessful_update));
            editAppointmentNotifier.show();
            return false;
        }
    }

    // TODO: Add comments here
    private void processNewAppointment () {
        mNewAppointmentTitle = binding.editAppointmentTitle.getText().toString().trim();
        mNewAppointmentState = binding.switchScheduleState.isChecked() ? 1 : 0;
        mNewAppointmentStartDate = binding.editStartDate.getText().toString().trim();
        mNewAppointmentStopDate = binding.editStopDate.getText().toString().trim();
        mNewAppointmentTime =
                configurations.getString(HOUR_FORMAT_PREFERENCE, HOUR_FORMAT_12).equals(HOUR_FORMAT_12)
                        ? TimeConverter.convert12To24HTime(binding.editAppointmentTime.getText().toString().trim())
                        : binding.editAppointmentTime.getText().toString().trim();
        mNewAppointmentLocation = binding.editLocation.getText().toString().trim();
        mNewAppointmentReminderTime = binding.spinnerTimeBeforeReminder.getSelectedItem().toString();
        mNewAppointmentRepeatState = binding.checkboxRepeat.isChecked() ? 1 : 0;

        if (!isMinimumMetricFilled()) return;

        // Check if an Appointment exists with same details
        // TODO: Notify and/or set varying details
        String[] matchArgs = new String[]{mNewAppointmentTitle, mNewAppointmentLocation, mNewAppointmentTime, mNewAppointmentStartDate, mNewAppointmentStopDate};

        try (Cursor matchFound = getContentResolver().query(
                AppointmentContract.AppointmentEntry.CONTENT_URI,
                null,
                AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_TITLE +
                        "=?" + " AND " +
                        AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_LOCATION +
                        "=?" + " AND (" +
                        AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_TIME +
                        "=?" + " AND " +
                        AppointmentContract.AppointmentEntry.COLUMN_REMINDER_START_DATE +
                        "=?" + " AND " +
                        AppointmentContract.AppointmentEntry.COLUMN_REMINDER_STOP_DATE +
                        "=?" + ")",
                matchArgs,
                null)) {

            if (matchFound != null && matchFound.getCount() > 0) {
                editAppointmentNotifier.setText(getString(R.string.edit_appointment_found_match));
                editAppointmentNotifier.show();
                return;
            }
        } catch (NullPointerException npe) {
            npe.getLocalizedMessage();
        }

        ContentValues values = new ContentValues();
        values.put(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_TITLE, mNewAppointmentTitle);
        values.put(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_STATE, mNewAppointmentState);
        values.put(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_START_DATE, mNewAppointmentStartDate);
        values.put(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_STOP_DATE, mNewAppointmentStopDate);
        values.put(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_TIME, mNewAppointmentTime);
        values.put(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_LOCATION, mNewAppointmentLocation);
        values.put(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_REMINDER_TIME, mNewAppointmentReminderTime);
        values.put(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_REPEAT_STATE, mNewAppointmentRepeatState);

        // Add the new Appointment to dB
        mAppointmentUri = getContentResolver().insert(AppointmentContract.AppointmentEntry.CONTENT_URI, values);
        Log.v(LOG_TAG, "Inserted Appointment URI: " + mAppointmentUri);

        if (mAppointmentUri != null) {
            editAppointmentNotifier.setText(getString(R.string.edit_appointment_successful_save));
//            editAppointmentNotifier.show();

            // Fetch the newly inserted Appointment's ID, to get it scheduled.
            try (Cursor newAppointment = getContentResolver().query(mAppointmentUri, null,
                    null, null, null)) {
                if (newAppointment != null) {
                    newAppointment.moveToFirst();
                    mAppointmentId = newAppointment.getInt(newAppointment.getColumnIndexOrThrow(AppointmentContract.AppointmentEntry._ID));
                } else
                    throw new CursorIndexOutOfBoundsException("Could not fetch appointment id!");
            } catch (CursorIndexOutOfBoundsException cursorIndexOutOfBoundsException) {
                Log.d(LOG_TAG, cursorIndexOutOfBoundsException.getLocalizedMessage());
            }

            // Schedule the Appointment
            String message = AppointmentScheduler.scheduleAppointment(
                    this,
                    mAppointmentId,
                    TimeConverter.getTimeInMillis(mNewAppointmentStartDate, mNewAppointmentTime));
            editAppointmentNotifier.setText(message);
        } else {
            editAppointmentNotifier.setText(getString(R.string.edit_appointment_unsuccessful_save));
        }
        editAppointmentNotifier.show();
        return;

    }

    /**
     * Fetch saved appointment details and set the data into relevant fields.
     */
    private void getAppointmentDetails () {
        Snackbar notFound = null;
        try (Cursor savedAppointment = getContentResolver().query(
                mAppointmentUri, null, null, null, null)) {

            if (savedAppointment != null) {
                savedAppointment.moveToFirst();

                mSavedAppointmentTitle = savedAppointment.getString(savedAppointment.getColumnIndexOrThrow(
                        AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_TITLE));
                binding.editAppointmentTitle.setText(mSavedAppointmentTitle);

                mSavedAppointmentState = savedAppointment.getInt(savedAppointment.getColumnIndexOrThrow(
                        AppointmentContract.AppointmentEntry.COLUMN_REMINDER_STATE));
                binding.switchScheduleState.setChecked(mSavedAppointmentState == 1);

                mSavedAppointmentStartDate = savedAppointment.getString(savedAppointment.getColumnIndexOrThrow(
                        AppointmentContract.AppointmentEntry.COLUMN_REMINDER_START_DATE));
                binding.editStartDate.setText(mSavedAppointmentStartDate);

                mSavedAppointmentStopDate = savedAppointment.getString(savedAppointment.getColumnIndexOrThrow(
                        AppointmentContract.AppointmentEntry.COLUMN_REMINDER_STOP_DATE));
                binding.editStopDate.setText(mSavedAppointmentStopDate);

                mSavedAppointmentTime = savedAppointment.getString(savedAppointment.getColumnIndexOrThrow(
                        AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_TIME));
                // If the user prefers 12hr format, display it; otherwise display the 24hr time
                binding.editAppointmentTime.setText(
                        configurations.getString(HOUR_FORMAT_PREFERENCE, HOUR_FORMAT_12).equals(HOUR_FORMAT_12)
                                ? TimeConverter.convert24To12HTime(mSavedAppointmentTime)
                                : mSavedAppointmentTime
                );

                mSavedAppointmentLocation = savedAppointment.getString(savedAppointment.getColumnIndexOrThrow(
                        AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_LOCATION));
                binding.editLocation.setText(mSavedAppointmentLocation);

                // TODO: Find appropriate method to display right item from the Spinner's Adapter
                mSavedAppointmentReminderTime = savedAppointment.getString(savedAppointment.getColumnIndexOrThrow(
                        AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_REMINDER_TIME));
                binding.spinnerTimeBeforeReminder.setSelection(
                        ((ArrayAdapter<String>) binding.spinnerTimeBeforeReminder.getAdapter()).
                                getPosition(mSavedAppointmentReminderTime));

                mSavedAppointmentRepeatState = savedAppointment.getInt(savedAppointment.getColumnIndexOrThrow(
                        AppointmentContract.AppointmentEntry.COLUMN_REMINDER_REPEAT_STATE));

                mSavedAppointmentRepeatState = savedAppointment.getInt(savedAppointment.getColumnIndexOrThrow(
                        AppointmentContract.AppointmentEntry.COLUMN_REMINDER_REPEAT_STATE));
                binding.checkboxRepeat.setChecked(mSavedAppointmentRepeatState == 1);

            } else
                throw new CursorIndexOutOfBoundsException("Could not find appointment!");
        } catch (CursorIndexOutOfBoundsException cursorException) {
            // Show a Snackbar only if one is not currently being shown.
            if (notFound == null) {
                notFound = Snackbar.make(findViewById(R.id.editor_snackbar_frame),
                        cursorException.getMessage() != null ? cursorException.getMessage() : "",
                        Snackbar.LENGTH_LONG);
                notFound.show();
            }

            // TODO: Set a timer to close [#finish()] this activity after complete dB transaction
            //  & scheduling of Appointment
        }
    }

    private class Dates implements DatePickerDialog.OnDateSetListener,
            TimePickerDialog.OnTimeSetListener, View.OnFocusChangeListener {

        protected Dates () {}

        /**
         * Store the date selected by user in format dd-mm-yyyy.
         *
         * @param datePicker the {@link DatePicker} edited by user
         * @param year       set by the user
         * @param month      set by the user
         * @param day        set by the user
         */
        @Override
        public void onDateSet (DatePicker datePicker, int year, int month, int day) {
            mActiveDateSelector.setText(TimeConverter.getDate(year, month, day));
        }

        /**
         * Display a 12/24-hour time based on the user's preference.
         *
         * @param timePicker the time picker edited by the user
         * @param hrs        set by the user
         * @param mins       set by the user
         */
        @Override
        public void onTimeSet (TimePicker timePicker, int hrs, int mins) {
            String hourFormat = configurations.getString(HOUR_FORMAT_PREFERENCE, HOUR_FORMAT_12);
            mActiveTimeSelector.setText(
                    hourFormat.equals(HOUR_FORMAT_12)
                            ? TimeConverter.get12HTime(hrs, mins)
                            : TimeConverter.get24HTime(hrs, mins)
            );
            mNewAppointmentTime = TimeConverter.get24HTime(hrs, mins);
        }

        @Override
        public void onFocusChange (View v, boolean hasFocus) {
            if (hasFocus) {
                switch (v.getId()) {
                    case R.id.edit_start_date:
                    case R.id.edit_stop_date:
                        mActiveDateSelector = (TextInputEditText) v;
                        mDatePickerDialogFrag = new DatePickerDialog(EditAppointmentActivity.this, this, mYear, mMonth, mDay);
                        mDatePickerDialogFrag.show();
//                        Log.v(LOG_TAG, "Stop Date -- " + v.getId());
                        break;
                    case R.id.edit_appointment_time:
                        mActiveTimeSelector = (TextInputEditText) v;
                        mTimePickerDialogFrag = new TimePickerDialog(EditAppointmentActivity.this, this, mHours, mMinutes, true);
                        mTimePickerDialogFrag.show();
//                        Log.v(LOG_TAG, "Start Time -- " + v.getId());
                        break;
                    default:
                        Log.v(LOG_TAG, String.valueOf(v.getId()));
                }
            }
        }
    }

}