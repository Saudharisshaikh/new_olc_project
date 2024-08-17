package com.app.fivestardoc.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.app.fivestardoc.ActivityLabRequests;
import com.app.fivestardoc.ActivityViewLabResults;
import com.app.fivestardoc.R;
import com.app.fivestardoc.model.LabRequestModel;
import com.app.fivestardoc.util.DATA;

import java.util.ArrayList;

public class LabRequestAdapter extends ArrayAdapter<LabRequestModel> {

    Activity activity;
    ArrayList<LabRequestModel> labRequestModels;
    SharedPreferences prefs;

    public static String labId = "";
    public LabRequestAdapter(Activity activity , ArrayList<LabRequestModel> labRequestModels) {
        super(activity, R.layout.lv_labrequest_row, labRequestModels);

        this.activity = activity;
        this.labRequestModels = labRequestModels;
        prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
//		filter(DATA.selectedDrId);
    }

    static class ViewHolder {

        TextView tvLabReqProviderName,tvLabReqDate;
        Button btnViewLabResult,btnViewLabRes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if(convertView == null) {

            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.lv_labrequest_row, null);

            viewHolder = new ViewHolder();

            viewHolder.tvLabReqProviderName = (TextView) convertView.findViewById(R.id.tvLabReqProviderName);
            viewHolder.tvLabReqDate = (TextView) convertView.findViewById(R.id.tvLabReqDate);
            viewHolder.btnViewLabResult = convertView.findViewById(R.id.btnViewLabResult);
            viewHolder.btnViewLabRes = convertView.findViewById(R.id.btnViewLabRes);


            convertView.setTag(viewHolder);
            convertView.setTag(R.id.tvLabReqProviderName, viewHolder.tvLabReqProviderName);
            convertView.setTag(R.id.tvLabReqDate, viewHolder.tvLabReqDate);
        }
        else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        String drName = labRequestModels.get(position).getFirst_name()+" "+labRequestModels.get(position).getLast_name();
        DATA.print("-- drName " + drName);
        viewHolder.tvLabReqProviderName.setText(drName);
        viewHolder.tvLabReqProviderName.setTag(drName);


        viewHolder.tvLabReqDate.setText(labRequestModels.get(position).getDateof());
        viewHolder.tvLabReqDate.setTag(labRequestModels.get(position).getDateof());

        viewHolder.btnViewLabResult.setOnClickListener(view ->
        {
            if (activity instanceof ActivityLabRequests) {
                ((ActivityLabRequests)activity).viewLabReq(labRequestModels.get(position).getId() , labRequestModels.get(position).getPatient_id());
            }
        });

        //int result = Integer.parseInt(labRequestModels.get(position).getResult_id());

        if (labRequestModels.get(position).getResult_id().isEmpty())
        {
            viewHolder.btnViewLabRes.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.btnViewLabRes.setVisibility(View.VISIBLE);
        }

        viewHolder.btnViewLabRes.setOnClickListener(view ->
        {
            labId = labRequestModels.get(position).getId();
            activity.startActivity(new Intent(activity , ActivityViewLabResults.class));
        });

        return convertView;
    }
}

