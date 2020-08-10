package com.freshhome.datamodel;

public class Countries {
    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    String country_name;
    String nationality;
    String flag;

    public String getN_code() {
        return N_code;
    }

    public void setN_code(String n_code) {
        N_code = n_code;
    }

    String N_code;
}
