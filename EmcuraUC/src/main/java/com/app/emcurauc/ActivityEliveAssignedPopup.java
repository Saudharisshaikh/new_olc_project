package com.app.emcurauc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.app.emcurauc.util.OpenActivity;

public class ActivityEliveAssignedPopup extends Activity {

    Activity activity;
    OpenActivity openActivity;
    Button btnView;
    TextView tvMsgPopupMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_eliveassigned_popup);

        this.setFinishOnTouchOutside(false);

        String popupMsg = getIntent().getStringExtra("popupMsg") ;

        activity = ActivityEliveAssignedPopup.this;
        openActivity = new OpenActivity(activity);

        btnView = findViewById(R.id.btnViewDetails);
        tvMsgPopupMessage = findViewById(R.id.tvMsgPopupMessage);

        tvMsgPopupMessage.setText(popupMsg);

        btnView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //openActivity.open(ViewConversationActivity.class, true);
                Intent intent = new Intent(activity,GetLiveCare.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
