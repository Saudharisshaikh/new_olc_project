package com.app.msu_cp.careplan;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.msu_cp.R;
import com.app.msu_cp.api.ApiCallBack;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.util.CustomAnimations;
import com.app.msu_cp.util.CustomToast;
import com.app.msu_cp.util.DATA;
import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class CPGoalsAdapter extends ArrayAdapter<CP_GoalBean> implements ApiCallBack {

	Activity activity;
	ArrayList<CP_GoalBean> cp_goalBeans;
	CustomToast customToast;

	public CPGoalsAdapter(Activity activity, ArrayList<CP_GoalBean> cp_goalBeans) {
		super(activity, R.layout.lv_cp_goal_row, cp_goalBeans);

		this.activity = activity;
		this.cp_goalBeans = cp_goalBeans;
		customToast = new CustomToast(activity);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cp_goalBeans.size();
	}

	static class ViewHolder {
		TextView tvCPG_Name,tvCPG_CareMgr,tvCPG_PLevel,tvCPG_DateSet,tvCPG_ProgAddView,tvCPG_Accomplish;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_cp_goal_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvCPG_Name = (TextView) convertView.findViewById(R.id.tvCPG_Name);
			viewHolder.tvCPG_CareMgr = (TextView) convertView.findViewById(R.id.tvCPG_CareMgr);
			viewHolder.tvCPG_PLevel = (TextView) convertView.findViewById(R.id.tvCPG_PLevel);
			viewHolder.tvCPG_DateSet = (TextView) convertView.findViewById(R.id.tvCPG_DateSet);
			viewHolder.tvCPG_ProgAddView = (TextView) convertView.findViewById(R.id.tvCPG_ProgAddView);
			viewHolder.tvCPG_Accomplish = (TextView) convertView.findViewById(R.id.tvCPG_Accomplish);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvCPG_Name, viewHolder.tvCPG_Name);
			convertView.setTag(R.id.tvCPG_CareMgr, viewHolder.tvCPG_CareMgr);
			convertView.setTag(R.id.tvCPG_PLevel, viewHolder.tvCPG_PLevel);
			convertView.setTag(R.id.tvCPG_DateSet, viewHolder.tvCPG_DateSet);
			convertView.setTag(R.id.tvCPG_ProgAddView, viewHolder.tvCPG_ProgAddView);
			convertView.setTag(R.id.tvCPG_Accomplish, viewHolder.tvCPG_Accomplish);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvCPG_Name.setText(cp_goalBeans.get(position).goal);
		viewHolder.tvCPG_CareMgr.setText(cp_goalBeans.get(position).caregiver_name);
		viewHolder.tvCPG_PLevel.setText(cp_goalBeans.get(position).goal_priority);
		viewHolder.tvCPG_DateSet.setText(cp_goalBeans.get(position).date_goal_set);
		//viewHolder.tvCPG_ProgAddView.setText(cp_goalBeans.get(position).is_accumplish);
		if(cp_goalBeans.get(position).is_accumplish.equalsIgnoreCase("1")){
			viewHolder.tvCPG_Accomplish.setText("Accomplished");
			//viewHolder.tvCPG_Accomplish.setBackgroundResource(R.drawable.apptmnt_cancel_drawable);
			viewHolder.tvCPG_Accomplish.setEnabled(false);
			viewHolder.tvCPG_ProgAddView.setText("View");
		}else {
			viewHolder.tvCPG_Accomplish.setText("Accomplish");
			//viewHolder.tvCPG_Accomplish.setBackgroundResource(R.drawable.btn_selector);
			viewHolder.tvCPG_Accomplish.setEnabled(true);
			viewHolder.tvCPG_ProgAddView.setText("Add/View");
		}

		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()){
					case R.id.tvCPG_ProgAddView:
						cp_goalBeanSelected = cp_goalBeans.get(position);
						getGoalProgress(cp_goalBeans.get(position).id);
						break;
					case R.id.tvCPG_Accomplish:
						RequestParams params = new RequestParams();
						params.put("id", cp_goalBeans.get(position).id);
						ApiManager apiManager = new ApiManager(ApiManager.MARK_ACCOMPLISH,"post",params, CPGoalsAdapter.this::fetchDataCallback, activity);
						apiManager.loadURL();

						cp_goalBeans.get(position).is_accumplish = "1";//mark as accomplished locally dont reload service
						break;
					default:
						break;
				}
			}
		};
		viewHolder.tvCPG_ProgAddView.setOnClickListener(onClickListener);
		viewHolder.tvCPG_Accomplish.setOnClickListener(onClickListener);

		return convertView;
	}


	static String goal_id_selected = "";
	static CP_GoalBean cp_goalBeanSelected;
	public void getGoalProgress(String goalId){
		goal_id_selected = goalId;
		RequestParams params = new RequestParams();
		ApiManager apiManager = new ApiManager(ApiManager.GET_GOAL_PROGRESS + goalId,"post",params,CPGoalsAdapter.this::fetchDataCallback, activity);
		apiManager.loadURL();
	}

	ArrayList<GoalProgressBean> goalProgressBeans;
	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		if(apiName.equalsIgnoreCase(ApiManager.MARK_ACCOMPLISH)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.getString("status").equalsIgnoreCase("success")){
					customToast.showToast("Goal has been accomplished",0,1);

					notifyDataSetChanged();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}else if(apiName.contains(ApiManager.GET_GOAL_PROGRESS)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray goal_progress = jsonObject.getJSONArray("goal_progress");
				goalProgressBeans = new ArrayList<>();
				Gson gson = new Gson();
				GoalProgressBean goalProgressBean;
				for (int i = 0; i < goal_progress.length(); i++) {
					goalProgressBean = gson.fromJson(goal_progress.getJSONObject(i)+"", GoalProgressBean.class);

					//for expendible
					goalProgressBean.goalProgressBeansSub = new ArrayList<>();
					goalProgressBean.goalProgressBeansSub.add(goalProgressBean);

					goalProgressBeans.add(goalProgressBean);
					goalProgressBean = null;
				}
				showGoalProgressDialog();

			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.ADD_GOAL_PROGRESS)){
			//{"status":"success","message":"Goal Progress Added"}
			try {
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.getString("status").equalsIgnoreCase("success")){
					customToast.showToast("Progress has been added for this goal", 0 , 0);
					dialogAddProgress.dismiss();
					dialogGoalProgress.dismiss();
					getGoalProgress(goal_id_selected);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
	}



	Dialog dialogGoalProgress;
	public void showGoalProgressDialog(){
		dialogGoalProgress = new Dialog(activity,R.style.TransparentThemeH4B);
		dialogGoalProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		dialogGoalProgress.setContentView(R.layout.dialog_goal_progress);

		ImageView ivCancel = (ImageView) dialogGoalProgress.findViewById(R.id.ivCancel);
		FloatingGroupExpandableListView lvGoalProgress = dialogGoalProgress.findViewById(R.id.lvGoalProgress);
		TextView tvNoProg = (TextView) dialogGoalProgress.findViewById(R.id.tvNoProg);
		Button btnAddProgress = (Button) dialogGoalProgress.findViewById(R.id.btnAddProgress);


		//lvGoalProgress.setAdapter(new GoalProgressAdapter(activity,goalProgressBeans));
		//lvGoalProgress.setAdapter(new GPAdapter(activity,goalProgressBeans));

		GPAdapter gpAdapter = new GPAdapter(activity, goalProgressBeans);
		WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(gpAdapter);
		lvGoalProgress.setAdapter(wrapperAdapter);

		if(goalProgressBeans.isEmpty()){
			tvNoProg.setVisibility(View.VISIBLE);
		}else {
			tvNoProg.setVisibility(View.GONE);
		}

		if(cp_goalBeanSelected.is_accumplish.equalsIgnoreCase("1")){
			btnAddProgress.setVisibility(View.GONE);
		}else {
			btnAddProgress.setVisibility(View.VISIBLE);
		}

		btnAddProgress.setOnClickListener(v -> {
			showAddGoalProgressDialog();
		});

		ivCancel.setOnClickListener(v -> dialogGoalProgress.dismiss());

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialogGoalProgress.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

		lp.gravity = Gravity.BOTTOM;
		//lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

		dialogGoalProgress.setCanceledOnTouchOutside(false);
		dialogGoalProgress.show();
		dialogGoalProgress.getWindow().setAttributes(lp);

//        WindowManager.LayoutParams a = dialogAddTeamMember.getWindow().getAttributes();//this code is to remove bacgrond dimness if a.dimAmount = 0;
//        a.dimAmount = 80;
//        dialogAddTeamMember.getWindow().setAttributes(a);
	}


	Dialog dialogAddProgress;
	String [] progressArr = {"-- Select Progress --", "Very good progress","Good progress","Average progress","Poor progress","No progress"};
	String [] yesNoArr = {"-- Select --" , "Yes", "No"};
	public void showAddGoalProgressDialog(){
		dialogAddProgress = new Dialog(activity,R.style.TransparentThemeH4B);
		dialogAddProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogAddProgress.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		//dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		dialogAddProgress.setContentView(R.layout.dialog_goal_add_progress);

		ImageView ivCancel = (ImageView) dialogAddProgress.findViewById(R.id.ivCancel);
		Button btnAddProgress = (Button) dialogAddProgress.findViewById(R.id.btnAddProgress);

		Spinner spGoalProgress = (Spinner) dialogAddProgress.findViewById(R.id.spGoalProgress);
		Spinner spProgRefMade = (Spinner) dialogAddProgress.findViewById(R.id.spProgRefMade);
		Spinner spEmBackup = (Spinner) dialogAddProgress.findViewById(R.id.spEmBackup);
		Spinner spRevNeeded = (Spinner) dialogAddProgress.findViewById(R.id.spRevNeeded);

		EditText etProgRatting = (EditText) dialogAddProgress.findViewById(R.id.etProgRatting);
		EditText etProgBarriers = (EditText) dialogAddProgress.findViewById(R.id.etProgBarriers);
		EditText etProgInterv = (EditText) dialogAddProgress.findViewById(R.id.etProgInterv);
		EditText etProgSelfMgt = (EditText) dialogAddProgress.findViewById(R.id.etProgSelfMgt);
		EditText etProgWhoWasRef = (EditText) dialogAddProgress.findViewById(R.id.etProgWhoWasRef);
		EditText etReasonForRef = (EditText) dialogAddProgress.findViewById(R.id.etReasonForRef);
		EditText etWhatRevMade = (EditText) dialogAddProgress.findViewById(R.id.etWhatRevMade);

		ArrayAdapter<String> spProgressAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, progressArr);
		spGoalProgress.setAdapter(spProgressAdapter);
		ArrayAdapter<String> spYesNoAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, yesNoArr);
		spProgRefMade.setAdapter(spYesNoAdapter);
		spEmBackup.setAdapter(spYesNoAdapter);
		spRevNeeded.setAdapter(spYesNoAdapter);

		btnAddProgress.setOnClickListener(v -> {
			if(spGoalProgress.getSelectedItemPosition() == 0){
				customToast.showToast("Please select goal progress",0,0);
				spGoalProgress.requestFocus();
				new CustomAnimations().shakeAnimate(spGoalProgress, 1000, spGoalProgress);
				return;
			}
			if(spProgRefMade.getSelectedItemPosition() == 0){
				customToast.showToast("Please select an option",0,0);
				spProgRefMade.requestFocus();
				new CustomAnimations().shakeAnimate(spProgRefMade, 1000, spProgRefMade);
				return;
			}
			if(spEmBackup.getSelectedItemPosition() == 0){
				customToast.showToast("Please select an option",0,0);
				spEmBackup.requestFocus();
				new CustomAnimations().shakeAnimate(spEmBackup, 1000, spEmBackup);
				return;
			}
			if(spRevNeeded.getSelectedItemPosition() == 0){
				customToast.showToast("Please select an option",0,0);
				spRevNeeded.requestFocus();
				new CustomAnimations().shakeAnimate(spRevNeeded, 1000, spRevNeeded);
				return;
			}
			String progress = progressArr[spGoalProgress.getSelectedItemPosition()];
			String referral_made = yesNoArr[spProgRefMade.getSelectedItemPosition()];
			String backup_plan = yesNoArr[spEmBackup.getSelectedItemPosition()];
			String are_revision_needed = yesNoArr[spRevNeeded.getSelectedItemPosition()];

			String explain_progress = etProgRatting.getText().toString();
			String barriers = etProgBarriers.getText().toString();
			String interventions_to_overcome_barriers = etProgInterv.getText().toString();
			String self_management = etProgSelfMgt.getText().toString();
			String referral_to = etProgWhoWasRef.getText().toString();
			String reason_for_referral = etReasonForRef.getText().toString();
			String what_revisions_were_made = etWhatRevMade.getText().toString();

			RequestParams params = new RequestParams();
			params.put("progress",progress);
			params.put("referral_made",referral_made);
			params.put("backup_plan",backup_plan);
			params.put("are_revision_needed",are_revision_needed);
			params.put("explain_progress",explain_progress);
			params.put("barriers",barriers);
			params.put("interventions_to_overcome_barriers",interventions_to_overcome_barriers);
			params.put("self_management",self_management);
			params.put("referral_to",referral_to);
			params.put("reason_for_referral",reason_for_referral);
			params.put("what_revisions_were_made",what_revisions_were_made);
			params.put("goal_id", goal_id_selected);
			params.put("doctor_id", ((ActivityCarePlanDetail)activity).prefs.getString("id",""));

			ApiManager apiManager = new ApiManager(ApiManager.ADD_GOAL_PROGRESS,"post",params,CPGoalsAdapter.this::fetchDataCallback, activity);
			apiManager.loadURL();

		});

		ivCancel.setOnClickListener(v -> dialogAddProgress.dismiss());

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialogAddProgress.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

		lp.gravity = Gravity.BOTTOM;
		//lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

		dialogAddProgress.setCanceledOnTouchOutside(false);
		dialogAddProgress.show();
		dialogAddProgress.getWindow().setAttributes(lp);

	}
}
