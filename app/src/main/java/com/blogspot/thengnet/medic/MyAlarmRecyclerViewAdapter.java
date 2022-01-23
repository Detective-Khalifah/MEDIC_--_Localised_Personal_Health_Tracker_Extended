package com.blogspot.thengnet.medic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blogspot.thengnet.medic.databinding.AlarmBinding;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * {@link RecyclerView.Adapter} that can display an {@link Alarm}.
 */
public class MyAlarmRecyclerViewAdapter extends RecyclerView.Adapter<MyAlarmRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<Alarm> mAlarms;
    private OnAlarmClickListener onAlarmClickListener;

    public interface OnAlarmClickListener {
        void onAlarmClick (int position);
    }

    public MyAlarmRecyclerViewAdapter (ArrayList<Alarm> alarms) {
        mAlarms = alarms;
    }

    public void setOnAlarmClickListener (OnAlarmClickListener alarmClickListener) {
        onAlarmClickListener = alarmClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {

        return new ViewHolder(AlarmBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), onAlarmClickListener);

    }

    @Override
    public void onBindViewHolder (final ViewHolder holder, int position) {
        holder.theAlarm = mAlarms.get(position);
        holder.alarmBinding.setAlarm(holder.theAlarm);
        holder.alarmBinding.executePendingBindings();

    }

    @Override
    public int getItemCount () {
        return mAlarms.size();
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
                        if (position != RecyclerView.NO_POSITION)
                            alarmClickListener.onAlarmClick(position);
                    }
                }
            });
        }

        @Override
        public String toString () {
            return super.toString() + " '" + alarmBinding.textviewMedicationTitle.getText() + "'";
        }
    }
}