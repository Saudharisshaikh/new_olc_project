package com.app.mdlive_uc.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class HideShowKeypad {
	
	Activity activity;

	public HideShowKeypad(Activity activity) {
		
		this.activity  = activity;
	}
	
	public void hideSoftKeyboard() {
	    if(activity.getCurrentFocus()!=null) {
	        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
	        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	    }
	}

	/**
	 * Shows the soft keyboard
	 */
	public void showSoftKeyboard(View view) {
	    InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
	    view.requestFocus();
	    inputMethodManager.showSoftInput(view, 0);
	}



	public void hidekeyboardOnDialog(){
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	}

}
