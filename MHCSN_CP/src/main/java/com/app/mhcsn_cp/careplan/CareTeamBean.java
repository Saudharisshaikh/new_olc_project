package com.app.mhcsn_cp.careplan;

/**
 * Created by Engr G M on 7/6/2018.
 */

public class CareTeamBean {

    public String doctor_id;
    public String first_name;
    public String last_name;
    public String mobile;
    public String dr_image;
    public String c_image;
    public String id;
    public String careplan_id;
    public String care_team_member_type;
    public String care_team_members;
    public String careteam_member_name;
    public String careteam_member_phone;
    public String image;
    public String created_at;
    public String is_online;
    public String designation;
    public String doctor_category;
    public String current_app;


    public CareTeamBean(String doctor_id, String first_name, String last_name, String mobile,
                        String dr_image, String c_image, String id, String careplan_id, String care_team_member_type,
                        String care_team_members, String careteam_member_name, String careteam_member_phone,
                        String image, String created_at, String is_online, String designation, String doctor_category, String current_app) {
        this.doctor_id = doctor_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.mobile = mobile;
        this.dr_image = dr_image;
        this.c_image = c_image;
        this.id = id;
        this.careplan_id = careplan_id;
        this.care_team_member_type = care_team_member_type;
        this.care_team_members = care_team_members;
        this.careteam_member_name = careteam_member_name;
        this.careteam_member_phone = careteam_member_phone;
        this.image = image;
        this.created_at = created_at;
        this.is_online = is_online;
        this.designation = designation;
        this.doctor_category = doctor_category;
        this.current_app = current_app;
    }

    @Override
    public String toString() {
        if(care_team_member_type.equalsIgnoreCase(FragmentCareTeam.CTM_TYPE_OTHER)){
            return careteam_member_name;
        }else {
            return first_name+" "+last_name;
        }
    }
}
