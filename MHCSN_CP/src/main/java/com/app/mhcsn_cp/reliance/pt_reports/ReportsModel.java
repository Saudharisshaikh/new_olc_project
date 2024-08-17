package com.app.mhcsn_cp.reliance.pt_reports;


public class ReportsModel {
	
	public String id = "";
	public String date = "";
	public String name = "";
	public String report_url = "";
	public String report_thumb = "";
	public String folder_id = "";
	public String folder_name = "";
	

	public String status="0";
    private boolean selected ;

	public ReportsModel() {
	}
	
	public void setDateName(String date, String name) {
		
		this.date = date;
		this.name = name;
	}

	public String getName() {
	       
		return name;
    }
	
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


}
