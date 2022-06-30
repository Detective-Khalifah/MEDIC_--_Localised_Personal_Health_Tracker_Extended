package com.blogspot.thengnet.medic;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blogspot.thengnet.medic.data.AppointmentContract;
import com.blogspot.thengnet.medic.data.AppointmentsCursorRecyclerAdapter;
import com.blogspot.thengnet.medic.databinding.FragmentAppointmentsBinding;
import com.blogspot.thengnet.medic.google.alarms.AlarmActivity;
import com.blogspot.thengnet.medic.utilities.NotificationUtil;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A fragment representing a list of Items.
 */
// TODO: changed appointment metrics - appointment volume, ring duration, snooze length, hr format - based on user preferences
public class AppointmentsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = AppointmentsFragment.class.getName();
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final int APPOINTMENTS_LOADER_ID = 11;
    private static Context mContext;
    private AppointmentsCursorRecyclerAdapter mAppointmentsCursorAdapter;
    private FragmentAppointmentsBinding mBinding;
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AppointmentsFragment () {}

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AppointmentsFragment newInstance (int columnCount) {
        AppointmentsFragment fragment = new AppointmentsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        mAppointmentsCursorAdapter = new AppointmentsCursorRecyclerAdapter(getContext(), null);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        mBinding = FragmentAppointmentsBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mBinding == null) {
            mBinding = FragmentAppointmentsBinding.bind(view);
        }

        // Set the adapter
        mContext = view.getContext();
        RecyclerView recyclerView = (RecyclerView) mBinding.recyclerAppointments;
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, mColumnCount));
        }

        recyclerView.setAdapter(mAppointmentsCursorAdapter);

        mAppointmentsCursorAdapter.setOnAppointmentClickListener(new AppointmentsCursorRecyclerAdapter.OnAppointmentClickListener() {
            @Override
            public void onAppointmentClick (int position, long id) {
                Log.v(LOG_TAG, "Item position: " + position + "\nId: " + id);
                startActivity(new Intent(mContext, EditAppointmentActivity.class).setData(
                        ContentUris.withAppendedId(AppointmentContract.AppointmentEntry.CONTENT_URI, id)));
                // TODO: Use appropriate callback method in Fragment
//                finish();
            }
        });

        mBinding.fabAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Snackbar.make(view, "Phase 2 Task: Add scheduled appointment", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startActivity(new Intent(mContext, EditAppointmentActivity.class));
            }
        });

        mBinding.buttonTestNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                NotificationUtil.buildMedicationNotification(mContext);
                Intent intent1 = new Intent(AppointmentsFragment.this.getContext(), AlarmActivity.class);
                startActivity(intent1);
            }
        });

        LoaderManager.getInstance(this).initLoader(APPOINTMENTS_LOADER_ID, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader (int id, @Nullable Bundle args) {
        if (id == APPOINTMENTS_LOADER_ID) {
            Log.v(LOG_TAG, "ID matches");
            String[] projection = {
                    AppointmentContract.AppointmentEntry._ID,
                    AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_TITLE,
                    AppointmentContract.AppointmentEntry.COLUMN_REMINDER_STATE,
                    AppointmentContract.AppointmentEntry.COLUMN_REMINDER_START_DATE,
                    AppointmentContract.AppointmentEntry.COLUMN_REMINDER_STOP_DATE,
                    AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_TIME,
                    AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_LOCATION,
                    AppointmentContract.AppointmentEntry.COLUMN_REMINDER_REPEAT_STATE,
                    AppointmentContract.AppointmentEntry.COLUMN_APPOINTMENT_REMINDER_TIME
            };

            String whereClause = null;
            String[] whereArgs = null;

            showProgress();
            return new CursorLoader(
                    mContext,
                    AppointmentContract.AppointmentEntry.CONTENT_URI,
                    projection,
                    whereClause,
                    whereArgs,
                    null
            );
        } else {
            Log.v(LOG_TAG, "ID does not match!");
            return null;
        }
    }

    @Override
    public void onLoadFinished (@NonNull Loader<Cursor> loader, Cursor data) {
        hideProgress();
        if (data != null) Log.v(LOG_TAG, "Appointments fetched! Count: " + data.getCount());
        mAppointmentsCursorAdapter.swapCursor(data);
        Log.v(LOG_TAG, "Cursor swapped!");
    }

    @Override
    public void onLoaderReset (@NonNull Loader<Cursor> loader) {
        mAppointmentsCursorAdapter.swapCursor(null);
    }

    private void showProgress () {
        mBinding.progressbarAppointments.setVisibility(View.VISIBLE);
    }

    private void hideProgress () {
        mBinding.progressbarAppointments.setVisibility(View.GONE);
    }
}