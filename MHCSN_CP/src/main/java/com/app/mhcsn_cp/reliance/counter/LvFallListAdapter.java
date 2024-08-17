package com.app.mhcsn_cp.reliance.counter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.util.DATA;

import java.util.ArrayList;

public class LvFallListAdapter extends ArrayAdapter<FallListBean> {

	Activity activity;
	ArrayList<FallListBean> fallListBeans;


	public LvFallListAdapter(Activity activity, ArrayList<FallListBean> fallListBeans) {
		super(activity, R.layout.lv_fall_list_row, fallListBeans);

		this.activity = activity;
		this.fallListBeans = fallListBeans;
	}

	static class ViewHolder {
		LinearLayout rootCell;
		TextView tvFallListDate,tvFallListFallDate,tvFallListInjury,tvFallListDesc, tvFallListMedCare,tvFallListInjuryResult;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_fall_list_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvFallListDate = convertView.findViewById(R.id.tvFallListDate);
			viewHolder.tvFallListFallDate = convertView.findViewById(R.id.tvFallListFallDate);
			viewHolder.tvFallListInjury = convertView.findViewById(R.id.tvFallListInjury);
			viewHolder.tvFallListDesc = convertView.findViewById(R.id.tvFallListDesc);
			viewHolder.tvFallListMedCare = convertView.findViewById(R.id.tvFallListMedCare);
			viewHolder.tvFallListInjuryResult = convertView.findViewById(R.id.tvFallListInjuryResult);
			viewHolder.rootCell = convertView.findViewById(R.id.rootCell);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvFallListDate, viewHolder.tvFallListDate);
			convertView.setTag(R.id.tvFallListFallDate, viewHolder.tvFallListFallDate);
			convertView.setTag(R.id.tvFallListInjury, viewHolder.tvFallListInjury);
			convertView.setTag(R.id.tvFallListDesc, viewHolder.tvFallListDesc);
			convertView.setTag(R.id.tvFallListMedCare, viewHolder.tvFallListMedCare);
			convertView.setTag(R.id.tvFallListInjuryResult, viewHolder.tvFallListInjuryResult);
			convertView.setTag(R.id.rootCell, viewHolder.rootCell);

			//viewHolder.radios = new RadioButton[]{viewHolder.rb0, viewHolder.rb1, viewHolder.rb2, viewHolder.rb3};

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvFallListDate.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Report Date : </font>"+ fallListBeans.get(position).dateof));
		viewHolder.tvFallListFallDate.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Fall Date : </font>"+ fallListBeans.get(position).fall_date));
		String isInjury = "No";
		if(fallListBeans.get(position).is_injury != null){
			isInjury = fallListBeans.get(position).is_injury.equalsIgnoreCase("1") ? "Yes" : "No";
		}
		viewHolder.tvFallListInjury.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Injury : </font>"+ isInjury));
		viewHolder.tvFallListDesc.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Additional Comments : </font>"+ fallListBeans.get(position).description));




		String isInjuryResult = "No";
		if(fallListBeans.get(position).injury_result != null){
			isInjuryResult = fallListBeans.get(position).injury_result.equalsIgnoreCase("1") ? "Yes" : "No";
		}
		viewHolder.tvFallListMedCare.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Medical Care Received : </font>"+ fallListBeans.get(position).medical_care));
		viewHolder.tvFallListInjuryResult.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Did the injury result in a hospitalization : </font>"+ isInjuryResult));

		if(fallListBeans.get(position).is_injury != null && fallListBeans.get(position).is_injury.equalsIgnoreCase("1")){
			viewHolder.tvFallListMedCare.setVisibility(View.VISIBLE);
			viewHolder.tvFallListInjuryResult.setVisibility(View.VISIBLE);
		}else {
			viewHolder.tvFallListMedCare.setVisibility(View.GONE);
			viewHolder.tvFallListInjuryResult.setVisibility(View.GONE);
		}

		return convertView;
	}

}
