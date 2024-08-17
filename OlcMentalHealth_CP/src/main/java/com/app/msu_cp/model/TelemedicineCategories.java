package com.app.msu_cp.model;

import java.util.ArrayList;

public class TelemedicineCategories {
	
	
	public String category_name;
	public ArrayList<TelemedicineCatData> telemedicineCatDatas;
	
	
	public TelemedicineCategories(String category_name, ArrayList<TelemedicineCatData> telemedicineCatDatas) {
		super();
		this.category_name = category_name;
		this.telemedicineCatDatas = telemedicineCatDatas;
	}
	
	

}
