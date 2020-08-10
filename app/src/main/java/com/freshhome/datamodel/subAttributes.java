package com.freshhome.datamodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;

public class subAttributes implements Parcelable {

    public subAttributes(){

    }
    protected subAttributes(Parcel in) {
        subAttr_name = in.readString();
        subAttr_quantity = in.readString();
        subAttr_subtract = in.readString();
        subAttr_price = in.readString();
        subAttr_price_prefix = in.readString();
        subAttr_main_name = in.readString();
        isSelected = in.readByte() != 0;
        option_id = in.readString();
    }

    public static final Creator<subAttributes> CREATOR = new Creator<subAttributes>() {
        @Override
        public subAttributes createFromParcel(Parcel in) {
            return new subAttributes(in);
        }

        @Override
        public subAttributes[] newArray(int size) {
            return new subAttributes[size];
        }
    };

    public String getSubAttr_name() {
        return subAttr_name;
    }

    public void setSubAttr_name(String subAttr_name) {
        this.subAttr_name = subAttr_name;
    }

    public String getSubAttr_quantity() {
        return subAttr_quantity;
    }

    public void setSubAttr_quantity(String subAttr_quantity) {
        this.subAttr_quantity = subAttr_quantity;
    }

    public String getSubAttr_subtract() {
        return subAttr_subtract;
    }

    public void setSubAttr_subtract(String subAttr_subtract) {
        this.subAttr_subtract = subAttr_subtract;
    }

    public String getSubAttr_price() {
        return subAttr_price;
    }

    public void setSubAttr_price(String subAttr_price) {
        this.subAttr_price = subAttr_price;
    }

    public String getSubAttr_price_prefix() {
        return subAttr_price_prefix;
    }

    public void setSubAttr_price_prefix(String subAttr_price_prefix) {
        this.subAttr_price_prefix = subAttr_price_prefix;
    }

    String subAttr_name;
    String subAttr_quantity;
    String subAttr_subtract;
    String subAttr_price;
    String subAttr_price_prefix;

    public String getSubAttr_main_name() {
        return subAttr_main_name;
    }

    public void setSubAttr_main_name(String subAttr_main_name) {
        this.subAttr_main_name = subAttr_main_name;
    }

    String subAttr_main_name;

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public RadioButton getRadioButton() {
        return radioButton;
    }

    public void setRadioButton(RadioButton radioButton) {
        this.radioButton = radioButton;
    }

    public Spinner getSpinner() {
        return spinner;
    }

    public void setSpinner(Spinner spinner) {
        this.spinner = spinner;
    }

    CheckBox checkBox;
    RadioButton radioButton;
    Spinner spinner;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    boolean isSelected;

    public String getOption_id() {
        return option_id;
    }

    public void setOption_id(String option_id) {
        this.option_id = option_id;
    }

    String option_id;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subAttr_name);
        dest.writeString(subAttr_quantity);
        dest.writeString(subAttr_subtract);
        dest.writeString(subAttr_price);
        dest.writeString(subAttr_price_prefix);
        dest.writeString(subAttr_main_name);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeString(option_id);
    }
}
