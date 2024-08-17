package com.app.mdlive_cp.model;

public class AppBean2 {

    public String appName;
    public String appLink;
    public int drawableSelecterID;
    public String appType;//for remove last line clinical code from msg

    public AppBean2(String appName, String appLink, int drawableSelecterID, String appType) {
        this.appName = appName;
        this.appLink = appLink;
        this.drawableSelecterID = drawableSelecterID;
        this.appType = appType;
    }
}
