package com.app.emcuradr;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.emcuradr.adapter.ExistingPatientAdapter;
import com.app.emcuradr.adapter.TelemedicineAdapter;
import com.app.emcuradr.adapter.TelemedicineAdapter2;
import com.app.emcuradr.api.ApiCallBack;
import com.app.emcuradr.api.ApiManager;
import com.app.emcuradr.api.Dialog_CustomProgress;
import com.app.emcuradr.model.AllScriptPatient;
import com.app.emcuradr.model.ExistingPatient;
import com.app.emcuradr.model.TelemedicineCatData;
import com.app.emcuradr.model.TelemedicineCategories;
import com.app.emcuradr.model.TimeDiff;
import com.app.emcuradr.util.CustomToast;
import com.app.emcuradr.util.DATA;
import com.app.emcuradr.util.Database;
import com.app.emcuradr.util.DialogAddEncNoteFromCallHist;
import com.app.emcuradr.util.DialogCallDetails;
import com.app.emcuradr.util.ExistingPatientCall;
import com.app.emcuradr.util.GeneralAlertDialog;
import com.app.emcuradr.util.SharedPrefsHelper;
import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ActivityConsultionNotes extends BaseActivity {

    Activity activity;

    public static String use_for_billing = "Yes";

    public static String call_id_instant_connect;
    TextView activityName;

    Dialog dialogNote;
    Dialog dialogTelemedicineSer;

    TextView patName,tvSaveNote,tvShowPreviousNotes;
    StringBuilder sbSelectedTMSCodes, sbSelectedTMSCodesWithNames;

    TextView scriptPatName,scriptDob,scriptGender,scriptMobile,scriptHome,scriptEmail,scriptAddress;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private boolean isListening = false;
    private TextView textView;
    private Button buttonRecording;

    private EditText etSpeechText;

    private String finalText = "";  // Holds confirmed text

    private int count = 0;
    private String partialText = "";

    CardView card1,card2;

    String disclaimer;

    FloatingGroupExpandableListView lvTelemedicineData;

    ListView lvTelemed2;
    Dialog_CustomProgress customProgressDialog;

    Handler handler;
    Runnable runnable;
    EditText etEncountNotes;
    TextView tvElivCatName;
    public static boolean autoNoteStatus = false;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION_CODE = 1;

    ImageView activeImg;
    TextView name,email,phone;

    WebView webView;
    boolean fromChatGPT = false;

    String gptFinalQuery;

    LinearLayout patLayout;

    ImageView getBackImage;
    ImageView ivCloseAi;
    ArrayList<TelemedicineCategories> telemedicineCategories;

    String tmsCodes, tmsCodesWithNames;
    ExistingPatient searchPatient;
    String patientType;
    AllScriptPatient allScriptPatient;
    private Handler noStartHandler;
    private Runnable showDialogRunnable;
    private boolean isDialogShown = false;

    private Handler noStopHandler;
    private Runnable showStopDialogRunnable;
    private boolean isStopDialogShown = false;

    String startTime = "";
    String endTime = "";


    final String API_SECRET_KEY = SharedPrefsHelper.getInstance().get("gpt_key","");
    private void requestAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION_CODE);
        } else {
            // Permission already granted
            initializeSpeechRecognizer();
        }
    }






    private void initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            textView.setText("Speech Recognition is not available on this device.");
            etSpeechText.setText("Speech Recognition is not available on this device.");
            return;
        }

        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                // textView.setHint("Listening...");
            }

            @Override
            public void onBeginningOfSpeech() {
                partialText = "";
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // You can use this to display a visualizer if needed
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // Not used
            }

            @Override
            public void onEndOfSpeech() {
                //  textView.setHint("Processing...");
            }

            @Override
            public void onError(int error) {
                handleError(error);
            }

            @Override
            public void onResults(Bundle results) {
                handleResults(results);
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                handlePartialResults(partialResults);
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // Not used
            }
        });
    }


    private void handlePartialResults(Bundle partialResults) {
        ArrayList<String> partials = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (partials != null && !partials.isEmpty()) {
            partialText = partials.get(0);
            textView.setText(finalText + " " + partialText);
            etSpeechText.setText(finalText+" "+partialText);
        }
    }

    private void handleResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches != null && !matches.isEmpty()) {
            finalText += " " + matches.get(0);
            textView.setText(finalText);
            etSpeechText.setText("");
            partialText = "";
        }
        if (isListening) {
            speechRecognizer.startListening(speechRecognizerIntent);
        }
    }



    private void showNoStartDialog(){
        isDialogShown = true;

        GeneralAlertDialog.callAlert(ActivityConsultionNotes.this,"Your conversation is not recorded. Please press start recording button in order to record your conversation.",()->{
            buttonRecording.performClick();
        },()->{

        },"Start Recording","Not Now","Alert");
    }


    private void showStopDialog(){
        isStopDialogShown = true;

        GeneralAlertDialog.callAlert(ActivityConsultionNotes.this,"Are you still with the patient? Please note that your conversation is still recording.",()->{
            buttonRecording.performClick();
        },()->{

        },"Stop Recording","Not Now","Alert");
    }



    private void handleError(int error) {
        String errorMessage = getErrorText(error);
        textView.setText(errorMessage);
        etSpeechText.setText(errorMessage);
        if (isListening) {
            speechRecognizer.cancel();
            speechRecognizer.startListening(speechRecognizerIntent);
        }
    }

    public static String getErrorText(int errorCode) {
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                return "Audio recording error";
            case SpeechRecognizer.ERROR_CLIENT:
                return "Client error";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                return "Insufficient permissions";
            case SpeechRecognizer.ERROR_NETWORK:
                return "Network error";
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                return "Network timeout";
            case SpeechRecognizer.ERROR_NO_MATCH:
                return ""; // No match found, continue listening
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                return "Recognition service is busy";
            case SpeechRecognizer.ERROR_SERVER:
                return "Server error";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                return ""; // No speech input, continue listening
            default:
                return "Unknown error";
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_consultation_note);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        searchPatient = getIntent().getParcelableExtra("search_patient");
        allScriptPatient = getIntent().getParcelableExtra("allscript_patient");
        patientType = getIntent().getStringExtra("patient_type");
        name = findViewById(R.id.tvName2);
        email = findViewById(R.id.tvEmail);
        phone = findViewById(R.id.tvPhone);
        activeImg = findViewById(R.id.ivIsonline);
        etSpeechText = findViewById(R.id.etSpeechText);
        webView = findViewById(R.id.webview);
        webView.loadUrl("file:///android_asset/mic.gif");
        patLayout = findViewById(R.id.pat_layout);
        customProgressDialog = new Dialog_CustomProgress(this);
        getBackImage = findViewById(R.id.ivBack);

        card1 = findViewById(R.id.pat_card);
        card2 = findViewById(R.id.pat_card2);
        scriptPatName = findViewById(R.id.tvName);
        scriptDob = findViewById(R.id.tvdob);
        scriptGender = findViewById(R.id.tvGender);
        scriptMobile = findViewById(R.id.tvMobile);
        scriptHome = findViewById(R.id.tvHome);
        scriptEmail = findViewById(R.id.tvEmail2);
        scriptAddress = findViewById(R.id.tvAddress);



        disclaimer = sharedPrefsHelper.get("disclaimer");
        getBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
        patLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Toast.makeText(ActivityConsultionNotes.this,""+count,Toast.LENGTH_LONG).show();
                count++;
                if (count > 0) {
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            count = 0;

                        }
                    };
                    if (handler != null) {
                        handler.removeCallbacks(runnable);
                    }
                    handler = new Handler();
                    handler.postDelayed(runnable, 2000);  // clear counter if user does not touch for one sec
                }

                DATA.print("-- Counter : " + count);

                if (count == 5) {

                    textView.setVisibility(View.VISIBLE);
                }
            }

        });



       if(patientType.equals("search_patient")){
           card1.setVisibility(View.VISIBLE);
           card2.setVisibility(View.GONE);
           if(searchPatient != null){
               name.setText(searchPatient.firstName+" "+searchPatient.lastName);
               email.setText(searchPatient.email);
               phone.setText(searchPatient.phone);
               if(searchPatient.is_online.equals("1")){
                   activeImg.setImageResource(R.drawable.icon_online);
               }
               else {
                   activeImg.setImageResource(R.drawable.icon_notification);
               }

           }
           else {
               name.setText("-");
               email.setText("-");
               phone.setText("-");
               activeImg.setImageResource(R.drawable.icon_notification);
           }
       }
       else {
            card1.setVisibility(View.GONE);
            card2.setVisibility(View.VISIBLE);
           if(allScriptPatient != null){
               scriptPatName.setText(allScriptPatient.firstName+" "+allScriptPatient.lastName);
               scriptEmail.setText("Email: "+allScriptPatient.emailAddress);
               scriptMobile.setText("Mobile: "+allScriptPatient.cellPhone);
               scriptHome.setText("Home: "+allScriptPatient.homePhone);
               scriptAddress.setText("Address: "+allScriptPatient.addressLine1);
               scriptGender.setText("Gender: "+allScriptPatient.gender);
               scriptDob.setText("DOB: "+allScriptPatient.dateOfBirth);


           }
           else {
               scriptPatName.setText("-");
               scriptEmail.setText("-");
               scriptMobile.setText("-");
               scriptHome.setText("-");
               scriptAddress.setText("-");
               scriptGender.setText("-");
               scriptDob.setText("-");

           }
       }

        noStartHandler = new Handler();
        showDialogRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isDialogShown) {
                    showNoStartDialog();
                }
            }
        };

        // Post the Runnable to be executed after 1 minute (60000 milliseconds)
        noStartHandler.postDelayed(showDialogRunnable, 60000);


        textView = findViewById(R.id.textView);
        buttonRecording = findViewById(R.id.btnRecording);
        requestAudioPermission();


        buttonRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isListening) {
                    endTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
                    noStopHandler.removeCallbacks(showStopDialogRunnable);
                    speechRecognizer.stopListening();

                    Log.d("--text", "onClick: "+finalText);
                    textView.setText(finalText);
                    etSpeechText.setText(finalText);

                    GeneralAlertDialog.callAlert(ActivityConsultionNotes.this,disclaimer,()->{
                       callChatGPT(finalText);
                    },()->{

                    },"OK","Not Now","Disclaimer");

                } else {
                    startTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
                    noStartHandler.removeCallbacks(showDialogRunnable);
                    configStopHandler();
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                isListening = !isListening;
                buttonRecording.setText(isListening ? "Stop Listening" : "Start Listening");
            }
        });






    }



   public void configStopHandler(){

       noStopHandler = new Handler();
       showStopDialogRunnable = new Runnable() {
           @Override
           public void run() {
               if (!isStopDialogShown) {
                   showStopDialog();
               }
           }
       };

       // Post the Runnable to be executed after 1 minute (60000 milliseconds)
       noStopHandler.postDelayed(showStopDialogRunnable, 60000);
   }


    public void callChatGPT(String query){

        query +="\n\n";
        query+="create medical notes  of above conversation with patient name Sample-Patient";
        DATA.print("quers:"+query);

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject params = new JSONObject();
        try {
            params.put("model", "gpt-3.5-turbo-instruct");
            params.put("prompt", query);
            params.put("temperature", 0);
            params.put("max_tokens", 100);
            params.put("top_p", 1);
            params.put("frequency_penalty", 0.0);
            params.put("presence_penalty", 0.0);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        DATA.print("-- query " + query);

        StringEntity entity = new StringEntity(params.toString(), "UTF-8");

        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", "Bearer " + API_SECRET_KEY);

        client.post(getApplicationContext(), "https://api.openai.com/v1/completions", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String text = response.getJSONArray("choices").getJSONObject(0).getString("text");
                    DATA.print("-- GPT RESPONSE " + text);
                    // Remove first word before "\n"
                    String clearedResponse = text.replaceFirst("^[^\\n]*\\n", "");
                    // Remove both instances of "\n"
                    clearedResponse = clearedResponse.replaceAll("\\n", "");
                    DATA.print("-- cleanedResponse frm chatgpt : " + clearedResponse);
                    DATA.print("-- response frm chatgpt : " + response);

                    StringBuilder sb = new StringBuilder();
                    sb.append("Patient Name : ").append(patientType.equals("search_patient")?searchPatient.firstName+" "+searchPatient.lastName:allScriptPatient.firstName+" "+allScriptPatient.lastName).append(" ")
                            .append("\nDOB : ").append(patientType.equals("search_patient")?searchPatient.birthdate:allScriptPatient.dateOfBirth)
                            .append("\n").append(clearedResponse);
                    customProgressDialog.dismissProgressDialog();
                    DATA.print("finalNote:"+sb.toString());

                    gptFinalQuery = sb.toString();
                    Intent intent = new Intent(ActivityConsultionNotes.this,ActivityNotesProcessed.class);
                    intent.putExtra("gptFinalQuery",gptFinalQuery);
                    intent.putExtra("patient_type",patientType);
                    intent.putExtra("start_time",startTime);
                    intent.putExtra("end_time",endTime);
                    if(patientType.equals("search_patient")){
                        intent.putExtra("patient_data",searchPatient);

                    }
                    else {
                        intent.putExtra("patient_data",allScriptPatient);
                    }
                   startActivity(intent);
                   //showTelemedicineDialog();



                    //openActivity.open(ActivityTelemedicineServices.class, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //Log.e("GPT Error", errorResponse.toString());

                customProgressDialog.dismissProgressDialog();

            }
        });

    }



    // no use
    public void showTelemedicineDialog() {
        dialogTelemedicineSer = new Dialog(ActivityConsultionNotes.this, R.style.TransparentThemeH4B);
        dialogTelemedicineSer.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        //dialogAddTemplPresc.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogTelemedicineSer.setContentView(R.layout.dialog_telemedicine_services);

        FloatingGroupExpandableListView lvTelemedicineData = (FloatingGroupExpandableListView) dialogTelemedicineSer.findViewById(R.id.lvTelemedicineData);
        Button btnDone = (Button) dialogTelemedicineSer.findViewById(R.id.btnDone);

        Button btnAddToFav = (Button) dialogTelemedicineSer.findViewById(R.id.btnAddToFav);
        Button btnRemoveFav = (Button) dialogTelemedicineSer.findViewById(R.id.btnRemoveFav);

        ListView lvTelemed2 = dialogTelemedicineSer.findViewById(R.id.lvTelemed2);
        CheckBox cbToggleExpList = dialogTelemedicineSer.findViewById(R.id.cbToggleExpList);
        TextView tvEliveSessionTime = dialogTelemedicineSer.findViewById(R.id.tvEliveSessionTime);
        tvElivCatName = dialogTelemedicineSer.findViewById(R.id.tvElivCatName);

        Button btnUseForBillingYes = dialogTelemedicineSer.findViewById(R.id.btnUseForBillingYes);
        Button btnUseForBillingNo = dialogTelemedicineSer.findViewById(R.id.btnUseForBillingNo);
        RelativeLayout layUseForBilling = dialogTelemedicineSer.findViewById(R.id.layUseForBilling);

        lvTelemed2.setVisibility(View.VISIBLE);
        cbToggleExpList.setVisibility(View.VISIBLE);
        tvEliveSessionTime.setVisibility(View.VISIBLE);
        tvElivCatName.setVisibility(View.VISIBLE);


        DATA.elivecare_end_time = new SimpleDateFormat("HH:mm:ss").format(new Date());

        try {
            Date elivecareStartTime = new SimpleDateFormat("HH:mm:ss").parse(DATA.elivecare_start_time);
            Date elivecareEndTime = new SimpleDateFormat("HH:mm:ss").parse(DATA.elivecare_end_time);

            TimeDiff timeDiff = getTimeDiff(elivecareStartTime, elivecareEndTime);

            long mm = timeDiff.diffMinutes, ss = timeDiff.diffSeconds, hh = timeDiff.diffHours;
            StringBuilder sb = new StringBuilder();

            StringBuilder sb2 = new StringBuilder();

            if (hh < 10) {
                sb.append("0" + String.valueOf(hh));
                if (hh > 0) {
                    sb2.append("0" + String.valueOf(hh) + " hr, ");
                }
            } else {
                sb.append(String.valueOf(hh));
                if (hh > 0) {
                    sb2.append(String.valueOf(hh) + " hr, ");
                }
            }
            sb.append(":");

            if (mm < 10) {
                sb.append("0" + String.valueOf(mm));
                sb2.append("0" + String.valueOf(mm) + " min, ");
            } else {
                sb.append(String.valueOf(mm));
                sb2.append(String.valueOf(mm) + " min, ");
            }
            sb.append(":");
            if (ss < 10) {
                sb.append("0" + String.valueOf(ss));
                sb2.append("0" + String.valueOf(ss) + " sec");
            } else {
                sb.append(String.valueOf(ss));
                sb2.append(String.valueOf(ss) + " sec");
            }

            tvEliveSessionTime.setText("Session Time : " + sb2.toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                if (view.getId() == R.id.btnDone) {
                    doneProceed();
                } else if (view.getId() == R.id.btnAddToFav) {
                    addToFav();
                } else if (view.getId() == R.id.btnRemoveFav) {
                    removeFav();
                } else if (view.getId() == R.id.btnUseForBillingYes) {
                    layUseForBilling.setVisibility(View.GONE);
                    use_for_billing = "Yes";
                } else if (view.getId() == R.id.btnUseForBillingNo) {
                    use_for_billing = "No";
                    tmsCodes = "";
                    tmsCodesWithNames = "";
                    DATA.print("-- selected tms codes: " + tmsCodes + "-- selected tmsCodesWithNames: " + tmsCodesWithNames);

                    if (dialogTelemedicineSer != null) {
                        sharedPrefsHelper.save("encounter_notesDurringCall", "");
//                        btnAfterCallEndConv.setVisibility(View.VISIBLE);
//                        dialogBoxAiResultFinal.setVisibility(View.GONE);
//                        layEndConvOptions.setVisibility(View.GONE);
//                        dialogBoxAiResultFinal.setVisibility(View.GONE);
                        dialogTelemedicineSer.dismiss();
                    }
                    //Ahmer code
                    if (fromChatGPT) {
                        fromChatGPT = false;
                        billWithoutNote(tmsCodes, gptFinalQuery);
                        //btnAfterCallEndConv.setVisibility(View.GONE);
                        //dialogBoxAiResultFinal.setVisibility(View.VISIBLE);
                    } else {
                          initAiNoteDialog();
                        //initNoteDialog();
                    }
                }
            }
        };
        btnDone.setOnClickListener(onClickListener);
        btnAddToFav.setOnClickListener(onClickListener);
        btnRemoveFav.setOnClickListener(onClickListener);

        btnUseForBillingYes.setOnClickListener(onClickListener);
        btnUseForBillingNo.setOnClickListener(onClickListener);

        cbToggleExpList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lvTelemed2.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            }
        });

        ImageView ivClose = dialogTelemedicineSer.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(v -> {
            dialogTelemedicineSer.dismiss();
           // dialogBoxAiResultFinal.setVisibility(View.GONE);
        });


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogTelemedicineSer.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogTelemedicineSer.setCanceledOnTouchOutside(false);
        dialogTelemedicineSer.show();
        dialogTelemedicineSer.getWindow().setAttributes(lp);

        //dialogForDismiss = dialogAddTemplPresc;

        this.lvTelemedicineData = lvTelemedicineData;
        this.lvTelemed2 = lvTelemed2;

       getTelemedicineServices();
    }

