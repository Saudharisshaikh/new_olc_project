package com.app.OnlineCareUS_Pt.b_health.assessment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.app.OnlineCareUS_Pt.R;

import java.util.List;

public class DastFormAdapter extends ArrayAdapter<DASTfieldBean> {

	Activity activity;
	List<DASTfieldBean> dasTfieldBeans;

	public static boolean validateFlag = false;

	public DastFormAdapter(Activity activity , List<DASTfieldBean> dasTfieldBeans) {
		super(activity, R.layout.lv_dast_form_row,dasTfieldBeans);
		this.activity = activity;
		this.dasTfieldBeans = dasTfieldBeans;
	}

	static class ViewHolder {
		TextView tvDastQues;
		RadioGroup rgDastAns;
		RadioButton radioYes,radioNo;
		LinearLayout layDastFormCell;
	}

	ViewHolder viewHolder = null;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_dast_form_row, null);

			viewHolder = new ViewHolder();
			viewHolder.tvDastQues= (TextView) convertView.findViewById(R.id.tvDastQues);
			viewHolder.rgDastAns = (RadioGroup) convertView.findViewById(R.id.rgDastAns);
			viewHolder.radioYes = (RadioButton) convertView.findViewById(R.id.radioYes);
			viewHolder.radioNo = (RadioButton) convertView.findViewById(R.id.radioNo);
			viewHolder.layDastFormCell = convertView.findViewById(R.id.layDastFormCell);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvDastQues, viewHolder.tvDastQues);
			convertView.setTag(R.id.rgDastAns,viewHolder.rgDastAns);
			convertView.setTag(R.id.radioYes,viewHolder.radioYes);
			convertView.setTag(R.id.radioNo,viewHolder.radioNo);
			convertView.setTag(R.id.layDastFormCell,viewHolder.layDastFormCell);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvDastQues.setText(dasTfieldBeans.get(position).question);

		viewHolder.rgDastAns.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {

						if (checkedId == R.id.radioNo) {
							dasTfieldBeans.get(position).scoreDerived = dasTfieldBeans.get(position).No;
							dasTfieldBeans.get(position).isGroupSelected = true;
						}else if (checkedId == R.id.radioYes) {
							dasTfieldBeans.get(position).scoreDerived = dasTfieldBeans.get(position).Yes;
							dasTfieldBeans.get(position).isGroupSelected = true;
						}else {
							dasTfieldBeans.get(position).isGroupSelected = false;
						}

						if(activity instanceof ActivityDAST_Form){
							((ActivityDAST_Form)activity).sumUpScore();
						}
					}

				});

		if (dasTfieldBeans.get(position).isGroupSelected && dasTfieldBeans.get(position).scoreDerived == dasTfieldBeans.get(position).No){
			viewHolder.radioNo.setChecked(true);
		}else if (dasTfieldBeans.get(position).isGroupSelected && dasTfieldBeans.get(position).scoreDerived == dasTfieldBeans.get(position).Yes){
			viewHolder.radioYes.setChecked(true);
		}else {
			viewHolder.rgDastAns.clearCheck();
		}

		if(validateFlag){
			int drawableId = dasTfieldBeans.get(position).isGroupSelected ? R.drawable.cust_border_white_outline : R.drawable.cust_border_white_outline_red;
			viewHolder.layDastFormCell.setBackgroundResource(drawableId);
		}


		if(activity instanceof ActivityDAST_Form){
			boolean clickAble = !((ActivityDAST_Form) activity).isReadOnly;
			viewHolder.radioYes.setClickable(clickAble);
			viewHolder.radioYes.setLongClickable(clickAble);
			viewHolder.radioNo.setClickable(clickAble);
			viewHolder.radioNo.setLongClickable(clickAble);
		}

		return convertView;
	}

}
