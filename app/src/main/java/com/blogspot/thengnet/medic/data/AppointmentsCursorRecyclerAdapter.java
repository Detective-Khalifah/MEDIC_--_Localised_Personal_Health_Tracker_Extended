package com.blogspot.thengnet.medic.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blogspot.thengnet.medic.databinding.AppointmentBinding;
import com.blogspot.thengnet.medic.utilities.TimeConverter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AppointmentsCursorRecyclerAdapter  extends BaseCursorRecyclerAdapter<AppointmentsCursorRecyclerAdapter.ViewHolder> {

    private OnAppointmentClickListener onAppointmentClickListener;

    public interface OnAppointmentClickListener {
        void onAppointmentClick (int position, long id);
    }

    public AppointmentsCursorRecyclerAdapter (Context context, Cursor cursor) {
        super(context, cursor);
    }

    public void setOnAppointmentClickListener (OnAppointmentClickListener appointmentClickListener) {
        onAppointmentClickListener = appointmentClickListener;
    }

    @NonNull
    @Override
    public AppointmentsCursorRecyclerAdapter.ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                AppointmentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false),
                onAppointmentClickListener
        );
    }

    @Override
    public void onBindViewHolder (AppointmentsCursorRecyclerAdapter.ViewHolder viewHolder, Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(AppointmentContract.AppointmentEntry._ID));
        String appointmentTitle = cursor.getString(cursor.getColumnIndexOrThrow(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_TITLE));
        int reminderState = cursor.getInt(cursor.getColumnIndexOrThrow(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_STATE));
        String reminderStartDate = cursor.getString(cursor.getColumnIndexOrThrow(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_START_DATE));
        String reminderStopDate = cursor.getString(cursor.getColumnIndexOrThrow(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_STOP_DATE));
        String appointmentTime = cursor.getString(cursor.getColumnIndexOrThrow(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_TIME));
        String appointmentLocation = cursor.getString(cursor.getColumnIndexOrThrow(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_LOCATION));
        String reminderTime = cursor.getString(cursor.getColumnIndexOrThrow(AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_REMINDER_TIME));
        int reminderRepeatState = cursor.getInt(cursor.getColumnIndexOrThrow(AppointmentContract.AppointmentEntry.COLUMN_REMINDER_REPEAT_STATE));

        // Create an instance of the Appointment from fetched dB record
        viewHolder.theAppointment = new Appointment(
                Integer.parseInt(id),
                reminderState == 1,
                appointmentTitle,
                TimeConverter.parseDate(reminderStartDate),
                TimeConverter.parseDate(reminderStopDate),
                TimeConverter.parseTime(appointmentTime),
                reminderRepeatState == 1,
                reminderTime,
                appointmentLocation
        );

        // Bind the Appointment to relevant views
        viewHolder.appointmentBinding.setAppointment(viewHolder.theAppointment);
        viewHolder.appointmentBinding.executePendingBindings();
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

        public Appointment theAppointment;
        public AppointmentBinding appointmentBinding;

        public ViewHolder (AppointmentBinding binding, final OnAppointmentClickListener appointmentClickListener) {
            super(binding.getRoot());
            appointmentBinding = binding;
            appointmentBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View view) {
                    if (appointmentClickListener != null) {
                        int position = getBindingAdapterPosition();
                        long id = theAppointment.getId();
//                        long id = getItem(position).getInt(getCursor().getColumnIndexOrThrow(AppointmentContract.AppointmentEntry._ID));
                        if (position != RecyclerView.NO_POSITION)
                            appointmentClickListener.onAppointmentClick(position, id);
                    }
                }
            });
        }
    }
}