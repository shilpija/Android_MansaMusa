package com.freshhome.datamodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.internal.ParcelableSparseArray;

public class MenuSupplier implements Parcelable {

    String id;
    String imageurl;
    String dname;
    String dstatus;
    String drating;
    String dprice;
    String dveiws;
    String davailable;
    String dpending;
    boolean ischecked = false;
    String dqtyl;
    String dtime;
    String discount;
    String collectedAmount;


    public MenuSupplier() {

    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }


    public String getCollectedAmount() {
        return collectedAmount;
    }

    public void setCollectedAmount(String collectedAmount) {
        this.collectedAmount = collectedAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getDstatus() {
        return dstatus;
    }

    public void setDstatus(String dstatus) {
        this.dstatus = dstatus;
    }

    public String getDrating() {
        return drating;
    }

    public void setDrating(String drating) {
        this.drating = drating;
    }

    public String getDprice() {
        return dprice;
    }

    public void setDprice(String dprice) {
        this.dprice = dprice;
    }

    public String getDveiws() {
        return dveiws;
    }

    public void setDveiws(String dveiws) {
        this.dveiws = dveiws;
    }

    public String getDavailable() {
        return davailable;
    }

    public void setDavailable(String davailable) {
        this.davailable = davailable;
    }

    public String getDpending() {
        return dpending;
    }

    public void setDpending(String dpending) {
        this.dpending = dpending;
    }

    public boolean isIschecked() {
        return ischecked;
    }

    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
    }


    public String getDqtyl() {
        return dqtyl;
    }

    public void setDqtyl(String dqtyl) {
        this.dqtyl = dqtyl;
    }

    public String getDtime() {
        return dtime;
    }

    public void setDtime(String dtime) {
        this.dtime = dtime;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(imageurl);
        parcel.writeString(dname);
        parcel.writeString(dstatus);
        parcel.writeString(drating);
        parcel.writeString(dprice);
        parcel.writeString(dveiws);
        parcel.writeString(davailable);
        parcel.writeString(dpending);
        parcel.writeByte((byte) (isIschecked() ? 1 : 0));
        parcel.writeString(dqtyl);
        parcel.writeString(dtime);
        parcel.writeString(discount);
        parcel.writeString(collectedAmount);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MenuSupplier createFromParcel(Parcel in) {
            return new MenuSupplier(in);
        }

        @Override
        public MenuSupplier[] newArray(int i) {
            return new MenuSupplier[i];
        }
    };

    public MenuSupplier(Parcel in) {
        id = in.readString();
        imageurl = in.readString();
        dname = in.readString();
        dstatus = in.readString();
        drating = in.readString();
        dprice = in.readString();
        dveiws = in.readString();
        davailable = in.readString();
        dpending = in.readString();
        ischecked = in.readByte() != 0;
        dqtyl = in.readString();
        dtime = in.readString();
        discount = in.readString();
        collectedAmount = in.readString();
    }
}
