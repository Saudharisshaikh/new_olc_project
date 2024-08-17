package com.app.amnm_dr.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LabReportBean {


    public String category_name;

    @SerializedName("data")
    public ArrayList<LabReportDataBean> labReportDataBeans;


    public class LabReportDataBean{

        public String id;
        public String category_id;
        public String code;
        public String name;
        public String is_deleted;
        public String sortby;
        public String category_name;

        public boolean isSelected;
    }
}
