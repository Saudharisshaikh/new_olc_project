package com.app.mhcsn_uc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mhcsn_uc.adapter.GroupMemberAdapter;
import com.app.mhcsn_uc.api.ApiCallBack;
import com.app.mhcsn_uc.api.ApiManager;
import com.app.mhcsn_uc.util.CheckInternetConnection;
import com.app.mhcsn_uc.util.CustomToast;
import com.app.mhcsn_uc.util.DATA;
import com.app.mhcsn_uc.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityGroupMembers extends AppCompatActivity implements ApiCallBack{

    Activity activity;
    SharedPreferences prefs;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    ApiCallBack apiCallBack;

    ListView lvGroupmembers;
    TextView tvNoMember;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);

        activity = ActivityGroupMembers.this;
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        openActivity = new OpenActivity(activity);
        apiCallBack = this;

        lvGroupmembers = (ListView) findViewById(R.id.lvGroupmembers);
        tvNoMember = (TextView) findViewById(R.id.tvNoMember);


        if(ActivityGroupMessages.groupMemberBeens.isEmpty()){
            tvNoMember.setVisibility(View.VISIBLE);
        }else {
            tvNoMember.setVisibility(View.VISIBLE);
        }

        lvGroupmembers.setAdapter(new GroupMemberAdapter(this,ActivityGroupMessages.groupMemberBeens));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_soap_notes, menu);

        menu.getItem(0).setTitle("Leave Group");

        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save_schedule) {
            AlertDialog alertDialog =
                    new AlertDialog.Builder(activity).setTitle("Confirm")
                            .setMessage("Do you want to leave the group ?")
                            .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    removeFromGroup("patient",prefs.getString("id", ""),true);
                                }
                            }).setNegativeButton("Cancel",null).create();
            alertDialog.show();
        }else if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if(apiName.equals(ApiManager.REMOVE_GROUP_USER)){
            //{"status":"success","message":"Deleted"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.getString("status").equals("success")){
                    if (isMe){
                        customToast.showToast("You left the group",0,1);
                        finish();
                        try{
                            ActivityGroupMessages.activity.finish();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else {
                        /*customToast.showToast("Group member has been removed",0,1);
                        ActivityGroupMessages.groupMemberBeens.remove(bean);
                        groupMemberAdapter.notifyDataSetChanged(); for group admin only patient is not admin*/
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }

    boolean isMe = false;
    void removeFromGroup(String userType,String userId,boolean isMe){
        this.isMe = isMe;
        RequestParams params = new RequestParams();
        params.put("group_id", ActivityGroupChat.selectedGroupBean.id);
        params.put("user_type", userType);
        params.put("user_id", userId);

        ApiManager apiManager = new ApiManager(ApiManager.REMOVE_GROUP_USER,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }
}
