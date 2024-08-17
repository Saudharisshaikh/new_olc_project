package com.app.mdlive_cp.careplan;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.mdlive_cp.R;

import java.util.ArrayList;


public class CP_MedAdapter extends ArrayAdapter<CP_MedBean> {

	Activity activity;
	ArrayList<CP_MedBean> cp_medBeans;

	public CP_MedAdapter(Activity activity, ArrayList<CP_MedBean> cp_medBeans) {
		super(activity, R.layout.cp_med_row, cp_medBeans);

		this.activity = activity;
		this.cp_medBeans = cp_medBeans;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cp_medBeans.size();
	}
	static class ViewHolder {
		TextView tvCPM_StartDate,tvCPM_PrescribedBy,tvCPM_Generic_Brand,tvCPM_Directions,tvCPM_Use,tvCPM_OTC,
				tvCPM_B,tvCPM_L,tvCPM_D,tvCPM_N,tvCPM_Comments;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.cp_med_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvCPM_StartDate = convertView.findViewById(R.id.tvCPM_StartDate);
			viewHolder.tvCPM_PrescribedBy = convertView.findViewById(R.id.tvCPM_PrescribedBy);
			viewHolder.tvCPM_Generic_Brand = convertView.findViewById(R.id.tvCPM_Generic_Brand);
			viewHolder.tvCPM_Directions = convertView.findViewById(R.id.tvCPM_Directions);
			viewHolder.tvCPM_Use = convertView.findViewById(R.id.tvCPM_Use);
			viewHolder.tvCPM_OTC = convertView.findViewById(R.id.tvCPM_OTC);
			viewHolder.tvCPM_B = convertView.findViewById(R.id.tvCPM_B);
			viewHolder.tvCPM_L = convertView.findViewById(R.id.tvCPM_L);
			viewHolder.tvCPM_D = convertView.findViewById(R.id.tvCPM_D);
			viewHolder.tvCPM_N = convertView.findViewById(R.id.tvCPM_N);
			viewHolder.tvCPM_Comments = convertView.findViewById(R.id.tvCPM_Comments);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.tvCPM_StartDate, viewHolder.tvCPM_StartDate);
			convertView.setTag(R.id.tvCPM_PrescribedBy, viewHolder.tvCPM_PrescribedBy);
			convertView.setTag(R.id.tvCPM_Generic_Brand, viewHolder.tvCPM_Generic_Brand);
			convertView.setTag(R.id.tvCPM_Directions, viewHolder.tvCPM_Directions);
			convertView.setTag(R.id.tvCPM_Use, viewHolder.tvCPM_Use);
			convertView.setTag(R.id.tvCPM_OTC, viewHolder.tvCPM_OTC);
			convertView.setTag(R.id.tvCPM_B, viewHolder.tvCPM_B);
			convertView.setTag(R.id.tvCPM_L, viewHolder.tvCPM_L);
			convertView.setTag(R.id.tvCPM_D, viewHolder.tvCPM_D);
			convertView.setTag(R.id.tvCPM_N, viewHolder.tvCPM_N);
			convertView.setTag(R.id.tvCPM_Comments, viewHolder.tvCPM_Comments);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvCPM_StartDate.setText(cp_medBeans.get(position).start_date);
		viewHolder.tvCPM_PrescribedBy.setText(cp_medBeans.get(position).prescribed_by);
		viewHolder.tvCPM_Generic_Brand.setText(cp_medBeans.get(position).medication_name);
		viewHolder.tvCPM_Directions.setText(cp_medBeans.get(position).direction);
		viewHolder.tvCPM_Use.setText(cp_medBeans.get(position).use);
		viewHolder.tvCPM_OTC.setText(cp_medBeans.get(position).otc);
		viewHolder.tvCPM_Comments.setText(cp_medBeans.get(position).comments);

		viewHolder.tvCPM_B.setText(cp_medBeans.get(position).b.replace("0","No").replace("1","Yes"));
		viewHolder.tvCPM_L.setText(cp_medBeans.get(position).l.replace("0","No").replace("1","Yes"));
		viewHolder.tvCPM_D.setText(cp_medBeans.get(position).d.replace("0","No").replace("1","Yes"));
		viewHolder.tvCPM_N.setText(cp_medBeans.get(position).n.replace("0","No").replace("1","Yes"));

		return convertView;
	}
}
