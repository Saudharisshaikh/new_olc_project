package com.app.mhcsn_cp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.mhcsn_cp.adapters.VVReportImagesAdapter;
import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.util.ActionEditText;
import com.app.mhcsn_cp.util.ActionSheetPopup;
import com.app.mhcsn_cp.util.CheckInternetConnection;
import com.app.mhcsn_cp.util.CustomToast;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.DatePickerFragment;
import com.app.mhcsn_cp.util.ExpandableHeightGridView;
import com.app.mhcsn_cp.util.GloabalMethods;
import com.app.mhcsn_cp.util.HideShowKeypad;
import com.app.mhcsn_cp.util.OpenActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

import static com.app.mhcsn_cp.ActivitySoapNotesNew.isDMEFormDone;

public class ActivityOTPT_Notes extends AppCompatActivity {

    AppCompatActivity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;

    ActionEditText etOTDate,etOTTimeIn,etOTTimeout,etOTBP,etOTHR,etOTRespirations,etOTO2Saturations,etOTBloodSugar,
            etOTSubjective,etOTADL,etOTIADL,etOTMobility,etOTDMENeeds,etOTPlan,etOTTemperature,etOTGenericAssessment;
    ActionEditText [] fields;
    LinearLayout otptOptionsBox;

    Button btnSubmit,btnDMEReferral;
    public static Button btnSelectImages;
    ExpandableHeightGridView gvReportImages;


