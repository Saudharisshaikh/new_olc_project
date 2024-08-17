package com.app.fivestardoc.api;

import android.app.Activity;
import com.google.android.material.snackbar.Snackbar;
import android.text.Spanned;
import android.view.View;

/**
 * Created by HP on 12/23/2016.
 */

public class CustomSnakeBar {

    private Activity context;
    private static CustomSnakeBar customSnakeBar = null;

    public static CustomSnakeBar getCustomSnakeBarInstance(Activity context){
        //if(customSnakeBar == null){
            customSnakeBar = new CustomSnakeBar(context);
        //}

        return customSnakeBar;
    }

    private CustomSnakeBar(Activity context) {
        this.context = context;
    }

    public void showToast(String text){
        /*SnackbarManager.show(
                Snackbar.with(context) // context
                        .text(text) // text to display
                        .actionLabel("") // action button label
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {

                            }
                        })
                , (Activity) context);*/

        Snackbar.make(context.findViewById(android.R.id.content), text , Snackbar.LENGTH_LONG)
                .setAction("", null)
                .show();
    }

    public void showToast(Spanned text){
        /*SnackbarManager.show(
                Snackbar.with(context) // context
                        .text(text) // text to display
                        .actionLabel("") // action button label
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {

                            }
                        })
                , (Activity) context);*/
        Snackbar.make(context.findViewById(android.R.id.content), text , Snackbar.LENGTH_LONG)
                .setAction("", null)
                .show();
    }

    public void showToastWithAction(String btnText, String text, View.OnClickListener onClickListener){

        Snackbar.make(context.findViewById(android.R.id.content), text , Snackbar.LENGTH_LONG)
                .setAction(btnText, onClickListener)
                .show();
    }
}
