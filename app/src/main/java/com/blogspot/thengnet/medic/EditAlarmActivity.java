package com.blogspot.thengnet.medic;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.blogspot.thengnet.medic.data.Alarm;
import com.blogspot.thengnet.medic.data.AlarmContract;
import com.blogspot.thengnet.medic.databinding.ActivityEditAlarmBinding;
import com.blogspot.thengnet.medic.utilities.AlarmScheduler;
import com.blogspot.thengnet.medic.utilities.TimeConverter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

public class EditAlarmActivity extends AppCompatActivity {

    private final static String LOG_TAG = EditAlarmActivity.class.getName();

    private static TimePickerDialog mTimePickerDialogFrag;
    private static DatePickerDialog mDatePickerDialogFrag;
    private static Uri mAlarmUri, mNewAlarmToneUri, mSavedAlarmToneUri;
    /**
     * {@link Preference} keys.
     */
    private static String HOUR_FORMAT_PREFERENCE, HOUR_FORMAT_12, HOUR_FORMAT_24;
    /**
     * Data of new {@link Alarm}.
     */
    private static String mNewAlarmTitle, mSavedAlarmAdministrationPerDay, mNewAlarmStartDate,
            mNewAlarmStopDate, mNewAlarmStartTime;
    /**
     * Data of existing {@link Alarm}.
     */
    private static String mSavedAlarmTitle, mNewAlarmAdministrationPerDay, mSavedAlarmStartDate,
            mSavedAlarmStopDate, mSavedAlarmStartTime;
    private static long mAlarmId;
    private TextInputEditText mActiveDateSelector, mActiveTimeSelector;
    private Snackbar editAlarmNotifier;
    private SharedPreferences configurations;
    private ActivityEditAlarmBinding binding;
    private Alarm currentAlarm;
    private /*final*/ int mYear, mMonth, mDay, mHours, mMinutes;
    private int mNewAlarmState, mNewAlarmRepeatState, mNewAlarmVibrateState,
            mSavedAlarmState, mSavedAlarmRepeatState, mSavedAlarmVibrateState;
    private boolean isNewAlarm;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_edit_alarm, parent, true);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_alarm);
