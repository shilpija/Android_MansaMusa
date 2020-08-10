package com.freshhome.datamodel;

public class ScheduleData {
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

    public String getSchedule_date() {
        return schedule_date;
    }

    public void setSchedule_date(String schedule_date) {
        this.schedule_date = schedule_date;
    }

    public String getScheduleItem() {
        return scheduleItem;
    }

    public void setScheduleItem(String scheduleItem) {
        this.scheduleItem = scheduleItem;
    }

    String start_time,end_time,schedule_date,scheduleItem;
}
