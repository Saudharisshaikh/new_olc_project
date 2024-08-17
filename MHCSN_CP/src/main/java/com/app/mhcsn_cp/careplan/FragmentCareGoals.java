package com.app.mhcsn_cp.careplan;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.api.ApiCallBack;
import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.util.CustomAnimations;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.DatePickerFragment;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCareGoals.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentCareGoals#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCareGoals extends Fragment implements ApiCallBack{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentCareGoals() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCareGoals.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCareGoals newInstance(String param1, String param2) {
        FragmentCareGoals fragment = new FragmentCareGoals();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public static Fragment newInstance() {
        Fragment fragment = new FragmentCareGoals();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
       // args.putString(ARG_PARAM2, param2);
       // fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if(parentActivity == null){
            parentActivity = getActivity();
        }
    }

    Activity parentActivity;

    ListView lvCareGoals;
    TextView tvNoGoals;
    Button btnAddCareGoal;
    Spinner spGoalType;
    String [] goalTypeArr = {"All","Accomplished Goals"};
    ArrayList<CP_GoalBean> cp_goalBeansFiltered;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_care_goals, container, false);

        lvCareGoals = rootView.findViewById(R.id.lvCareGoals);
        tvNoGoals = rootView.findViewById(R.id.tvNoGoals);
        spGoalType = rootView.findViewById(R.id.spGoalType);

        btnAddCareGoal = rootView.findViewById(R.id.btnAddCareGoal);



        btnAddCareGoal.setOnClickListener(v -> showAddGoalDialog());

        cp_goalBeansFiltered = new ArrayList<>();

        spGoalType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(ActivityCarePlanDetail.cp_goalBeans != null){
                    cp_goalBeansFiltered.clear();
                    if(position == 0){
                        for (int i = 0; i < ActivityCarePlanDetail.cp_goalBeans.size(); i++) {
                            cp_goalBeansFiltered.add(ActivityCarePlanDetail.cp_goalBeans.get(i));
                        }
                    }else {
                        for (int i = 0; i < ActivityCarePlanDetail.cp_goalBeans.size(); i++) {
                            if(ActivityCarePlanDetail.cp_goalBeans.get(i).is_accumplish.equalsIgnoreCase("1")){
                                cp_goalBeansFiltered.add(ActivityCarePlanDetail.cp_goalBeans.get(i));
                            }
                        }
                    }
                    lvCareGoals.setAdapter(new CPGoalsAdapter(parentActivity,cp_goalBeansFiltered));
                    if(cp_goalBeansFiltered.isEmpty()){
                        tvNoGoals.setVisibility(View.VISIBLE);
                    }else {
                        tvNoGoals.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> spGoalTypeAdapter = new ArrayAdapter<>(parentActivity, android.R.layout.simple_dropdown_item_1line, goalTypeArr);
        spGoalType.setAdapter(spGoalTypeAdapter);


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        parentActivity = (Activity) context;

        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    //========================================================
    Dialog dialogAddGoal;
    String [] priorityArr = {"-- Select Priority --", "High","Medium","Low"};
    String [] yesNoArr = {"-- Select --" , "Yes", "No"};
    ArrayList<CareTeamBean> careTeamBeansForSpinner;
    public void showAddGoalDialog(){
        dialogAddGoal = new Dialog(parentActivity,R.style.TransparentThemeH4B);
        dialogAddGoal.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddGoal.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogAddGoal.setContentView(R.layout.dialog_add_goal);

        ImageView ivCancel = dialogAddGoal.findViewById(R.id.ivCancel);
        Button btnAddGoal = dialogAddGoal.findViewById(R.id.btnAddGoal);

        Spinner spAddGoalPriority = dialogAddGoal.findViewById(R.id.spAddGoalPriority);
        Spinner spAddGoalCareMgr = dialogAddGoal.findViewById(R.id.spAddGoalCareMgr);
        Spinner spAddGoalSuggest = dialogAddGoal.findViewById(R.id.spAddGoalSuggest);

        EditText etAddGoalName = dialogAddGoal.findViewById(R.id.etAddGoalName);
        EditText etAddGoalDateSet = dialogAddGoal.findViewById(R.id.etAddGoalDateSet);
        EditText etAddGoalIfSuggestNoWhy = dialogAddGoal.findViewById(R.id.etAddGoalIfSuggestNoWhy);
        EditText etAddGoalContctDate = dialogAddGoal.findViewById(R.id.etAddGoalContctDate);
        EditText etAddGoalWhoContacted = dialogAddGoal.findViewById(R.id.etAddGoalWhoContacted);
        EditText etAddGoalDiscussions = dialogAddGoal.findViewById(R.id.etAddGoalDiscussions);

        etAddGoalDateSet.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(etAddGoalDateSet);
            newFragment.show(((ActivityCarePlanDetail)parentActivity).getSupportFragmentManager(), "datePicker");
        });
        etAddGoalContctDate.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(etAddGoalContctDate);
            newFragment.show(((ActivityCarePlanDetail)parentActivity).getSupportFragmentManager(), "datePicker");
        });


        ArrayAdapter<String> spPriorityAdapter = new ArrayAdapter<>(parentActivity, android.R.layout.simple_dropdown_item_1line, priorityArr);
        spAddGoalPriority.setAdapter(spPriorityAdapter);
        ArrayAdapter<String> spYesNoAdapter = new ArrayAdapter<>(parentActivity, android.R.layout.simple_dropdown_item_1line, yesNoArr);
        spAddGoalSuggest.setAdapter(spYesNoAdapter);

        careTeamBeansForSpinner = new ArrayList<>();
        careTeamBeansForSpinner.add(new CareTeamBean("", "-- Select --","","","","","","","","",
                "","","","","","","",""));
        if(ActivityCarePlanDetail.careTeamBeans != null){
            careTeamBeansForSpinner.addAll(ActivityCarePlanDetail.careTeamBeans);
        }
        ArrayAdapter<CareTeamBean> spCareMgrAdapter = new ArrayAdapter<>(parentActivity, android.R.layout.simple_dropdown_item_1line, careTeamBeansForSpinner);
        spAddGoalCareMgr.setAdapter(spCareMgrAdapter);


        btnAddGoal.setOnClickListener(v -> {
            if(spAddGoalPriority.getSelectedItemPosition() == 0){
                ((ActivityCarePlanDetail)parentActivity).customToast.showToast("Please select goal priority level",0,0);
                spAddGoalPriority.requestFocus();
                new CustomAnimations().shakeAnimate(spAddGoalPriority, 1000, spAddGoalPriority);
                return;
            }
            if(spAddGoalSuggest.getSelectedItemPosition() == 0){
                ((ActivityCarePlanDetail)parentActivity).customToast.showToast("Please select an option",0,0);
                spAddGoalSuggest.requestFocus();
                new CustomAnimations().shakeAnimate(spAddGoalSuggest, 1000, spAddGoalSuggest);
                return;
            }
            if(spAddGoalCareMgr.getSelectedItemPosition() == 0){
                ((ActivityCarePlanDetail)parentActivity).customToast.showToast("Please select an care manager",0,0);
                spAddGoalCareMgr.requestFocus();
                new CustomAnimations().shakeAnimate(spAddGoalCareMgr, 1000, spAddGoalCareMgr);
                return;
            }

            String goal_priority = priorityArr[spAddGoalPriority.getSelectedItemPosition()];
            String suggest_this_goal = yesNoArr[spAddGoalSuggest.getSelectedItemPosition()];
            String goal_case_manager = careTeamBeansForSpinner.get(spAddGoalCareMgr.getSelectedItemPosition()).id;//doctor_id

            String goal = etAddGoalName.getText().toString();
            String date_goal_set = etAddGoalDateSet.getText().toString();
            String who_set_this_goal = etAddGoalIfSuggestNoWhy.getText().toString();
            String contact_date = etAddGoalContctDate.getText().toString();
            String who_was_contacted = etAddGoalWhoContacted.getText().toString();
            String discussion_related_to_goal = etAddGoalDiscussions.getText().toString();

            RequestParams params = new RequestParams();
            params.put("goal_priority",goal_priority);
            params.put("suggest_this_goal",suggest_this_goal);
            params.put("goal_case_manager",goal_case_manager);
            params.put("goal",goal);
            params.put("date_goal_set",date_goal_set);
            params.put("who_set_this_goal",who_set_this_goal);
            params.put("contact_date",contact_date);
            params.put("who_was_contacted",who_was_contacted);
            params.put("discussion_related_to_goal",discussion_related_to_goal);
            params.put("careplan_id", ActivityCarePlan.slectedCareID);
            //params.put("patient_id", DATA.selectedUserCallId);
            params.put("doctor_id", ((ActivityCarePlanDetail)parentActivity).prefs.getString("id", ""));

            ApiManager apiManager = new ApiManager(ApiManager.ADD_GOAL,"post",params,FragmentCareGoals.this::fetchDataCallback, parentActivity);
            apiManager.loadURL();

        });

        ivCancel.setOnClickListener(v -> dialogAddGoal.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogAddGoal.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogAddGoal.setCanceledOnTouchOutside(false);
        dialogAddGoal.show();
        dialogAddGoal.getWindow().setAttributes(lp);

    }


    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.ADD_GOAL)){
            //{"status":"success","message":"Goal Added"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.getString("status").equalsIgnoreCase("success")){
                    ((ActivityCarePlanDetail)parentActivity).customToast.showToast(jsonObject.getString("message"),0,0);
                    if(dialogAddGoal != null){
                        dialogAddGoal.dismiss();
                    }
                    ((ActivityCarePlanDetail)parentActivity).lvDrawer.performItemClick(
                            ((ActivityCarePlanDetail)parentActivity).lvDrawer, 2,
                            ((ActivityCarePlanDetail)parentActivity).lvDrawer.getItemIdAtPosition(2));//to reload data
                }
            } catch (JSONException e) {
                e.printStackTrace();
                ((ActivityCarePlanDetail)parentActivity).customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }
}