//        binding = ActivityEditAlarmBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

        // get an instance of the system time
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
        editAlarmNotifier = Snackbar.make(binding.editorSnackbarFrame, "",
                Snackbar.LENGTH_SHORT);

        Intent existingAlarm = getIntent();
        mAlarmUri = existingAlarm.getData();

        // Fetch the saved alarm's details if its Uri was passed; do not fetch details of an alarm
        // that does not exist yet, otherwise -- initialise fields instead
        if (mAlarmUri != null) {
            isNewAlarm = false;
            mAlarmId = existingAlarm.getExtras().getLong("alarm-id");
            getAlarmDetails();
            Log.v(LOG_TAG, "OLD Alarm " + mAlarmId + "!");
        } else {
            isNewAlarm = true;
            Log.v(LOG_TAG, "NEW Alarm!");

            // TODO: Initialise schedule state, dates, time
            String hourFormat = configurations.getString(HOUR_FORMAT_PREFERENCE, HOUR_FORMAT_12);
            binding.editStartDate.setText(TimeConverter.getCurrentDate());
            binding.editStopDate.setText(TimeConverter.getCurrentDate());
            binding.editStartTime.setText(hourFormat.equals(HOUR_FORMAT_12)
                    ? TimeConverter.getCurrent12HTime()
                    : TimeConverter.getCurrent24HTime());
            binding.switchScheduleState.setChecked(true);
        }

        // set event handlers on Views
        binding.editStartTime.setOnFocusChangeListener(new Dates());
        binding.editStartDate.setOnFocusChangeListener(new Dates());
        binding.editStopDate.setOnFocusChangeListener(new Dates());
        binding.textviewAlarmTonePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                /** Source https://www.physicsforums.com/insights/create-an-android-ringtone-picker-using-the-ringtonemanager-class/ */
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.select_alarm_tone));
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, mSavedAlarmToneUri);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                startActivityForResult(intent, /* RINGTONE_REQUEST_CODE*/ 77);
            }
        });

    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO: Refer EditAlarm 1a - 1c
        if (requestCode == 77) {
            if (resultCode == RESULT_OK && data != null) {
                Uri toneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                Log.v(LOG_TAG, "(PICKED) Alarm Tone URI:: " +
                        data.getExtras().get(RingtoneManager.EXTRA_RINGTONE_PICKED_URI));
                mNewAlarmToneUri = toneUri;
                binding.textviewAlarmTonePicker.setText(
                        getString(R.string.alarm_tone_picker_label, toneUri.toString()));
            }
        }
    }

    // TODO: Replace with onPause override; onSupportNavigateUp & onNavigateUp only work with the
    //  {@link ActionBar}
    @Override
    public boolean onSupportNavigateUp () {
        Log.v(LOG_TAG, "Nav up");
        if (isNewAlarm) {
            processNewAlarm();
        } else {
            updateAlarm();
        }
//                isNewAlarm ? updateAlarm() : processNewAlarm();
        Toast.makeText(this, "...", Toast.LENGTH_SHORT).show();
        return super.onSupportNavigateUp();
    }

    private boolean isMinimumMetricFilled () {
        // TODO: Define standard using appropriate Helper class -- ValidationUtil -- to check if
        //  fields are 'filled' && 'filled_correctly'
        if (mNewAlarmTitle.equals("")) {
            Log.v(LOG_TAG, "Alarm Title not set");
            return false;
        }

        if (mNewAlarmStartDate.equals("")) {
            Log.v(LOG_TAG, "Alarm Reminder Start Date not set!");
            return false;
        }

        if (mNewAlarmStopDate.equals("")) {
            Log.v(LOG_TAG, "Alarm Reminder Stop Date not set!");
            return false;
        }

        if (mNewAlarmStartTime.equals("")) {
            Log.v(LOG_TAG, "Alarm Reminder Time not set!");
            return false;
        }

        if (mNewAlarmToneUri.equals("")) {
            Log.v(LOG_TAG, "Alarm Tone not set!");
            return false;
        }

        // should be return false;
        return true;
    }

    private boolean updateAlarm () {
        Log.v(LOG_TAG, "updateAlarm()");
        mNewAlarmTitle = binding.editScheduleTitle.getText().toString().trim();
        mNewAlarmState = binding.switchScheduleState.isChecked() ? 1 : 0;
        mNewAlarmStartDate = binding.editStartDate.getText().toString();
        mNewAlarmStopDate = binding.editStopDate.getText().toString();
        mNewAlarmStartTime =
                configurations.getString(HOUR_FORMAT_PREFERENCE, HOUR_FORMAT_12).equals(HOUR_FORMAT_12)
                        ? TimeConverter.convert12To24HTime(binding.editStartTime.getText().toString().trim())
                        : binding.editStartTime.getText().toString().trim();
        mNewAlarmAdministrationPerDay = binding.spinnerAdministrationPerDay.getSelectedItem().toString();
        mNewAlarmToneUri = Uri.parse(binding.textviewAlarmTonePicker.getText().toString());
        mNewAlarmVibrateState = binding.checkboxVibrateState.isChecked() ? 1 : 0;
        mNewAlarmRepeatState = binding.checkboxRepeat.isChecked() ? 1 : 0;

        // TODO: Ascertain changes were made to the Alarm's metrics; true if at least one
        //  View got modified
        if (mNewAlarmTitle.equals(mSavedAlarmTitle) &&
                mNewAlarmState == mSavedAlarmState &&
                mNewAlarmStartDate.equals(mSavedAlarmStartDate) &&
                mNewAlarmStopDate.equals(mSavedAlarmStopDate) &&
                mNewAlarmStartTime.equals(mSavedAlarmStartTime) &&
                mNewAlarmAdministrationPerDay.equals(mSavedAlarmAdministrationPerDay) &&
                mNewAlarmToneUri.equals(mSavedAlarmToneUri) &&
                mNewAlarmVibrateState == mSavedAlarmVibrateState &&
                mNewAlarmRepeatState == mSavedAlarmRepeatState) {
            Log.v(LOG_TAG, "Found matching alarm!");
            editAlarmNotifier.setText(getString(R.string.edit_alarm_found_match));
            editAlarmNotifier.show();
            return false;
        }

        if (!isMinimumMetricFilled()) return false;

        ContentValues updateValues = new ContentValues();
        // Only update Alarm label if it's been modified. NPE is thrown otherwise, due to the check in AlarmProvider.updateAlarm()
        if (!mNewAlarmTitle.equals(mSavedAlarmTitle))
            updateValues.put(AlarmContract.AlarmEntry.COLUMN_ALARM_TITLE, mNewAlarmTitle);
        updateValues.put(AlarmContract.AlarmEntry.COLUMN_ALARM_STATE, mNewAlarmState);
        updateValues.put(AlarmContract.AlarmEntry.COLUMN_ALARM_START_DATE, mNewAlarmStartDate);
        updateValues.put(AlarmContract.AlarmEntry.COLUMN_ALARM_STOP_DATE, mNewAlarmStopDate);
        updateValues.put(AlarmContract.AlarmEntry.COLUMN_ALARM_TIME, mNewAlarmStartTime);
        updateValues.put(AlarmContract.AlarmEntry.COLUMN_ADMINISTRATION_PER_DAY, mNewAlarmAdministrationPerDay);
        updateValues.put(AlarmContract.AlarmEntry.COLUMN_ALARM_TONE, mNewAlarmToneUri.toString());
        updateValues.put(AlarmContract.AlarmEntry.COLUMN_ALARM_VIBRATE_OR_NOT, mNewAlarmVibrateState);
        updateValues.put(AlarmContract.AlarmEntry.COLUMN_ALARM_REPEAT_STATE, mNewAlarmRepeatState);

        int numberOfAlarmsUpdated = getContentResolver()
                .update(mAlarmUri, updateValues, null, null);
        Log.v(LOG_TAG, "Updated alarm(s): " + numberOfAlarmsUpdated);

        // TODO: Cancel old Alarm metrics-wise; schedule new Alarm metrics-wise
        // Check if there were modifications in the time metrics of the Alarm
        // -- un-schedule old Alarm if there were modifications, and (old) Alarm was enabled.
        // -- schedule new Alarm if there were modifications, and (new) Alarm is enabled.
        // If Alarm got disabled WITHOUT any modifications made, un-scheduled the old Alarm.
            if (mNewAlarmStartTime.equals(mSavedAlarmStartTime) &&
                    mNewAlarmStartDate.equals(mSavedAlarmStartDate) &&
                    mNewAlarmStopDate.equals(mSavedAlarmStopDate)) {
                Log.v(LOG_TAG, "Alarm time metrics modified!");

                // Cancel previously set Alarm, only if the old Alarm's time metric(s) got updated.
                if (mSavedAlarmState == 1) AlarmScheduler.stopAlarm(this, mAlarmId);
                // (re-)Schedule a new Alarm using the updated metric(s), only if the Alarm got enabled (at update).
                if (mNewAlarmState == 1) AlarmScheduler.setupAlarm(this, mAlarmId, mAlarmUri);
            } else {
                // Un-schedule disabled, OLD Alarm; field "mNewAlarmState" suggests a NEW Alarm but
                // the int value is only used to hold metrics of an Alarm being edited, which may or
                // may not be an update to the OLD Alarm -- have same (time) metrics as the OLD Alarm.
                if (mNewAlarmState == 0) AlarmScheduler.stopAlarm(this, mAlarmId);
            }

        if (numberOfAlarmsUpdated == 1) {
            editAlarmNotifier.setText(getString(R.string.edit_alarm_successful_save));
            editAlarmNotifier.show();
            finish();
            return true;
        }

        editAlarmNotifier.setText(getString(R.string.edit_alarm_unsuccessful_update));
        editAlarmNotifier.show();
        return false;
    }

    private void processNewAlarm () {
        mNewAlarmTitle = binding.editScheduleTitle.getText().toString().trim();
        mNewAlarmState = binding.switchScheduleState.isChecked() ? 1 : 0;
        mNewAlarmStartDate = binding.editStartDate.getText().toString().trim();
        mNewAlarmStopDate = binding.editStopDate.getText().toString().trim();
        mNewAlarmStartTime =
                configurations.getString(HOUR_FORMAT_PREFERENCE, HOUR_FORMAT_12).equals(HOUR_FORMAT_12)
                        ? TimeConverter.convert12To24HTime(binding.editStartTime.getText().toString().trim())
                        : binding.editStartTime.getText().toString().trim();
        mNewAlarmAdministrationPerDay = binding.spinnerAdministrationPerDay.getSelectedItem().toString();
        mNewAlarmToneUri = Uri.parse(binding.textviewAlarmTonePicker.getText().toString());
        mNewAlarmVibrateState = binding.checkboxVibrateState.isChecked() ? 1 : 0;
        mNewAlarmRepeatState = binding.checkboxRepeat.isChecked() ? 1 : 0;

        // Check if an Alarm exists with same details
        // TODO: Notify and/or set varying details
        String[] matchArgs = new String[]{mNewAlarmTitle, mNewAlarmStartTime, mNewAlarmStartDate, mNewAlarmStopDate};

        try (Cursor matchFound = getContentResolver().query(
                AlarmContract.AlarmEntry.CONTENT_URI,
                null,
                AlarmContract.AlarmEntry.COLUMN_ALARM_TITLE + "=?" + " AND (" +
                        AlarmContract.AlarmEntry.COLUMN_ALARM_TIME + "=?" + " AND " +
                        AlarmContract.AlarmEntry.COLUMN_ALARM_START_DATE + "=?" + " AND " +
                        AlarmContract.AlarmEntry.COLUMN_ALARM_STOP_DATE + "=?" + ")",
                matchArgs,
                null)) {

            if (matchFound != null && matchFound.getCount() > 0) {
                editAlarmNotifier.setText(getString(R.string.edit_alarm_found_match));
                editAlarmNotifier.show();
                return;
            }
        } catch (NullPointerException npe) {
            npe.getLocalizedMessage();
        }

        ContentValues values = new ContentValues();
        values.put(AlarmContract.AlarmEntry.COLUMN_ALARM_TITLE, mNewAlarmTitle);
        values.put(AlarmContract.AlarmEntry.COLUMN_ALARM_STATE, mNewAlarmState);
        values.put(AlarmContract.AlarmEntry.COLUMN_ALARM_START_DATE, mNewAlarmStartDate);
        values.put(AlarmContract.AlarmEntry.COLUMN_ALARM_STOP_DATE, mNewAlarmStopDate);
        values.put(AlarmContract.AlarmEntry.COLUMN_ALARM_TIME, mNewAlarmStartTime);
        values.put(AlarmContract.AlarmEntry.COLUMN_ADMINISTRATION_PER_DAY, mNewAlarmAdministrationPerDay);
        values.put(AlarmContract.AlarmEntry.COLUMN_ALARM_TONE, String.valueOf(mNewAlarmToneUri));
        values.put(AlarmContract.AlarmEntry.COLUMN_ALARM_VIBRATE_OR_NOT, mNewAlarmVibrateState);
        values.put(AlarmContract.AlarmEntry.COLUMN_ALARM_REPEAT_STATE, mNewAlarmRepeatState);

        if (!isMinimumMetricFilled()) return;

        // Add the new Alarm to dB
        mAlarmUri = getContentResolver().insert(AlarmContract.AlarmEntry.CONTENT_URI, values);
        Log.v(LOG_TAG, "Inserted Alarm URI: " + mAlarmUri);

        if (mAlarmUri != null) {
            editAlarmNotifier.setText(getString(R.string.edit_alarm_successful_save));

            try (Cursor newAlarm = getContentResolver().query(mAlarmUri, new String[]{AlarmContract.AlarmEntry._ID}, null,
                    null, null)) {
                if (newAlarm != null) {
                    newAlarm.moveToFirst();
                    mAlarmId = newAlarm.getInt(newAlarm.getColumnIndexOrThrow(AlarmContract.AlarmEntry._ID));
                } else
                    throw new CursorIndexOutOfBoundsException("Could not fetch alarm id!");
            } catch (CursorIndexOutOfBoundsException cursorIndexOutOfBoundsException) {
                Log.d(LOG_TAG, cursorIndexOutOfBoundsException.getLocalizedMessage());
            }

            // Schedule the Alarm
            // TODO: Schedule in bg; display message meanwhile.
            String message = AlarmScheduler.setupAlarm(
                    this,
                    mAlarmId,
                    mAlarmUri);
            editAlarmNotifier.setText(message);
            editAlarmNotifier.show();
            finish();
        }

        editAlarmNotifier.setText(getString(R.string.edit_alarm_unsuccessful_save));
        editAlarmNotifier.show();

    }

    /**
     * Fetch saved alarm details and set the data into relevant fields.
     */
    private void getAlarmDetails () {
        Snackbar notFound = null;
        try (Cursor savedAlarm = getContentResolver().query(
                mAlarmUri, null, null, null, null)) {

            if (savedAlarm != null) {
                savedAlarm.moveToFirst();

                mSavedAlarmTitle = savedAlarm.getString(savedAlarm.getColumnIndexOrThrow(
                        AlarmContract.AlarmEntry.COLUMN_ALARM_TITLE));
                binding.editScheduleTitle.setText(mSavedAlarmTitle);

                mSavedAlarmState = savedAlarm.getInt(savedAlarm.getColumnIndexOrThrow(
                        AlarmContract.AlarmEntry.COLUMN_ALARM_STATE));
                binding.switchScheduleState.setChecked(mSavedAlarmState == 1);

                mSavedAlarmStartDate = savedAlarm.getString(savedAlarm.getColumnIndexOrThrow(
                        AlarmContract.AlarmEntry.COLUMN_ALARM_START_DATE));
                binding.editStartDate.setText(mSavedAlarmStartDate);

                mSavedAlarmStopDate = savedAlarm.getString(savedAlarm.getColumnIndexOrThrow(
                        AlarmContract.AlarmEntry.COLUMN_ALARM_STOP_DATE));
                binding.editStopDate.setText(mSavedAlarmStopDate);

                mSavedAlarmStartTime = savedAlarm.getString(savedAlarm.getColumnIndexOrThrow(
                        AlarmContract.AlarmEntry.COLUMN_ALARM_TIME));
                // If the user prefers 12hr format, display it; otherwise display the 24hr time
                binding.editStartTime.setText(
                        configurations.getString(HOUR_FORMAT_PREFERENCE, HOUR_FORMAT_12).equals(HOUR_FORMAT_12)
                                ? TimeConverter.convert24To12HTime(mSavedAlarmStartTime)
                                : mSavedAlarmStartTime
                );

                // TODO: Find appropriate method to display selected item from the Spinner's Adapter
                mSavedAlarmAdministrationPerDay = savedAlarm.getString(savedAlarm.getColumnIndexOrThrow(
                        AlarmContract.AlarmEntry.COLUMN_ADMINISTRATION_PER_DAY));
                binding.spinnerAdministrationPerDay.setSelection(
                        ((ArrayAdapter<String>) binding.spinnerAdministrationPerDay.getAdapter()).
                                getPosition(mSavedAlarmAdministrationPerDay));

                mSavedAlarmToneUri = Uri.parse(savedAlarm.getString(savedAlarm.getColumnIndexOrThrow(
                        AlarmContract.AlarmEntry.COLUMN_ALARM_TONE)));
                binding.textviewAlarmTonePicker.setText(mSavedAlarmToneUri.toString());

                mSavedAlarmVibrateState = savedAlarm.getInt(savedAlarm.getColumnIndexOrThrow(
                        AlarmContract.AlarmEntry.COLUMN_ALARM_VIBRATE_OR_NOT));
                binding.checkboxVibrateState.setChecked(mSavedAlarmVibrateState == 1);

                mSavedAlarmRepeatState = savedAlarm.getInt(savedAlarm.getColumnIndexOrThrow(
                        AlarmContract.AlarmEntry.COLUMN_ALARM_REPEAT_STATE));
                binding.checkboxRepeat.setChecked(mSavedAlarmRepeatState == 1);

            } else
                throw new CursorIndexOutOfBoundsException("Could not find alarm!");
        } catch (CursorIndexOutOfBoundsException cursorException) {
            // TODO: Show a {@link Snackbar} only if one is not currently being shown.
            if (notFound == null) {
                notFound = Snackbar.make(findViewById(R.id.editor_snackbar_frame),
                        cursorException.getMessage() != null ? cursorException.getMessage() : "",
                        Snackbar.LENGTH_LONG);
                notFound.show();
            }

            // TODO: Set a timer to close [#finish()] this activity after complete dB transaction
            //  & scheduling of Alarm
        }
    }

    private class Dates implements DatePickerDialog.OnDateSetListener,
            TimePickerDialog.OnTimeSetListener, View.OnFocusChangeListener {

        protected Dates () {
        }

        /**
         * Store the date selected by user in format dd-mm-yyyy
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
            mNewAlarmStartTime = TimeConverter.get24HTime(hrs, mins);
        }

        @Override
        public void onFocusChange (View v, boolean hasFocus) {
            if (hasFocus) {
                switch (v.getId()) {
                    case R.id.edit_start_date:
//                        mActiveDateSelector = (TextInputEditText) v;
//                        Log.v(LOG_TAG, "Start Date -- " + v.getId());
//                        break;
                    case R.id.edit_stop_date:
                        mActiveDateSelector = (TextInputEditText) v;
                        mDatePickerDialogFrag = new DatePickerDialog(EditAlarmActivity.this, this, mYear, mMonth, mDay);
                        mDatePickerDialogFrag.show();
//                        Log.v(LOG_TAG, "Stop Date -- " + v.getId());
                        break;
                    case R.id.edit_start_time:
                        mActiveTimeSelector = (TextInputEditText) v;
                        mTimePickerDialogFrag = new TimePickerDialog(EditAlarmActivity.this, this, mHours, mMinutes, true);
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