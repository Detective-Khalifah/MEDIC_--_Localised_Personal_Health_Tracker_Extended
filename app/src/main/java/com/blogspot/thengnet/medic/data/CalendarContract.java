package com.blogspot.thengnet.medic.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class CalendarContract {
    public static final String TABLE_NAME = "calendar_events";

    public static final String SCHEMA = "content://";
    public static final String CONTENT_AUTHORITY = "com.blogspot.thengnet.medic";
    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEMA + CONTENT_AUTHORITY);
    public static final String CALENDAR_EVENTS_PATH = "calendar_events";

    /**
     * A private constructor to prevent instantiation of the contract class.
     */
    private CalendarContract () {
    }

    public static final class CalendarEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CALENDAR_EVENTS_PATH);

        /**
         * ID column of the database: Type - TEXT.
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * Subject/title of the calendar event: Type - TEXT.
         */
        public static final String COLUMN_CALENDAR_EVENT_LABEL = "event";


        /**
         * First date to trigger reminder: Type - TEXT.
         */
        public static final String COLUMN_EVENT_START_DATE = "start_date";

        /**
         * Last date to trigger reminder: Type - TEXT.
         */
        public static final String COLUMN_EVENT_STOP_DATE = "stop_date";

        /**
         * Reminder status -- off/on: Type - INTEGER.
         */
        public static final String COLUMN_EVENT_REMINDER_STATUS = "active";

        /**
         * Time to trigger event reminder notification: Type - TEXT.
         */
        public static final String COLUMN_EVENT_REMINDER_TIME = "reminder_time";

        /**
         * Whether to repeat reminder on selected days or trigger once: Type - INTEGER.
         */
        public static final String COLUMN_EVENT_REPEAT_STATUS = "repeat_or_not";

        /**
         * Repeat dates of event reminder (Array of DATEs): Type - TEXT.
         */
        public static final String COLUMN_EVENT_REPEAT_DATES = "repeat_dates";

        /**
         * Location of appointed event (geopoint/coordinates): Type - TEXT.
         */
        public static final String COLUMN_EVENT_LOCATION = "location";
    }
}
