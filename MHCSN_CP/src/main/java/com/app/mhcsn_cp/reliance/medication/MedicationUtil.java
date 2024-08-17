package com.app.mhcsn_cp.reliance.medication;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.fragment.app.DialogFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mhcsn_cp.DoctorsList;
import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.api.ApiCallBack;
import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.model.DoctorsModel;
import com.app.mhcsn_cp.model.DrugBean;
import com.app.mhcsn_cp.model.PotencyUnitBean;
import com.app.mhcsn_cp.util.CheckInternetConnection;
import com.app.mhcsn_cp.util.CustomToast;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.DatePickerFragment;
import com.app.mhcsn_cp.util.GloabalMethods;
import com.app.mhcsn_cp.util.HideShowKeypad;
import com.app.mhcsn_cp.util.PrescriptionsPopup;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MedicationUtil implements ApiCallBack {


    AppCompatActivity activity;
    SharedPreferences prefs;
    CheckInternetConnection checkInternetConnection;
    CustomToast customToast;

    //PrescriptionsPopup prescriptionsPopup;


    public MedicationUtil(AppCompatActivity activity) {
        this.activity = activity;
        prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        //gloabalMethods = new GloabalMethods(activity);

        //prescriptionsPopup = new PrescriptionsPopup(activity);
    }



    DrugBean selectedDrugBean;
    Dialog drugsDialog;
    Spinner spinnerDrugName;
    //String dosage_form = "";


    //String dosage = "",freq1 = "",freq2 = "";//dosage_formVal = "",
    public void initDrugsDialog() {
        drugsDialog = new Dialog(activity, R.style.TransparentThemeH4B);
        drugsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        drugsDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        drugsDialog.setContentView(R.layout.dialog_add_drugs_1);
        drugsDialog.setCancelable(false);

        drugsDialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drugsDialog.dismiss();
            }
        });

        final EditText etPurpose = (EditText) drugsDialog.findViewById(R.id.etPurpose);
        final EditText etNotes = (EditText) drugsDialog.findViewById(R.id.etNotes);

        final EditText etSearchQuery = (EditText) drugsDialog.findViewById(R.id.etSearchQuery);
        ImageView ivSearchQuery = (ImageView) drugsDialog.findViewById(R.id.ivSearchQuery);
        //final Spinner spinnerDrugForm = (Spinner) drugsDialog.findViewById(R.id.spinnerDrugForm);
        spinnerDrugName = (Spinner) drugsDialog.findViewById(R.id.spinnerDrugName);
        final Spinner spinnerRoute = (Spinner) drugsDialog.findViewById(R.id.spinner1);
        final Spinner spinnerDosageForm = (Spinner) drugsDialog.findViewById(R.id.spinner2);
        final Spinner spinnerStrength = (Spinner) drugsDialog.findViewById(R.id.spinner3);
        final Spinner spinnerUnit = (Spinner) drugsDialog.findViewById(R.id.spinner4);



		 /*Spinner spinnerDosage = (Spinner) drugsDialog.findViewById(R.id.spinner5);
		 Spinner spinnerFrequency1 = (Spinner) drugsDialog.findViewById(R.id.spinner6);
		 Spinner spinnerFrequency2 = (Spinner) drugsDialog.findViewById(R.id.spinner7);*/
        final Spinner spinnerRefill = (Spinner) drugsDialog.findViewById(R.id.spinner8);

        final Spinner spinner_potency_unit = (Spinner) drugsDialog.findViewById(R.id.spinner_potency_unit);
        final EditText etTotalQuantity = (EditText) drugsDialog.findViewById(R.id.etTotalQuantity);

        Button btnAddDrugs = (Button) drugsDialog.findViewById(R.id.btnAddDrugs);
        Button btnAddDrugsCancel = (Button) drugsDialog.findViewById(R.id.btnAddDrugsCancel);
        final EditText etStartDate = (EditText) drugsDialog.findViewById(R.id.etStartDate);
        final EditText etEndtDate = (EditText) drugsDialog.findViewById(R.id.etEndtDate);

        final EditText etInstructions = (EditText) drugsDialog.findViewById(R.id.etInstructions);
        ImageView ic_mike_Instructions = (ImageView) drugsDialog.findViewById(R.id.ic_mike_Instructions);
        ic_mike_Instructions.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //startVoiceRecognitionActivity(etInstructions);
            }
        });

        etStartDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                DialogFragment newFragment = new DatePickerFragment(etStartDate);
                newFragment.show(activity.getSupportFragmentManager(), "datePicker");
            }
        });
        etEndtDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                DialogFragment newFragment = new DatePickerFragment(etEndtDate);
                newFragment.show(activity.getSupportFragmentManager(), "datePicker");
            }
        });



        EditText etPrescriber = drugsDialog.findViewById(R.id.etPrescriber);
        etPrescriber.setText(precriberName);
        etPrescriber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyDoctorsDialog(etPrescriber);
            }
        });


		 /*if (dosageFormsBeans != null) {
			 ArrayAdapter<DosageFormsBean> spDosageFormAdapter = new ArrayAdapter<DosageFormsBean>(
				        activity,
				        R.layout.view_spinner_item,
				        dosageFormsBeans
				);
			 spDosageFormAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			 spinnerDrugForm.setAdapter(spDosageFormAdapter);
		} else {
			if (checkInternetConnection.isConnectedToInternet()) {
				getDosageForms();
			} else {
				Toast.makeText(activity, "No internet connection", 0).show();
			}
		}*/


		 /*spinnerDrugForm.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				dosage_form = dosageFormsBeans.get(arg2).getField_value();
				dosage_formVal = dosageFormsBeans.get(arg2).getDosage_form();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});*/

        //final String[] dosage={"1","2","3","4","5","6","7","8","9","10"};//,"11","12","13","14","15","16","17","18","19","20"

		 /*ArrayAdapter<String> spDosageAdapter = new ArrayAdapter<String>(
			        this,
			         R.layout.spinner_item_lay,
			        dosage
			);
		 spDosageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerDosage.setAdapter(spDosageAdapter);


			//final String[] f1={"once","twice","thrice"};
			 ArrayAdapter<String> spFreq1Adapter = new ArrayAdapter<String>(
				        this,
				         R.layout.spinner_item_lay,
				        f1
				);
			 spFreq1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerFrequency1.setAdapter(spFreq1Adapter);

			//final String[] f2={"day","week","month"};
			 ArrayAdapter<String> spFreq2Adapter = new ArrayAdapter<String>(
				        this,
				         R.layout.spinner_item_lay,
				        f2
				);
			 spFreq2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerFrequency2.setAdapter(spFreq2Adapter);


		    spinnerDosage.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					AfterCallDialog.this.dosage = dosage[arg2];
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
		    spinnerFrequency1.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					freq1 = f1[arg2];
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
		    spinnerFrequency2.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					freq2 = f2[arg2];
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});*/

        ivSearchQuery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //hideShowKeypad.hideSoftKeyboard();
                // TODO Auto-generated method stub
                if (etSearchQuery.getText().toString().trim().isEmpty() || etSearchQuery.getText().toString().trim().length()==1) {
                    Toast.makeText(activity, "Please enter at least 2 characters of a medication name to search the medication", Toast.LENGTH_LONG).show();
                }else if (!checkInternetConnection.isConnectedToInternet()) {
                    Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
                } else {

                    getDrugs(etSearchQuery.getText().toString().trim());
                }
            }
        });

        etSearchQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (etSearchQuery.getText().toString().trim().isEmpty() || etSearchQuery.getText().toString().trim().length() < 2) {
                        Toast.makeText(activity, "Please enter atleast 2 characters of a drug name to search medication", Toast.LENGTH_LONG).show();
                        etSearchQuery.setError("Please enter at least 2 characters of a medication name to search the medication");
                    }else if (!checkInternetConnection.isConnectedToInternet()) {
                        Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
                    } else {
                        getDrugs(etSearchQuery.getText().toString().trim());
                    }
                    //return true; return true not closes keyboard
                    return false;
                }
                return false;
            }
        });


        spinnerDrugName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                // TODO Auto-generated method stub
                selectedDrugBean = drugBeans.get(pos);

                ArrayList<String> route = new ArrayList<String>();
                route.add(selectedDrugBean.getRoute());

                ArrayList<String> dosage_form = new ArrayList<String>();
                dosage_form.add(selectedDrugBean.getDosage_form());

                ArrayList<String> strength = new ArrayList<String>();
                strength.add(selectedDrugBean.getStrength());

                ArrayList<String> strength_unit_of_measure = new ArrayList<String>();
                strength_unit_of_measure.add(selectedDrugBean.getStrength_unit_of_measure());

                //------------------------potency_unit------------------------------------------------

                final ArrayList<PotencyUnitBean> potencyUnitBeans = new ArrayList<>();
                String[] pu = selectedDrugBean.getPotency_unit().split(",");
                String[] pc = selectedDrugBean.getPotency_code().split(",");

                for (int i = 0; i < pu.length; i++) {
                    potencyUnitBeans.add(new PotencyUnitBean(pu[i], pc[i]));
                }

                spinner_potency_unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                        // TODO Auto-generated method stub
                        selectedDrugBean.setPotency_code(potencyUnitBeans.get(pos).getPotency_code());
                        selectedDrugBean.setPotency_unit(potencyUnitBeans.get(pos).getPotency_unit());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }
                });

                ArrayAdapter<PotencyUnitBean> spPtencyUnitAdapter = new ArrayAdapter<PotencyUnitBean>(
                        activity,
                        R.layout.spinner_item_lay,
                        potencyUnitBeans
                );
                spPtencyUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_potency_unit.setAdapter(spPtencyUnitAdapter);

                //--------------set Refill value for selectedDrugBean--------------------------
                final String[] refillArray={"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"
                        ,"16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31",
                        "32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49","50",
                        "51","52","53","54","55","56","57","58","59","60","61","62","63","64","65","66","67","68","69","70",
                        "71","72","73","74","75","76","77","78","79","80","81","82","83","84","85","86","87","88"
                        ,"89","90","91","92","93","94","95","96","97","98","99"};
                spinnerRefill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        // TODO Auto-generated method stub
                        selectedDrugBean.refill = refillArray[arg2];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });


                ArrayAdapter<String> spRefillAdapter = new ArrayAdapter<String>(
                        activity,
                        R.layout.spinner_item_lay,
                        refillArray
                );
                spRefillAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerRefill.setAdapter(spRefillAdapter);

                //---------------------set Refill value for selectedDrugBean----------------------

                //------------------------potency_unit----------------------------------------------------
                ArrayAdapter<String> sprouteAdapter = new ArrayAdapter<String>(
                        activity,
                        R.layout.spinner_item_lay,
                        route
                );
                sprouteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerRoute.setAdapter(sprouteAdapter);

                ArrayAdapter<String> spdosage_formAdapter = new ArrayAdapter<String>(
                        activity,
                        R.layout.spinner_item_lay,
                        dosage_form
                );
                spdosage_formAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDosageForm.setAdapter(spdosage_formAdapter);

                ArrayAdapter<String> spstrengthAdapter = new ArrayAdapter<String>(
                        activity,
                        R.layout.spinner_item_lay,
                        strength
                );
                spstrengthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerStrength.setAdapter(spstrengthAdapter);

                ArrayAdapter<String> spstrength_unit_of_measureAdapter = new ArrayAdapter<String>(
                        activity,
                        R.layout.spinner_item_lay,
                        strength_unit_of_measure
                );
                spstrength_unit_of_measureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerUnit.setAdapter(spstrength_unit_of_measureAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        etTotalQuantity.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                if (arg0.toString().startsWith(".")) {
                    etTotalQuantity.setText("0"+arg0.toString());
                    etTotalQuantity.setSelection(etTotalQuantity.getText().length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });

        etTotalQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    if (!etTotalQuantity.getText().toString().isEmpty()) {
                        etTotalQuantity.setText(PrescriptionsPopup.validateQuantity(etTotalQuantity.getText().toString()));
                        Toast.makeText(activity, "Leading and trailing zeeros will be truncated from quantity.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        btnAddDrugsCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                drugsDialog.dismiss();
            }
        });
        btnAddDrugs.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                if (selectedDrugBean!=null) {

                    if (etStartDate.getText().toString().equalsIgnoreCase("Start Date")) {//|| etEndtDate.getText().toString().equalsIgnoreCase("End Date")
                        //Toast.makeText(activity, "Please add medicine start date and end date", Toast.LENGTH_LONG).show();
                        Toast.makeText(activity, "Please add medicine start date", Toast.LENGTH_LONG).show();
                    }else if (etTotalQuantity.getText().toString().isEmpty()) {
                        Toast.makeText(activity, "Please enter total quantity for the medicine", Toast.LENGTH_SHORT).show();
                    }else if (etInstructions.getText().toString().trim().isEmpty()) {
                        Toast.makeText(activity, "Please enter usage instructions for the medicine", Toast.LENGTH_SHORT).show();
                    } else {
                        selectedDrugBean.setDrug_name(selectedDrugBean.getDrug_name());
						/*+"\n"+AfterCallDialog.this.dosage+" "+selectedDrugBean.getDfdesc()+" "+freq1+" a "+freq2
						+"\nFrom date: "+etStartDate.getText().toString()+" To date: "+etEndtDate.getText().toString()*/

                        //this is total quantity for the medicine i.e sent in savePrescription
                        //selectedDrugBean.setDosage_form(validateQuantity(etTotalQuantity.getText().toString()));//AfterCallDialog.this.dosage
                        selectedDrugBean.totalQuantity = PrescriptionsPopup.validateQuantity(etTotalQuantity.getText().toString());

                        selectedDrugBean.start_date = etStartDate.getText().toString();
                        selectedDrugBean.end_date = etEndtDate.getText().toString();

                        if (!etInstructions.getText().toString().trim().isEmpty()) {
							/*selectedDrugBean.setDrug_name(selectedDrugBean.getDrug_name()
									+"\n\nUsage Instructions: "+etInstructions.getText().toString().trim()+"\n");*/

                            selectedDrugBean.instructions = etInstructions.getText().toString().trim();
                        }

                        selectedDrugBean.duration = PrescriptionsPopup.getDateDifference(etStartDate.getText().toString(), etEndtDate.getText().toString());

                        /*DATA.drugBeans.add(selectedDrugBean);
                        DrugsAdapter adapter = new DrugsAdapter(activity);
                        lvDrugs.setAdapter(adapter);
                        lvDrugs.setExpanded(true);// This actually does the magic
                        //lvDrugs.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                        drugsDialog.dismiss();*/

                        String purpose = etPurpose.getText().toString().trim();
                        String notes = etNotes.getText().toString().trim();
                        String autherId = etPrescriber.getText().toString().trim();

                        if(purpose.isEmpty()){etPurpose.setError("Required");return;}
                        if(autherId.isEmpty()){etPrescriber.setError("Required");return;}

                        RequestParams params = new RequestParams();
                        params.put("patient_id", DATA.selectedUserCallId);
                        //params.put("author_id", prefs.getString("id", ""));
                        params.put("name", selectedDrugBean.getDrug_name());
                        params.put("strength", ((String) spinnerStrength.getAdapter().getItem(spinnerStrength.getSelectedItemPosition()))+ ((String) spinnerUnit.getAdapter().getItem(spinnerUnit.getSelectedItemPosition())));
                        params.put("route", selectedDrugBean.getRoute());
                        params.put("directions", selectedDrugBean.instructions);
                        params.put("start_date", selectedDrugBean.start_date);
                        if(!selectedDrugBean.end_date.equalsIgnoreCase("End Date")){
                            params.put("stop_date", selectedDrugBean.end_date);
                        }
                        params.put("purpose", purpose);
                        params.put("notes", notes);
                        params.put("author_id", autherId);
                        params.put("modified_by", prefs.getString("id", ""));

                        ApiManager apiManager = new ApiManager(ApiManager.MEDICATION_ADD,"post",params,MedicationUtil.this, activity);
                        apiManager.loadURL();
                    }
                } else {
                    Toast.makeText(activity, "Please select a medicine to add", Toast.LENGTH_LONG).show();
                }

            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(drugsDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        drugsDialog.show();
        drugsDialog.getWindow().setAttributes(lp);
    }




    ArrayList<DrugBean> drugBeans;
    public void getDrugs(String keyword) {

        DATA.showLoaderDefault(activity,"");

        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity,client);
        RequestParams params = new RequestParams();
        params.put("keyword", keyword);
        //params.put("dosage_form", dosage_form);

        client.post(DATA.baseUrl+"medications/getDrugs", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);

                    DATA.print("--reaponce in getDrugs "+content);
                    try {
                        drugBeans = new ArrayList<DrugBean>();
                        DrugBean temp;

                        JSONArray jsonArray = new JSONObject(content).getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            String drug_descriptor_id = jsonArray.getJSONObject(i).getString("drug_descriptor_id");
                            String route_of_administration = jsonArray.getJSONObject(i).getString("route_of_administration");
                            String drug_name = jsonArray.getJSONObject(i).getString("drug_name");
                            String code = jsonArray.getJSONObject(i).getString("code");
                            String route = jsonArray.getJSONObject(i).getString("route");
                            String strength = jsonArray.getJSONObject(i).getString("strength");
                            String strength_unit_of_measure = jsonArray.getJSONObject(i).getString("strength_unit_of_measure");
                            String dosage_form = jsonArray.getJSONObject(i).getString("dosage_form");
                            String dfcode = jsonArray.getJSONObject(i).getString("dfcode");
                            String dfdesc = jsonArray.getJSONObject(i).getString("dfdesc");

                            String potency_unit = jsonArray.getJSONObject(i).getString("potency_unit");
                            String potency_code = jsonArray.getJSONObject(i).getString("potency_code");

                            temp = new DrugBean(drug_descriptor_id, route_of_administration, drug_name, code, route, strength, strength_unit_of_measure, dosage_form, dfcode, dfdesc,potency_unit,potency_code);
                            drugBeans.add(temp);
                            temp = null;
                        }

                        //setData here

                        ArrayAdapter<DrugBean> spDrugNameAdapter = new ArrayAdapter<DrugBean>(
                                activity,
                                R.layout.view_spinner_item,
                                drugBeans
                        );
                        spDrugNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerDrugName.setAdapter(spDrugNameAdapter);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: getDrugs, http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    DATA.print("--onfail getDrugs " +content);
                    new GloabalMethods(activity).checkLogin(content);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }//end getDrugs


    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.MEDICATION_ADD)){
            //{"status":"success","message":"Saved.."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                customToast.showToast(jsonObject.optString("message"),0,1);
                if(jsonObject.optString("status").equalsIgnoreCase("success")){
                    drugsDialog.dismiss();
                    ((ActivityMedicationList) activity).loadMedications();
                }
            } catch (Exception e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.MY_DOCTORS)){

            try {
                doctorsModels = DoctorsList.parseMyDoc(content);

                int vis = doctorsModels.isEmpty() ? View.VISIBLE : View.GONE;
                tvNoDataMyDocDialog.setVisibility(vis);

                lvDoctorsAdapter2 = new LvDoctorsAdapter2(activity, doctorsModels);
                lvMyDoc.setAdapter(lvDoctorsAdapter2);

                srDoctors.setRefreshing(false);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }






    SwipeRefreshLayout srDoctors;
    TextView tvNoDataMyDocDialog;
    ListView lvMyDoc;
    ArrayList<DoctorsModel> doctorsModels;
    LvDoctorsAdapter2 lvDoctorsAdapter2;
    static String precriberName = "";
    public void showMyDoctorsDialog(EditText etPrescriber){
        Dialog dialogMyDoc = new Dialog(activity,R.style.TransparentThemeH4B);
        dialogMyDoc.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogMyDoc.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogMyDoc.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogMyDoc.setContentView(R.layout.dialog_my_doc);

        ImageView ivCancel = (ImageView) dialogMyDoc.findViewById(R.id.ivCancel);
        lvMyDoc =  dialogMyDoc.findViewById(R.id.lvMyDoc);
        tvNoDataMyDocDialog = dialogMyDoc.findViewById(R.id.tvNoData);

        Button btnOtherDoctor = dialogMyDoc.findViewById(R.id.btnOtherDoctor);

        EditText etSearchDoc = (EditText) dialogMyDoc.findViewById(R.id.etSearchDoc);
        etSearchDoc.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                if (lvDoctorsAdapter2 != null) {
                    lvDoctorsAdapter2.filter(arg0.toString());
                }
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            @Override
            public void afterTextChanged(Editable arg0) {}
        });


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.ivCancel:
                        dialogMyDoc.dismiss();
                        break;
                    case R.id.btnOtherDoctor:
                        showAddOtherDocDialog(etPrescriber, dialogMyDoc);
                        break;
                }
            }
        };

        ivCancel.setOnClickListener(onClickListener);
        btnOtherDoctor.setOnClickListener(onClickListener);


        lvMyDoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialogMyDoc.dismiss();
                precriberName = doctorsModels.get(position).fName+" "+doctorsModels.get(position).lName;
                etPrescriber.setText(precriberName);
                etPrescriber.setTag(doctorsModels.get(position).id);//not used now - name is used instead.
            }
        });

        //======================swip to refresh==================================
        //mySwipeRefreshLayout = fragmentView.findViewById(R.id.swiperefresh);

        srDoctors = dialogMyDoc.findViewById(R.id.srDoctors);
        int colorsArr[] = {Color.parseColor("#3cba54"), Color.parseColor("#f4c20d"), Color.parseColor("#db3236"), Color.parseColor("#4885ed")};
        srDoctors.setColorSchemeColors(colorsArr);
        srDoctors.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if(!checkInternetConnection.isConnectedToInternet()){
                            srDoctors.setRefreshing(false);
                        }else {
                            //toggleViews(true);
                        }
                        ApiManager.shouldShowPD = false;
                        mydoctors();
                    }
                }
        );
        //======================swip to refresh ends=============================


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogMyDoc.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogMyDoc.setCanceledOnTouchOutside(false);
        dialogMyDoc.show();
        dialogMyDoc.getWindow().setAttributes(lp);

        //dialogMyDocG = dialogMyDoc;


        if(doctorsModels != null){
            int vis = doctorsModels.isEmpty() ? View.VISIBLE : View.GONE;
            tvNoDataMyDocDialog.setVisibility(vis);

            lvDoctorsAdapter2 = new LvDoctorsAdapter2(activity, doctorsModels);
            lvMyDoc.setAdapter(lvDoctorsAdapter2);
        }else {
            mydoctors();
        }
    }



    public void mydoctors() {
        RequestParams params = new RequestParams();
        params.put("my_id", prefs.getString("id", ""));

        ApiManager apiManager = new ApiManager(ApiManager.MY_DOCTORS,"post",params,this, activity);
        apiManager.loadURL();

    }//end mydoctors




    public void showAddOtherDocDialog(EditText etPrecriber, Dialog dialogMyDoc){
        Dialog dialogAddOther = new Dialog(activity);
        dialogAddOther.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddOther.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogAddOther.setContentView(R.layout.dialog_add_other_doc);
        //dialogAddOther.setCancelable(false);
        dialogAddOther.setCanceledOnTouchOutside(false);

        EditText etDocName = dialogAddOther.findViewById(R.id.etDocName);
        Button btnSave = dialogAddOther.findViewById(R.id.btnSave);
        Button btnCancel = dialogAddOther.findViewById(R.id.btnCancel);
        ImageView ivCloseDialog = dialogAddOther.findViewById(R.id.ivCloseDialog);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btnSave:
                        precriberName = etDocName.getText().toString().trim();
                        if(TextUtils.isEmpty(precriberName)){
                            etDocName.setError("Required");
                        }else {
                            new HideShowKeypad(activity).hidekeyboardOnDialog();
                            etPrecriber.setText(precriberName);
                            dialogAddOther.dismiss();
                            dialogMyDoc.dismiss();
                        }
                        break;
                    case R.id.btnCancel:
                        dialogAddOther.dismiss();
                        break;
                    case R.id.ivCloseDialog:
                        dialogAddOther.dismiss();
                        break;
                }
            }
        };

        btnSave.setOnClickListener(onClickListener);
        btnCancel.setOnClickListener(onClickListener);
        ivCloseDialog.setOnClickListener(onClickListener);


        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialogAddOther.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialogAddOther.show();
		dialogAddOther.getWindow().setAttributes(lp);*/

        dialogAddOther.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAddOther.show();
    }
}
