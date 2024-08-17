package com.app.mhcsn_cp.reliance.preschistory;

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

import java.util.List;

public class PrescHistoryAdapter extends ArrayAdapter<PrescHistoryBean> {

	Activity activity;
	List<PrescHistoryBean> prescHistoryBeans;


	public PrescHistoryAdapter(Activity activity, List<PrescHistoryBean> prescHistoryBeans) {
		super(activity, R.layout.lv_presc_history_row, prescHistoryBeans);

		this.activity = activity;
		this.prescHistoryBeans = prescHistoryBeans;
	}

	static class ViewHolder {
		LinearLayout rootCell;
		TextView tvPrescDrugDesc,tvPrescLastFillDate,tvPrescDaysSupp,tvPrescQty,tvPrescNote,tvPrescPharm;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_presc_history_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvPrescDrugDesc = convertView.findViewById(R.id.tvPrescDrugDesc);
			viewHolder.tvPrescLastFillDate = convertView.findViewById(R.id.tvPrescLastFillDate);
			viewHolder.tvPrescDaysSupp = convertView.findViewById(R.id.tvPrescDaysSupp);
			viewHolder.tvPrescQty = convertView.findViewById(R.id.tvPrescQty);
			viewHolder.tvPrescNote = convertView.findViewById(R.id.tvPrescNote);
			viewHolder.tvPrescPharm = convertView.findViewById(R.id.tvPrescPharm);
			viewHolder.rootCell = convertView.findViewById(R.id.rootCell);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvPrescDrugDesc, viewHolder.tvPrescDrugDesc);
			convertView.setTag(R.id.tvPrescLastFillDate, viewHolder.tvPrescLastFillDate);
			convertView.setTag(R.id.tvPrescDaysSupp, viewHolder.tvPrescDaysSupp);
			convertView.setTag(R.id.tvPrescQty, viewHolder.tvPrescQty);
			convertView.setTag(R.id.tvPrescNote, viewHolder.tvPrescNote);
			convertView.setTag(R.id.tvPrescPharm, viewHolder.tvPrescPharm);
			convertView.setTag(R.id.rootCell, viewHolder.rootCell);

			//viewHolder.radios = new RadioButton[]{viewHolder.rb0, viewHolder.rb1, viewHolder.rb2, viewHolder.rb3};

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvPrescDrugDesc.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>"+(position+1)+") Drug Description : </font>"+ prescHistoryBeans.get(position).drugDescription));
		viewHolder.tvPrescLastFillDate.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Last Fill Date : </font>"+ prescHistoryBeans.get(position).last_fill_date));
		viewHolder.tvPrescDaysSupp.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Days Supply : </font>"+ prescHistoryBeans.get(position).days_supply));
		viewHolder.tvPrescQty.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Quantity : </font>"+ prescHistoryBeans.get(position).quantity));
		viewHolder.tvPrescNote.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Note : </font>"+ prescHistoryBeans.get(position).note));
		viewHolder.tvPrescPharm.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Pharmacy : </font>"+ prescHistoryBeans.get(position).pharmacy));


		return convertView;
	}

}
