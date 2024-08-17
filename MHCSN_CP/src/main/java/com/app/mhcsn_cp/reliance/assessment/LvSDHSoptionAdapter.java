package com.app.mhcsn_cp.reliance.assessment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.util.DATA;

import java.util.ArrayList;

public class LvSDHSoptionAdapter extends ArrayAdapter<SDHSfieldBean.SDHSoptionBean> {

	Activity activity;
	SDHSfieldBean.SDHSquestionBean sdhSquestionBean;//from parent listview
	ArrayList<SDHSfieldBean.SDHSoptionBean> sdhSoptionBeans;

	//public static boolean validateFlag = false;

	public LvSDHSoptionAdapter(Activity activity, SDHSfieldBean.SDHSquestionBean sdhSquestionBean) {
		super(activity, R.layout.lv_sdhs_option_row, sdhSquestionBean.options);

		this.activity = activity;
		this.sdhSoptionBeans = sdhSquestionBean.options;
		this.sdhSquestionBean = sdhSquestionBean;
	}

	static class ViewHolder {
		LinearLayout rootCell;
		RadioButton rbSdhsOption;
		Spinner spSdhsOption;
		CheckBox cbSdhsOption;

		/*TextView tvQuestion;
		RadioGroup rgPHQOptions;
		RadioButton rb0,rb1,rb2,rb3;
		RadioButton [] radios;*/
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_sdhs_option_row, null);

			viewHolder = new ViewHolder();

			viewHolder.rbSdhsOption = convertView.findViewById(R.id.rbSdhsOption);
			viewHolder.spSdhsOption = convertView.findViewById(R.id.spSdhsOption);
			viewHolder.cbSdhsOption = convertView.findViewById(R.id.cbSdhsOption);
			viewHolder.rootCell = convertView.findViewById(R.id.rootCell);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.rbSdhsOption, viewHolder.rbSdhsOption);
			convertView.setTag(R.id.spSdhsOption, viewHolder.spSdhsOption);
			convertView.setTag(R.id.cbSdhsOption, viewHolder.cbSdhsOption);
			convertView.setTag(R.id.rootCell, viewHolder.rootCell);

			//viewHolder.radios = new RadioButton[]{viewHolder.rb0, viewHolder.rb1, viewHolder.rb2, viewHolder.rb3};

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.rbSdhsOption.setText(sdhSoptionBeans.get(position).key);

		if(sdhSoptionBeans.get(position).is_multi == 1){

			viewHolder.spSdhsOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int positionSp, long id) {
					sdhSoptionBeans.get(position).selectedSpinnerValue = sdhSoptionBeans.get(position).arr.get(positionSp);

					DATA.print("-- JJ bilo :P");
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {

				}
			});
			ArrayAdapter<String> spOpAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, sdhSoptionBeans.get(position).arr);
			viewHolder.spSdhsOption.setAdapter(spOpAdapter);

			//for edit
			if(activity instanceof ActivitySDHS_Form && ((ActivitySDHS_Form)activity).isEdit){
				for (int i = 0; i < sdhSoptionBeans.get(position).arr.size(); i++) {
					if(sdhSoptionBeans.get(position).arr.get(i).equalsIgnoreCase(sdhSoptionBeans.get(position).selectedSpinnerValue)){
						try {
							viewHolder.spSdhsOption.setSelection(i);
						}catch (Exception e){
							e.printStackTrace();
						}
						break;
					}
				}
			}
			//for edit

			int vis = sdhSoptionBeans.get(position).isSelected ? View.VISIBLE : View.GONE;
			viewHolder.spSdhsOption.setVisibility(vis);

		}else {
			viewHolder.spSdhsOption.setVisibility(View.GONE);
		}


		viewHolder.rbSdhsOption.setChecked(sdhSoptionBeans.get(position).isSelected);
		viewHolder.rbSdhsOption.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				resetAllAndSelect(position);
			}
		});



		viewHolder.cbSdhsOption.setText(sdhSoptionBeans.get(position).key);
		viewHolder.cbSdhsOption.setChecked(sdhSoptionBeans.get(position).isSelected);

		ViewHolder finalViewHolder = viewHolder;
		viewHolder.cbSdhsOption.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectFromCheckbox(position, finalViewHolder.cbSdhsOption.isChecked());
			}
		});

		if(sdhSquestionBean.question.contains(ActivitySDHS_Form.QUES_TYPE_CHAECKBOX)){
			viewHolder.cbSdhsOption.setVisibility(View.VISIBLE);
			viewHolder.rbSdhsOption.setVisibility(View.GONE);
		}else {
			viewHolder.cbSdhsOption.setVisibility(View.GONE);
			viewHolder.rbSdhsOption.setVisibility(View.VISIBLE);
		}

		return convertView;
	}


	public void resetAllAndSelect(int pos){
		for (int i = 0; i < sdhSoptionBeans.size(); i++) {
			sdhSoptionBeans.get(i).isSelected = false;
		}
		sdhSoptionBeans.get(pos).isSelected = true;
		sdhSquestionBean.isAnswered = true;
		notifyDataSetChanged();
	}

	public void selectFromCheckbox(int pos, boolean isChecked){
		/*for (int i = 0; i < sdhSoptionBeans.size(); i++) {
			sdhSoptionBeans.get(i).isSelected = false;
		}*/
		//None of the above
		if(sdhSoptionBeans.get(pos).key.contains("None of the above") && isChecked){//reset all cbs
			for (int i = 0; i < sdhSoptionBeans.size(); i++) {
				sdhSoptionBeans.get(i).isSelected = false;
			}
		}else{
			boolean is_oneCheked = false;
			for (int i = 0; i < sdhSoptionBeans.size(); i++) {
				if(sdhSoptionBeans.get(i).isSelected){
					is_oneCheked = true;
				}

				if(is_oneCheked && sdhSoptionBeans.get(i).key.contains("None of the above")){
					sdhSoptionBeans.get(i).isSelected = false;
					//sdhSoptionBeans.get(i).isSelected = !sdhSoptionBeans.get(i).isSelected;
				}
			}
		}


		sdhSoptionBeans.get(pos).isSelected = isChecked;//assign from  user input


		//uncheck none of the above cb
		/*boolean is_oneCheked = false;
		for (int i = 0; i < sdhSoptionBeans.size(); i++) {
			if(sdhSoptionBeans.get(i).isSelected){
				is_oneCheked = true;
			}

			if(is_oneCheked && sdhSoptionBeans.get(i).key.contains("None of the above")){
				//sdhSoptionBeans.get(i).isSelected = false;
				sdhSoptionBeans.get(i).isSelected = !sdhSoptionBeans.get(i).isSelected;
			}
		}*/
		//uncheck none of the above cb ends


		sdhSquestionBean.isAnswered = false;
		for (int i = 0; i < sdhSoptionBeans.size(); i++) {
			if(sdhSoptionBeans.get(i).isSelected){
				sdhSquestionBean.isAnswered = true;
			}
		}
		notifyDataSetChanged();
	}
}
