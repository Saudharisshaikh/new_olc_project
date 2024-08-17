package com.app.mhcsn_uc.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import androidx.core.content.FileProvider;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.mhcsn_uc.ActivityInsurance;
import com.app.mhcsn_uc.BookAppointment;
import com.app.mhcsn_uc.R;
import com.app.mhcsn_uc.adapter.ACTV_InsuranceAdapter;
import com.app.mhcsn_uc.adapter.DialogInsuranceAdapter;
import com.app.mhcsn_uc.adapter.InsuranceAdapter;
import com.app.mhcsn_uc.api.ApiCallBack;
import com.app.mhcsn_uc.api.ApiManager;
import com.app.mhcsn_uc.model.MyInsuranceBean;
import com.app.mhcsn_uc.model.PayerBean;
import com.app.mhcsn_uc.paypal.PaymentLiveCare;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;

public class LiveCareInsurance implements ApiCallBack, LiveCareInsuranceInterface {


    Activity activity;
    SharedPreferences prefs;
    CustomToast customToast;
    SharedPrefsHelper sharedPrefsHelper;

    public LiveCareInsurance(Activity activity) {
        this.activity = activity;
        prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        customToast = new CustomToast(activity);
        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        DATA.print("-- LiveCareInsurance constructor calling activity is: "+activity.getClass().getName());
    }


    boolean showLoader;
    public void getMyInsurance(boolean showLoader){
        this.showLoader = showLoader;
        RequestParams params = new RequestParams();
        params.put("patient_id",prefs.getString("id",""));
        ApiManager apiManager = new ApiManager(ApiManager.GET_MY_INSURANCE,"post",params,this, activity);

        ApiManager.shouldShowLoader = showLoader;

        apiManager.loadURL();
    }

    public void showInsurancesList(){
        myInsuranceBeen = sharedPrefsHelper.getPatientInrances();

        if(myInsuranceBeen.isEmpty()){
            getMyInsurance(true);
        }else {
            showInsuranceListDialog();
        }
    }

