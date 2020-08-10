package com.freshhome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.GraphCls.DayAxisValueFormatter;
import com.freshhome.GraphCls.ValueFormatter;
import com.freshhome.GraphCls.WeekAxisValueFormatter;
import com.freshhome.datamodel.SalesDate;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalesGraphDetail extends AppCompatActivity implements View.OnClickListener, OnChartValueSelectedListener, OnChartGestureListener {
    ImageView image_back;
    private BarChart chart;
    ArrayList<SalesDate> arraySalesReport;
    String intentValue = "";
    TextView heading;
    ApiInterface apiInterface;
    TextView text_per, text_this, text_timeperiod, text_total_earnings, text_totalrevenue, text_plan_fee, text_kitchenrevenue,
            text_totalsold, text_per_profit_loss, text_bestday, text_bestmeal;
//    String isWeek = "0"; //day 0 week 1 month 2
    String[] mWeek, mMonths, mDays;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_graph_detail);
        apiInterface = ApiClient.getInstance().getClient();
        arraySalesReport = new ArrayList<>();
//        addData();
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);
        chart = (BarChart) findViewById(R.id.chart1);
        heading = (TextView) findViewById(R.id.heading);
        intentValue = getIntent().getStringExtra("value");

        text_total_earnings = (TextView) findViewById(R.id.text_total_earnings);
        text_totalrevenue = (TextView) findViewById(R.id.text_totalrevenue);
        text_plan_fee = (TextView) findViewById(R.id.text_plan_fee);
        text_kitchenrevenue = (TextView) findViewById(R.id.text_kitchenrevenue);
        text_totalsold = (TextView) findViewById(R.id.text_totalsold);
        text_per_profit_loss = (TextView) findViewById(R.id.text_per_profit_loss);
        text_bestday = (TextView) findViewById(R.id.text_bestday);
        text_bestmeal = (TextView) findViewById(R.id.text_bestmeal);

        if (CommonMethods.checkConnection()) {
            getSalesData(getIntent().getStringExtra("start_date"), getIntent().getStringExtra("end_date"), intentValue);
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(SalesGraphDetail.this, getResources().getString(R.string.internetconnection));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                SalesGraphDetail.this.finish();
                break;
        }
    }


    private void setUpChart() {
        chart = (BarChart) findViewById(R.id.chart1);
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
        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart,mDays);
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

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e.getX() < arraySalesReport.size()){
            if (intentValue.equalsIgnoreCase("weeks")) {
                Intent intent = new Intent(SalesGraphDetail.this, SalesGraphDetail.class);
                intent.putExtra("value", "days");
                intent.putExtra("start_date", arraySalesReport.get((int) (e.getX())).getStart_date());
                intent.putExtra("end_date", arraySalesReport.get((int) (e.getX())).getEnd_date());
                startActivity(intent);
            }
    }

    }

    @Override
    public void onNothingSelected() {

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
            if (intentValue.equalsIgnoreCase("days")) {
                //DAY
                set1.setColors(day_color);
            } else if (intentValue.equalsIgnoreCase("weeks")) {
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

    private void getSalesData(String start_time, String end_time, final String isWeek) {
        final ProgressDialog progressDialog = new ProgressDialog(SalesGraphDetail.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.GetSalesReportData(CommonUtilFunctions.changeDateFormatDDMMYYYY_2(start_time),
                CommonUtilFunctions.changeDateFormatDDMMYYYY_2(end_time),intentValue.toLowerCase());

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
//                                salesDate.setTotal("20");
                                salesDate.setTotal(CommonUtilFunctions.NullToZero(obj_i.getString("total")));
//                                salesDate.setCurrency(obj_i.getString("currency"));
                                salesDate.setTotal_products_sale(CommonUtilFunctions.NullToZero(obj_i.getString("total_products_sale")));
                                salesDate.setStart_date(obj_i.getString("start_date"));
                                salesDate.setEnd_date(obj_i.getString("end_date"));
                                salesDate.setTotal_orders(obj_i.getString("total_orders"));


                                if (intentValue.equalsIgnoreCase("weeks")) {
                                    //weeks eg week 12
                                    salesDate.setWeek(obj_i.getString("title"));
                                    mWeek[i] = obj_i.getString("title");
                                }

                                if (intentValue.equalsIgnoreCase("days")) {
                                    //days //will return date in eg 16 mon
                                    salesDate.setDate(CommonMethods.getDateDay(obj_i.getString("title")));
                                    mDays[i] = CommonMethods.getDateDay(obj_i.getString("title"));
                                }
                                arraySalesReport.add(salesDate);
                            }

                            JSONObject obj_total = object.getJSONObject("total_data");
                            text_total_earnings.setText(obj_total.getString("default_currency") + " " + CommonUtilFunctions.NullToZero(obj_total.getString("total_revenue")));
                            text_totalrevenue.setText(obj_total.getString("default_currency") + " " + CommonUtilFunctions.NullToZero(obj_total.getString("total_revenue")));
                            //text_plan_fee.setText(obj_total.getString("default_currency") + " " + CommonUtilFunctions.NullToZero(obj_total.getString("my_plan_fee")));
                            text_kitchenrevenue.setText(obj_total.getString("default_currency") + " " + CommonUtilFunctions.NullToZero(obj_total.getString("my_kitchen_revenue")));
                            text_totalsold.setText(CommonUtilFunctions.NullToZero(obj_total.getString("total_meals_sold")));
//                            text_per_profit_loss.setText(CommonUtilFunctions.NullToZero(obj_total.getString("")));
                            text_bestday.setText(obj_total.getString("best_selling_day"));
                            text_bestmeal.setText(obj_total.getString("best_selling_product"));

                            SetVaulesToChart();

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(SalesGraphDetail.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(SalesGraphDetail.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(SalesGraphDetail.this, getResources().getString(R.string.server_error));
            }
        });
    }

    private void SetVaulesToChart() {
        if (intentValue.equalsIgnoreCase("weeks")) {
            //week
            heading.setText("Weekly Sales Reports");
            setUpChart();
            setWeek();
        } else {
            //days
            heading.setText("Daily Sales Reports");
            setUpChart();
            setDay();
        }
    }
}
