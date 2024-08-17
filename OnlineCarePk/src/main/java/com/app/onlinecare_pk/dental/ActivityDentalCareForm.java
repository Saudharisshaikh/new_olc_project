package com.app.onlinecare_pk.dental;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.onlinecare_pk.ActivityUrgentCareDoc;
import com.app.onlinecare_pk.BaseActivity;
import com.app.onlinecare_pk.LiveCareWaitingArea;
import com.app.onlinecare_pk.R;
import com.app.onlinecare_pk.api.ApiManager;
import com.app.onlinecare_pk.paypal.PaymentLiveCare;
import com.app.onlinecare_pk.util.DATA;
import com.app.onlinecare_pk.util.ExpandableHeightGridView;
import com.app.onlinecare_pk.util.ExpandableHeightListView;
import com.app.onlinecare_pk.util.LiveCareInsurance;
import com.app.onlinecare_pk.util.RecyclerItemClickListener;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.app.onlinecare_pk.util.SharedPrefsHelper.PKG_AMOUNT;

public class ActivityDentalCareForm extends BaseActivity{


	ExpandableHeightGridView lvDentalSymptom;
	RecyclerView rvDentalPainLevel;
	Button btnContinue,btnContinue2;

	//ImageView ivPrevSec1;

	LinearLayout laySec_1, laySec_2,laySec_3,laySec_4;
	TextView tvSec1, tvSec2,tvSec3,tvSec4;
	CheckBox cbWholeMouth;

	ExpandableHeightListView lvDentalSymptomSec1, lvDentalSymptomSec2,lvDentalSymptomSec3;

	EditText etLiveExtraInfo;
	ImageView  ic_mike_LiveExtraInfo;

	Button btnSubmit;

	ViewFlipper vfDentalCareForm;
	int selectedChild = 0;

	ArrayList<DentalSymptomBean> dentalSymptomBeans = new ArrayList<>();
	List<String> dentalPainLevelOptions;

	String selectedPainLevel;


	ArrayList<String> sectionNames = new ArrayList<>();

	ArrayList<DentalSymptomBean> dentalSymptomBeansSection1 = new ArrayList<>(),
			dentalSymptomBeansSection2 = new ArrayList<>(),
			dentalSymptomBeansSection3 = new ArrayList<>();


	Button btnPre2,btnPre3;

	ImageView ivTeeth1,ivTeeth2,ivTeeth3,ivTeeth4,ivTeeth5,ivTeeth6,ivTeeth7,ivTeeth8,ivTeeth9,ivTeeth10,
			ivTeeth11,ivTeeth12,ivTeeth13,ivTeeth14,ivTeeth15,ivTeeth16,ivTeeth17,ivTeeth18,ivTeeth19,ivTeeth20,
			ivTeeth21,ivTeeth22,ivTeeth23,ivTeeth24,ivTeeth25,ivTeeth26,ivTeeth27,ivTeeth28,ivTeeth29,ivTeeth30;


	CheckBox cbTeeth_1, cbTeeth_2, cbTeeth_3, cbTeeth_4, cbTeeth_5, cbTeeth_6, cbTeeth_7, cbTeeth_8, cbTeeth_9, cbTeeth_10, cbTeeth_11, cbTeeth_12, cbTeeth_13, cbTeeth_14, cbTeeth_15, cbTeeth_16,
	cbTeeth_17, cbTeeth_18, cbTeeth_19, cbTeeth_20, cbTeeth_21, cbTeeth_22, cbTeeth_23, cbTeeth_24, cbTeeth_25, cbTeeth_26, cbTeeth_27, cbTeeth_28, cbTeeth_29, cbTeeth_30, cbTeeth_31, cbTeeth_32 ;
	TextView tvTeeth_1, tvTeeth_2, tvTeeth_3, tvTeeth_4, tvTeeth_5, tvTeeth_6, tvTeeth_7, tvTeeth_8, tvTeeth_9, tvTeeth_10, tvTeeth_11, tvTeeth_12, tvTeeth_13, tvTeeth_14, tvTeeth_15, tvTeeth_16,
	tvTeeth_17, tvTeeth_18, tvTeeth_19, tvTeeth_20, tvTeeth_21, tvTeeth_22, tvTeeth_23, tvTeeth_24, tvTeeth_25, tvTeeth_26, tvTeeth_27, tvTeeth_28, tvTeeth_29, tvTeeth_30, tvTeeth_31, tvTeeth_32 ;


	public static boolean callAPI = false;

