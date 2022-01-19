package com.blogspot.thengnet.medic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.blogspot.thengnet.medic.databinding.ActivityAlarmsBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AlarmsActivity extends AppCompatActivity {

    private ActivityAlarmsBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser mSignedInUser;

    private Context appContext;
    static ArrayList<Alarm> alarms;
    AlarmsAdapter alarmsAdapter;

    private Bundle mSelectedAlarmParams;
    private ArrayList<String> mSelectedAlarmParamsList;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlarmsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        appContext = this;

        mAuth = FirebaseAuth.getInstance();
        mSignedInUser = mAuth.getCurrentUser();

        alarms = new ArrayList<>();

        Alarm testSched0 = new Alarm(true, false,  "Vitamin supplement", "1 pill", "2017-12-01", "09:00", "2021-12-10", "10:00");
        Alarm testSched1 = new Alarm(true, true, "Blood tonic", "20 ml", "2021-07-02", "11:00", "2021-07-30", "09:00");
        Alarm testSched2 = new Alarm(true, false,"Exercise", "Daily jogging exercise.", "2020-05-01", "06:00", "2020-05-30", "06:30");
        Alarm testSched3 = new Alarm(false, true,"Apple", "1", "2021-02-20", "21:45", "2021-04-30", "22:00");
        alarms.add(testSched0);
        alarms.add(testSched1);
        alarms.add(testSched2);
        alarms.add(testSched3);

//        alarmsAdapter = new AlarmsAdapter(this, 0, alarms);
        alarmsAdapter = new AlarmsAdapter(this, R.layout.alarm, alarms);
        binding.listviewAlarms.setAdapter(alarmsAdapter);

        mSelectedAlarmParams = new Bundle();

        binding.listviewAlarms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> adapterView, View row, int position, long id) {
                /**
                 * Dummy data to send to {@link EditAlarmActivity}
                 * TODO: set up a database to contain the data, and use id to fetch data from it
                 * instead of passing the data from this {@link AlarmsActivity}
                 */
                mSelectedAlarmParamsList.addAll(Collections.singleton(alarms.toString()));
//                mSelectedScheduleParamsList.add(binding.listviewSchedules.getChildAt(position).toString()); // id of the selected CardView

                Alarm selectedAlarm = alarms.get(position);
                mSelectedAlarmParams.putStringArray("selected-alarm", new String[]{
                                selectedAlarm.getTitle(), selectedAlarm.getDescription(),
                                selectedAlarm.getStartDate(), selectedAlarm.getStartTime(),
                                selectedAlarm.getEndDate(), selectedAlarm.getEndTime()
                        }
                );
                Log.v(this.getClass().getName(), "List item " + position + " clicked!!!");
                Snackbar.make(row, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Toast.makeText(appContext, "List item " + position + " clicked.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(appContext, EditAlarmActivity.class).putExtras(mSelectedAlarmParams));
                finish();
            }
        });





        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Snackbar.make(view, "Phase 2 Task: Add scheduled alarm", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startActivity(new Intent(appContext, EditAlarmActivity.class).putExtras(mSelectedAlarmParams));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_view_profile:
                startActivity(new Intent(this, ViewProfileActivity.class));
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        binding = null;
    }
}