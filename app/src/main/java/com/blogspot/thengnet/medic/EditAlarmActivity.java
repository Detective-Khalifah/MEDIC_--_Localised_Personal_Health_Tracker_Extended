package com.blogspot.thengnet.medic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.blogspot.thengnet.medic.databinding.ActivityEditAlarmBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class EditAlarmActivity extends AppCompatActivity {

    private ActivityEditAlarmBinding binding;

    private static TimePickerDialog mTimePickerDialogFrag;
    private static DatePickerDialog mDatePickerDialogFrag;
    private TextInputEditText mActiveDateSelector, mActiveTimeSelector;

    private Alarm currentAlarm;

    private /*final*/ int mYear, mMonth, mDay, mHours, mMinutes;
    private final static String LOG_TAG = EditAlarmActivity.class.getName();

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Calendar now = Calendar.getInstance(Locale.getDefault());
        mYear = now.get(Calendar.YEAR);
        mMonth = now.get(Calendar.MONTH);
        mDay = now.get(Calendar.DAY_OF_MONTH);
        mHours = now.get(Calendar.HOUR_OF_DAY);
        mMinutes = now.get(Calendar.MINUTE);

//        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_edit_alarm, parent, true);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_alarm);
//        binding = ActivityEditAlarmBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

        binding.editStartTime.setOnFocusChangeListener(new Dates());
        binding.editStartDate.setOnFocusChangeListener(new Dates());
        binding.editStopDate.setOnFocusChangeListener(new Dates());
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.edit_alarm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                Toast.makeText(this, "...", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private class Dates implements DatePickerDialog.OnDateSetListener,
            TimePickerDialog.OnTimeSetListener, View.OnFocusChangeListener {

        TextInputEditText clickedPicker;
        String pickerType;

        protected Dates () {}

        @Override
        public void onDateSet (DatePicker datePicker, int year, int month, int day) {
            mActiveDateSelector.setText(year + "-" + month + "-" + day);
        }

        @Override
        public void onTimeSet (TimePicker timePicker, int hrs, int mins) {
            mActiveTimeSelector.setText(hrs + ":" + mins);
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