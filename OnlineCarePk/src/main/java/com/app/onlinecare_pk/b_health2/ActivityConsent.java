package com.app.onlinecare_pk.b_health2;

import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

import com.app.onlinecare_pk.BaseActivity;
import com.app.onlinecare_pk.R;
import com.app.onlinecare_pk.util.CustomAnimations;

public class ActivityConsent extends BaseActivity{


	CheckBox cbAcceptConsent;
	Button btnNext;

	public static int flagNav;//1 = immedite care , 2 = appointment
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consent_th);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Consent");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

		cbAcceptConsent = findViewById(R.id.cbAcceptConsent);
		btnNext = findViewById(R.id.btnNext);


		OnClickListener onClickListener = view -> {

			//dialog.dismiss();

			switch (view.getId()){
				case R.id.btnNext:
					if(cbAcceptConsent.isChecked()){
					    if(flagNav == 1){
                            //openActivity.open(ActivityIcWaitingRoom.class, true);
							//openActivity.open(ActivityImmeCareProviders.class, true);
							//openActivity.open(GetLiveCareFormBhealth.class, true);
							openActivity.open(ActivityImmeCareProviders.class, true);
                        }else if(flagNav == 2){
                            openActivity.open(ActivityApptmntProviders.class, true);
                        }

					}else {
						new CustomAnimations().shakeAnimate(cbAcceptConsent.getRootView(), cbAcceptConsent);
					}
					break;
				default:
					break;
			}
		};
		btnNext.setOnClickListener(onClickListener);

	}

}
