package com.app.msu_cp.reliance.assessment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.msu_cp.R;
import com.app.msu_cp.util.ExpandableHeightListView;

import java.util.ArrayList;

public class LvSDHSquestionAdapter extends ArrayAdapter<SDHSfieldBean.SDHSquestionBean> {

	Activity activity;
	ArrayList<SDHSfieldBean.SDHSquestionBean> sdhSquestionBeans;

	public static boolean validateFlag = false;

	public LvSDHSquestionAdapter(Activity activity, ArrayList<SDHSfieldBean.SDHSquestionBean> sdhSquestionBeans) {
		super(activity, R.layout.lv_sdhs_question_row, sdhSquestionBeans);

		this.activity = activity;
		this.sdhSquestionBeans = sdhSquestionBeans;
	}

	static class ViewHolder {
		LinearLayout rootCell;
		TextView tvSdhsQuestion;
		ExpandableHeightListView lvSDHSOptions;

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
			convertView = layoutInflater.inflate(R.layout.lv_sdhs_question_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvSdhsQuestion = convertView.findViewById(R.id.tvSdhsQuestion);
			viewHolder.lvSDHSOptions = convertView.findViewById(R.id.lvSDHSOptions);
			viewHolder.rootCell = convertView.findViewById(R.id.rootCell);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvSdhsQuestion, viewHolder.tvSdhsQuestion);
			convertView.setTag(R.id.lvSDHSOptions, viewHolder.lvSDHSOptions);

			convertView.setTag(R.id.rootCell, viewHolder.rootCell);

			//viewHolder.radios = new RadioButton[]{viewHolder.rb0, viewHolder.rb1, viewHolder.rb2, viewHolder.rb3};

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvSdhsQuestion.setText(sdhSquestionBeans.get(position).question);
		viewHolder.lvSDHSOptions.setAdapter(new LvSDHSoptionAdapter(activity, sdhSquestionBeans.get(position)));
		viewHolder.lvSDHSOptions.setExpanded(true);


		if(validateFlag){
			int colorId = sdhSquestionBeans.get(position).isAnswered ? android.R.color.white : R.color.theme_red_opaque_40;
			viewHolder.rootCell.setBackgroundColor(activity.getResources().getColor(colorId));
		}

		return convertView;
	}
}
