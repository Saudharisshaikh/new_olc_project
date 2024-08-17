package com.app.OnlineCareTDC_Pt.b_health.assessment.new_assesment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.OnlineCareTDC_Pt.R;
import com.app.OnlineCareTDC_Pt.util.ExpandableHeightListView;

import java.util.ArrayList;

public class LvOCDformAdapter extends ArrayAdapter<OCDFormBean> {

	Activity activity;
	ArrayList<OCDFormBean> ocdFormBeans;

	public static boolean validateFlag = false;

	public LvOCDformAdapter(Activity activity, ArrayList<OCDFormBean> ocdFormBeans) {
		super(activity, R.layout.lv_ocd_form_row, ocdFormBeans);

		this.activity = activity;
		this.ocdFormBeans = ocdFormBeans;
	}

	static class ViewHolder {
		LinearLayout rootCell;
		TextView tvQuestion;//tvQuesDesc
		ExpandableHeightListView lvOptions;

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
			convertView = layoutInflater.inflate(R.layout.lv_ocd_form_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvQuestion = convertView.findViewById(R.id.tvQuestion);
			//viewHolder.tvQuesDesc = convertView.findViewById(R.id.tvQuesDesc);
			viewHolder.lvOptions = convertView.findViewById(R.id.lvOptions);
			viewHolder.rootCell = convertView.findViewById(R.id.rootCell);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvQuestion, viewHolder.tvQuestion);
			//convertView.setTag(R.id.tvQuesDesc, viewHolder.tvQuesDesc);
			convertView.setTag(R.id.lvOptions, viewHolder.lvOptions);
			convertView.setTag(R.id.rootCell, viewHolder.rootCell);

			//viewHolder.radios = new RadioButton[]{viewHolder.rb0, viewHolder.rb1, viewHolder.rb2, viewHolder.rb3};

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvQuestion.setText(ocdFormBeans.get(position).question);
		viewHolder.lvOptions.setAdapter(new LvOCDoptionAdapter(activity, ocdFormBeans.get(position)));
		viewHolder.lvOptions.setExpanded(true);

		//int vis = ActivityAdlForm.formFlagA == 1 ? View.VISIBLE : View.GONE;
		//viewHolder.tvQuesDesc.setVisibility(vis);

		if(validateFlag){
			int drawableId = ocdFormBeans.get(position).isAnswered ? R.drawable.cust_border_white_outline : R.drawable.cust_border_white_outline_red;
			//viewHolder.rootCell.setBackgroundColor(activity.getResources().getColor(colorId));
			viewHolder.rootCell.setBackgroundResource(drawableId);
		}

		return convertView;
	}
}
