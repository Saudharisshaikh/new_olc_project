package com.app.priorityone_dr;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.priorityone_dr.api.ApiCallBack;
import com.app.priorityone_dr.api.ApiManager;
import com.app.priorityone_dr.model.GroupBean;
import com.app.priorityone_dr.util.CheckInternetConnection;
import com.app.priorityone_dr.util.CustomToast;
import com.app.priorityone_dr.util.DATA;
import com.app.priorityone_dr.util.Database;
import com.app.priorityone_dr.util.HideShowKeypad;
import com.app.priorityone_dr.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityGroupChat extends BaseActivity implements ApiCallBack{

    AppCompatActivity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;

    ListView lvGroups;
    TextView tvNoGroup;

    @Override
    protected void onResume() {
        RequestParams params = new RequestParams();
        params.put("user_id", prefs.getString("id", ""));
        params.put("user_type", "doctor");

        ApiManager apiManager = new ApiManager(ApiManager.API_GET_ROOMS,"post",params,apiCallBack, activity);
        apiManager.loadURL();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        activity = ActivityGroupChat.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        openActivity = new OpenActivity(activity);
        apiCallBack = this;

        new Database(activity).deleteNotif(DATA.NOTIF_TYPE_GROUP_MESSAGE);

        tvNoGroup = (TextView) findViewById(R.id.tvNoGroup);
        lvGroups = (ListView) findViewById(R.id.lvGroups);

        lvGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedGroupBean = groupBeens.get(position);
                openActivity.open(ActivityGroupMessages.class, false);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_plus, menu);

        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);

        //return true;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            /*final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.add_to_group_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            Button btnPatient = (Button) dialog.findViewById(R.id.btnPatient);
            Button btnDocSp = (Button) dialog.findViewById(R.id.btnDocSp);
            Button btnCP = (Button) dialog.findViewById(R.id.btnCP);
            Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btnDocSp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openActivity.open(ActivityDoctors.class,false);
                }
            });

            dialog.show();*/
            createFolderDialog();

        }else if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    List<GroupBean> groupBeens;
    public static GroupBean selectedGroupBean;
    @Override
    public void fetchDataCallback(String status, String apiName, String content) {

        if (apiName.equals(ApiManager.API_GET_ROOMS)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                groupBeens = new ArrayList<>();
                GroupBean bean;
                if(data.length() == 0){
                    ArrayAdapter<GroupBean> adapter = new ArrayAdapter<GroupBean>(activity,android.R.layout.simple_list_item_1,
                            groupBeens);
                    lvGroups.setAdapter(adapter);
                    tvNoGroup.setVisibility(View.VISIBLE);
                }else{
                    tvNoGroup.setVisibility(View.GONE);

                    for (int i = 0; i < data.length(); i++) {
                        String id = data.getJSONObject(i).getString("id");
                        String group_name = data.getJSONObject(i).getString("group_name");
                        String created_by = data.getJSONObject(i).getString("created_by");
                        String created_date = data.getJSONObject(i).getString("created_date");

                        bean = new GroupBean(id,group_name,created_by,created_date);
                        groupBeens.add(bean);
                    }

                    ArrayAdapter<GroupBean> adapter = new ArrayAdapter<GroupBean>(activity,android.R.layout.simple_list_item_1,
                            groupBeens);
                    lvGroups.setAdapter(adapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }else if(apiName.equals(ApiManager.CREATE_GROUP)){
            //result: {"status":"success","message":"Group has been created"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                customToast.showToast(jsonObject.getString("message"),0,0);
                createGroupDialog.dismiss();
                RequestParams params = new RequestParams();
                params.put("user_id", prefs.getString("id", ""));
                params.put("user_type", "doctor");

                ApiManager apiManager = new ApiManager(ApiManager.API_GET_ROOMS,"post",params,apiCallBack, activity);
                apiManager.loadURL();

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }

        }
    }


    Dialog createGroupDialog;
    public void createFolderDialog() {
        createGroupDialog = new Dialog(activity);
        createGroupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        createGroupDialog.setContentView(R.layout.dialog_create_group);
        createGroupDialog.getWindow().setBackgroundDrawableResource(R.drawable.cust_border_white_outline);
        final EditText etGroupName = (EditText) createGroupDialog.findViewById(R.id.etGroupName);
        Button btnCreateGroup = (Button) createGroupDialog.findViewById(R.id.btnCreateGroup);
        ImageView ivClose = (ImageView) createGroupDialog.findViewById(R.id.ivClose);


        btnCreateGroup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                    String groupName = etGroupName.getText().toString();
                    if (groupName.isEmpty()) {
                        customToast.showToast("Please enter group name", 0, 0);
                    } else {
                        RequestParams params = new RequestParams();
                        params.put("doctor_id", prefs.getString("id", ""));
                        params.put("group_name", groupName);

                        ApiManager apiManager = new ApiManager(ApiManager.CREATE_GROUP,"post",params,apiCallBack, activity);
                        apiManager.loadURL();
                    }
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroupDialog.dismiss();
            }
        });
        createGroupDialog.show();
    }
}
