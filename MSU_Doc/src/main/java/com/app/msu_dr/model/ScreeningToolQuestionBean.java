package com.app.msu_dr.model;

/**
 * Created by Engr G M on 8/5/2017.
 */

public class ScreeningToolQuestionBean {

    public String answer;
    public String question;
    public String question_id;

    public ScreeningToolQuestionBean(String answer, String question, String question_id) {
        this.answer = answer;
        this.question = question;
        this.question_id = question_id;
    }
}
