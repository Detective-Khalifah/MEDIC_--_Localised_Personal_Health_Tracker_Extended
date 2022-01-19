package com.blogspot.thengnet.medic;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blogspot.thengnet.medic.placeholder.PlaceholderContent;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 */
public class AlarmFragment extends Fragment {

    static ArrayList<Alarm> alarms;

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
        View view = inflater.inflate(R.layout.fragment_alarm_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            alarms = new ArrayList<>();

            Alarm testSched0 = new Alarm(true, false,  "Vitamin supplement", "1 pill", "2017-12-01", "09:00", "2021-12-10", "10:00");
            Alarm testSched1 = new Alarm(true, true, "Blood tonic", "20 ml", "2021-07-02", "11:00", "2021-07-30", "09:00");
            Alarm testSched2 = new Alarm(true, false,"Exercise", "Daily jogging exercise.", "2020-05-01", "06:00", "2020-05-30", "06:30");
            Alarm testSched3 = new Alarm(false, true,"Apple", "1", "2021-02-20", "21:45", "2021-04-30", "22:00");
            alarms.add(testSched0);
            alarms.add(testSched1);
            alarms.add(testSched2);
            alarms.add(testSched3);

//            recyclerView.setAdapter(new MyAlarmRecyclerViewAdapter(PlaceholderContent.ITEMS));
            recyclerView.setAdapter(new MyAlarmRecyclerViewAdapter(alarms));
        }
        return view;
    }
}