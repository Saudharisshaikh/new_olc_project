package com.app.OnlineCareTDC_MA.reliance.therapist;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.OnlineCareTDC_MA.R;
import com.app.OnlineCareTDC_MA.util.DATA;
import com.app.OnlineCareTDC_MA.util.OpenActivity;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.util.TextUtils;

public class LvReffPtAdapter extends ArrayAdapter<RefferedPatientBean> {

    Activity activity;
    ArrayList<RefferedPatientBean> refferedPatientBeans,refferedPatientBeansOrig;
    OpenActivity openActivity;

    public LvReffPtAdapter(Activity activity, ArrayList<RefferedPatientBean> refferedPatientBeans) {
        super(activity, R.layout.lv_reff_pt_row, refferedPatientBeans);

        this.activity = activity;
        this.refferedPatientBeans = refferedPatientBeans;

        this.refferedPatientBeansOrig = new ArrayList<>();
        this.refferedPatientBeansOrig.addAll(refferedPatientBeans);

        openActivity = new OpenActivity(activity);
    }

    static class ViewHolder {

        TextView tvPatientName,tvReffBy,tvAccept,tvDecline,tvReffStatus;
        ImageView ivPatient,ivIsonline;
        LinearLayout layRefStatus,layBtns;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if(convertView == null) {

            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.lv_reff_pt_row, null);

            viewHolder = new ViewHolder();

            viewHolder.tvPatientName= (TextView) convertView.findViewById(R.id.tvPatientName);
            viewHolder.tvReffBy = (TextView) convertView.findViewById(R.id.tvReffBy);
            viewHolder.ivPatient = (CircularImageView) convertView.findViewById(R.id.ivPatient);
            viewHolder.ivIsonline = (ImageView) convertView.findViewById(R.id.ivIsonline);
            viewHolder.tvAccept = (TextView) convertView.findViewById(R.id.tvAccept);
            viewHolder.tvDecline = (TextView) convertView.findViewById(R.id.tvDecline);
            viewHolder.tvReffStatus = convertView.findViewById(R.id.tvReffStatus);
            viewHolder.layRefStatus = convertView.findViewById(R.id.layRefStatus);
            viewHolder.layBtns = convertView.findViewById(R.id.layBtns);

            convertView.setTag(viewHolder);
            convertView.setTag(R.id.tvPatientName, viewHolder.tvPatientName);
            convertView.setTag(R.id.tvReffBy, viewHolder.tvReffBy);
            convertView.setTag(R.id.ivPatient, viewHolder.ivPatient);
            convertView.setTag(R.id.ivIsonline, viewHolder.ivIsonline);
            convertView.setTag(R.id.tvAccept, viewHolder.tvAccept);
            convertView.setTag(R.id.tvDecline, viewHolder.tvDecline);
            convertView.setTag(R.id.tvReffStatus, viewHolder.tvReffStatus);
            convertView.setTag(R.id.layRefStatus, viewHolder.layRefStatus);
            convertView.setTag(R.id.layBtns, viewHolder.layBtns);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.tvPatientName.setText(refferedPatientBeans.get(position).patient_name);
        viewHolder.tvReffBy.setText(refferedPatientBeans.get(position).doctor_name);
        viewHolder.tvReffStatus.setText(refferedPatientBeans.get(position).status);
        DATA.loadImageFromURL(refferedPatientBeans.get(position).pimage,R.drawable.icon_call_screen,viewHolder.ivPatient);

		/*if(doctorBeans.get(position).current_app.equalsIgnoreCase("nurse")){
            viewHolder.tvProviderDesig.setText(WordUtils.capitalize(doctorBeans.get(position).doctor_category));
        }else{
            viewHolder.tvProviderDesig.setText(WordUtils.capitalize(doctorBeans.get(position).current_app));
        }*/

        if (refferedPatientBeans.get(position).is_online.equals("1")) {
            viewHolder.ivIsonline.setImageResource(R.drawable.icon_online);
        }else{
            viewHolder.ivIsonline.setImageResource(R.drawable.icon_notification);
        }

        if(TextUtils.isEmpty(refferedPatientBeans.get(position).status)){
            viewHolder.layBtns.setVisibility(View.VISIBLE);
            viewHolder.layRefStatus.setVisibility(View.GONE);
        }else {
            viewHolder.layBtns.setVisibility(View.GONE);
            viewHolder.layRefStatus.setVisibility(View.VISIBLE);
        }
        if(!TextUtils.isEmpty(refferedPatientBeans.get(position).status)){
            if(refferedPatientBeans.get(position).status.equalsIgnoreCase("Accept")){
                viewHolder.tvReffStatus.setTextColor(Color.parseColor("#43A047"));
            }else {
                viewHolder.tvReffStatus.setTextColor(Color.parseColor("#FFCCCB"));
            }
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tvAccept:
                        if(activity instanceof  ActivityReferedPatients){
                            ((ActivityReferedPatients) activity).acceptRefered(refferedPatientBeans.get(position).id);
                        }
                        /*new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme).setTitle("View Bio")
                                .setMessage(doctorBeans.get(position).introduction)
                                .setPositiveButton("Done",null)
                                .create().show();*/
                        break;
                    case R.id.tvDecline:
                        if(activity instanceof  ActivityReferedPatients){
                            ((ActivityReferedPatients) activity).showDeclineReasonDialog(refferedPatientBeans.get(position).id);
                        }
                        break;
                }
            }
        };
        viewHolder.tvAccept.setOnClickListener(onClickListener);
        viewHolder.tvDecline.setOnClickListener(onClickListener);

        return convertView;
    }


    public void filter(String filterText) {
        refferedPatientBeans.clear();
        filterText = filterText.toLowerCase(Locale.getDefault());
        DATA.print("-- refferedPatientBeansOrig size: "+refferedPatientBeansOrig.size());
        if(filterText.length() == 0) {
            refferedPatientBeans.addAll(refferedPatientBeansOrig);
        } else {
            for(RefferedPatientBean temp : refferedPatientBeansOrig) {
                if(temp.patient_name.toLowerCase(Locale.getDefault()).contains(filterText) || temp.doctor_name.toLowerCase(Locale.getDefault()).contains(filterText)) {
                    refferedPatientBeans.add(temp);
                }
            }
        }
        notifyDataSetChanged();
    }//end filter
}
