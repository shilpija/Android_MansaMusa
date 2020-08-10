package com.freshhome.datamodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.Menu;

import java.util.ArrayList;

public class MonthDays implements Parcelable {
    String passed;
    String day;
    String date;
    String dateNo;
    String schedule_id;
    String start_time, end_time;
    String is_selected;//1 for selected and 0 for unselected
    ArrayList<MenuSupplier> arrayMenu;

    public  MonthDays() {

    }

    protected MonthDays(Parcel in) {
        passed = in.readString();
        day = in.readString();
        date = in.readString();
        dateNo = in.readString();
        schedule_id = in.readString();
        start_time = in.readString();
        end_time = in.readString();
        is_selected = in.readString();
        this.arrayMenu = new ArrayList<>();
        in.readList(this.arrayMenu, MonthDays.class.getClassLoader());
    }

    public static final Creator<MonthDays> CREATOR = new Creator<MonthDays>() {
        @Override
        public MonthDays createFromParcel(Parcel in) {
            return new MonthDays(in);
        }

        @Override
        public MonthDays[] newArray(int size) {
            return new MonthDays[size];
        }
    };

    public String getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(String schedule_id) {
        this.schedule_id = schedule_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getIs_selected() {
        return is_selected;
    }

    public void setIs_selected(String is_selected) {
        this.is_selected = is_selected;
    }

    public ArrayList<MenuSupplier> getArrayMenu() {
        return arrayMenu;
    }

    public void setArrayMenu(ArrayList<MenuSupplier> arrayMenu) {
        this.arrayMenu = arrayMenu;
    }


    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateNo() {
        return dateNo;
    }

    public void setDateNo(String dateNo) {
        this.dateNo = dateNo;
    }


    public String getPassed() {
        return passed;
    }

    public void setPassed(String passed) {
        this.passed = passed;
    }


    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(passed);
        parcel.writeString(day);
        parcel.writeString(date);
        parcel.writeString(dateNo);
        parcel.writeString(schedule_id);
        parcel.writeString(start_time);
        parcel.writeString(end_time);
        parcel.writeString(is_selected);
        parcel.writeList(this.arrayMenu);

    }
}
