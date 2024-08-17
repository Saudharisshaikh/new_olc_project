package com.app.msu_cp.reliance.therapist;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.app.msu_cp.BaseActivity;
import com.app.msu_cp.R;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.util.DATA;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ActivityTherapyNoteList extends BaseActivity {



    ListView lvTherapyNotes;
    EditText etSearchNote;


    static boolean shoulRefresh = false;
    @Override
    protected void onResume() {
        if(shoulRefresh){
            shoulRefresh = false;

            loadTherapyNoteList();
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_therapynote_list);

        setSupportActionBar(findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Therapy Notes");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button btnToolbar = findViewById(R.id.btnToolbar);
        btnToolbar.setText("Add New");
        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ActivityTherapyNote.class, false);
            }
        });

        lvTherapyNotes = (ListView) findViewById(R.id.lvTherapyNotes);
        etSearchNote = findViewById(R.id.etSearchNote);

        etSearchNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(therapyNoteAdapter != null){
                    therapyNoteAdapter.filter(s.toString());
                }
            }
        });


        loadTherapyNoteList();
    }


    private void loadTherapyNoteList(){
        RequestParams params = new RequestParams();
        params.put("patient_id", DATA.selectedUserCallId);
        ApiManager apiManager = new ApiManager(ApiManager.BHEALTH_GET_THERAPY_NOTES,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<TherapyNoteBean> therapyNoteBeans;
    TherapyNoteAdapter therapyNoteAdapter;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.BHEALTH_GET_THERAPY_NOTES)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                if(data.length() == 0){
                    findViewById(R.id.tvNoData).setVisibility(View.VISIBLE);
                }else {
                    findViewById(R.id.tvNoData).setVisibility(View.GONE);
                }

                therapyNoteBeans = gson.fromJson(data.toString(), new TypeToken<ArrayList<TherapyNoteBean>>() {}.getType());
                if(therapyNoteBeans != null){
                    therapyNoteAdapter = new TherapyNoteAdapter(activity,therapyNoteBeans);
                    lvTherapyNotes.setAdapter(therapyNoteAdapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }
}
