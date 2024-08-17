package com.app.fivestardoc.util;

import static android.os.Build.VERSION_CODES.R;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.app.fivestardoc.R;


public class ListDialog {

    public static <T> AlertDialog showListDialogGenericList(Context context, String []items, @StringRes int titleId, SelectionListener listener) {


        View view = LayoutInflater.from(context).inflate(com.app.fivestardoc.R.layout.list_dialog_item, null, false);
        ListView lv = view.findViewById(com.app.fivestardoc.R.id.lv);
        ImageView cancelDialog = view.findViewById(com.app.fivestardoc.R.id.close_dialog);
        lv.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, items));
        TextView tv = view.findViewById(com.app.fivestardoc.R.id.tvTitle);
        tv.setText(titleId);

        AlertDialog ad = new AlertDialog.Builder(context)
                .setView(view)
                .show();
        lv.setOnItemClickListener((parent, view1, position, id) -> {
            ad.dismiss();
            listener.onListItemSelected(position);
        });
        cancelDialog.setOnClickListener(view1 -> {
        ad.dismiss();
        });
        return ad;
    }


    public interface SelectionListener {
        void onListItemSelected(int position);
    }
}
