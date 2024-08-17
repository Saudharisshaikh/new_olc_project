package com.app.OnlineCareTDC_Dr.model;

import java.util.ArrayList;

public class IcdcodesCategories {

    public String id ,category_name;
    public ArrayList<IcdCodesData> icdCodesData;


    public IcdcodesCategories(String id , String category_name, ArrayList<IcdCodesData> icdCodesData) {
        super();
        this.id = id;
        this.category_name = category_name;
        this.icdCodesData = icdCodesData;
    }
}
