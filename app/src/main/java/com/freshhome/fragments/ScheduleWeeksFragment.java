package com.freshhome.fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshhome.AdapterClass.choosedaysAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.FlowLayout;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.R;
import com.freshhome.ScheduleDayActivity;
import com.freshhome.ScheduleEachWeekActivity;
import com.freshhome.datamodel.MenuSupplier;
import com.freshhome.datamodel.MonthDays;
import com.freshhome.datamodel.NameID;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleWeeksFragment extends Fragment implements View.OnClickListener {
    TextView text_w4date, text_w3date, text_w2date, text_w1date, text_month_name;
    LinearLayout linear_week1, linear_week2, linear_week3, linear_week4;
    //cal dates
    TextView text_dateMon, text_dateTue, text_dateWed, text_dateThu, text_dateFri, text_dateSat, text_dateSun;
    FlowLayout flow_layout_w4, flow_layout_w3, flow_layout_w2, flow_layout_w1;
    ImageView image_backward, image_forward;
    FloatingActionButton floatingActionButton;
    ArrayList<MonthDays> monthDays_array, WeekDays_array;
    String[] monthArray;
    ArrayList<String> activeMontharray;
    boolean isbackactive = false;
    ArrayList<String> monthDays;
    int currentMonth = 0;
    ArrayList<MonthDays> array_week1, array_week2, array_week3, array_week4;
    ApiInterface apiInterface;
    ArrayList<MonthDays> arrayScheduledDays;
    UserSessionManager sessionManager;

    public ScheduleWeeksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_schedule_weeks, container, false);
        MainActivity_NavDrawer.heading.setText(R.string.schedule);
        MainActivity_NavDrawer.image_addmenu.setVisibility(View.GONE);

        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(getActivity());
        arrayScheduledDays = new ArrayList<>();
        monthDays_array = new ArrayList<>();
        activeMontharray = new ArrayList<>();
        WeekDays_array = new ArrayList<>();

        array_week1 = new ArrayList<>();
        array_week2 = new ArrayList<>();
        array_week3 = new ArrayList<>();
        array_week4 = new ArrayList<>();

//        weekCalendar = (WeekCalendar) v.findViewById(R.id.weekCalendar);
//        text_week = (TextView) v.findViewById(R.id.text_week);

        text_w1date = (TextView) v.findViewById(R.id.text_w1date);
        text_w2date = (TextView) v.findViewById(R.id.text_w2date);
        text_w3date = (TextView) v.findViewById(R.id.text_w3date);
        text_w4date = (TextView) v.findViewById(R.id.text_w4date);

        flow_layout_w1 = (FlowLayout) v.findViewById(R.id.flow_layout_w1);
        flow_layout_w2 = (FlowLayout) v.findViewById(R.id.flow_layout_w2);
        flow_layout_w3 = (FlowLayout) v.findViewById(R.id.flow_layout_w3);
        flow_layout_w4 = (FlowLayout) v.findViewById(R.id.flow_layout_w4);

        monthArray = getActivity().getResources().getStringArray(R.array.month_name);
        text_month_name = (TextView) v.findViewById(R.id.text_month_name);

        setMonthName();

        image_backward = (ImageView) v.findViewById(R.id.image_backward);
        image_backward.setOnClickListener(this);
        image_forward = (ImageView) v.findViewById(R.id.image_forward);
        image_forward.setOnClickListener(this);

        floatingActionButton = (FloatingActionButton) v.findViewById(R.id.floatingActionButton);
        floatingActionButton.setVisibility(View.GONE);
        floatingActionButton.setOnClickListener(this);

        linear_week1 = (LinearLayout) v.findViewById(R.id.linear_week1);
        linear_week1.setOnClickListener(this);
        linear_week2 = (LinearLayout) v.findViewById(R.id.linear_week2);
        linear_week2.setOnClickListener(this);
        linear_week3 = (LinearLayout) v.findViewById(R.id.linear_week3);
        linear_week3.setOnClickListener(this);
        linear_week4 = (LinearLayout) v.findViewById(R.id.linear_week4);
        linear_week4.setOnClickListener(this);

        //get data from server
//        if (CommonMethods.checkConnection()) {
//            getScheduleData();
//        } else {
//            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
//        }

