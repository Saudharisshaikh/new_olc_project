package com.app.onlinecare.util;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.app.onlinecare.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class CustomAnimations {

	public void shakeAnimate(View v, final int time, final Spinner sp) {
		//et.setHintTextColor(Color.parseColor("#e74c3c"));
		sp.setBackgroundResource(R.drawable.cust_border_white_outline_red);

		YoYo.with(Techniques.Shake).withListener(new Animator.AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub
				//et.setHintTextColor(Color.parseColor("#e74c3c"));
				//et.setTextColor(Color.parseColor("#e74c3c"));
				sp.setBackgroundResource(R.drawable.cust_border_white_outline_red);
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				// TODO Auto-generated method stub
				//et.setHintTextColor(Color.parseColor("#808080"));
				//et.setTextColor(Color.parseColor("#191919"));
				sp.setBackgroundResource(R.drawable.spinner_bg);
			}

			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub

			}
		}).duration(time).playOn(v);
	}

	public void shakeAnimate(View v, final int time, final EditText et) {
		et.setHintTextColor(Color.parseColor("#e74c3c"));

		YoYo.with(Techniques.Shake).withListener(new Animator.AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub
				et.setHintTextColor(Color.parseColor("#e74c3c"));
				et.setTextColor(Color.parseColor("#e74c3c"));
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				// TODO Auto-generated method stub
				et.setHintTextColor(Color.parseColor("#808080"));
				et.setTextColor(Color.parseColor("#191919"));
			}

			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub

			}
		}).duration(time).playOn(v);
	}
	public void shakeAnimate(View v,  final EditText et) {
		final int time = 800;
		et.setHintTextColor(Color.parseColor("#e74c3c"));

		YoYo.with(Techniques.Shake).withListener(new Animator.AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub
				et.setHintTextColor(Color.parseColor("#e74c3c"));
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				// TODO Auto-generated method stub
				et.setHintTextColor(Color.parseColor("#808080"));
			}

			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub

			}
		}).duration(time).playOn(v);
	}


	public void shakeAnimate(View v,  final CheckBox cb) {
		final int time = 800;
		cb.setTextColor(Color.parseColor("#e74c3c"));
		YoYo.with(Techniques.Shake).withListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator arg0) {
				cb.setTextColor(Color.parseColor("#e74c3c"));
			}
			@Override
			public void onAnimationRepeat(Animator arg0) {}
			@Override
			public void onAnimationEnd(Animator arg0) {
				cb.setTextColor(Color.parseColor("#808080"));
			}
			@Override
			public void onAnimationCancel(Animator arg0) {}
		}).duration(time).playOn(v);
	}

	public void RunAnimation(Context c, final EditText et, LinearLayout lay) {
		Animation a = AnimationUtils.loadAnimation(c, R.anim.bounce);
		a.reset();
		a.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				et.setHintTextColor(Color.parseColor("#e74c3c"));
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				et.setHintTextColor(Color.parseColor("#808080"));

			}
		});
		lay.clearAnimation();
		lay.startAnimation(a);
	}

	public void dropDownAnimate(View v, int time) {

		YoYo.with(Techniques.SlideInDown).duration(time).playOn(v);
	}

	public void dropUpAnimate(View v, int time) {

		YoYo.with(Techniques.SlideOutUp).duration(time).playOn(v);
	}
}
