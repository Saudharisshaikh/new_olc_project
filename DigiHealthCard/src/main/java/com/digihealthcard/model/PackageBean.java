package com.digihealthcard.model;

public class PackageBean {

    public String id;
    public String package_name;
    public String duration_month;
    public String amount;
    public String is_deleted;
    public String pkg_type;
    public String pkg_mode;
    public String stripe_plan_id;
    public String appstore_pkg_id;
    public String android_pkg_id;

    public boolean isCustomAdded;


    public PackageBean(String package_name, boolean isCustomAdded,String pkg_type, String pkg_mode,String amount) {
        this.package_name = package_name;
        this.isCustomAdded = isCustomAdded;
        this.pkg_type = pkg_type;
        this.pkg_mode = pkg_mode;
        this.amount= amount;
    }
}
