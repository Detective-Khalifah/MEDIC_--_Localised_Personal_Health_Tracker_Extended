package com.blogspot.thengnet.medic.data;

import java.time.LocalDate;
import java.time.LocalTime;

public class Alarm {

    private int id;
    private String title;
    private boolean alarmState;
    private LocalDate startDate;
    private LocalDate stopDate;
    private LocalTime startTime;
    private String administrationPerDay;
    private String tone;
    private boolean vibrateState;
    private boolean repeatState;
    private String repeatTimes;

    public Alarm(int alarmId, String title, boolean state,
                 LocalDate startDate, LocalDate stopDate, LocalTime time, String administrationPerDay,
                 String tone, boolean vibrateState, boolean repeatState/*, String repeatTimes*/) {
        this.id = alarmId;
        this.title = title;
        this.alarmState = state;
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.startTime = time;
        this.administrationPerDay = administrationPerDay;
        this.tone = tone;
        this.vibrateState = vibrateState;
        this.repeatState = repeatState;
//        this.repeatTimes = repeatTimes;
    }

    public int getId () {
        return id;
    }
    public void setId (int id) {
        this.id = id;
    }

    public boolean getAlarmState () {
        return alarmState;
    }
    public void setAlarmState (boolean alarmState) {
        this.alarmState = alarmState;
    }

    public String getTitle () {
        return title;
    }
    public void setTitle (String title) {
        this.title = title;
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

    public LocalTime getStartTime () {
        return startTime;
    }
    public void setStartTime (LocalTime startTime) {
        this.startTime = startTime;
    }

    public String getAdministrationPerDay () {
        return administrationPerDay;
    }
    public void setAdministrationPerDay (String administrationPerDay) {
        this.administrationPerDay = administrationPerDay;
    }

    public String getTone () {
        return tone;
    }
    public void setTone (String tone) {
        this.tone = tone;
    }

    public boolean getVibrateState () {
        return vibrateState;
    }
    public void setVibrateState (boolean vibrateState) {
        this.vibrateState = vibrateState;
    }

    public boolean getRepeatState () {
        return repeatState;
    }
    public void setRepeatState (boolean repeatState) {
        this.repeatState = repeatState;
    }

    public String getRepeatTimes () { return repeatTimes; }
    public void setRepeatTimes (String repeatTimes) { this.repeatTimes = repeatTimes; }
}
