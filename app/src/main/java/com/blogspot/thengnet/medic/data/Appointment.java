package com.blogspot.thengnet.medic.data;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {

    private int id;
    private String title;
    private boolean reminderState;
    private LocalDate startDate;
    private LocalDate stopDate;
    private LocalTime reminderTime;
    private boolean repeatState;
    private String repeatDates;
    private String location;

    public Appointment(int alarmId, boolean state, String title, LocalDate startDate, LocalDate stopDate,
                       LocalTime time, boolean repeatState, String repeatDates, String location) {
        this.id = alarmId;
        this.reminderState = state;
        this.title = title;
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.reminderTime = time;
        this.repeatState = repeatState;
        this.repeatDates = repeatDates;
        this.location = location;
    }

    public int getId () {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }

    public String getTitle () {
        return title;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public boolean getReminderState () {
        return reminderState;
    }

    public void setReminderState (boolean reminderState) {
        this.reminderState = reminderState;
    }

    public LocalDate getStartDate () {
        return startDate;
    }

    public void setStartDate (LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getStopDate () {
        return stopDate;
    }

    public void setStopDate (LocalDate stopDate) {
        this.stopDate = stopDate;
    }

    public LocalTime getReminderTime () {
        return reminderTime;
    }

    public void setReminderTime (LocalTime reminderTime) {
        this.reminderTime = reminderTime;
    }

    public boolean getRepeatState () {
        return repeatState;
    }

    public void setRepeatState (boolean repeatState) {
        this.repeatState = repeatState;
    }

    public String getRepeatDates () {
        return repeatDates;
    }

    public void setRepeatDates (String repeatDates) {
        this.repeatDates = repeatDates;
    }

    public String getLocation () {
        return location;
    }

    public void setLocation (String location) {
        this.location = location;
    }
}
