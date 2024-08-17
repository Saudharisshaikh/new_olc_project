package com.app.mhcsn_cp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.model.ConditionsModel;

import java.util.ArrayList;

public class GvConditionAdapter extends ArrayAdapter<ConditionsModel> {

	Activity activity;
	ArrayList<ConditionsModel> conditionsModels;


	public static ArrayList<Integer> selectedPositons = new ArrayList<>();

	public GvConditionAdapter(Activity activity, ArrayList<ConditionsModel> conditionsModels) {
		super(activity, R.layout.listitem_singlechoice, conditionsModels);

		this.activity = activity;
		this.conditionsModels = conditionsModels;

		selectedPositons = new ArrayList<>();//important to reset when const called
	}

	static class ViewHolder {
		CheckedTextView text1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.listitem_singlechoice, null);

			viewHolder = new ViewHolder();

			viewHolder.text1 = convertView.findViewById(R.id.text1);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.text1, viewHolder.text1);
		}
		else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.text1.setText(conditionsModels.get(position).conditionName);

		viewHolder.text1.setChecked(selectedPositons.contains(position));

		return convertView;
	}
}
