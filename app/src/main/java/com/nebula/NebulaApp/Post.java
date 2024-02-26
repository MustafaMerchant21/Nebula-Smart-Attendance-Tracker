package com.nebula.NebulaApp;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Post {

    String Firstname ;
    String Middlename ;
    String Lastname ;
    String Mobile ;
    String Email;
    String Date_of_birth ;
    String Selectedgender ;
    String SelectedCourse ;
    String SelectedSemester;
    String Selected_year_of_study ;
    boolean newuser;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(boolean newuser,String sanitizedEmail, String Institute_ID,String Firstname, String Middlename,String Lastname,
                String Mobile ,
                String Email,
                String Date_of_birth,
                String Selectedgender ,
                String SelectedCourse ,
                String SelectedSemester ,
                String Selected_year_of_study) {
        this.Firstname = Firstname ;
        this.Middlename = Middlename;
        this.Lastname= Lastname;
        this.Mobile = Mobile;
        this.Email = Email;
        this.Date_of_birth = Date_of_birth;
        this.Selectedgender = Selectedgender;
        this.SelectedCourse = SelectedCourse;
        this.SelectedSemester = SelectedSemester;
        this.Selected_year_of_study = Selected_year_of_study;
        this.newuser = newuser;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Firstname", Firstname);
        result.put("Middlename", Middlename);
        result.put("Lastname", Lastname);
        result.put("Mobile", Mobile);
        result.put("Email", Email);
        result.put("Date_of_birth", Date_of_birth);
        result.put("Selectedgender", Selectedgender);
        result.put("SelectedCourse", SelectedCourse);
        result.put("SelectedSemester", SelectedSemester);
        result.put("Selected_year_of_study", Selected_year_of_study);
        result.put("newuser",newuser);
        return result;
    }
}
