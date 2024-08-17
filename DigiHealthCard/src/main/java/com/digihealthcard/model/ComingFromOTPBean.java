package com.digihealthcard.model;

public class ComingFromOTPBean {

   public String fName , lName , email , phoneNo , fromSocial , socialId , commingfromOtp;

    public ComingFromOTPBean(String fName, String lName, String email, String phoneNo, String fromSocial, String socialId, String commingfromOtp) {
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.fromSocial = fromSocial;
        this.socialId = socialId;
        this.commingfromOtp = commingfromOtp;
    }

    public ComingFromOTPBean() {
    }
}
