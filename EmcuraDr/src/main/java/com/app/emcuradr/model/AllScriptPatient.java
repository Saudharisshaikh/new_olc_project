package com.app.emcuradr.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class AllScriptPatient implements Parcelable {

    public String lastName;
    public String middleName;
    public String suffix;
    public String MRN;
    public String dateOfBirth;
    public String firstName;
    public String gender;
    public String ID;
    public String patientComments;
    public String prefProv;
    public String emailAddress;
    public String SSN;
    public String age;
    public String addressLine2;
    public String addressLine1;
    public String zipCode;
    public String state;
    public String organizationID;
    public String cellPhone;
    public String workPhone;
    public String city;
    public String patientNumber;

    public String otherNumber;
    public String homePhone;

    public AllScriptPatient() {
    }
// Getters and setters for all fields

    protected AllScriptPatient(Parcel in) {
        lastName = in.readString();
        middleName = in.readString();
        suffix = in.readString();
        MRN = in.readString();
        dateOfBirth = in.readString();
        firstName = in.readString();
        gender = in.readString();
        ID = in.readString();
        patientComments = in.readString();
        prefProv = in.readString();
        emailAddress = in.readString();
        SSN = in.readString();
        age = in.readString();
        addressLine2 = in.readString();
        addressLine1 = in.readString();
        zipCode = in.readString();
        state = in.readString();
        organizationID = in.readString();
        cellPhone = in.readString();
        workPhone = in.readString();
        city = in.readString();
        patientNumber = in.readString();
        otherNumber = in.readString();
        homePhone = in.readString();
    }

    public static final Creator<AllScriptPatient> CREATOR = new Creator<AllScriptPatient>() {
        @Override
        public AllScriptPatient createFromParcel(Parcel in) {
            return new AllScriptPatient(in);
        }

        @Override
        public AllScriptPatient[] newArray(int size) {
            return new AllScriptPatient[size];
        }
    };

    // Example:
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getMRN() {
        return MRN;
    }

    public void setMRN(String MRN) {
        this.MRN = MRN;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPatientComments() {
        return patientComments;
    }

    public void setPatientComments(String patientComments) {
        this.patientComments = patientComments;
    }

    public String getPrefProv() {
        return prefProv;
    }

    public void setPrefProv(String prefProv) {
        this.prefProv = prefProv;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getSSN() {
        return SSN;
    }

    public void setSSN(String SSN) {
        this.SSN = SSN;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOrganizationID() {
        return organizationID;
    }

    public void setOrganizationID(String organizationID) {
        this.organizationID = organizationID;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }


    public String getOtherNumber() {
        return otherNumber;
    }

    public void setOtherNumber(String otherNumber) {
        this.otherNumber = otherNumber;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(lastName);
        parcel.writeString(middleName);
        parcel.writeString(suffix);
        parcel.writeString(MRN);
        parcel.writeString(dateOfBirth);
        parcel.writeString(firstName);
        parcel.writeString(gender);
        parcel.writeString(ID);
        parcel.writeString(patientComments);
        parcel.writeString(prefProv);
        parcel.writeString(emailAddress);
        parcel.writeString(SSN);
        parcel.writeString(age);
        parcel.writeString(addressLine2);
        parcel.writeString(addressLine1);
        parcel.writeString(zipCode);
        parcel.writeString(state);
        parcel.writeString(organizationID);
        parcel.writeString(cellPhone);
        parcel.writeString(workPhone);
        parcel.writeString(city);
        parcel.writeString(patientNumber);
        parcel.writeString(otherNumber);
        parcel.writeString(homePhone);
    }

    // ... (other getters and setters)
}
