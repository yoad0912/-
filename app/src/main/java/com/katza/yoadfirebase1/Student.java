package com.katza.yoadfirebase1;

public class Student {
    private String fullName, id, imageBase64;
    private int grade;

    public Student() {
    }

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

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    @Override
    public String toString() {
        return "------------------------------------\n" +
                "שם מלא: " + this.getFullName() + "\n" +
                "מספר ת.ז.: " + this.getId() + "\n" +
                "ציון סופי: " + this.getGrade() + "\n" +
                "------------------------------------\n\n";
    }
}
