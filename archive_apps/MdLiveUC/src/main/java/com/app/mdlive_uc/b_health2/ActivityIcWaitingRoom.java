package com.app.mdlive_uc.b_health2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.app.mdlive_uc.BaseActivity;
import com.app.mdlive_uc.R;
import com.app.mdlive_uc.api.ApiManager;
import com.app.mdlive_uc.util.DATA;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class ActivityIcWaitingRoom extends BaseActivity{


	TextView tvWaitingMsg,tvLiveCareTicker;
	Button btnPaperwork, btnExitWaitingRoom;

	//boolean isAlreadyInRoom;

    Typeface timerfonts;

    public static final String ISIN_ICROOM_PREFS_KEY = "isAlreadyInICroom";

    boolean isFromDocList;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ic_waitingroom);

        isFromDocList = getIntent().getBooleanExtra("isFromDocList", false);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("IC Waiting Room");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        timerfonts = Typeface.createFromAsset(getAssets(),"digital-7.ttf");

        tvWaitingMsg = findViewById(R.id.tvWaitingMsg);
		btnPaperwork = findViewById(R.id.btnPaperwork);
		btnExitWaitingRoom = findViewById(R.id.btnExitWaitingRoom);
        tvLiveCareTicker = findViewById(R.id.tvLiveCareTicker);


		OnClickListener onClickListener = view -> {

			//dialog.dismiss();

			switch (view.getId()){
				case R.id.btnPaperwork:
					customToast.showToast("Comming Soon",0,0);
					break;
				case R.id.btnExitWaitingRoom:
					AlertDialog alertDialog =
							new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Confirm")
									.setMessage("Are you sure ? Do you want to exit waiting room ?")
									.setPositiveButton("Yes Exit",new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											exitRoom();
										}
									})
									.setNegativeButton("Not Now", null)
									.create();
					alertDialog.show();
					break;
				default:
					break;
			}
		};
		btnPaperwork.setOnClickListener(onClickListener);
		btnExitWaitingRoom.setOnClickListener(onClickListener);


		getWaitingPatients();
	}


	public void getImmediateCare(){
		RequestParams params = new RequestParams();
		params.put("patient_id", prefs.getString("id", ""));
		if(isFromDocList){
		    params.put("selected_doctor_id", ActivityDoctorSlots.doctorBean.id);
        }

		ApiManager apiManager = new ApiManager(ApiManager.BHEALTH_IMMCARE,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	public void exitRoom(){
		RequestParams params = new RequestParams();
		params.put("patient_id", prefs.getString("id", ""));

		ApiManager apiManager = new ApiManager(ApiManager.BHEALTH_EXIT_ROOM,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	public void getWaitingPatients(){
		ApiManager apiManager = new ApiManager(ApiManager.BHEALTH_GET_WAITING_PATIENTS+prefs.getString("id", "") ,"post", null ,apiCallBack, activity);
		apiManager.loadURL();
	}



	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);

		if(apiName.contains(ApiManager.BHEALTH_GET_WAITING_PATIENTS)){
			//{"status":"error","message":"You are not in any immediate care"}
			//{"total_patient":"0","status":"success","your_number":1}

			try {
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.getString("status").equalsIgnoreCase("success")){
					//isAlreadyInRoom = true;
                    sharedPrefsHelper.save(ISIN_ICROOM_PREFS_KEY, true);
					int your_number = jsonObject.getInt("your_number");
					//String waitingMsg = "There are "+(your_number - 1)+" people ahead of you.\nYou are at number: " + (your_number);
					String waitingMsg = "There are "+your_number+" people ahead of you.";
                    tvWaitingMsg.setText(waitingMsg);

                    //start Timer
                    /*int avtTimeMillis =  (your_number * 600000);//300000 milis = 5 minutes, 600000 = 10 minutes
                    if(your_number >= 1) {
                        if (!isTimerRunning) {
                            countDownTimer(avtTimeMillis);
                        }
                        //tvLiveCareTicker.setText("Your approximate waiting time is: "+convrtTime(avtTimeMillis));
                    }else {
                        tvWaitingMsg.setText("Be ready, our doctor will contact you soon. Thanks.");//waitingPatients+
                        tvLiveCareTicker.setText("Your approximate waiting time is: "+convrtTime(avtTimeMillis));
                    }*/

				}else {
					//isAlreadyInRoom = false;
                    sharedPrefsHelper.save(ISIN_ICROOM_PREFS_KEY, false);
					getImmediateCare();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.BHEALTH_IMMCARE)){
			//{"status":"success","msg":"Added Successfully","id":1522}
			//{"status":"error","msg":"Your are already in queue","id":"1523","is_already":1}
			try {
				JSONObject jsonObject = new JSONObject(content);
				String status = jsonObject.getString("status");
				String msg = jsonObject.getString("msg");
				if(status.equalsIgnoreCase("success")){
					getWaitingPatients();
				}
				new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
						.setTitle(getResources().getString(R.string.app_name))
						.setMessage(msg)
						.setPositiveButton("Done",null)
						.create().show();

			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.BHEALTH_EXIT_ROOM)){
			//{"status":"success","message":"Exit successfully"}
			try {
				JSONObject jsonObject = new JSONObject(content);
				AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
						.setTitle(getResources().getString(R.string.app_name))
						.setMessage(jsonObject.optString("message"))
						.setPositiveButton("Done",null)
						.create();
				if(jsonObject.optString("status").equalsIgnoreCase("success")){

                    sharedPrefsHelper.save(ISIN_ICROOM_PREFS_KEY, false);

					alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							finish();
						}
					});
				}
				alertDialog.show();
			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}
	}




	CountDownTimer countDownTimer;
	private boolean isTimerRunning;
    public void countDownTimer(long milis) {
        if(countDownTimer != null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
        isTimerRunning = true;
        tvLiveCareTicker.setTypeface(timerfonts);
        tvLiveCareTicker.setTextSize(30.0f);
        countDownTimer = new CountDownTimer(milis, 1000) {

            public void onTick(long millisUntilFinished) {
                //isTimerRunning = true;
                //tvLiveCareTicker.setText("seconds remaining: " + millisUntilFinished / 1000);
                //tvLiveCareTicker.setTypeface(timerfonts);
                //tvLiveCareTicker.setTextSize(30.0f);
                tvLiveCareTicker.setText(convrtTime(millisUntilFinished));//"Time remaining: " +
                //here you can have your logic to set text to edittext

                System.out.println("-- contdowntimer is running");
            }

            public void onFinish() {
                isTimerRunning = false;
                //tvLiveCareTicker.setTypeface(Typeface.DEFAULT);
                //tvLiveCareTicker.setTextSize(18.0f);
                //tvLiveCareTicker.setText("Its your trun. Be ready for live video checkup.");

                try {
                    new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage("Sorry, it has been longer then the anticipated wait time. Office Manager has been informed and the provider will be with your shortly. Thank you for your patience.")
                            .setPositiveButton("Done",null)
                            .create().show();//causes nullpointer when activity not running

                    //timerAlert();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        };
        countDownTimer.start();
    }

    private String convrtTime(long millis) {


//		long days = TimeUnit.MILLISECONDS.toDays(millis);
//        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        millis -= TimeUnit.SECONDS.toMillis(seconds);

        //System.out.println("--online care waiting time millis: "+millis + "hrs: "+hours+" mins: "+minutes);

//		return hours+" hours, "+minutes+" minutes";

        String h = hours < 10 ? "0"+hours : ""+hours;
        String m = minutes < 10 ? "0"+minutes : ""+minutes;
        String s = seconds < 10 ? "0"+seconds : ""+seconds;
        return h+":"+m+":"+s;
    }


    @Override
    protected void onDestroy() {

        /*if(timerAPIcall != null){
            timerAPIcall.cancel();
        }*/
        if(countDownTimer != null){
            countDownTimer.cancel();
        }

        //DATA.shouldLiveCareWatingRefresh = 1;

        System.out.println("-- ActivityIcWaitingRoom Activity onDestroy called");

        super.onDestroy();
    }
}
