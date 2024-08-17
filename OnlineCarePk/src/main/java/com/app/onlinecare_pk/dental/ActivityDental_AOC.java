package com.app.onlinecare_pk.dental;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.app.onlinecare_pk.BaseActivity;
import com.app.onlinecare_pk.R;

public class ActivityDental_AOC extends BaseActivity{


	CheckBox cbLip,cbTeeth,cbHardPalate,cbSoftPalate ,cbRetTri,cbTongue,cbGingiva,cbUvula,cbTonsil,cbBluccail,cbFloorOfMouth,
			cb1stBicuspid,cb2ndBicuspid,cb1stMolar,cb2ndMolar,cb3rdMolar,cbCentralIncisor,cbLateralIncisor,cbCuspid,cbHardPalate2,cbSoftPalate2,
			cbUvula2,cbPalatineTonsil,cbBodyOfTongue,cbSubmandibularDucts,cbLips,cbGingiva2;
	ImageView ivLip,ivTeeth,ivHardPalate,ivSoftPalate, ivRetTri, ivTongue1,ivTongue2,ivGingiva,ivUvula,ivTonsil,ivBluccail,ivFloorOfMouth,
			iv1stBicuspid,iv2ndBicuspid,iv1stMolar,iv2ndMolar,iv3rdMolar,ivCentralIncisor,ivLateralIncisor,ivCuspid,ivHardPalate2,ivSoftPalate2,
			ivUvula2,ivPalatineTonsil,ivBodyOfTongue,ivSubmandibularDucts,ivLips,ivGingiva2;

	ViewFlipper vfDentalCareForm;
	Button btnTabPart1, btnTabPart2;
	int selectedChild;