// no use

    public void getTelemedicineServices() {

        String checheData = sharedPrefsHelper.get(ActivityTelemedicineServices.TELEMEDICINE_PREFS_KEY, "");
        if (!TextUtils.isEmpty(checheData)) {
            parseTelemedicineData(checheData);
            ApiManager.shouldShowPD = false;
        }

        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("livecare_id", "");
        ApiManager apiManager = new ApiManager(ApiManager.GET_TELEMEDICINE_SERVICES, "post", params, apiCallBack, ActivityConsultionNotes.this);
        apiManager.loadURL();
    }



    public void parseTelemedicineData(String content) {
        telemedicineCategories = new ArrayList<>();
        TelemedicineCategories temp;

        try {
            JSONArray jsonArray = new JSONArray(content);

            for (int i = 0; i < jsonArray.length(); i++) {

                ArrayList<TelemedicineCatData> catData = new ArrayList<>();
                TelemedicineCatData telemedicineCatDataTEMP;

                String category_name = jsonArray.getJSONObject(i).getString("category_name");
                JSONArray data = jsonArray.getJSONObject(i).getJSONArray("data");

                for (int j = 0; j < data.length(); j++) {
                    String category_name1 = data.getJSONObject(j).getString("category_name");
                    String hcpcs_code = data.getJSONObject(j).getString("hcpcs_code");
                    String service_name = data.getJSONObject(j).getString("service_name");
                    String category_id = data.getJSONObject(j).getString("category_id");
                    String non_fac_fee = data.getJSONObject(j).getString("non_fac_fee");
                    String service_id = data.getJSONObject(j).getString("service_id");

                    telemedicineCatDataTEMP = new TelemedicineCatData(category_name1, hcpcs_code, service_name, category_id, non_fac_fee, false, service_id);
                    catData.add(telemedicineCatDataTEMP);
                    telemedicineCatDataTEMP = null;
                }

                temp = new TelemedicineCategories(category_name, catData);
                telemedicineCategories.add(temp);

                if (category_name.equalsIgnoreCase("Most Common") && lvTelemed2 != null) {
                    lvTelemed2.setAdapter(new TelemedicineAdapter2(ActivityConsultionNotes.this, catData));
                    tvElivCatName.setText(category_name);
                }

            }

            TelemedicineAdapter adapter = new TelemedicineAdapter(ActivityConsultionNotes.this, telemedicineCategories);
            WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(adapter);
            if (lvTelemedicineData != null) {
                lvTelemedicineData.setAdapter(wrapperAdapter);
            }

            sharedPrefsHelper.save(ActivityTelemedicineServices.TELEMEDICINE_PREFS_KEY, content);

        } catch (JSONException e) {
            DATA.print("js:"+e.getMessage());
            // TODO Auto-generated catch block
            customToast.showToast(DATA.JSON_ERROR_MSG, 0, 1);
            e.printStackTrace();
        }

    }


    public void addToFav() {
        boolean isServicesSelected = false;
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));

        if (telemedicineCategories != null) {
            for (int i = 0; i < telemedicineCategories.size(); i++) {
                for (int j = 0; j < telemedicineCategories.get(i).telemedicineCatDatas.size(); j++) {

                    if (telemedicineCategories.get(i).telemedicineCatDatas.get(j).isSelected) {
                        params.put("services[" + (i + j) + "]", telemedicineCategories.get(i).telemedicineCatDatas.get(j).service_id);
                        isServicesSelected = true;
                    }
                }
            }
            if (isServicesSelected) {
                ApiManager apiManager = new ApiManager(ApiManager.SAVE_FAVOURITE_SERVICES, "post", params, apiCallBack, ActivityConsultionNotes.this);
                apiManager.loadURL();
            } else {
                customToast.showToast("Please select telemedicine services", 0, 0);
            }
        } else {
            DATA.print("-- telemedicineCategories list is null !");
        }
    }

    public void removeFav() {
        boolean isServicesSelected = false;
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));

        if (telemedicineCategories != null) {
            for (int i = 0; i < telemedicineCategories.size(); i++) {
                for (int j = 0; j < telemedicineCategories.get(i).telemedicineCatDatas.size(); j++) {

                    if (telemedicineCategories.get(i).telemedicineCatDatas.get(j).isSelected) {
                        params.put("services[" + (i + j) + "]", telemedicineCategories.get(i).telemedicineCatDatas.get(j).service_id);
                        isServicesSelected = true;
                    }
                }
            }
            if (isServicesSelected) {
                ApiManager apiManager = new ApiManager(ApiManager.REMOVE_FAVOURITE_SERVICES, "post", params, apiCallBack, ActivityConsultionNotes.this);
                apiManager.loadURL();
            } else {
                customToast.showToast("Please select telemedicine services", 0, 0);
            }
        } else {
            DATA.print("-- telemedicineCategories list is null !");
        }
    }




    public TimeDiff getTimeDiff(Date startTime, Date endTime) {
        // d1, d2 are dates
        long diff = endTime.getTime() - startTime.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        /*DATA.print("-- Time diffrence");
        System.out.print("-- "+diffDays + " days, ");
        System.out.print(diffHours + " hours, ");
        System.out.print(diffMinutes + " minutes, ");
        System.out.print(diffSeconds + " seconds.");*/

        return new TimeDiff(diffSeconds, diffMinutes, diffHours, diffDays);
    }


    //Ahmer code for chatGPT auto Note

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {

// no use

  if (apiName.equalsIgnoreCase(ApiManager.BILL_WITHOUT_NOTE)) {
            //{"success":1,"message":"Saved.","note_id":1878}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.has("success")) {
                    sharedPrefsHelper.save("encounter_notesDurringCall", "");
                       customToast.showToast("Auto Encounter Notes submitted successfully", 0, 0);
                       finish();

                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);

            }
        }

  // no
  else if (apiName.equalsIgnoreCase(ApiManager.GET_TELEMEDICINE_SERVICES)) {

      parseTelemedicineData(content);

  }




    }

