package com.app.onlinecare_dr_pk.model;

public class CategoriesModel {
	
	public  String catId = "";
	public  String catName = "";
	public  int catIcon = 0;

	public CategoriesModel(String catId, String catName, int catIcon) {
		super();
		this.catId = catId;
		this.catName = catName;
		this.catIcon = catIcon;
	}
}
