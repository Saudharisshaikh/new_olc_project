package com.app.covacard.b_health.assessment;

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

import com.app.covacard.R;

import java.util.List;

public class LvPHQformAdapter extends ArrayAdapter<PHQfieldBean> {

	Activity activity;
	List<PHQfieldBean> phQfieldBeans;

	public static boolean validateFlag = false;

	public LvPHQformAdapter(Activity activity, List<PHQfieldBean> phQfieldBeans) {
		super(activity, R.layout.lv_phq_form_row, phQfieldBeans);

		this.activity = activity;
		this.phQfieldBeans = phQfieldBeans;
	}

	static class ViewHolder {

		TextView tvQuestion;
		RadioGroup rgPHQOptions;
		RadioButton rb0,rb1,rb2,rb3;

		RadioButton [] radios;

		LinearLayout rootCell;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_phq_form_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvQuestion = convertView.findViewById(R.id.tvQuestion);
			viewHolder.rgPHQOptions = convertView.findViewById(R.id.rgPHQOptions);
			viewHolder.rb0 = convertView.findViewById(R.id.rb0);
			viewHolder.rb1 = convertView.findViewById(R.id.rb1);
			viewHolder.rb2 = convertView.findViewById(R.id.rb2);
			viewHolder.rb3 = convertView.findViewById(R.id.rb3);
			viewHolder.rootCell = convertView.findViewById(R.id.rootCell);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvQuestion, viewHolder.tvQuestion);
			convertView.setTag(R.id.rgPHQOptions, viewHolder.rgPHQOptions);
			convertView.setTag(R.id.rb0, viewHolder.rb0);
			convertView.setTag(R.id.rb1, viewHolder.rb1);
			convertView.setTag(R.id.rb2, viewHolder.rb2);
			convertView.setTag(R.id.rb3, viewHolder.rb3);
			convertView.setTag(R.id.rootCell, viewHolder.rootCell);

			viewHolder.radios = new RadioButton[]{viewHolder.rb0, viewHolder.rb1, viewHolder.rb2, viewHolder.rb3};

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvQuestion.setText(phQfieldBeans.get(position).question);


		//set checked moved from here to after listener

		viewHolder.rgPHQOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
				switch (radioGroup.getCheckedRadioButtonId()){//checkedId
					case R.id.rb0:
						phQfieldBeans.get(position).score = 0;
						phQfieldBeans.get(position).seletedRadioIndex = 0;
						phQfieldBeans.get(position).isGroupSelected = true;
						if(activity instanceof ActivityPHQ_Form){
							((ActivityPHQ_Form) activity).sumUpScore();
						}
						break;
					case R.id.rb1:
						phQfieldBeans.get(position).score = 1;
						phQfieldBeans.get(position).seletedRadioIndex = 1;
						phQfieldBeans.get(position).isGroupSelected = true;
						if(activity instanceof ActivityPHQ_Form){
							((ActivityPHQ_Form) activity).sumUpScore();
						}
						break;
					case R.id.rb2:
						phQfieldBeans.get(position).score = 2;
						phQfieldBeans.get(position).seletedRadioIndex = 2;
						phQfieldBeans.get(position).isGroupSelected = true;
						if(activity instanceof ActivityPHQ_Form){
							((ActivityPHQ_Form) activity).sumUpScore();
						}
						break;
					case R.id.rb3:
						phQfieldBeans.get(position).score = 3;
						phQfieldBeans.get(position).seletedRadioIndex = 3;
						phQfieldBeans.get(position).isGroupSelected = true;
						if(activity instanceof ActivityPHQ_Form){
							((ActivityPHQ_Form) activity).sumUpScore();
						}
						break;
					default:

						phQfieldBeans.get(position).score = 0;
						phQfieldBeans.get(position).seletedRadioIndex = -1;
						phQfieldBeans.get(position).isGroupSelected = false;
						if(activity instanceof ActivityPHQ_Form){
							((ActivityPHQ_Form) activity).sumUpScore();
						}
						break;
				}
			}
		});

		//final RadioButton [] radios = {viewHolder.rb0, viewHolder.rb1, viewHolder.rb2, viewHolder.rb3};

		for (int i = 0; i < phQfieldBeans.get(position).options.size(); i++) {

			viewHolder.radios[i].setText(phQfieldBeans.get(position).options.get(i));

			//boolean b = bdBeans.get(position).isGroupSelected && i == bdBeans.get(position).seletedRadioIndex;

			//System.out.println("-- pos : "+position+" flag: "+b);

            /*if(bdBeans.get(position).isGroupSelected && i == bdBeans.get(position).seletedRadioIndex){
				viewHolder.radios[i].setChecked(true);
            }else {
				viewHolder.radios[i].setChecked(false);
            }*/
		}


		int checkedRBId = getRadioBtnId(phQfieldBeans.get(position).seletedRadioIndex);
		if((checkedRBId != 0) && phQfieldBeans.get(position).isGroupSelected){
			viewHolder.rgPHQOptions.check(checkedRBId);
		}else {
			viewHolder.rgPHQOptions.clearCheck();
		}


		if(validateFlag){
			int drawableId = phQfieldBeans.get(position).isGroupSelected ? R.drawable.cust_border_white_outline : R.drawable.cust_border_white_outline_red;
			viewHolder.rootCell.setBackgroundResource(drawableId);
		}

		if(activity instanceof ActivityPHQ_Form){
			boolean clickAble = !((ActivityPHQ_Form) activity).isReadOnly;
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
