package com.app.emcuradr.model;

public class AppBean2 {

    public String appName;
    public String appLink;
    public int drawableSelecterID;

    public AppBean2(String appName, String appLink, int drawableSelecterID) {
        this.appName = appName;
        this.appLink = appLink;
        this.drawableSelecterID = drawableSelecterID;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppLink() {
        return appLink;
    }

    public void setAppLink(String appLink) {
        this.appLink = appLink;
    }

    public int getDrawableSelecterID() {
        return drawableSelecterID;
    }

    public void setDrawableSelecterID(int drawableSelecterID) {
        this.drawableSelecterID = drawableSelecterID;
    }
}
