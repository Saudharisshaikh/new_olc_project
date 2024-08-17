package com.app.greatriverma.util;

import android.app.Activity;
import android.app.Dialog;
import androidx.core.widget.NestedScrollView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.greatriverma.R;
import com.app.greatriverma.adapter.VVReportImagesAdapter2;
import com.app.greatriverma.api.ApiCallBack;
import com.app.greatriverma.api.ApiManager;
import com.github.chrisbanes.photoview.PhotoView;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Engr G M on 9/26/2017.
 */

public class DialogPatientInfo implements ApiCallBack {

    Activity activity;
    CustomToast customToast;

    public static String patientIdGCM = "";
    public DialogPatientInfo(Activity activity) {
        this.activity = activity;
        customToast = new CustomToast(activity);
    }


    Dialog infoDialog;
    CircularImageView imgSelPtImage;
    TextView tvPtSymptom,tvPtCondition,tvPtDescription,tvPain,tvPainSeverity,tvPainBodyPart,tvSymptomDetails,
    tvSelPtPhone,tvSelPtEmail,tvSelPtAddress,tvSelPtDOB,tvSelPtName;
    TextView etSOAPHistoryMedical,etSOAPHistorySocial,etSOAPHistoryFamily,etSOAPHistoryMedications,etSOAPHistoryAllergies;
    EditText etOTBP,etOTHR,etOTRespirations,etOTO2Saturations,etOTBloodSugar,etOTTemperature,etOTHeight,etOTWeight,etOTBMI;
    TextView tvVitalsDate;
    ExpandableHeightGridView gvReportImages;
    ScrollView svPatientDetails;
    NestedScrollView nsvHistory;
    TextView tvHistoryBoxTittle;