//      getNumbersDays();

        return v;
    }

    private void setUpWeeks(FlowLayout flowlayout, int week, int count, TextView textview) {
        WeekDays_array = new ArrayList<>();
        //set 7 days for every week execpt 4th(all remaining days)
        for (int i = count; i < monthDays_array.size(); i++) {
            if (week == 4) {
                WeekDays_array.add(monthDays_array.get(i));
            } else {
                if (WeekDays_array.size() < 7) {
                    WeekDays_array.add(monthDays_array.get(i));
                }
            }
        }
        flowlayout.removeAllViews();
        //set views on front end according to weeks
        for (int i = 0; i < WeekDays_array.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.week_day_grid_item, null);
            TextView daytext = (TextView) view.findViewById(R.id.daytext);
            if (week == 1) {
                if (WeekDays_array.get(i).getIs_selected().equalsIgnoreCase("1") && WeekDays_array.get(i).getPassed().equalsIgnoreCase("0")) {
                    daytext.setBackgroundResource(R.drawable.circle_bg_w_one_selected);
                } else {
                    daytext.setBackgroundResource(R.drawable.circle_bg_w_one);
                }
                if (WeekDays_array.get(i).getPassed().equalsIgnoreCase("0")) {
                    array_week1.add(WeekDays_array.get(i));
                }
            } else if (week == 2) {
                if (WeekDays_array.get(i).getIs_selected().equalsIgnoreCase("1") && WeekDays_array.get(i).getPassed().equalsIgnoreCase("0")) {
                    daytext.setBackgroundResource(R.drawable.circle_bg_w_two_selected);
                } else {
                    daytext.setBackgroundResource(R.drawable.circle_bg_w_two);
                }
                if (WeekDays_array.get(i).getPassed().equalsIgnoreCase("0")) {
                    array_week2.add(WeekDays_array.get(i));
                }

            } else if (week == 3) {
                if (WeekDays_array.get(i).getIs_selected().equalsIgnoreCase("1") && WeekDays_array.get(i).getPassed().equalsIgnoreCase("0")) {
                    daytext.setBackgroundResource(R.drawable.circle_bg_w_three_selected);
                } else {
                    daytext.setBackgroundResource(R.drawable.circle_bg_w_three);
                }
                if (WeekDays_array.get(i).getPassed().equalsIgnoreCase("0")) {
                    array_week3.add(WeekDays_array.get(i));
                }
            } else {
                //4
                if (WeekDays_array.get(i).getIs_selected().equalsIgnoreCase("1") && WeekDays_array.get(i).getPassed().equalsIgnoreCase("0")) {
                    daytext.setBackgroundResource(R.drawable.circle_bg_w_four_selected);
                } else {
                    daytext.setBackgroundResource(R.drawable.circle_bg_w_four);
                }
                if (WeekDays_array.get(i).getPassed().equalsIgnoreCase("0")) {
                    array_week4.add(WeekDays_array.get(i));
                }
            }
            if (WeekDays_array.get(i).getPassed().equalsIgnoreCase("1")) {
                daytext.setAlpha((float) .6);
                daytext.setClickable(false);
            }
            textview.setText(WeekDays_array.get(0).getDateNo() + " - " + WeekDays_array.get(WeekDays_array.size() - 1).getDateNo() + " " + text_month_name.getText().toString());
            daytext.setText(WeekDays_array.get(i).getDateNo());
            flowlayout.addView(view);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_forward:
                for (int i = 0; i < activeMontharray.size(); i++) {
                    if (text_month_name.getText().toString().equalsIgnoreCase(activeMontharray.get(i))) {
                        if (i + 1 < activeMontharray.size()) {
                            currentMonth = currentMonth + 1;
                            text_month_name.setText(activeMontharray.get(i + 1));
                            isbackactive = true;
                            image_backward.setVisibility(View.VISIBLE);
                            image_forward.setVisibility(View.INVISIBLE);
                        }
                        getNumbersDays(arrayScheduledDays);
                        return;
                    }
                }

//                weekCalendar.setStartDate(new DateTime().plusDays(7));
                break;

            case R.id.image_backward:
                if (isbackactive) {
                    for (int i = 0; i < activeMontharray.size(); i++) {
                        if (text_month_name.getText().toString().equalsIgnoreCase(activeMontharray.get(i))) {
                            if (i - 1 < activeMontharray.size()) {
                                if (currentMonth == 0) {
                                    currentMonth = 11;
                                } else {
                                    currentMonth = currentMonth - 1;
                                }
                                text_month_name.setText(activeMontharray.get(i - 1));
                                isbackactive = false;
                                image_backward.setVisibility(View.INVISIBLE);
                                image_forward.setVisibility(View.VISIBLE);
                            }
                            getNumbersDays(arrayScheduledDays);
                            return;
                        }
                    }

                }
//                weekCalendar.setSelectedDate(new DateTime().minusDays(7));
                break;

            case R.id.floatingActionButton:
                Intent intent = new Intent(getActivity(), ScheduleEachWeekActivity.class);
                startActivity(intent);
                break;


            case R.id.linear_week4:
                if (array_week4.size() != 0) {
//                    if (sessionManager.getSubscriptionDetails().get(UserSessionManager.KEY_STATUS).equalsIgnoreCase("active")) {
                        selectDaysDialog(array_week4, 4);
//                    } else {
//                        CommonMethods.show_buy_plan(getActivity());
//                    }
                }
                break;

            case R.id.linear_week3:
                if (array_week3.size() != 0) {
//                    if (sessionManager.getSubscriptionDetails().get(UserSessionManager.KEY_STATUS).equalsIgnoreCase("active")) {
                        selectDaysDialog(array_week3, 3);
//                    } else {
//                        CommonMethods.show_buy_plan(getActivity());
//                    }
                }
                break;

            case R.id.linear_week2:
                if (array_week2.size() != 0) {
//                    if (sessionManager.getSubscriptionDetails().get(UserSessionManager.KEY_STATUS).equalsIgnoreCase("active")) {
                        selectDaysDialog(array_week2, 2);
//                    } else {
//                        CommonMethods.show_buy_plan(getActivity());
//                    }
                }
                break;

            case R.id.linear_week1:
                if (array_week1.size() != 0) {
//                    if (sessionManager.getSubscriptionDetails().get(UserSessionManager.KEY_STATUS).equalsIgnoreCase("active")) {
                        selectDaysDialog(array_week1, 1);
//                    } else {
//                        CommonMethods.show_buy_plan(getActivity());
//                    }
                }
                break;


        }
    }

    private void setMonthName() {
        monthArray = getActivity().getResources().getStringArray(R.array.month_name);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        String month_name = month_date.format(cal.getTime());
        for (int i = 0; i < monthArray.length; i++) {
            if (monthArray[i].equalsIgnoreCase(month_name)) {
                text_month_name.setText(monthArray[i].toString());
                currentMonth = i;
                activeMontharray.add(monthArray[i]);
                if (i + 1 < monthArray.length) {
                    activeMontharray.add(monthArray[i + 1]);
                } else {
                    activeMontharray.add(monthArray[0]);
                }
            }
        }
    }

    private void getNumbersDays(ArrayList<MonthDays> arrayScheduledDays) {
        array_week1 = new ArrayList<>();
        array_week2 = new ArrayList<>();
        array_week3 = new ArrayList<>();
        array_week4 = new ArrayList<>();

        monthDays_array = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("E-MMM-dd-yyyy");
        SimpleDateFormat dayf = new SimpleDateFormat("E");
        SimpleDateFormat Nof = new SimpleDateFormat("dd");
        SimpleDateFormat selected_datef = new SimpleDateFormat("yyyy-MM-dd");

        Calendar c = Calendar.getInstance();
        int monthMaxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, currentMonth);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < maxDay; i++) {
            cal.set(Calendar.DAY_OF_MONTH, i + 1);
            MonthDays monthDays = new MonthDays();
            monthDays.setDay(dayf.format(cal.getTime()));
            monthDays.setDate(df.format(cal.getTime()));
            monthDays.setDateNo(Nof.format(cal.getTime()));
            monthDays.setIs_selected("0");
            if (cal.after(c)) {
                monthDays.setPassed("0");
            } else {
                monthDays.setPassed("1");
            }
            for (int j = 0; j < arrayScheduledDays.size(); j++) {
                if (selected_datef.format(cal.getTime()).equalsIgnoreCase(arrayScheduledDays.get(j).getDate())) {
                    monthDays.setStart_time(arrayScheduledDays.get(j).getStart_time());
                    monthDays.setEnd_time(arrayScheduledDays.get(j).getEnd_time());
                    monthDays.setStart_time(arrayScheduledDays.get(j).getStart_time());
                    monthDays.setArrayMenu(arrayScheduledDays.get(j).getArrayMenu());
                    monthDays.setSchedule_id(arrayScheduledDays.get(j).getSchedule_id());
                    monthDays.setIs_selected("1");
                }
            }
            monthDays_array.add(monthDays);
        }
        Log.e("day", monthDays_array.toString());

        //set days on ui
        setUpWeeks(flow_layout_w1, 1, 0, text_w1date);
        setUpWeeks(flow_layout_w2, 2, 7, text_w2date);
        setUpWeeks(flow_layout_w3, 3, 14, text_w3date);
        setUpWeeks(flow_layout_w4, 4, 21, text_w4date);
    }

    private void moveToNext(int s) {
        Intent intent = new Intent(getActivity(), ScheduleEachWeekActivity.class);
        if (s == 1) {
            intent.putExtra("weekarray", array_week1);
        } else if (s == 2) {
            intent.putExtra("weekarray", array_week2);
        } else if (s == 3) {
            intent.putExtra("weekarray", array_week3);
        } else if (s == 4) {
            intent.putExtra("weekarray", array_week4);
        }
        intent.putExtra("week", s);
        startActivity(intent);
//        selectDaysDialog(monthDays, Integer.parseInt(s));
    }

    private void selectDaysDialog(final ArrayList<MonthDays> array, final int week) {
        final ArrayList<MonthDays> arrayWeekdays = new ArrayList<>();
        arrayWeekdays.addAll(array);
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_selectdays);
        dialog.setCanceledOnTouchOutside(false);
        GridView dayGridView = (GridView) dialog.findViewById(R.id.dayGridView);
        TextView text_done = (TextView) dialog.findViewById(R.id.text_done);
        text_done.setVisibility(View.GONE);
        text_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int item_counts = 0;
                //save
                for (int i = 0; i < arrayWeekdays.size(); i++) {
                    if (arrayWeekdays.get(i).getIs_selected().equalsIgnoreCase("1")) {
                        item_counts = item_counts + 1;
                    }
                }
                if (item_counts != 0) {
                    moveToNext(week);
                    dialog.cancel();
                }
            }
        });
        choosedaysAdapter adapter = new choosedaysAdapter(getActivity(), arrayWeekdays, week);
        dayGridView.setAdapter(adapter);
        dayGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                if (sessionManager.isLoggedIn()) {
                    if (sessionManager.getSubscriptionDetails().get(UserSessionManager.KEY_STATUS).equalsIgnoreCase("active")) {
                        Intent i = new Intent(getActivity(), ScheduleDayActivity.class);
                        i.putParcelableArrayListExtra("date", arrayWeekdays);
                        i.putExtra("pos", position);
                        startActivity(i);
                    } else {
                        CommonMethods.show_buy_plan(getActivity());
                    }

                } else {
                    CommonMethods.ShowLoginDialog(getActivity());
                }
