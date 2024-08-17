package com.app.onlinecare_pk.dental;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.app.onlinecare_pk.R;

import java.util.List;

public class LvDentalSymptomAdapter2 extends ArrayAdapter<DentalSymptomBean> {

	Activity activity;
	List<DentalSymptomBean> dentalSymptomBeans;

	public LvDentalSymptomAdapter2(Activity activity , List<DentalSymptomBean> dentalSymptomBeans) {
		super(activity, R.layout.lv_dental_symptom_row2, dentalSymptomBeans);
		this.activity = activity;
		this.dentalSymptomBeans = dentalSymptomBeans;
	}

	static class ViewHolder {
		RadioButton tvSymptomName;
		//ImageView ivResultStatus;
	}
	ViewHolder viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_dental_symptom_row2, null);

			viewHolder = new ViewHolder();

			viewHolder.tvSymptomName = convertView.findViewById(R.id.tvSymptomName);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.tvSymptomName , viewHolder.tvSymptomName);



		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvSymptomName.setText(dentalSymptomBeans.get(position).symptomName);





		viewHolder.tvSymptomName.setChecked(((ListView) parent).isItemChecked(position));



		return convertView;
	}

}
