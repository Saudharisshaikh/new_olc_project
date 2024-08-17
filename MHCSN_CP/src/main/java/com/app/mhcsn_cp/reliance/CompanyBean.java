package com.app.mhcsn_cp.reliance;

public class CompanyBean {

    public String id;
    public String company_id;
    public String doctor_id;
    public String company_name;



    @Override
    public String toString() {
        return company_name;
    }




    public class AllCompanyBean {

        public String id;
        public String hospital_id;
        public String company_name;
        public String is_deleted;

        public AllCompanyBean(String id, String hospital_id, String company_name, String is_deleted) {
            this.id = id;
            this.hospital_id = hospital_id;
            this.company_name = company_name;
            this.is_deleted = is_deleted;
        }

        @Override
        public String toString() {
            return company_name;
        }
    }
}
