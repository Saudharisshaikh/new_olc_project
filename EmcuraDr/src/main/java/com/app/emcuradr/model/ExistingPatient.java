package com.app.emcuradr.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ExistingPatient implements Parcelable {

    public String id;
    public String firstName;
    public String lastName;
    public Integer gender;
    public String phone;

    public String email;
    public String username;
    public String image;
    public String city;
    public String state;
    public String is_online;
    public String birthdate;
    public String country;
    public String occupation;
    public String residency;
    public Integer maritalStatus;
    public String latitude;
    public String longitude;
    public String zipcode;
    public String timezone;
    public String platform;
   public String accountStatus;

    public ExistingPatient() {
    }

    protected ExistingPatient(Parcel in) {
        id = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        if (in.readByte() == 0) {
            gender = null;
        } else {
            gender = in.readInt();
        }
        phone = in.readString();
        email = in.readString();
        username = in.readString();
        image = in.readString();
        city = in.readString();
        state = in.readString();
        is_online = in.readString();
        birthdate = in.readString();
        country = in.readString();
        occupation = in.readString();
        residency = in.readString();
        if (in.readByte() == 0) {
            maritalStatus = null;
        } else {
            maritalStatus = in.readInt();
        }
        latitude = in.readString();
        longitude = in.readString();
        zipcode = in.readString();
        timezone = in.readString();
        platform = in.readString();
        accountStatus = in.readString();
    }

    public static final Creator<ExistingPatient> CREATOR = new Creator<ExistingPatient>() {
        @Override
        public ExistingPatient createFromParcel(Parcel in) {
            return new ExistingPatient(in);
        }

        @Override
        public ExistingPatient[] newArray(int size) {
            return new ExistingPatient[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        if (gender == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(gender);
        }
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(username);
        dest.writeString(image);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(is_online);
        dest.writeString(birthdate);
        dest.writeString(country);
        dest.writeString(occupation);
        dest.writeString(residency);
        if (maritalStatus == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(maritalStatus);
        }
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(zipcode);
        dest.writeString(timezone);
        dest.writeString(platform);
        dest.writeString(accountStatus);
    }
}