//                LinearLayout linearLayout_ok = (LinearLayout) view.findViewById(R.id.linear_ok);
//                if (arrayWeekdays.get(position).getIs_selected().equalsIgnoreCase("1")) {
//                    //false
//                    arrayWeekdays.get(position).setIs_selected("0");
//                    linearLayout_ok.setVisibility(View.GONE);
//                } else {
//                    //true
//                    arrayWeekdays.get(position).setIs_selected("1");
//                    linearLayout_ok.setVisibility(View.VISIBLE);
//                }
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onResume() {
        super.onResume();
        array_week1 = new ArrayList<>();
        array_week2 = new ArrayList<>();
        array_week3 = new ArrayList<>();
        array_week4 = new ArrayList<>();
        //get data from server
        //TODO : CHECK IF LOGGEDIN THEN HIT API
        if (sessionManager.isLoggedIn()) {
            if (CommonMethods.checkConnection()) {
                getScheduleData();
            } else {
                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
            }
        } else {
            getNumbersDays(arrayScheduledDays);
        }

    }


    private void getScheduleData() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetSchedule();

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
                            JSONObject obj = object.getJSONObject("success");
                            arrayScheduledDays = new ArrayList<>();
                            JSONArray jsonArray = obj.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject j_obj = jsonArray.getJSONObject(i);
                                MonthDays monthDays = new MonthDays();
                                monthDays.setSchedule_id(j_obj.getString("id"));
                                monthDays.setStart_time(j_obj.getString("start_time"));
                                monthDays.setEnd_time(j_obj.getString("end_time"));
                                monthDays.setDate(j_obj.getString("schedule_date"));
                                ArrayList<MenuSupplier> arrayScheduledItems = new ArrayList<>();
                                JSONArray array = j_obj.getJSONArray("menu_items");
                                for (int j = 0; j < array.length(); j++) {
                                    JSONObject obj_arr = array.getJSONObject(j);
                                    MenuSupplier menuItem = new MenuSupplier();
                                    menuItem.setId(obj_arr.getString("menu_id"));
                                    menuItem.setDqtyl(obj_arr.getString("qty"));
//                                    menuItem.setDprice(obj_arr.getString(""));
                                    menuItem.setImageurl(obj_arr.getString("dish_image"));
                                    menuItem.setDname(obj_arr.getString("dish_name"));
                                    arrayScheduledItems.add(menuItem);
                                    //added selected items
                                    monthDays.setArrayMenu(arrayScheduledItems);
                                }

                                //added selected days with items
                                arrayScheduledDays.add(monthDays);
                            }
                            //get all days of month
                            getNumbersDays(arrayScheduledDays);
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
                            getNumbersDays(arrayScheduledDays);
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
                    }
                } catch (
                        JSONException e)

                {
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

}
