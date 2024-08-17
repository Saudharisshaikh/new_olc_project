package com.app.msu_cp.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.speech.RecognizerIntent;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.msu_cp.ActivitySoapNotesEditNew;
import com.app.msu_cp.ActivitySoapNotesEmpty;
import com.app.msu_cp.ActivitySoapNotesNew;
import com.app.msu_cp.R;
import com.app.msu_cp.adapters.DrugsAdapter;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.model.DrugBean;
import com.app.msu_cp.model.PotencyUnitBean;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Engr G M on 8/8/2017.
 */

public class PrescriptionsPopup {

    AppCompatActivity activity;
    SharedPreferences prefs;
    CheckInternetConnection checkInternetConnection;
    CustomToast customToast;
    GloabalMethods gloabalMethods;

    public PrescriptionsPopup(AppCompatActivity activity) {
        this.activity = activity;
        prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        gloabalMethods = new GloabalMethods(activity);

        initPrescripDialog();
    }

    Dialog verDialog;
    public void initDoubleVerficationDialog() {
        verDialog = new Dialog(activity);
        verDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        verDialog.setContentView(R.layout.dialog_verification);

        final TextView tvMessage = (TextView) verDialog.findViewById(R.id.tvMessage);
        final EditText etPincode = (EditText) verDialog.findViewById(R.id.etPincode);
        Button btnEnterPincode = (Button) verDialog.findViewById(R.id.btnEnterPincode);
        Button btnForgotPincode = (Button) verDialog.findViewById(R.id.btnForgotPincode);

        btnEnterPincode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (etPincode.getText().toString().isEmpty()) {
                    customToast.showToast("Please enter your prescription pincode",0,0);
                } else {
                    if (prefs.getString("pincode", "1234").equals(etPincode.getText().toString())) {


                        if(checkInternetConnection.isConnectedToInternet()) {

                            verDialog.dismiss();

                            //sendCallDoneReport();  to remove patient from queue
                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(prescriptionsDialog.getWindow().getAttributes());
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                            prescriptionsDialog.show();
                            prescriptionsDialog.getWindow().setAttributes(lp);

                        }
                        else {

                            customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
                        }

                    } else {
                        tvMessage.setText("Incorrect pincode. Possible typing mistake?");
                    }
                }
            }
        });

        btnForgotPincode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                initForgotPincodeDialogDialog();
            }
        });

        verDialog.show();

    }

    Dialog forgotPincodeDialog;
    public void initForgotPincodeDialogDialog() {
        forgotPincodeDialog = new Dialog(activity);
        forgotPincodeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        forgotPincodeDialog.setContentView(R.layout.dialog_forgot_pincode);


        final EditText etEmailForgotPincode = (EditText) forgotPincodeDialog.findViewById(R.id.etEmailForgotPincode);
        Button btnEnterForgotPincode = (Button) forgotPincodeDialog.findViewById(R.id.btnEnterForgotPincode);
        Button btnCancelForgotPincode = (Button) forgotPincodeDialog.findViewById(R.id.btnCancelForgotPincode);

        btnEnterForgotPincode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (etEmailForgotPincode.getText().toString().isEmpty()) {
                    customToast.showToast("Please enter your email used for OnlineCare account",0,0);
                } else {

                    if(checkInternetConnection.isConnectedToInternet()) {
                        forgotPincode(etEmailForgotPincode.getText().toString());
                    } else {
                        customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
                    }


                }
            }
        });

        btnCancelForgotPincode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                forgotPincodeDialog.dismiss();
            }
        });

        forgotPincodeDialog.show();

    }

    public void forgotPincode(String email) {

        DATA.showLoaderDefault(activity,"");

        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity,client);
        RequestParams params = new RequestParams();

        params.put("email", email);

        client.post(DATA.baseUrl+"/forgotPincode", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);

                    DATA.print("--reaponce in forgotPincode "+content);
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        String status = jsonObject.getString("status");
                        String msg = jsonObject.getString("msg");

                        customToast.showToast(msg, 0, 1);

                        forgotPincodeDialog.dismiss();

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: forgotPincode, http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    DATA.print("--onfail forgotPincode " +content);
                    new GloabalMethods(activity).checkLogin(content, statusCode);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }//end forgotPincode


    ArrayList<DrugBean> drugBeans;
    public void getDrugs(String keyword) {

        DATA.showLoaderDefault(activity,"");

        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity,client);
        RequestParams params = new RequestParams();
        params.put("keyword", keyword);
        //params.put("dosage_form", dosage_form);

        client.post(DATA.baseUrl+"/getDrugs", params, new AsyncHttpResponseHandler() {
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
                    new GloabalMethods(activity).checkLogin(content, statusCode);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }//end getDrugs


    DrugBean selectedDrugBean;
    Dialog drugsDialog;
    Spinner spinnerDrugName;
    //String dosage_form = "";


    //String dosage = "",freq1 = "",freq2 = "";//dosage_formVal = "",
    public void initDrugsDialog() {
        drugsDialog = new Dialog(activity, R.style.TransparentThemeH4B);
        drugsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        drugsDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        drugsDialog.setContentView(R.layout.dialog_add_drugs);
        drugsDialog.setCancelable(false);

        drugsDialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drugsDialog.dismiss();
            }
        });

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
                startVoiceRecognitionActivity(etInstructions);
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
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int pos, long arg3) {
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
                        etTotalQuantity.setText(validateQuantity(etTotalQuantity.getText().toString()));
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

                    if (etStartDate.getText().toString().equalsIgnoreCase("Start Date") ||
                            etEndtDate.getText().toString().equalsIgnoreCase("End Date")) {
                        Toast.makeText(activity, "Please add medicine start date and end date", Toast.LENGTH_LONG).show();
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
                        selectedDrugBean.totalQuantity = validateQuantity(etTotalQuantity.getText().toString());

                        selectedDrugBean.start_date = etStartDate.getText().toString();
                        selectedDrugBean.end_date = etEndtDate.getText().toString();

                        if (!etInstructions.getText().toString().trim().isEmpty()) {
							/*selectedDrugBean.setDrug_name(selectedDrugBean.getDrug_name()
									+"\n\nUsage Instructions: "+etInstructions.getText().toString().trim()+"\n");*/

                            selectedDrugBean.instructions = etInstructions.getText().toString().trim();
                        }

                        selectedDrugBean.duration = getDateDifference(etStartDate.getText().toString(), etEndtDate.getText().toString());

                        DATA.drugBeans.add(selectedDrugBean);
                        DrugsAdapter adapter = new DrugsAdapter(activity);
                        lvDrugs.setAdapter(adapter);
                        lvDrugs.setExpanded(true);// This actually does the magic
                        //lvDrugs.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                        drugsDialog.dismiss();
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


    public static String getDateDifference(String startDate, String endDate) {
        int daysDiff = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date sDate = sdf.parse(startDate);
            Date eDate = sdf.parse(endDate);
            long diff = eDate.getTime() - sDate.getTime();

            DATA.print("--days: "+(int) (diff / (1000*60*60*24)));
            daysDiff = (int) (diff / (1000*60*60*24));
            daysDiff = daysDiff+1;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return daysDiff+" Days";
    }

    public static String validateQuantity(String q){
        long a = 0;
        float b = 0.0f;

        String result = "";


        String[] arr = q.split("\\.");

        //DATA.print("-- arr.length "+arr.length);
        //DATA.print("-- q = "+q);

        if (arr.length > 1) {

            a = Long.parseLong(arr[0]);
            b = Float.parseFloat("."+arr[1]);

            if (b > 0) {
                result = (a+b)+"";
            } else {
                result = a+"";
            }

        } else if(arr.length == 1){
            DATA.print("-- Long.MAX_VALUE : "+Long.MAX_VALUE);
            a = Long.parseLong(arr[0]);

            result = a+"";
        }



        DATA.print("--result: "+result);
        return result;
    }

    EditText editText;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private void startVoiceRecognitionActivity(EditText editText) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // identifying your application to the Google service
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        // hint in the dialog
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, activity.getString(R.string.app_name));
        // hint to the recognizer about what the user is going to say
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // number of results
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        // recognition language
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-US");
        activity.startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
        this.editText = editText;
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            // do whatever you want with the results
            this.editText.setText(matches.get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }*/


    Dialog prescriptionsDialog;
    ImageView ivSignature;
    ExpandableHeightListView lvDrugs;
    ScrollView svSendPres;
    //ArrayList<DrugBean> temp;
    //String vitals = "";
    //String diagnoses = "";

    String treatments ="";

    String drugs = "",quantity = "",directions = "";
    String potency_code = "",refill = "";

    String start_date = "",end_date = "";

    public static String dob,gender,phone,address,pharmacyName,pharmacyPhone;
    public static TextView tvPrescPharmacy, tvPrescPharmacyPhone;

    public void initPrescripDialog() {
        DATA.drugBeans = new ArrayList<DrugBean>();
        prescriptionsDialog = new Dialog(activity,R.style.TransparentThemeH4B);//, android.R.style.Theme_DeviceDefault_Light_Dialog
        prescriptionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        prescriptionsDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        prescriptionsDialog.setContentView(R.layout.send_prescription_dialog);
        prescriptionsDialog.setTitle("Send prescriptions to patient");
        prescriptionsDialog.setCanceledOnTouchOutside(false);
        prescriptionsDialog.setCancelable(false);

        prescriptionsDialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prescriptionsDialog.dismiss();
            }
        });

        TextView tvSendPres = (TextView) prescriptionsDialog.findViewById(R.id.tvSendPresAndDone);
        TextView tvSendPresCencel = (TextView) prescriptionsDialog.findViewById(R.id.tvSendPresCencel);
        final EditText etVitals =  (EditText) prescriptionsDialog.findViewById(R.id.etVitals);
        final EditText etDiagnoses =  (EditText) prescriptionsDialog.findViewById(R.id.etDiagnosis);
        //final EditText etTreatment =  (EditText) prescriptionsDialog.findViewById(R.id.etTreatment);
        lvDrugs =  prescriptionsDialog.findViewById(R.id.lvDrugs);
        svSendPres = (ScrollView) prescriptionsDialog.findViewById(R.id.svSendPres);
        TextView tvPrescPtName = (TextView) prescriptionsDialog.findViewById(R.id.tvPrescPtName);
        TextView tvPrescDate = (TextView) prescriptionsDialog.findViewById(R.id.tvPrescDate);

        TextView tvPrescPatientDOB = (TextView) prescriptionsDialog.findViewById(R.id.tvPrescPatientDOB);
        TextView tvPrescPatientGender = (TextView) prescriptionsDialog.findViewById(R.id.tvPrescPatientGender);
        TextView tvPrescPatientPhone = (TextView) prescriptionsDialog.findViewById(R.id.tvPrescPatientPhone);
        TextView tvPrescPatientAdress = (TextView) prescriptionsDialog.findViewById(R.id.tvPrescPatientAdress);
        tvPrescPharmacy = (TextView) prescriptionsDialog.findViewById(R.id.tvPrescPharmacy);
        tvPrescPharmacyPhone = (TextView) prescriptionsDialog.findViewById(R.id.tvPrescPharmacyPhone);
        TextView tvPrescDrName = (TextView) prescriptionsDialog.findViewById(R.id.tvPrescDrName);
        TextView tvPrescDrPhone = (TextView) prescriptionsDialog.findViewById(R.id.tvPrescDrPhone);


        tvPrescPatientDOB.setText(dob);
        if (gender.equals("1")) {
            tvPrescPatientGender.setText("Male");
        } else {
            tvPrescPatientGender.setText("Female");
        }
        tvPrescPatientPhone.setText(phone);
        tvPrescPatientAdress.setText(address);
        tvPrescPharmacy.setText(pharmacyName);
        tvPrescPharmacyPhone.setText(pharmacyPhone);

        Button btnChangePharmacyPresc = prescriptionsDialog.findViewById(R.id.btnChangePharmacyPresc);
        btnChangePharmacyPresc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GloabalMethods.pharmacyBeans != null) {
                    gloabalMethods.showPharmacyDialog();
                } else {
                    DATA.print("-- pharmacyBeans list is null");
                    if (checkInternetConnection.isConnectedToInternet()) {
                        gloabalMethods.getPharmacy("",true);
                    } else {
                        customToast.showToast("Network not connected", 0, 0);
                    }
                }
            }
        });


        tvPrescDrName.setText(prefs.getString("first_name", "")+" "+prefs.getString("last_name", ""));
        tvPrescDrPhone.setText(prefs.getString("mobile", ""));

        ivSignature = (ImageView) prescriptionsDialog.findViewById(R.id.ivSignature);
        ivSignature.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                initSignatureDialog();

            }
        });


        tvPrescPtName.setText(DATA.selectedUserCallName);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy  h:mm a");
        String currentDateandTime = sdf.format(new Date());
        tvPrescDate.setText(currentDateandTime);

        tvSendPresCencel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                prescriptionsDialog.dismiss();
            }
        });
        tvSendPres.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                /*vitals = etVitals.getText().toString();
                diagnoses = etDiagnoses.getText().toString();
                vitals = vitals;
                ActivitySoapNotes.diagnosis = diagnoses;*/

                //String treatments = etTreatment.getText().toString();
				/*String treatments ="";

				String drugs = "",quantity = "",directions = "";
				String potency_code = "",refill = "";

				String start_date = "",end_date = "";*/
                treatments ="";
                drugs = "";
                quantity = "";
                directions = "";
                potency_code = "";
                refill = "";
                start_date = "";
                end_date = "";

                if (DATA.drugBeans != null) {
                    for (int i = 0; i < DATA.drugBeans.size(); i++) {
                        treatments = treatments+(i+1)+": "+ DATA.drugBeans.get(i).getDrug_name()+"\n";

                        drugs = drugs + DATA.drugBeans.get(i).getDrug_descriptor_id()+",";
                        quantity = quantity + DATA.drugBeans.get(i).totalQuantity+",";
                        directions = directions + DATA.drugBeans.get(i).instructions+"^|";

                        potency_code = potency_code+DATA.drugBeans.get(i).getPotency_code()+",";
                        refill = refill + DATA.drugBeans.get(i).refill+",";

                        start_date = start_date + DATA.drugBeans.get(i).start_date+",";
                        end_date = end_date + DATA.drugBeans.get(i).end_date+",";
                    }
                }else {
                    treatments ="";

                    drugs = "";
                    quantity = "";
                    directions = "";

                    potency_code = "";
                    refill = "";

                    start_date = "";
                    end_date = "";
                }
                if (treatments.isEmpty() || drugs.isEmpty() || quantity.isEmpty() || directions.isEmpty() ||
                        potency_code.isEmpty() || refill.isEmpty()) { //vitals.isEmpty() || diagnoses.isEmpty() ||
                    Toast.makeText(activity, "All fields are required", Toast.LENGTH_SHORT).show();
                    /*if (vitals.isEmpty()) {
                        etVitals.setError("Vitals can't be empty");
                    }
                    if (diagnoses.isEmpty()) {
                        etDiagnoses.setError("Diagnoses can't be empty");
                    }*/
                    svSendPres.scrollTo(0, 0);
                }/*else if(signaturePath.isEmpty()){
                    Toast.makeText(activity, "Please add your signature to prescriptions", Toast.LENGTH_SHORT).show();
                }*/else {

                    drugs = drugs.substring(0, (drugs.length()-1));
                    quantity = quantity.substring(0, (quantity.length()-1));
                    directions = directions.substring(0, (directions.length()-2));

                    potency_code = potency_code.substring(0, (potency_code.length()-1));
                    refill = refill.substring(0, (refill.length()-1));

                    start_date = start_date.substring(0, (start_date.length()-1));
                    end_date = end_date.substring(0, (end_date.length()-1));



                    new AlertDialog.Builder(activity).setTitle("Confirm").
                            setMessage("Are you sure? You are going to send prescriptions request to the pharmacy. Patient will recieve prescriptions from the pharmacy.").
                            setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    if (checkInternetConnection.isConnectedToInternet()) {
                                        sendPresscription(prefs.getString("id", ""), DATA.selectedUserCallId, "","", treatments,signaturePath,
                                                DATA.selectedUserAppntID,drugs,quantity,directions,potency_code,refill,start_date,end_date);//vitals, diagnoses,
                                    } else {
                                        Toast.makeText(activity, "Please check internet connection", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    }).show();
                }



            }
        });

        ImageView ic_mike_vitals = (ImageView) prescriptionsDialog.findViewById(R.id.ic_mike_vitals);
        ic_mike_vitals.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                startVoiceRecognitionActivity(etVitals);
            }
        });

        ImageView ic_mike_Diagnosis = (ImageView) prescriptionsDialog.findViewById(R.id.ic_mike_Diagnosis);
        ic_mike_Diagnosis.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                startVoiceRecognitionActivity(etDiagnoses);
            }
        });




        Button btnAddDrugs = (Button) prescriptionsDialog.findViewById(R.id.btnAddDrugs);
        btnAddDrugs.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                initDrugsDialog();
            }
        });

		/*if (checkInternetConnection.isConnectedToInternet()) {
			getDosageForms();
		} else {
			Toast.makeText(activity, "No internet connection", 0).show();
		}*/
    }

    public static String prescription_id = "";
    public void sendPresscription(String doctor_id,String patient_id,String vitals,String diagnosis,String treatment,
                                  String signFilePath,String live_checkup_id, String drugs, String quantity, String directions,
                                  String potency_code,String refill,String start_date,String end_date) {
        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity,client);
        client.setTimeout(50000);
        RequestParams params = new RequestParams();
        params.put("doctor_id", doctor_id);
        params.put("patient_id", patient_id);
        params.put("vitals", vitals);
        params.put("diagnosis", diagnosis);
        params.put("treatment", treatment);
        params.put("send_email", "1");

        //params.put("live_checkup_id", live_checkup_id);   removed by Maaz
        params.put("drugs", drugs);
        params.put("quantity", quantity);
        params.put("directions", directions);
        params.put("potency_code", potency_code);
        params.put("refill", refill);

        params.put("start_date", start_date);
        params.put("end_date", end_date);

        /*try {
            params.put("signature", new File(signFilePath));
        } catch (FileNotFoundException e1) {
            DATA.print("-- sign file not found !");
            e1.printStackTrace();
        }*/
        //params.put("send_email", "1"); if dr want to email
        params.put("selected_doctor_id",DATA.selectedDrId);
        if(!PrescriptionsPopup.prescription_id.isEmpty()){
            params.put("prescription_id",PrescriptionsPopup.prescription_id);
        }

        DATA.print("--params in API prescription/savePrescriptions : "+params.toString());

        DATA.showLoaderDefault(activity,"");

        client.post(DATA.baseUrl+"prescription/savePrescriptions", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);

                    DATA.print("-- responce in prescription/savePrescriptions: "+content);
                    //responce {"success":1,"message":"Saved"}
                    try {
                        JSONObject obj = new JSONObject(content);
                        String msg = obj.getString("message");
                        if (obj.has("success")) {

                            prescription_id = obj.getString("prescription_id");
                            prescriptionsDialog.dismiss();

                            //showMessageBox(AfterCallDialog.this, "Success", "Prescriptions sent to patient. Remove patient from live care queue?");
                            //initRemoveDialog();

                            AlertDialog.Builder b = new AlertDialog.Builder(activity);
                            b.setMessage("Prescriptions has been successfully sent.").setPositiveButton("OK", null);
                            AlertDialog d = b.create();
                            d.show();
                            d.setOnDismissListener(new DialogInterface.OnDismissListener() {

                                @Override
                                public void onDismiss(DialogInterface arg0) {
                                    // TODO Auto-generated method stub
                                    //startActivity(new Intent(activity,ActivityTelemedicineServices.class));
                                    //((ActivitySoapNotes)activity).appendNewPrescriptionInSOAP();
                                }
                            });
                            try {
                                String soapPrescTxt = "";
                                if(DATA.drugBeans!=null){
                                    for (int i = 0; i < DATA.drugBeans.size(); i++) {
                                        soapPrescTxt = soapPrescTxt+(i+1)+": "+ DATA.drugBeans.get(i).getDrug_name()+"\n"
                                                +"Usage Instruction: "+DATA.drugBeans.get(i).instructions+"\nStatus: Pending\n";
                                    }
                                }
                                if(ActivitySoapNotesNew.etSOAPPrescription != null){
                                    ActivitySoapNotesNew.etSOAPPrescription.setText(soapPrescTxt);
                                }
                                if(ActivitySoapNotesEditNew.etSOAPPrescription != null){
                                    ActivitySoapNotesEditNew.etSOAPPrescription.setText(soapPrescTxt);
                                }
                                if(ActivitySoapNotesEmpty.etSOAPPrescription != null){
                                    ActivitySoapNotesEmpty.etSOAPPrescription.setText(soapPrescTxt);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        } else {

                            //showMessageBoxError(AfterCallDialog.this, "Error", msg);
                            customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: prescription/savePrescriptions, http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    DATA.print("-- onfail prescription/savePrescriptions: "+content);
                    new GloabalMethods(activity).checkLogin(content, statusCode);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });
    }


    Dialog signatureDialog;
    SignaturePad mSignaturePad;
    Button mClearButton;
    Button mSaveButton;
    private String signaturePath = "";

    public void initSignatureDialog() {
        signatureDialog = new Dialog(activity);
        signatureDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        signatureDialog.setContentView(R.layout.dialog_signature);

        mSignaturePad = (SignaturePad) signatureDialog.findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                Toast.makeText(activity, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });

        mClearButton = (Button) signatureDialog.findViewById(R.id.clear_button);
        mSaveButton = (Button) signatureDialog.findViewById(R.id.save_button);

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                String signPath = addSignatureToGallery(signatureBitmap);
                if(signPath != null) {
                    Toast.makeText(activity, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                    signaturePath = signPath;
                    signatureDialog.dismiss();
                    //ivSignature.setScaleType(ScaleType.CENTER_CROP);
                    ivSignature.setImageBitmap(BitmapFactory.decodeFile(signPath));
                } else {
                    Toast.makeText(activity, "Unable to store the signature", Toast.LENGTH_SHORT).show();
                }
            }
        });
        signatureDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        signatureDialog.show();
    }//end init

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }
    public String addSignatureToGallery(Bitmap signature) {
        //boolean result = false;
        try {
            File photo = new File(getAlbumStorageDir("Online Care Dr"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(photo);
            mediaScanIntent.setData(contentUri);
            activity.sendBroadcast(mediaScanIntent);
            //result = true;
            return photo.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
