package com.app.mdlive_cp.reliance.counter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.mdlive_cp.R;
import com.app.mdlive_cp.util.DATA;

import java.util.ArrayList;

public class LvEmergRoomListAdapter extends ArrayAdapter<EmergRoomListBean> {

	Activity activity;
	ArrayList<EmergRoomListBean> emergRoomListBeans;


	public LvEmergRoomListAdapter(Activity activity, ArrayList<EmergRoomListBean> emergRoomListBeans) {
		super(activity, R.layout.lv_emergroom_list_row, emergRoomListBeans);

		this.activity = activity;
		this.emergRoomListBeans = emergRoomListBeans;
	}

	static class ViewHolder {
		LinearLayout rootCell;
		TextView tvEmergRoomListDate,tvEmergRoomListERDate,tvEmergRoomListDesc,tvEmergRoomListHospital,tvEmergRoomListAdmObserv,tvEmergRoomListAvoidableER,
				tvEmergRoomListTypeER,tvEmergRoomListAddInfo,tvEmergRoomListFacName;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_emergroom_list_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvEmergRoomListDate = convertView.findViewById(R.id.tvEmergRoomListDate);
			viewHolder.tvEmergRoomListERDate = convertView.findViewById(R.id.tvEmergRoomListERDate);
			viewHolder.tvEmergRoomListDesc = convertView.findViewById(R.id.tvEmergRoomListDesc);
			viewHolder.tvEmergRoomListHospital = convertView.findViewById(R.id.tvEmergRoomListHospital);
			viewHolder.tvEmergRoomListAdmObserv = convertView.findViewById(R.id.tvEmergRoomListAdmObserv);
			viewHolder.tvEmergRoomListAvoidableER = convertView.findViewById(R.id.tvEmergRoomListAvoidableER);
			viewHolder.tvEmergRoomListTypeER = convertView.findViewById(R.id.tvEmergRoomListTypeER);
			viewHolder.tvEmergRoomListFacName = convertView.findViewById(R.id.tvEmergRoomListFacName);
			viewHolder.tvEmergRoomListAddInfo = convertView.findViewById(R.id.tvEmergRoomListAddInfo);
			viewHolder.rootCell = convertView.findViewById(R.id.rootCell);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvEmergRoomListDate, viewHolder.tvEmergRoomListDate);
			convertView.setTag(R.id.tvEmergRoomListERDate, viewHolder.tvEmergRoomListERDate);
			convertView.setTag(R.id.tvEmergRoomListDesc, viewHolder.tvEmergRoomListDesc);
			convertView.setTag(R.id.tvEmergRoomListHospital, viewHolder.tvEmergRoomListHospital);
			convertView.setTag(R.id.tvEmergRoomListAdmObserv, viewHolder.tvEmergRoomListAdmObserv);
			convertView.setTag(R.id.tvEmergRoomListAvoidableER, viewHolder.tvEmergRoomListAvoidableER);
			convertView.setTag(R.id.tvEmergRoomListTypeER, viewHolder.tvEmergRoomListTypeER);
			convertView.setTag(R.id.tvEmergRoomListFacName, viewHolder.tvEmergRoomListFacName);
			convertView.setTag(R.id.tvEmergRoomListAddInfo, viewHolder.tvEmergRoomListAddInfo);
			convertView.setTag(R.id.rootCell, viewHolder.rootCell);

			//viewHolder.radios = new RadioButton[]{viewHolder.rb0, viewHolder.rb1, viewHolder.rb2, viewHolder.rb3};

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvEmergRoomListDate.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Report Date : </font>"+ emergRoomListBeans.get(position).dateof));
		viewHolder.tvEmergRoomListERDate.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>ER Visit Date : </font>"+ emergRoomListBeans.get(position).trip_date));
		viewHolder.tvEmergRoomListTypeER.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Type of ER Visit : </font>"+ emergRoomListBeans.get(position).type_of_visit));
		viewHolder.tvEmergRoomListFacName.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Facility Name : </font>"+ emergRoomListBeans.get(position).facility_name));
		viewHolder.tvEmergRoomListAdmObserv.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Admitted Observation : </font>"+ emergRoomListBeans.get(position).admitted_observation));
		viewHolder.tvEmergRoomListAddInfo.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Additional Information : </font>"+ emergRoomListBeans.get(position).additional_info));


		//hidden views
		viewHolder.tvEmergRoomListDesc.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Reason : </font>"+ emergRoomListBeans.get(position).reason));
		viewHolder.tvEmergRoomListHospital.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Hospital : </font>"+ emergRoomListBeans.get(position).hospital));
		viewHolder.tvEmergRoomListAvoidableER.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Avoidable ER : </font>"+ emergRoomListBeans.get(position).avoidable_er));

		return convertView;
	}

}
