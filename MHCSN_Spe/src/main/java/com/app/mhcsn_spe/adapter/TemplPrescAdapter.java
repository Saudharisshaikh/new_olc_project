package com.app.mhcsn_spe.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.app.mhcsn_spe.R;
import com.app.mhcsn_spe.model.PrescripTempletBean;
import com.app.mhcsn_spe.util.CheckInternetConnection;

import java.util.ArrayList;

public class TemplPrescAdapter extends BaseExpandableListAdapter{

	private Activity context;
	CheckInternetConnection checkInternetConnection;
	private ArrayList<PrescripTempletBean> data;


	public TemplPrescAdapter(Activity context, ArrayList<PrescripTempletBean> data) {
		//		super(context, R.layout.custom_supplier_past_tour_detail_attendee_list_item, objects);

		this.context = context;
		this.data = data;

		checkInternetConnection = new  CheckInternetConnection(context);
	}


	static class ViewHolder{
		TextView tvGroupCategoryName,tvChildCategoryData;
		CheckBox cbSelectCategoryData;
	}

	@Override
	public PrescripTempletBean.TemplateDrugBean getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return data.get(arg0).templateDrugBeans.get(arg1);
	}


	@Override
	public long getChildId(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return arg1;
	}


	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean arg2, View view,
							 ViewGroup arg4) {

		ViewHolder viewHolder;
		if (view == null) {
			LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = infalInflater.inflate(R.layout.lv_templpresc_row_child, null);
		}

		viewHolder = new ViewHolder();
		viewHolder.tvChildCategoryData = (TextView) view.findViewById(R.id.tvChildCategoryData);
		viewHolder.cbSelectCategoryData = (CheckBox) view.findViewById(R.id.cbSelectCategoryData);

		viewHolder.tvChildCategoryData.setText(data.get(groupPosition).templateDrugBeans.get(childPosition).drug_name);//+" - "+ data.get(groupPosition).templateDrugBeans.get(childPosition).dosage_form


		viewHolder.cbSelectCategoryData.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				data.get(groupPosition).templateDrugBeans.get(childPosition).isSelected = isChecked;
			}
		});
		viewHolder.cbSelectCategoryData.setChecked(data.get(groupPosition).templateDrugBeans.get(childPosition).isSelected);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				data.get(groupPosition).templateDrugBeans.get(childPosition).isSelected = !data.get(groupPosition).templateDrugBeans.get(childPosition).isSelected;
				notifyDataSetChanged();
			}
		});

		return view;
	}


	@Override
	public int getChildrenCount(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0).templateDrugBeans.size();
	}


	@Override
	public PrescripTempletBean getGroup(int arg0) {
		return data.get(arg0);
	}


	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return data.size();
	}


	@Override
	public long getGroupId(int arg0) {
		return arg0;
	}


	@Override
	public View getGroupView(final int arg0, boolean arg1, View view, ViewGroup arg3) {


		ViewHolder viewHolder;

		PrescripTempletBean headerInfo = getGroup(arg0);
		if (view == null) {
			LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inf.inflate(R.layout.lv_templpresc_row_parent, null);
		}
		viewHolder = new ViewHolder();

		viewHolder.tvGroupCategoryName = (TextView) view.findViewById(R.id.tvGroupCategoryName);

		viewHolder.tvGroupCategoryName.setText(headerInfo.category_name);


		return view;
	}


	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

}
