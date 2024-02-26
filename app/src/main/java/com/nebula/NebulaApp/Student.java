package com.nebula.NebulaApp;
;

public class Student {
    public String email;
    public String userId;
    public boolean newuser;
    public Student(){
        //Default constructor
    }
    public Student(boolean newuser,String email,String userId){
        this.email = email;
        this.userId = userId;
        this.newuser=newuser;

    }

}


