package com.freshhome.fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.GraphCls.MonthAxisValueFormatter;
import com.freshhome.GraphCls.ValueFormatter;
import com.freshhome.GraphCls.DayAxisValueFormatter;
import com.freshhome.GraphCls.WeekAxisValueFormatter;
import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.R;
import com.freshhome.SalesGraphDetail;
import com.freshhome.datamodel.Cart;
import com.freshhome.datamodel.SalesDate;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalesReportFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, OnChartValueSelectedListener, View.OnClickListener, OnChartGestureListener {
    //    ListView salesList;
    ApiInterface apiInterface;
    Spinner salesspinner;
    ImageView spinner_arrow;
    LinearLayout linearTimePeriod;
    List<String> spinner_array;
    UserSessionManager sessionManager;
    private BarChart chart;
    //    private SeekBar seekBarX, seekBarY;
//    private TextView tvX, tvY;
    TextView text_per, text_this, text_timeperiod, text_total_earnings, text_totalrevenue, text_plan_fee, text_kitchenrevenue,
            text_totalsold, text_per_profit_loss, text_bestday, text_bestmeal;
    ArrayList<SalesDate> arraySalesReport;
    String isWeek = "days"; //day 0 week 1 month 2
    View v;
    String[] mWeek, mMonths, mDays;

    public SalesReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_sales_report, container, false);
        spinner_array = new ArrayList<>();
        arraySalesReport = new ArrayList<>();

        sessionManager = new UserSessionManager(getActivity());
        apiInterface = ApiClient.getInstance().getClient();
        MainActivity_NavDrawer.heading.setText(R.string.salesreport);
        MainActivity_NavDrawer.image_addmenu.setVisibility(View.GONE);
        linearTimePeriod = (LinearLayout) v.findViewById(R.id.linearTimePeriod);
        linearTimePeriod.setVisibility(View.GONE);
        text_timeperiod = (TextView) v.findViewById(R.id.text_timeperiod);
        text_timeperiod.setOnClickListener(this);
        text_this = (TextView) v.findViewById(R.id.text_this);
        text_per = (TextView) v.findViewById(R.id.text_per);
        spinner_arrow = (ImageView) v.findViewById(R.id.spinner_arrow);
        spinner_arrow.setOnClickListener(this);
        salesspinner = (Spinner) v.findViewById(R.id.salesspinner);

        text_total_earnings = (TextView) v.findViewById(R.id.text_total_earnings);
        text_totalrevenue = (TextView) v.findViewById(R.id.text_totalrevenue);
        text_plan_fee = (TextView) v.findViewById(R.id.text_plan_fee);
        text_kitchenrevenue = (TextView) v.findViewById(R.id.text_kitchenrevenue);
        text_totalsold = (TextView) v.findViewById(R.id.text_totalsold);
        text_per_profit_loss = (TextView) v.findViewById(R.id.text_per_profit_loss);
        text_bestday = (TextView) v.findViewById(R.id.text_bestday);
        text_bestmeal = (TextView) v.findViewById(R.id.text_bestmeal);

