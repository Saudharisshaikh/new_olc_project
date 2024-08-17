package com.onlinecare.b_health;

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

import com.covacard.R;

import java.util.List;

public class LvBeckDepAdapter extends ArrayAdapter<BDBean> {

	Activity activity;
	List<BDBean> bdBeans;

	public static boolean validateFlag = false;

	public LvBeckDepAdapter(Activity activity, List<BDBean> bdBeans) {
		super(activity, R.layout.lv_beck_dep_row, bdBeans);

		this.activity = activity;
		this.bdBeans = bdBeans;
	}

	static class ViewHolder {

		TextView tvQNo;
		RadioGroup rgBDOptions;
		RadioButton rb0,rb1,rb2,rb3;

		RadioButton [] radios;

		LinearLayout rootCell;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_beck_dep_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvQNo = convertView.findViewById(R.id.tvQNo);
			viewHolder.rgBDOptions = convertView.findViewById(R.id.rgBDOptions);
			viewHolder.rb0 = convertView.findViewById(R.id.rb0);
			viewHolder.rb1 = convertView.findViewById(R.id.rb1);
			viewHolder.rb2 = convertView.findViewById(R.id.rb2);
			viewHolder.rb3 = convertView.findViewById(R.id.rb3);
			viewHolder.rootCell = convertView.findViewById(R.id.rootCell);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvQNo, viewHolder.tvQNo);
			convertView.setTag(R.id.rgBDOptions, viewHolder.rgBDOptions);
			convertView.setTag(R.id.rb0, viewHolder.rb0);
			convertView.setTag(R.id.rb1, viewHolder.rb1);
			convertView.setTag(R.id.rb2, viewHolder.rb2);
			convertView.setTag(R.id.rb3, viewHolder.rb3);
			convertView.setTag(R.id.rootCell, viewHolder.rootCell);

			viewHolder.radios = new RadioButton[]{viewHolder.rb0, viewHolder.rb1, viewHolder.rb2, viewHolder.rb3};

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvQNo.setText((position+1) + "");

		//RadioButton [] radios = {viewHolder.rb0, viewHolder.rb1, viewHolder.rb2, viewHolder.rb3};

		//BDBean bdBean = bdBeans.get(position);

		//set checked moved from here to after listener

		viewHolder.rgBDOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
				switch (radioGroup.getCheckedRadioButtonId()){//checkedId
					case R.id.rb0:
						bdBeans.get(position).score = 0;
						bdBeans.get(position).seletedRadioIndex = 0;
						bdBeans.get(position).isGroupSelected = true;
						if(activity instanceof ActivityBecksDepression){
							((ActivityBecksDepression) activity).sumUpScore();
						}
						break;
					case R.id.rb1:
						bdBeans.get(position).score = 1;
						bdBeans.get(position).seletedRadioIndex = 1;
						bdBeans.get(position).isGroupSelected = true;
						if(activity instanceof ActivityBecksDepression){
							((ActivityBecksDepression) activity).sumUpScore();
						}
						break;
					case R.id.rb2:
						bdBeans.get(position).score = 2;
						bdBeans.get(position).seletedRadioIndex = 2;
						bdBeans.get(position).isGroupSelected = true;
						if(activity instanceof ActivityBecksDepression){
							((ActivityBecksDepression) activity).sumUpScore();
						}
						break;
					case R.id.rb3:
						bdBeans.get(position).score = 3;
						bdBeans.get(position).seletedRadioIndex = 3;
						bdBeans.get(position).isGroupSelected = true;
						if(activity instanceof ActivityBecksDepression){
							((ActivityBecksDepression) activity).sumUpScore();
						}
						break;
					default:

						bdBeans.get(position).score = 0;
						bdBeans.get(position).seletedRadioIndex = -1;
						bdBeans.get(position).isGroupSelected = false;
						if(activity instanceof ActivityBecksDepression){
							((ActivityBecksDepression) activity).sumUpScore();
						}
						break;
				}
			}
		});

		//final RadioButton [] radios = {viewHolder.rb0, viewHolder.rb1, viewHolder.rb2, viewHolder.rb3};

		for (int i = 0; i < bdBeans.get(position).bdOptions.size(); i++) {

			viewHolder.radios[i].setText(bdBeans.get(position).bdOptions.get(i));

			//boolean b = bdBeans.get(position).isGroupSelected && i == bdBeans.get(position).seletedRadioIndex;

			//System.out.println("-- pos : "+position+" flag: "+b);

            /*if(bdBeans.get(position).isGroupSelected && i == bdBeans.get(position).seletedRadioIndex){
				viewHolder.radios[i].setChecked(true);
            }else {
				viewHolder.radios[i].setChecked(false);
            }*/
		}


		int checkedRBId = getRadioBtnId(bdBeans.get(position).seletedRadioIndex);
		if((checkedRBId != 0) && bdBeans.get(position).isGroupSelected){
			viewHolder.rgBDOptions.check(checkedRBId);
		}else {
			viewHolder.rgBDOptions.clearCheck();
		}


		if(validateFlag){
			int drawableId = bdBeans.get(position).isGroupSelected ? R.drawable.cust_border_white_outline : R.drawable.cust_border_white_outline_red;
			viewHolder.rootCell.setBackgroundResource(drawableId);
		}

		if(activity instanceof ActivityBecksDepression){
			boolean clickAble = !((ActivityBecksDepression) activity).isEdit;
			viewHolder.rb0.setClickable(clickAble);
			viewHolder.rb0.setLongClickable(clickAble);
			viewHolder.rb1.setClickable(clickAble);
			viewHolder.rb1.setLongClickable(clickAble);
			viewHolder.rb2.setClickable(clickAble);
			viewHolder.rb2.setLongClickable(clickAble);
			viewHolder.rb3.setClickable(clickAble);
			viewHolder.rb3.setLongClickable(clickAble);
		}

		return convertView;
	}


	public int getRadioBtnId(int index){
		int rbId = 0;
		if(index == 0){
			rbId = R.id.rb0;
		}if(index == 1){
			rbId = R.id.rb1;
		}if(index == 2){
			rbId = R.id.rb2;
		}if(index == 3){
			rbId = R.id.rb3;
		}

		return rbId;
	}
}
