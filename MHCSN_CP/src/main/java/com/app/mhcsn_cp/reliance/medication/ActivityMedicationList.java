package com.app.mhcsn_cp.reliance.medication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mhcsn_cp.BaseActivity;
import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.DatePickerFragment;
import com.app.mhcsn_cp.util.RecyclerItemClickListener;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ActivityMedicationList extends BaseActivity {


    Button btnAddMedication,btnStopMedication, btnPastMedication;
    CheckBox cbSortMeds;
    MedicationUtil medicationUtil;


    //===========================MultiSelect Recyclerview=====================================
    ActionMode mActionMode;
    Menu context_menu;

    RecyclerView recyclerView;
    MultiSelectAdapter multiSelectAdapter;
    boolean isMultiSelect = false;

    ArrayList<MedicationBean> user_list = new ArrayList<>();
    ArrayList<MedicationBean> multiselect_list = new ArrayList<>();
    //===========================MultiSelect Recyclerview=====================================

    TextView tvNoMed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_medication_list);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Current Medication List");
        }

        medicationUtil = new MedicationUtil(appCompatActivity);

        btnAddMedication = findViewById(R.id.btnAddMedication);
        btnStopMedication = findViewById(R.id.btnStopMedication);
        btnPastMedication = findViewById(R.id.btnPastMedication);

        mDragListView = (DragListView) findViewById(R.id.lvMedications);

        cbSortMeds = findViewById(R.id.cbSortMeds);

        tvNoMed = findViewById(R.id.tvNoMed);



        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btnAddMedication:
                        medicationUtil.initDrugsDialog();
                        break;

                    case R.id.btnStopMedication:

                        EditText editText = new EditText(activity);
                        editText.setId(R.id.etStopMedicationID);
                        DialogFragment newFragment = new DatePickerFragment(editText);
                        newFragment.show(getSupportFragmentManager(), "datePicker");

                        DATA.print("-- btnStopMedication click");

                        break;

                    case R.id.btnPastMedication:
                        loadPastMedications();
                        break;
                }
            }
        };
        btnAddMedication.setOnClickListener(onClickListener);
        btnStopMedication.setOnClickListener(onClickListener);
        btnPastMedication.setOnClickListener(onClickListener);



        //===========================MultiSelect Recyclerview=====================================
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        //user_list  = sharedPrefsHelper.getAllContacts();

        //multiSelectAdapter = new MultiSelectAdapter(this,user_list,multiselect_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setAdapter(multiSelectAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect){
                    multi_select(position);
                } else{
                    /*if(multiSelectAdapter.expandPos == position){
                        multiSelectAdapter.expandPos = -1;
                    }else {
                        multiSelectAdapter.expandPos = position;
                    }
                    multiSelectAdapter.notifyDataSetChanged();*/

                    showMedicationDetailDialog(medicationBeans.get(position),1);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<MedicationBean>();
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback);
                    }
                }

                multi_select(position);

            }
        }));
        //===========================MultiSelect Recyclerview=====================================



        cbSortMeds.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mDragListView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else {
                    mDragListView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });


        loadMedications();
    }


    //===========================MultiSelect Recyclerview=====================================
    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(user_list.get(position)))
                multiselect_list.remove(user_list.get(position));
            else
                multiselect_list.add(user_list.get(position));

            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();

        }
    }


    public void refreshAdapter()
    {
        multiSelectAdapter.selected_usersList=multiselect_list;
        multiSelectAdapter.usersList=user_list;
        multiSelectAdapter.notifyDataSetChanged();
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    //alertDialogHelper.showAlertDialog("","Delete Contact","DELETE","CANCEL",1,false);

                    /*ActivitySendMsg.contactBeans = new ArrayList<>();
                    ActivitySendMsg.contactBeans.addAll(multiselect_list);

                    ActivitySendMsg.isSingleMsg = false;
                    openActivity.open(ActivitySendMsg.class,false);*/


                    if(!multiselect_list.isEmpty()){

                        EditText editText = new EditText(activity);
                        editText.setId(R.id.etStopMedicationID);
                        DialogFragment newFragment = new DatePickerFragment(editText);
                        newFragment.show(getSupportFragmentManager(), "datePicker");
                    }else {
                        customToast.showToast("Please select medication(s) to stop",0,0);
                    }







                    /*if (Utils.isDefaultSmsApp(activity)) {
                        //setDefaultAppButton.setVisibility(View.GONE);
                        //ActivitySendMsg.contactBeans = multiselect_list;

                        ActivitySendMsg.contactBeans = new ArrayList<>();
                        ActivitySendMsg.contactBeans.addAll(multiselect_list);

                        ActivitySendMsg.isSingleMsg = false;
                        openActivity.open(ActivitySendMsg.class,false);
                    } else {
                        //setDefaultSmsApp();
                        new AlertDialog.Builder(activity).setTitle(getString(R.string.app_name))
                                .setMessage("Please set "+getString(R.string.app_name)+" as default message app to send multiple messages")
                                .setPositiveButton("Set as default", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        setDefaultSmsApp();
                                    }
                                }).setNegativeButton("Not now",null).create().show();
                    }*/

                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<>();
            refreshAdapter();
        }
    };
    //===========================MultiSelect Recyclerview=====================================



    public void loadMedications(){
        RequestParams params = new RequestParams();
        params.put("patient_id", DATA.selectedUserCallId);

        ApiManager apiManager = new ApiManager(ApiManager.MEDICATION_LIST,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    public void sortMedications(String ids){
        RequestParams params = new RequestParams();
        params.put("ids", ids);

        ApiManager apiManager = new ApiManager(ApiManager.MEDICATION_SORT,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }



    public void stopMedications(String dateSelected){
        new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Confirm")
                //.setMessage("Are you sure? Do you want to stop "+ multiselect_list.size()+"  medications with date "+dateSelected+"?")
                .setMessage("Are you sure? Do you want to stop "+ medIdsStopMed.split(",").length+"  medications with date "+dateSelected+"?")
                .setPositiveButton("Yes Stop", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*StringBuilder sbIds = new StringBuilder();
                        for (int i = 0; i < multiselect_list.size(); i++) {
                            sbIds.append(multiselect_list.get(i).id);
                            if(i < (multiselect_list.size()-1)){
                                sbIds.append(",");
                            }
                        }*/
                        //note : multi select RV not used more

                        RequestParams params = new RequestParams();
                        params.put("ids", medIdsStopMed);//sbIds.toString()
                        params.put("stop_date", dateSelected);

                        ApiManager apiManager = new ApiManager(ApiManager.MEDICATION_STOP,"post",params,apiCallBack, activity);
                        apiManager.loadURL();
                    }
                })
                .setNegativeButton("Not Now",null)
                .create().show();
    }


    public void loadPastMedications(){
        RequestParams params = new RequestParams();
        params.put("patient_id", DATA.selectedUserCallId);

        ApiManager apiManager = new ApiManager(ApiManager.MEDICATION_PAST_LIST,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }



    ArrayList<MedicationBean> medicationBeans;
    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        super.fetchDataCallback(status, apiName, content);

        if (apiName.contains(ApiManager.MEDICATION_LIST)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                Type listType = new TypeToken<ArrayList<MedicationBean>>() {}.getType();
                medicationBeans = gson.fromJson(data.toString(), listType);

                int vis = medicationBeans.isEmpty() ? View.VISIBLE : View.GONE;
                tvNoMed.setVisibility(vis);

                user_list = medicationBeans;

                multiSelectAdapter = new MultiSelectAdapter(this,user_list,multiselect_list);
                recyclerView.setAdapter(multiSelectAdapter);


                initDragableList();

                btnStopMedication.setText("Stop Medication");
                btnStopMedication.setEnabled(false);

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.MEDICATION_SORT) || apiName.equalsIgnoreCase(ApiManager.MEDICATION_STOP) ||
                apiName.equalsIgnoreCase(ApiManager.MEDICATION_UPDATE) || apiName.equalsIgnoreCase(ApiManager.MEDICATION_RENEW)){
            //{"status":"success","message":"Saved.."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                customToast.showToast(jsonObject.optString("message"),0,1);
                if(jsonObject.optString("status").equalsIgnoreCase("success")){

                    if(dialogMediDetailG != null){
                        dialogMediDetailG.dismiss();
                    }
                    loadMedications();
                }
            } catch (Exception e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }else if (apiName.contains(ApiManager.MEDICATION_PAST_LIST)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");

                Type listType = new TypeToken<ArrayList<MedicationBean>>() {}.getType();
                ArrayList<MedicationBean> medicationBeansPast = gson.fromJson(data.toString(), listType);

                showPastMedicationListDialog(medicationBeansPast);

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }






    private ArrayList<Pair<Long, MedicationBean>> mItemArray;
    private DragListView mDragListView;
    private void initDragableList(){
        //mDragListView = (DragListView) findViewById(R.id.lvMedications);
        mDragListView.getRecyclerView().setVerticalScrollBarEnabled(true);
        mDragListView.setDragListListener(new DragListView.DragListListenerAdapter() {
            @Override
            public void onItemDragStarted(int position) {
                //mRefreshLayout.setEnabled(false);
                //Toast.makeText(mDragListView.getContext(), "Start - position: " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                //mRefreshLayout.setEnabled(true);
                if (fromPosition != toPosition) {
                    //Toast.makeText(mDragListView.getContext(), "End - position: " + toPosition, Toast.LENGTH_SHORT).show();


                    StringBuilder sbIds = new StringBuilder();

                    for (int i = 0; i < mItemArray.size(); i++) {
                        Pair<Long, MedicationBean> pair =  mItemArray.get(i);
                        sbIds.append(pair.second.id);
                        if(i < (mItemArray.size() - 1)){
                            sbIds.append(",");
                        }
                    }

                    sortMedications(sbIds.toString());
                }
            }
        });

        mItemArray = new ArrayList<>();
        for (int i = 0; i < medicationBeans.size(); i++) {
            mItemArray.add(new Pair<>((long) i, medicationBeans.get(i)));
        }




        mDragListView.setLayoutManager(new LinearLayoutManager(activity));
        ItemAdapter listAdapter = new ItemAdapter(mItemArray, R.layout.lv_medication_drag_row, R.id.item_layout, true, activity);
        mDragListView.setAdapter(listAdapter, true);
        mDragListView.setCanDragHorizontally(false);
        //mDragListView.setCanDragVertically(true);
        mDragListView.setCustomDragItem(new MyDragItem(activity, R.layout.lv_medication_drag_row));
    }

    private static class MyDragItem extends DragItem {

        MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            CharSequence text = ((TextView) clickedView.findViewById(R.id.tvMedName)).getText();
            ((TextView) dragView.findViewById(R.id.tvMedName)).setText(text);

            CharSequence text2 = ((TextView) clickedView.findViewById(R.id.tvMedStrength)).getText();
            ((TextView) dragView.findViewById(R.id.tvMedStrength)).setText(text2);

            CharSequence text3 = ((TextView) clickedView.findViewById(R.id.tvMedInstructions)).getText();
            ((TextView) dragView.findViewById(R.id.tvMedInstructions)).setText(text3);
            //dragView.findViewById(R.id.item_layout).setBackgroundColor(dragView.getResources().getColor(R.color.list_item_background));
        }
    }









    Dialog dialogMediDetailG = null;
    public void showMedicationDetailDialog(MedicationBean medicationBean, int flag){//flag -> 1=View, 2=Edit, 3=Renew
        Dialog dialogMediDetail = new Dialog(activity,R.style.TransparentThemeH4B);
        dialogMediDetail.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogMediDetail.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogMediDetail.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogMediDetail.setContentView(R.layout.dialog_medication_view);

        ImageView ivCancel = (ImageView) dialogMediDetail.findViewById(R.id.ivCancel);
        TextView tvHeader = dialogMediDetail.findViewById(R.id.tvHeader);

        EditText etMedName =  dialogMediDetail.findViewById(R.id.etMedName);
        EditText etRoute = (EditText) dialogMediDetail.findViewById(R.id.etRoute);
        EditText etPurpose = (EditText) dialogMediDetail.findViewById(R.id.etPurpose);
        EditText etPrescriber = (EditText) dialogMediDetail.findViewById(R.id.etPrescriber);
        EditText etStartDate = (EditText) dialogMediDetail.findViewById(R.id.etStartDate);
        EditText etStopDate = (EditText) dialogMediDetail.findViewById(R.id.etStopDate);
        EditText etDirection = (EditText) dialogMediDetail.findViewById(R.id.etDirection);
        EditText etStrength = (EditText) dialogMediDetail.findViewById(R.id.etStrength);
        EditText etNotes = (EditText) dialogMediDetail.findViewById(R.id.etNotes);

        EditText etModifiedBy = (EditText) dialogMediDetail.findViewById(R.id.etModifiedBy);
        EditText etModifiedDate = (EditText) dialogMediDetail.findViewById(R.id.etModifiedDate);



        Button btnEdit = (Button) dialogMediDetail.findViewById(R.id.btnEdit);
        //Button btnRenew = (Button) dialogMediDetail.findViewById(R.id.btnRenew);
        //Button btnStopMedication = (Button) dialogMediDetail.findViewById(R.id.btnStopMedication);


        etMedName.setText(medicationBean.name);
        etRoute.setText(medicationBean.route);
        etPurpose.setText(medicationBean.purpose);
        etPrescriber.setText(medicationBean.doctor_name);
        etPrescriber.setTag(medicationBean.author_id);//important
        etStartDate.setText(medicationBean.start_date);
        etStopDate.setText(medicationBean.stop_date);
        etDirection.setText(medicationBean.directions);
        etStrength.setText(medicationBean.strength);
        etNotes.setText(medicationBean.notes);
        etModifiedBy.setText(medicationBean.modified_by);
        etModifiedDate.setText(medicationBean.modified_date);


        if(flag == 1){//view
            etMedName.setEnabled(false);
            etMedName.setBackgroundResource(android.R.color.transparent);
            etRoute.setEnabled(false);
            etRoute.setBackgroundResource(android.R.color.transparent);
            etPurpose.setEnabled(false);
            etPurpose.setBackgroundResource(android.R.color.transparent);
            etPrescriber.setEnabled(false);
            etPrescriber.setBackgroundResource(android.R.color.transparent);
            etStartDate.setEnabled(false);
            etStartDate.setBackgroundResource(android.R.color.transparent);
            etStopDate.setEnabled(false);
            etStopDate.setBackgroundResource(android.R.color.transparent);
            etDirection.setEnabled(false);
            etDirection.setBackgroundResource(android.R.color.transparent);
            etStrength.setEnabled(false);
            etStrength.setBackgroundResource(android.R.color.transparent);
            etNotes.setEnabled(false);
            etNotes.setBackgroundResource(android.R.color.transparent);

            btnEdit.setText("Done");
            tvHeader.setText("View Medication");

            etMedName.setHint("");
            etRoute.setHint("");
            etPurpose.setHint("");
            etPrescriber.setHint("");
            etPrescriber.setHint("");
            etStartDate.setHint("");
            etStopDate.setHint("");
            etDirection.setHint("");
            etStrength.setHint("");
            etNotes.setHint("");
            etModifiedBy.setHint("");
            etModifiedDate.setHint("");

        }if(flag == 3){//Reniew
            etMedName.setEnabled(false);
            etMedName.setBackgroundResource(android.R.color.transparent);
            etRoute.setEnabled(false);
            etRoute.setBackgroundResource(android.R.color.transparent);
            etPurpose.setEnabled(false);
            etPurpose.setBackgroundResource(android.R.color.transparent);
            etPrescriber.setEnabled(false);
            etPrescriber.setBackgroundResource(android.R.color.transparent);
            etStartDate.setEnabled(false);
            etStartDate.setBackgroundResource(android.R.color.transparent);
            etStopDate.setEnabled(false);
            etStopDate.setBackgroundResource(android.R.color.transparent);
            etDirection.setEnabled(true);
            etDirection.setBackgroundResource(R.drawable.cust_border_white_outline);
            etStrength.setEnabled(true);
            etStrength.setBackgroundResource(R.drawable.cust_border_white_outline);
            etNotes.setEnabled(false);
            etNotes.setBackgroundResource(android.R.color.transparent);

            btnEdit.setText("Renew Medication");
            tvHeader.setText("Renew Medication");
        }


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){

                    case R.id.etStartDate:
                        DialogFragment newFragment = new DatePickerFragment(etStartDate);
                        newFragment.show(getSupportFragmentManager(), "datePicker");
                        break;

                    case R.id.etStopDate:
                        DialogFragment newFragment2 = new DatePickerFragment(etStopDate);
                        newFragment2.show(getSupportFragmentManager(), "datePicker");
                        break;

                    case R.id.btnEdit:

                        if(flag == 1) {//view
                            dialogMediDetail.dismiss();
                        }else if(flag == 2) {//edit
                            editReniewAPIcall(etRoute, etPurpose, etStartDate, etStopDate,etNotes,etPrescriber, etStrength, etDirection ,medicationBean, ApiManager.MEDICATION_UPDATE);
                        }else if(flag == 3) {//Renew
                            editReniewAPIcall(etRoute, etPurpose, etStartDate, etStopDate,etNotes,etPrescriber, etStrength, etDirection,medicationBean, ApiManager.MEDICATION_RENEW);
                        }
                        break;
                    /*case R.id.btnRenew:
                        editReniewAPIcall(etRoute, etPurpose, etStartDate, etStopDate,etNotes,etPrescriber,medicationBean, ApiManager.MEDICATION_RENEW);
                        break;*/

                    case R.id.etPrescriber:
                        medicationUtil.showMyDoctorsDialog(etPrescriber);
                        break;
                }
            }
        };

        etStartDate.setOnClickListener(onClickListener);
        etStopDate.setOnClickListener(onClickListener);
        btnEdit.setOnClickListener(onClickListener);
        //btnRenew.setOnClickListener(onClickListener);
        //btnStop.setOnClickListener(onClickListener);
        etPrescriber.setOnClickListener(onClickListener);


        ivCancel.setOnClickListener(v -> dialogMediDetail.dismiss());


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogMediDetail.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogMediDetail.setCanceledOnTouchOutside(false);
        dialogMediDetail.show();
        dialogMediDetail.getWindow().setAttributes(lp);

        dialogMediDetailG = dialogMediDetail;
    }


    public void editReniewAPIcall(EditText etRoute, EditText etPurpose, EditText etStartDate, EditText etStopDate, EditText etNotes, EditText etPrescriber,
                                  EditText etStrength, EditText etDirections ,MedicationBean medicationBean, String apiName){
        String route  = etRoute.getText().toString().trim();
        String purpose = etPurpose.getText().toString().trim();
        String start_date = etStartDate.getText().toString().trim();
        String stop_date = etStopDate.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        //Object autherId = etPrescriber.getTag();
        String autherId = etPrescriber.getText().toString().trim();

        String strength = etStrength.getText().toString().trim();//for renew api
        String directions = etDirections.getText().toString().trim();//for renew api

        boolean validatedEmpty = true;
        if(apiName.equalsIgnoreCase(ApiManager.MEDICATION_UPDATE)){
            if(route.isEmpty()){etRoute.setError("Required");validatedEmpty = false;}
            if(purpose.isEmpty()){etPurpose.setError("Required");validatedEmpty = false;}
            if(start_date.isEmpty()){etStartDate.setError("Required");validatedEmpty = false;}
            //if(stop_date.isEmpty()){etStopDate.setError("Required");validatedEmpty = false;}
            if(notes.isEmpty()){etNotes.setError("Required");validatedEmpty = false;}
            //if(autherId == null){etPrescriber.setError("Required");validatedEmpty = false;}
            if(autherId.isEmpty()){etPrescriber.setError("Required");validatedEmpty = false;}
        }else if(apiName.equalsIgnoreCase(ApiManager.MEDICATION_RENEW)){
            if(strength.isEmpty()){etStrength.setError("Required");validatedEmpty = false;}
            if(directions.isEmpty()){etDirections.setError("Required");validatedEmpty = false;}
        }

        if(! validatedEmpty){customToast.showToast("Please enter the required information", 0, 0);return;}

        RequestParams params = new RequestParams();
        params.put("id", medicationBean.id);
        params.put("route", route);
        params.put("purpose", purpose);
        params.put("start_date", start_date);
        params.put("stop_date", stop_date);
        params.put("notes", notes);
        params.put("author_id", autherId);

        params.put("strength", strength);//for renew api
        params.put("directions", directions);//for renew api

        params.put("modified_by", prefs.getString("id", ""));


        ApiManager apiManager = new ApiManager(apiName,"post",params, apiCallBack, activity);
        apiManager.loadURL();
    }





    public void showPastMedicationListDialog(ArrayList<MedicationBean> medicationBeansPast){
        Dialog dialogMediPast = new Dialog(activity,R.style.TransparentThemeH4B);
        dialogMediPast.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogMediPast.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogMediPast.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogMediPast.setContentView(R.layout.dialog_medication_past);

        ImageView ivCancel = (ImageView) dialogMediPast.findViewById(R.id.ivCancel);
        ListView lvPastMed =  dialogMediPast.findViewById(R.id.lvPastMed);
        Button btnDone = (Button) dialogMediPast.findViewById(R.id.btnDone);
        TextView tvNoData = dialogMediPast.findViewById(R.id.tvNoData);


        lvPastMed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                showMedicationDetailDialog(medicationBeansPast.get(position),1);

                DATA.print(" -- item click pos "+position);
            }
        });


        int vis = medicationBeansPast.isEmpty() ? View.VISIBLE : View.GONE;
        tvNoData.setVisibility(vis);
        lvPastMed.setAdapter(new LvPastMedicationAdapter(activity, medicationBeansPast));


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){

                    case R.id.btnDone:
                        dialogMediPast.dismiss();
                        break;

                    case R.id.ivCancel:
                        dialogMediPast.dismiss();
                        break;
                }
            }
        };

        btnDone.setOnClickListener(onClickListener);
        ivCancel.setOnClickListener(onClickListener);


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogMediPast.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogMediPast.setCanceledOnTouchOutside(false);
        dialogMediPast.show();
        dialogMediPast.getWindow().setAttributes(lp);

        //dialogMediPastG = dialogMediPast;
    }




    String medIdsStopMed;
    public void enableStopBtn(){
        boolean isAtleastOneSelected = false;
        StringBuilder sbIds = new StringBuilder();
        for (int i = 0; i < medicationBeans.size(); i++) {
            if(medicationBeans.get(i).isSelected){
                sbIds.append(medicationBeans.get(i).id);
                sbIds.append(",");
                isAtleastOneSelected = true;
            }
        }
        if(isAtleastOneSelected){
            medIdsStopMed = sbIds.substring(0, sbIds.length()-1);
            DATA.print("-- medIds "+medIdsStopMed);
            btnStopMedication.setText("Stop "+medIdsStopMed.split(",").length+ " Medication(s)");
        }else {
            btnStopMedication.setText("Stop Medication");
        }
        btnStopMedication.setEnabled(isAtleastOneSelected);
    }


}
