package com.app.fivestardoc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.app.fivestardoc.util.DATA;
import com.app.fivestardoc.util.OpenActivity;

import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class ShowCancelPopup extends Activity {

    OpenActivity openActivity;
    TextView tvCancelMessage;
    Button btnNo, btncallFup;

    String msg,patient_id,pImage,pName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show_cancel_popup);

        openActivity = new OpenActivity(ShowCancelPopup.this);
        tvCancelMessage = (TextView) findViewById(R.id.tvCancelMessage);

        btnNo = (Button) findViewById(R.id.btnNo);
        btncallFup = (Button) findViewById(R.id.btncallFup);

        msg = getIntent().getStringExtra("msg");
        patient_id = getIntent().getStringExtra("pID");
        pName = getIntent().getStringExtra("pName");
        pImage = getIntent().getStringExtra("pImage");

        tvCancelMessage.setText(msg);

        btncallFup.setOnClickListener(v -> {
            Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            DATA.selectedUserCallId = patient_id;
            DATA.isFromDocToDoc = false;
            DATA.selectedUserCallName = pName;
            DATA.selectedUserCallImage = pImage;
            startActivity(myIntent);
            finish();
        });

        btnNo.setOnClickListener(arg0 -> finish());
    }
}