package com.freshhome.WeeklyCustomcalendar;

import org.joda.time.DateTime;


public interface OnWeekChangeListener {

    void onWeekChange(DateTime firstDayOfTheWeek, boolean forward);
}