    Dialog dialogInsList;
    ListView lvMyInsurance;
    LinearLayout layNoInsurance;
    TextView tvNoInsurance;
    public void showInsuranceListDialog(){
        dialogInsList = new Dialog(activity,R.style.TransparentThemeH4B);
        dialogInsList.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogInsList.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogInsList.setContentView(R.layout.dialog_ins);

        ImageView ivCancel = dialogInsList.findViewById(R.id.ivCancel);

        lvMyInsurance  = dialogInsList.findViewById(R.id.lvMyInsurance);

        InsuranceAdapter insuranceAdapter = new InsuranceAdapter(activity,myInsuranceBeen);
        lvMyInsurance.setAdapter(insuranceAdapter);

        lvMyInsurance.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                selected_insurance = myInsuranceBeen.get(i).id;

                varifyInsurance(myInsuranceBeen.get(i).id);

                /*selected_insurance = myInsuranceBeen.get(i).id;
                coPayAmount = myInsuranceBeen.get(i).copay_uc;
                dialogInsList.dismiss();

                double coPayCheck = 0;
                try {
                    coPayCheck = Double.parseDouble(coPayAmount);
                }catch (Exception e){
                    e.printStackTrace();
                }

                if(coPayCheck > 0){
                    showCreditCardDialog();
                }else {
                    if(PaymentLiveCare.btnPay != null){
                        PaymentLiveCare.btnPay.setEnabled(true);
                    }
                }*/
            }
        });

        layNoInsurance = dialogInsList.findViewById(R.id.layNoInsurance);
        tvNoInsurance = dialogInsList.findViewById(R.id.tvNoInsurance);
        Button btnAddIns = dialogInsList.findViewById(R.id.btnAddIns);

        btnAddIns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getPayers();
                getSimpleState();
            }
        });

        Button btnSkipIns = dialogInsList.findViewById(R.id.btnSkipIns);
        btnSkipIns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInsList.dismiss();
                if(activity instanceof ActivityInsurance){
                    ((ActivityInsurance)activity).finishAndContinue();
                }
            }
        });

        /*if(myInsuranceBeen.isEmpty()){
            layNoInsurance.setVisibility(View.VISIBLE);
        }else {
            layNoInsurance.setVisibility(View.GONE);
        }*/

        //int vis = (myInsuranceBeen.isEmpty()) ? View.VISIBLE : View.GONE;
        //layNoInsurance.setVisibility(vis);

        layNoInsurance.setVisibility( (myInsuranceBeen.isEmpty()) ? View.VISIBLE : View.GONE);




        /*Spinner spMemberType = dialogInsList.findViewById(R.id.spMemberType);
        spSelMember = dialogInsList.findViewById(R.id.spSelMember);
        Button btnAdMember = dialogInsList.findViewById(R.id.btnAdMember);

        LinearLayout spMemberCont = dialogInsList.findViewById(R.id.spMemberCont);
        LinearLayout otherCTMCont = dialogInsList.findViewById(R.id.otherCTMCont);
        EditText etCTM_Name = dialogInsList.findViewById(R.id.etCTM_Name);
        EditText etCTM_Phone = dialogInsList.findViewById(R.id.etCTM_Phone);
        ivCTM_Img = dialogInsList.findViewById(R.id.ivCTM_Img);
        Button btnCTM_Img = dialogInsList.findViewById(R.id.btnCTM_Img);

        btnCTM_Img.setOnClickListener(v -> (
                (ActivityCarePlanDetail)activity).openActivity.open(ChoosePictureDialog.class, false)
        );

        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getId()){
                    case R.id.spMemberType:
                        if(ct_typeBeans.get(position).kay.equalsIgnoreCase(CTM_TYPE_OTHER)){
                            otherCTMCont.setVisibility(View.VISIBLE);
                            spMemberCont.setVisibility(View.GONE);
                        }else {
                            otherCTMCont.setVisibility(View.GONE);
                            spMemberCont.setVisibility(View.VISIBLE);

                            ApiManager.shouldShowPD = false;
                            RequestParams params = new RequestParams();
                            ApiManager apiManager = new ApiManager(ApiManager.CARE_TEAM_BY_TYPE+"/"+ct_typeBeans.get(position).kay,"get",params,FragmentCareTeam.this::fetchDataCallback, activity);
                            apiManager.loadURL();
                        }
                        break;
                    case R.id.spSelMember:
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        spMemberType.setOnItemSelectedListener(onItemSelectedListener);
        getCT_Types();
        ArrayAdapter<CT_TypeBean> ct_typeBeanAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, ct_typeBeans);
        spMemberType.setAdapter(ct_typeBeanAdapter);

        btnAdMember.setOnClickListener(v -> {
            String ctmType = ct_typeBeans.get(spMemberType.getSelectedItemPosition()).kay;

            if(ctmType.equalsIgnoreCase(CTM_TYPE_OTHER)){
                String ctmName = etCTM_Name.getText().toString();
                String ctmPhone = etCTM_Phone.getText().toString();
                if(ctmName.isEmpty()){
                    etCTM_Name.setError("Please enter name");
                    ((ActivityCarePlanDetail)activity).customToast.showToast("Please enter name",0,0);
                    return;
                }
                if(ctmPhone.isEmpty()){
                    etCTM_Phone.setError("Please enter phone no");
                    ((ActivityCarePlanDetail)activity).customToast.showToast("Please enter phone no",0,0);
                    return;
                }
                if(DATA.imagePath.isEmpty()){
                    ((ActivityCarePlanDetail)activity).customToast.showToast("Please choose image",0,0);
                    return;
                }

                addTeamMember(ctmType, "", ctmName,ctmPhone, DATA.imagePath);
            }else {
                String ctmMembers = ct_selMemberBeans.get(spSelMember.getSelectedItemPosition()).id;
                addTeamMember(ctmType,ctmMembers,"","","");
            }
        });*/

        ivCancel.setOnClickListener(v -> dialogInsList.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogInsList.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogInsList.setCanceledOnTouchOutside(false);
        dialogInsList.show();
        dialogInsList.getWindow().setAttributes(lp);

//        WindowManager.LayoutParams a = dialogInsList.getWindow().getAttributes();//this code is to remove bacgrond dimness if a.dimAmount = 0;
//        a.dimAmount = 80;
//        dialogInsList.getWindow().setAttributes(a);
    }


    //Dialog dialogCrediCard;
    public void showCreditCardDialog(){
        Dialog dialogCrediCard = new Dialog(activity,R.style.TransparentThemeH4B);
        dialogCrediCard.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogCrediCard.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogCrediCard.setContentView(R.layout.dialog_creditcard);

        ImageView ivCancel = dialogCrediCard.findViewById(R.id.ivCancel);
        ivCancel.setOnClickListener(v -> dialogCrediCard.dismiss());


        TextView tvCrediCardAmount = dialogCrediCard.findViewById(R.id.tvCrediCardAmount);
        EditText etCardholder = dialogCrediCard.findViewById(R.id.etCardholder);
        EditText etCardNo = dialogCrediCard.findViewById(R.id.etCardNo);
        EditText etCardCVV = dialogCrediCard.findViewById(R.id.etCardCVV);
        Spinner spCardType = dialogCrediCard.findViewById(R.id.spCardType);
        Spinner spCardExpiryMonth = dialogCrediCard.findViewById(R.id.spCardExpiryMonth);
        Spinner spCardExpiryYear = dialogCrediCard.findViewById(R.id.spCardExpiryYear);


        tvCrediCardAmount.setText(Html.fromHtml("Your <font color='"+DATA.APP_THEME_RED_COLOR+"'><b>copayment</b></font> amount is : <font color='"+DATA.APP_THEME_RED_COLOR+"'><b>USD "+coPayAmount+"</b></font>"));

        //SpannableStringBuilder str = new SpannableStringBuilder("Your awesome text");
        //str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), INT_START, INT_END, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //tvCrediCardAmount.setText(str);



        final String cardTypeArr[] = {"Visa","MasterCard","Discover","American Express"};
        final String monthsArr[] = {"01","02","03","04","05","06","07","08","09","10","11","12"};
        int year = Calendar.getInstance().get(Calendar.YEAR);
        final String yearsArr[] = {year+"",(year+1)+"",(year+2)+"",(year+3)+"",(year+4)+"",(year+5)+""
                ,(year+6)+"",(year+7)+"",(year+8)+"",(year+9)+"",(year+10)+""};
        spCardType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PaymentLiveCare.selectedCardType = cardTypeArr[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spCardExpiryMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PaymentLiveCare.selectedExpMonth = monthsArr[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spCardExpiryYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PaymentLiveCare.selectedExpYear = yearsArr[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> spCardTypeAdapter = new ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,cardTypeArr);
        spCardTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCardType.setAdapter(spCardTypeAdapter);


        ArrayAdapter<String> spMonthAdapter = new ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,monthsArr);
        spMonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCardExpiryMonth.setAdapter(spMonthAdapter);


        ArrayAdapter<String> spYearAdapter = new ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,yearsArr);
        spYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCardExpiryYear.setAdapter(spYearAdapter);


        Button btnSubmitCard = dialogCrediCard.findViewById(R.id.btnSubmitCard);
        btnSubmitCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PaymentLiveCare.cardHolderName = etCardholder.getText().toString();
                PaymentLiveCare.cardNo = etCardNo.getText().toString();
                PaymentLiveCare.cvvCode = etCardCVV.getText().toString();
                if (PaymentLiveCare.cardHolderName.isEmpty()){
                    etCardholder.setError("Field should not be empty.");
                    return;
                }else if (PaymentLiveCare.cardNo.isEmpty()){
                    etCardNo.setError("Field should not be empty.");
                    return;
                }else if (PaymentLiveCare.cvvCode.isEmpty()){
                    etCardCVV.setError("Field should not be empty.");
                    return;
                }

                dialogCrediCard.dismiss();

                if(activity instanceof PaymentLiveCare){
                    if(PaymentLiveCare.btnPay != null){
                        PaymentLiveCare.btnPay.setEnabled(true);//perform click
                        PaymentLiveCare.btnPay.performClick();
                    }
                }else if(activity instanceof BookAppointment){
                    if(BookAppointment.btnBKAccept != null){
                        BookAppointment.btnBKAccept.setVisibility(View.VISIBLE);//perform click
                        BookAppointment.btnBKAccept.performClick();
                    }
                }
            }
        });




        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogCrediCard.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogCrediCard.setCanceledOnTouchOutside(false);
        dialogCrediCard.show();
        dialogCrediCard.getWindow().setAttributes(lp);

