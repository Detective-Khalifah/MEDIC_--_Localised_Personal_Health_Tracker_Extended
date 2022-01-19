package com.blogspot.thengnet.medic;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.blogspot.thengnet.medic.databinding.AlarmBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import static com.blogspot.thengnet.medic.AlarmsActivity.alarms;

public class AlarmsAdapter extends ArrayAdapter<Alarm> {

    private static TimePickerDialog mTimePickerDialogFrag;
    private static DatePickerDialog mDatePickerDialogFrag;
    private final Context mAppContext;
    private final ArrayList<Alarm> mAlarms;
    private final int mYear, mMonth, mDay, mHours, mMinutes;
    protected AlarmBinding binding;
    private LayoutInflater mInflater;
    private TextInputEditText mActiveDateSelector, mActiveTimeSelector;
    private Alarm currentAlarm;

    public AlarmsAdapter (@NonNull Context context, int resource, @NonNull ArrayList<Alarm> theAlarms) {
        super(context, resource, theAlarms);
        mAppContext = context;
        mAlarms = theAlarms;

        Calendar now = Calendar.getInstance(Locale.getDefault());
        mYear = now.get(Calendar.YEAR);
        mMonth = now.get(Calendar.MONTH);
        mDay = now.get(Calendar.DAY_OF_MONTH);
        mHours = now.get(Calendar.HOUR_OF_DAY);
        mMinutes = now.get(Calendar.MINUTE);
    }

    @NonNull
    @Override
    public View getView (int position, @NonNull View convertView, @NonNull ViewGroup parent) {

        if (mInflater == null)
            mInflater = ((Activity) mAppContext).getLayoutInflater();

        binding = AlarmBinding.inflate(mInflater);
//                DataBindingUtil.getBinding(convertView);

        if (binding == null)
            binding = DataBindingUtil.inflate(mInflater, R.layout.alarm, parent, true);

        currentAlarm = getItem(position);
        binding.setAlarm(currentAlarm);
        binding.executePendingBindings();

        binding.editStartTime.setOnClickListener(new Dates(binding.editStartTime, "time"));

//        binding.getRoot().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick (View view) {
//                /**
//                 * Dummy data to send to {@link EditAlarmActivity}
//                 * TODO: set up a database to contain the data, and use id to fetch data from it
//                 * instead of passing the data from this {@link AlarmsActivity}
//                 */
////                Bundle mSelectedAlarmParams = null;
////                ArrayList<String> mSelectedAlarmParamsList = null;
////
////                mSelectedAlarmParamsList.addAll(Collections.singleton(alarms.toString()));
////
////                Alarm selectedAlarm = alarms.get(position);
////                mSelectedAlarmParams.putStringArray("selected-alarm", new String[]{
////                                selectedAlarm.getTitle(), selectedAlarm.getDescription(),
////                                selectedAlarm.getStartDate(), selectedAlarm.getStartTime(),
////                                selectedAlarm.getEndDate(), selectedAlarm.getEndTime()
////                        }
////                );
//                Log.v(this.getClass().getName(), "List item " + position + " clicked.");
//                Toast.makeText(mAppContext, "List item " + position + " clicked.", Toast.LENGTH_SHORT).show();
////                mAppContext.startActivity(new Intent(mAppContext, EditAlarmActivity.class).putExtras(mSelectedAlarmParams));
//            }
//        });
        return binding.getRoot();
    }

    protected class Dates implements DatePickerDialog.OnDateSetListener,
            TimePickerDialog.OnTimeSetListener, View.OnClickListener {

        final TextInputEditText clickedPicker;
        final String pickerType;

        protected Dates (TextInputEditText thePicker, String pickerType) {
            this.clickedPicker = thePicker;
            this.pickerType = pickerType;
        }

        @Override
        public void onClick (View view) {
            switch (pickerType) {
                case "date":
                    mActiveDateSelector = clickedPicker;
                    mDatePickerDialogFrag = new DatePickerDialog(mAppContext, this, mYear, mMonth, mDay);
                    mDatePickerDialogFrag.show();
                    break;
                case "time":
                    mActiveTimeSelector = clickedPicker;
                    mTimePickerDialogFrag = new TimePickerDialog(mAppContext, this, mHours, mMinutes, true);
                    mTimePickerDialogFrag.show();
                    break;
                default:
            }
        }

        @Override
        public void onDateSet (DatePicker datePicker, int year, int month, int day) {
            mActiveDateSelector.setText(year + "-" + month + "-" + day);
        }

        @Override
        public void onTimeSet (TimePicker timePicker, int hrs, int mins) {
            mActiveTimeSelector.setText(hrs + ":" + mins);
        }
    }
}
