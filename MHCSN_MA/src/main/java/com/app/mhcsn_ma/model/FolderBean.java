package com.app.mhcsn_ma.model;

import java.util.ArrayList;

/**
 * Created by Engr G M on 4/5/2017.
 */

public class FolderBean {

    public String id;
    public String patient_id;
    public String sub_patient_id;
    public String folder_name;
    public String dateof;
    public String typeof;
    public String report_name;
    public ArrayList<FileBean> fileBeens;

    public FolderBean(String id, String patient_id, String sub_patient_id, String folder_name, String dateof, String typeof, String report_name,ArrayList<FileBean> fileBeens) {
        this.id = id;
        this.patient_id = patient_id;
        this.sub_patient_id = sub_patient_id;
        this.folder_name = folder_name;
        this.dateof = dateof;
        this.typeof = typeof;
        this.report_name = report_name;
        this.fileBeens = fileBeens;
    }
}
