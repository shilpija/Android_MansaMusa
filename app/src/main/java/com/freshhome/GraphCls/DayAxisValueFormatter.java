package com.freshhome.GraphCls;

import com.github.mikephil.charting.charts.BarLineChartBase;


/**
 * Created by philipp on 02/06/16.
 */
public class DayAxisValueFormatter extends ValueFormatter {
    String date = "";
    private final String[] mDays;

    private final BarLineChartBase<?> chart;

    public DayAxisValueFormatter(BarLineChartBase<?> chart, String[] mDays) {
        this.chart = chart;
        this.mDays = mDays;
    }

    @Override
    public String getFormattedValue(float value) {

//        int days = (int) value;
//
//        int year = determineYear(days);
//
//        int month = determineMonth(days);
//        String monthName = weeks[month % weeks.length];
//        String yearName = String.valueOf(year);
//
//        if (chart.getVisibleXRange() > 30 * 6) {
//
//            return monthName + " " + yearName;
//        } else {
//
//            int dayOfMonth = determineDayOfMonth(days, month + 12 * (year - 2016));
//
//            return dayOfMonth == 0 ? "" : dayOfMonth + "/" + monthName;
//        }

        if (value < mDays.length) {
            date = mDays[(int) value];
        }
        return date;
//        }
    }

    private int getDaysForMonth(int month, int year) {

        // month is 0-based

        if (month == 1) {
            boolean is29Feb = false;

            if (year < 1582)
                is29Feb = (year < 1 ? year + 1 : year) % 4 == 0;
            else if (year > 1582)
                is29Feb = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);

            return is29Feb ? 29 : 28;
        }

        if (month == 3 || month == 5 || month == 8 || month == 10)
            return 30;
        else
            return 31;
    }

    private int determineMonth(int dayOfYear) {

        int month = -1;
        int days = 0;

        while (days < dayOfYear) {
            month = month + 1;

            if (month >= 12)
                month = 0;

            int year = determineYear(days);
            days += getDaysForMonth(month, year);
        }

        return Math.max(month, 0);
    }

    private int determineDayOfMonth(int days, int month) {
        int count = 0;
        int daysForMonths = 0;

        while (count < month) {
            int year = determineYear(daysForMonths);
            daysForMonths += getDaysForMonth(count % 12, year);
            count++;
        }

        return days - daysForMonths;
    }

    private int determineYear(int days) {

        if (days <= 366)
            return 2016;
        else if (days <= 730)
            return 2017;
        else if (days <= 1094)
            return 2018;
        else if (days <= 1458)
            return 2019;
        else
            return 2020;

    }
}
