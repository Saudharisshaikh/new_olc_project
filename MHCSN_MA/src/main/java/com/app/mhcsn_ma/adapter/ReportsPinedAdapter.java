package com.app.mhcsn_ma.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mhcsn_ma.R;
import com.app.mhcsn_ma.model.FileBean;
import com.app.mhcsn_ma.model.FolderBean;
import com.app.mhcsn_ma.util.CheckInternetConnection;
import com.app.mhcsn_ma.util.DATA;

import java.util.ArrayList;

public class ReportsPinedAdapter extends BaseExpandableListAdapter{

	private Activity context;
	CheckInternetConnection checkInternetConnection;
	private ArrayList<FolderBean> folderBeens;


	public ReportsPinedAdapter(Activity context, ArrayList<FolderBean> folderBeens) {
		//		super(context, R.layout.custom_supplier_past_tour_detail_attendee_list_item, objects);

		this.context = context;
		this.folderBeens = folderBeens;
		checkInternetConnection = new  CheckInternetConnection(context);
	}


	static class ViewHolder{
		TextView tvGroupFolderName,tvChildReportName;
		ImageView ivChildReportIcon;
	}

	@Override
	public FileBean getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return folderBeens.get(arg0).fileBeens.get(arg1);
	}


	@Override
	public long getChildId(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return arg1;
	}


	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean arg2, View view, ViewGroup arg4) {

		ViewHolder viewHolder;
		if (view == null) {
			LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = infalInflater.inflate(R.layout.reports_row_child, null);
		}

		viewHolder = new ViewHolder();
		viewHolder.tvChildReportName = (TextView) view.findViewById(R.id.tvChildReportName);
		viewHolder.ivChildReportIcon = (ImageView) view.findViewById(R.id.ivChildReportIcon);

		viewHolder.tvChildReportName.setText(folderBeens.get(groupPosition).fileBeens.get(childPosition).file_display_name);

		if(folderBeens.get(groupPosition).fileBeens.get(childPosition).report_url.endsWith("pdf")){
			viewHolder.ivChildReportIcon.setImageResource(R.drawable.icon_pdf);
		}else if(folderBeens.get(groupPosition).fileBeens.get(childPosition).report_url.endsWith("doc") ||
				folderBeens.get(groupPosition).fileBeens.get(childPosition).report_url.endsWith("docx")){
			viewHolder.ivChildReportIcon.setImageResource(R.drawable.icon_doc);
		}else {
			/*Picasso.with(context).load(folderBeens.get(groupPosition).fileBeens.get(childPosition).report_url)
					.placeholder(R.drawable.icon_drawer_reports).into(viewHolder.ivChildReportIcon);*/
			DATA.loadImageFromURL(folderBeens.get(groupPosition).fileBeens.get(childPosition).report_url,
					R.drawable.ic_placeholder_2,viewHolder.ivChildReportIcon);//icon_drawer_reports
		}

		return view;
	}


	@Override
	public int getChildrenCount(int arg0) {
		// TODO Auto-generated method stub
		return folderBeens.get(arg0).fileBeens.size();
	}


	@Override
	public FolderBean getGroup(int arg0) {
		return folderBeens.get(arg0);
	}


	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return folderBeens.size();
	}


	@Override
	public long getGroupId(int arg0) {
		return arg0;
	}


	@Override
	public View getGroupView(final int arg0, boolean arg1, View view, ViewGroup arg3) {


		ViewHolder viewHolder;

		FolderBean headerInfo = getGroup(arg0);
		if (view == null) {
			LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inf.inflate(R.layout.reports_row_parent, null);
		}
		viewHolder = new ViewHolder();

		viewHolder.tvGroupFolderName = (TextView) view.findViewById(R.id.tvGroupFolderName);

		viewHolder.tvGroupFolderName.setText(headerInfo.folder_name);


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
		return true;
	}

}
