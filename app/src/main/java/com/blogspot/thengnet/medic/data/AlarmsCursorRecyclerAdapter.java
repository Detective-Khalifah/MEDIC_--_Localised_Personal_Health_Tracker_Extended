package com.blogspot.thengnet.medic.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blogspot.thengnet.medic.databinding.AlarmBinding;
import com.blogspot.thengnet.medic.utilities.TimeConverter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * {@link RecyclerView.Adapter} that can display an {@link Alarm}.
 */
public class AlarmsCursorRecyclerAdapter extends BaseCursorRecyclerAdapter<AlarmsCursorRecyclerAdapter.ViewHolder> {

    private OnAlarmClickListener onAlarmClickListener;

    public interface OnAlarmClickListener {
        void onAlarmClick (int position, long id);
    }

    public AlarmsCursorRecyclerAdapter (Context context, Cursor cursor) {
        super(context, cursor);
    }

    public void setOnAlarmClickListener (OnAlarmClickListener alarmClickListener) {
        onAlarmClickListener = alarmClickListener;
    }

    @NonNull
    @Override
    public AlarmsCursorRecyclerAdapter.ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                AlarmBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false),
                onAlarmClickListener
        );
    }

    @Override
    public void onBindViewHolder (AlarmsCursorRecyclerAdapter.ViewHolder viewHolder, Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(AlarmContract.AlarmEntry._ID));
        String alarmTitle = cursor.getString(cursor.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ALARM_TITLE));
        int alarmState = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ALARM_STATE));
        String administrationForm = cursor.getString(cursor.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ADMINISTRATION_FORM));
        String alarmStartDate = cursor.getString(cursor.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ALARM_START_DATE));
        String alarmStopDate = cursor.getString(cursor.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ALARM_STOP_DATE));
        String alarmTime = cursor.getString(cursor.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ALARM_TIME));
        String administrationPerDay = cursor.getString(cursor.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ADMINISTRATION_PER_DAY));
        String alarmTone = cursor.getString(cursor.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ALARM_TONE));
        int alarmVibrateState = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ALARM_VIBRATE_OR_NOT));
        int alarmRepeatState = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmContract.AlarmEntry.COLUMN_ALARM_REPEAT_STATE));

        // Create an instance of the Alarm from fetched dB record
        viewHolder.theAlarm = new Alarm(
                Integer.parseInt(id),
                alarmState == 1,
                alarmTitle,
                TimeConverter.parseDate(alarmStartDate),
                TimeConverter.parseDate(alarmStopDate),
                TimeConverter.parseTime(alarmTime),
                alarmRepeatState == 1,
                administrationPerDay,
                alarmVibrateState == 1,
                alarmTone,
                administrationForm
        );

        // Bind the Alarm to relevant views
        viewHolder.alarmBinding.setAlarm(viewHolder.theAlarm);
        viewHolder.alarmBinding.executePendingBindings();
    }

    @Override
    public Cursor getItem (int position) {
        return super.getItem(position);
    }

    @Override
    public Cursor getCursor () {
        return super.getCursor();
    }

    @Override
    public int getItemCount () {
        return super.getItemCount();
    }

    @Override
    public void changeCursor (Cursor cursor) {
        super.changeCursor(cursor);
    }

    @Override
    public Cursor swapCursor (Cursor newCursor) {
        return super.swapCursor(newCursor);
    }

    @Override
    public long getItemId (int position) {
        return super.getItemId(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public Alarm theAlarm;
        public AlarmBinding alarmBinding;

        public ViewHolder (AlarmBinding binding, final OnAlarmClickListener alarmClickListener) {
            super(binding.getRoot());
            alarmBinding = binding;
            alarmBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View view) {
                    if (alarmClickListener != null) {
                        int position = getBindingAdapterPosition();
                        long id = theAlarm.getId();
//                        long id = getItem(position).getInt(getCursor().getColumnIndexOrThrow(AlarmContract.AlarmEntry._ID));
                        if (position != RecyclerView.NO_POSITION)
                            alarmClickListener.onAlarmClick(position, id);
                    }
                }
            });
        }

        @Override
        public String toString () {
            return super.toString() + " '" + alarmBinding.textviewTitle.getText() + "'";
        }

    }
}