//        //chart
//        tvX = (TextView) v.findViewById(R.id.tvXMax);
//        tvX.setTextSize(10);
//        tvY = (TextView) v.findViewById(R.id.tvYMax);
//        seekBarX = (SeekBar) v.findViewById(R.id.seekBar1);
//        seekBarY = (SeekBar) v.findViewById(R.id.seekBar2);
        chart = (BarChart) v.findViewById(R.id.chart1);
        showDateDilaog();

        return v;
    }


    private void setSalesSpinner() {
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner_sales, spinner_array);
        salesspinner.setAdapter(spinner_adapter);
        salesspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                text_per.setText("Total Revenue");
                text_this.setText("this " + spinner_array.get(position));
                if (salesspinner.getSelectedItem().equals("Months")) {
                    isWeek = "month";
                    setUpChart();
                    setMonth();
                } else if (salesspinner.getSelectedItem().equals("Weeks")) {
                    isWeek = "weeks";
                    setUpChart();
                    setWeek();
                } else {
                    //day
                    isWeek = "days";
                    setUpChart();
                    setDay();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showDateDilaog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_salesreport_selectdate);
        dialog.setCanceledOnTouchOutside(false);
        TextView text_submit = (TextView) dialog.findViewById(R.id.text_submit);
        final TextView text_to = (TextView) dialog.findViewById(R.id.text_to);
        final TextView text_from = (TextView) dialog.findViewById(R.id.text_from);
        text_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text_from.getText().toString().equalsIgnoreCase("")) {
                    CommonMethods.showtoast(getActivity(), getResources().getString(R.string.enterfrom));

                } else if (text_to.getText().toString().equalsIgnoreCase("")) {
                    CommonMethods.showtoast(getActivity(), getResources().getString(R.string.enterto));
                } else {
                    float noofDays = CommonUtilFunctions.getCountOfDays(text_from.getText().toString().toString().trim(), text_to.getText().toString().toString().trim());
                    if (noofDays <= 7) {
                        //days //orange
                        isWeek = "days";
                        spinner_array = new ArrayList<>();
                        spinner_array.add("Days");
                        MainActivity_NavDrawer.heading.setText(R.string.dailysales);

                    } else if (noofDays <= 30) {
                        //weeks //multi
                        isWeek = "weeks";
                        spinner_array = new ArrayList<>();
                        spinner_array.add("Weeks");
                        MainActivity_NavDrawer.heading.setText(R.string.weeklysales);
                        //spinner_array.add("Days");

                    } else if (noofDays > 30) {
                        //months//orange
                        isWeek = "month";
                        spinner_array = new ArrayList<>();
                        spinner_array.add("Months");
                        MainActivity_NavDrawer.heading.setText(R.string.monthysales);
//                        spinner_array.add("Weeks");
                        //spinner_array.add("Days");

                    }
                    text_timeperiod.setText(text_from.getText().toString() + " - " + text_to.getText().toString());
                    linearTimePeriod.setVisibility(View.VISIBLE);

                    if (sessionManager.isLoggedIn()) {
                        if (CommonMethods.checkConnection()) {
                            getSalesData(text_from.getText().toString().trim(), text_to.getText().toString().trim(), isWeek);
                        } else {
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
                        }
                    } else {
                        setSalesSpinner();
                    }

                    dialog.dismiss();

                }
            }
        });

        text_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!text_from.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.DatePickerToDialog(getActivity(), text_to, text_from.getText().toString().trim());
                } else {
                    CommonMethods.showtoast(getActivity(), getResources().getString(R.string.enterfrom));
                }
            }
        });

        text_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtilFunctions.DatePickerFromDialog(getActivity(), text_from);
                text_to.setText("");
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void getSalesData(String start_time, String end_time, final String isWeek) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.GetSalesReportData(start_time, end_time, isWeek);

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            arraySalesReport = new ArrayList<>();
                            JSONArray jarray = object.getJSONArray("results");
                            mWeek = new String[jarray.length()];
                            mDays = new String[jarray.length()];
                            mMonths = new String[jarray.length()];
                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject obj_i = jarray.getJSONObject(i);
                                SalesDate salesDate = new SalesDate();
                                salesDate.setTitle(obj_i.getString("title"));
                                salesDate.setTotal(CommonUtilFunctions.NullToZero(obj_i.getString("total")));
//                                salesDate.setCurrency(obj_i.getString("currency"));
                                salesDate.setTotal_products_sale(CommonUtilFunctions.NullToZero(obj_i.getString("total_products_sale")));
                                salesDate.setStart_date(obj_i.getString("start_date"));
                                salesDate.setEnd_date(obj_i.getString("end_date"));
                                salesDate.setTotal_orders(obj_i.getString("total_orders"));

                                if (isWeek.equalsIgnoreCase("month")) {
                                    //month  //will return month name in eg Jan
                                    salesDate.setMonth(CommonMethods.getMonth(obj_i.getString("title")));
                                    mMonths[i] = CommonMethods.getMonth(obj_i.getString("title"));
                                }

                                if (isWeek.equalsIgnoreCase("weeks")) {
                                    //weeks eg week 12
                                    salesDate.setWeek(obj_i.getString("title"));
                                    mWeek[i] = obj_i.getString("title");
                                }

                                if (isWeek.equalsIgnoreCase("days")) {
                                    //days //will return date in eg 16 mon
                                    salesDate.setDate(CommonMethods.getDateDay(obj_i.getString("title")));
                                    mDays[i] = CommonMethods.getDateDay(obj_i.getString("title"));
                                }
                                arraySalesReport.add(salesDate);
                            }

                            JSONObject obj_total = object.getJSONObject("total_data");
                            text_total_earnings.setText(obj_total.getString("default_currency") + " " + CommonUtilFunctions.NullToZero(obj_total.getString("total_revenue")));
                            text_totalrevenue.setText(obj_total.getString("default_currency") + " " + CommonUtilFunctions.NullToZero(obj_total.getString("total_revenue")));
                            text_plan_fee.setText(obj_total.getString("default_currency") + " " + CommonUtilFunctions.NullToZero(obj_total.getString("my_plan_fee")));
                            text_kitchenrevenue.setText(obj_total.getString("default_currency") + " " + CommonUtilFunctions.NullToZero(obj_total.getString("my_kitchen_revenue")));
                            text_totalsold.setText(CommonUtilFunctions.NullToZero(obj_total.getString("total_meals_sold")));