//        WindowManager.LayoutParams a = dialogCrediCard.getWindow().getAttributes();//this code is to remove bacgrond dimness if a.dimAmount = 0;
//        a.dimAmount = 80;
//        dialogCrediCard.getWindow().setAttributes(a);
    }


    //========================Add insurance==============================================================
    ArrayList<String> allStates;
    ArrayList<PayerBean> payerBeens;

    public void getSimpleState() {

        ApiManager apiManager = new ApiManager(ApiManager.SIMPLE_STATES,"get",null,this, activity);
        apiManager.loadURL();

    }
    public void getPayers(String state) {

        String reqURL = ApiManager.GET_PAYERS;
        if(!state.isEmpty()){
            reqURL = ApiManager.GET_PAYERS+"?state="+state;
        }
        ApiManager apiManager = new ApiManager(reqURL,"get",null,this, activity);
        apiManager.loadURL();

    }

    private Dialog dialog;
    private LinearLayout insuranceCont;
    //Spinner spInsurance;
    private SpinnerCustom spInsuranceState;
    //private AutoCompleteTextView actvInsurance;
    private EditText etPolicynumber,etGroup,etCode;
    private Button btnVarifyInsurance;
    private String insurance = "";

    private ImageView ivICfrontImg,ivDeleteICfrontImg,ivICbackImg,ivDeleteICbackImg;

    private String iCardFrontImgPath = "", iCardBackImgPath = "";

    private void showAddInsuranceDialog(){
        dialog = new Dialog(activity, R.style.TransparentThemeH4B);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setContentView(R.layout.dialog_add_insurance);

        insuranceCont= dialog.findViewById(R.id.insuranceCont);
        //spInsurance = (Spinner) dialog.findViewById(spInsurance);
        spInsuranceState = dialog.findViewById(R.id.spInsuranceState);
        //actvInsurance = dialog.findViewById(R.id.actvInsurance);
        EditText etInsuranceSelect = dialog.findViewById(R.id.etInsuranceSelect);
        etPolicynumber = dialog.findViewById(R.id.etPolicynumber);
        etGroup = dialog.findViewById(R.id.etGroup);
        etCode = dialog.findViewById(R.id.etCode);
        btnVarifyInsurance = dialog.findViewById(R.id.btnVarifyInsurance);


        EditText etFname = dialog.findViewById(R.id.etFname);
        EditText etLname = dialog.findViewById(R.id.etLname);
        EditText etDOB = dialog.findViewById(R.id.etDOB);

        etFname.setText(prefs.getString("first_name", ""));
        etLname.setText(prefs.getString("last_name", ""));
        etDOB.setText(prefs.getString("birthdate", ""));

        etInsuranceSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInsuranceSelectionDialog(etInsuranceSelect);
            }
        });
        /*actvInsurance.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PayerBean selectedPayerBean = (PayerBean) parent.getItemAtPosition(position);
                //PayerBean selectedPayerBean = spInsuranceAdapter.getObject(position);
                insurance = selectedPayerBean.payer_id;
                DATA.print("-- selected payer name: "+selectedPayerBean.payer_name+" , ID : "+selectedPayerBean.payer_id);
            }
        });
        actvInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actvInsurance.getText().toString().equalsIgnoreCase(" ")){
                    actvInsurance.setText("");
                }
            }
        });*/


        dialog.findViewById(R.id.ivCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Button btnSkipInsurance = dialog.findViewById(R.id.btnSkipInsurance);
        btnSkipInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(activity instanceof ActivityInsurance){
                    ((ActivityInsurance)activity).finishAndContinue();
                }
            }
        });


        ivICfrontImg = dialog.findViewById(R.id.ivICfrontImg);
        ivDeleteICfrontImg = dialog.findViewById(R.id.ivDeleteICfrontImg);
        ivICbackImg = dialog.findViewById(R.id.ivICbackImg);
        ivDeleteICbackImg = dialog.findViewById(R.id.ivDeleteICbackImg);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.ivICfrontImg:
                        if(activity instanceof ActivityInsurance){
                            ((ActivityInsurance) activity).callPicCardImgMethod(1, LiveCareInsurance.this);
                        }else if(activity instanceof PaymentLiveCare){
                            ((PaymentLiveCare) activity).callPicCardImgMethod(1, LiveCareInsurance.this);
                        }else if(activity instanceof BookAppointment){
                            ((BookAppointment) activity).callPicCardImgMethod(1, LiveCareInsurance.this);
                        }
                        break;
                    case R.id.ivDeleteICfrontImg:
                        new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                                .setTitle("Confirm")
                                .setMessage("Are you sure? Do you want to delete this file")
                                .setPositiveButton("Yes Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        iCardFrontImgPath = "";
                                        ivDeleteICfrontImg.setVisibility(View.GONE);
                                        ivICfrontImg.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
                                        ivICfrontImg.setScaleType(ImageView.ScaleType.CENTER);
                                    }
                                })
                                .setNegativeButton("Not Now",null)
                                .create().show();
                        break;
                    case R.id.ivICbackImg:
                        if(activity instanceof ActivityInsurance){
                            ((ActivityInsurance) activity).callPicCardImgMethod(2, LiveCareInsurance.this);
                        }else if(activity instanceof PaymentLiveCare){
                            ((PaymentLiveCare) activity).callPicCardImgMethod(2, LiveCareInsurance.this);
                        }else if(activity instanceof BookAppointment){
                            ((BookAppointment) activity).callPicCardImgMethod(2, LiveCareInsurance.this);
                        }
                        break;
                    case R.id.ivDeleteICbackImg:
                        new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                                .setTitle("Confirm")
                                .setMessage("Are you sure? Do you want to delete this file")
                                .setPositiveButton("Yes Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        iCardBackImgPath = "";
                                        ivDeleteICbackImg.setVisibility(View.GONE);
                                        ivICbackImg.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
                                        ivICbackImg.setScaleType(ImageView.ScaleType.CENTER);
                                    }
                                })
                                .setNegativeButton("Not Now",null)
                                .create().show();
                        break;
                    default:
                        break;
                }
            }
        };
        ivICfrontImg.setOnClickListener(onClickListener);
        ivDeleteICfrontImg.setOnClickListener(onClickListener);
        ivICbackImg.setOnClickListener(onClickListener);
        ivDeleteICbackImg.setOnClickListener(onClickListener);


		/*actvInsurance.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				insurance = "";
				actvInsurance.setText("");
			}
		});*/
		/*spInsurance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				insurance = payerBeens.get(position).payer_id;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});*/

        btnVarifyInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new HideShowKeypad(activity).hidekeyboardOnDialog();

                String policyNo = etPolicynumber.getText().toString();
                String group = etGroup.getText().toString();
                String code = "-";//etCode.getText().toString();

                if(TextUtils.isEmpty(insurance)){
                    new CustomAnimations().shakeAnimate(etInsuranceSelect, 1000, etInsuranceSelect);
                    customToast.showToast("Please select insurance",0,0);
                }else if (policyNo.isEmpty()){
                    etPolicynumber.setError("Policy no should not be empty");
                }
                //else if (group.isEmpty()){
                //    etGroup.setError("Group should not be empty");
                //}
                else if(code.isEmpty()){
                    etCode.setError("Code should not be empty");
                }else {

                    saveInsuranceInfo(insurance,policyNo,group,code);
                }
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        lp.gravity = Gravity.BOTTOM;

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        //note : bellow line causes issue. Form not scroll to botton when keyboard open
        //dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }


    ListView lvInsurance;
    DialogInsuranceAdapter dialogInsuranceAdapter;
    private void showInsuranceSelectionDialog(final EditText etInsuranceInput){
        final Dialog dialogInsuranceSelection = new Dialog(activity);
        dialogInsuranceSelection.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogInsuranceSelection.setContentView(R.layout.dialog_insurance_selection);
        dialogInsuranceSelection.setCanceledOnTouchOutside(false);

        EditText etSerchInsurance = dialogInsuranceSelection.findViewById(R.id.etSerchInsurance);
        lvInsurance = dialogInsuranceSelection.findViewById(R.id.lvInsurance);
        SpinnerCustom spInsuranceState2 = dialogInsuranceSelection.findViewById(R.id.spInsuranceState2);

        dialogInsuranceSelection.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInsuranceSelection.dismiss();
            }
        });

        if(allStates != null){
            spInsuranceState2.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, allStates));
            spInsuranceState2.setOnItemSelectedListener(new SpinnerCustom.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id, boolean userSelected) {
                    //if(userSelected){}//condition is not required here
                    if(allStates.get(position).equalsIgnoreCase("Other")){
                        getPayers("");
                    }else {
                        getPayers(allStates.get(position));
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });
            if(spInsuranceState != null){
                try {
                    spInsuranceState2.setSelection(spInsuranceState.getSelectedItemPosition());
                }catch (Exception e){e.printStackTrace();}
            }
        }

        dialogInsuranceSelection.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(spInsuranceState != null){
                    try {
                        spInsuranceState.setSelection(spInsuranceState2.getSelectedItemPosition());
                    }catch (Exception e){e.printStackTrace();}
                }
            }
        });


        lvInsurance.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //PayerBean selectedPayerBean = (PayerBean) parent.getItemAtPosition(position);
                //PayerBean selectedPayerBean = spInsuranceAdapter.getObject(position);
                PayerBean selectedPayerBean = payerBeens.get(position);
                insurance = selectedPayerBean.payer_id;
                DATA.print("-- selected payer name: "+selectedPayerBean.payer_name+" , ID : "+selectedPayerBean.payer_id);


                etInsuranceInput.setError(null);
                etInsuranceInput.setText(selectedPayerBean.payer_name);

                new HideShowKeypad(activity).hidekeyboardOnDialog();

                dialogInsuranceSelection.dismiss();
            }
        });

        if(payerBeens != null){
            dialogInsuranceAdapter = new DialogInsuranceAdapter(activity, payerBeens);
            lvInsurance.setAdapter(dialogInsuranceAdapter);
        }

        etSerchInsurance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(dialogInsuranceAdapter != null){
                    dialogInsuranceAdapter.filter(s.toString());
                }
            }
        });

        //dialogInsuranceSelection.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialogInsuranceSelection.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogInsuranceSelection.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);

        dialogInsuranceSelection.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogInsuranceSelection.show();
        dialogInsuranceSelection.getWindow().setAttributes(lp);
    }


    private void saveInsuranceInfo(String insurance, String p_no, String group, String code) {
        RequestParams params = new RequestParams();

        params.put("patient_id", prefs.getString("id", "0"));
        params.put("insurance", insurance);
        params.put("policy_number", p_no);
        params.put("group", group);
        params.put("code", code);

        if(! TextUtils.isEmpty(iCardFrontImgPath)){
            try {params.put("inc_front", new File(iCardFrontImgPath));} catch (FileNotFoundException e) {e.printStackTrace();}
        }
        if(! TextUtils.isEmpty(iCardBackImgPath)){
            try {params.put("inc_back", new File(iCardBackImgPath));} catch (FileNotFoundException e) {e.printStackTrace();}
        }

		/*Editor ed = prefs.edit();
		ed.putString("insurance", insurance);
		ed.putString("policy_number", p_no);
		ed.putString("group", group);
		ed.putString("code", group);
		ed.commit();*/
        ApiManager apiManager = new ApiManager(ApiManager.SAVE_INSURANCE_INFO,"post",params,this, activity);
        apiManager.loadURL();

    }//end saveInsuranceInfo

    //========================Add insurance==============================================================



    //==================Varify insurance=================================================================
    private void varifyInsurance(String insuranceId){

        RequestParams params = new RequestParams();
        params.put("patient_id",prefs.getString("id",""));
        params.put("id",insuranceId);
        ApiManager apiManager = new ApiManager(ApiManager.VARIFY_INSURANCE,"post",params,this, activity);

        //ApiManager.shouldShowLoader = showLoader;

        apiManager.loadURL();
    }

    //==================Varify insurance=================================================================

    private ArrayList<MyInsuranceBean> myInsuranceBeen;
    public static String selected_insurance = "", coPayAmount = "";

    private ACTV_InsuranceAdapter spInsuranceAdapter;
    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if(apiName.equals(ApiManager.GET_MY_INSURANCE)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                /*if(data.length() == 0){
                    findViewById(R.id.tvNoInsurance).setVisibility(View.VISIBLE);
                }else {
                    findViewById(R.id.tvNoInsurance).setVisibility(View.GONE);
                }*/

                myInsuranceBeen = new ArrayList<>();
                MyInsuranceBean myInsuranceBean;
                for (int i = 0; i < data.length(); i++) {
                    String id = data.getJSONObject(i).getString("id");
                    String patient_id = data.getJSONObject(i).getString("patient_id");
                    String insurance = data.getJSONObject(i).getString("insurance");
                    String policy_number = data.getJSONObject(i).getString("policy_number");
                    String insurance_group = data.getJSONObject(i).getString("insurance_group");
                    String insurance_code = data.getJSONObject(i).getString("insurance_code");
                    String payer_name = data.getJSONObject(i).getString("payer_name");
                    String copay_uc = data.getJSONObject(i).getString("copay_uc");

                    /*if(i == 0){//just for testing
                        copay_uc = "6";
                    }*/

                    myInsuranceBean = new MyInsuranceBean(id,patient_id,insurance,policy_number,insurance_group,insurance_code,payer_name,copay_uc);
                    myInsuranceBeen.add(myInsuranceBean);
                    myInsuranceBean = null;
                }

                sharedPrefsHelper.savePatientInrances(myInsuranceBeen);

                if(lvMyInsurance != null){
                    lvMyInsurance.setAdapter(new InsuranceAdapter(activity,myInsuranceBeen));
                }

                if(layNoInsurance != null){
                    layNoInsurance.setVisibility( (myInsuranceBeen.isEmpty()) ? View.VISIBLE : View.GONE);
                }

                if(tvNoInsurance != null){
                    tvNoInsurance.setText(jsonObject.optString("message"));
                }

                if(showLoader){
                    showInsuranceListDialog();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }else if(apiName.contains(ApiManager.GET_PAYERS)){
            try {
                payerBeens = new ArrayList<PayerBean>();
                PayerBean payerBean;
                JSONArray jsonArray = new JSONArray(content);
                for (int i = 0; i<jsonArray.length(); i++){
                    String id = jsonArray.getJSONObject(i).getString("id");
                    String payer_id = jsonArray.getJSONObject(i).getString("payer_id");
                    String payer_name = jsonArray.getJSONObject(i).getString("payer_name");

                    payerBean = new PayerBean(id,payer_id,payer_name);
                    payerBeens.add(payerBean);
                    payerBean = null;
                }


                //showAddInsuranceDialog();

                /*ArrayAdapter<PayerBean> spInsuranceAdapter = new ArrayAdapter<PayerBean>(
                        activity,
                        R.layout.spinner_item_lay,
                        payerBeens);*/

                /*if(actvInsurance != null){
                    spInsuranceAdapter = new ACTV_InsuranceAdapter(activity, R.layout.spinner_item_lay, payerBeens);
                    spInsuranceAdapter.setDropDownViewResource(R.layout.spinner_item_lay);
                    //spInsurance.setAdapter(spInsuranceAdapter);
                    actvInsurance.setAdapter(spInsuranceAdapter);
                    actvInsurance.setThreshold(0);

                    actvInsurance.setText(" ");

                }else {
                    DATA.print("-- actvInsurance null "+actvInsurance);
                }*/

                if(lvInsurance != null){
                    dialogInsuranceAdapter = new DialogInsuranceAdapter(activity, payerBeens);
                    lvInsurance.setAdapter(dialogInsuranceAdapter);
                }else {
                    DATA.print("-- lvInsurance is null "+lvInsurance);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
            }
        }else if(apiName.equals(ApiManager.SAVE_INSURANCE_INFO)){
            try {
                JSONObject jsonObject = new JSONObject(content);

                if (jsonObject.has("success")){
                    //customToast.showToast("Your insurance information varified !", 0, 1);

                    AlertDialog alertDialog =
                            new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Info")
                                    .setMessage(jsonObject.optString("message"))
                                    .setPositiveButton("Done",null).create();
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            if(dialog != null){
                                dialog.dismiss();
                            }
                            if(activity instanceof ActivityInsurance){
                                ((ActivityInsurance) activity).getMyInsurance();
                            }else {
                                getMyInsurance(false);
                            }
                        }
                    });
                    alertDialog.show();


                }else if(jsonObject.has("error")){
                    new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setMessage(jsonObject.getString("message")).setPositiveButton("OK",null).show();
                }else if(jsonObject.optString("status").equalsIgnoreCase("Error")){
                    //{"status":"Error","msg":"insurance parameter is missing OR missing value","message":"Insurance Required"}
                    new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setMessage(jsonObject.getString("message")).setPositiveButton("OK",null).show();
                }else {
                    //customToast.showToast("Opps! Some thing went wrong please try again", 0, 1);
                    customToast.showToast(content,0,1);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                //customToast.showToast(DATA.JSON_ERROR_MSG,0,1);
                customToast.showToast(content,0,1);
                e.printStackTrace();
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.VARIFY_INSURANCE)){
            //{"status":"error","message":"The Insurance input was not recognized Please check the Insurance Information and Re-enterPCP SELECTION NOT REQUIREDAll Other In-Network ProvidersAll Other In-Network ProvidersMed Dent,DED NOT INCL IN OOP,Medical Ancillary,Semi Private Room and Board,Intensive Care Room and Board,Inpatient Facility,Outpatient Facility,Non Emergency use of Emergency Room,Emergency use of Emergency Room,Emergency Room Physician,Outpatient Surgery FacilityMed Dent,DED NOT INCL IN OOP,Medical Ancillary,Semi Private Room and Board,Intensive Care Room and Board,Inpatient Facility,Outpatient Facility,Non Emergency use of Emergency Room,Emergency use of Emergency Room,Emergency Room Physician,Outpatient Surgery FacilityOur records indicate the provider ID you entered includes both in-network and out of network providers.All Other In-Network ProvidersAll Other In-Network ProvidersAll Other In-Network ProvidersAll Other In-Network ProvidersAll Other In-Network ProvidersAll Other In-Network ProvidersSemi Private Room and BoardInpatient FacilityNon Emergency use of Emergency RoomEmergency use of Emergency RoomEmergency Room PhysicianPlan Requires PreCertSelf FundedPlan includes NAP, but program limitations may apply in relation to Third Party Discount Networks. Final determination is made at the time of claim processing."}
            //{"status":"error","message":"The Insurance input was not recognized Please check the Insurance Information and Re-enter"}
            //{"status":"success","data":{"id":"35","patient_id":"615","insurance":"10004","policy_number":"TC001001","insurance_group":"gr97","insurance_code":"lx470","copay_uc":60,"payer_name":"Aetna"}}
            /*apiName: patient/getInsuranceData
            {
                "status": "success",
                    "data": {
                         "id": "55",
                        "patient_id": "426",
                        "insurance": "10004",
                        "policy_number": "TC001001",
                        "insurance_group": "forbiden",
                        "insurance_code": "123",
                        "copay_uc": 0,
                        "payer_name": "Aetna"
            }
            }*/
            try {
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");
                String message = jsonObject.optString("message");

                if(status.equalsIgnoreCase("success")){

                    AlertDialog alertDialog =
                            new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Info")
                                    .setMessage(message)
                                    .setPositiveButton("Continue For Care",null).create();
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            try {
                                int copay_uc = jsonObject.getJSONObject("data").getInt("copay_uc");

                                coPayAmount = copay_uc+"";

                                if(copay_uc > 0){
                                    showCreditCardDialog();
                                }else {

                                    if(activity instanceof PaymentLiveCare){
                                        if(PaymentLiveCare.btnPay != null){
                                            PaymentLiveCare.btnPay.setEnabled(true);//perform click
                                            PaymentLiveCare.btnPay.performClick();
                                        }
                                    }else if(activity instanceof BookAppointment){
                                        if(BookAppointment.btnBKAccept != null){
                                            BookAppointment.btnBKAccept.setVisibility(View.VISIBLE);//perform click
                                            BookAppointment.btnBKAccept.performClick();
                                        }
                                    }
                                }

                                if(dialogInsList != null){
                                    dialogInsList.dismiss();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
                            }
                        }
                    });
                    alertDialog.show();

                }else{
                    AlertDialog alertDialog =
                            new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).
                                    setTitle(activity.getResources().getString(R.string.app_name))
                                    .setMessage(message)
                                    .setPositiveButton("Done",null).create();
                    /*alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }
                    });*/
                    alertDialog.show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                //customToast.showToast(content,0,0);
                AlertDialog alertDialog =
                        new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).
                                setTitle(activity.getResources().getString(R.string.app_name))
                                .setMessage(content)
                                .setPositiveButton("Done",null).create();
                    /*alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }
                    });*/
                alertDialog.show();
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.SIMPLE_STATES)){

            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                allStates = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    allStates.add(data.getString(i));
                }


                showAddInsuranceDialog();

                if(spInsuranceState != null){
                    spInsuranceState.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, allStates));

                    //No need to load data here
                    /*spInsuranceState.setOnItemSelectedListener(new SpinnerCustom.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id, boolean userSelected) {
                            if(userSelected){
                                if(allStates.get(position).equalsIgnoreCase("Other")){
                                    getPayers("");
                                }else {
                                    getPayers(allStates.get(position));
                                }
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) { }
                    });*/
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }

        }
    }




    @Override
    public void displayICfrontImg(String imgPath) {
        try {
            iCardFrontImgPath = imgPath;
            ivDeleteICfrontImg.setVisibility(View.VISIBLE);

            File imageFile = new File(iCardFrontImgPath);
            String uri = FileProvider.getUriForFile(activity.getApplicationContext(), activity.getPackageName()+".fileprovider", imageFile).toString();
            final String decoded = Uri.decode(uri);
            DATA.loadImageFromURL(decoded,R.drawable.ic_placeholder_2, ivICfrontImg);
            ivICfrontImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void displayICbackImg(String imgPath) {
        try {
            iCardBackImgPath = imgPath;
            ivDeleteICbackImg.setVisibility(View.VISIBLE);

            File imageFile = new File(iCardBackImgPath);
            String uri = FileProvider.getUriForFile(activity.getApplicationContext(), activity.getPackageName()+".fileprovider", imageFile).toString();
            final String decoded = Uri.decode(uri);
            DATA.loadImageFromURL(decoded,R.drawable.ic_placeholder_2, ivICbackImg);
            ivICbackImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
