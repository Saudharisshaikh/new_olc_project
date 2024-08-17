package com.app.mhcsn_cp.reliance.therapist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.careplan.IcdCodeBean;
import com.app.mhcsn_cp.util.DATA;

import java.util.ArrayList;
import java.util.Locale;

public class LvDiagnosisAdapter extends ArrayAdapter<IcdCodeBean> {

	Activity activity;
	ArrayList<IcdCodeBean> icdCodeBeans, icdCodeBeansOrig;
	//CustomToast customToast;

	public LvDiagnosisAdapter(Activity activity, ArrayList<IcdCodeBean> icdCodeBeans) {
		super(activity, R.layout.lv_diag_icdcodes_row, icdCodeBeans);

		this.activity = activity;
		//customToast = new CustomToast(activity);

		this.icdCodeBeans = icdCodeBeans;

		icdCodeBeansOrig = new ArrayList<>();
		icdCodeBeansOrig.addAll(icdCodeBeans);
	}

	static class ViewHolder {
		TextView tvICDCode,tvCodeDesc;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if (convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_diag_icdcodes_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvICDCode =  convertView.findViewById(R.id.tvICDCode);
			viewHolder.tvCodeDesc = (TextView) convertView.findViewById(R.id.tvCodeDesc);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvICDCode, viewHolder.tvICDCode);
			convertView.setTag(R.id.tvCodeDesc, viewHolder.tvCodeDesc);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvICDCode.setText(icdCodeBeans.get(position).code);
		viewHolder.tvCodeDesc.setText(icdCodeBeans.get(position).desc);


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
