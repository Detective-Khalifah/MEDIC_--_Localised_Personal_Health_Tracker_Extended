package com.blogspot.thengnet.medic;

import android.content.Intent;
//import android.databinding.DataBindingUtil;
//import android.support.v7.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.database.CursorWindow;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.blogspot.thengnet.medic.databinding.ActivityMainBinding;
import com.blogspot.thengnet.medic.utilities.AlarmScheduler;
import com.blogspot.thengnet.medic.utilities.NotificationUtil;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser mSignedInUser;

    private SharedPreferences configurations;

    /** Preference Keys */
    private static String HOUR_FORMAT_PREFERENCE, HOUR_FORMAT_12, HOUR_FORMAT_24,
            ALARM_VOLUME_PREFERENCE,
            ALARM_RING_DURATION_PREFERENCE, ALARM_RING_DURATION_ONE, ALARM_RING_DURATION_THREE,
            ALARM_RING_DURATION_FIVE, ALARM_RING_DURATION_TEN,
            ALARM_SNOOZE_PREFERENCE, ALARM_SNOOZE_DURATION_ONE, ALARM_SNOOZE_DURATION_THREE,
            ALARM_SNOOZE_DURATION_FIVE, ALARM_SNOOZE_DURATION_TEN
            ;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawerLayout = binding.layoutDrawer;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_alarm, R.id.nav_appointment, R.id.nav_hospital)
                .setOpenableLayout(drawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Get a link to SharedPreferences
        configurations = PreferenceManager.getDefaultSharedPreferences(this);

        NotificationUtil.createNotificationChannel(this, "alarm");
        NotificationUtil.createNotificationChannel(this, "appointment");
        NotificationUtil.createNotificationChannel(this, "map");

        // TODO: Find a way to manage memory/transaction against dB; had to use this block to query
        //  large data, otherwise I got SQLiteBlobTooBigException --
        //  android.database.sqlite.SQLiteBlobTooBigException: Row too big to fit into CursorWindow requiredPos=34, totalRows=1
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }

        mAuth = FirebaseAuth.getInstance();
        mSignedInUser = mAuth.getCurrentUser();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null){
//            startActivity(new Intent(this, AlarmsFragment.class));
//        } else {
//            startActivity(new Intent(this, SignInActivity.class));
//        }
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        binding = null;
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String key) {
        // TODO: changed metrics - alarm volume, ring duration, snooze length, hr format,
        //  reminder volume, reminder duration - based on user preferences
    }
}