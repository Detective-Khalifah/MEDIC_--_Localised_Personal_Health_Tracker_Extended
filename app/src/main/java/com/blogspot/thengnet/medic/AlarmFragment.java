package com.blogspot.thengnet.medic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.blogspot.thengnet.medic.databinding.FragmentAlarmListBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A fragment representing a list of Items.
 */
public class AlarmFragment extends Fragment {

    private ArrayList<Alarm> alarms;

    private Bundle mSelectedAlarmParams;
    private ArrayList<String> mSelectedAlarmParamsList;
    private FragmentAlarmListBinding binding;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AlarmFragment () {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AlarmFragment newInstance (int columnCount) {
        AlarmFragment fragment = new AlarmFragment();
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
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        binding = FragmentAlarmListBinding.inflate(getLayoutInflater());
        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_alarm_list, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (binding == null) {
            binding = FragmentAlarmListBinding.bind(view);
        }

        // Set the adapter
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) binding.list;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            alarms = new ArrayList<>();

            Alarm testSched0 = new Alarm(true, false,  "Vitamin supplement", "1 pill", "2017-12-01", "09:00", "2021-12-10", 4);
            Alarm testSched1 = new Alarm(true, true, "Blood tonic", "20 ml", "2021-07-02", "11:00", "2021-07-30", 3);
            Alarm testSched2 = new Alarm(true, false,"Exercise", "Daily jogging exercise.", "2020-05-01", "06:00", "2020-05-30", 6);
            Alarm testSched3 = new Alarm(false, true,"Apple", "1", "2021-02-20", "21:45", "2021-04-30", 2);
            alarms.add(testSched0);
            alarms.add(testSched1);
            alarms.add(testSched2);
            alarms.add(testSched3);

            MyAlarmRecyclerViewAdapter adapter = new MyAlarmRecyclerViewAdapter(alarms);

            recyclerView.setAdapter(adapter);

            mSelectedAlarmParams = new Bundle();

            adapter.setOnAlarmClickListener(new MyAlarmRecyclerViewAdapter.OnAlarmClickListener() {
                @Override
                public void onAlarmClick (int position) {
                    /**
                     * Dummy data to send to {@link EditAlarmActivity}
                     * TODO: set up a database to contain the data, and use id to fetch data from it
                     * instead of passing the data from here
                     */
                    mSelectedAlarmParamsList = new ArrayList<>();
                    mSelectedAlarmParamsList.addAll(Collections.singleton(alarms.toString()));
//                mSelectedScheduleParamsList.add(binding.listviewSchedules.getChildAt(position).toString()); // id of the selected CardView
                    mSelectedAlarmParamsList.add( recyclerView.getChildAt(position).toString());

                    Alarm selectedAlarm = alarms.get(position);
                    mSelectedAlarmParams.putStringArray("selected-alarm", new String[]{
                                    selectedAlarm.getMedicationTitle(), selectedAlarm.getDescription(),
                                    selectedAlarm.getStartDate(), selectedAlarm.getStartTime(),
                                    selectedAlarm.getStopDate(), String.valueOf(selectedAlarm.getAdministrationPerDay())
                            }
                    );
                    Log.v(AlarmFragment.this.getClass().getName(), "List item " + position + " clicked!!!");
//                    Snackbar.make(row, "Replace with your own action", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
                    Toast.makeText(AlarmFragment.this.getContext(), "List item " + position + " clicked.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AlarmFragment.this.getContext(), EditAlarmActivity.class).putExtras(mSelectedAlarmParams));
//                    finish();
                }
            });

            binding.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View view) {
                    Snackbar.make(view, "Phase 2 Task: Add scheduled alarm", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    startActivity(new Intent(AlarmFragment.this.getContext(), EditAlarmActivity.class).putExtras(mSelectedAlarmParams));
                }
            });
        }
    }

