package com.freshhome.AdapterClass;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.R;
import com.freshhome.datamodel.HomeItem;
import com.freshhome.datamodel.ScheduleData;
import com.freshhome.datamodel.ScheduleMenuItem;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by HP on 01-05-2018.
 */

public class ScheduleGridAdapter extends BaseAdapter {
    Context context;
    ArrayList<ScheduleData> arraySchedule;
    LayoutInflater inflater;
    ApiInterface apiInterface;

    public ScheduleGridAdapter(Context context, ArrayList<ScheduleData> arraySchedule) {
        this.context = context;
        this.arraySchedule = arraySchedule;
        apiInterface = ApiClient.getInstance().getClient();
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return arraySchedule.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.single_row_schedule_view, viewGroup, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.text_date.setText(CommonMethods.checkNull(CommonMethods.getDate(arraySchedule.get(i).getSchedule_date())));
        mViewHolder.text_month.setText(CommonMethods.checkNull(CommonMethods.getMonthYear(arraySchedule.get(i).getSchedule_date())));
        mViewHolder.text_day.setText(CommonMethods.checkNull(CommonMethods.getDayName(arraySchedule.get(i).getSchedule_date())));

        mViewHolder.text_starttime.setText(CommonMethods.checkNull(CommonUtilFunctions.changeTimeFormahhmm(arraySchedule.get(i).getStart_time())) + "am");
        mViewHolder.text_endtime.setText(CommonMethods.checkNull(CommonUtilFunctions.changeTimeFormahhmm(arraySchedule.get(i).getEnd_time())) + "pm");
        mViewHolder.text_available_items.setText(CommonMethods.checkNull(arraySchedule.get(i).getScheduleItem()));
        mViewHolder.text_view_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }

    private class MyViewHolder {
        TextView text_date, text_month, text_day, text_starttime, text_endtime, text_available_items, text_view_menu;


        public MyViewHolder(View convertView) {
            text_date = (TextView) convertView.findViewById(R.id.text_date);
            text_month = (TextView) convertView.findViewById(R.id.text_month);
            text_day = (TextView) convertView.findViewById(R.id.text_day);

            text_starttime = (TextView) convertView.findViewById(R.id.text_starttime);
            text_endtime = (TextView) convertView.findViewById(R.id.text_endtime);
            text_available_items = (TextView) convertView.findViewById(R.id.text_available_items);
            text_view_menu = (TextView) convertView.findViewById(R.id.text_view_menu);
        }
    }

}