package com.nebula.NebulaApp;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AttendaneOperations {
    private final String STUDENT = "Student";
    private final String INSTITUTE = "Institute";
    private final String ATTENDANCE_DATA = "Attendance Data";
    private final String IS_MARKED = "isMarked";
    private final String TIME = "time";
    private String sanitizedEmail;
    private String year;
    private String month;
    private String date;
    private String time;
    private String instituteID;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference().child("Nebula");


    public AttendaneOperations(String sanitizedEmail, String year, String month, String date, String time, String instituteID) {
        this.sanitizedEmail = sanitizedEmail;
        this.year = year;
        this.month = month;
        this.date = date;
        this.time = time;
        this.instituteID = instituteID;
    }

    public String getSTUDENT() {
        return STUDENT;
    }
    public String getINSTITUTE() {
        return INSTITUTE;
    }
    public String getATTENDANCE_DATA() {
        return ATTENDANCE_DATA;
    }
    public String getSanitizedEmail() {
        return sanitizedEmail;
    }
    public void setSanitizedEmail(String sanitizedEmail) {
        this.sanitizedEmail = sanitizedEmail;
    }
    public String getYear() {
        return year;
    }
    public void setYear(String year) {
        this.year = year;
    }
    public String getMonth() {
        return month;
    }
    public void setMonth(String month) {
        this.month = month;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getIsMarked() {
        return IS_MARKED;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getInstituteID() {
        return instituteID;
    }
    public void setInstituteID(String instituteID) {
        this.instituteID = instituteID;
    }

    public void markAttendance(boolean isMarked, String time){
        if (isMarked){
            reference.child(INSTITUTE).child(instituteID).child(STUDENT).child(sanitizedEmail)
                    .child(ATTENDANCE_DATA).child(year).child(month).child(date).child(IS_MARKED).setValue(Boolean.TRUE);
            reference.child(INSTITUTE).child(instituteID).child(STUDENT).child(sanitizedEmail)
                    .child(ATTENDANCE_DATA).child(year).child(month).child(date).child(IS_MARKED).child(TIME).setValue(time);
        }else{
            reference.child(INSTITUTE).child(instituteID).child(STUDENT).child(sanitizedEmail)
                    .child(ATTENDANCE_DATA).child(year).child(month).child(date).child(IS_MARKED).setValue(Boolean.FALSE);
            reference.child(INSTITUTE).child(instituteID).child(STUDENT).child(sanitizedEmail)
                    .child(ATTENDANCE_DATA).child(year).child(month).child(date).child(IS_MARKED).child(TIME).setValue(time);
        }
    }

    public double getThisMonthStats(){
        double percentage = 0.0;
        // TODO  INSTITUTE > instituteId > STUDENT > studentID > ATTENDANCE_DATA > year > month > date > (isMarked=boolean), (time=String)
        return percentage;
    }
}
