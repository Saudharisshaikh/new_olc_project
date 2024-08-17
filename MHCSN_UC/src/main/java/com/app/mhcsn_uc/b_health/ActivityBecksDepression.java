package com.app.mhcsn_uc.b_health;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mhcsn_uc.BaseActivity;
import com.app.mhcsn_uc.GetLiveCare;
import com.app.mhcsn_uc.R;
import com.app.mhcsn_uc.api.ApiManager;
import com.app.mhcsn_uc.util.DATA;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityBecksDepression extends BaseActivity {


    ListView lvBD;
    TextView tvTotalScore, tvScoreCriteria, tvSubmitSurvey;

    public boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_becks_depression);

        isEdit = getIntent().getBooleanExtra("isEdit",false);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Beck's Depression Inventory");

        //GetLiveCareFormBhealth.assessmentsAdded.add(getSupportActionBar().getTitle()+"");

        lvBD = findViewById(R.id.lvBD);
        tvTotalScore = findViewById(R.id.tvTotalScore);
        tvScoreCriteria = findViewById(R.id.tvScoreCriteria);
        tvSubmitSurvey = findViewById(R.id.tvSubmitSurvey);

        Button btnToolbar = (Button) findViewById(R.id.btnToolbar);

        btnToolbar.setText("Submit");

        int visibility = isEdit ? View.GONE : View.VISIBLE;
        btnToolbar.setVisibility(visibility);
        tvSubmitSurvey.setVisibility(visibility);
        int visibilityR = !isEdit ? View.GONE : View.VISIBLE;
        tvScoreCriteria.setVisibility(visibilityR);

        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitDepSurvey();
            }
        });

        tvSubmitSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitDepSurvey();
            }
        });


        ApiManager apiManager = new ApiManager(ApiManager.DEP_INV_JSON,"get",null,apiCallBack, activity);
        apiManager.loadURL();
    }


    public void submitDepSurvey(){
        RequestParams params = new RequestParams();
        params.put("patient_id", prefs.getString("id", ""));
        params.put("score", totalScore+"");
        for (int i = 0; i < bdBeans.size(); i++) {
                    /*answer[0]=2
                    answer[1]=0
                    answer[2]=2
                    answer[3]=2*/
            DATA.print("answer["+i+"]"   + " = "  + bdBeans.get(i).score);

            if(bdBeans.get(i).isGroupSelected){
                params.put("answer["+i+"]", bdBeans.get(i).score +"");
            }
        }

        ApiManager apiManager = new ApiManager(ApiManager.SAVE_DEP_INV,"post",params,apiCallBack, activity);
        apiManager.loadURL();

                /*if(lvBeckDepAdapter != null){
                    LvBeckDepAdapter.validateFlag = true;
                    lvBeckDepAdapter.notifyDataSetChanged();
                }*/
    }


    List<BDBean> bdBeans;
    LvBeckDepAdapter lvBeckDepAdapter;
    public void parseData(String jsonStr){
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            bdBeans = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray innerArray = jsonArray.getJSONArray(i);
                List<String> data = new ArrayList<>();
                for (int j = 0; j < innerArray.length(); j++) {
                    data.add(innerArray.getString(j).trim());
                }

                BDBean bdBean = new BDBean(data);

                if(isEdit){
                    //view data part starts
                    try {
                        JSONObject dataJSON = new JSONObject(ActivityDepInvList.selectedDepInvListBean.data);
                        if(dataJSON.has(i + "")){
                            bdBean.isGroupSelected = true;
                            bdBean.score = dataJSON.getInt(i+"");
                            bdBean.seletedRadioIndex = dataJSON.getInt(i+"");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    //view data part ends
                }

                bdBeans.add(bdBean);
            }

            lvBeckDepAdapter = new LvBeckDepAdapter(activity, bdBeans);
            lvBD.setAdapter(lvBeckDepAdapter);
        } catch (JSONException e) {
            e.printStackTrace();

            customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
        }
    }


    int totalScore = 0;
    public void sumUpScore(){
        totalScore = 0;
        for (int i = 0; i < bdBeans.size(); i++) {
            totalScore = totalScore + bdBeans.get(i).score;

            //DATA.print("--  Total Score: "+totalScore);
            //DATA.print("-- Index : "+i+" isGroupSelected: "+bdBeans.get(i).isGroupSelected+" checkedRadio: "+bdBeans.get(i).seletedRadioIndex);
        }

        tvTotalScore.setText("Total Score : "+totalScore);
        tvScoreCriteria.setText(getScoreCrieteria(totalScore));
    }

    /*1-10____________________These ups and downs are considered normal
      11-16___________________ Mild mood disturbance
      17-20___________________Borderline clinical depression
      21-30___________________Moderate depression
      31-40___________________Severe depression
      over 40__________________Extreme depression*/

    public String getScoreCrieteria(int score){
        String criteria = "";
        if(score >=1  && score <= 10){
            criteria = "(1-10) These ups and downs are considered normal";
        }else if(score >=11  && score <= 16){
            criteria = "(11-16) Mild mood disturbance ";
        }else if(score >=17  && score <= 20){
            criteria = "(17-20) Borderline clinical depression";
        }else if(score >=21  && score <= 30){
            criteria = "(21-30) Moderate depression";
        }else if(score >=31  && score <= 40){
            criteria = "(31-40) Severe depression";
        }
        return criteria;
    }


    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        super.fetchDataCallback(httpStatus, apiName, content);

        if(apiName.equalsIgnoreCase(ApiManager.DEP_INV_JSON)){
            parseData(content);
        }else if(apiName.equalsIgnoreCase(ApiManager.SAVE_DEP_INV)){
            //{"success":"Form Submitted"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.has("success")){

                    /*customToast.showToast(jsonObject.getString("success"), 0 , 1);
                    ActivityDepInvList.shoulRefresh = true;
                    finish();*/

                    showDepSavedDialog();


                }
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }


    public void showDepSavedDialog(){
        final Dialog dialogSupport = new Dialog(activity);
        dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSupport.setContentView(R.layout.dialog_dep_saved);
        dialogSupport.setCancelable(false);

        Button btnMakeApptmnt = dialogSupport.findViewById(R.id.btnMakeApptmnt);
        Button btnCancel = dialogSupport.findViewById(R.id.btnCancel);
        TextView tvScore = dialogSupport.findViewById(R.id.tvScore);
        TextView tvScoreCri = dialogSupport.findViewById(R.id.tvScoreCri);

        tvScore.setText(tvTotalScore.getText());
        tvScoreCri.setText(tvScoreCriteria.getText());

        btnMakeApptmnt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();

                openActivity.open(GetLiveCare.class, true);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();
                finish();
            }
        });
        dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSupport.show();

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSupport.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogSupport.show();
        dialogSupport.getWindow().setAttributes(lp);*/
    }
}
