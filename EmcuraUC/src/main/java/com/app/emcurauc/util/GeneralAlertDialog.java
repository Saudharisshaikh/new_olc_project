package com.app.emcurauc.util;

import android.app.AlertDialog;
import android.content.Context;

import com.app.emcurauc.R;

public class GeneralAlertDialog {

    public static void callAlert(Context context,String message,Lamda positiveLamda,Lamda negativeLamda,String positiveText,String negativeText,String title){
        AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.CustomAlertDialogTheme)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, (dialog, which) -> {
                    positiveLamda.callLamda();
                    dialog.dismiss();

                })
                .setNegativeButton(negativeText,((dialogInterface, i) -> {
                    negativeLamda.callLamda();
                    dialogInterface.dismiss();
                }))
                .create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

}
