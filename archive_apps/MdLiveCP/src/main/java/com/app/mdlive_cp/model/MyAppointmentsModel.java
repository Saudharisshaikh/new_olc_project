package com.app.mdlive_cp.model;

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

	public String patient_category = "";

	public String is_online = "1";//1 due to livecare activity  //"0";

	public String pain_where = "None";
	public String pain_severity = "None";
	public String symptom_details = "None";

	public String pain_related = "None";

	public String ot_respirations = "-";
	public String ot_blood_sugar = "-";
	public String ot_hr = "-";
	public String ot_bp = "-";
	public String ot_saturation = "-";
	public String ot_height = "-";
	public String ot_temperature = "-";
	public String ot_weight = "-";
	public String bmi = "-";
	public String virtualVisitId = "";

}
