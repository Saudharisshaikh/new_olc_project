package com.app.msu_cp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.msu_cp.adapters.DocToDocAdapter;
import com.app.msu_cp.adapters.UsersAdapter;
import com.app.msu_cp.api.ApiCallBack;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.api.CustomSnakeBar;
import com.app.msu_cp.model.DoctorsModel;
import com.app.msu_cp.util.CheckInternetConnection;
import com.app.msu_cp.util.CustomToast;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.HideShowKeypad;
import com.app.msu_cp.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityDocToDocNew extends AppCompatActivity implements ApiCallBack{

    AppCompatActivity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    CustomSnakeBar customSnakeBar;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;

    Button btnDoctors,btnSpecialist;
    ListView lvDoctors;
    TextView tvNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_to_doc_new);

        activity = ActivityDocToDocNew.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        openActivity = new OpenActivity(activity);
        apiCallBack = this;

        btnDoctors = (Button) findViewById(R.id.btnDoctors);
        btnSpecialist = (Button) findViewById(R.id.btnSpecialist);
        lvDoctors = (ListView) findViewById(R.id.lvDoctors);
        tvNoData = (TextView) findViewById(R.id.tvNoData);

        btnDoctors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                    btnDoctors.setBackgroundColor(getResources().getColor(R.color.theme_red));
                    btnDoctors.setTextColor(getResources().getColor(android.R.color.white));
                    btnSpecialist.setBackgroundColor(getResources().getColor(android.R.color.white));
                    btnSpecialist.setTextColor(getResources().getColor(R.color.theme_red));
                    //btnAll.setBackgroundColor(getResources().getColor(android.R.color.white));
                    //btnAll.setTextColor(getResources().getColor(R.color.theme_red));
                    getAllDrs("doctor");
            }
        });
        btnSpecialist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                    btnDoctors.setBackgroundColor(getResources().getColor(android.R.color.white));
                    btnDoctors.setTextColor(getResources().getColor(R.color.theme_red));
                    btnSpecialist.setBackgroundColor(getResources().getColor(R.color.theme_red));
                    btnSpecialist.setTextColor(getResources().getColor(android.R.color.white));
                    //btnAll.setBackgroundColor(getResources().getColor(android.R.color.white));
                    //btnAll.setTextColor(getResources().getColor(R.color.theme_red));
                    getAllDrs("specialist");
            }
        });

        /*lvDoctors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                    DATA.selectedDrId = doctorsModels.get(position).id;
                   // DATA.selectedUserCallId = doctorsModels.get(position).id;
                    DATA.selectedDrName = doctorsModels.get(position).fName + " " + doctorsModels.get(position).lName;
                    DATA.selectedDrQbId = doctorsModels.get(position).qb_id;
                    DATA.selectedDrImage = doctorsModels.get(position).image;
                    DATA.selectedDrQualification = doctorsModels.get(position).qualification;
                    DATA.isFromDocToDoc = true;
                    DATA.selectedDoctorsModel = doctorsModels.get(position);
                    Intent intent = new Intent(activity,SelectedDoctorsProfile.class);
                    intent.putExtra("isFromMyDoctors",true);
                    startActivity(intent);
                    //finish();
                }
        });*/

        getAllDrs("doctor");
    }
    String user_typeGloabal;
    public void getAllDrs(String user_type) {
        user_typeGloabal = user_type;
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("user_type", user_type);

        ApiManager apiManager = new ApiManager(ApiManager.SEARCHALLDOCTORS_,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<DoctorsModel> doctorsModels;
    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if(apiName.equals(ApiManager.SEARCHALLDOCTORS_)){
            try {
                JSONArray data = new JSONArray(content);
                doctorsModels = new ArrayList<DoctorsModel>();
                DoctorsModel temp = null;

                if (data.length() == 0) {
                    //showMessageBox(activity, "We are sorry", "Currently no doctors available");
                    tvNoData.setVisibility(View.VISIBLE);
                    UsersAdapter usersAdapter = new UsersAdapter(activity);
                    lvDoctors.setAdapter(usersAdapter);
                }else{
                    tvNoData.setVisibility(View.GONE);

                    for (int i = 0; i < data.length(); i++) {
                        temp = new DoctorsModel();
                        JSONObject object = data.getJSONObject(i);
                        temp.id = object.getString("id");
                        temp.latitude =object.getString("latitude");
                        temp.longitude=object.getString("longitude");
                        temp.zip_code=object.getString("zip_code");
                        temp.fName=object.getString("first_name");
                        temp.lName=object.getString("last_name");
                        temp.is_online=object.getString("is_online");
                        temp.image=object.getString("image");
                        temp.designation=object.getString("designation");


                        if (temp.latitude.equalsIgnoreCase("null")) {
                            temp.latitude = "0.0";
                        }
                        if (temp.longitude.equalsIgnoreCase("null")) {
                            temp.longitude = "0.0";
                        }

                        temp.speciality_id=object.getString("speciality_id");
                        temp.current_app=user_typeGloabal;//object.getString("current_app");
                        temp.speciality_name=object.getString("speciality_name");

                        temp.qualification = object.getString("qualification");
                        temp.introduction = object.getString("introduction");
                        temp.careerData = object.getString("career_data");
                        if (object.has("mobile")) {
                            temp.mobile = object.getString("mobile");
                        }

                        doctorsModels.add(temp);
                        temp = null;
                    }

					/*if (checkGooglePlayservices()) {
						initilizeMap(latLongBeansList);
					}*/
                    DocToDocAdapter docToDocAdapter = new DocToDocAdapter(activity,doctorsModels);
                    lvDoctors.setAdapter(docToDocAdapter);
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
