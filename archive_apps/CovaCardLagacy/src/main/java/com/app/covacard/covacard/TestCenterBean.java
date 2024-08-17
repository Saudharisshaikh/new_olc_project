package com.app.covacard.covacard;

public class TestCenterBean {

    public String Id;
    public String Shape;
    public String SiteUrl;
    public String DwRecordCreateDt;
    public String CtrNm;
    public String CtrAddress;
    public String CtrCity;
    public String CtrStateAbbr;
    public String CtrZipCd;
    public String CtrPhoneNum;
    public String ParentCtrNm;
    public String CountyNm;
    public String StateNm;
    public String ApproxValueCd;
    public String UrlTxt;
    public String LocNameDesc;
    public double Longitude;
    public double Latitude;
    public String EndDt;
    public String ParentCtrCity;
    public float Distance;
    public boolean FilteredResultsReturned;
    public String Covid19TestStatus;
    public String TeleHealthStatus;
    public String TelehealthText;


    @Override
    public String toString() {
        return CtrNm + "\nAddress : " + CtrAddress;
    }
}
