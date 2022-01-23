package com.blogspot.thengnet.medic;

public class Alarm {

    // TODO: Using {@link String} type for #startTime & #endTime during testing; re-factor to a
    //  suitable Date format later.
    private boolean isDay;
    private boolean alarmStatus;
    private String medicationTitle;
    private String description;
    private String startDate;
    private String stopDate;
    private String startTime;
    private int administrationPerDay;

    public Alarm (boolean isDay, boolean alarmStatus, String medicationTitle, String description, String startDate,
                  String startTime, String stopDate, int administrationPerDay) {
        this.isDay = isDay;
        this.alarmStatus = alarmStatus;
        this.medicationTitle = medicationTitle;
        this.description = description;
        this.startDate = startDate;
        this.startTime = startTime;
        this.stopDate = stopDate;
        this.administrationPerDay = administrationPerDay;
    }

    public boolean isDay () {
        return isDay;
    }

    public void setDay (boolean day) {
        isDay = day;
    }

    public boolean getAlarmStatus () {
        return alarmStatus;
    }

    public void setAlarmStatus (boolean alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    public String getMedicationTitle () {
        return medicationTitle;
    }

    public void setMedicationTitle (String medicationTitle) {
        this.medicationTitle = medicationTitle;
    }

    public String getDescription () {
        return description;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public String getStartDate () {
        return startDate;
    }

    public void setStartDate (String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime () {
        return startTime;
    }

    public void setStartTime (String startTime) {
        this.startTime = startTime;
    }

    public String getStopDate () {
        return stopDate;
    }

    public void setStopDate (String stopDate) {
        this.stopDate = stopDate;
    }

    public int getAdministrationPerDay () {
        return administrationPerDay;
    }

    public void setAdministrationPerDay (int administrationPerDay) {
        this.administrationPerDay = administrationPerDay;
    }

}
