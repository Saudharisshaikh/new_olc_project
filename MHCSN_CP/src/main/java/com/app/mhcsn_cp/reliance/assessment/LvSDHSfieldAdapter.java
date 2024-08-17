package com.app.mhcsn_cp.reliance.assessment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.util.ExpandableHeightListView;

import java.util.ArrayList;

public class LvSDHSfieldAdapter extends ArrayAdapter<SDHSfieldBean> {

	Activity activity;
	ArrayList<SDHSfieldBean> sdhSfieldBeans;

	//public static boolean validateFlag = false;

	public LvSDHSfieldAdapter(Activity activity, ArrayList<SDHSfieldBean> sdhSfieldBeans) {
		super(activity, R.layout.lv_sdhs_form_row, sdhSfieldBeans);

		this.activity = activity;
		this.sdhSfieldBeans = sdhSfieldBeans;
	}

	static class ViewHolder {
		LinearLayout rootCell;
		TextView tvCatName;
		ExpandableHeightListView lvSDHSQuestion;

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
			convertView = layoutInflater.inflate(R.layout.lv_sdhs_form_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvCatName = convertView.findViewById(R.id.tvCatName);
			viewHolder.lvSDHSQuestion = convertView.findViewById(R.id.lvSDHSQuestion);
			viewHolder.rootCell = convertView.findViewById(R.id.rootCell);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvCatName, viewHolder.tvCatName);
			convertView.setTag(R.id.lvSDHSQuestion, viewHolder.lvSDHSQuestion);

			convertView.setTag(R.id.rootCell, viewHolder.rootCell);

			//viewHolder.radios = new RadioButton[]{viewHolder.rb0, viewHolder.rb1, viewHolder.rb2, viewHolder.rb3};

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvCatName.setText(sdhSfieldBeans.get(position).category);
		viewHolder.lvSDHSQuestion.setAdapter(new LvSDHSquestionAdapter(activity, sdhSfieldBeans.get(position).questions));
		viewHolder.lvSDHSQuestion.setExpanded(true);


		return convertView;
	}
}