    public void showDialog(){
        infoDialog = new Dialog(activity);
        infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        infoDialog.setContentView(R.layout.dialog_patient_info);

        tvPtSymptom = (TextView) infoDialog.findViewById(R.id.tvPtSymptom);
        tvPtCondition = (TextView) infoDialog.findViewById(R.id.tvPtCondition);
        tvPtDescription = (TextView) infoDialog.findViewById(R.id.tvPtDescription);
        tvPain = (TextView) infoDialog.findViewById(R.id.tvPain);
        tvPainSeverity = (TextView) infoDialog.findViewById(R.id.tvPainSeverity);
        tvPainBodyPart = infoDialog.findViewById(R.id.tvPainBodyPart);
        tvSymptomDetails = (TextView) infoDialog.findViewById(R.id.tvSymptomDetails);

        tvSelPtName = (TextView) infoDialog.findViewById(R.id.tvSelPtName);
        tvSelPtPhone= (TextView) infoDialog.findViewById(R.id.tvSelPtPhone);
        tvSelPtEmail= (TextView) infoDialog.findViewById(R.id.tvSelPtEmail);
        tvSelPtAddress= (TextView) infoDialog.findViewById(R.id.tvSelPtAddress);
        tvSelPtDOB= (TextView) infoDialog.findViewById(R.id.tvSelPtDOB);
        imgSelPtImage = (CircularImageView) infoDialog.findViewById(R.id.imgSelPtImage);

        etOTBP = infoDialog.findViewById(R.id.etOTBP);
        etOTHR = infoDialog.findViewById(R.id.etOTHR);
        etOTRespirations = infoDialog.findViewById(R.id.etOTRespirations);
        etOTO2Saturations = infoDialog.findViewById(R.id.etOTO2Saturations);
        etOTBloodSugar = infoDialog.findViewById(R.id.etOTBloodSugar);
        etOTTemperature = infoDialog.findViewById(R.id.etOTTemperature);
        etOTHeight = infoDialog.findViewById(R.id.etOTHeight);
        etOTWeight = infoDialog.findViewById(R.id.etOTWeight);
        etOTBMI = infoDialog.findViewById(R.id.etOTBMI);
        tvVitalsDate = (TextView) infoDialog.findViewById(R.id.tvVitalsDate);
        svPatientDetails = (ScrollView) infoDialog.findViewById(R.id.svPatientDetails);

        gvReportImages = infoDialog.findViewById(R.id.gvReportImages);

        etSOAPHistoryMedical = (TextView) infoDialog.findViewById(R.id.etSOAPHistoryMedical);
        etSOAPHistorySocial = (TextView) infoDialog.findViewById(R.id.etSOAPHistorySocial);
        etSOAPHistoryFamily = (TextView) infoDialog.findViewById(R.id.etSOAPHistoryFamily);
        etSOAPHistoryMedications = (TextView) infoDialog.findViewById(R.id.etSOAPHistoryMedications);
        etSOAPHistoryAllergies = (TextView) infoDialog.findViewById(R.id.etSOAPHistoryAllergies);
        nsvHistory = infoDialog.findViewById(R.id.nsvHistory);
        tvHistoryBoxTittle = infoDialog.findViewById(R.id.tvHistoryBoxTittle);

        infoDialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoDialog.dismiss();
            }
        });

        /*final ImageView ivExpendExamLay = (ImageView) infoDialog.findViewById(R.id.ivExpendExamLay);
        final ExpandableRelativeLayout layExpandExam = (ExpandableRelativeLayout) infoDialog.findViewById(R.id.layExpandExam);
        //layExpandExam.collapse();
        layExpandExam.expand();
        ivExpendExamLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layExpandExam.isExpanded()){
                    layExpandExam.collapse();
                    ivExpendExamLay.setImageResource(R.drawable.ic_add_box_black_24dp);
                }else {
                    layExpandExam.expand();
                    ivExpendExamLay.setImageResource(R.drawable.ic_indeterminate_check_box_black_24dp);
                }
            }
        });

        final ImageView ivExpendHistoryLay = (ImageView) infoDialog.findViewById(R.id.ivExpendHistoryLay);
        final ExpandableRelativeLayout layExpandHistory = (ExpandableRelativeLayout) infoDialog.findViewById(R.id.layExpandHistory);
        //layExpandHistory.collapse();
        //layExpandHistory.toggle();
        layExpandHistory.expand();
        ivExpendHistoryLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layExpandHistory.isExpanded()){
                    layExpandHistory.collapse();
                    ivExpendHistoryLay.setImageResource(R.drawable.ic_add_box_black_24dp);
                }else {
                    //layExpandHistory.expand();
                    layExpandHistory.expand(1000, new FastOutSlowInInterpolator());
                    ivExpendHistoryLay.setImageResource(R.drawable.ic_indeterminate_check_box_black_24dp);
                }
            }
        });*/



        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(infoDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        infoDialog.show();
        infoDialog.getWindow().setAttributes(lp);

        ApiManager apiManager = new ApiManager(ApiManager.PATIENT_DETAIL+"/"+patientIdGCM,"get",null,this, activity);
        apiManager.loadURL();
    }

    public static void showPicDialog(Activity activity,String imgURL){
        final Dialog picDialog = new Dialog(activity,R.style.TransparentThemeH4B);
        picDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        picDialog.setContentView(R.layout.pic_dialog);

        PhotoView ivPhoto = (PhotoView) picDialog.findViewById(R.id.ivPhoto);

        DATA.loadImageFromURL(imgURL,R.drawable.ic_placeholder_2,ivPhoto);

        picDialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picDialog.dismiss();
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(picDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        picDialog.show();
        picDialog.getWindow().setAttributes(lp);
    }

    public static void showImgMsgDialog(Activity activity,String imgURL, String tittleTxt){
        final Dialog picDialog = new Dialog(activity);
        picDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        picDialog.setContentView(R.layout.pic_dialog);

        PhotoView ivPhoto = (PhotoView) picDialog.findViewById(R.id.ivPhoto);
        TextView dialogTittle = (TextView) picDialog.findViewById(R.id.dialogTittle);

        dialogTittle.setText(tittleTxt);

        DATA.loadImageFromURL(imgURL,R.drawable.ic_placeholder_2,ivPhoto);

        picDialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picDialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(picDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        picDialog.show();
        picDialog.getWindow().setAttributes(lp);
    }

    @Override
    public void fetchDataCallback(String status, String apiName, String content) {

        if(apiName.contains(ApiManager.PATIENT_DETAIL)){
            try {
                JSONObject jsonObject = new JSONObject(content).getJSONObject("data");

                String pName = jsonObject.getJSONObject("patient_data").getString("first_name")+" "+
                        jsonObject.getJSONObject("patient_data").getString("last_name");
                String pImage = jsonObject.getJSONObject("patient_data").getString("pimage");
                tvSelPtPhone.setText("Mobile: "+jsonObject.getJSONObject("patient_data").getString("phone"));
                tvSelPtEmail.setText("Email: "+jsonObject.getJSONObject("patient_data").getString("email"));
                tvSelPtAddress.setText("Address: "+jsonObject.getJSONObject("patient_data").getString("residency"));
                tvSelPtDOB.setText("DOB: "+jsonObject.getJSONObject("patient_data").getString("birthdate"));

                tvSelPtName.setText(pName);

                DATA.loadImageFromURL(pImage , R.drawable.icon_call_screen,imgSelPtImage);
                /*ptPolicyNo = jsonObject.getJSONObject("patient_data").getString("policy_number");
                ptDOB  = jsonObject.getJSONObject("patient_data").getString("birthdate");
                ptAddress = jsonObject.getJSONObject("patient_data").getString("residency");
                ptZipcode  = jsonObject.getJSONObject("patient_data").getString("zipcode");
                ptPhone = jsonObject.getJSONObject("patient_data").getString("phone");*/
                //ptRefDate  = jsonObject.getJSONObject("patient_data").getString("phone");
                //ptRefDr  = jsonObject.getJSONObject("patient_data").getString("phone")+" "
                //+jsonObject.getJSONObject("patient_data").getString("phone");
                //ptPriHomeCareDiag  = jsonObject.getJSONObject("patient_data").getString("phone");

                if(!jsonObject.getString("virtual_visit").isEmpty()){
                    JSONObject virtual_visit = jsonObject.getJSONObject("virtual_visit");
                    if(virtual_visit.has("dateof")){
                        tvVitalsDate.setText("Date: "+virtual_visit.getString("dateof"));
                    }
                    if(virtual_visit.has("ot_data")&& !virtual_visit.getString("ot_data").isEmpty()){
                        JSONObject virtual_ot_data = new JSONObject(virtual_visit.getString("ot_data"));
                        if(virtual_ot_data.has("ot_respirations")){
                            String ot_respirations = virtual_ot_data.getString("ot_respirations");
                            etOTRespirations.setText(ot_respirations);
                        }
                        if(virtual_ot_data.has("ot_blood_sugar")){
                            String ot_blood_sugar = virtual_ot_data.getString("ot_blood_sugar");
                            etOTBloodSugar.setText(ot_blood_sugar);
                        }
                        if(virtual_ot_data.has("ot_hr")){
                            String ot_hr = virtual_ot_data.getString("ot_hr");
                            etOTHR.setText(ot_hr);
                        }
                        if(virtual_ot_data.has("ot_bp")){
                            String ot_bp = virtual_ot_data.getString("ot_bp");
                            etOTBP.setText(ot_bp);
                        }
                        if(virtual_ot_data.has("ot_saturation")){
                            String ot_saturation = virtual_ot_data.getString("ot_saturation");
                            etOTO2Saturations.setText(ot_saturation);
                        }

                        if(virtual_ot_data.has("ot_height")){
                            String ot_height = virtual_ot_data.getString("ot_height");
                            etOTHeight.setText(ot_height);
                        }
                        if(virtual_ot_data.has("ot_temperature")){
                            String ot_temperature = virtual_ot_data.getString("ot_temperature");
                            etOTTemperature.setText(ot_temperature);
                        }
                        if(virtual_ot_data.has("ot_weight")){
                            String ot_weight = virtual_ot_data.getString("ot_weight");
                            etOTWeight.setText(ot_weight);
                        }

                        if(virtual_ot_data.has("ot_bmi")){
                            String bmi = virtual_ot_data.getString("ot_bmi");
                            etOTBMI.setText(bmi);
                        }
                    }

                    tvPtSymptom.setText(virtual_visit.getString("symptom_name"));
                    tvPtCondition.setText(virtual_visit.getString("condition_name"));
                    tvPtDescription.setText(virtual_visit.getString("description"));
                    tvSymptomDetails.setText(virtual_visit.getString("symptom_details"));

                    if(virtual_visit.has("additional_data")){
                        String additional_data = virtual_visit.getString("additional_data");
                        if(!additional_data.isEmpty()){
                            JSONObject jbjb = new JSONObject(additional_data);
                            if(jbjb.has("pain_where")){
                                tvPain.setText(jbjb.getString("pain_where"));
                            }
                            if(jbjb.has("pain_severity")){
                                tvPainSeverity.setText(jbjb.getString("pain_severity"));
                            }
                        }
                    }
                    if(virtual_visit.has("pain_related")){
                        tvPainBodyPart.setText(virtual_visit.getString("pain_related"));
                    }

                    JSONArray reports = virtual_visit.getJSONArray("reports");
                    final ArrayList<String> vvImgs = new ArrayList<>();
                    for (int i = 0; i < reports.length(); i++) {
                        vvImgs.add(reports.getJSONObject(i).getString("report_name"));
                    }
                    gvReportImages.setAdapter(new VVReportImagesAdapter2(activity,vvImgs));
                    gvReportImages.setExpanded(true);
                    gvReportImages.setPadding(5,5,5,5);

                    gvReportImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            showPicDialog(activity,vvImgs.get(position));
                        }
                    });

                    //svPatientDetails.scrollTo(0,0);
                }


                if(jsonObject.has("medical_history")){
                    JSONObject medical_history = jsonObject.getJSONObject("medical_history");

                    String phistory = medical_history.getString("phistory");
                    etSOAPHistoryMedical.setText(phistory);

                    String is_smoke = medical_history.getString("is_smoke");
                    String smoke_detail = medical_history.getString("smoke_detail");
                    String is_drink = medical_history.getString("is_drink");
                    String drink_detail = medical_history.getString("drink_detail");
                    String is_drug = medical_history.getString("is_drug");
                    String drug_detail = medical_history.getString("drug_detail");

                    StringBuilder socialHistory = new StringBuilder();
                    /*if (is_smoke.equalsIgnoreCase("1")) {
                        socialHistory.append("Smoke:Yes, ");
                        if ((!smoke_detail.isEmpty()) || (!smoke_detail.equalsIgnoreCase("null"))) {
                            String[] smokeArr = smoke_detail.split("/");
                            if (smokeArr.length < 2) {

                            }else {
                                socialHistory.append("How long: "+smokeArr[0]+", How much: "+smokeArr[1]);
                            }
                        }
                    }*/
                    if(is_smoke.equalsIgnoreCase("0")){
                        String arr[] = smoke_detail.split("\\|");
                        String smokeType = "-", smokeAge = "-", smokeHowMuchPerDay = "-", smokeReadyToQuit = "-";
                        try {smokeType = arr[0]; }catch (Exception e){e.printStackTrace();}
                        try {smokeAge = arr[1]; }catch (Exception e){e.printStackTrace();}
                        try {smokeHowMuchPerDay = arr[2];}catch (Exception e){e.printStackTrace();}
                        try {smokeReadyToQuit = arr[3];}catch (Exception e){e.printStackTrace();}

                        socialHistory.append("Smoking Status : Current Smoker");
                        socialHistory.append(", Type : "+smokeType);
                        socialHistory.append(", What age did you start : "+smokeAge);
                        socialHistory.append(", How much per day : "+smokeHowMuchPerDay);
                        socialHistory.append(", Are you ready to quit : "+smokeReadyToQuit);

                    }else if(is_smoke.equalsIgnoreCase("1")){
                        String arr[] = smoke_detail.split("\\|");
                        String smokeType = "-", smokeHowLongDidU = "-", smokeQuitDate = "-";
                        try {smokeType = arr[0]; }catch (Exception e){e.printStackTrace();}
                        try {smokeHowLongDidU = arr[1]; }catch (Exception e){e.printStackTrace();}
                        try {smokeQuitDate = arr[2];}catch (Exception e){e.printStackTrace();}
                        socialHistory.append("Smoking Status : Former Smoker");
                        socialHistory.append(", Type : "+smokeType);
                        socialHistory.append(", How long did you smoke : "+smokeHowLongDidU);
                        socialHistory.append(", Quit date : "+smokeQuitDate);
                    }else if(is_smoke.equalsIgnoreCase("2")){
                        socialHistory.append("Smoking Status : Non Smoker");
                    }else{
                        socialHistory.append("Smoke:No, ");
                    }

                    if (is_drink.equalsIgnoreCase("1")) {
                        socialHistory.append("\nDrink alcohol:Yes, ");
                        if ((!drink_detail.isEmpty()) || (!drink_detail.equalsIgnoreCase("null"))) {
                            String[] drinkArr = drink_detail.split("/");
                            if (drinkArr.length < 2) {

                            }else {
                                socialHistory.append("How long: "+drinkArr[0]+", How much: "+drinkArr[1]);
                            }
                        }
                    }else{
                        socialHistory.append("Drink alcohol:No, ");
                    }

                    if (is_drug.equalsIgnoreCase("1")) {
                        socialHistory.append("\nUse Drug:Yes, ");
                        if ((!drug_detail.isEmpty()) || (!drug_detail.equalsIgnoreCase("null"))) {
                            socialHistory.append("Detail: "+drug_detail);
                        }
                    }else{
                        socialHistory.append("Use Drug:No");
                    }
                    socialHistory.append("\nOther: "+medical_history.getString("social_other"));

                    etSOAPHistorySocial.setText(socialHistory.toString());

                    String relation_had = medical_history.getString("relation_had");
                    String relation_had_name = medical_history.getString("relation_had_name");
                    String[] rhArr = relation_had.split("/");
                    String[] rhNameArr = relation_had_name.split("/");
                    StringBuilder sbFamilyHistory = new StringBuilder();
                    for (int i = 0; i < rhNameArr.length; i++) {
                        sbFamilyHistory.append(rhArr[i]+" : "+rhNameArr[i]);
                        if(i < (rhNameArr.length-1)){
                            sbFamilyHistory.append("\n");
                        }
                    }
                    etSOAPHistoryFamily.setText(sbFamilyHistory.toString());


                    etSOAPHistoryMedications.setText("Medication: "+medical_history.getString("medication_detail")
                            +"\n"+"Other: "+medical_history.getString("other"));
                    etSOAPHistoryAllergies.setText(medical_history.getString("alergies_detail"));
                }else {
                    nsvHistory.setVisibility(View.GONE);
                    tvHistoryBoxTittle.setText("Medical history not available");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                //customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }
}
