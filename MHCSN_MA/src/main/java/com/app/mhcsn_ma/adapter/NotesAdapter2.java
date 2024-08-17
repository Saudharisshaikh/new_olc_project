package com.app.mhcsn_ma.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.mhcsn_ma.ActivityCallLogs;
import com.app.mhcsn_ma.ActivitySoapNotesEditNew;
import com.app.mhcsn_ma.ActivityTelemedicineServices;
import com.app.mhcsn_ma.AfterCallDialogEmcura;
import com.app.mhcsn_ma.R;
import com.app.mhcsn_ma.model.NotesBean;
import com.app.mhcsn_ma.util.DATA;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


public class NotesAdapter2 extends ArrayAdapter<NotesBean> {


	Activity activity;
	ArrayList<NotesBean> notesBeans;
	SharedPreferences prefs;

	public NotesAdapter2(Activity activity, ArrayList<NotesBean> notesBeans) {
		super(activity, R.layout.lv_soap_notes_row2, notesBeans);

		this.activity = activity;
		this.notesBeans = notesBeans;
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME,Context.MODE_PRIVATE);

	}

	static class ViewHolder {

		TextView tvBillingHistory,tvBillingServices,tvBillingObjective,tvBillingFamily,
				tvBillingSubjective,tvBillingAssesment,tvBillingDrname,tvBillingCarePlan,tvAmmend,tvEdit,
				tvOTNotesDate,tvOTNotesTimeIn,tvOTNotesTimeOut,tvOTNotesBP,tvOTNotesHR,tvOTNotesRespirations,
				tvOTNotesSaturation,tvOTNotesBloodSugar,
				tvNotesComplain,tvNotesPain,tvNotesPainBodyPart,tvNotesPrescription,tvExamination,tvDmeReferral,tvSkilledNursing,
				tvHomeCareReferal,
				tvOTNotesTemp,tvOTNotesHeight,tvOTNotesWeight,tvOTNotesBMI,tvSympExp;//tvBillingPatientName

		LinearLayout layEncounterNote, laySoapDetail;
		TextView tvEncounterNotes, tvEncNoteCallStrtTime,tvEncNoteCallEndTime,tvEncNoteCallDuration;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_soap_notes_row2, null);

			viewHolder = new ViewHolder();

			//viewHolder.tvBillingPatientName = (TextView) convertView.findViewById(R.id.tvBillingPatientName);
			viewHolder.tvBillingHistory = (TextView) convertView.findViewById(R.id.tvBillingHistory);
			viewHolder.tvBillingServices = (TextView) convertView.findViewById(R.id.tvBillingServices);
			viewHolder.tvBillingObjective = (TextView) convertView.findViewById(R.id.tvBillingObjective);
			viewHolder.tvBillingFamily = (TextView) convertView.findViewById(R.id.tvBillingFamily);
			viewHolder.tvBillingSubjective = (TextView) convertView.findViewById(R.id.tvBillingSubjective);
			viewHolder.tvBillingAssesment = (TextView) convertView.findViewById(R.id.tvBillingAssesment);
			viewHolder.tvBillingDrname = (TextView) convertView.findViewById(R.id.tvBillingDrname);
			viewHolder.tvBillingCarePlan = (TextView) convertView.findViewById(R.id.tvBillingCarePlan);
			viewHolder.tvAmmend = (TextView) convertView.findViewById(R.id.tvAmmend);
			viewHolder.tvEdit = (TextView) convertView.findViewById(R.id.tvEdit);
			viewHolder.tvNotesComplain = (TextView) convertView.findViewById(R.id.tvNotesComplain);
			viewHolder.tvNotesPain = (TextView) convertView.findViewById(R.id.tvNotesPain);
			viewHolder.tvNotesPainBodyPart = (TextView) convertView.findViewById(R.id.tvNotesPainBodyPart);
			viewHolder.tvNotesPrescription = (TextView) convertView.findViewById(R.id.tvNotesPrescription);

			viewHolder.tvOTNotesDate = (TextView) convertView.findViewById(R.id.tvOTNotesDate);
			viewHolder.tvOTNotesTimeIn = (TextView) convertView.findViewById(R.id.tvOTNotesTimeIn);
			viewHolder.tvOTNotesTimeOut = (TextView) convertView.findViewById(R.id.tvOTNotesTimeOut);
			viewHolder.tvOTNotesBP = (TextView) convertView.findViewById(R.id.tvOTNotesBP);
			viewHolder.tvOTNotesHR = (TextView) convertView.findViewById(R.id.tvOTNotesHR);
			viewHolder.tvOTNotesRespirations = (TextView) convertView.findViewById(R.id.tvOTNotesRespirations);
			viewHolder.tvOTNotesSaturation = (TextView) convertView.findViewById(R.id.tvOTNotesSaturation);
			viewHolder.tvOTNotesBloodSugar = (TextView) convertView.findViewById(R.id.tvOTNotesBloodSugar);

			viewHolder.tvExamination = (TextView) convertView.findViewById(R.id.tvExamination);
			viewHolder.tvSkilledNursing = (TextView) convertView.findViewById(R.id.tvSkilledNursing);
			viewHolder.tvDmeReferral= (TextView) convertView.findViewById(R.id.tvDmeReferral);
			viewHolder.tvHomeCareReferal= (TextView) convertView.findViewById(R.id.tvHomeCareReferal);

			viewHolder.tvSympExp= (TextView) convertView.findViewById(R.id.tvSympExp);
			viewHolder.tvOTNotesTemp= (TextView) convertView.findViewById(R.id.tvOTNotesTemp);
			viewHolder.tvOTNotesHeight= (TextView) convertView.findViewById(R.id.tvOTNotesHeight);
			viewHolder.tvOTNotesWeight= (TextView) convertView.findViewById(R.id.tvOTNotesWeight);
			viewHolder.tvOTNotesBMI= (TextView) convertView.findViewById(R.id.tvOTNotesBMI);

			viewHolder.laySoapDetail= (LinearLayout) convertView.findViewById(R.id.laySoapDetail);
			viewHolder.layEncounterNote= (LinearLayout) convertView.findViewById(R.id.layEncounterNote);
			viewHolder.tvEncounterNotes= (TextView) convertView.findViewById(R.id.tvEncounterNotes);
			viewHolder.tvEncNoteCallStrtTime= (TextView) convertView.findViewById(R.id.tvEncNoteCallStrtTime);
			viewHolder.tvEncNoteCallEndTime= (TextView) convertView.findViewById(R.id.tvEncNoteCallEndTime);
			viewHolder.tvEncNoteCallDuration= (TextView) convertView.findViewById(R.id.tvEncNoteCallDuration);

			convertView.setTag(viewHolder);

			//convertView.setTag(R.id.tvBillingPatientName, viewHolder.tvBillingPatientName);
			convertView.setTag(R.id.tvBillingHistory, viewHolder.tvBillingHistory);
			convertView.setTag(R.id.tvBillingServices, viewHolder.tvBillingServices);
			convertView.setTag(R.id.tvBillingObjective, viewHolder.tvBillingObjective);
			convertView.setTag(R.id.tvBillingFamily, viewHolder.tvBillingFamily);
			convertView.setTag(R.id.tvBillingSubjective, viewHolder.tvBillingSubjective);
			convertView.setTag(R.id.tvBillingAssesment, viewHolder.tvBillingAssesment);
			convertView.setTag(R.id.tvBillingDrname, viewHolder.tvBillingDrname);
			convertView.setTag(R.id.tvBillingCarePlan, viewHolder.tvBillingCarePlan);
			convertView.setTag(R.id.tvAmmend, viewHolder.tvAmmend);
			convertView.setTag(R.id.tvEdit, viewHolder.tvEdit);
			convertView.setTag(R.id.tvNotesComplain, viewHolder.tvNotesComplain);
			convertView.setTag(R.id.tvNotesPain, viewHolder.tvNotesPain);
			convertView.setTag(R.id.tvNotesPainBodyPart, viewHolder.tvNotesPainBodyPart);
			convertView.setTag(R.id.tvNotesPrescription, viewHolder.tvNotesPrescription);

			convertView.setTag(R.id.tvOTNotesDate, viewHolder.tvOTNotesDate);
			convertView.setTag(R.id.tvOTNotesTimeIn, viewHolder.tvOTNotesTimeIn);
			convertView.setTag(R.id.tvOTNotesTimeOut, viewHolder.tvOTNotesTimeOut);
			convertView.setTag(R.id.tvOTNotesBP, viewHolder.tvOTNotesBP);
			convertView.setTag(R.id.tvOTNotesHR, viewHolder.tvOTNotesHR);
			convertView.setTag(R.id.tvOTNotesRespirations, viewHolder.tvOTNotesRespirations);
			convertView.setTag(R.id.tvOTNotesSaturation, viewHolder.tvOTNotesSaturation);
			convertView.setTag(R.id.tvOTNotesBloodSugar, viewHolder.tvOTNotesBloodSugar);

			convertView.setTag(R.id.tvExamination, viewHolder.tvExamination);
			convertView.setTag(R.id.tvSkilledNursing, viewHolder.tvSkilledNursing);
			convertView.setTag(R.id.tvDmeReferral, viewHolder.tvDmeReferral);
			convertView.setTag(R.id.tvHomeCareReferal, viewHolder.tvHomeCareReferal);

			convertView.setTag(R.id.tvSympExp, viewHolder.tvSympExp);
			convertView.setTag(R.id.tvOTNotesTemp, viewHolder.tvOTNotesTemp);
			convertView.setTag(R.id.tvOTNotesHeight, viewHolder.tvOTNotesHeight);
			convertView.setTag(R.id.tvOTNotesWeight, viewHolder.tvOTNotesWeight);
			convertView.setTag(R.id.tvOTNotesBMI, viewHolder.tvOTNotesBMI);

			convertView.setTag(R.id.laySoapDetail, viewHolder.laySoapDetail);
			convertView.setTag(R.id.layEncounterNote, viewHolder.layEncounterNote);
			convertView.setTag(R.id.tvEncounterNotes, viewHolder.tvEncounterNotes);
			convertView.setTag(R.id.tvEncNoteCallStrtTime, viewHolder.tvEncNoteCallStrtTime);
			convertView.setTag(R.id.tvEncNoteCallEndTime, viewHolder.tvEncNoteCallEndTime);
			convertView.setTag(R.id.tvEncNoteCallDuration, viewHolder.tvEncNoteCallDuration);
		} else {

			viewHolder = (ViewHolder) convertView.getTag();
		}

		//viewHolder.tvBillingPatientName.setText(notesBeans.get(position).);
		viewHolder.tvBillingHistory.setText(notesBeans.get(position).history);
		viewHolder.tvBillingServices.setText(notesBeans.get(position).plan);
		viewHolder.tvBillingObjective.setText(notesBeans.get(position).objective);
		viewHolder.tvBillingFamily.setText(notesBeans.get(position).family);
		viewHolder.tvBillingSubjective.setText(notesBeans.get(position).subjective);
		viewHolder.tvBillingAssesment.setText(notesBeans.get(position).assesment);

		String noteTitle = activity instanceof ActivityCallLogs ? "Notes":"SOAP Notes";
		if(notesBeans.get(position).is_amended.equals("1")){
			viewHolder.tvBillingDrname.setText(noteTitle+" Amended by:\n"+notesBeans.get(position).dr_name+" On: "+notesBeans.get(position).notes_date+"\nPatient: "+notesBeans.get(position).patient_name);
		}else{
			viewHolder.tvBillingDrname.setText(noteTitle+" by:\n"+notesBeans.get(position).dr_name+" On: "+notesBeans.get(position).notes_date+"\nPatient: "+notesBeans.get(position).patient_name);
		}
		viewHolder.tvBillingCarePlan.setText(notesBeans.get(position).care_plan);

		/*if(notesBeans.get(position).amend_btn.equals("1")){
			viewHolder.tvAmmend.setVisibility(View.VISIBLE);
		}else{
			viewHolder.tvAmmend.setVisibility(View.GONE);
		}*/
		if(notesBeans.get(position).author_by.equals(prefs.getString("id",""))){
			viewHolder.tvAmmend.setVisibility(View.VISIBLE);
		}else{
			viewHolder.tvAmmend.setVisibility(View.GONE);
		}
		if(notesBeans.get(position).submit_type.equalsIgnoreCase("save")){
			viewHolder.tvEdit.setVisibility(View.VISIBLE);
		}else{
			viewHolder.tvEdit.setVisibility(View.GONE);
		}
		viewHolder.tvAmmend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivitySoapNotesEditNew.selectedNotesBean= notesBeans.get(position);
				activity.startActivity(new Intent(activity, ActivitySoapNotesEditNew.class));
				//ActivitySoapNotesEdit.selectedNotesBean= notesBeans.get(position);
				//activity.startActivity(new Intent(activity, ActivitySoapNotesEdit.class));
			}
		});

		viewHolder.tvEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivitySoapNotesEditNew.selectedNotesBean = notesBeans.get(position);
				Intent intent = new Intent(activity, ActivitySoapNotesEditNew.class);
				intent.putExtra(ActivitySoapNotesEditNew.IS_FOR_EDIT_NOTE_KEY, true);
				activity.startActivity(intent);
			}
		});

		viewHolder.tvOTNotesTemp.setText(notesBeans.get(position).ot_temperature);
		viewHolder.tvOTNotesHeight.setText(notesBeans.get(position).ot_height);
		viewHolder.tvOTNotesWeight.setText(notesBeans.get(position).ot_weight);
		viewHolder.tvOTNotesBMI.setText(notesBeans.get(position).ot_bmi);
		viewHolder.tvOTNotesDate.setText(notesBeans.get(position).ot_date);
		viewHolder.tvOTNotesTimeIn.setText(notesBeans.get(position).ot_timein);
		viewHolder.tvOTNotesTimeOut.setText(notesBeans.get(position).ot_timeout);
		viewHolder.tvOTNotesBP.setText(notesBeans.get(position).ot_bp);
		viewHolder.tvOTNotesHR.setText(notesBeans.get(position).ot_hr);
		viewHolder.tvOTNotesRespirations.setText(notesBeans.get(position).ot_respirations);
		viewHolder.tvOTNotesSaturation.setText(notesBeans.get(position).ot_saturation);
		viewHolder.tvOTNotesBloodSugar.setText(notesBeans.get(position).ot_blood_sugar);

		viewHolder.tvNotesComplain.setText(notesBeans.get(position).complain);
		viewHolder.tvNotesPain.setText(notesBeans.get(position).pain_severity);
		viewHolder.tvNotesPainBodyPart.setText(notesBeans.get(position).pain_related);

		viewHolder.tvSympExp.setText(notesBeans.get(position).pain_where);

		viewHolder.tvNotesPrescription.setText(notesBeans.get(position).prescription);

		StringBuilder examinaton = new StringBuilder();
		if(!notesBeans.get(position).examination.isEmpty()){
			try {
				JSONObject jsonObject = new JSONObject(notesBeans.get(position).examination);
				Iterator<String> iter = jsonObject.keys();
				while (iter.hasNext()) {
					String key = iter.next();
					try {
						Object value = jsonObject.get(key);

						key = key.replace("_"," ");
						key = WordUtils.capitalize(key);
						examinaton.append(key+" : "+value+"\n");
					} catch (JSONException e) {
						// Something went wrong!
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		viewHolder.tvExamination.setText(examinaton);


		StringBuilder skilled_nursing = new StringBuilder();
		if(!notesBeans.get(position).skilled_nursing.isEmpty()){
			try {
				JSONObject jsonObject = new JSONObject(notesBeans.get(position).skilled_nursing);
				Iterator<String> iter = jsonObject.keys();
				while (iter.hasNext()) {
					String key = iter.next();
					try {
						Object value = jsonObject.get(key);
						if(value.toString().equalsIgnoreCase("1")){
							value = "Yes";
						}else if(value.toString().equalsIgnoreCase("0")){
							value = "No";
						}
						key = key.replace("_"," ");
						key = WordUtils.capitalize(key);
						skilled_nursing.append(key+" : "+value+"\n");
					} catch (JSONException e) {
						// Something went wrong!
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		viewHolder.tvSkilledNursing.setText(skilled_nursing);


		StringBuilder dme_referral = new StringBuilder();
		if(!notesBeans.get(position).dme_referral.isEmpty()){
			try {
				JSONObject jsonObject = new JSONObject(notesBeans.get(position).dme_referral);
				Iterator<String> iter = jsonObject.keys();
				while (iter.hasNext()) {
					String key = iter.next();
					try {
						Object value = jsonObject.get(key);

						if(value.toString().equalsIgnoreCase("1")){
							value = "Yes";
						}else if(value.toString().equalsIgnoreCase("0")){
							value = "No";
						}

						key = key.replace("_"," ");
						key = WordUtils.capitalize(key);
						dme_referral.append(key+" : "+value+"\n");
					} catch (JSONException e) {
						// Something went wrong!
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		viewHolder.tvDmeReferral.setText(dme_referral);


		StringBuilder homecare_referral = new StringBuilder();
		if(!notesBeans.get(position).homecare_referral.isEmpty()){
			try {
				JSONObject jsonObject = new JSONObject(notesBeans.get(position).homecare_referral);
				Iterator<String> iter = jsonObject.keys();
				while (iter.hasNext()) {
					String key = iter.next();
					try {
						Object value = jsonObject.get(key);

						if(value.toString().equalsIgnoreCase("1")){
							value = "Yes";
						}else if(value.toString().equalsIgnoreCase("0")){
							value = "No";
						}

						key = key.replace("_"," ");
						key = WordUtils.capitalize(key);
						homecare_referral.append(key+" : "+value+"\n");
					} catch (JSONException e) {
						// Something went wrong!
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		viewHolder.tvHomeCareReferal.setText(homecare_referral);



		if(activity instanceof ActivityCallLogs || activity instanceof AfterCallDialogEmcura || activity instanceof ActivityTelemedicineServices){
			viewHolder.laySoapDetail.setVisibility(View.GONE);
			viewHolder.layEncounterNote.setVisibility(View.VISIBLE);
			viewHolder.tvEncounterNotes.setText(notesBeans.get(position).note_text);
			viewHolder.tvEncNoteCallStrtTime.setText(notesBeans.get(position).visit_start_time);
			viewHolder.tvEncNoteCallEndTime.setText(notesBeans.get(position).visit_end_time);
			viewHolder.tvEncNoteCallDuration.setText(notesBeans.get(position).callDuration);
		}else {
			viewHolder.laySoapDetail.setVisibility(View.VISIBLE);
			viewHolder.layEncounterNote.setVisibility(View.GONE);
		}

		return convertView;
	}

}