    @Override
    protected void onResume() {
        if(isDMEFormDone){
            isDMEFormDone = false;
            btnDMEReferral.setCompoundDrawablesWithIntrinsicBounds( null, null, getResources().getDrawable(R.drawable.ic_check_white_24dp), null);
            btnDMEReferral.setPadding(0,0,10,0);
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpt_notes);

        activity = ActivityOTPT_Notes.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        openActivity = new OpenActivity(activity);

        etOTDate = (ActionEditText) findViewById(R.id.etOTDate);
        etOTTimeIn = (ActionEditText) findViewById(R.id.etOTTimeIn);
        etOTTimeout = (ActionEditText) findViewById(R.id.etOTTimeout);
        etOTBP = (ActionEditText) findViewById(R.id.etOTBP);
        etOTHR = (ActionEditText) findViewById(R.id.etOTHR);
        etOTRespirations = (ActionEditText) findViewById(R.id.etOTRespirations);
        etOTO2Saturations = (ActionEditText) findViewById(R.id.etOTO2Saturations);
        etOTBloodSugar = (ActionEditText) findViewById(R.id.etOTBloodSugar);
        etOTSubjective = (ActionEditText) findViewById(R.id.etOTSubjective);
        etOTADL = (ActionEditText) findViewById(R.id.etOTADL);
        etOTIADL = (ActionEditText) findViewById(R.id.etOTIADL);
        etOTMobility = (ActionEditText) findViewById(R.id.etOTMobility);
        etOTDMENeeds = (ActionEditText) findViewById(R.id.etOTDMENeeds);
        etOTPlan = (ActionEditText) findViewById(R.id.etOTPlan);
        etOTTemperature = (ActionEditText) findViewById(R.id.etOTTemperature);
        etOTGenericAssessment = (ActionEditText) findViewById(R.id.etOTGenericAssessment);

        otptOptionsBox = (LinearLayout) findViewById(R.id.otptOptionsBox);

        if(prefs.getString("doctor_category","").equalsIgnoreCase("ot") || prefs.getString("doctor_category","").equalsIgnoreCase("pt")){
            otptOptionsBox.setVisibility(View.VISIBLE);
            fields = new ActionEditText[]{etOTDate, etOTTimeIn, etOTTimeout, etOTBP, etOTHR, etOTRespirations, etOTO2Saturations,etOTBloodSugar,
                    etOTSubjective, etOTADL, etOTIADL, etOTMobility, etOTDMENeeds, etOTPlan,etOTTemperature,etOTGenericAssessment};
        }else{
            otptOptionsBox.setVisibility(View.GONE);
            fields = new ActionEditText[]{etOTDate, etOTTimeIn, etOTTimeout, etOTBP, etOTHR, etOTRespirations, etOTO2Saturations,etOTBloodSugar};
        }

        etOTDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
        etOTTimeIn.setText(new SimpleDateFormat("hh:mm a").format(new Date()));
        //etOTTimeout.setText(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
        etOTDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(etOTDate);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        etOTTimeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DATA.setTimeByTimePicker(activity,etOTTimeIn);
            }
        });
        etOTTimeout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DATA.setTimeByTimePicker(activity,etOTTimeout);
            }
        });



        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnDMEReferral = (Button) findViewById(R.id.btnDMEReferral);
        btnSelectImages = (Button) findViewById(R.id.btnSelectImages);
        gvReportImages = (ExpandableHeightGridView) findViewById(R.id.gvReportImages);

        btnDMEReferral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ActivityDmeRefferal.class,false);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitNotes();
            }
        });
        btnSelectImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActionSheetPopup(activity).showActionSheet();
            }
        });
        vvReportImagesAdapter = new VVReportImagesAdapter(activity,new ArrayList<Image>());
        gvReportImages.setAdapter(vvReportImagesAdapter);
        gvReportImages.setExpanded(true);
        gvReportImages.setPadding(5,5,5,5);
    }


    ArrayList<Image> images;
    VVReportImagesAdapter vvReportImagesAdapter;
    final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            if(images == null){
                images = new ArrayList<>();
            }
            ArrayList<Image> imagesFromGal = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            images.addAll(imagesFromGal);

            vvReportImagesAdapter = new VVReportImagesAdapter(activity,images);
            gvReportImages.setAdapter(vvReportImagesAdapter);
            gvReportImages.setExpanded(true);
            gvReportImages.setPadding(5,5,5,5);
            /*if(vvReportImagesAdapter != null){
                vvReportImagesAdapter.notifyDataSetChanged();
            }else {
                vvReportImagesAdapter = new VVReportImagesAdapter(activity,images);
                gvReportImages.setAdapter(vvReportImagesAdapter);
                gvReportImages.setExpanded(true);
            }*/
        }else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            //String imgPath = ImageFilePath.getPath(activity, ActionSheetPopup.outputFileUriVV);//Android O issue, file provider,  GM
            String imgPath = ActionSheetPopup.capturedImagePath;
            if(images == null){
                images = new ArrayList<>();
            }
            images.add(new Image(0,"",imgPath,true));

            vvReportImagesAdapter = new VVReportImagesAdapter(activity,images);
            gvReportImages.setAdapter(vvReportImagesAdapter);
            gvReportImages.setExpanded(true);
            gvReportImages.setPadding(5,5,5,5);

            /*if(vvReportImagesAdapter != null){
                vvReportImagesAdapter.notifyDataSetChanged();
            }else {
                vvReportImagesAdapter = new VVReportImagesAdapter(activity,images);
                gvReportImages.setAdapter(vvReportImagesAdapter);
                gvReportImages.setExpanded(true);
            }*/

        }
    }


    /*https://www.onlinecare.com/dev/index.php/app/doctor/addotnotes/
May 11 18:26:10 iPhone Care Provider[376] <Warning>: {
     "doctor_id" = 240;
     "ot_adl" = Adl;
     "ot_bp" = bp;
     "ot_date" = "05/11/2017";
     "ot_dmeneed" = "DME needs";
     "ot_hr" = hy;
     "ot_iadl" = Ladl;
     "ot_mobility" = "Mobility ";
     "ot_plan" = Plan;
     "ot_respirations" = res;
     "ot_saturation" = sat;
     "ot_subjective" = Subjective;
     "ot_timein" = "06:23 PM";
     "ot_timeout" = "06:23 PM";
     "patient_id" = 36;
 }
May 11 18:26:11 iPhone Care Provider[376] <Warning>: Success
May 11 18:26:11 iPhone Care Provider[376] <Warning>: Success -- sharedManager: {
     message = Saved;
     status = success;
 }*/
    public void addotnotes(String ot_adl,String ot_bp,String ot_date,String ot_dmeneed,String ot_hr,String ot_iadl,
                          String ot_mobility, String ot_plan,String ot_respirations,String ot_saturation,String ot_subjective,
                           String ot_timein,String ot_timeout,String ot_blood_sugar,String ot_temperature,String ot_generic_assessment) {

        DATA.showLoaderDefault(activity, "");
        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity,client);
        RequestParams params = new RequestParams();

        params.put("doctor_id", prefs.getString("id", "0"));
        params.put("ot_adl", ot_adl);
        params.put("ot_bp", ot_bp);
        params.put("ot_date", ot_date);
        params.put("ot_dmeneed", ot_dmeneed);
        params.put("ot_hr", ot_hr);
        params.put("ot_iadl", ot_iadl);
        params.put("ot_mobility", ot_mobility);
        params.put("ot_plan", ot_plan);
        params.put("ot_respirations", ot_respirations);
        params.put("ot_saturation", ot_saturation);
        params.put("ot_subjective", ot_subjective);
        params.put("ot_timein", ot_timein);
        params.put("ot_timeout", ot_timeout);
        params.put("ot_blood_sugar",ot_blood_sugar);
        params.put("ot_temperature",ot_temperature);
        params.put("ot_generic_assessment",ot_generic_assessment);
        params.put("patient_id", DATA.selectedUserCallId);
        params.put("selected_doctor_id", DATA.selectedDrId);

        if(images!=null){
            for (int i = 0; i < images.size(); i++) {
                try {
                    params.put("v_image_"+(i+1),new File(images.get(i).path));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            params.put("num_images",images.size()+"");
        }


        Iterator it1 = ActivityDmeRefferal.paramsMap.entrySet().iterator();
        while (it1.hasNext()) {
            Map.Entry pair = (Map.Entry)it1.next();
            //DATA.print(pair.getKey() + " = " + pair.getValue());
            params.put(pair.getKey()+"",pair.getValue()+"");
            it1.remove(); // avoids a ConcurrentModificationException
        }

        DATA.print("-- params in addotnotes: "+params.toString());

        client.post(DATA.baseUrl+"doctor/addotnotes", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);

                    DATA.print("--reaponce in addotnotes"+content);

                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                            customToast.showToast("Your OT/PT notes have been saved",0,0);
                            finish();
                        }else{
                            customToast.showToast(jsonObject.getString("message"),0,0);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: addotnotes, http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    DATA.print("--onfail addotnotes" +content);
                    new GloabalMethods(activity).checkLogin(content);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }//addotnotes


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_soap_notes, menu);

        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save_schedule) {
            submitNotes();
        }else if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;//return true back activity state maintains if false back activity oncreate called
        }
        return super.onOptionsItemSelected(item);
    }


    boolean validate(){
        boolean validate = true;
        if(fields != null){
            for (ActionEditText editText : fields) {
                if(editText.getText().toString().isEmpty()){
                    editText.setBackgroundResource(R.drawable.cust_border_white_outline_red);
                    validate = false;
                }else {
                    editText.setBackgroundResource(R.drawable.cust_border_white_outline);
                }
            }
        }
        return validate;
    }


    public void submitNotes(){

        // submitSOAP_Notes();
        String ot_adl = etOTADL.getText().toString();
        String ot_bp = etOTBP.getText().toString();
        String ot_date = etOTDate.getText().toString();
        String ot_dmeneed = etOTDMENeeds.getText().toString();
        String ot_hr = etOTHR.getText().toString();
        String ot_iadl = etOTIADL.getText().toString();
        String ot_mobility = etOTMobility.getText().toString();
        String ot_plan = etOTPlan.getText().toString();
        String ot_respirations = etOTRespirations.getText().toString();
        String ot_saturation = etOTO2Saturations.getText().toString();
        String ot_blood_sugar = etOTBloodSugar.getText().toString();//-----
        String ot_subjective = etOTSubjective.getText().toString();
        String ot_timein = etOTTimeIn.getText().toString();
        String ot_timeout = etOTTimeout.getText().toString();
        String ot_temperature = etOTTemperature.getText().toString();
        String ot_generic_assessment = etOTGenericAssessment.getText().toString();


        if(checkInternetConnection.isConnectedToInternet()){
            if(! validate()){
                customToast.showToast("All fields are required",0,0);
                return;//false
            }
            addotnotes(ot_adl,ot_bp,ot_date,ot_dmeneed,ot_hr,ot_iadl,ot_mobility,ot_plan,ot_respirations,
                    ot_saturation,ot_subjective,ot_timein,ot_timeout,ot_blood_sugar, ot_temperature,ot_generic_assessment);
        }else {
            customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
        }
    }
}
