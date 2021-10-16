package com.blogspot.thengnet.medic.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class AlarmContract {
    public static final String TABLE_NAME = "alarms";

    public static final String SCHEMA = "content://";
    public static final String CONTENT_AUTHORITY = "com.blogspot.thengnet.medic";
    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEMA + CONTENT_AUTHORITY);
    public static final String ALARMS_PATH = "alarms";

    /**
     * A private constructor to prevent instantiation of the contract class.
     */
    private AlarmContract () {
    }

    public static final class AlarmEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, ALARMS_PATH);

        /**
         * ID column of the database: Type - TEXT.
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * Subject/title of the alarm: Type - TEXT.
         */
        public static final String COLUMN_ALARM_LABEL = "label";


        /**
         * First date to trigger alarm: Type - TEXT.
         */
        public static final String COLUMN_ALARM_START_DATE = "start_date";

        /**
         * Last date to trigger alarm: Type - TEXT.
         */
        public static final String COLUMN_ALARM_STOP_DATE = "stop_date";

        /**
         * Alarm status -- off/on: Type - INTEGER.
         */
        public static final String COLUMN_ALARM_STATUS = "active";

        /**
         * Time to trigger alarm: Type - TEXT.
         */
        public static final String COLUMN_ALARM_TIME = "time";

        /**
         * Whether to repeat alarm on selected days or trigger once: Type - INTEGER.
         */
        public static final String COLUMN_ALARM_REPEAT_STATUS = "repeat_or_not";

        /**
         * Repeat dates of alarm (Array of DATEs): Type - TEXT.
         */
        public static final String COLUMN_ALARM_REPEAT_DATES = "repeat_dates";

        /**
         * Vibration status of alarm: Type - INTEGER.
         */
        public static final String COLUMN_ALARM_VIBRATE_OR_NOT = "vibrate_or_not";

        /**
         * Tone to play for a specific alarm: Type - TEXT.
         */
        public static final String COLUMN_ALARM_TONE = "alarm_tone";
    }
}