//  no use
    public void doneProceed() {

        if (telemedicineCategories != null) {
            sbSelectedTMSCodes = new StringBuilder();
            sbSelectedTMSCodesWithNames = new StringBuilder();
            for (int i = 0; i < telemedicineCategories.size(); i++) {
                for (int j = 0; j < telemedicineCategories.get(i).telemedicineCatDatas.size(); j++) {

                    if (telemedicineCategories.get(i).telemedicineCatDatas.get(j).isSelected) {
                        sbSelectedTMSCodes.append(telemedicineCategories.get(i).telemedicineCatDatas.get(j).hcpcs_code);
                        sbSelectedTMSCodes.append(",");

                        sbSelectedTMSCodesWithNames.append(telemedicineCategories.get(i).telemedicineCatDatas.get(j).hcpcs_code
                                + " - " + telemedicineCategories.get(i).telemedicineCatDatas.get(j).service_name);
                        sbSelectedTMSCodesWithNames.append(",");
                        sbSelectedTMSCodesWithNames.append("\n");
                    }
                }
            }

            tmsCodes = sbSelectedTMSCodes.toString();
            tmsCodesWithNames = sbSelectedTMSCodesWithNames.toString();
            if (tmsCodes.isEmpty()) {
                //customToast.showToast("Please select telemedicine services", 0, 1);
                new AlertDialog.Builder(ActivityConsultionNotes.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Confirm").
                        setMessage("You do not selected any service. Do you want to skip ?")
                        .setPositiveButton("Skip", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tmsCodes = "";
                                tmsCodesWithNames = "";
                                DATA.print("-- selected tms codes: " + tmsCodes + "-- selected tmsCodesWithNames: " + tmsCodesWithNames);

                                if (dialogTelemedicineSer != null) {
                                    sharedPrefsHelper.save("encounter_notesDurringCall", "");
                                    //dialogBoxAiResultFinal.setVisibility(View.GONE);
                                    dialogTelemedicineSer.dismiss();
                                }
                                if (fromChatGPT) {
                                    fromChatGPT = false;
                                    billWithoutNote(tmsCodes, gptFinalQuery);


                                } else {
                                    initAiNoteDialog();
                                    //initNoteDialog();
                                }

                            }
                        }).setNegativeButton("No", null).create().show();
            } else {
                tmsCodes = tmsCodes.substring(0, tmsCodes.length() - 1);
                tmsCodesWithNames = tmsCodesWithNames.substring(0, tmsCodesWithNames.length() - 1);
                DATA.print("-- selected tms codes: " + tmsCodes + "-- selected tmsCodesWithNames: " + tmsCodesWithNames);

                if (dialogTelemedicineSer != null) {

                    dialogTelemedicineSer.dismiss();
                }
                if (fromChatGPT) {
                    fromChatGPT = false;

                    billWithoutNote(tmsCodes, gptFinalQuery);
                } else {
                    initAiNoteDialog();
                   // initNoteDialog();
                }


            }

        } else {
            DATA.print("-- telemedicineCategories list is null !");
        }

    }

    // no use

    public void billWithoutNote(String tmsCodes, String note_text) {
        RequestParams params = new RequestParams();
        String patientId = patientType.equals("search_patient")?searchPatient.id:allScriptPatient.ID;
        DATA.print("--pates"+patientId);
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("patient_id", patientId);
        params.put("treatment_codes", tmsCodes);
        params.put("start_time", DATA.elivecare_start_time);
        params.put("note_text", note_text);
        params.put("use_for_billing", use_for_billing);//(Yes,No)


        String callId = "0";

        params.put("call_id", callId);
        //GM added call_id

        ApiManager apiManager = new ApiManager(ApiManager.BILL_WITHOUT_NOTE, "post", params, apiCallBack, ActivityConsultionNotes.this);
        apiManager.loadURL();
    }


    @Override
    protected void onStop() {
        super.onStop();
        speechRecognizer.stopListening();
        speechRecognizer.destroy();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.stopListening();
        speechRecognizer.destroy();
        noStartHandler.removeCallbacks(showDialogRunnable);
        noStopHandler.removeCallbacks(showStopDialogRunnable);
    }



