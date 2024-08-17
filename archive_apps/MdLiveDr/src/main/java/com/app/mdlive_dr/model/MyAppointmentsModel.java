package com.app.mdlive_dr.model;

import java.util.ArrayList;


public class MyAppointmentsModel {
	
	public String id = "";
	public String liveCheckupId = "";
	public String first_name = "";
	public String last_name = "";
	public String symptom_name = "";
	public String condition_name = "";
	public String description = "";
	public String patients_qbid = "";
	public String datetime = "";
	
	public double latitude;
	public double longitude;
	
	public String image = "";
	
	public ArrayList<ReportsModel> sharedReports;
	
	public String birthdate = "";
	public String gender = "";
	public String residency = "";
	public String patient_phone = "";
	public String StoreName = "";
	public String PhonePrimary = "";
	public String pharmacy_address = "-";

	public String is_online = "1";

	public String pain_where = "None";
	public String pain_severity = "None";
	public String symptom_details = "None";

	public String pain_related = "None";
}
