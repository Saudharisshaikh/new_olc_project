package com.app.mdlive_cp.reliance.assessment;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.mdlive_cp.R;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.OpenActivity;

import java.util.List;

public class AssesSummaryAdapter extends ArrayAdapter<AssessmentsSummaryBean> {

	Activity activity;
	List<AssessmentsSummaryBean> assessmentsSummaryBeans;
	OpenActivity openActivity;


	public AssesSummaryAdapter(Activity activity, List<AssessmentsSummaryBean> assessmentsSummaryBeans) {
		super(activity, R.layout.lv_asses_summary_row, assessmentsSummaryBeans);

		this.activity = activity;
		this.assessmentsSummaryBeans = assessmentsSummaryBeans;

		openActivity = new OpenActivity(activity);
	}

	static class ViewHolder {
		LinearLayout rootCell;
		TextView tvAssesName,tvAssesDate,tvViewAllAsses,tvAssesProvider;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_asses_summary_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvAssesName = convertView.findViewById(R.id.tvAssesName);
			viewHolder.tvAssesDate = convertView.findViewById(R.id.tvAssesDate);
			viewHolder.tvViewAllAsses = convertView.findViewById(R.id.tvViewAllAsses);
			viewHolder.tvAssesProvider = convertView.findViewById(R.id.tvAssesProvider);
			viewHolder.rootCell = convertView.findViewById(R.id.rootCell);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvAssesName, viewHolder.tvAssesName);
			convertView.setTag(R.id.tvAssesDate, viewHolder.tvAssesDate);
			convertView.setTag(R.id.tvViewAllAsses, viewHolder.tvViewAllAsses);
			convertView.setTag(R.id.tvAssesProvider, viewHolder.tvAssesProvider);
			convertView.setTag(R.id.rootCell, viewHolder.rootCell);

			//viewHolder.radios = new RadioButton[]{viewHolder.rb0, viewHolder.rb1, viewHolder.rb2, viewHolder.rb3};

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvAssesName.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Assessment : </font>"+ assessmentsSummaryBeans.get(position).form_name));
		viewHolder.tvAssesDate.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Date : </font>"+ assessmentsSummaryBeans.get(position).dateof));
		viewHolder.tvAssesProvider.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Provider : </font>"+ assessmentsSummaryBeans.get(position).doctor_name));


		viewHolder.tvViewAllAsses.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String formName = assessmentsSummaryBeans.get(position).form_name;
				if(formName.contains("Self- Management Action Plan")){
					openActivity.open(ActivitySelfMgtList.class, false);
				}else if(formName.contains("Social Determinents of Health Short Form")){
					openActivity.open(ActivitySDHSList.class, false);
				}else if(formName.contains("Patient Health Questionnaire (PHQ-9)")){
					ActivityPHQ_Form.formFlag = 1;
					openActivity.open(ActivityPhqList.class, false);
				}else if(formName.contains("The Lawton Instrumental Activities of Daily Living Scale")){
					ActivityAdlForm.formFlagA = 2;
					openActivity.open(ActivityAdlList.class,false);
				}else if(formName.contains("Katz Index of Independence in Activities of Daily Living")){
					ActivityAdlForm.formFlagA = 1;
					openActivity.open(ActivityAdlList.class,false);
				}else if(formName.contains("INITIAL OCCUPATIONAL THERAPY EVALUATION REPORT")){
					openActivity.open(ActivityOtEvaList.class, false);
				}else if(formName.contains("AOTA OCCUPATIONAL PROFILE")){
					openActivity.open(ActivityOtProfileList.class, false);
				}else if(formName.contains("Complex Care Nutrition Assessment")){
					openActivity.open(ActivityDietAssesList.class, false);
				}else if(formName.contains("Generalized Anxiety Disorder 7-item (GAD-7) scale")){
					ActivityPHQ_Form.formFlag = 2;
					//dialogOptions.dismiss();
					openActivity.open(ActivityPhqList.class, false);
				}
				/*else if(formName.contains("")){

				}else if(formName.contains("")){

				}else if(formName.contains("")){

				}else if(formName.contains("")){

				}else if(formName.contains("")){

				}else if(formName.contains("")){

				}else if(formName.contains("")){

				}else if(formName.contains("")){

				}else if(formName.contains("")){

				}else if(formName.contains("")){

				}*/
			}
		});


		return convertView;
	}

}
