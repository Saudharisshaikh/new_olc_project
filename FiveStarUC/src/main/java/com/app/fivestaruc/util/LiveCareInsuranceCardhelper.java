package com.app.fivestaruc.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.app.fivestaruc.R;
import com.wildma.idcardcamera.camera.IDCardCamera;

import java.util.ArrayList;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.models.sort.SortingTypes;
import droidninja.filepicker.utils.Orientation;

public class LiveCareInsuranceCardhelper {

    Activity activity;
    int fileFlag;// 1 = ICfrontImg, 2 = ICbackImg, 3=IDcardFrontImg, 4 = IDcardBackImg;
    LiveCareInsuranceInterface liveCareInsuranceInterface;

    public LiveCareInsuranceCardhelper(Activity activity, LiveCareInsuranceInterface liveCareInsuranceInterface) {
        this.activity = activity;
        this.liveCareInsuranceInterface = liveCareInsuranceInterface;
    }



    private ArrayList<String> photoPaths = new ArrayList<>();
    private ArrayList<String> docPaths = new ArrayList<>();//not using doc yet
    public void pickInsuranceCardPhoto(int imgFlag) {
        fileFlag = imgFlag;
        /*FilePickerBuilder.getInstance()
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
                .pickPhoto(activity, Reports.CUSTOM_REQUEST_CODE);*/

        if(fileFlag == 1 || fileFlag == 3){
            IDCardCamera.create(activity).openCamera(IDCardCamera.TYPE_IDCARD_FRONT);
        }else {
            IDCardCamera.create(activity).openCamera(IDCardCamera.TYPE_IDCARD_BACK);
        }
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


        if (resultCode == IDCardCamera.RESULT_CODE) {
            final String filePath = IDCardCamera.getImagePath(data);
            DATA.print("-- File Path from ID card Camera : "+filePath);
            if (!TextUtils.isEmpty(filePath)) {
                if(fileFlag == 1 && liveCareInsuranceInterface != null){
                    liveCareInsuranceInterface.displayICfrontImg(filePath);
                }else if(fileFlag == 2 && liveCareInsuranceInterface != null){
                    liveCareInsuranceInterface.displayICbackImg(filePath);
                }else if(fileFlag == 3 && liveCareInsuranceInterface != null){
                    liveCareInsuranceInterface.displayIDcardFrontImg(filePath);
                }else if(fileFlag == 4 && liveCareInsuranceInterface != null){
                    liveCareInsuranceInterface.displayIDcardBackImg(filePath);
                }
            }
        }

        /*photoPaths = new ArrayList<>();
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
        DATA.print("--total files: "+filePaths.size());
        if(!filePaths.isEmpty()){
            String filePath = filePaths.get(0);
            if(fileFlag == 1 && liveCareInsuranceInterface != null){
                liveCareInsuranceInterface.displayICfrontImg(filePath);
            }else if(fileFlag == 2 && liveCareInsuranceInterface != null){
                liveCareInsuranceInterface.displayICbackImg(filePath);
            }else if(fileFlag == 3 && liveCareInsuranceInterface != null){
                liveCareInsuranceInterface.displayIDcardFrontImg(filePath);
            }else if(fileFlag == 4 && liveCareInsuranceInterface != null){
                liveCareInsuranceInterface.displayIDcardBackImg(filePath);
            }
        }*/
    }
}