// no use
    public void initAiNoteDialog() {
        dialogNote = new Dialog(ActivityConsultionNotes.this);
        dialogNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogNote.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogNote.getWindow().setGravity(Gravity.TOP);
        //dialogNote.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogNote.setContentView(R.layout.dialog_recorded_notes);
        dialogNote.setCanceledOnTouchOutside(false);
        dialogNote.setCancelable(false);

        TextView tvNotePatientName = dialogNote.findViewById(R.id.tvNotePatientName);

        etEncountNotes = dialogNote.findViewById(R.id.etNotes);
        ivCloseAi = dialogNote.findViewById(R.id.ivClose);
        patName = dialogNote.findViewById(R.id.tvName);
        tvSaveNote = dialogNote.findViewById(R.id.tvsavenotes);
        tvShowPreviousNotes = dialogNote.findViewById(R.id.tvshowprevious);

        patName.setText(patientType.equals("search_patient")?searchPatient.firstName+" "+searchPatient.lastName:allScriptPatient.firstName+" "+allScriptPatient.lastName);
        etEncountNotes.setText(gptFinalQuery);
        ivCloseAi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogNote.dismiss();
            }
        });
        tvSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogNote.dismiss();
                billWithoutNote(tmsCodes, gptFinalQuery);
            }
        });

        //Ahmer's code for AI




        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogNote.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

         dialogNote.show();

        dialogNote.getWindow().setAttributes(lp);
    }

}