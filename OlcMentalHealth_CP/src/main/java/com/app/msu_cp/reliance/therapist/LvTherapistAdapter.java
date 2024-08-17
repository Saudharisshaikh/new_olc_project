package com.app.msu_cp.reliance.therapist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.msu_cp.R;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.OpenActivity;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Locale;

public class LvTherapistAdapter extends ArrayAdapter<DoctorBean> {

    Activity activity;
    ArrayList<DoctorBean> doctorBeans,doctorBeansOrig;
    OpenActivity openActivity;

    public LvTherapistAdapter(Activity activity, ArrayList<DoctorBean> doctorBeans) {
        super(activity, R.layout.lv_therapist_row, doctorBeans);

        this.activity = activity;
        this.doctorBeans = doctorBeans;

        this.doctorBeansOrig = new ArrayList<>();
        this.doctorBeansOrig.addAll(doctorBeans);

        openActivity = new OpenActivity(activity);
    }

    static class ViewHolder {

        TextView tvProviderName,tvProviderDesig,tvRefer;
        ImageView ivProvider,ivIsonline;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if(convertView == null) {

            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.lv_therapist_row, null);

            viewHolder = new ViewHolder();

            viewHolder.tvProviderName= (TextView) convertView.findViewById(R.id.tvProviderName);
            viewHolder.tvProviderDesig = (TextView) convertView.findViewById(R.id.tvProviderDesig);
            viewHolder.ivProvider = (CircularImageView) convertView.findViewById(R.id.ivProvider);
            viewHolder.ivIsonline = (ImageView) convertView.findViewById(R.id.ivIsonline);
            viewHolder.tvRefer = (TextView) convertView.findViewById(R.id.tvRefer);

            convertView.setTag(viewHolder);
            convertView.setTag(R.id.tvProviderName, viewHolder.tvProviderName);
            convertView.setTag(R.id.tvProviderDesig, viewHolder.tvProviderDesig);
            convertView.setTag(R.id.ivProvider, viewHolder.ivProvider);
            convertView.setTag(R.id.ivIsonline, viewHolder.ivIsonline);
            convertView.setTag(R.id.tvRefer, viewHolder.tvRefer);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.tvProviderName.setText(doctorBeans.get(position).first_name+
                " "+doctorBeans.get(position).last_name);

        viewHolder.tvProviderDesig.setText(doctorBeans.get(position).designation);

        DATA.loadImageFromURL(doctorBeans.get(position).image,R.drawable.icon_call_screen,viewHolder.ivProvider);

		/*if(doctorBeans.get(position).current_app.equalsIgnoreCase("nurse")){
            viewHolder.tvProviderDesig.setText(WordUtils.capitalize(doctorBeans.get(position).doctor_category));
        }else{
            viewHolder.tvProviderDesig.setText(WordUtils.capitalize(doctorBeans.get(position).current_app));
        }*/

        if (doctorBeans.get(position).is_online.equals("1")) {
            viewHolder.ivIsonline.setImageResource(R.drawable.icon_online);
        }else{
            viewHolder.ivIsonline.setImageResource(R.drawable.icon_notification);
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tvRefer:
                        new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme).setTitle("Confirm")
                                .setMessage("Are you sure ? Do you want to send referral request to "+doctorBeans.get(position).first_name+" "+doctorBeans.get(position).last_name)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(activity instanceof  ActivityTherapyNote){
                                            ((ActivityTherapyNote) activity).referPatient(doctorBeans.get(position).id);
                                        }
                                    }
                                })
                                .setNegativeButton("No", null)
                                .create().show();
                        break;
                }
            }
        };
        viewHolder.tvRefer.setOnClickListener(onClickListener);

        return convertView;
    }


    public void filter(String filterText) {
        doctorBeans.clear();
        filterText = filterText.toLowerCase(Locale.getDefault());
        DATA.print("-- doctorBeansOrig size: "+doctorBeansOrig.size());
        if(filterText.length() == 0) {
            doctorBeans.addAll(doctorBeansOrig);
        } else {
            for(DoctorBean temp : doctorBeansOrig) {
                if(temp.first_name.toLowerCase(Locale.getDefault()).contains(filterText) || temp.last_name.toLowerCase(Locale.getDefault()).contains(filterText)) {
                    doctorBeans.add(temp);
                }
            }
        }
        notifyDataSetChanged();
    }//end filter
}
