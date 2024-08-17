package com.app.mhcsn_cp.reliance.assessment;

import java.util.ArrayList;

public class SDHSfieldBean {


    public String category;
    public ArrayList<SDHSquestionBean> questions;




    public class SDHSquestionBean{
        public String question;
        public ArrayList<SDHSoptionBean> options;

        public boolean isAnswered = false;//for decission making

        public SDHSquestionBean(String question, ArrayList<SDHSoptionBean> options) {
            this.question = question;
            this.options = options;
        }
    }




    public class SDHSoptionBean{
        public String key;
        public int is_multi;
        public ArrayList<String> arr;

        public boolean isSelected;
        public String selectedSpinnerValue = "";

        public SDHSoptionBean(String key, int is_multi, ArrayList<String> arr) {
            this.key = key;
            this.is_multi = is_multi;
            this.arr = arr;
        }
    }
}
