package com.freshhome.datamodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.CheckBox;

import java.io.Serializable;
import java.util.ArrayList;

public class NameID implements Parcelable {

    public NameID() {
    }

    protected NameID(Parcel in) {
        name = in.readString();
        id = in.readString();
        fee = in.readString();
        img_url = in.readString();
        hasparents = in.readString();
        isselected = in.readByte() != 0;
    }

    public static final Creator<NameID> CREATOR = new Creator<NameID>() {
        @Override
        public NameID createFromParcel(Parcel in) {
            return new NameID(in);
        }

        @Override
        public NameID[] newArray(int size) {
            return new NameID[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String name;
    String id;
    String fee;

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    String img_url;

    public String getHasparents() {
        return hasparents;
    }

    public void setHasparents(String hasparents) {
        this.hasparents = hasparents;
    }

    String hasparents;

    public boolean isIsselected() {
        return isselected;
    }

    public void setIsselected(boolean isselected) {
        this.isselected = isselected;
    }

    boolean isselected;

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    CheckBox checkBox;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(fee);
        dest.writeString(img_url);
        dest.writeString(hasparents);
        dest.writeByte((byte) (isselected ? 1 : 0));
    }
}
