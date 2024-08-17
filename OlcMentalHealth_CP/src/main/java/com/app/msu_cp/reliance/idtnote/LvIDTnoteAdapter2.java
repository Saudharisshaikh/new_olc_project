package com.app.msu_cp.reliance.idtnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.msu_cp.R;
import com.app.msu_cp.api.ApiCallBack;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.util.CustomToast;
import com.app.msu_cp.util.DATA;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LvIDTnoteAdapter2 extends ArrayAdapter<IDTnoteBean> implements ApiCallBack {

	Activity activity;
	ArrayList<IDTnoteBean> idTnoteBeans;
	SharedPreferences prefs;
	CustomToast customToast;


	public LvIDTnoteAdapter2(Activity activity, ArrayList<IDTnoteBean> idTnoteBeans) {
		super(activity, R.layout.lv_idt_note_row2, idTnoteBeans);

		this.activity = activity;
		this.idTnoteBeans = idTnoteBeans;

		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME,Context.MODE_PRIVATE);
		customToast = new CustomToast(activity);
	}


	static class ViewHolder {
		TextView tvIdtRowProvName,tvIdtRowNotes,tvIdtRowDuration, tvIDTLockLbl;
		LinearLayout layIDTeditOrLock;
		ImageView ivIdtLockOrEdit;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_idt_note_row2, null);

			viewHolder = new ViewHolder();

			viewHolder.tvIdtRowProvName = convertView.findViewById(R.id.tvIdtRowProvName);
			viewHolder.tvIdtRowNotes = convertView.findViewById(R.id.tvIdtRowNotes);
			viewHolder.tvIdtRowDuration = convertView.findViewById(R.id.tvIdtRowDuration);
			viewHolder.ivIdtLockOrEdit = convertView.findViewById(R.id.ivIdtLockOrEdit);
			viewHolder.tvIDTLockLbl = convertView.findViewById(R.id.tvIDTLockLbl);
			viewHolder.layIDTeditOrLock = convertView.findViewById(R.id.layIDTeditOrLock);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvIdtRowProvName, viewHolder.tvIdtRowProvName);
			convertView.setTag(R.id.tvIdtRowNotes, viewHolder.tvIdtRowNotes);
			convertView.setTag(R.id.tvIdtRowDuration, viewHolder.tvIdtRowDuration);
			convertView.setTag(R.id.ivIdtLockOrEdit, viewHolder.ivIdtLockOrEdit);
			convertView.setTag(R.id.tvIDTLockLbl, viewHolder.tvIDTLockLbl);
			convertView.setTag(R.id.layIDTeditOrLock, viewHolder.layIDTeditOrLock);

			//viewHolder.radios = new RadioButton[]{viewHolder.rb0, viewHolder.rb1, viewHolder.rb2, viewHolder.rb3};

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvIdtRowProvName.setText(idTnoteBeans.get(position).first_name+ " "+idTnoteBeans.get(position).last_name);

		viewHolder.tvIdtRowDuration.setText(idTnoteBeans.get(position).duration);

		String styledText = "<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Notes : </font>"+idTnoteBeans.get(position).notes;
		viewHolder.tvIdtRowNotes.setText(Html.fromHtml(styledText));

		//int imgResId = (idTnoteBeans.get(position).is_lock != null && idTnoteBeans.get(position).is_lock.equalsIgnoreCase("1")) ? R.drawable.ic_locked : R.drawable.ic_unlocked;
		//viewHolder.cbIdtIsLocked.setChecked();

		if(idTnoteBeans.get(position).is_lock != null && idTnoteBeans.get(position).is_lock.equalsIgnoreCase("1")){
			viewHolder.ivIdtLockOrEdit.setImageResource(R.drawable.ic_locked );
			viewHolder.tvIDTLockLbl.setText("Locked");
		}else {
			if(idTnoteBeans.get(position).author_id.equalsIgnoreCase(prefs.getString("id", ""))){
				viewHolder.ivIdtLockOrEdit.setImageResource(R.drawable.ic_edit_idt );
				viewHolder.tvIDTLockLbl.setText("Edit");
			}else {
				viewHolder.ivIdtLockOrEdit.setImageResource(R.drawable.ic_unlocked );
				viewHolder.tvIDTLockLbl.setText("UnLocked");
			}
		}

		viewHolder.layIDTeditOrLock.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(idTnoteBeans.get(position).is_lock != null && idTnoteBeans.get(position).is_lock.equalsIgnoreCase("1")){
					//no action as idt note is locked
				}else {
					if(idTnoteBeans.get(position).author_id.equalsIgnoreCase(prefs.getString("id", ""))){
						//edit dialog
						showUpdateIDTnoteDialog(idTnoteBeans.get(position));
					}else {
						//no action as idt note not given by me
					}
				}
			}
		});

		return convertView;
	}



	Dialog dialogAddIdtNoteForDismiss;
	public void showUpdateIDTnoteDialog(IDTnoteBean idTnoteBean){
		Dialog dialogAddIdtNote = new Dialog(activity,R.style.TransparentThemeH4B);
		dialogAddIdtNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		dialogAddIdtNote.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		dialogAddIdtNote.setContentView(R.layout.dialog_update_idtnote);

		ImageView ivCancel = (ImageView) dialogAddIdtNote.findViewById(R.id.ivCancel);
		Button btnAddIDTsubmit = (Button) dialogAddIdtNote.findViewById(R.id.btnAddIDTsubmit);

		EditText etAddIDTPtName = (EditText) dialogAddIdtNote.findViewById(R.id.etAddIDTPtName);
		EditText etAddIDTdate = (EditText) dialogAddIdtNote.findViewById(R.id.etAddIDTdate);
		/*RadioGroup rgAddIDTptLocation = dialogAddIdtNote.findViewById(R.id.rgAddIDTptLocation);
		CheckBox cbAddIdtSkNursing = dialogAddIdtNote.findViewById(R.id.cbAddIdtSkNursing);
		CheckBox cbAddIdtHomeTheropy = dialogAddIdtNote.findViewById(R.id.cbAddIdtHomeTheropy);
		CheckBox cbAddIdtOutPtTheropy = dialogAddIdtNote.findViewById(R.id.cbAddIdtOutPtTheropy);
		CheckBox[] cbArr = new CheckBox[]{cbAddIdtSkNursing, cbAddIdtHomeTheropy, cbAddIdtOutPtTheropy};*/
		EditText etAddIdtNote = (EditText) dialogAddIdtNote.findViewById(R.id.etAddIdtNote);
		CheckBox cbIdtIsLocked = dialogAddIdtNote.findViewById(R.id.cbIdtIsLocked);

		/*LinearLayout layIDTform = dialogAddIdtNote.findViewById(R.id.layIDTform);
		ExpandableHeightListView lvIDTnote = dialogAddIdtNote.findViewById(R.id.lvIDTnote);
		ExpandableHeightListView lvCareGoal = dialogAddIdtNote.findViewById(R.id.lvCareGoal);


		etAddIDTdate.setOnClickListener(v -> {
			DialogFragment newFragment = new DatePickerFragment(etAddIDTdate);
			newFragment.show(appCompatActivity.getSupportFragmentManager(), "datePicker");
		});*/


		if(idTnoteBean.pfirst_name == null || idTnoteBean.plast_name == null){//idTnoteBean.plast_name is null if come from add IDT
			etAddIDTPtName.setText(DATA.selectedUserCallName);
		}else {
			etAddIDTPtName.setText(idTnoteBean.pfirst_name+" "+idTnoteBean.plast_name);
		}
		//etAddIDTdate.setText(new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).format(new Date()));
		if(idTnoteBean.dateof != null){//idTnoteBean.dateof is null if come from add IDT
			etAddIDTdate.setText(idTnoteBean.dateof);
		}else {
			etAddIDTdate.setText(DATA.date);
		}

		etAddIdtNote.setText(idTnoteBean.notes);
		etAddIdtNote.setSelection(etAddIdtNote.getText().toString().length());


		btnAddIDTsubmit.setOnClickListener(v -> {

			final Dialog dialogSupport = new Dialog(activity);
			dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialogSupport.setContentView(R.layout.dialog_saveassess_opt);

			TextView tvEditAssessDialog = (TextView) dialogSupport.findViewById(R.id.tvEditAssessDialog);
			TextView tvSaveAssesDialog = (TextView) dialogSupport.findViewById(R.id.tvSaveAssesDialog);
			TextView tvConfirmAssess = (TextView) dialogSupport.findViewById(R.id.tvConfirmAssess);

			TextView tvAssesTittle = dialogSupport.findViewById(R.id.tvAssesTittle);

			//tvAssesTittle.setText(assesTittle);

			tvEditAssessDialog.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialogSupport.dismiss();
				}
			});
			tvSaveAssesDialog.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialogSupport.dismiss();
					//assessSubmit.submitAssessment("0");
					updateIDT(btnAddIDTsubmit,dialogAddIdtNote,etAddIdtNote,idTnoteBean,"0");
				}
			});

			tvConfirmAssess.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialogSupport.dismiss();
					//assessSubmit.submitAssessment("1");
					updateIDT(btnAddIDTsubmit,dialogAddIdtNote,etAddIdtNote,idTnoteBean,"1");
				}
			});
			dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			dialogSupport.show();


		});

		ivCancel.setOnClickListener(v -> dialogAddIdtNote.dismiss());

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialogAddIdtNote.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

		lp.gravity = Gravity.BOTTOM;
		//lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

		dialogAddIdtNote.setCanceledOnTouchOutside(false);
		dialogAddIdtNote.show();
		dialogAddIdtNote.getWindow().setAttributes(lp);

		//Assign Views to gloabal variables
		dialogAddIdtNoteForDismiss = dialogAddIdtNote;
		/*lvIDTnoteAddDialog = lvIDTnote;
		lvCareGoalAddDialog = lvCareGoal;
		layIDTformAddDialog = layIDTform;
		btnAddIDTsubmitAddDialog = btnAddIDTsubmit;
		etAddIDTdateAddDialog = etAddIDTdate;
		if(isFromListClick){
			etAddIDTdate.setText(dateofIDt);
			loadIDTnoteByDate(dateofIDt);
		}
		loadLatestCarePlanGoals();*/

		ActivityIDTnoteList.start_timeIDT = new SimpleDateFormat("HH:mm:ss").format(new Date());
	}


	public void updateIDT(Button btnAddIDTsubmit,
						  Dialog dialogAddIdtNote,
						  EditText etAddIdtNote,
						  IDTnoteBean idTnoteBean,
						  String is_lock){


		if(btnAddIDTsubmit.getText().toString().equalsIgnoreCase("Done")){
			dialogAddIdtNote.dismiss();
		}else {
			//String dateof = etAddIDTdate.getText().toString().trim();

			String notes = etAddIdtNote.getText().toString().trim();

				/*if(dateof.isEmpty()){
					etAddIDTdate.setError("");
					customToast.showToast("Please enter the required information", 0, 0);
					return;
				}*/
			if(notes.isEmpty()){
				etAddIdtNote.setError("");
				customToast.showToast("Please enter the required information", 0, 0);
				return;
			}

			//String is_lock = cbIdtIsLocked.isChecked() ? "1" : "0";


			updateIDTnote(notes,is_lock, idTnoteBean.id);

			idTnoteBean.notes = notes;//for adapter notifydatasetchanged
			idTnoteBean.is_lock = is_lock;

				/*String patient_location = "";
				if(rgAddIDTptLocation.getCheckedRadioButtonId() == R.id.rbAddIdtAtHome){
					patient_location = "At Home";
				}else if(rgAddIDTptLocation.getCheckedRadioButtonId() == R.id.rbAddIdtHospitalized){
					patient_location = "Hospitalized";
				}else if(rgAddIDTptLocation.getCheckedRadioButtonId() == R.id.rbAddIdtRehab){
					patient_location = "Rehab";
				}else {
					customToast.showToast("Please select patient location",0,0);
					return;
				}*/


				/*RequestParams params = new RequestParams();
				params.put("dateof", dateof);
				params.put("patient_location",patient_location);
				for (int i = 0; i < cbArr.length; i++) {
					if(cbArr[i].isChecked()){
						params.put("services["+i+"]", cbArr[i].getText().toString());
					}
				}
				params.put("notes",notes);
				params.put("is_lock", is_lock);
				params.put("patient_id", DATA.selectedUserCallId);
				params.put("author_id", prefs.getString("id", ""));
				ApiManager apiManager = new ApiManager(ApiManager.IDT_UPDATE,"post",params, apiCallBack, activity);
				apiManager.loadURL();*/
		}


	}

	public void updateIDTnote(String notes, String is_lock, String id){
		RequestParams params = new RequestParams();
		params.put("patient_id",DATA.selectedUserCallId);
		params.put("author_id", prefs.getString("id", ""));
		params.put("notes", notes);
		params.put("is_lock", is_lock);
		params.put("id" , id);

		ActivityIDTnoteList.end_timeIDT = new SimpleDateFormat("HH:mm:ss").format(new Date());
		params.put("start_time", ActivityIDTnoteList.start_timeIDT);
		params.put("end_time", ActivityIDTnoteList.end_timeIDT);

		ApiManager apiManager = new ApiManager(ApiManager.IDT_UPDATE, "post", params, this, activity);
		apiManager.loadURL();
	}

	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		if(apiName.equalsIgnoreCase(ApiManager.IDT_UPDATE)){
			//{"status":"success","message":"Saved."}
			try {
				JSONObject jsonObject = new JSONObject(content);
				AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
						.setTitle(activity.getResources().getString(R.string.app_name))
						.setMessage("Information saved successfully.")
						.setPositiveButton("Ok, Done",null).create();
				if(jsonObject.getString("status").equalsIgnoreCase("success")){
					if(dialogAddIdtNoteForDismiss != null){
						dialogAddIdtNoteForDismiss.dismiss();
					}
					alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							//loadListData();
							/*if(etAddIDTdateAddDialog != null && (!etAddIDTdateAddDialog.getText().toString().trim().isEmpty())){
								loadIDTnoteByDate(etAddIDTdateAddDialog.getText().toString().trim());
							}*/

							notifyDataSetChanged();
						}
					});
				}else {
					alertDialog.setMessage(jsonObject.optString("message"));
				}

				alertDialog.show();
			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
			}
		}
	}
}
