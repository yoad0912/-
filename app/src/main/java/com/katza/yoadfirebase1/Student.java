package com.katza.yoadfirebase1;

public class Student {
    private String fullName, id;
    private int grade;

    public Student(){}

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    @Override
    public String toString(){
        return "\n\n************פרטי תלמיד****************\nשם מלא: "+this.getFullName()+"\nמספר ת.ז.: "+this.getId()+"\nציון סופי: "+this.getGrade()+"\n**************************************\n\n";
    }
}
