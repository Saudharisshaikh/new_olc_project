package com.app.mhcsn_cp.careplan;

import java.util.ArrayList;

/**
 * Created by Engr G M on 7/11/2018.
 */

public class GoalProgressBean {

    public String id;
    public String goal_id;
    public String progress;
    public String explain_progress;
    public String barriers;
    public String interventions_to_overcome_barriers;
    public String self_management;
    public String referral_made;
    public String referral_to;
    public String reason_for_referral;
    public String backup_plan;
    public String are_revision_needed;
    public String what_revisions_were_made;
    public String added_by;
    public String created_at;

    public ArrayList<GoalProgressBean> goalProgressBeansSub;


    public GoalProgressBean(String id, String goal_id, String progress, String explain_progress, String barriers,
                            String interventions_to_overcome_barriers, String self_management, String referral_made,
                            String referral_to, String reason_for_referral, String backup_plan, String are_revision_needed,
                            String what_revisions_were_made, String added_by, String created_at,ArrayList<GoalProgressBean> goalProgressBeansSub) {
        this.id = id;
        this.goal_id = goal_id;
        this.progress = progress;
        this.explain_progress = explain_progress;
        this.barriers = barriers;
        this.interventions_to_overcome_barriers = interventions_to_overcome_barriers;
        this.self_management = self_management;
        this.referral_made = referral_made;
        this.referral_to = referral_to;
        this.reason_for_referral = reason_for_referral;
        this.backup_plan = backup_plan;
        this.are_revision_needed = are_revision_needed;
        this.what_revisions_were_made = what_revisions_were_made;
        this.added_by = added_by;
        this.created_at = created_at;
        this.goalProgressBeansSub = goalProgressBeansSub;
    }
}
