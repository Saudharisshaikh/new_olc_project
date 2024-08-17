package com.app.amnm_dr.model;

/**
 * Created by Engr G M on 4/5/2017.
 */

public class FileBean {

    public String id;
    public String patient_id;
    public String sub_patient_id;
    public String folder_id;
    public String report_name;
    public String file_display_name;
    public String dateof;
    public String typeof;
    public String report_url;
    public String report_thumb;

    public FileBean(String id, String patient_id, String sub_patient_id, String folder_id, String report_name, String file_display_name, String dateof, String typeof, String report_url, String report_thumb) {
        this.id = id;
        this.patient_id = patient_id;
        this.sub_patient_id = sub_patient_id;
        this.folder_id = folder_id;
        this.report_name = report_name;
        this.file_display_name = file_display_name;
        this.dateof = dateof;
        this.typeof = typeof;
        this.report_url = report_url;
        this.report_thumb = report_thumb;
    }
}
