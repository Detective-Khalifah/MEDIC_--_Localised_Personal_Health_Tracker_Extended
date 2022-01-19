package com.blogspot.thengnet.medic;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.thengnet.medic.databinding.AlarmBinding;
import com.blogspot.thengnet.medic.placeholder.PlaceholderContent.PlaceholderItem;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyAlarmRecyclerViewAdapter extends RecyclerView.Adapter<MyAlarmRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<Alarm> mAlarms;
    private OnAlarmClickListener onAlarmClickListener;

    public interface OnAlarmClickListener {
        void onAlarmClick(int position);
    }

    public void setOnAlarmClickListener(OnAlarmClickListener alarmClickListener) {
        onAlarmClickListener = alarmClickListener;
    }

    public MyAlarmRecyclerViewAdapter (ArrayList<Alarm> alarms) {
        mAlarms = alarms;
    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {

        return new ViewHolder(AlarmBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), onAlarmClickListener);

    }

    @Override
    public void onBindViewHolder (final ViewHolder holder, int position) {
//        holder.mItem = mValues.get(position);
        holder.alarm = mAlarms.get(position);

//        holder.mIdView.setText(mValues.get(position).id);
//        holder.mContentView.setText(mValues.get(position).content);
//        holder.imageMedication.setImage...(mAlarms.get(position).get);

//        holder.tvScheduleTitle.setText(mAlarms.get(position).getTitle());
//        holder.tvDescription.setText(mAlarms.get(position).getDescription());
//        holder.textInputEditTextStartTime.setText(mAlarms.get(position).getStartTime());
//        holder.switchSchedule.setChecked(mAlarms.get(position).getAlarmStatus());

        Alarm currentAlarm = mAlarms.get(position);
        holder.alarmBinding.setAlarm(currentAlarm);
        holder.alarmBinding.executePendingBindings();

//        currentAlarm = getItem(position);
//        binding.setAlarm(currentAlarm);
//        binding.executePendingBindings();

    }

    @Override
    public int getItemCount () {
        return mAlarms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        public final TextView mIdView;
//        public final TextView mContentView;
//        public PlaceholderItem mItem;

        public ImageView imageMedication;
        public TextView tvScheduleTitle;
        public TextView tvDescription;
        public TextInputEditText textInputEditTextStartTime;
        public SwitchMaterial switchSchedule;

        public Alarm alarm;

        public AlarmBinding alarmBinding;

        public ViewHolder (AlarmBinding binding, final OnAlarmClickListener alarmClickListener) {
            super(binding.getRoot());
            imageMedication = binding.imageAlarmThumbnail;
            tvScheduleTitle = binding.textviewScheduleTitle;
            tvDescription = binding.textviewDescription;
            textInputEditTextStartTime = binding.editStartTime;
            switchSchedule = binding.switchSchedule;
            alarmBinding = binding;

//            mIdView = binding.itemNumber;
//            mContentView = binding.content;
        }

        @Override
        public String toString () {
//            return super.toString() + " '" + mContentView.getText() + "'";
            return super.toString() + " '" + tvScheduleTitle.getText() + "'";
        }
    }
}