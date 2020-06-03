package com.shikshavyavastha;

import androidx.annotation.Keep;

@Keep
public class UserDetailsFileHelper {
    String first_Name, last_Name, email, age, gender, country, ts;

    public UserDetailsFileHelper(String first_Name, String last_Name, String email, String age, String gender, String country, String ts) {
        this.first_Name = first_Name;
        this.last_Name = last_Name;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.country = country;
        this.ts = ts;
    }

    public String getFirst_Name() {
        return first_Name;
    }

    public void setFirst_Name(String first_Name) {
        this.first_Name = first_Name;
    }

    public String getLast_Name() {
        return last_Name;
    }

    public void setLast_Name(String last_Name) {
        this.last_Name = last_Name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

}