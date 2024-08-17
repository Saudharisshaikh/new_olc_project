package com.app.mhcsn_dr.model;

public class TelemedicineCatData {
	
	public String category_name;
	public String hcpcs_code;
	public String service_name;
	public String category_id;
	public String non_fac_fee;
	public boolean isSelected;

	public String service_id;
	
	
	public TelemedicineCatData(String category_name, String hcpcs_code, String service_name, String category_id,
			String non_fac_fee,boolean isSelected,String service_id) {
		super();
		this.category_name = category_name;
		this.hcpcs_code = hcpcs_code;
		this.service_name = service_name;
		this.category_id = category_id;
		this.non_fac_fee = non_fac_fee;
		this.isSelected = isSelected;
		this.service_id = service_id;
	}
	
	
	

}
