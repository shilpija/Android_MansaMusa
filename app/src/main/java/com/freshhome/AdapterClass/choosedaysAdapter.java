package com.freshhome.AdapterClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.R;
import com.freshhome.datamodel.HomeItem;
import com.freshhome.datamodel.MonthDays;
import com.freshhome.datamodel.NameID;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by HP on 01-05-2018.
 */

public class choosedaysAdapter extends BaseAdapter {
    Context context;
    ArrayList<MonthDays> arrayHomeItem;
    LayoutInflater inflater;
    int week;

    public choosedaysAdapter(Context context, ArrayList<MonthDays> arrayHomeItem, int week) {
        this.context = context;
        this.arrayHomeItem = arrayHomeItem;
        this.week = week;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return arrayHomeItem.size();
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
            convertView = inflater.inflate(R.layout.single_row_choosedays, viewGroup, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        if (week == 1) {
            if (arrayHomeItem.get(i).getIs_selected().equalsIgnoreCase("1")) {
                mViewHolder.linear_ok.setVisibility(View.VISIBLE);
//                mViewHolder.linear_background.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.circle_bg_w_one_selected));
            } else {
                mViewHolder.linear_ok.setVisibility(View.GONE);
            }
            mViewHolder.linear_background.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.circle_bg_w_one));
        } else if (week == 2) {
            if (arrayHomeItem.get(i).getIs_selected().equalsIgnoreCase("1")) {
                mViewHolder.linear_ok.setVisibility(View.VISIBLE);
//                mViewHolder.linear_background.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.circle_bg_w_two_selected));
            } else {
                mViewHolder.linear_ok.setVisibility(View.GONE);
            }
                mViewHolder.linear_background.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.circle_bg_w_two));
        } else if (week == 3) {
            if (arrayHomeItem.get(i).getIs_selected().equalsIgnoreCase("1")) {
                mViewHolder.linear_ok.setVisibility(View.VISIBLE);
//                mViewHolder.linear_background.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.circle_bg_w_three_selected));
            } else {
                mViewHolder.linear_ok.setVisibility(View.GONE);
            }
                mViewHolder.linear_background.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.circle_bg_w_three));

        } else if (week == 4) {
            if (arrayHomeItem.get(i).getIs_selected().equalsIgnoreCase("1")) {
                mViewHolder.linear_ok.setVisibility(View.VISIBLE);
//                mViewHolder.linear_background.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.circle_bg_w_four_selected));
            } else {
                mViewHolder.linear_ok.setVisibility(View.GONE);
            }
                mViewHolder.linear_background.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.circle_bg_w_four));

        }

        mViewHolder.textday.setText(arrayHomeItem.get(i).getDay());
        mViewHolder.textdate.setText(arrayHomeItem.get(i).getDateNo());

        return convertView;
    }

    private class MyViewHolder {
        TextView textday, textdate;
        LinearLayout linear_background, linear_ok;

        public MyViewHolder(View convertView) {
            textday = (TextView) convertView.findViewById(R.id.textday);
            textdate = (TextView) convertView.findViewById(R.id.textdate);
            linear_background = (LinearLayout) convertView.findViewById(R.id.linear_background);
            linear_ok = (LinearLayout) convertView.findViewById(R.id.linear_ok);
        }
    }
}