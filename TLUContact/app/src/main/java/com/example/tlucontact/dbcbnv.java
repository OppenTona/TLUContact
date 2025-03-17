package com.example.tlucontact;

public class dbcbnv {
    private String id, name, position, department, phoneNumber, email;
    private int avatar;

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public dbcbnv(String id, String name, String position, String department, String phoneNumber, String email, int avatar) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.department = department;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }





    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
