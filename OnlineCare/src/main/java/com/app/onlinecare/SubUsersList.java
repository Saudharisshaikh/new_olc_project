package com.app.onlinecare;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.app.onlinecare.adapter.SubUsersAdapter;
import com.app.onlinecare.util.DATA;
import com.app.onlinecare.util.GloabalMethods;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class SubUsersList  extends BaseActivity {

    ListView lvSubUsersList;
    SubUsersAdapter usersAdapter;


    static boolean shouldLoadData = false;

    public boolean isFromHome = false;

    GloabalMethods gloabalMethods;

    @Override
    protected void onResume() {
        super.onResume();
        if(shouldLoadData){
            shouldLoadData = false;
            gloabalMethods.loadSubPatients();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subusers_list);

        isFromHome = getIntent().getBooleanExtra("isFromHome", false);

        gloabalMethods = new GloabalMethods(activity);

        lvSubUsersList = (ListView) findViewById(R.id.lvSubUsersList);


        lvSubUsersList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				/*SharedPreferences.Editor ed1 = prefs.edit();
				ed1.putString("insurance",DATA.allSubUsers.get(position).insurance);
				ed1.putBoolean("livecareTimerRunning", false);
				ed1.commit();

					*//*Intent i = new Intent();
					i.setAction("LIVE_CARE_WAITING_TIMER_STOP");
					sendBroadcast(i);*//*


				SharedPreferences.Editor ed = prefs.edit();
				ed.putString("id", DATA.allSubUsers.get(position).id);
				ed.putString("subPatientID", DATA.allSubUsers.get(position).id);
				ed.putString("first_name", DATA.allSubUsers.get(position).firstName);
				ed.putString("last_name", DATA.allSubUsers.get(position).lastName);
				ed.putString("username", DATA.allSubUsers.get(position).username);
				ed.putString("gender", DATA.allSubUsers.get(position).gender);
				ed.putString("birthdate", DATA.allSubUsers.get(position).dob);
				//ed.putString("phone", user_info.getString("phone"));
				ed.putString("image", DATA.allSubUsers.get(position).image);
				ed.putString("occupation", DATA.allSubUsers.get(position).occupation);
				ed.putString("marital_status", DATA.allSubUsers.get(position).marital_status);

				ed.putBoolean("subUserSelected", true);
				ed.commit();

				String patientName = prefs.getString("first_name", "") + " " +prefs.getString("last_name", "");
				customToast.showToast("Welcome: "+patientName, 0	, 0);

				openActivity.open(Splash.class,true);*/


                if(position == 0 && !isFromHome){
                    boolean shouldProceed = true;

                    try {
                        Date patientDOB = new SimpleDateFormat("mm/dd/yyyy").parse(DATA.allSubUsers.get(position).dob);
                        Date currentDate = new Date();
                        int diffYears = getDiffYears(patientDOB, currentDate);
                        System.out.println("-- diff years: "+diffYears);

                        if(position != 0){
                            if(diffYears < 18){
                                shouldProceed = true;
                            }else {
                                shouldProceed = false;
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    if(shouldProceed){
                        SharedPreferences.Editor ed1 = prefs.edit();
                        ed1.putString("insurance",DATA.allSubUsers.get(position).insurance);
                        ed1.putBoolean("livecareTimerRunning", false);
                        ed1.commit();

					/*Intent i = new Intent();
					i.setAction("LIVE_CARE_WAITING_TIMER_STOP");
					sendBroadcast(i);*/


                        SharedPreferences.Editor ed = prefs.edit();
                        ed.putString("id", DATA.allSubUsers.get(position).id);
                        ed.putString("subPatientID", DATA.allSubUsers.get(position).id);
                        ed.putString("first_name", DATA.allSubUsers.get(position).firstName);
                        ed.putString("last_name", DATA.allSubUsers.get(position).lastName);
                        ed.putString("username", DATA.allSubUsers.get(position).username);
                        ed.putString("gender", DATA.allSubUsers.get(position).gender);
                        ed.putString("birthdate", DATA.allSubUsers.get(position).dob);
                        //ed.putString("phone", user_info.getString("phone"));
                        ed.putString("image", DATA.allSubUsers.get(position).image);
                        ed.putString("occupation", DATA.allSubUsers.get(position).occupation);
                        ed.putString("marital_status", DATA.allSubUsers.get(position).marital_status);

                        ed.putBoolean("subUserSelected", true);
                        ed.commit();

                        String patientName = prefs.getString("first_name", "") + " " +prefs.getString("last_name", "");
                        customToast.showToast("Welcome: "+patientName, 0	, 0);

                        openActivity.open(Splash.class,true);
				/*if(position == 0) {

					SharedPreferences.Editor ed = prefs.edit();
					ed.putString("subPatientID","0");
					ed.putBoolean("subUserSelected", true);

					ed.putString("id", DATA.allSubUsers.get(position).id);

					ed.commit();
				}
				else {}*/
                    }else {
                        customToast.showToast("You can't login with this family member",0,1);
                    }
                }
            }
        });


        gloabalMethods.loadSubPatients();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Medical History");
        Button btnToolbar = (Button) findViewById(R.id.btnToolbar);
        btnToolbar.setText("Add Family");// Member
        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openActivity.open(AddFamilyMember.class, false);

				/*int mActivePosition = 0;
				lvSubUsersList.performItemClick(
						lvSubUsersList.getAdapter().getView(mActivePosition, null, null),
						mActivePosition,
						lvSubUsersList.getAdapter().getItemId(mActivePosition));*/
            }
        });

        //btnToolbar.setVisibility(View.GONE);
    }



    public void displaySubUsersList(String content){
        DATA.allSubUsers = new GloabalMethods(activity).parseSubUsers(content);
        usersAdapter = new SubUsersAdapter(activity);
        lvSubUsersList.setAdapter(usersAdapter);
    }


    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH) ||
                (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }



	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//return super.onCreateOptionsMenu(menu);
		return false;
	}*/
}