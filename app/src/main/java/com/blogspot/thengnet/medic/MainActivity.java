package com.blogspot.thengnet.medic;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.CursorWindow;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.blogspot.thengnet.medic.databinding.ActivityMainBinding;
import com.blogspot.thengnet.medic.utilities.ContextUtils;
import com.blogspot.thengnet.medic.utilities.NotificationUtil;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity;
import com.zeugmasolutions.localehelper.LocaleHelper;
import com.zeugmasolutions.localehelper.LocaleHelperActivityDelegate;
import com.zeugmasolutions.localehelper.LocaleHelperApplicationDelegate;

import java.lang.reflect.Field;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceManager;

public class MainActivity extends LocaleAwareCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * Preference Keys
     */
    private static String HOUR_FORMAT_PREFERENCE, HOUR_FORMAT_12, HOUR_FORMAT_24,
            ALARM_VOLUME_PREFERENCE,
            ALARM_RING_DURATION_PREFERENCE, ALARM_RING_DURATION_ONE, ALARM_RING_DURATION_THREE,
            ALARM_RING_DURATION_FIVE, ALARM_RING_DURATION_TEN,
            ALARM_SNOOZE_PREFERENCE, ALARM_SNOOZE_DURATION_ONE, ALARM_SNOOZE_DURATION_THREE,
            ALARM_SNOOZE_DURATION_FIVE, ALARM_SNOOZE_DURATION_TEN;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser mSignedInUser;
    private SharedPreferences configurations;
    SharedPreferences preferences;
    private static final String LOG_TAG = MainActivity.class.getName();
    private String selectedLanguageKey;

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

        // SharedPreferences
        preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        selectedLanguageKey = preferences.getString(getString(R.string.settings_language_key), getString(R.string.lang_english_key));
        Log.v(LOG_TAG, "lang fetched");
    }

    @Override
    public void onStart () {
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
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_view_profile:
//                startActivity(new Intent(this, ViewProfileActivity.class));
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp () {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

//    @Override
//    protected void attachBaseContext (@NonNull Context newBase) {
//        Locale localeToSwitchTo = new Locale("ha-rNG");
//        ContextWrapper localeUpdatedContext = ContextUtils.updateLocale(newBase, localeToSwitchTo);
//        super.attachBaseContext(LocaleHelper.INSTANCE.setLocale(localeUpdatedContext, localeToSwitchTo));
//    }


    @Override
    protected void attachBaseContext (@NonNull Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onResume () {
        super.onResume();
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String key) {
        // TODO: changed metrics - alarm volume, ring duration, snooze length, hr format,
        //  reminder volume, reminder duration - based on user preferences
        Log.v(LOG_TAG, "settings changed");
        Log.v(LOG_TAG, "sharedPrefKey: " + key);

        // Language Preferences changed
        if (key.equals(getResources().getString(R.string.settings_language_key))) {

            String lang_key, selectedLanguage = "en";
            lang_key = sharedPreferences.getString(getString(R.string.settings_language_key), getString(R.string.lang_english_key));
            Log.v(LOG_TAG, "Lang: " + lang_key);

            if (lang_key.equals(getString(R.string.lang_english_key)))
                selectedLanguage = "en";
            else if (lang_key.equals(getString(R.string.lang_hausa_key)))
                selectedLanguage = "ha";
            else if (lang_key.equals(getString(R.string.lang_igbo_key)))
                selectedLanguage = "ig";
            else if (lang_key.equals(getString(R.string.lang_yoruba_key)))
                selectedLanguage = "yo";
            Log.v(LOG_TAG, "Selected Lang: " + selectedLanguage);

            Locale localeToSwitchTo = new Locale(selectedLanguage);
            ContextWrapper localeUpdatedContext = ContextUtils.updateLocale(this, localeToSwitchTo);
            updateLocale(localeToSwitchTo);
//            attachBaseContext(localeUpdatedContext);

//            Locale locale = Locale.forLanguageTag(selectedLanguage);
//            Locale.setDefault(locale);
//            Configuration config = getBaseContext().getResources().getConfiguration();
//            config.locale = locale;
//            getBaseContext().getResources().updateConfiguration(config,
//                    getBaseContext().getResources().getDisplayMetrics());
        }

    }

}