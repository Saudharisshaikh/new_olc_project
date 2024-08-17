package com.app.OnlineCareTDC_Dr.model;

public class IcdCodesData {

    public String id,category_id,code,desc;
    public boolean isSelected;

    public IcdCodesData(String id, String category_id, String code, String desc, boolean isSelected) {
        this.id = id;
        this.category_id = category_id;
        this.code = code;
        this.desc = desc;
        this.isSelected = isSelected;
    }
}

