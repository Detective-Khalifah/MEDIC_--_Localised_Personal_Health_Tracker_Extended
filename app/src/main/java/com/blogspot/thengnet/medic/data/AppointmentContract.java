package com.blogspot.thengnet.medic.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class AppointmentContract {
    public static final String TABLE_NAME = "appointments";

    public static final String SCHEMA = "content://";
    public static final String CONTENT_AUTHORITY = "com.blogspot.thengnet.medic.data.AppointmentProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEMA + CONTENT_AUTHORITY);
    public static final String APPOINTMENTS_PATH = "appointments";

    /**
     * A private constructor to prevent instantiation of the contract class.
     */
    private AppointmentContract () {}

    public static final class AppointmentEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, APPOINTMENTS_PATH);

        /**
         * ID column of the database: Type - TEXT.
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * Subject/title of the appointment: Type - TEXT.
         */
        public static final String COLUMN_APPOINTMENT_TITLE = "appointment_title";

        /**
         * Reminder status -- off/on: Type - INTEGER.
         */
        public static final String COLUMN_REMINDER_STATE = "active";

        /**
         * First date to trigger reminder: Type - TEXT.
         */
        public static final String COLUMN_REMINDER_START_DATE = "reminder_start_date";

        /**
         * Last date to trigger reminder: Type - TEXT.
         */
        public static final String COLUMN_REMINDER_STOP_DATE = "reminder_stop_date";

        /**
         * Time of appointment: Type - TEXT.
         */
        public static final String COLUMN_APPOINTMENT_TIME = "appointment_time";

        /**
         * Location of appointed event (geo-point/coordinates): Type - TEXT.
         */
        public static final String COLUMN_APPOINTMENT_LOCATION = "appointment_location";

        /**
         * Whether to repeat reminder or trigger once: Type - INTEGER.
         */
        public static final String COLUMN_REMINDER_REPEAT_STATE = "repeat_or_not";

        /**
         * Time to trigger event reminder notification: Type - TEXT.
         */
        public static final String COLUMN_APPOINTMENT_REMINDER_TIME = "reminder_time";
    }
}
