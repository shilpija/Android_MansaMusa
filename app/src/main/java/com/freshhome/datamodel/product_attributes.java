package com.freshhome.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class product_attributes implements Parcelable {
 public product_attributes(){

 }
    protected product_attributes(Parcel in) {
        attribute_name = in.readString();
        attribute_id = in.readString();
        attribute_type = in.readString();
        option_id = in.readString();
        is_required = in.readString();
        menu_attr = in.readString();
        arraySubAttributies = new ArrayList<subAttributes>();
        in.readList(arraySubAttributies, getClass().getClassLoader());
    }

    public static final Creator<product_attributes> CREATOR = new Creator<product_attributes>() {
        @Override
        public product_attributes createFromParcel(Parcel in) {
            return new product_attributes(in);
        }

        @Override
        public product_attributes[] newArray(int size) {
            return new product_attributes[size];
        }
    };

    public String getAttribute_name() {
        return attribute_name;
    }

    public void setAttribute_name(String attribute_name) {
        this.attribute_name = attribute_name;
    }

    public String getAttribute_id() {
        return attribute_id;
    }

    public void setAttribute_id(String attribute_id) {
        this.attribute_id = attribute_id;
    }

    //attribute_type    select,radio,checkbox
    String attribute_name;
    String attribute_id;
    String attribute_type;
    String option_id;
    String is_required;

    public String getAttribute_type() {
        return attribute_type;
    }

    public void setAttribute_type(String attribute_type) {
        this.attribute_type = attribute_type;
    }

    public String getOption_id() {
        return option_id;
    }

    public void setOption_id(String option_id) {
        this.option_id = option_id;
    }

    public String getIs_required() {
        return is_required;
    }

    public void setIs_required(String is_required) {
        this.is_required = is_required;
    }

    public String getMenu_attr() {
        return menu_attr;
    }

    public void setMenu_attr(String menu_attr) {
        this.menu_attr = menu_attr;
    }

    String menu_attr;

    public ArrayList<subAttributes> getArraySubAttributies() {
        return arraySubAttributies;
    }

    public void setArraySubAttributies(ArrayList<subAttributes> arraySubAttributies) {
        this.arraySubAttributies = arraySubAttributies;
    }

    ArrayList<subAttributes> arraySubAttributies;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(menu_attr);
        dest.writeString(is_required);
        dest.writeString(option_id);
        dest.writeString(attribute_type);
        dest.writeString(attribute_name);
        dest.writeString(attribute_id);
        dest.writeList(arraySubAttributies);
    }
}
