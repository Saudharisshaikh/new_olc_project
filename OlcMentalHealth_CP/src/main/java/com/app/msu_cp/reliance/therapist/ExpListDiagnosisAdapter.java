package com.app.msu_cp.reliance.therapist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.msu_cp.R;
import com.app.msu_cp.careplan.IcdCodeBean;
import com.app.msu_cp.util.DATA;

import java.util.ArrayList;
import java.util.Locale;

public class ExpListDiagnosisAdapter extends ArrayAdapter<IcdCodeBean> {

	Activity activity;
	ArrayList<IcdCodeBean> icdCodeBeans, icdCodeBeansOrig;
	//CustomToast customToast;

	public ExpListDiagnosisAdapter(Activity activity, ArrayList<IcdCodeBean> icdCodeBeans) {
		super(activity, R.layout.lv_diagnosis_row_explist, icdCodeBeans);

		this.activity = activity;
		//customToast = new CustomToast(activity);

		this.icdCodeBeans = icdCodeBeans;

		icdCodeBeansOrig = new ArrayList<>();
		icdCodeBeansOrig.addAll(icdCodeBeans);
	}

	static class ViewHolder {
		TextView tvDiag;
		ImageView ivDeleteDiag;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if (convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_diagnosis_row_explist, null);

			viewHolder = new ViewHolder();

			viewHolder.tvDiag = (TextView) convertView.findViewById(R.id.tvDiag);
			viewHolder.ivDeleteDiag = (ImageView) convertView.findViewById(R.id.ivDeleteDiag);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.tvDiag, viewHolder.tvDiag);
			convertView.setTag(R.id.ivDeleteDiag, viewHolder.ivDeleteDiag);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		//DATA.loadImageFromURL(companyDoctorBeans.get(position).image, R.drawable.icon_call_screen, viewHolder.ivPatient);

		viewHolder.tvDiag.setText(icdCodeBeans.get(position).code + " - " + icdCodeBeans.get(position).desc);

		//int resID = companyDoctorBeans.get(position).is_online.equalsIgnoreCase("1") ? R.drawable.icon_online : R.drawable.icon_notification;
		//viewHolder.ivIsonline.setImageResource(resID);

		viewHolder.ivDeleteDiag.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				icdCodeBeans.remove(position);
				notifyDataSetChanged();
			}
		});


		return convertView;
	}


	public void filter(String filterText) {
		try {
			icdCodeBeans.clear();
			filterText = filterText.toLowerCase(Locale.getDefault());
			DATA.print("---icdCodeBeansOrig size: " + icdCodeBeansOrig.size());
			if (filterText.length() == 0) {
				icdCodeBeans.addAll(icdCodeBeansOrig);
			} else {
				for (IcdCodeBean temp : icdCodeBeansOrig) {
					if (temp.desc.toLowerCase(Locale.getDefault()).contains(filterText) || temp.code.toLowerCase(Locale.getDefault()).contains(filterText)) {//startsWith
						icdCodeBeans.add(temp);
					}
				}
			}
			notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//end filter

}
