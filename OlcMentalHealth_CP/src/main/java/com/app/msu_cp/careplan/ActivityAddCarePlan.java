package com.app.msu_cp.careplan;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.msu_cp.BaseActivity;
import com.app.msu_cp.R;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.util.DATA;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityAddCarePlan extends BaseActivity {


    Button btnAddCarePlan;
    Spinner spPrefMethod,spPtCompLifePlan,spWasLifePalnDisc,spEduMatSent,spFollowupDisc;
    EditText etMethodInput,etDelCMLTSS,etOutComeOfDisc,etNameOfMaterials;
    LinearLayout spPtCompLifePlan_IfNo,spEduMatSent_IfYes;
    TextView etOutComeOfDiscLabel;

    String [] yesNoArr = {"-- Select --" , "Yes", "No"};
    String [] prefMethodOfCommArr = {"-- Select Method --","Phone","Text","Email","Mail"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_care_plan);

        btnAddCarePlan = (Button) findViewById(R.id.btnAddCarePlan);

        spPrefMethod = (Spinner) findViewById(R.id.spPrefMethod);
        spPtCompLifePlan = (Spinner) findViewById(R.id.spPtCompLifePlan);
        spWasLifePalnDisc = (Spinner) findViewById(R.id.spWasLifePalnDisc);
        spEduMatSent = (Spinner) findViewById(R.id.spEduMatSent);
        spFollowupDisc = (Spinner) findViewById(R.id.spFollowupDisc);

        etMethodInput = (EditText) findViewById(R.id.etMethodInput);
        etDelCMLTSS = (EditText) findViewById(R.id.etDelCMLTSS);
        etOutComeOfDisc = (EditText) findViewById(R.id.etOutComeOfDisc);
        etNameOfMaterials = (EditText) findViewById(R.id.etNameOfMaterials);

        spPtCompLifePlan_IfNo = (LinearLayout) findViewById(R.id.spPtCompLifePlan_IfNo);
        spEduMatSent_IfYes = (LinearLayout) findViewById(R.id.spEduMatSent_IfYes);

        etOutComeOfDiscLabel = (TextView) findViewById(R.id.etOutComeOfDiscLabel);

        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getId()){
                    case R.id.spPtCompLifePlan:
                        if(yesNoArr[position].equalsIgnoreCase("No")){
                            spPtCompLifePlan_IfNo.setVisibility(View.VISIBLE);
                        }else {
                            spPtCompLifePlan_IfNo.setVisibility(View.GONE);
                        }
                        break;
                    case R.id.spEduMatSent:
                        if(yesNoArr[position].equalsIgnoreCase("Yes")){
                            spEduMatSent_IfYes.setVisibility(View.VISIBLE);
                        }else {
                            spEduMatSent_IfYes.setVisibility(View.GONE);
                        }
                        break;
                    case R.id.spWasLifePalnDisc:
                        if(yesNoArr[position].equalsIgnoreCase("Yes")){
                            etOutComeOfDisc.setHint("Enter outcome of discussion");
                            etOutComeOfDiscLabel.setText("Outcome of discussion :");
                        }else {
                            etOutComeOfDisc.setHint("If not discussed, why ?");
                            etOutComeOfDiscLabel.setText("If not discussed, why ?");
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        spPtCompLifePlan.setOnItemSelectedListener(onItemSelectedListener);
        spEduMatSent.setOnItemSelectedListener(onItemSelectedListener);
        spWasLifePalnDisc.setOnItemSelectedListener(onItemSelectedListener);


        ArrayAdapter<String> spPrefMethodAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, prefMethodOfCommArr);
        spPrefMethod.setAdapter(spPrefMethodAdapter);

        ArrayAdapter<String> spYesNoAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, yesNoArr);
        spPtCompLifePlan.setAdapter(spYesNoAdapter);
        spWasLifePalnDisc.setAdapter(spYesNoAdapter);
        spEduMatSent.setAdapter(spYesNoAdapter);
        spFollowupDisc.setAdapter(spYesNoAdapter);



        btnAddCarePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //validate here !


                RequestParams params = new RequestParams();

                String method_of_communication = prefMethodOfCommArr[spPrefMethod.getSelectedItemPosition()];
                String method_input = etMethodInput.getText().toString();
                String preferences_delivery_services = etDelCMLTSS.getText().toString();
                String completed_planning_activities = yesNoArr[spPtCompLifePlan.getSelectedItemPosition()];
                String were_educational_materials_sent = yesNoArr[spEduMatSent.getSelectedItemPosition()];
                String discussion_scheduled = yesNoArr[spFollowupDisc.getSelectedItemPosition()];

                params.put("completed_planning_activities",completed_planning_activities);
                if(completed_planning_activities.equalsIgnoreCase("No")){
                    String life_planning_discussed = yesNoArr[spWasLifePalnDisc.getSelectedItemPosition()];

                    params.put("life_planning_discussed",life_planning_discussed);

                    if(life_planning_discussed.equalsIgnoreCase("Yes")){
                        String outcome_of_discussion = etOutComeOfDisc.getText().toString();
                        params.put("outcome_of_discussion",outcome_of_discussion);
                    }else if(life_planning_discussed.equalsIgnoreCase("No")){
                        String not_discussed_why = etOutComeOfDisc.getText().toString();
                        params.put("not_discussed_why",not_discussed_why);
                    }
                }
                params.put("were_educational_materials_sent",were_educational_materials_sent);
                if(were_educational_materials_sent.equalsIgnoreCase("Yes")){
                    String name_of_materials = etNameOfMaterials.getText().toString();
                    params.put("name_of_materials",name_of_materials);
                }

                params.put("method_of_communication",method_of_communication);
                params.put("method_input",method_input);
                params.put("preferences_delivery_services",preferences_delivery_services);
                params.put("discussion_scheduled",discussion_scheduled);

                params.put("patient_id", DATA.selectedUserCallId);
                params.put("doctor_id",prefs.getString("id",""));

                ApiManager apiManager = new ApiManager(ApiManager.ADD_CARE_PLAN,"post",params,apiCallBack, activity);
                apiManager.loadURL();
            }
        });
    }


    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        super.fetchDataCallback(status, apiName, content);
        if(apiName.equalsIgnoreCase(ApiManager.ADD_CARE_PLAN)){
            //{"status":"success","message":"Saved","data":7}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.getString("status").equalsIgnoreCase("success")){
                    customToast.showToast("Care Plan has been created",0,1);
                    ActivityCarePlan.shouldLoadCP = true;
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }
}
