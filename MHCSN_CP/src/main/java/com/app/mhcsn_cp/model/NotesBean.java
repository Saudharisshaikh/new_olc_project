package com.app.mhcsn_cp.model;

public class NotesBean {

	public String history;
	public String plan;
	public String objective;
	public String family;
	public String subjective;
	public String assesment;
	public String patient_id;
	public String notes_date;
	public String dr_name;
	public String care_plan;
	public String author_by;
	public String amend_btn;
	public String is_amended;

	public String id = "";
	public String treatment_codes = "";

	public String ot_date = "";
	public String ot_timein = "";
	public String ot_timeout = "";
	public String ot_bp = "";
	public String ot_hr = "";
	public String ot_respirations = "";
	public String ot_saturation = "";
	public String ot_blood_sugar = "";
	//public String ot_additional_vitals = "";

	public String complain;
	public String pain_where;
	public String pain_severity;
	public String pain_related;
	public String prescription;

	public String examination = "";
	public String dme_referral = "";
	public String skilled_nursing = "";
	public String homecare_referral = "";

	public String submit_type = "";

	public String ot_height = "";
	public String ot_temperature = "";
	public String ot_weight = "";
	public String ot_bmi = "";

	public String patient_name = "";

	public String note_text;//encounter notes new
	public String visit_start_time;//encounter notes new
	public String visit_end_time;//encounter notes new
	public String callDuration;//encounter notes new

	public NotesBean(String history, String plan, String objective,
					 String family, String subjective, String assesment,
					 String patient_id, String notes_date,String dr_name,String care_plan,
					 String author_by,String amend_btn,String is_amended) {
		super();
		this.history = history;
		this.plan = plan;
		this.objective = objective;
		this.family = family;
		this.subjective = subjective;
		this.assesment = assesment;
		this.patient_id = patient_id;
		this.notes_date = notes_date;
		this.dr_name  = dr_name;
		this.care_plan = care_plan;
		this.author_by = author_by;
		this.amend_btn = amend_btn;
		this.is_amended = is_amended;
	}
}
