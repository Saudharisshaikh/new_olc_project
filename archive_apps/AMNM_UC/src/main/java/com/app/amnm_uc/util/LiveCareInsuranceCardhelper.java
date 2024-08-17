package com.app.amnm_uc.util;

import android.app.Activity;
import android.content.Intent;

import com.app.amnm_uc.R;
import com.app.amnm_uc.Reports;

import java.util.ArrayList;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.models.sort.SortingTypes;
import droidninja.filepicker.utils.Orientation;

public class LiveCareInsuranceCardhelper {

    Activity activity;
    int fileFlag;
    LiveCareInsuranceInterface liveCareInsuranceInterface;

    public LiveCareInsuranceCardhelper(Activity activity, LiveCareInsuranceInterface liveCareInsuranceInterface) {
        this.activity = activity;
        this.liveCareInsuranceInterface = liveCareInsuranceInterface;
    }



    private ArrayList<String> photoPaths = new ArrayList<>();
    private ArrayList<String> docPaths = new ArrayList<>();//not using doc yet
    public void pickInsuranceCardPhoto(int imgFlag) {
        fileFlag = imgFlag;
        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setSelectedFiles(photoPaths)
                .setActivityTheme(R.style.FilePickerTheme)
                .setActivityTitle("Please select a card picture")
                .enableVideoPicker(false)
                .enableCameraSupport(true)
                .showGifs(false)
                .showFolderView(false)
                .enableSelectAll(true)
                .enableImagePicker(true)
                .setCameraPlaceholder(R.drawable.custom_camera)
                .withOrientation(Orientation.PORTRAIT_ONLY)
                .pickPhoto(activity, Reports.CUSTOM_REQUEST_CODE);
    }

    public void onPickDoc() {//just call this method to pic document
        fileFlag = 2;
        String[] zips = { ".zip", ".rar" };
        String[] pdfs = { ".pdf" };
        String[] doc = { ".doc", ".docx" };
        String[] ppt = { ".ppt", ".pptx" };
        String[] xlsx = {".xls", ".xlsx"};
        String[] txt = {".txt"};


        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setSelectedFiles(docPaths)
                .setActivityTheme(R.style.FilePickerTheme)//DrawerTheme2
                .setActivityTitle("Please select a file")
                //.addFileSupport("ZIP", zips)
                .addFileSupport("PDF", pdfs, R.drawable.ic_pickfile_pdf)
                .addFileSupport("DOC", doc, R.drawable.ic_pickfile_docx)
                .addFileSupport("PPT", ppt, R.drawable.ic_pickfile_pptx)
                .addFileSupport("XLSX", xlsx, R.drawable.ic_pickfile_xlsx)
                //.addFileSupport("TXT", txt, R.drawable.ic_pickfile_txt)

                .enableDocSupport(false)
                .enableSelectAll(true)
                .sortDocumentsBy(SortingTypes.name)
                .withOrientation(Orientation.UNSPECIFIED)
                .pickFile(activity);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data){
        photoPaths = new ArrayList<>();
        docPaths = new ArrayList<>();
        switch (requestCode) {
            case Reports.CUSTOM_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    photoPaths = new ArrayList<>();
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                }
                break;
            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    docPaths = new ArrayList<>();
                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                }
                break;
        }
        //addThemToView(photoPaths, docPaths);
        ArrayList<String> filePaths = new ArrayList<>();
        if (photoPaths != null) filePaths.addAll(photoPaths);
        if (docPaths != null) filePaths.addAll(docPaths);
        System.out.println("--total files: "+filePaths.size());
        if(!filePaths.isEmpty()){
            String filePath = filePaths.get(0);
            if(fileFlag == 1 && liveCareInsuranceInterface != null){
                liveCareInsuranceInterface.displayICfrontImg(filePath);
            }else if(fileFlag == 2 && liveCareInsuranceInterface != null){
                liveCareInsuranceInterface.displayICbackImg(filePath);
            }
        }
    }
}
