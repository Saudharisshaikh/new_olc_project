package com.app.mhcsn_dr.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PrescripTempletBean {


    public String category_name;

    @SerializedName("data")
    public ArrayList<TemplateDrugBean> templateDrugBeans;


    public class TemplateDrugBean{

        public String drug_descriptor_id;
        public String route_of_administration;
        public String drug_name;
        public String code;
        public String route;
        public String strength;
        public String strength_unit_of_measure;
        public String dosage_form;
        public String dfcode;
        public String dfdesc;
        public String potency_unit;
        public String potency_code;
        public String package_size_unit;
        public String category_name;
        public String instruction;
        public String qty;
        public String no_of_days;
        public String refill;
        public String instructionNote;
        public String rxfillindicator;
        public String local_order;

        public boolean isSelected;
    }
}
