package com.app.msu_cp.careplan;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.msu_cp.DoctorsList;
import com.app.msu_cp.R;
import com.app.msu_cp.api.ApiCallBack;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.util.ChoosePictureDialog;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.GloabalSocket;
import com.github.ksoichiro.android.observablescrollview.ObservableGridView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by Engr G M on 7/6/2018.
 */

public class FragmentCareTeam extends Fragment implements ApiCallBack, GloabalSocket.SocketEmitterCallBack,ObservableScrollViewCallbacks {

    Activity activity;
    ObservableGridView gvCareTeam;
    TextView tvNoCareTeam;
    Button btnAddTeamMember;
    CareTeamAdapter careTeamAdapter;
    LinearLayout layBottom1;

    public static final String CTM_TYPE_OTHER = "other";//means CTM added by raw input

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        DATA.print("-- fragment onCreateView");

        View rootView = inflater.inflate(R.layout.frag_care_team, container,false);

        gvCareTeam = rootView.findViewById(R.id.gvCareTeam);
        gvCareTeam.setScrollViewCallbacks(this);

        tvNoCareTeam = rootView.findViewById(R.id.tvNoCareTeam);
        btnAddTeamMember = rootView.findViewById(R.id.btnAddTeamMember);
        layBottom1 = rootView.findViewById(R.id.layBottom1);

        btnAddTeamMember.setOnClickListener(v -> showAddMemberDialog());

        if(ActivityCarePlanDetail.careTeamBeans != null){
            careTeamAdapter = new CareTeamAdapter(activity,ActivityCarePlanDetail.careTeamBeans);
            gvCareTeam.setAdapter(careTeamAdapter);
            if(ActivityCarePlanDetail.careTeamBeans.isEmpty()){
                tvNoCareTeam.setVisibility(View.VISIBLE);
            }else {
                tvNoCareTeam.setVisibility(View.GONE);
            }
        }

