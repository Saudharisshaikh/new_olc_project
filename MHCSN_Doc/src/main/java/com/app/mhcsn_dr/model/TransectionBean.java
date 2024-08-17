package com.app.mhcsn_dr.model;

public class TransectionBean {

	private String id;
	private String patient_id;
	private String dateof;
	private String amount;
	private String livecare_id;
	private String payment_method;
	private String donation_id;
	private String transaction_id;
	public TransectionBean(String id, String patient_id, String dateof,
			String amount, String livecare_id, String payment_method,
			String donation_id, String transaction_id) {
		super();
		this.id = id;
		this.patient_id = patient_id;
		this.dateof = dateof;
		this.amount = amount;
		this.livecare_id = livecare_id;
		this.payment_method = payment_method;
		this.donation_id = donation_id;
		this.transaction_id = transaction_id;
	}
	public String getId() {
		return id;
	}
	public String getPatient_id() {
		return patient_id;
	}
	public String getDateof() {
		return dateof;
	}
	public String getAmount() {
		return amount;
	}
	public String getLivecare_id() {
		return livecare_id;
	}
	public String getPayment_method() {
		return payment_method;
	}
	public String getDonation_id() {
		return donation_id;
	}
	public String getTransaction_id() {
		return transaction_id;
	}
	
	
}
