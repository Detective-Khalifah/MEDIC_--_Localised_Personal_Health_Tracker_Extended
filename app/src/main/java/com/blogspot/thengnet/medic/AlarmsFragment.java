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

import com.blogspot.thengnet.medic.data.AlarmContract;
import com.blogspot.thengnet.medic.data.AlarmsCursorRecyclerAdapter;
import com.blogspot.thengnet.medic.databinding.FragmentAlarmsBinding;
import com.blogspot.thengnet.medic.utilities.AlarmScheduler;
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
// TODO: changed alarm metrics - alarm volume, ring duration, snooze length, hr format - based on user preferences
public class AlarmsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = AlarmsFragment.class.getName();
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final int ALARMS_LOADER_ID = 10;
    private Context mContext;
    private AlarmsCursorRecyclerAdapter mAlarmsCursorAdapter;
    private FragmentAlarmsBinding mBinding;
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
     * screen orientation changes).
     */
    public AlarmsFragment () {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AlarmsFragment newInstance (int columnCount) {
        AlarmsFragment fragment = new AlarmsFragment();
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

        mAlarmsCursorAdapter = new AlarmsCursorRecyclerAdapter(getContext(), null);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        mBinding = FragmentAlarmsBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
//        return inflater.inflate(R.layout.fragment_alarm_list, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mBinding == null) {
            mBinding = FragmentAlarmsBinding.bind(view);
        }

        // Set the adapter
        mContext = view.getContext();
        RecyclerView recyclerView = (RecyclerView) mBinding.recyclerAlarms;
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, mColumnCount));
        }

        recyclerView.setAdapter(mAlarmsCursorAdapter);

        mAlarmsCursorAdapter.setOnAlarmClickListener(new AlarmsCursorRecyclerAdapter.OnAlarmClickListener() {
            @Override
            public void onAlarmClick (int position, long id) {
                Log.v(LOG_TAG, "Alarm position: " + position + "\nId: " + id);
                startActivity(new Intent(mContext, EditAlarmActivity.class).
                        setData(ContentUris.withAppendedId(AlarmContract.AlarmEntry.CONTENT_URI, id)).
                        putExtra("alarm-id", id)
                );
                // TODO: Use appropriate callback method in Fragment
//                finish();
            }
        });

        //
        mAlarmsCursorAdapter.setOnAlarmSwitch(new AlarmsCursorRecyclerAdapter.OnAlarmSwitchListener() {
            @Override
            public void onAlarmSwitch (long id, boolean isSwitched) {
                Log.v(LOG_TAG, "Alarm " + id + " switched " + (isSwitched ? "on" : "off"));
                if (!isSwitched) AlarmScheduler.stopAlarm(mContext, id);
                else AlarmScheduler.setupAlarm(mContext, id,
                        ContentUris.withAppendedId(AlarmContract.AlarmEntry.CONTENT_URI, id));
            }
        });

        mBinding.fabAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Snackbar.make(view, "Phase 2 Task: Add scheduled alarm", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startActivity(new Intent(mContext, EditAlarmActivity.class));
            }
        });

        mBinding.buttonTestNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
//                Intent intent1 = new Intent(AlarmsFragment.this.getContext(), AlarmActivity.class);
//                startActivity(intent1);
                AlarmScheduler.assessAlarms(mContext);
            }
        });

        LoaderManager.getInstance(this).initLoader(ALARMS_LOADER_ID, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader (int id, @Nullable Bundle args) {
        if (id == ALARMS_LOADER_ID) {
            Log.v(LOG_TAG, "ID matches");
            String[] projection = {
                    AlarmContract.AlarmEntry._ID,
                    AlarmContract.AlarmEntry.COLUMN_ALARM_TITLE,
                    AlarmContract.AlarmEntry.COLUMN_ALARM_STATE,
                    AlarmContract.AlarmEntry.COLUMN_ADMINISTRATION_FORM,
                    AlarmContract.AlarmEntry.COLUMN_ALARM_START_DATE,
                    AlarmContract.AlarmEntry.COLUMN_ALARM_STOP_DATE,
                    AlarmContract.AlarmEntry.COLUMN_ALARM_TIME,
                    AlarmContract.AlarmEntry.COLUMN_ADMINISTRATION_PER_DAY,
                    AlarmContract.AlarmEntry.COLUMN_ALARM_TONE,
                    AlarmContract.AlarmEntry.COLUMN_ALARM_VIBRATE_OR_NOT,
                    AlarmContract.AlarmEntry.COLUMN_ALARM_REPEAT_STATE,
            };

            String whereClause = null;
            String[] whereArgs = null;

            showProgress();
            return new CursorLoader(
                    mContext,
                    AlarmContract.AlarmEntry.CONTENT_URI,
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
        if (data != null) Log.v(LOG_TAG, "Alarms fetched! Count: " + data.getCount());
        mAlarmsCursorAdapter.swapCursor(data);
        Log.v(LOG_TAG, "Cursor swapped!");
    }

    @Override
    public void onLoaderReset (@NonNull Loader<Cursor> loader) {
        mAlarmsCursorAdapter.swapCursor(null);
    }

    private void showProgress () {
        mBinding.progressbarAlarms.setVisibility(View.VISIBLE);
    }

    private void hideProgress () {
        mBinding.progressbarAlarms.setVisibility(View.GONE);
    }
}