        return rootView;
    }

    Dialog dialogAddTeamMember;
    Spinner spSelMember;
    ImageView ivCTM_Img;
    public void showAddMemberDialog(){
        dialogAddTeamMember = new Dialog(activity,R.style.TransparentThemeH4B);
        dialogAddTeamMember.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogAddTeamMember.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogAddTeamMember.setContentView(R.layout.dialog_add_member);

        ImageView ivCancel = dialogAddTeamMember.findViewById(R.id.ivCancel);
        Spinner spMemberType = dialogAddTeamMember.findViewById(R.id.spMemberType);
        spSelMember = dialogAddTeamMember.findViewById(R.id.spSelMember);
        Button btnAdMember = dialogAddTeamMember.findViewById(R.id.btnAdMember);

        LinearLayout spMemberCont = dialogAddTeamMember.findViewById(R.id.spMemberCont);
        LinearLayout otherCTMCont = dialogAddTeamMember.findViewById(R.id.otherCTMCont);
        EditText etCTM_Name = dialogAddTeamMember.findViewById(R.id.etCTM_Name);
        EditText etCTM_Phone = dialogAddTeamMember.findViewById(R.id.etCTM_Phone);
        ivCTM_Img = dialogAddTeamMember.findViewById(R.id.ivCTM_Img);
        Button btnCTM_Img = dialogAddTeamMember.findViewById(R.id.btnCTM_Img);

        btnCTM_Img.setOnClickListener(v -> (
                (ActivityCarePlanDetail)activity).openActivity.open(ChoosePictureDialog.class, false)
        );

        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getId()){
                    case R.id.spMemberType:
                        if(ct_typeBeans.get(position).kay.equalsIgnoreCase(CTM_TYPE_OTHER)){
                            otherCTMCont.setVisibility(View.VISIBLE);
                            spMemberCont.setVisibility(View.GONE);
                        }else {
                            otherCTMCont.setVisibility(View.GONE);
                            spMemberCont.setVisibility(View.VISIBLE);

                            ApiManager.shouldShowPD = false;
                            RequestParams params = new RequestParams();
                            ApiManager apiManager = new ApiManager(ApiManager.CARE_TEAM_BY_TYPE+"/"+ct_typeBeans.get(position).kay,"get",params,FragmentCareTeam.this::fetchDataCallback, activity);
                            apiManager.loadURL();
                        }
                        break;
                    case R.id.spSelMember:
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        spMemberType.setOnItemSelectedListener(onItemSelectedListener);
        getCT_Types();
        ArrayAdapter<CT_TypeBean> ct_typeBeanAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, ct_typeBeans);
        spMemberType.setAdapter(ct_typeBeanAdapter);

        btnAdMember.setOnClickListener(v -> {
            String ctmType = ct_typeBeans.get(spMemberType.getSelectedItemPosition()).kay;

            if(ctmType.equalsIgnoreCase(CTM_TYPE_OTHER)){
                String ctmName = etCTM_Name.getText().toString();
                String ctmPhone = etCTM_Phone.getText().toString();
                if(ctmName.isEmpty()){
                    etCTM_Name.setError("Please enter name");
                    ((ActivityCarePlanDetail)activity).customToast.showToast("Please enter name",0,0);
                    return;
                }
                if(ctmPhone.isEmpty()){
                    etCTM_Phone.setError("Please enter phone no");
                    ((ActivityCarePlanDetail)activity).customToast.showToast("Please enter phone no",0,0);
                    return;
                }
                if(DATA.imagePath.isEmpty()){
                    ((ActivityCarePlanDetail)activity).customToast.showToast("Please choose image",0,0);
                    return;
                }

                addTeamMember(ctmType, "", ctmName,ctmPhone, DATA.imagePath);
            }else {
                String ctmMembers = ct_selMemberBeans.get(spSelMember.getSelectedItemPosition()).id;
                addTeamMember(ctmType,ctmMembers,"","","");
            }
        });

        ivCancel.setOnClickListener(v -> dialogAddTeamMember.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogAddTeamMember.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogAddTeamMember.setCanceledOnTouchOutside(false);
        dialogAddTeamMember.show();
        dialogAddTeamMember.getWindow().setAttributes(lp);

//        WindowManager.LayoutParams a = dialogAddTeamMember.getWindow().getAttributes();//this code is to remove bacgrond dimness if a.dimAmount = 0;
//        a.dimAmount = 80;
//        dialogAddTeamMember.getWindow().setAttributes(a);
    }
    ArrayList<CT_TypeBean> ct_typeBeans;
    ArrayList<CT_TypeBean> getCT_Types(){
        ct_typeBeans = new ArrayList<>();
        ct_typeBeans.add(new CT_TypeBean("doctor","Doctor"));
        ct_typeBeans.add(new CT_TypeBean("specialist","Specialist"));
        ct_typeBeans.add(new CT_TypeBean("nurse","Care Provider"));
        ct_typeBeans.add(new CT_TypeBean("nursing_home","Nursing Home"));
        ct_typeBeans.add(new CT_TypeBean("other","Other"));
        return ct_typeBeans;
    }

    ArrayList<CT_SelMemberBean> ct_selMemberBeans;
    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if(apiName.contains(ApiManager.CARE_TEAM_BY_TYPE)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                ct_selMemberBeans = new ArrayList<>();
                CT_SelMemberBean ct_selMemberBeam;
                Gson gson = new Gson();
                for (int i = 0; i < data.length(); i++) {
                    ct_selMemberBeam = gson.fromJson(data.getJSONObject(i)+"",CT_SelMemberBean.class);
                    ct_selMemberBeans.add(ct_selMemberBeam);
                    ct_selMemberBeam = null;
                }
                ArrayAdapter<CT_SelMemberBean> ct_selMemberBeamAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, ct_selMemberBeans);
                spSelMember.setAdapter(ct_selMemberBeamAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
                ((ActivityCarePlanDetail)activity).customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.ADD_TEAM_MEMBER)){
            try {
                //{"status":"error","message":"Team Member Already Added"}
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");
                //result: {"status":"success","message":"Team Member Added","data":{"careplan_id":"2","care_team_member_type":"nursing_home","created_at":"2018-07-08 11:32:55","care_team_members":"191","image":"noimage.jpg"},"site_url":"https:\/\/www.onlinecare.com\/odev\/images"}

                if(dialogAddTeamMember != null){
                    dialogAddTeamMember.dismiss();
                }

                ((ActivityCarePlanDetail)activity).customToast.showToast(jsonObject.getString("message"),0,1);

                if(status.equalsIgnoreCase("success")){
                    ((ActivityCarePlanDetail)activity).getCarePlanById();
                }else {
                    //((ActivityCarePlanDetail)activity).customToast.showToast(content,0,1);
                }
                /*AlertDialog alertDialog =
					new AlertDialog.Builder(activity).setTitle(getString(R.string.app_name))
					.setMessage(jsonObject.getString("message"))
					.setPositiveButton("Done",null).create();
			alertDialog.setOnDismissListener(dialog -> {
                if(status.equalsIgnoreCase("success")){
                    ((ActivityCarePlanDetail)activity).getCarePlanById();
                }
            });
			alertDialog.show();*/
            } catch (JSONException e) {
                e.printStackTrace();
                ((ActivityCarePlanDetail)activity).customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }

    //-----------------------type = doctor,specialist,nursing_home,nurse,other
    void addTeamMember(String care_team_member_type,String care_team_members,String careteam_member_name,String careteam_member_phone,String careteam_member_image){
        RequestParams params = new RequestParams();

        params.put("careplan_id", ActivityCarePlan.slectedCareID);

        params.put("care_team_member_type",care_team_member_type);

        if(! care_team_members.isEmpty()){
            params.put("care_team_members",care_team_members);
        }
        if(!careteam_member_name.isEmpty()){
            params.put("careteam_member_name",careteam_member_name);
        }
        if(!careteam_member_phone.isEmpty()){
            params.put("careteam_member_phone",careteam_member_phone);
        }
        if(!careteam_member_image.isEmpty()){
            try {
                params.put("careteam_member_image",new File(careteam_member_image));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        ApiManager apiManager = new ApiManager(ApiManager.ADD_TEAM_MEMBER,"post",params,FragmentCareTeam.this::fetchDataCallback, activity);
        apiManager.loadURL();
    }


    GloabalSocket gloabalSocket;
    //============Fragment methods

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DATA.print("-- fragment oncreate");

        activity = getActivity();

        gloabalSocket = new GloabalSocket(activity,this);
    }

    @Override
    public void onResume() {
        DATA.print("-- fragment onresume");
        if(DATA.isImageCaptured){
            DATA.isImageCaptured = false;

            //String path = "drawable://"+hadithBGArr.get(position);  //direct pas this path

            //Uri uri = Uri.parse("android.resource://com.gexton.hadith/drawable/bg"+(position+1));
            final String uri = Uri.fromFile(new File(DATA.imagePath)).toString();
            final String decoded = Uri.decode(uri.toString());
            if(ivCTM_Img != null){
                DATA.loadImageFromURL(decoded, R.drawable.ic_placeholder_2, ivCTM_Img);
            }
        }
        super.onResume();
    }

    @Override
    public void onStart() {
        DATA.print("-- fragment onStart");
        super.onStart();
    }

    @Override
    public void onStop() {
        DATA.print("-- fragment onStop");
        super.onStop();
    }

    @Override
    public void onPause() {
        DATA.print("-- fragment onPause");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        DATA.print("-- fragment onDestroy");
        DATA.selectedDrId = ((ActivityCarePlanDetail)activity).prefs.getString(DoctorsList.SELCTED_DR_ID_PREFS_KEY,"");//assign back .. important

        gloabalSocket.offSocket();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        DATA.print("-- fragment onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        DATA.print("-- fragment onAttach");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        DATA.print("-- fragment onDetach");
        super.onDetach();
    }


    //====Socket callback
    public void onSocketCallBack(String emitterResponse) {

        try {
            JSONObject jsonObject = new JSONObject(emitterResponse);
            String id = jsonObject.getString("id");
            String usertype = jsonObject.getString("usertype");
            String status = jsonObject.getString("status");

            if(usertype.equalsIgnoreCase("doctor")){
                if(ActivityCarePlanDetail.careTeamBeans != null){
                    for (int i = 0; i < ActivityCarePlanDetail.careTeamBeans.size(); i++) {
                        if(ActivityCarePlanDetail.careTeamBeans.get(i).doctor_id.equalsIgnoreCase(id)){
                            if(status.equalsIgnoreCase("login")){
                                ActivityCarePlanDetail.careTeamBeans.get(i).is_online = "1";
                            }else if(status.equalsIgnoreCase("logout")){
                                ActivityCarePlanDetail.careTeamBeans.get(i).is_online = "0";
                            }
                        }
                    }
                    if(careTeamAdapter != null){
                        careTeamAdapter.notifyDataSetChanged();
                    }
                }
            }

            /*if(usertype.equalsIgnoreCase("patient")){
                for (int i = 0; i < careVistBeans.size(); i++) {
                    if(careVistBeans.get(i).patient_id.equalsIgnoreCase(id)){
                        if(status.equalsIgnoreCase("login")){
                            careVistBeans.get(i).is_online = "1";
                        }else if(status.equalsIgnoreCase("logout")){
                            careVistBeans.get(i).is_online = "0";
                        }
                    }
                }
                careVisitAdapter.notifyDataSetChanged();
            }*/
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    //-------------------------------------------------Obs list---------------------------------------------------------------------------
    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        /*ActionBar ab = getSupportActionBar();
        if (ab == null) {
            return;
        }*/
        if (scrollState == ScrollState.UP) {
            /*if (ab.isShowing()) {
                ab.hide();
            }*/
            //hideViews();
        } else if (scrollState == ScrollState.DOWN) {
            /*if (!ab.isShowing()) {
                ab.show();
            }*/
            //showViews();
        }
    }

    private void hideViews() {
        if(layBottom1.getVisibility() == View.VISIBLE){
            Animation bottomDown = AnimationUtils.loadAnimation(activity, R.anim.bottom_down);
            layBottom1.startAnimation(bottomDown);
            layBottom1.setVisibility(View.GONE);
        }
    }

    private void showViews() {
        if(layBottom1.getVisibility() != View.VISIBLE){
            Animation bottomUp = AnimationUtils.loadAnimation(activity, R.anim.bottom_up);
            layBottom1.startAnimation(bottomUp);
            layBottom1.setVisibility(View.VISIBLE);
        }
    }
    //-------------------------------------------------Obs list---------------------------------------------------------------------------
}