//                            text_per_profit_loss.setText(CommonUtilFunctions.NullToZero(obj_total.getString("")));
                            text_bestday.setText(obj_total.getString("best_selling_day"));
                            text_bestmeal.setText(obj_total.getString("best_selling_product"));

                            setSalesSpinner();

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                call.cancel();
                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
            }
        });
    }

    private void setUpChart() {
        chart = (BarChart) v.findViewById(R.id.chart1);
        chart.clear();
//        seekBarX.setMax(10);
//        seekBarX.setOnSeekBarChangeListener(this);
//        seekBarY.setOnSeekBarChangeListener(this);

        chart.setOnChartValueSelectedListener(this);
        chart.setOnChartGestureListener(this);
        chart.getDescription().setEnabled(false);
        chart.setPinchZoom(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

//        seekBarX.setProgress(20);
//        seekBarY.setProgress(100);
    }

    private void setWeek() {
        ValueFormatter xAxisFormatter = new WeekAxisValueFormatter(chart, mWeek);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        LargeValueFormatter custom = new LargeValueFormatter("AED");
        YAxis leftAxis = chart.getAxisLeft();
        //  leftAxis.setTextSize(6);
        //  leftAxis.setValueFormatter(custom);
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setDrawGridLines(false);
        // add a nice and smooth animation
        chart.animateY(1500);
        chart.getLegend().setEnabled(false);

        setData("1");
    }

    private void setDay() {
        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart, mDays);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        LargeValueFormatter custom = new LargeValueFormatter("AED");
        YAxis leftAxis = chart.getAxisLeft();
        //  leftAxis.setTextSize(6);
        //  leftAxis.setValueFormatter(custom);
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setDrawGridLines(false);
        // add a nice and smooth animation
        chart.animateY(1500);
        chart.getLegend().setEnabled(false);

        setData("0");
    }

    private void setMonth() {
        ValueFormatter xAxisFormatter = new MonthAxisValueFormatter(chart, mMonths);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        LargeValueFormatter custom = new LargeValueFormatter("AED");
        YAxis leftAxis = chart.getAxisLeft();
        // leftAxis.setTextSize(6);
        //leftAxis.setValueFormatter(custom);
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setDrawGridLines(false);
        // add a nice and smooth animation
        chart.animateY(1500);
        chart.getLegend().setEnabled(false);
        setData("2");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//        int groupCount = seekBarX.getProgress() + 1;
//        int startYear = 2017;
//        int endYear = startYear + groupCount;
//        tvX.setText(String.format(Locale.ENGLISH, "%d-%d", startYear, endYear));
//        tvY.setText(String.valueOf(seekBarY.getProgress()));
//        setData(groupCount);
    }

    private void setData(String ismonth) {
        ArrayList<BarEntry> values1 = new ArrayList<>();
        for (int i = 0; i < arraySalesReport.size(); i++) {
            values1.add(new BarEntry(i, Float.parseFloat(arraySalesReport.get(i).getTotal())));
        }

        BarDataSet set1;

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values1);
            set1.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return String.valueOf((int) value);
                }
            });
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            // create 4 DataSets
            Integer day_color = Color.rgb(102, 205, 170);
            Integer week_color = Color.rgb(32, 178, 170);
            Integer month_color = Color.rgb(0, 139, 139);

            ArrayList<Integer> color_array = new ArrayList<>();
            color_array.add(Color.rgb(255, 128, 64));
            color_array.add(Color.rgb(24, 149, 6));
            color_array.add(Color.rgb(245, 240, 16));
            color_array.add(Color.rgb(0, 0, 255));

            set1 = new BarDataSet(values1, "Company A");
            if (isWeek.equalsIgnoreCase("days")) {
                //DAY
                set1.setColors(day_color);
            } else if (isWeek.equalsIgnoreCase("weeks")) {
                //WEEK
                set1.setColors(week_color);
            } else {
                //MONTH
                set1.setColor(month_color);
            }
            BarData data = new BarData(set1);
            chart.setData(data);
            chart.setFitBars(true);
        }

        chart.invalidate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.spinner_arrow:
                salesspinner.performClick();
                break;

            case R.id.text_timeperiod:
                showDateDilaog();
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
//        https://stackoverflow.com/questions/39212590/mpandroidchart-long-click-listener
        if (e.getY() != 0.0) {
            if (e.getX() < arraySalesReport.size())
                if (salesspinner.getSelectedItem().toString().equalsIgnoreCase("Months")) {
                    Intent intent = new Intent(getActivity(), SalesGraphDetail.class);
                    intent.putExtra("value", "weeks");
                    intent.putExtra("start_date", arraySalesReport.get((int) (e.getX())).getStart_date());
                    intent.putExtra("end_date", arraySalesReport.get((int) (e.getX())).getEnd_date());
                    startActivity(intent);
                } else if (salesspinner.getSelectedItem().toString().equalsIgnoreCase("Weeks")) {
                    Intent intent = new Intent(getActivity(), SalesGraphDetail.class);
                    intent.putExtra("value", "days");
                    intent.putExtra("start_date", arraySalesReport.get((int) (e.getX())).getStart_date());
                    intent.putExtra("end_date", arraySalesReport.get((int) (e.getX())).getEnd_date());
                    startActivity(intent);
                }
        }

    }

    @Override
    public void onNothingSelected() {
        Log.i("Activity", "Nothing selected.");
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {


    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }
}
