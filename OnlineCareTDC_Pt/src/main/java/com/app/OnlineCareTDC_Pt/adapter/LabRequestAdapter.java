package com.app.OnlineCareTDC_Pt.adapter;

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

import com.app.OnlineCareTDC_Pt.ActivityLabRequests;
import com.app.OnlineCareTDC_Pt.ActivityUploadLabRequest;
import com.app.OnlineCareTDC_Pt.R;
import com.app.OnlineCareTDC_Pt.model.ConversationBean;
import com.app.OnlineCareTDC_Pt.model.LabRequestModel;
import com.app.OnlineCareTDC_Pt.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

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
        Button btnViewLabResult,btnUploadLabRes;
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
            viewHolder.btnUploadLabRes = convertView.findViewById(R.id.btnUploadLabRes);


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
                ((ActivityLabRequests)activity).viewLabReq(labRequestModels.get(position).getId());
            }
        });


        viewHolder.btnUploadLabRes.setOnClickListener(view ->
        {
            labId = labRequestModels.get(position).getId();
            activity.startActivity(new Intent(activity , ActivityUploadLabRequest.class));
        });

        return convertView;
    }
}

