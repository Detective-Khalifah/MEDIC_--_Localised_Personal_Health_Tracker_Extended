package com.blogspot.thengnet.medic.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.blogspot.thengnet.medic.R;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import androidx.preference.Preference;

public class TimeConverter {
    private static final String LOG_TAG = TimeConverter.class.getName();

    private static SharedPreferences timeConfig;

    // TODO: convert between java.time.*, long (millisecond) and java.lang.String types to parse
    //  into Firebase Cloud Firestore and SQLite types

    /**
     * Utility method to convert time from source type
     *
     * @param milliseconds fetched time in same unit
     * @return converted time in seconds, minutes and hours.
     */
    public static String convertTime (String milliseconds) {
        long hour, minute, second, millisecond;
        millisecond = Long.parseLong(milliseconds);
        StringBuilder time = new StringBuilder();

        hour = TimeUnit.HOURS.convert(millisecond, TimeUnit.MILLISECONDS);
        minute = TimeUnit.MINUTES.convert(millisecond, TimeUnit.MILLISECONDS);
        second = TimeUnit.SECONDS.convert(millisecond, TimeUnit.MILLISECONDS);

        if (hour > 0)
            time.append(hour).append(":");

        if (minute > 9) {
            if (minute > 59) {
                long remainingMinutes = minute - ((minute / 60) * 60);
                if (remainingMinutes > 9)
                    time.append(remainingMinutes).append(":");
                else
                    time.append("0").append(remainingMinutes).append(":");
            } else
                time.append(minute).append(":");
        } else
            time.append("0").append(minute).append(":");

        if (second > 9) {
            if (second > 59) {
                long remainingSeconds = second - (second / 60) * 60;
                if (remainingSeconds > 9)
                    time.append(remainingSeconds);
                else
                    time.append("0").append(remainingSeconds);
            } else
                time.append(second);
        } else {
            time.append("0").append(second);
        }

        return String.valueOf(time);
    }

    public static String getCurrentTime (Context context) {
        return LocalTime.now().toString();
    }

    /**
     * Convert 12-hour-formatted time into 24-hour format.
     * @param _12HTime -- the 12h time received in format hh:mm a (e.g. '01:01 PM').
     * @return a 24hr format time {@link String} (e.g. '13:01') -- default used by {@link LocalTime}.
     */
    public static String convert12To24HTime(String _12HTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        return String.valueOf(LocalTime.parse(_12HTime, formatter));
    }

    /**
     * Convert 24hr format time into 12-hour format.
     * @param _24HTime -- the 24h time received in format HH:mm (e.g. '13:01').
     * @return a 24hr format time {@link String} of format hh:mm a (e.g. '01:01 pm').
     */
    public static String convert24To12HTime(String _24HTime) {
        Log.v(LOG_TAG, "" + DateTimeFormatter.ofPattern("hh:mm a").format(LocalTime.parse(_24HTime)));
        return DateTimeFormatter.ofPattern("hh:mm a").format(LocalTime.parse(_24HTime));
    }

    /**
     * Get the current system time.
     * @return  12hr time as a {@link String} in the form "hh:mm a" (e.g. 01:01 pm).
     */
    public static String getCurrent12HTime() {
        return DateTimeFormatter.ofPattern("hh:mm a").format(LocalTime.now());
    }

    /**
     * Get the current system time.
     * @return  24hr time as a {@link String} in the form "hh:mm a" (e.g. 13:01).
     */
    public static String getCurrent24HTime() {
        return DateTimeFormatter.ofPattern("HH:mm").format(LocalTime.now());
    }

    /**
     * Get the current system date.
     * @return  date as a {@link String} in the format "dd-mm-yyyy" (e.g. 31-12-2022).
     */
    public static String getCurrentDate() {
        return DateTimeFormatter.ofPattern("dd-MM-uuuu").format(LocalDate.now());
    }

    /**
     * Parse 24hr format time into 12hr format.
     * @param hour   of the day -- 0-23.
     * @param minute of the hour -- 0-59.
     * @return 12hr time as a {@link String} in the form "hh:mm a" (e.g. 01:01 pm).
     */
    public static String get12HTime (int hour, int minute) {
        return DateTimeFormatter.ofPattern("hh:mm a").format(LocalTime.of(hour, minute));
    }

    /**
     * Parse 12hr format time into 24hr format.
     * @param hour   of the day -- 1-12 A.M./P.M.
     * @param minute of the hour -- 0-59.
     * @return 24hr time as a {@link String} in the form "HH:mm" (e.g. 13:01).
     */
    public static String get24HTime (int hour, int minute) {
        return LocalTime.of(hour, minute).toString();
    }

    /**
     * Parse the date into format dd-mm-yyyy.
     * @param year  numeric value of the year.
     * @param month zero-index int value of the month-- 0-11.
     * @param day   of the month -- 1-28/29/30/31.
     * @return a {@link String} of the parsed date.
     */
    public static String getDate (int year, int month, int day) {
        // TODO: do not allow past date
        return DateTimeFormatter.ofPattern("dd-MM-uuuu").format(LocalDate.of(year, month + 1, day));
    }

    /**
     * Convert a {@link String} of the time into {@link LocalTime}. Conversions are made between
     * 24hr & 12hr format according to user {@link Preference}s.
     *
     * @param time {@link String} of the time from database.
     * @return a parsed {@link LocalTime} {@link Object}.
     */
    public static LocalTime parseTime(String time) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(time, dateTimeFormatter);
    }

    /**
     * Convert a {@link String} of the date into {@link LocalDate}. No conversions done,
     * save between the data types.
     *
     * @param date {@link String} of the date from database.
     * @return a parsed {@link LocalDate} {@link Object}.
     */
    public static LocalDate parseDate(String date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(date, dateTimeFormatter);
    }

    public static long getTimeInMillis(int year, int month, int day, int hour, int minute) {
        long timeInMillis;
        LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute);
        ZonedDateTime zdt = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        timeInMillis = zdt.toInstant().toEpochMilli();
//        Assert.assertEquals(millis, zdt.toInstant().toEpochMilli());
        return timeInMillis;
    }

    public static long getTimeInMillis(String date, String time) {
        long timeInMillis;
        String theDateTime = date + " " + time;
        Log.v(LOG_TAG, "theDateTime: " + theDateTime);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(theDateTime, dateTimeFormatter);

        timeInMillis = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return timeInMillis;
    }
}