	ImageView ivClose;
	Button btnDone;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dental_aoc);

		if(getSupportActionBar() != null){
			getSupportActionBar().setTitle("Whole Mouth");//Anatomy of Oral Cavity
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_dental_header));
		}

		cbLip = findViewById(R.id.cbLip);
		cbTeeth = findViewById(R.id.cbTeeth);
		cbHardPalate = findViewById(R.id.cbHardPalate);
		cbSoftPalate = findViewById(R.id.cbSoftPalate);

		ivLip = findViewById(R.id.ivLip);
		ivTeeth = findViewById(R.id.ivTeeth);
		ivHardPalate = findViewById(R.id.ivHardPalate);
		ivSoftPalate = findViewById(R.id.ivSoftPalate);

		cbRetTri = findViewById(R.id.cbRetTri);
		cbTongue = findViewById(R.id.cbTongue);
		cbGingiva = findViewById(R.id.cbGingiva);
		cbUvula = findViewById(R.id.cbUvula);
		cbTonsil = findViewById(R.id.cbTonsil);
		cbBluccail = findViewById(R.id.cbBluccail);
		cbFloorOfMouth = findViewById(R.id.cbFloorOfMouth);

		ivRetTri = findViewById(R.id.ivRetTri);
		ivTongue1 = findViewById(R.id.ivTongue1);
		ivTongue2 = findViewById(R.id.ivTongue2);
		ivGingiva = findViewById(R.id.ivGingiva);
		ivUvula = findViewById(R.id.ivUvula);
		ivTonsil = findViewById(R.id.ivTonsil);
		ivBluccail = findViewById(R.id.ivBluccail);
		ivFloorOfMouth = findViewById(R.id.ivFloorOfMouth);

		vfDentalCareForm = findViewById(R.id.vfDentalCareForm);
		btnTabPart1 = findViewById(R.id.btnTabPart1);
		btnTabPart2 = findViewById(R.id.btnTabPart2);

		ivClose = findViewById(R.id.ivClose);
		btnDone = findViewById(R.id.btnDone);



		cb1stBicuspid = findViewById(R.id.cb1stBicuspid);
		cb2ndBicuspid = findViewById(R.id.cb2ndBicuspid);
		cb1stMolar = findViewById(R.id.cb1stMolar);
		cb2ndMolar = findViewById(R.id.cb2ndMolar);
		cb3rdMolar = findViewById(R.id.cb3rdMolar);
		cbCentralIncisor = findViewById(R.id.cbCentralIncisor);
		cbLateralIncisor = findViewById(R.id.cbLateralIncisor);
		cbCuspid = findViewById(R.id.cbCuspid);
		cbHardPalate2 = findViewById(R.id.cbHardPalate2);
		cbSoftPalate2 = findViewById(R.id.cbSoftPalate2);
		cbUvula2 = findViewById(R.id.cbUvula2);
		cbPalatineTonsil = findViewById(R.id.cbPalatineTonsil);
		cbBodyOfTongue = findViewById(R.id.cbBodyOfTongue);
		cbSubmandibularDucts = findViewById(R.id.cbSubmandibularDucts);
		cbLips = findViewById(R.id.cbLips);
		cbGingiva2 = findViewById(R.id.cbGingiva2);

		iv1stBicuspid = findViewById(R.id.iv1stBicuspid);
		iv2ndBicuspid = findViewById(R.id.iv2ndBicuspid);
		iv1stMolar = findViewById(R.id.iv1stMolar);
		iv2ndMolar = findViewById(R.id.iv2ndMolar);
		iv3rdMolar = findViewById(R.id.iv3rdMolar);
		ivCentralIncisor = findViewById(R.id.ivCentralIncisor);
		ivLateralIncisor = findViewById(R.id.ivLateralIncisor);
		ivCuspid = findViewById(R.id.ivCuspid);
		ivHardPalate2 = findViewById(R.id.ivHardPalate2);
		ivSoftPalate2 = findViewById(R.id.ivSoftPalate2);
		ivUvula2 = findViewById(R.id.ivUvula2);
		ivPalatineTonsil = findViewById(R.id.ivPalatineTonsil);
		ivBodyOfTongue = findViewById(R.id.ivBodyOfTongue);
		ivSubmandibularDucts = findViewById(R.id.ivSubmandibularDucts);
		ivLips = findViewById(R.id.ivLips);
		ivGingiva2 = findViewById(R.id.ivGingiva2);




		CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (buttonView, isChecked) -> {

			switch (buttonView.getId()){
				case R.id.cbLip:
					ivLip.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cbTeeth:
					ivTeeth.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cbHardPalate:
					ivHardPalate.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cbSoftPalate:
					ivSoftPalate.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cbRetTri:
					ivRetTri.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cbTongue:
					ivTongue1.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					ivTongue2.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cbGingiva:
					ivGingiva.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cbUvula:
					ivUvula.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cbTonsil:
					ivTonsil.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cbBluccail:
					ivBluccail.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cbFloorOfMouth:
					ivFloorOfMouth.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;


				case R.id.cb1stBicuspid:
					iv1stBicuspid.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cb2ndBicuspid:
					iv2ndBicuspid.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cb1stMolar:
					iv1stMolar.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cb2ndMolar:
					iv2ndMolar.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cb3rdMolar:
					iv3rdMolar.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cbCentralIncisor:
					ivCentralIncisor.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cbLateralIncisor:
					ivLateralIncisor.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cbCuspid:
					ivCuspid.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cbHardPalate2:
					ivHardPalate2.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cbSoftPalate2:
					ivSoftPalate2.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cbUvula2:
					ivUvula2.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cbPalatineTonsil:
					ivPalatineTonsil.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cbBodyOfTongue:
					ivBodyOfTongue.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cbSubmandibularDucts:
					ivSubmandibularDucts.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cbLips:
					ivLips.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;
				case R.id.cbGingiva2:
					ivGingiva2.setVisibility(isChecked ? View.VISIBLE : View.GONE);
					break;


			}
		};
		cbLip.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTeeth.setOnCheckedChangeListener(onCheckedChangeListener);
		cbHardPalate.setOnCheckedChangeListener(onCheckedChangeListener);
		cbSoftPalate.setOnCheckedChangeListener(onCheckedChangeListener);
		cbRetTri.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTongue.setOnCheckedChangeListener(onCheckedChangeListener);
		cbGingiva.setOnCheckedChangeListener(onCheckedChangeListener);
		cbUvula.setOnCheckedChangeListener(onCheckedChangeListener);
		cbTonsil.setOnCheckedChangeListener(onCheckedChangeListener);
		cbBluccail.setOnCheckedChangeListener(onCheckedChangeListener);
		cbFloorOfMouth.setOnCheckedChangeListener(onCheckedChangeListener);

		cb1stBicuspid.setOnCheckedChangeListener(onCheckedChangeListener);
		cb2ndBicuspid.setOnCheckedChangeListener(onCheckedChangeListener);
		cb1stMolar.setOnCheckedChangeListener(onCheckedChangeListener);
		cb2ndMolar.setOnCheckedChangeListener(onCheckedChangeListener);
		cb3rdMolar.setOnCheckedChangeListener(onCheckedChangeListener);
		cbCentralIncisor.setOnCheckedChangeListener(onCheckedChangeListener);
		cbLateralIncisor.setOnCheckedChangeListener(onCheckedChangeListener);
		cbCuspid.setOnCheckedChangeListener(onCheckedChangeListener);
		cbHardPalate2.setOnCheckedChangeListener(onCheckedChangeListener);
		cbSoftPalate2.setOnCheckedChangeListener(onCheckedChangeListener);
		cbUvula2.setOnCheckedChangeListener(onCheckedChangeListener);
		cbPalatineTonsil.setOnCheckedChangeListener(onCheckedChangeListener);
		cbBodyOfTongue.setOnCheckedChangeListener(onCheckedChangeListener);
		cbSubmandibularDucts.setOnCheckedChangeListener(onCheckedChangeListener);
		cbLips.setOnCheckedChangeListener(onCheckedChangeListener);
		cbGingiva2.setOnCheckedChangeListener(onCheckedChangeListener);


		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()){
					case R.id.btnTabPart1:
						btnTabPart1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
						btnTabPart1.setTextColor(getResources().getColor(android.R.color.white));
						btnTabPart2.setBackgroundColor(getResources().getColor(android.R.color.white));
						btnTabPart2.setTextColor(getResources().getColor(R.color.colorPrimary));

						selectedChild = 0;
						if (selectedChild > vfDentalCareForm.getDisplayedChild()) {

							vfDentalCareForm.setInAnimation(activity, R.anim.in_right);
							vfDentalCareForm.setOutAnimation(activity, R.anim.out_left);
						} else {
							vfDentalCareForm.setInAnimation(activity, R.anim.in_left);
							vfDentalCareForm.setOutAnimation(activity, R.anim.out_right);
						}
						if (vfDentalCareForm.getDisplayedChild() != selectedChild) {
							vfDentalCareForm.setDisplayedChild(selectedChild);
						}
						break;
					case R.id.btnTabPart2:
						btnTabPart2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
						btnTabPart2.setTextColor(getResources().getColor(android.R.color.white));
						btnTabPart1.setBackgroundColor(getResources().getColor(android.R.color.white));
						btnTabPart1.setTextColor(getResources().getColor(R.color.colorPrimary));

						selectedChild = 1;
						if (selectedChild > vfDentalCareForm.getDisplayedChild()) {

							vfDentalCareForm.setInAnimation(activity, R.anim.in_right);
							vfDentalCareForm.setOutAnimation(activity, R.anim.out_left);
						} else {
							vfDentalCareForm.setInAnimation(activity, R.anim.in_left);
							vfDentalCareForm.setOutAnimation(activity, R.anim.out_right);
						}
						if (vfDentalCareForm.getDisplayedChild() != selectedChild) {
							vfDentalCareForm.setDisplayedChild(selectedChild);
						}
						break;

					case R.id.ivClose:
						onBackPressed();
						break;

					case R.id.btnDone:
						onBackPressed();
						break;
				}
			}
		};
		btnTabPart1 .setOnClickListener(onClickListener);
		btnTabPart2.setOnClickListener(onClickListener);
		ivClose.setOnClickListener(onClickListener);
		btnDone.setOnClickListener(onClickListener);

	}





}
