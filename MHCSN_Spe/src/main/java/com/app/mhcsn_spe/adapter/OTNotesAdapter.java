package com.app.mhcsn_spe.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.app.mhcsn_spe.R;
import com.app.mhcsn_spe.api.ApiCallBack;
import com.app.mhcsn_spe.api.ApiManager;
import com.app.mhcsn_spe.model.OTNoteBean;
import com.app.mhcsn_spe.util.DATA;
import com.app.mhcsn_spe.util.DialogPatientInfo;
import com.app.mhcsn_spe.util.ExpandableHeightGridView;
import com.loopj.android.http.RequestParams;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class OTNotesAdapter extends ArrayAdapter<OTNoteBean> implements ApiCallBack{

	Activity activity;
	ArrayList<OTNoteBean> otNoteBeens;
	SharedPreferences prefs;

	public OTNotesAdapter(Activity activity, ArrayList<OTNoteBean> otNoteBeens) {
		super(activity, R.layout.lv_ot_notes_row, otNoteBeens);

		this.activity = activity;
		this.otNoteBeens = otNoteBeens;
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME,Context.MODE_PRIVATE);
		
	}

	static class ViewHolder {

		TextView tvOTNotesWrittenBy,tvOTNotesDate,tvOTNotesTimeIn,tvOTNotesTimeOut,tvOTNotesBP,tvOTNotesHR,tvOTNotesRespirations,
				tvOTNotesSaturation,tvOTNotesSubjective,tvOTNotesADL,tvOTNotesIADL,tvOTNotesMobility,tvOTNotesDMENeeds,tvOTNotesPlan,
				tvOTNotesBloodSugar,tvOTNotesTemperature,tvOTNotesGenericAssessment,tvDmeReferral;
		Button btnViewImges;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.lv_ot_notes_row, null);
			
			viewHolder = new ViewHolder();

			viewHolder.tvOTNotesWrittenBy = (TextView) convertView.findViewById(R.id.tvOTNotesWrittenBy);
			viewHolder.tvOTNotesDate = (TextView) convertView.findViewById(R.id.tvOTNotesDate);
			viewHolder.tvOTNotesTimeIn = (TextView) convertView.findViewById(R.id.tvOTNotesTimeIn);
			viewHolder.tvOTNotesTimeOut = (TextView) convertView.findViewById(R.id.tvOTNotesTimeOut);
			viewHolder.tvOTNotesBP = (TextView) convertView.findViewById(R.id.tvOTNotesBP);
			viewHolder.tvOTNotesHR = (TextView) convertView.findViewById(R.id.tvOTNotesHR);
			viewHolder.tvOTNotesRespirations = (TextView) convertView.findViewById(R.id.tvOTNotesRespirations);
			viewHolder.tvOTNotesSaturation = (TextView) convertView.findViewById(R.id.tvOTNotesSaturation);
			viewHolder.tvOTNotesSubjective = (TextView) convertView.findViewById(R.id.tvOTNotesSubjective);
			viewHolder.tvOTNotesADL = (TextView) convertView.findViewById(R.id.tvOTNotesADL);
			viewHolder.tvOTNotesIADL = (TextView) convertView.findViewById(R.id.tvOTNotesIADL);
			viewHolder.tvOTNotesMobility = (TextView) convertView.findViewById(R.id.tvOTNotesMobility);
			viewHolder.tvOTNotesDMENeeds = (TextView) convertView.findViewById(R.id.tvOTNotesDMENeeds);
			viewHolder.tvOTNotesPlan = (TextView) convertView.findViewById(R.id.tvOTNotesPlan);
			viewHolder.tvOTNotesBloodSugar = (TextView) convertView.findViewById(R.id.tvOTNotesBloodSugar);
			viewHolder.tvOTNotesTemperature = (TextView) convertView.findViewById(R.id.tvOTNotesTemperature);
			viewHolder.tvOTNotesGenericAssessment = (TextView) convertView.findViewById(R.id.tvOTNotesGenericAssessment);
			viewHolder.tvDmeReferral = (TextView) convertView.findViewById(R.id.tvDmeReferral);
			viewHolder.btnViewImges = (Button) convertView.findViewById(R.id.btnViewImges);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvOTNotesWrittenBy, viewHolder.tvOTNotesWrittenBy);
			convertView.setTag(R.id.tvOTNotesDate, viewHolder.tvOTNotesDate);
			convertView.setTag(R.id.tvOTNotesTimeIn, viewHolder.tvOTNotesTimeIn);
			convertView.setTag(R.id.tvOTNotesTimeOut, viewHolder.tvOTNotesTimeOut);
			convertView.setTag(R.id.tvOTNotesBP, viewHolder.tvOTNotesBP);
			convertView.setTag(R.id.tvOTNotesHR, viewHolder.tvOTNotesHR);
			convertView.setTag(R.id.tvOTNotesRespirations, viewHolder.tvOTNotesRespirations);
			convertView.setTag(R.id.tvOTNotesSaturation, viewHolder.tvOTNotesSaturation);
			convertView.setTag(R.id.tvOTNotesSubjective, viewHolder.tvOTNotesSubjective);
			convertView.setTag(R.id.tvOTNotesADL, viewHolder.tvOTNotesADL);
			convertView.setTag(R.id.tvOTNotesIADL, viewHolder.tvOTNotesIADL);
			convertView.setTag(R.id.tvOTNotesMobility, viewHolder.tvOTNotesMobility);
			convertView.setTag(R.id.tvOTNotesDMENeeds, viewHolder.tvOTNotesDMENeeds);
			convertView.setTag(R.id.tvOTNotesPlan, viewHolder.tvOTNotesPlan);
			convertView.setTag(R.id.tvOTNotesBloodSugar, viewHolder.tvOTNotesBloodSugar);
			convertView.setTag(R.id.tvOTNotesTemperature, viewHolder.tvOTNotesTemperature);
			convertView.setTag(R.id.tvOTNotesGenericAssessment, viewHolder.tvOTNotesGenericAssessment);
			convertView.setTag(R.id.tvDmeReferral, viewHolder.tvDmeReferral);
			convertView.setTag(R.id.btnViewImges, viewHolder.btnViewImges);
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvOTNotesWrittenBy.setText("Added by: "+otNoteBeens.get(position).first_name+" "+otNoteBeens.get(position).last_name);
		viewHolder.tvOTNotesDate.setText(otNoteBeens.get(position).ot_date);
		viewHolder.tvOTNotesTimeIn.setText(otNoteBeens.get(position).ot_timein);
		viewHolder.tvOTNotesTimeOut.setText(otNoteBeens.get(position).ot_timeout);
		viewHolder.tvOTNotesBP.setText(otNoteBeens.get(position).ot_bp);
		viewHolder.tvOTNotesHR.setText(otNoteBeens.get(position).ot_hr);
		viewHolder.tvOTNotesRespirations.setText(otNoteBeens.get(position).ot_respirations);
		viewHolder.tvOTNotesSaturation.setText(otNoteBeens.get(position).ot_saturation);
		viewHolder.tvOTNotesSubjective.setText(otNoteBeens.get(position).ot_subjective);
		viewHolder.tvOTNotesADL.setText(otNoteBeens.get(position).ot_adl);
		viewHolder.tvOTNotesIADL.setText(otNoteBeens.get(position).ot_iadl);
		viewHolder.tvOTNotesMobility.setText(otNoteBeens.get(position).ot_mobility);
		viewHolder.tvOTNotesDMENeeds.setText(otNoteBeens.get(position).ot_dmeneed);
		viewHolder.tvOTNotesPlan.setText(otNoteBeens.get(position).ot_plan);
		viewHolder.tvOTNotesBloodSugar.setText(otNoteBeens.get(position).ot_blood_sugar);
		viewHolder.tvOTNotesTemperature.setText(otNoteBeens.get(position).ot_temperature);
		viewHolder.tvOTNotesGenericAssessment.setText(otNoteBeens.get(position).ot_generic_assessment);

		StringBuilder dme_referral = new StringBuilder();
		if(!otNoteBeens.get(position).dme_referral.isEmpty()){
			try {
				JSONObject jsonObject = new JSONObject(otNoteBeens.get(position).dme_referral);
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

		if(otNoteBeens.get(position).num_images.equalsIgnoreCase("0")){
			viewHolder.btnViewImges.setVisibility(View.GONE);
		}else{
			viewHolder.btnViewImges.setVisibility(View.VISIBLE);
		}
		viewHolder.btnViewImges.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RequestParams params = new RequestParams();
				params.put("ot_id", otNoteBeens.get(position).id);
				ApiManager apiManager = new ApiManager(ApiManager.GET_OT_REPORTS,"post",params,OTNotesAdapter.this, activity);
				apiManager.loadURL();
			}
		});

		return convertView;
	}


	public void showReportsDialog(final ArrayList<String> vvImgs){
		final Dialog dialogSupport = new Dialog(activity);
		dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSupport.setContentView(R.layout.dialog_ot_reports);
		dialogSupport.setCanceledOnTouchOutside(false);

		ExpandableHeightGridView gvReportImages = (ExpandableHeightGridView) dialogSupport.findViewById(R.id.gvReportImages);
		gvReportImages.setAdapter(new VVReportImagesAdapter2(activity,vvImgs));
		gvReportImages.setExpanded(true);
		gvReportImages.setPadding(5,5,5,5);

		gvReportImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				DialogPatientInfo.showPicDialog(activity,vvImgs.get(position));
			}
		});

		dialogSupport.findViewById(R.id.ivCancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogSupport.dismiss();
			}
		});
		dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialogSupport.show();
	}

	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		if(apiName.equalsIgnoreCase(ApiManager.GET_OT_REPORTS)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");
				ArrayList<String> reportImages = new ArrayList<>();
				for (int i = 0; i < data.length(); i++) {
					reportImages.add(data.getJSONObject(i).getString("report_name"));
				}

				showReportsDialog(reportImages);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
