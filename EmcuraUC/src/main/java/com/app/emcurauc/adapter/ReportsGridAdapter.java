package com.app.emcurauc.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.emcurauc.R;
import com.app.emcurauc.Reports;
import com.app.emcurauc.model.ReportsModel;
import com.app.emcurauc.util.DATA;

public class ReportsGridAdapter extends ArrayAdapter<ReportsModel> {

    Context context;
    //int layoutResourceId;
    //ArrayList<ReportsModel> data = new ArrayList<ReportsModel>();

    public ReportsGridAdapter(Context context) //, int layoutResourceId, ArrayList<ReportsModel> data
    {
        super(context, R.layout.reports_grid_row, DATA.allReportsFiltered);
        //this.layoutResourceId = layoutResourceId;
        this.context = context;
        //this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        RecordHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.reports_grid_row, parent, false);
            holder = new RecordHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.item_text);
            holder.imgIcon = (ImageView) row.findViewById(R.id.item_image);
            holder.ivDeleteReport = row.findViewById(R.id.ivDeleteReport);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        //ReportsModel item = data.get(position);
        holder.txtTitle.setText(DATA.allReportsFiltered.get(position).name);
        //holder.imgIcon.setImageResource(position);

        try {
            String extention = DATA.allReportsFiltered.get(position).report_thumb.substring(DATA.allReportsFiltered.get(position).report_thumb.lastIndexOf("."));
            if (extention.equals(".png") || extention.equals(".jpg") || extention.equals(".jpeg")) {
                //UrlImageViewHelper.setUrlDrawable(holder.imgIcon, DATA.allReportsFiltered.get(position).report_thumb, R.drawable.folder_icon);
                //holder.imgIcon.setImageResource(R.drawable.icon_image);
                DATA.loadImageFromURL(DATA.allReportsFiltered.get(position).report_url, R.drawable.ic_placeholder_2, holder.imgIcon);
            } else if (extention.equals(".pdf")) {
                holder.imgIcon.setImageResource(R.drawable.ic_pickfile_pdf);
            } else if (extention.equals(".docx") || extention.equals(".doc")) {
                holder.imgIcon.setImageResource(R.drawable.ic_pickfile_docx);
            }else if(extention.equals(".pptx") || extention.equals(".ppt")){
                holder.imgIcon.setImageResource(R.drawable.ic_pickfile_pptx);
            }else if (extention.equals(".xls") || extention.equals(".xlsx")) {
                holder.imgIcon.setImageResource(R.drawable.ic_pickfile_xlsx);
            }else if(extention.equals(".txt")){
                holder.imgIcon.setImageResource(R.drawable.ic_pickfile_txt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.ivDeleteReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Reports) context).confirmDeleteReportDialog(DATA.allReportsFiltered.get(position).id);
            }
        });

        return row;
    }


    static class RecordHolder {
        TextView txtTitle;
        ImageView imgIcon,ivDeleteReport;
    }
}