	@Override
	protected void onResume() {
		super.onResume();

		if(callAPI){
			getLiveCareCall("free");
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dentalcare_form);

		if(getSupportActionBar() != null){
			getSupportActionBar().setTitle("Dental Care");
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);

			getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_dental_header));
		}

		lvDentalSymptom = findViewById(R.id.lvDentalSymptom);
		rvDentalPainLevel = findViewById(R.id.rvDentalPainLevel);
		btnContinue = findViewById(R.id.btnContinue);

		btnContinue2 = findViewById(R.id.btnContinue2);

		//ivPrevSec1 = findViewById(R.id.ivPrevSec1);

		vfDentalCareForm = findViewById(R.id.vfDentalCareForm);

		laySec_1 = findViewById(R.id.laySec_1);
		laySec_2 = findViewById(R.id.laySec_2);
		laySec_3 = findViewById(R.id.laySec_3);
		laySec_4 = findViewById(R.id.laySec_4);
		tvSec1 = findViewById(R.id.tvSec1);
		tvSec2 = findViewById(R.id.tvSec2);
		tvSec3 = findViewById(R.id.tvSec3);
		tvSec4 = findViewById(R.id.tvSec4);

		cbWholeMouth = findViewById(R.id.cbWholeMouth);

		lvDentalSymptomSec1 = findViewById(R.id.lvDentalSymptomSec1);
		lvDentalSymptomSec2 = findViewById(R.id.lvDentalSymptomSec2);
		lvDentalSymptomSec3 = findViewById(R.id.lvDentalSymptomSec3);

		etLiveExtraInfo = findViewById(R.id.etLiveExtraInfo);
		ic_mike_LiveExtraInfo = findViewById(R.id.ic_mike_LiveExtraInfo);

		btnSubmit = findViewById(R.id.btnSubmit);

		btnPre2 = findViewById(R.id.btnPre2);
		btnPre3 = findViewById(R.id.btnPre3);


		ivTeeth1 = findViewById(R.id.ivTeeth1);
		ivTeeth2 = findViewById(R.id.ivTeeth2);
		ivTeeth3 = findViewById(R.id.ivTeeth3);
		ivTeeth4 = findViewById(R.id.ivTeeth4);
		ivTeeth5 = findViewById(R.id.ivTeeth5);
		ivTeeth6 = findViewById(R.id.ivTeeth6);
		ivTeeth7 = findViewById(R.id.ivTeeth7);
		ivTeeth8 = findViewById(R.id.ivTeeth8);
		ivTeeth9 = findViewById(R.id.ivTeeth9);
		ivTeeth10 = findViewById(R.id.ivTeeth10);
		ivTeeth11 = findViewById(R.id.ivTeeth11);
		ivTeeth12 = findViewById(R.id.ivTeeth12);
		ivTeeth13 = findViewById(R.id.ivTeeth13);
		ivTeeth14 = findViewById(R.id.ivTeeth14);
		ivTeeth15 = findViewById(R.id.ivTeeth15);
		ivTeeth16 = findViewById(R.id.ivTeeth16);
		ivTeeth17 = findViewById(R.id.ivTeeth17);
		ivTeeth18 = findViewById(R.id.ivTeeth18);
		ivTeeth19 = findViewById(R.id.ivTeeth19);
		ivTeeth20 = findViewById(R.id.ivTeeth20);
		ivTeeth21 = findViewById(R.id.ivTeeth21);
		ivTeeth22 = findViewById(R.id.ivTeeth22);
		ivTeeth23 = findViewById(R.id.ivTeeth23);
		ivTeeth24 = findViewById(R.id.ivTeeth24);
		ivTeeth25 = findViewById(R.id.ivTeeth25);
		ivTeeth26 = findViewById(R.id.ivTeeth26);
		ivTeeth27 = findViewById(R.id.ivTeeth27);
		ivTeeth28 = findViewById(R.id.ivTeeth28);
		ivTeeth29 = findViewById(R.id.ivTeeth29);
		ivTeeth30 = findViewById(R.id.ivTeeth30);


		cbTeeth_1 = findViewById(R.id.cbTeeth_1);
		cbTeeth_2 = findViewById(R.id.cbTeeth_2);
		cbTeeth_3 = findViewById(R.id.cbTeeth_3);
		cbTeeth_4 = findViewById(R.id.cbTeeth_4);
		cbTeeth_5 = findViewById(R.id.cbTeeth_5);
		cbTeeth_6 = findViewById(R.id.cbTeeth_6);
		cbTeeth_7 = findViewById(R.id.cbTeeth_7);
		cbTeeth_8 = findViewById(R.id.cbTeeth_8);
		cbTeeth_9 = findViewById(R.id.cbTeeth_9);
		cbTeeth_10 = findViewById(R.id.cbTeeth_10);
		cbTeeth_11 = findViewById(R.id.cbTeeth_11);
		cbTeeth_12 = findViewById(R.id.cbTeeth_12);
		cbTeeth_13 = findViewById(R.id.cbTeeth_13);
		cbTeeth_14 = findViewById(R.id.cbTeeth_14);
		cbTeeth_15 = findViewById(R.id.cbTeeth_15);
		cbTeeth_16 = findViewById(R.id.cbTeeth_16);
		cbTeeth_17 = findViewById(R.id.cbTeeth_17);
		cbTeeth_18 = findViewById(R.id.cbTeeth_18);
		cbTeeth_19 = findViewById(R.id.cbTeeth_19);
		cbTeeth_20 = findViewById(R.id.cbTeeth_20);
		cbTeeth_21 = findViewById(R.id.cbTeeth_21);
		cbTeeth_22 = findViewById(R.id.cbTeeth_22);
		cbTeeth_23 = findViewById(R.id.cbTeeth_23);
		cbTeeth_24 = findViewById(R.id.cbTeeth_24);
		cbTeeth_25 = findViewById(R.id.cbTeeth_25);
		cbTeeth_26 = findViewById(R.id.cbTeeth_26);
		cbTeeth_27 = findViewById(R.id.cbTeeth_27);
		cbTeeth_28 = findViewById(R.id.cbTeeth_28);
		cbTeeth_29 = findViewById(R.id.cbTeeth_29);
		cbTeeth_30 = findViewById(R.id.cbTeeth_30);
		cbTeeth_31 = findViewById(R.id.cbTeeth_31);
		cbTeeth_32 = findViewById(R.id.cbTeeth_32);

		tvTeeth_1 = findViewById(R.id.tvTeeth_1);
		tvTeeth_2 = findViewById(R.id.tvTeeth_2);
		tvTeeth_3 = findViewById(R.id.tvTeeth_3);
		tvTeeth_4 = findViewById(R.id.tvTeeth_4);
		tvTeeth_5 = findViewById(R.id.tvTeeth_5);
		tvTeeth_6 = findViewById(R.id.tvTeeth_6);
		tvTeeth_7 = findViewById(R.id.tvTeeth_7);
		tvTeeth_8 = findViewById(R.id.tvTeeth_8);
		tvTeeth_9 = findViewById(R.id.tvTeeth_9);
		tvTeeth_10 = findViewById(R.id.tvTeeth_10);
		tvTeeth_11 = findViewById(R.id.tvTeeth_11);
		tvTeeth_12 = findViewById(R.id.tvTeeth_12);
		tvTeeth_13 = findViewById(R.id.tvTeeth_13);
		tvTeeth_14 = findViewById(R.id.tvTeeth_14);
		tvTeeth_15 = findViewById(R.id.tvTeeth_15);
		tvTeeth_16 = findViewById(R.id.tvTeeth_16);
		tvTeeth_17 = findViewById(R.id.tvTeeth_17);
		tvTeeth_18 = findViewById(R.id.tvTeeth_18);
		tvTeeth_19 = findViewById(R.id.tvTeeth_19);
		tvTeeth_20 = findViewById(R.id.tvTeeth_20);
		tvTeeth_21 = findViewById(R.id.tvTeeth_21);
		tvTeeth_22 = findViewById(R.id.tvTeeth_22);
		tvTeeth_23 = findViewById(R.id.tvTeeth_23);
		tvTeeth_24 = findViewById(R.id.tvTeeth_24);
		tvTeeth_25 = findViewById(R.id.tvTeeth_25);
		tvTeeth_26 = findViewById(R.id.tvTeeth_26);
		tvTeeth_27 = findViewById(R.id.tvTeeth_27);
		tvTeeth_28 = findViewById(R.id.tvTeeth_28);
		tvTeeth_29 = findViewById(R.id.tvTeeth_29);
		tvTeeth_30 = findViewById(R.id.tvTeeth_30);
		tvTeeth_31 = findViewById(R.id.tvTeeth_31);
		tvTeeth_32 = findViewById(R.id.tvTeeth_32);

		ic_mike_LiveExtraInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				startVoiceRecognitionActivity(etLiveExtraInfo);
			}
		});

		cbWholeMouth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				openActivity.open(ActivityDental_AOC.class,false);
				/*if(isChecked){
					//sectionNames.clear();
					laySec_1.performClick();
					laySec_2.performClick();
					laySec_3.performClick();
					laySec_4.performClick();
				}else {
					laySec_1.performClick();
					laySec_2.performClick();
					laySec_3.performClick();
					laySec_4.performClick();
				}*/
			}
		});


		dentalSymptomBeans.clear();
		dentalSymptomBeans.add(new DentalSymptomBean("Discomfort"));
		dentalSymptomBeans.add(new DentalSymptomBean("Pressure"));
		dentalSymptomBeans.add(new DentalSymptomBean("Tingling"));
		dentalSymptomBeans.add(new DentalSymptomBean("Dull"));
		dentalSymptomBeans.add(new DentalSymptomBean("Pulsating"));
		dentalSymptomBeans.add(new DentalSymptomBean("Sharp"));
		dentalSymptomBeans.add(new DentalSymptomBean("Headache"));

		lvDentalSymptom.setAdapter(new LvDentalSymptomAdapter(activity, dentalSymptomBeans));
		lvDentalSymptom.setExpanded(true);
		lvDentalSymptom.setOnItemClickListener((parent, view, position, id) -> ((LvDentalSymptomAdapter)lvDentalSymptom.getAdapter()).notifyDataSetChanged());

		dentalPainLevelOptions = Arrays.asList(new String[]{"0", "1", "2", "3","4", "5", "6","7", "8", "9","10"});

		RvDentalPainLevelAdapter rvDentalPainLevelAdapter = new RvDentalPainLevelAdapter(activity, dentalPainLevelOptions);
		//RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
		//rvDentalPainLevel.setLayoutManager(mLayoutManager);
		rvDentalPainLevel.setLayoutManager(new GridLayoutManager(getApplicationContext(), 11));
		rvDentalPainLevel.setItemAnimator(new DefaultItemAnimator());
		rvDentalPainLevel.setAdapter(rvDentalPainLevelAdapter);

		rvDentalPainLevel.addOnItemTouchListener(new RecyclerItemClickListener(activity, rvDentalPainLevel, new RecyclerItemClickListener.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {

				selectedPainLevel = dentalPainLevelOptions.get(position);

				//spPainSeverity.setSelection(position);

				RvDentalPainLevelAdapter.selectedPos = position;

				rvDentalPainLevelAdapter.notifyDataSetChanged();
			}

			@Override
			public void onItemLongClick(View view, int position) {
				DATA.print("-- item long press pos: "+position);
			}
		}));




		dentalSymptomBeansSection1.clear();
		dentalSymptomBeansSection1.add(new DentalSymptomBean("Sharp Throbbing or Constant pain"));
		dentalSymptomBeansSection1.add(new DentalSymptomBean("Tooth pain (occurs only when pressure is applied)"));
		dentalSymptomBeansSection1.add(new DentalSymptomBean("Swollen gums"));
		dentalSymptomBeansSection1.add(new DentalSymptomBean("Bad taste in mouth"));
		dentalSymptomBeansSection1.add(new DentalSymptomBean("Fever"));
		dentalSymptomBeansSection1.add(new DentalSymptomBean("Headache"));
		dentalSymptomBeansSection1.add(new DentalSymptomBean("Burning or Shock like pain"));
		dentalSymptomBeansSection1.add(new DentalSymptomBean("Bleeding or Discharge from around a tooth or gums"));

		lvDentalSymptomSec1.setAdapter(new LvDentalSymptomAdapter2(activity, dentalSymptomBeansSection1));
		lvDentalSymptomSec1.setExpanded(true);
		lvDentalSymptomSec1.setOnItemClickListener((parent, view, position, id) -> ((LvDentalSymptomAdapter2)lvDentalSymptomSec1.getAdapter()).notifyDataSetChanged());


		dentalSymptomBeansSection2.clear();
		dentalSymptomBeansSection2.add(new DentalSymptomBean("Cavities (tooth decay)"));
		dentalSymptomBeansSection2.add(new DentalSymptomBean("Chipped or broken tooth"));
		dentalSymptomBeansSection2.add(new DentalSymptomBean("Infection of the root of the tooth"));
		dentalSymptomBeansSection2.add(new DentalSymptomBean("Damaged filling"));
		dentalSymptomBeansSection2.add(new DentalSymptomBean("Repitative motions (chewing or grinding)"));
		dentalSymptomBeansSection2.add(new DentalSymptomBean("Infected gums"));
		dentalSymptomBeansSection2.add(new DentalSymptomBean("Injury or truma to the area"));



		lvDentalSymptomSec2.setAdapter(new LvDentalSymptomAdapter2(activity, dentalSymptomBeansSection2));
		lvDentalSymptomSec2.setExpanded(true);
		lvDentalSymptomSec2.setOnItemClickListener((parent, view, position, id) -> ((LvDentalSymptomAdapter2)lvDentalSymptomSec2.getAdapter()).notifyDataSetChanged());


		dentalSymptomBeansSection3.clear();
		dentalSymptomBeansSection3.add(new DentalSymptomBean("Cold drinks or food"));
		dentalSymptomBeansSection3.add(new DentalSymptomBean("Sweet food"));
		dentalSymptomBeansSection3.add(new DentalSymptomBean("Bitting down on anything"));
		dentalSymptomBeansSection3.add(new DentalSymptomBean("Putting other pressure on the tooth"));

		lvDentalSymptomSec3.setAdapter(new LvDentalSymptomAdapter2(activity, dentalSymptomBeansSection3));
		lvDentalSymptomSec3.setExpanded(true);
		lvDentalSymptomSec3.setOnItemClickListener((parent, view, position, id) -> ((LvDentalSymptomAdapter2)lvDentalSymptomSec3.getAdapter()).notifyDataSetChanged());


		ImageView ivTeethSection1 = findViewById(R.id.ivTeethSection1);
		ImageView ivTeethSection2 = findViewById(R.id.ivTeethSection2);
		ImageView ivTeethSection3 = findViewById(R.id.ivTeethSection3);
		ImageView ivTeethSection4 = findViewById(R.id.ivTeethSection4);

		OnClickListener onClickListener = view -> {

			//dialog.dismiss();

			switch (view.getId()){

				case R.id.btnPre2:
					selectedChild = 0;
					if (selectedChild > vfDentalCareForm.getDisplayedChild()) {
						vfDentalCareForm.setInAnimation(activity, R.anim.in_right);
						vfDentalCareForm.setOutAnimation(activity, R.anim.out_left);
					} else {
						vfDentalCareForm.setInAnimation(activity, R.anim.in_left);
						vfDentalCareForm.setOutAnimation(activity, R.anim.out_right);
					}
					if (vfDentalCareForm.getDisplayedChild() != selectedChild) {
						vfDentalCareForm.setDisplayedChild(selectedChild);
					}
					break;
				case R.id.btnPre3:
					selectedChild = 1;
					if (selectedChild > vfDentalCareForm.getDisplayedChild()) {
						vfDentalCareForm.setInAnimation(activity, R.anim.in_right);
						vfDentalCareForm.setOutAnimation(activity, R.anim.out_left);
					} else {
						vfDentalCareForm.setInAnimation(activity, R.anim.in_left);
						vfDentalCareForm.setOutAnimation(activity, R.anim.out_right);
					}
					if (vfDentalCareForm.getDisplayedChild() != selectedChild) {
						vfDentalCareForm.setDisplayedChild(selectedChild);
					}
					break;

				case R.id.btnContinue:
					selectedChild = 1;
					if (selectedChild > vfDentalCareForm.getDisplayedChild()) {
						vfDentalCareForm.setInAnimation(activity, R.anim.in_right);
						vfDentalCareForm.setOutAnimation(activity, R.anim.out_left);
					} else {
						vfDentalCareForm.setInAnimation(activity, R.anim.in_left);
						vfDentalCareForm.setOutAnimation(activity, R.anim.out_right);
					}
					if (vfDentalCareForm.getDisplayedChild() != selectedChild) {
						vfDentalCareForm.setDisplayedChild(selectedChild);
					}
					break;

				case R.id.btnContinue2:
					selectedChild = 2;
					if (selectedChild > vfDentalCareForm.getDisplayedChild()) {
						vfDentalCareForm.setInAnimation(activity, R.anim.in_right);
						vfDentalCareForm.setOutAnimation(activity, R.anim.out_left);
					} else {
						vfDentalCareForm.setInAnimation(activity, R.anim.in_left);
						vfDentalCareForm.setOutAnimation(activity, R.anim.out_right);
					}
					if (vfDentalCareForm.getDisplayedChild() != selectedChild) {
						vfDentalCareForm.setDisplayedChild(selectedChild);
					}
					break;


				case R.id.laySec_1:

					String secName = tvSec1.getText().toString();
					if(sectionNames.contains(secName)){
						sectionNames.remove(secName);
						tvSec1.setTextColor(getResources().getColor(R.color.black));
						laySec_1.setBackgroundColor(getResources().getColor(R.color.dental_part_unselected));

						//ivTeeth1.setTag("1");ivTeeth2.setTag("1");ivTeeth3.setTag("1");ivTeeth4.setTag("1");ivTeeth5.setTag("1");ivTeeth6.setTag("1");ivTeeth7.setTag("1");ivTeeth8.setTag("1");
					}else {
						sectionNames.add(secName);
						tvSec1.setTextColor(getResources().getColor(android.R.color.white));
						laySec_1.setBackgroundResource(R.drawable.dental_part_selected_bg);

						//ivTeeth1.setTag("0");ivTeeth2.setTag("0");ivTeeth3.setTag("0");ivTeeth4.setTag("0");ivTeeth5.setTag("0");ivTeeth6.setTag("0");ivTeeth7.setTag("0");ivTeeth8.setTag("0");
					}
					//ivTeeth1.performClick();ivTeeth2.performClick();ivTeeth3.performClick();ivTeeth4.performClick();ivTeeth5.performClick();ivTeeth6.performClick();ivTeeth7.performClick();ivTeeth8.performClick();
					cbTeeth_1.setChecked(sectionNames.contains(secName));
					cbTeeth_2.setChecked(sectionNames.contains(secName));
					cbTeeth_3.setChecked(sectionNames.contains(secName));
					cbTeeth_4.setChecked(sectionNames.contains(secName));
					cbTeeth_5.setChecked(sectionNames.contains(secName));
					cbTeeth_6.setChecked(sectionNames.contains(secName));
					cbTeeth_7.setChecked(sectionNames.contains(secName));
					cbTeeth_8.setChecked(sectionNames.contains(secName));

					break;
				case R.id.laySec_2:
					String secName2 = tvSec2.getText().toString();
					if(sectionNames.contains(secName2)){
						sectionNames.remove(secName2);
						tvSec2.setTextColor(getResources().getColor(R.color.black));
						laySec_2.setBackgroundColor(getResources().getColor(R.color.dental_part_unselected));

						//ivTeeth9.setTag("1");ivTeeth10.setTag("1");ivTeeth11.setTag("1");ivTeeth12.setTag("1");ivTeeth13.setTag("1");ivTeeth14.setTag("1");ivTeeth15.setTag("1");ivTeeth16.setTag("1");
					}else {
						sectionNames.add(secName2);
						tvSec2.setTextColor(getResources().getColor(android.R.color.white));
						laySec_2.setBackgroundResource(R.drawable.dental_part_selected_bg);

						//ivTeeth9.setTag("0");ivTeeth10.setTag("0");ivTeeth11.setTag("0");ivTeeth12.setTag("0");ivTeeth13.setTag("0");ivTeeth14.setTag("0");ivTeeth15.setTag("0");ivTeeth16.setTag("0");
					}
					//ivTeeth9.performClick();ivTeeth10.performClick();ivTeeth11.performClick();ivTeeth12.performClick();ivTeeth13.performClick();ivTeeth14.performClick();ivTeeth15.performClick();ivTeeth16.performClick();

					cbTeeth_9.setChecked(sectionNames.contains(secName2));
					cbTeeth_10.setChecked(sectionNames.contains(secName2));
					cbTeeth_11.setChecked(sectionNames.contains(secName2));
					cbTeeth_12.setChecked(sectionNames.contains(secName2));
					cbTeeth_13.setChecked(sectionNames.contains(secName2));
					cbTeeth_14.setChecked(sectionNames.contains(secName2));
					cbTeeth_15.setChecked(sectionNames.contains(secName2));
					cbTeeth_16.setChecked(sectionNames.contains(secName2));
					break;
				case R.id.laySec_3:

					String secName3 = tvSec3.getText().toString();
					if(sectionNames.contains(secName3)){
						sectionNames.remove(secName3);
						tvSec3.setTextColor(getResources().getColor(R.color.black));
						laySec_3.setBackgroundColor(getResources().getColor(R.color.dental_part_unselected));
					}else {
						sectionNames.add(secName3);
						tvSec3.setTextColor(getResources().getColor(android.R.color.white));
						laySec_3.setBackgroundResource(R.drawable.dental_part_selected_bg);
					}

					cbTeeth_25.setChecked(sectionNames.contains(secName3));
					cbTeeth_26.setChecked(sectionNames.contains(secName3));
					cbTeeth_27.setChecked(sectionNames.contains(secName3));
					cbTeeth_28.setChecked(sectionNames.contains(secName3));
					cbTeeth_29.setChecked(sectionNames.contains(secName3));
					cbTeeth_30.setChecked(sectionNames.contains(secName3));
					cbTeeth_31.setChecked(sectionNames.contains(secName3));
					cbTeeth_32.setChecked(sectionNames.contains(secName3));
					break;
				case R.id.laySec_4:

					String secName4 = tvSec4.getText().toString();
					if(sectionNames.contains(secName4)){
						sectionNames.remove(secName4);
						tvSec4.setTextColor(getResources().getColor(R.color.black));
						laySec_4.setBackgroundColor(getResources().getColor(R.color.dental_part_unselected));
					}else {
						sectionNames.add(secName4);
						tvSec4.setTextColor(getResources().getColor(android.R.color.white));
						laySec_4.setBackgroundResource(R.drawable.dental_part_selected_bg);
					}

					cbTeeth_17.setChecked(sectionNames.contains(secName4));
					cbTeeth_18.setChecked(sectionNames.contains(secName4));
					cbTeeth_19.setChecked(sectionNames.contains(secName4));
					cbTeeth_20.setChecked(sectionNames.contains(secName4));
					cbTeeth_21.setChecked(sectionNames.contains(secName4));
					cbTeeth_22.setChecked(sectionNames.contains(secName4));
					cbTeeth_23.setChecked(sectionNames.contains(secName4));
					cbTeeth_24.setChecked(sectionNames.contains(secName4));

					break;

				case R.id.btnSubmit:
					submitDentalCareForm();
					break;


				case R.id.ivTeeth1:
					ivTeeth1.setImageResource(ivTeeth1.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth1.setTag(ivTeeth1.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth2:
					ivTeeth2.setImageResource(ivTeeth2.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth2.setTag(ivTeeth2.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth3:
					ivTeeth3.setImageResource(ivTeeth3.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth3.setTag(ivTeeth3.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth4:
					ivTeeth4.setImageResource(ivTeeth4.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth4.setTag(ivTeeth4.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth5:
					ivTeeth5.setImageResource(ivTeeth5.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth5.setTag(ivTeeth5.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth6:
					ivTeeth6.setImageResource(ivTeeth6.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth6.setTag(ivTeeth6.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth7:
					ivTeeth7.setImageResource(ivTeeth7.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth7.setTag(ivTeeth7.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth8:
					ivTeeth8.setImageResource(ivTeeth8.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth8.setTag(ivTeeth8.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth9:
					ivTeeth9.setImageResource(ivTeeth9.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth9.setTag(ivTeeth9.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth10:
					ivTeeth10.setImageResource(ivTeeth10.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth10.setTag(ivTeeth10.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth11:
					ivTeeth11.setImageResource(ivTeeth11.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth11.setTag(ivTeeth11.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth12:
					ivTeeth12.setImageResource(ivTeeth12.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth12.setTag(ivTeeth12.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth13:
					ivTeeth13.setImageResource(ivTeeth13.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth13.setTag(ivTeeth13.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth14:
					ivTeeth14.setImageResource(ivTeeth14.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth14.setTag(ivTeeth14.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth15:
					ivTeeth15.setImageResource(ivTeeth15.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth15.setTag(ivTeeth15.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth16:
					ivTeeth16.setImageResource(ivTeeth16.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth16.setTag(ivTeeth16.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth17:
					ivTeeth17.setImageResource(ivTeeth17.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth17.setTag(ivTeeth17.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth18:
					ivTeeth18.setImageResource(ivTeeth18.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth18.setTag(ivTeeth18.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth19:
					ivTeeth19.setImageResource(ivTeeth19.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth19.setTag(ivTeeth19.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth20:
					ivTeeth20.setImageResource(ivTeeth20.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth20.setTag(ivTeeth20.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth21:
					ivTeeth21.setImageResource(ivTeeth21.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth21.setTag(ivTeeth21.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth22:
					ivTeeth22.setImageResource(ivTeeth22.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth22.setTag(ivTeeth22.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth23:
					ivTeeth23.setImageResource(ivTeeth23.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth23.setTag(ivTeeth23.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth24:
					ivTeeth24.setImageResource(ivTeeth24.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth24.setTag(ivTeeth24.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth25:
					ivTeeth25.setImageResource(ivTeeth25.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth25.setTag(ivTeeth25.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth26:
					ivTeeth26.setImageResource(ivTeeth26.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth26.setTag(ivTeeth26.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth27:
					ivTeeth27.setImageResource(ivTeeth27.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth27.setTag(ivTeeth27.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth28:
					ivTeeth28.setImageResource(ivTeeth28.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth28.setTag(ivTeeth28.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth29:
					ivTeeth29.setImageResource(ivTeeth29.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth29.setTag(ivTeeth29.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;
				case R.id.ivTeeth30:
					ivTeeth30.setImageResource(ivTeeth30.getTag().toString().equalsIgnoreCase("0") ? R.drawable.cust_cir_blue_light : 0);
					ivTeeth30.setTag(ivTeeth30.getTag().toString().equalsIgnoreCase("0") ? "1" : "0");
					break;

				case R.id.ivTeethSection1:

					break;
				case R.id.ivTeethSection2:

					break;
				case R.id.ivTeethSection3:

					break;
				case R.id.ivTeethSection4:

					break;

			}
		};
		btnContinue.setOnClickListener(onClickListener);

		laySec_1.setOnClickListener(onClickListener);
		laySec_2.setOnClickListener(onClickListener);
		laySec_3.setOnClickListener(onClickListener);
		laySec_4.setOnClickListener(onClickListener);
		btnContinue2.setOnClickListener(onClickListener);
		//ivPrevSec1.setOnClickListener(onClickListener);
		btnSubmit.setOnClickListener(onClickListener);
		btnPre2.setOnClickListener(onClickListener);
		btnPre3.setOnClickListener(onClickListener);

		ivTeeth1.setOnClickListener(onClickListener);
		ivTeeth2.setOnClickListener(onClickListener);
		ivTeeth3.setOnClickListener(onClickListener);
		ivTeeth4.setOnClickListener(onClickListener);
		ivTeeth5.setOnClickListener(onClickListener);
		ivTeeth6.setOnClickListener(onClickListener);
		ivTeeth7.setOnClickListener(onClickListener);
		ivTeeth8.setOnClickListener(onClickListener);
		ivTeeth9.setOnClickListener(onClickListener);
		ivTeeth10.setOnClickListener(onClickListener);
		ivTeeth11.setOnClickListener(onClickListener);
		ivTeeth12.setOnClickListener(onClickListener);
		ivTeeth13.setOnClickListener(onClickListener);
		ivTeeth14.setOnClickListener(onClickListener);
		ivTeeth15.setOnClickListener(onClickListener);
		ivTeeth16.setOnClickListener(onClickListener);
		ivTeeth17.setOnClickListener(onClickListener);
		ivTeeth18.setOnClickListener(onClickListener);
		ivTeeth19.setOnClickListener(onClickListener);
		ivTeeth20.setOnClickListener(onClickListener);
		ivTeeth21.setOnClickListener(onClickListener);
		ivTeeth22.setOnClickListener(onClickListener);
		ivTeeth23.setOnClickListener(onClickListener);
		ivTeeth24.setOnClickListener(onClickListener);
		ivTeeth25.setOnClickListener(onClickListener);
		ivTeeth26.setOnClickListener(onClickListener);
		ivTeeth27.setOnClickListener(onClickListener);
		ivTeeth28.setOnClickListener(onClickListener);
		ivTeeth29.setOnClickListener(onClickListener);
		ivTeeth30.setOnClickListener(onClickListener);

		ivTeethSection1.setOnClickListener(onClickListener);
		ivTeethSection2.setOnClickListener(onClickListener);
		ivTeethSection3.setOnClickListener(onClickListener);
		ivTeethSection4.setOnClickListener(onClickListener);


		CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (buttonView, isChecked) -> {
			switch (buttonView.getId()){

				case R.id.cbTeeth_1:
					tvTeeth_1.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_2:
					tvTeeth_2.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_3:
					tvTeeth_3.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_4:
					tvTeeth_4.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_5:
					tvTeeth_5.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_6:
					tvTeeth_6.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_7:
					tvTeeth_7.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_8:
					tvTeeth_8.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_9:
					tvTeeth_9.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_10:
					tvTeeth_10.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_11:
					tvTeeth_11.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_12:
					tvTeeth_12.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_13:
					tvTeeth_13.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_14:
					tvTeeth_14.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_15:
					tvTeeth_15.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_16:
					tvTeeth_16.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_17:
					tvTeeth_17.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_18:
					tvTeeth_18.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_19:
					tvTeeth_19.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_20:
					tvTeeth_20.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_21:
					tvTeeth_21.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_22:
					tvTeeth_22.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_23:
					tvTeeth_23.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_24:
					tvTeeth_24.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_25:
					tvTeeth_25.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_26:
					tvTeeth_26.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_27:
					tvTeeth_27.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_28:
					tvTeeth_28.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_29:
					tvTeeth_29.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_30:
					tvTeeth_30.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_31:
					tvTeeth_31.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
				case R.id.cbTeeth_32:
					tvTeeth_32.setTextColor(isChecked ? getResources().getColor(R.color.dental_blue) : getResources().getColor(android.R.color.white));
					break;
			}
		};

		cbTeeth_1.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_2.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_3.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_4.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_5.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_6.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_7.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_8.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_9.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_10.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_11.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_12.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_13.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_14.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_15.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_16.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_17.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_18 .setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_19.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_20.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_21.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_22.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_23.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_24.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_25.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_26.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_27.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_28.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_29.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_30.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_31.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth_32.setOnCheckedChangeListener(onCheckedChangeListener);

	}







	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
			ArrayList<String> matches = data.getStringArrayListExtra(
					RecognizerIntent.EXTRA_RESULTS);
			// do whatever you want with the results
			if(this.editText != null){
				this.editText.setText(matches.get(0));
			}
		}

		super.onActivityResult(requestCode,resultCode,data);
	}


	EditText editText;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	private void startVoiceRecognitionActivity(EditText editText) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		// identifying your application to the Google service
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
		// hint in the dialog
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.app_name));
		// hint to the recognizer about what the user is going to say
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		// number of results
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		// recognition language
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-US");
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
		this.editText = editText;
	}



	ArrayList<String> symptomsMain = new ArrayList<>(),
			symptomsSection1 = new ArrayList<>(),
			symptomsSection2 = new ArrayList<>(),
			symptomsSection3 = new ArrayList<>();

	private void loadSecMain(){
		symptomsMain.clear();
		SparseBooleanArray checked = lvDentalSymptom.getCheckedItemPositions();
		DATA.print("--checked: "+checked);
		DATA.print("--checked size: "+checked.size());
		int c = 0;
		for (int i = 0; i < checked.size(); i++) {
			// Item position in adapter
			int position1 = checked.keyAt(i);
			// Add sport if it is checked i.e.) == TRUE!
			if (checked.valueAt(i)){
				DATA.print("--pos checked "+position1);
				c++;
				symptomsMain.add(dentalSymptomBeans.get(position1).symptomName);
			}
		}
	}
	private void loadSec1(){
		symptomsSection1.clear();
		SparseBooleanArray checked = lvDentalSymptomSec1.getCheckedItemPositions();
		DATA.print("--checked: "+checked);
		DATA.print("--checked size: "+checked.size());
		int c = 0;
		for (int i = 0; i < checked.size(); i++) {
			// Item position in adapter
			int position1 = checked.keyAt(i);
			// Add sport if it is checked i.e.) == TRUE!
			if (checked.valueAt(i)){
				DATA.print("--pos checked "+position1);
				c++;
				symptomsSection1.add(dentalSymptomBeansSection1.get(position1).symptomName);
			}
		}
	}
	private void loadSec2(){
		symptomsSection2.clear();
		SparseBooleanArray checked = lvDentalSymptomSec2.getCheckedItemPositions();
		DATA.print("--checked: "+checked);
		DATA.print("--checked size: "+checked.size());
		int c = 0;
		for (int i = 0; i < checked.size(); i++) {
			// Item position in adapter
			int position1 = checked.keyAt(i);
			// Add sport if it is checked i.e.) == TRUE!
			if (checked.valueAt(i)){
				DATA.print("--pos checked "+position1);
				c++;
				symptomsSection2.add(dentalSymptomBeansSection2.get(position1).symptomName);
			}
		}
	}
	private void loadSec3(){
		symptomsSection3.clear();
		SparseBooleanArray checked = lvDentalSymptomSec3.getCheckedItemPositions();
		DATA.print("--checked: "+checked);
		DATA.print("--checked size: "+checked.size());
		int c = 0;
		for (int i = 0; i < checked.size(); i++) {
			// Item position in adapter
			int position1 = checked.keyAt(i);
			// Add sport if it is checked i.e.) == TRUE!
			if (checked.valueAt(i)){
				DATA.print("--pos checked "+position1);
				c++;
				symptomsSection3.add(dentalSymptomBeansSection3.get(position1).symptomName);
			}
		}
	}


	public void submitDentalCareForm(){

		loadSecMain();loadSec1();loadSec2();loadSec3();


		openActivity.open(ActivityDentalDoctors.class, false);

	}



	private void getLiveCareCall(String payment_type) {

		StringBuilder sbCondition = new StringBuilder();

		sbCondition.append("Main Symptom : \n");
		for (int i = 0; i < symptomsMain.size(); i++) {
			sbCondition.append(symptomsMain.get(i));
			sbCondition.append(", ");
		}

		sbCondition.append("Signs and Symptoms : \n");
		for (int i = 0; i < symptomsSection1.size(); i++) {
			sbCondition.append(symptomsSection1.get(i));
			sbCondition.append(", ");
		}

		sbCondition.append("Causes of toothache : \n");
		for (int i = 0; i < symptomsSection2.size(); i++) {
			sbCondition.append(symptomsSection2.get(i));
			sbCondition.append(", ");
		}

		sbCondition.append("Toothache triggers : \n");
		for (int i = 0; i < symptomsSection3.size(); i++) {
			sbCondition.append(symptomsSection3.get(i));
			sbCondition.append(", ");
		}



		String levelOfPain = selectedPainLevel + " Out of 10";

		StringBuilder areaOfPain = new StringBuilder();
		for (int i = 0; i < sectionNames.size(); i++) {
			areaOfPain.append(sectionNames.get(i).replace("\n", " "));
			areaOfPain.append(", ");
		}

		String description = etLiveExtraInfo.getText().toString().trim();

		RequestParams params = new RequestParams();
		params.put("patient_id",prefs.getString("id", ""));
		params.put("sub_patient_id",prefs.getString("subPatientID", ""));
		params.put("symptom_id", "903");
		params.put("condition_id", sbCondition.toString());
		params.put("description",description);
		params.put("report_ids", "");
		params.put("doctor_id", DATA.doctorIdForLiveCare);
		params.put("pain_where", "");
		params.put("pain_severity", levelOfPain);
		params.put("pain_related",areaOfPain);

		params.put("ot_bp" , "");
		params.put("ot_hr" , "");
		params.put("ot_respirations" ,"");
		params.put("ot_saturation" , "");
		params.put("ot_blood_sugar" , "");
		params.put("ot_temperature" , "");
		params.put("ot_height" , "");
		params.put("ot_weight" , "");

		params.put("payment_type", payment_type);

		params.put("transaction_id", PaymentLiveCare.transaction_id);

		//if(prefs.getString("hospital_id","").equalsIgnoreCase(DATA.urgentCareHospitalID)){
		params.put("care_center_id", ActivityUrgentCareDoc.urgentCareCenterId);
		//}

		if (payment_type.equals("credit_card")){
			params.put("creditCardType", PaymentLiveCare.selectedCardType);
			params.put("creditCardNumber", PaymentLiveCare.cardNo);
			params.put("expDateMonth", PaymentLiveCare.selectedExpMonth);
			params.put("expDateYear", PaymentLiveCare.selectedExpYear);
			params.put("cvv2Number", PaymentLiveCare.cvvCode);
			params.put("total_amount", sharedPrefsHelper.get(PKG_AMOUNT, "5"));

		}else if (payment_type.equals("insurance")){

			params.put("creditCardType", PaymentLiveCare.selectedCardType);
			params.put("creditCardNumber", PaymentLiveCare.cardNo);
			params.put("expDateMonth", PaymentLiveCare.selectedExpMonth);
			params.put("expDateYear", PaymentLiveCare.selectedExpYear);
			params.put("cvv2Number", PaymentLiveCare.cvvCode);

			params.put("total_amount", LiveCareInsurance.coPayAmount);

			params.put("selected_insurance", LiveCareInsurance.selected_insurance);
		}


		/*if(images!=null){
			for (int i = 0; i < images.size(); i++) {
				try {
					*//*long tim = System.currentTimeMillis();
					File file = new File(images.get(i).path);
					String folderPath = file.getParent();
					String origFilePath = images.get(i).path;
					String ext = origFilePath.substring(origFilePath.lastIndexOf("."));

					File fileToSend = new File(folderPath, tim+"v_image_"+(i+1)+ext);
					boolean isRenamed = file.renameTo(fileToSend);
					if(isRenamed){
						params.put("v_image_"+(i+1),fileToSend);
						images.get(i).path = fileToSend.getAbsolutePath();
						DATA.print("--Renamed: "+ fileToSend.getPath()+" \nAbsPath"+fileToSend.getAbsolutePath());
					}else {
						params.put("v_image_"+(i+1),new File(images.get(i).path));
					}*//*

					params.put("v_image_"+(i+1),new File(images.get(i).path));
					DATA.print("--"+i+" filesize: "+new File(images.get(i).path).length());
					DATA.print("-- path: "+images.get(i).path);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			params.put("num_images",images.size()+"");
		}*/

		ApiManager apiManager = new ApiManager(ApiManager.ADD_LIVE_CHECKUP,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}


	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);

		if(apiName.equals(ApiManager.ADD_LIVE_CHECKUP)){

			DATA.isLiveCarePaymentDone = false;
			try {
				JSONObject jsonObject = new JSONObject(content);
				String status = jsonObject.getString("status");
				String msg = jsonObject.getString("msg");

				if (status.equalsIgnoreCase("error")) {
					customToast.showToast(msg, 0, 1);
					return;
				}

				DATA.liveCareIdForPayment = jsonObject.getInt("id");

				//if(prefs.getString("hospital_id","").equalsIgnoreCase(DATA.urgentCareHospitalID)){//jugaar --- need to refine


				String ucFlag = prefs.getString("after_urgentcare_form","");
				boolean ucFlagBool = ucFlag != null && ucFlag.equalsIgnoreCase("doctors");


				/*if(! ucFlagBool){//  true   noneed to check hospId for urgent care app
					if(!ActivityUrgentCareDoc.urgentCareCenterId.equalsIgnoreCase("a1")){
						DATA.NumOfReprtsSelected = 0;
						DATA.isReprtSelected = false;
						DATA.selectedReportIdsForApntmt = "";

						new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Info").
								setMessage("Your Immediate Care request has been submitted. It will be assigned to the doctor soon.")
								.setPositiveButton("Got It", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										//finish();
										checkUrgentCarePatientqueue();
									}
								}).setCancelable(false).show();
						return;
					}
				}*/

				String docName = "Your have applied for eLiveCare with "+jsonObject.getString("doctor_name");
				prefs.edit().putString("doctor_queue_msg", docName).commit();

				String docId_eLiveCare = jsonObject.optString("doctor_id");//urgent care--> no doc selected-->bugg due to hospital_id cond not matched
				prefs.edit().putString("docId_eLiveCare", docId_eLiveCare).commit();

				DATA.NumOfReprtsSelected = 0;
				DATA.isReprtSelected = false;
				DATA.selectedReportIdsForApntmt = "";

				if(status.equals("success")) {
					String patient_id = prefs.getString("id", "");
					String livecare_id = DATA.liveCareIdForPayment+"";
					String amount = sharedPrefsHelper.get(PKG_AMOUNT, "5");
					String payment_method = "paypal";
					String transaction_id = DATA.livecarePaymentPaypalInfo;
					//if (DATA.isFreeCare) {
					//DATA.isFreeCare = false;

					SharedPreferences.Editor ed = prefs.edit();
					ed.putBoolean("livecareTimerRunning", true);
					ed.putString("getLiveCareApptID", DATA.liveCareIdForPayment+"");
					ed.commit();

						/*Intent intent1 = new Intent();
						intent1.setAction("LIVE_CARE_WAITING_TIMER");
						sendBroadcast(intent1);*/


					ActivityDentalCareForm.callAPI = false;

					customToast.showToast("Registered for live care",0,0);
					openActivity.open(LiveCareWaitingArea.class, true);

					/*} else {
						//saveTransaction(patient_id, livecare_id, amount, payment_method, transaction_id);
					}*/
				}else {

					/*Intent intent1 = new Intent();
					intent1.setAction("LIVE_CARE_WAITING_TIMER");
					sendBroadcast(intent1);*/

					SharedPreferences.Editor ed = prefs.edit();
					ed.putString("getLiveCareApptID", jsonObject.getString("id"));
					ed.putBoolean("livecareTimerRunning", true);
					ed.commit();

					Toast.makeText(activity, "You have already applied for live care", Toast.LENGTH_LONG).show();
					openActivity.open(LiveCareWaitingArea.class, true);

				}
			} catch (JSONException e) {
				DATA.print("--Exception in getLiveCareCall: "+e);
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
	}
}
