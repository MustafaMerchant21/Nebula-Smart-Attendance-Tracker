package com.nebula.NebulaApp;

import java.util.HashMap;
import java.util.Map;

public class MarkNewAttendance {
    private boolean isMarked;
    private String time;

    public MarkNewAttendance() {
        // Default constructor required for calls to DataSnapshot.getValue(Attendance.class)
    }

    public MarkNewAttendance(boolean isMarked, String time) {
        this.isMarked = isMarked;
        this.time = time;
    }

    // Getters and setters for the fields
    public boolean getIsMarked() {
        return isMarked;
    }

    public void setIsMarked(boolean isMarked) {
        this.isMarked = isMarked;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public HashMap<String, Object> markAttendance(){
        HashMap<String, Object> dayAttendanceData = new HashMap<String, Object>();
        Map<String, String> isMarked = new HashMap<String, String>();
        return dayAttendanceData;
    }
}
