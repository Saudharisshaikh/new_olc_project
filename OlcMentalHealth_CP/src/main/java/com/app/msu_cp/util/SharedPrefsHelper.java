package com.app.msu_cp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.app.msu_cp.OnlineCareNurse;
import com.app.msu_cp.model.PastHistoryBean;
import com.app.msu_cp.model.SpecialityModel;
import com.app.msu_cp.reliance.CompanyBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class SharedPrefsHelper {
    private static final String SHARED_PREFS_NAME = DATA.SHARED_PREFS_NAME;

    public static final String PKG_AMOUNT = "eLiveCarePackageAmount";

    //public static final String LAST_INSERTED_ROW = "last_inserted_row_id";
    //public static final String LAST_INSERTED_ROW_BREAK = "last_inserted_row_id_break";

    /*private static final String QB_USER_ID = "qb_user_id";
    private static final String QB_USER_LOGIN = "qb_user_login";
    private static final String QB_USER_PASSWORD = "qb_user_password";
    private static final String QB_USER_FULL_NAME = "qb_user_full_name";
    private static final String QB_USER_TAGS = "qb_user_tags";*/

    private static SharedPrefsHelper instance;

    private SharedPreferences sharedPreferences;

    public static synchronized SharedPrefsHelper getInstance() {
        if (instance == null) {
            instance = new SharedPrefsHelper();
        }

        return instance;
    }

    private SharedPrefsHelper() {
        instance = this;
        Log.i("++--++","App instance in prefshelper");
        sharedPreferences = OnlineCareNurse.getInstance().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        DATA.print("--App instance in prefshelper: "+ OnlineCareNurse.getInstance());
    }

    public void delete(String key) {
        if (sharedPreferences.contains(key)) {
            getEditor().remove(key).commit();
        }
    }

    public void save(String key, Object value) {
        SharedPreferences.Editor editor = getEditor();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Enum) {
            editor.putString(key, value.toString());
        } else if (value != null) {
            throw new RuntimeException("Attempting to save non-supported preference");
        }

        editor.commit();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) sharedPreferences.getAll().get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defValue) {
        T returnValue = (T) sharedPreferences.getAll().get(key);
        return returnValue == null ? defValue : returnValue;
    }

    public boolean has(String key) {
        return sharedPreferences.contains(key);
    }


    /*public void saveQbUser(QBUser qbUser) {
        save(QB_USER_ID, qbUser.getId());
        save(QB_USER_LOGIN, qbUser.getLogin());
        save(QB_USER_PASSWORD, qbUser.getPassword());
        save(QB_USER_FULL_NAME, qbUser.getFullName());
        save(QB_USER_TAGS, qbUser.getTags().getItemsAsString());
    }

    public void removeQbUser() {
        delete(QB_USER_ID);
        delete(QB_USER_LOGIN);
        delete(QB_USER_PASSWORD);
        delete(QB_USER_FULL_NAME);
        delete(QB_USER_TAGS);
    }

    public QBUser getQbUser() {
        if (hasQbUser()) {
            Integer id = get(QB_USER_ID);
            String login = get(QB_USER_LOGIN);
            String password = get(QB_USER_PASSWORD);
            String fullName = get(QB_USER_FULL_NAME);
            String tagsInString = get(QB_USER_TAGS);

            StringifyArrayList<String> tags = null;

            if (tagsInString != null) {
                tags = new StringifyArrayList<>();
                tags.add(tagsInString.split(","));
            }

            QBUser user = new QBUser(login, password);
            user.setId(id);
            user.setFullName(fullName);
            user.setTags(tags);
            return user;
        } else {
            return null;
        }
    }

    public boolean hasQbUser() {
        return has(QB_USER_LOGIN) && has(QB_USER_PASSWORD);
    }*/

    public void clearAllData(){
        SharedPreferences.Editor editor = getEditor();
        editor.clear().commit();
    }

    private SharedPreferences.Editor getEditor() {
        return sharedPreferences.edit();
    }


    //========================================User=========================================================
    /*final public static String KEY_USER_OBJECT = "parent_user_object";

    public void saveParentUser(SubUsersModel subUsersModel){
        String json = new Gson().toJson(subUsersModel);
        save(KEY_USER_OBJECT,json);
    }

    public SubUsersModel getParentUser(){
        String json = get(KEY_USER_OBJECT,"");
        if (!json.isEmpty()) {
            return new Gson().fromJson(json, SubUsersModel.class);
        } else {
            return new SubUsersModel();
        }
    }*/



    //===============================INSURANCE==========================================================
    public void savePastHistoryDiag(ArrayList<PastHistoryBean> pastHistoryBeans){
        String json = new Gson().toJson(pastHistoryBeans);
        save("past_history_beans",json);
    }

    public ArrayList<PastHistoryBean> getPastHistoryDiag(){
        String json = get("past_history_beans","");
        DATA.print("-- JSON: "+json);
        if (!json.isEmpty()) {
            Type listType = new TypeToken<ArrayList<PastHistoryBean>>() {}.getType();
            ArrayList<PastHistoryBean> pastHistoryBeans = new Gson().fromJson(json, listType);
            return pastHistoryBeans;
        } else {
            //return null;
            //return new ArrayList<PackageBean>();
            return new ArrayList<>();
        }
    }
    //===============================INSURANCE==========================================================


    //===============================All Companies==========================================================
    public void saveAllCompanies(List<CompanyBean.AllCompanyBean> companyBeans){
        String json = new Gson().toJson(companyBeans);
        save("all_companies_relaince",json);
    }

    public List<CompanyBean.AllCompanyBean> getAllCompanies(){
        String json = get("all_companies_relaince","");
        DATA.print("-- JSON: "+json);
        if (!json.isEmpty()) {
            Type listType = new TypeToken<ArrayList<CompanyBean.AllCompanyBean>>() {}.getType();
            List<CompanyBean.AllCompanyBean> allCompanyBeans = new Gson().fromJson(json, listType);
            return allCompanyBeans;
        } else {
            //return null;
            //return new ArrayList<PackageBean>();
            return new ArrayList<>();
        }
    }
    //===============================All Companies==========================================================



    //===============================Specialities==========================================================
    public void saveAllSpecialities(ArrayList<SpecialityModel> specialityModels){
        String json = new Gson().toJson(specialityModels);
        save("specialities_olc",json);
    }

    public ArrayList<SpecialityModel> getAllSpecialities(){
        String json = get("specialities_olc","");
        DATA.print("-- JSON: "+json);
        if (!json.isEmpty()) {
            Type listType = new TypeToken<ArrayList<SpecialityModel>>() {}.getType();
            ArrayList<SpecialityModel> specialityModels = new Gson().fromJson(json, listType);
            return specialityModels;
        } else {
            //return null;
            //return new ArrayList<PackageBean>();
            return new ArrayList<>();
        }
    }
    //===============================Specialities==========================================================

    /*public void saveDefaultAddress(AddressBean addressBean){
        if(addressBean!=null){
            String json = new Gson().toJson(addressBean);
            save("default_address",json);
        }else {
            save("default_address","");
        }
    }

    public AddressBean getDefaultAddress(){
        String json = get("default_address","");
        if (!json.isEmpty()) {
            return new Gson().fromJson(json, AddressBean.class);
        } else {
            return null;
//            return new ProductBean();
        }
    }*/


    //========================================User=========================================================

    //=====================SITES===============================================
    /*public void saveAllSites(ArrayList<Site> sites){
        String json = new Gson().toJson(sites);
        save("all_sites",json);
    }

    public ArrayList<Site> getAllSites(){
        String json = get("all_sites","");
        DATA.print("-- JSON: "+json);
        if (!json.isEmpty()) {
            Type listType = new TypeToken<ArrayList<Site>>() {}.getType();
            ArrayList<Site> sites = new Gson().fromJson(json, listType);
            return sites;
        } else {
            //return null;
            //return new ArrayList<PackageBean>();
            return new ArrayList<>();
        }
    }*/
    //=====================SITES===============================================


    //==============================Subscription package================================================
    /*public void saveMyPackage(MyPackageBean myPackageBean){
        String json = new Gson().toJson(myPackageBean);
        save("my_package",json);
    }
    public MyPackageBean getMyPackage(){
        String json = get("my_package","");
        if (!json.isEmpty()) {
            return new Gson().fromJson(json, MyPackageBean.class);
        } else {
            //return null;
            return new MyPackageBean("","","","","","");
        }
    }

    public void saveAllPackages(ArrayList<PackageBean> packageBeens){
        String json = new Gson().toJson(packageBeens);
        save("all_packages",json);
    }

    public ArrayList<PackageBean> getAllPackage(){
        String json = get("all_packages","");
        DATA.print("-- JSON: "+json);
        if (!json.isEmpty()) {
            Type listType = new TypeToken<ArrayList<PackageBean>>() {}.getType();
            ArrayList<PackageBean> packageBeens = new Gson().fromJson(json, listType);
            return packageBeens;
        } else {
            //return null;
            //return new ArrayList<PackageBean>();
           return new ArrayList<>();
        }
    }*/
    //==============================Subscription package================================================

    //=======================AllStudio/User video beans==================================================
    //ArrayList<AllStudioVideoBean> allStudioVideoBeens

    /*public void saveAllVideosStudio(ArrayList<AllStudioVideoBean> allStudioVideoBeens){
        String json = new Gson().toJson(allStudioVideoBeens);
        save("all_studio_videos",json);
    }

    public ArrayList<AllStudioVideoBean> getAllVideosStudio(){
        String json = get("all_studio_videos","");
        DATA.print("-- JSON: "+json);
        if (!json.isEmpty()) {
            Type listType = new TypeToken<ArrayList<AllStudioVideoBean>>() {}.getType();
            ArrayList<AllStudioVideoBean> allStudioVideoBeens = new Gson().fromJson(json, listType);
            return allStudioVideoBeens;
        } else {
            //return null;
            //return new ArrayList<PackageBean>();
            return new ArrayList<>();
        }
    }

    public void saveAllVideosUser(ArrayList<AllStudioVideoBean> allStudioVideoBeens){
        String json = new Gson().toJson(allStudioVideoBeens);
        save("all_user_videos",json);
    }

    public ArrayList<AllStudioVideoBean> getAllVideosUser(){
        String json = get("all_user_videos","");
        DATA.print("-- JSON: "+json);
        if (!json.isEmpty()) {
            Type listType = new TypeToken<ArrayList<AllStudioVideoBean>>() {}.getType();
            ArrayList<AllStudioVideoBean> allStudioVideoBeens = new Gson().fromJson(json, listType);
            return allStudioVideoBeens;
        } else {
            //return null;
            //return new ArrayList<PackageBean>();
            return new ArrayList<>();
        }
    }*/

    //single studio/user
    /*public void saveVideosSingleStudio(ArrayList<StudioVideoBean> studioVideoBeens, String prefsKeyId){
        String json = new Gson().toJson(studioVideoBeens);
        save("single_studio_"+prefsKeyId,json);
    }

    public ArrayList<StudioVideoBean> getVideosSingleStudio(String prefsKeyId){
        String json = get("single_studio_"+prefsKeyId,"");
        DATA.print("-- JSON: "+json);
        if (!json.isEmpty()) {
            Type listType = new TypeToken<ArrayList<StudioVideoBean>>() {}.getType();
            ArrayList<StudioVideoBean> studioVideoBeens = new Gson().fromJson(json, listType);
            return studioVideoBeens;
        } else {
            //return null;
            //return new ArrayList<PackageBean>();
            return new ArrayList<>();
        }
    }

    public void saveVideosSingleUser(ArrayList<UserVideoBean> userVideoBeens, String prefsKeyId){
        String json = new Gson().toJson(userVideoBeens);
        save("single_user_"+prefsKeyId,json);
    }

    public ArrayList<UserVideoBean> getVideosSingleUser(String prefsKeyId){
        String json = get("single_user_"+prefsKeyId,"");
        DATA.print("-- JSON: "+json);
        if (!json.isEmpty()) {
            Type listType = new TypeToken<ArrayList<UserVideoBean>>() {}.getType();
            ArrayList<UserVideoBean> userVideoBeens = new Gson().fromJson(json, listType);
            return userVideoBeens;
        } else {
            //return null;
            //return new ArrayList<PackageBean>();
            return new ArrayList<>();
        }
    }*/
    //=======================AllStudio/User video beans==================================================


    //====================liked videos, subscribed channels=============================================
    /*public void saveLikedVideos(ArrayList<AllStudioVideoBean> likedVideos, String prefsKeyId){
        String json = new Gson().toJson(likedVideos);
        save("liked_videos_"+prefsKeyId,json);
    }

    public ArrayList<AllStudioVideoBean> getLikedVideos(String prefsKeyId){
        String json = get("liked_videos_"+prefsKeyId,"");
        DATA.print("-- JSON: "+json);
        if (!json.isEmpty()) {
            Type listType = new TypeToken<ArrayList<AllStudioVideoBean>>() {}.getType();
            ArrayList<AllStudioVideoBean> likedVideos = new Gson().fromJson(json, listType);
            return likedVideos;
        } else {
            //return null;
            //return new ArrayList<PackageBean>();
            return new ArrayList<>();
        }
    }

    public void saveSubsChannels(ArrayList<SubsChannelBean> subsChannelBeens, String prefsKeyId){
        String json = new Gson().toJson(subsChannelBeens);
        save("subscribed_channels_"+prefsKeyId,json);
    }

    public ArrayList<SubsChannelBean> getSubsChannels(String prefsKeyId){
        String json = get("subscribed_channels_"+prefsKeyId,"");
        DATA.print("-- JSON: "+json);
        if (!json.isEmpty()) {
            Type listType = new TypeToken<ArrayList<SubsChannelBean>>() {}.getType();
            ArrayList<SubsChannelBean> subsChannelBeens = new Gson().fromJson(json, listType);
            return subsChannelBeens;
        } else {
            //return null;
            //return new ArrayList<PackageBean>();
            return new ArrayList<>();
        }
    }*/
    //====================liked videos, subscribed channels=============================================
}
