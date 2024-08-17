package com.app.onlinecare.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.onlinecare.R;
import com.app.onlinecare.ReportFolders;
import com.app.onlinecare.model.ReportsModel;

import java.util.ArrayList;

public class FoldersAdapter extends ArrayAdapter<ReportsModel> {

	Context context;
	ArrayList<ReportsModel> reportsModels;

	public FoldersAdapter(Context context, ArrayList<ReportsModel> reportsModels) {
		super(context, R.layout.folders_row, reportsModels);

		this.context = context;
		this.reportsModels = reportsModels;
	}


	static class RecordHolder {
		TextView txtTitle;
		ImageView ivDeleteFolder;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		RecordHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			convertView = inflater.inflate(R.layout.folders_row, parent, false);
			holder = new RecordHolder();
			holder.txtTitle = (TextView) convertView.findViewById(R.id.item_text);
			holder.ivDeleteFolder = convertView.findViewById(R.id.ivDeleteFolder);
			convertView.setTag(holder);
		} else {
			holder = (RecordHolder) convertView.getTag();
		}

		holder.txtTitle.setText(reportsModels.get(position).folder_name);
		holder.ivDeleteFolder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((ReportFolders) context).confirmDeleteFolderDialog(reportsModels.get(position).folder_id);
			}
		});

		return convertView;
	}

}

