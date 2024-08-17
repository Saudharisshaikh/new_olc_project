package com.app.emcuradr.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.emcuradr.ActivityCovidFormList;
import com.app.emcuradr.ActivityTcmDetails;
import com.app.emcuradr.R;
import com.app.emcuradr.model.CovidFormListBean;
import com.app.emcuradr.model.MyAppointmentsModel;
import com.app.emcuradr.util.DATA;
import com.app.emcuradr.util.GloabalMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class CovidListAdapter extends ArrayAdapter<CovidFormListBean> {

    Activity activity;
    List<CovidFormListBean> covidFormListBeans, covidFormListBeansOrig;

    public CovidListAdapter(Activity activity , List<CovidFormListBean> covidFormListBeans) {
        super(activity, R.layout.lv_covid_list_row, covidFormListBeans);
        this.activity = activity;
        this.covidFormListBeans = covidFormListBeans;

        covidFormListBeansOrig = new ArrayList<>();
        covidFormListBeansOrig.addAll(covidFormListBeans);
    }

    static class ViewHolder {
        TextView tvPatientName, tvDate, tvViewForm,tvAction, tvCareAct, tvCall, tvMessage,tvResultStatus,tvSendResults,tvTestLocation,tvAddVitals,tvRemove;//tvAssignToProvider,tvCheckedBy
        TextView tvSwabSendOut,tvInHouseSwab,tvStrepTest,tvRapidFlu,tvDnaFlu,tvCulture;
        ImageView ivPatientImg,ivIsonline;//ivResultStatus
        RelativeLayout layLvCovidListItem;
    }
    ViewHolder viewHolder = null;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if(convertView == null) {

            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.lv_covid_list_row, null);

            viewHolder = new ViewHolder();
            viewHolder.tvPatientName = convertView.findViewById(R.id.tvPatientName);
            viewHolder.tvDate = convertView.findViewById(R.id.tvDate);
            viewHolder.tvViewForm = convertView.findViewById(R.id.tvViewForm);
            viewHolder.tvAction = convertView.findViewById(R.id.tvAction);
            viewHolder.tvCareAct = convertView.findViewById(R.id.tvCareAct);
            viewHolder.tvCall = convertView.findViewById(R.id.tvCall);
            viewHolder.tvMessage = convertView.findViewById(R.id.tvMessage);
            //viewHolder.tvAssignToProvider = convertView.findViewById(R.id.tvAssignToProvider);
            viewHolder.ivPatientImg = convertView.findViewById(R.id.ivPatientImg);
            viewHolder.ivIsonline = convertView.findViewById(R.id.ivIsonline);
            viewHolder.tvResultStatus = convertView.findViewById(R.id.tvResultStatus);
            //viewHolder.ivResultStatus = convertView.findViewById(R.id.ivResultStatus);
            viewHolder.tvSendResults = convertView.findViewById(R.id.tvSendResults);
            viewHolder.tvTestLocation = convertView.findViewById(R.id.tvTestLocation);
            //viewHolder.tvCheckedBy = convertView.findViewById(R.id.tvCheckedBy);
            viewHolder.tvAddVitals = convertView.findViewById(R.id.tvAddVitals);
            viewHolder.tvSwabSendOut = convertView.findViewById(R.id.tvSwabSendOut);
            viewHolder.tvInHouseSwab = convertView.findViewById(R.id.tvInHouseSwab);
            viewHolder.tvStrepTest = convertView.findViewById(R.id.tvStrepTest);
            viewHolder.tvRapidFlu = convertView.findViewById(R.id.tvRapidFlu);
            viewHolder.tvDnaFlu = convertView.findViewById(R.id.tvDnaFlu);
            viewHolder.tvCulture = convertView.findViewById(R.id.tvCulture);
            viewHolder.layLvCovidListItem = convertView.findViewById(R.id.layLvCovidListItem);
            viewHolder.tvRemove = convertView.findViewById(R.id.tvRemove);

            convertView.setTag(viewHolder);

            convertView.setTag(R.id.tvPatientName , viewHolder.tvPatientName);
            convertView.setTag(R.id.tvDate , viewHolder.tvDate);
            convertView.setTag(R.id.tvViewForm , viewHolder.tvViewForm);
            convertView.setTag(R.id.tvAction , viewHolder.tvAction);
            convertView.setTag(R.id.tvCareAct , viewHolder.tvCareAct);
            convertView.setTag(R.id.tvCall , viewHolder.tvCall);
            convertView.setTag(R.id.tvMessage , viewHolder.tvMessage);
            //convertView.setTag(R.id.tvAssignToProvider , viewHolder.tvAssignToProvider);
            convertView.setTag(R.id.ivPatientImg , viewHolder.ivPatientImg);
            convertView.setTag(R.id.ivIsonline , viewHolder.ivIsonline);
            convertView.setTag(R.id.tvResultStatus , viewHolder.tvResultStatus);
            //convertView.setTag(R.id.ivResultStatus , viewHolder.ivResultStatus);
            convertView.setTag(R.id.tvSendResults , viewHolder.tvSendResults);
            convertView.setTag(R.id.tvTestLocation , viewHolder.tvTestLocation);
            //convertView.setTag(R.id.tvCheckedBy , viewHolder.tvCheckedBy);
            convertView.setTag(R.id.tvAddVitals , viewHolder.tvAddVitals);
            convertView.setTag(R.id.tvSwabSendOut , viewHolder.tvSwabSendOut);
            convertView.setTag(R.id.tvInHouseSwab , viewHolder.tvInHouseSwab);
            convertView.setTag(R.id.tvStrepTest , viewHolder.tvStrepTest);
            convertView.setTag(R.id.tvRapidFlu , viewHolder.tvRapidFlu);
            convertView.setTag(R.id.tvDnaFlu , viewHolder.tvDnaFlu);
            convertView.setTag(R.id.tvCulture , viewHolder.tvCulture);
            convertView.setTag(R.id.layLvCovidListItem , viewHolder.layLvCovidListItem);
            convertView.setTag(R.id.tvRemove , viewHolder.tvRemove);


        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvPatientName.setText(covidFormListBeans.get(position).patient_name);
        viewHolder.tvDate.setText(covidFormListBeans.get(position).dateof);

        DATA.loadImageFromURL(covidFormListBeans.get(position).image, R.drawable.icon_call_screen, viewHolder.ivPatientImg);

        int drawableResId = covidFormListBeans.get(position).is_online.equalsIgnoreCase("1") ? R.drawable.icon_online : R.drawable.icon_notification;
        viewHolder.ivIsonline.setImageResource(drawableResId);

        viewHolder.tvCall.setEnabled(covidFormListBeans.get(position).is_online.equalsIgnoreCase("1"));

        /*if(TextUtils.isEmpty(covidFormListBeans.get(position).result)){
            viewHolder.tvResultStatus.setText("Processing");
            viewHolder.ivResultStatus.setImageResource(R.drawable.cust_cir_res_processing);
            viewHolder.tvSendResults.setEnabled(true);
        }else if(covidFormListBeans.get(position).result.equalsIgnoreCase("Positive")){
            viewHolder.tvResultStatus.setText(covidFormListBeans.get(position).result);
            viewHolder.ivResultStatus.setImageResource(R.drawable.cust_cir_res_positvie);
            viewHolder.tvSendResults.setEnabled(false);
        }else if(covidFormListBeans.get(position).result.equalsIgnoreCase("Negative")){
            viewHolder.tvResultStatus.setText(covidFormListBeans.get(position).result);
            viewHolder.ivResultStatus.setImageResource(R.drawable.cust_cir_res_negatvie);
            viewHolder.tvSendResults.setEnabled(false);
        }*/

        /*if(!TextUtils.isEmpty(covidFormListBeans.get(position).rapid_covid_result) && !TextUtils.isEmpty(covidFormListBeans.get(position).pcr_covid_result) &&
                !TextUtils.isEmpty(covidFormListBeans.get(position).rapid_strep_result) && !TextUtils.isEmpty(covidFormListBeans.get(position).strep_culture_result) &&
                !TextUtils.isEmpty(covidFormListBeans.get(position).rapid_flu_result)){
            viewHolder.tvSendResults.setEnabled(false);
        }else {
            viewHolder.tvSendResults.setEnabled(true);
        }*/

        viewHolder.tvTestLocation.setText(covidFormListBeans.get(position).location_name);
        //viewHolder.tvCheckedBy.setText(TextUtils.isEmpty(covidFormListBeans.get(position).doctor_name) ? "-" : covidFormListBeans.get(position).doctor_name);

        viewHolder.tvSwabSendOut.setCompoundDrawablesWithIntrinsicBounds( null, null, covidFormListBeans.get(position).swab_send_out.equalsIgnoreCase("1") ? activity.getResources().getDrawable(R.drawable.green_check): activity.getResources().getDrawable(R.drawable.gray_check) , null);
        viewHolder.tvInHouseSwab.setCompoundDrawablesWithIntrinsicBounds( null, null, covidFormListBeans.get(position).in_house_swab.equalsIgnoreCase("1") ? activity.getResources().getDrawable(R.drawable.green_check): activity.getResources().getDrawable(R.drawable.gray_check) , null);
        viewHolder.tvStrepTest.setCompoundDrawablesWithIntrinsicBounds( null, null, covidFormListBeans.get(position).strep_test.equalsIgnoreCase("1") ? activity.getResources().getDrawable(R.drawable.green_check): activity.getResources().getDrawable(R.drawable.gray_check) , null);
        viewHolder.tvRapidFlu.setCompoundDrawablesWithIntrinsicBounds( null, null, covidFormListBeans.get(position).rapid_flu.equalsIgnoreCase("1") ? activity.getResources().getDrawable(R.drawable.green_check): activity.getResources().getDrawable(R.drawable.gray_check) , null);
        viewHolder.tvDnaFlu.setCompoundDrawablesWithIntrinsicBounds( null, null, covidFormListBeans.get(position).dna_flu.equalsIgnoreCase("1") ? activity.getResources().getDrawable(R.drawable.green_check): activity.getResources().getDrawable(R.drawable.gray_check) , null);
        viewHolder.tvCulture.setCompoundDrawablesWithIntrinsicBounds( null, null, covidFormListBeans.get(position).culture.equalsIgnoreCase("1") ? activity.getResources().getDrawable(R.drawable.green_check): activity.getResources().getDrawable(R.drawable.gray_check) , null);
        viewHolder.tvCulture.setText(covidFormListBeans.get(position).culture.equalsIgnoreCase("1") ? "Strep Culture : Yes" : "Strep Culture : No");

        if(TextUtils.isEmpty(covidFormListBeans.get(position).rapid_covid_result) && TextUtils.isEmpty(covidFormListBeans.get(position).pcr_covid_result) &&
                TextUtils.isEmpty(covidFormListBeans.get(position).rapid_strep_result) && TextUtils.isEmpty(covidFormListBeans.get(position).strep_culture_result) &&
                TextUtils.isEmpty(covidFormListBeans.get(position).rapid_flu_result)  && TextUtils.isEmpty(covidFormListBeans.get(position).dna_flu_result)){
            viewHolder.tvResultStatus.setEnabled(false);
        }else {
            viewHolder.tvResultStatus.setEnabled(true);
        }

        /*int colorRow = covidFormListBeans.get(position).is_read.equalsIgnoreCase("0") ? activity.getResources().getColor(R.color.light_blue) :
                activity.getResources().getColor(android.R.color.white);
        viewHolder.layLvCovidListItem.setBackgroundColor(colorRow);*/

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tvViewForm:
                        new GloabalMethods(activity).showWebviewDialog(DATA.baseUrl + "ctesting/view/"+covidFormListBeans.get(position).id+"?platform=mobile" , "Covid Testing");
                        break;
                    case R.id.tvCareAct:
                        new GloabalMethods(activity).showWebviewDialog(DATA.baseUrl + "ctesting/view_careact/"+covidFormListBeans.get(position).id+"?platform=mobile" , "COVID Data Elements Reporting Sheet (CARES ACT)");
                        break;
                    case R.id.tvAction:
                        if(activity instanceof ActivityCovidFormList){
                            ((ActivityCovidFormList) activity).showCovidActionDialog(position);
                        }
                        break;


                    case R.id.tvCall:
                        DATA.selectedUserCallId = covidFormListBeans.get(position).patient_id;
                        DATA.selectedUserCallName = covidFormListBeans.get(position).patient_name;
                        DATA.selectedUserCallSympTom = covidFormListBeans.get(position).symptom_txt;
                        DATA.selectedUserCallCondition = "";//covidFormListBeans.get(position).condition_name;
                        DATA.selectedUserCallDescription = "";//careRequestBeans.get(position).description;
                        DATA.selectedUserCallImage = covidFormListBeans.get(position).image;
                        DATA.isFromDocToDoc = false;
                        DATA.incomingCall = false;
                        ActivityTcmDetails.primary_patient_id = "";//careVistBeans.get(position).primary_patient_id;
                        ActivityTcmDetails.family_is_online = "0";//"1";//not good but api limitation
                        Intent myIntent = new Intent(activity, MainActivity.class);//SampleActivity.class
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(myIntent);
                        break;

                    case R.id.tvMessage:
                        DATA.isFromDocToDoc = false;
                        DATA.selectedUserCallId = covidFormListBeans.get(position).patient_id;
                        DATA.selectedUserCallName = covidFormListBeans.get(position).patient_name;
                        DATA.selectedUserCallImage = covidFormListBeans.get(position).image;

                        ActivityTcmDetails.primary_patient_id = "";//careVistBeans.get(position).primary_patient_id;
                        ActivityTcmDetails.family_is_online = "0";//"1";//not good but api limitation
                        new GloabalMethods(activity).initMsgDialog();
                        break;

                    /*case R.id.tvAssignToProvider:
                        ActivityCovidAssignDocList.covidFormListBean = covidFormListBeans.get(position);
                        activity.startActivity(new Intent(activity, ActivityCovidAssignDocList.class));
                        break;*/
                    case R.id.tvSendResults:
                        if(activity instanceof ActivityCovidFormList){
                            ((ActivityCovidFormList) activity).showCovidSendResultDialog(position);
                        }
                        break;
                    case R.id.tvAddVitals:
                        if(activity instanceof ActivityCovidFormList){
                            ((ActivityCovidFormList) activity).showCovidAddVitalsDialog(position);
                        }
                        break;
                    case R.id.tvResultStatus:
                        if(activity instanceof ActivityCovidFormList){
                            ((ActivityCovidFormList) activity).showCovidViewDialog(position);
                        }
                        break;
                    case R.id.tvRemove:
                        if(activity instanceof ActivityCovidFormList){
                            ((ActivityCovidFormList) activity).askRemovePatient(position);
                        }
                        break;
                    case R.id.ivPatientImg:
                        DATA.selectedUserCallId = covidFormListBeans.get(position).patient_id;
                        DATA.selectedUserCallName = covidFormListBeans.get(position).patient_name;
                        DATA.selectedLiveCare = new MyAppointmentsModel();
                        activity.startActivity(new Intent(activity, ActivityTcmDetails.class));
                        break;
                }
            }
        };
        viewHolder.tvViewForm.setOnClickListener(onClickListener);
        viewHolder.tvAction.setOnClickListener(onClickListener);
        viewHolder.tvCareAct.setOnClickListener(onClickListener);
        viewHolder.tvCall.setOnClickListener(onClickListener);
        viewHolder.tvMessage.setOnClickListener(onClickListener);
        //viewHolder.tvAssignToProvider.setOnClickListener(onClickListener);
        viewHolder.tvSendResults.setOnClickListener(onClickListener);
        viewHolder.tvAddVitals.setOnClickListener(onClickListener);
        viewHolder.tvResultStatus.setOnClickListener(onClickListener);
        viewHolder.tvRemove.setOnClickListener(onClickListener);
        viewHolder.ivPatientImg.setOnClickListener(onClickListener);

        return convertView;
    }


    public void filter(String filterText) {
        covidFormListBeans.clear();
        filterText = filterText.toLowerCase(Locale.getDefault());
        DATA.print("-- covidFormListBeansOrig size: "+covidFormListBeansOrig.size());
        if(filterText.length() == 0) {
            covidFormListBeans.addAll(covidFormListBeansOrig);
        } else {
            for(CovidFormListBean temp : covidFormListBeansOrig) {
                if(temp.patient_name.toLowerCase(Locale.getDefault()).contains(filterText)) {
                    covidFormListBeans.add(temp);
                }
            }
        }
        notifyDataSetChanged();
    }//end filter

}
