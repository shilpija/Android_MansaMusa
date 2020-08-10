package com.freshhome.datamodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.CheckBox;

import java.util.ArrayList;

public class SubCategory implements Parcelable {
    String name;
    String fee;
    String id;
    String img_url;
    String hasparents;
    boolean isselected;
    CheckBox checkBox;
    ArrayList<NameID> arrayList;
    public ArrayList<NameID> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<NameID> arrayList) {
        this.arrayList = arrayList;
    }

    public SubCategory() {

    }

    public SubCategory(ArrayList<NameID> arrayList)
    {
        this.arrayList = arrayList;
    }

    protected SubCategory(Parcel in) {
        fee = in.readString();
        name = in.readString();
        id = in.readString();
        img_url = in.readString();
        hasparents = in.readString();
        isselected = in.readByte() != 0;
        arrayList = new ArrayList<NameID>();
        in.readList(arrayList, getClass().getClassLoader());
    }
    public static final Creator<SubCategory> CREATOR = new Creator<SubCategory>() {
        @Override
        public SubCategory createFromParcel(Parcel in) {
            return new SubCategory(in);
        }

        @Override
        public SubCategory[] newArray(int size) {
            return new SubCategory[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }


    public String getHasparents() {
        return hasparents;
    }

    public void setHasparents(String hasparents) {
        this.hasparents = hasparents;
    }


    public boolean isIsselected() {
        return isselected;
    }

    public void setIsselected(boolean isselected) {
        this.isselected = isselected;
    }


    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fee);
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(img_url);
        dest.writeString(hasparents);
        dest.writeByte((byte) (isselected ? 1 : 0));
        dest.writeList(arrayList);
    }
}
