package com.app.msu_cp.reliance.idtnote;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.app.msu_cp.BaseActivity;
import com.app.msu_cp.R;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.DatePickerFragment;
import com.app.msu_cp.util.ExpandableHeightListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActivityIDTnoteList extends BaseActivity {


    ListView lvIDTnoteList;
    TextView tvNoData;

    static String start_timeIDT, end_timeIDT;

    /*static boolean shoulRefresh = false;
    @Override
    protected void onResume() {
        if(shoulRefresh){
            shoulRefresh = false;

            loadListData();
        }
        super.onResume();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idtnote_list);

        lvIDTnoteList = findViewById(R.id.lvIDTnoteList);
        tvNoData = findViewById(R.id.tvNoData);

        lvIDTnoteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //openActivity.open(ActivityBecksDepression.class, false);
                /*selectedDastListBean = dastListBeans.get(position);
                Intent intent = new Intent(activity,ActivityDAST_Form.class);
                intent.putExtra("isEdit",true);
                startActivity(intent);*/

                idTnoteListBeanSelected = idTnoteListBeans.get(position);
                loadIDTnote(idTnoteListBeanSelected.id);
                //showAddIDTnoteDialog(true, idTnoteListBeanSelected.dateof);
            }
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("IDT Meeting Notes");
        Button btnToolbar = findViewById(R.id.btnToolbar);

        btnToolbar.setText("Add New");
        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openActivity.open(ActivityDAST_Form.class, false);
                showAddIDTnoteDialog(false, null);
            }
        });


        loadListData();
    }

    public void loadListData(){
        RequestParams params = new RequestParams("patient_id", DATA.selectedUserCallId);
        ApiManager apiManager = new ApiManager(ApiManager.IDT_NOTE_LIST, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    //note: this method do not in use any more
    public void loadIDTnote(String id){
        RequestParams params = new RequestParams("patient_id",DATA.selectedUserCallId);
        ApiManager apiManager = new ApiManager(ApiManager.VIEW_IDT+id, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    public void loadIDTnoteByDate(String dateof){
        RequestParams params = new RequestParams();
        params.put("patient_id",DATA.selectedUserCallId);
        params.put("dateof", dateof);
        params.put("author_id", prefs.getString("id", ""));

        ApiManager apiManager = new ApiManager(ApiManager.IDT_NOTES_BY_DATE, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }


    public void loadLatestCarePlanGoals(){
        RequestParams params = new RequestParams("patient_id",DATA.selectedUserCallId);
        ApiManager apiManager = new ApiManager(ApiManager.LATEST_CAREPLAN_GOALS, "post", params, apiCallBack, activity);
        ApiManager.shouldShowPD = false;
        apiManager.loadURL();
    }

    ArrayList<IDTnoteListBean> idTnoteListBeans;
    public static IDTnoteListBean idTnoteListBeanSelected;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.equalsIgnoreCase(ApiManager.IDT_NOTE_LIST)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                if(data.length() == 0){
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }

                Type listType = new TypeToken<ArrayList<IDTnoteListBean>>() {}.getType();
                idTnoteListBeans = new Gson().fromJson(data.toString(), listType);

                lvIDTnoteList.setAdapter(new IDTnoteListAdapter(activity, idTnoteListBeans));

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.contains(ApiManager.VIEW_IDT)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                Type listType = new TypeToken<ArrayList<IDTnoteBean>>() {}.getType();
                ArrayList<IDTnoteBean> idTnoteBeans = new Gson().fromJson(data.toString(), listType);

                if(idTnoteBeans != null){
                    showIDTdetailDialog(idTnoteBeans);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.IDT_NOTES_BY_DATE)){

            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                Type listType = new TypeToken<ArrayList<IDTnoteBean>>() {}.getType();
                ArrayList<IDTnoteBean> idTnoteBeans = new Gson().fromJson(data.toString(), listType);

                if(idTnoteBeans != null){
                    /*lvIDTnoteAddDialog = lvIDTnote;
                      layIDTformAddDialog = layIDTform;
                      btnAddIDTsubmitAddDialog = btnAddIDTsubmit;*/

                    if(lvIDTnoteAddDialog != null){
                        lvIDTnoteAddDialog.setAdapter(new LvIDTnoteAdapter2(activity, idTnoteBeans));//LvIDTnoteAdapter    //note: LvIDTnoteAdapter is not used more b/c edit function is in LvIDTnoteAdapter2
                        lvIDTnoteAddDialog.setExpanded(true);
                    }

                    boolean isIdtNoteGivenByMeOnDate = false;

                    for (int i = 0; i < idTnoteBeans.size(); i++) {
                        if(idTnoteBeans.get(i).author_id.equalsIgnoreCase(prefs.getString("id", ""))){
                            isIdtNoteGivenByMeOnDate = true;
                        }
                    }
                    if(isIdtNoteGivenByMeOnDate){//I can add idt note in a date only once
                        if(layIDTformAddDialog != null){
                            layIDTformAddDialog.setVisibility(View.GONE);
                        }
                        if(btnAddIDTsubmitAddDialog != null){
                            btnAddIDTsubmitAddDialog.setText("Done");//Discision making in click action
                        }
                    }else {
                        if(layIDTformAddDialog != null){
                            layIDTformAddDialog.setVisibility(View.VISIBLE);
                        }
                        if(btnAddIDTsubmitAddDialog != null){
                            btnAddIDTsubmitAddDialog.setText("Submit");//Discision making in click action
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }

        }else if(apiName.equalsIgnoreCase(ApiManager.ADD_IDT)){
            //{"status":"success","message":"Saved."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage("Information saved successfully.")
                        .setPositiveButton("Ok, Done",null).create();
                if(jsonObject.getString("status").equalsIgnoreCase("success")){
                    if(dialogForDismiss != null){
                        dialogForDismiss.dismiss();
                    }
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            //loadListData();
                            if(etAddIDTdateAddDialog != null && (!etAddIDTdateAddDialog.getText().toString().trim().isEmpty())){
                                loadIDTnoteByDate(etAddIDTdateAddDialog.getText().toString().trim());
                            }
                        }
                    });
                }else {
                    alertDialog.setMessage(jsonObject.optString("message"));
                }

                alertDialog.show();
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.LATEST_CAREPLAN_GOALS)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                Type listType = new TypeToken<ArrayList<CareGoalBean>>() {}.getType();
                ArrayList<CareGoalBean> careGoalBeans = new Gson().fromJson(data.toString(), listType);

                if(careGoalBeans != null){
                    if(lvCareGoalAddDialog != null){
                        lvCareGoalAddDialog.setAdapter(new CareGoalAdapter(activity, careGoalBeans));
                        lvCareGoalAddDialog.setExpanded(true);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }


    Dialog dialogForDismiss;
    public void showIDTdetailDialog(ArrayList<IDTnoteBean> idTnoteBeans){
        Dialog dialogViewIDT = new Dialog(activity,R.style.TransparentThemeH4B);
        dialogViewIDT.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogViewIDT.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogViewIDT.setContentView(R.layout.dialog_view_idtnote);

        ImageView ivCancel = (ImageView) dialogViewIDT.findViewById(R.id.ivCancel);
        Button btnViewIdtDone = (Button) dialogViewIDT.findViewById(R.id.btnViewIdtDone);

        TextView tvViewIdtPtName = dialogViewIDT.findViewById(R.id.tvViewIdtPtName);
        TextView tvViewIdtDate = dialogViewIDT.findViewById(R.id.tvViewIdtDate);

        tvViewIdtPtName.setText(DATA.selectedUserCallName);
        if(idTnoteListBeanSelected != null){
            tvViewIdtDate.setText(idTnoteListBeanSelected.dateof);
        }

        ExpandableHeightListView lvIDTnote = dialogViewIDT.findViewById(R.id.lvIDTnote);
        lvIDTnote.setAdapter(new LvIDTnoteAdapter2(activity, idTnoteBeans));
        lvIDTnote.setExpanded(true);

        /*etAddERDate.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(etAddERDate);
            newFragment.show(appCompatActivity.getSupportFragmentManager(), "datePicker");
        });*/


        btnViewIdtDone.setOnClickListener(v -> {
            dialogViewIDT.dismiss();
        });

        ivCancel.setOnClickListener(v -> dialogViewIDT.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogViewIDT.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogViewIDT.setCanceledOnTouchOutside(false);
        dialogViewIDT.show();
        dialogViewIDT.getWindow().setAttributes(lp);

        dialogForDismiss = dialogViewIDT;
    }



    ExpandableHeightListView lvIDTnoteAddDialog,lvCareGoalAddDialog;
    LinearLayout layIDTformAddDialog;
    Button btnAddIDTsubmitAddDialog;
    EditText etAddIDTdateAddDialog;
    Dialog dialogAddIdtNoteForDismiss;
    public void showAddIDTnoteDialog(boolean isFromListClick, String dateofIDt){
        Dialog dialogAddIdtNote = new Dialog(activity,R.style.TransparentThemeH4B);
        dialogAddIdtNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogAddIdtNote.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogAddIdtNote.setContentView(R.layout.dialog_add_idtnote);

        ImageView ivCancel = (ImageView) dialogAddIdtNote.findViewById(R.id.ivCancel);
        Button btnAddIDTsubmit = (Button) dialogAddIdtNote.findViewById(R.id.btnAddIDTsubmit);

        EditText etAddIDTPtName = (EditText) dialogAddIdtNote.findViewById(R.id.etAddIDTPtName);
        EditText etAddIDTdate = (EditText) dialogAddIdtNote.findViewById(R.id.etAddIDTdate);
        RadioGroup rgAddIDTptLocation = dialogAddIdtNote.findViewById(R.id.rgAddIDTptLocation);
        CheckBox cbAddIdtSkNursing = dialogAddIdtNote.findViewById(R.id.cbAddIdtSkNursing);
        CheckBox cbAddIdtHomeTheropy = dialogAddIdtNote.findViewById(R.id.cbAddIdtHomeTheropy);
        CheckBox cbAddIdtOutPtTheropy = dialogAddIdtNote.findViewById(R.id.cbAddIdtOutPtTheropy);
        CheckBox[] cbArr = new CheckBox[]{cbAddIdtSkNursing, cbAddIdtHomeTheropy, cbAddIdtOutPtTheropy};
        EditText etAddIdtNote = (EditText) dialogAddIdtNote.findViewById(R.id.etAddIdtNote);
        CheckBox cbIdtIsLocked = dialogAddIdtNote.findViewById(R.id.cbIdtIsLocked);

        LinearLayout layIDTform = dialogAddIdtNote.findViewById(R.id.layIDTform);
        ExpandableHeightListView lvIDTnote = dialogAddIdtNote.findViewById(R.id.lvIDTnote);
        ExpandableHeightListView lvCareGoal = dialogAddIdtNote.findViewById(R.id.lvCareGoal);


        etAddIDTdate.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(etAddIDTdate);
            newFragment.show(appCompatActivity.getSupportFragmentManager(), "datePicker");
        });

        etAddIDTPtName.setText(DATA.selectedUserCallName);
        //etAddIDTdate.setText(new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).format(new Date()));


        btnAddIDTsubmit.setOnClickListener(v -> {


            if(btnAddIDTsubmit.getText().toString().equalsIgnoreCase("Done")){
                dialogAddIdtNote.dismiss();
            }else {
                final Dialog dialogSupport = new Dialog(activity);
                dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogSupport.setContentView(R.layout.dialog_saveassess_opt);

                TextView tvEditAssessDialog = (TextView) dialogSupport.findViewById(R.id.tvEditAssessDialog);
                TextView tvSaveAssesDialog = (TextView) dialogSupport.findViewById(R.id.tvSaveAssesDialog);
                TextView tvConfirmAssess = (TextView) dialogSupport.findViewById(R.id.tvConfirmAssess);

                TextView tvAssesTittle = dialogSupport.findViewById(R.id.tvAssesTittle);

                //tvAssesTittle.setText(assesTittle);

                tvEditAssessDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        dialogSupport.dismiss();
                    }
                });
                tvSaveAssesDialog.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        dialogSupport.dismiss();
                        //assessSubmit.submitAssessment("0");
                        addIDTNote(btnAddIDTsubmit,dialogAddIdtNote,etAddIDTdate,etAddIdtNote,rgAddIDTptLocation,cbArr,"0");
                    }
                });

                tvConfirmAssess.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        dialogSupport.dismiss();
                        //assessSubmit.submitAssessment("1");
                        addIDTNote(btnAddIDTsubmit,dialogAddIdtNote,etAddIDTdate,etAddIdtNote,rgAddIDTptLocation,cbArr,"1");
                    }
                });
                dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogSupport.show();
            }

        });

        ivCancel.setOnClickListener(v -> dialogAddIdtNote.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogAddIdtNote.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogAddIdtNote.setCanceledOnTouchOutside(false);
        dialogAddIdtNote.show();
        dialogAddIdtNote.getWindow().setAttributes(lp);

        //Assign Views to gloabal variables
        lvIDTnoteAddDialog = lvIDTnote;
        lvCareGoalAddDialog = lvCareGoal;
        layIDTformAddDialog = layIDTform;
        btnAddIDTsubmitAddDialog = btnAddIDTsubmit;
        dialogAddIdtNoteForDismiss = dialogAddIdtNote;
        etAddIDTdateAddDialog = etAddIDTdate;

        if(isFromListClick){
            etAddIDTdate.setText(dateofIDt);
            loadIDTnoteByDate(dateofIDt);
        }
        loadLatestCarePlanGoals();


        ActivityIDTnoteList.start_timeIDT = new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    public void addIDTNote(Button btnAddIDTsubmit,
                           Dialog dialogAddIdtNote,
                           EditText etAddIDTdate,
                           EditText etAddIdtNote,
                           RadioGroup rgAddIDTptLocation,
                           CheckBox[] cbArr,
                           String is_lock){


        if(btnAddIDTsubmit.getText().toString().equalsIgnoreCase("Done")){
            dialogAddIdtNote.dismiss();
        }else {
            String dateof = etAddIDTdate.getText().toString().trim();

            String notes = etAddIdtNote.getText().toString().trim();

            if(dateof.isEmpty()){
                etAddIDTdate.setError("");
                customToast.showToast("Please enter the required information", 0, 0);
                return;
            }
            if(notes.isEmpty()){
                etAddIdtNote.setError("");
                customToast.showToast("Please enter the required information", 0, 0);
                return;
            }

            //String is_lock = cbIdtIsLocked.isChecked() ? "1" : "0";

            String patient_location = "";
            if(rgAddIDTptLocation.getCheckedRadioButtonId() == R.id.rbAddIdtAtHome){
                patient_location = "At Home";
            }else if(rgAddIDTptLocation.getCheckedRadioButtonId() == R.id.rbAddIdtHospitalized){
                patient_location = "Hospitalized";
            }else if(rgAddIDTptLocation.getCheckedRadioButtonId() == R.id.rbAddIdtRehab){
                patient_location = "Rehab";
            }else {
                customToast.showToast("Please select patient location",0,0);
                return;
            }


            RequestParams params = new RequestParams();
            params.put("dateof", dateof);
            params.put("patient_location",patient_location);
            for (int i = 0; i < cbArr.length; i++) {
                if(cbArr[i].isChecked()){
                    params.put("services["+i+"]", cbArr[i].getText().toString());
                }
            }
            params.put("notes",notes);
            params.put("is_lock", is_lock);


            params.put("patient_id", DATA.selectedUserCallId);
            params.put("author_id", prefs.getString("id", ""));

            ActivityIDTnoteList.end_timeIDT = new SimpleDateFormat("HH:mm:ss").format(new Date());
            params.put("start_time", ActivityIDTnoteList.start_timeIDT);
            params.put("end_time", ActivityIDTnoteList.end_timeIDT);

            ApiManager apiManager = new ApiManager(ApiManager.ADD_IDT,"post",params, apiCallBack, activity);
            apiManager.loadURL();
        }


    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new, menu);

        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);

        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save_schedule) {

            openActivity.open(ActivityBradenScale.class, false);//not used !

        }else if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;//return true back activity state maintains if false back activity oncreate called
        }
        return super.onOptionsItemSelected(item);
    }*/
}
