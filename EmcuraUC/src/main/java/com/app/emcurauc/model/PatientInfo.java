package com.app.emcurauc.model;

public class PatientInfo{
    public String first_name;
    public String last_name;
    public String middle_name;
    public String suffix;
    public String member_id;
    public String street_address;
    public Object street_address_2;
    public String city;
    public String state;
    public String zip;
    public String dob;
    public String gender;

    public PatientInfo() {
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getStreet_address() {
        return street_address;
    }

    public void setStreet_address(String street_address) {
        this.street_address = street_address;
    }

    public Object getStreet_address_2() {
        return street_address_2;
    }

    public void setStreet_address_2(Object street_address_2) {
        this.street_address_2 = street_address_2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public PatientInfo(String first_name, String last_name, String middle_name, String suffix, String member_id, String street_address, Object street_address_2, String city, String state, String zip, String dob, String gender) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.middle_name = middle_name;
        this.suffix = suffix;
        this.member_id = member_id;
        this.street_address = street_address;
        this.street_address_2 = street_address_2;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.dob = dob;
        this.gender = gender;
    }
}
