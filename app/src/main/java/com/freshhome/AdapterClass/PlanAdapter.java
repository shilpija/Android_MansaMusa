package com.freshhome.AdapterClass;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.R;
import com.freshhome.ScheduleDayActivity;
import com.freshhome.datamodel.Plans;
import com.freshhome.fragments.PlanFragment;

import java.util.ArrayList;


/**
 * Created by HP on 01-05-2018.
 */

public class PlanAdapter extends BaseAdapter {
    Context context;
    ArrayList<Plans> arrayplans;
    LayoutInflater inflater;
    PlanFragment frag_plan;

    public PlanAdapter(Context context, ArrayList<Plans> arrayplans, PlanFragment frag_plan) {
        this.context = context;
        this.arrayplans = arrayplans;
        inflater = LayoutInflater.from(this.context);
        this.frag_plan = frag_plan;
    }

    @Override
    public int getCount() {
        return arrayplans.size();
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
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        final MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.single_row_plan, viewGroup, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.plan_name.setText(arrayplans.get(i).getPlan_name());
        mViewHolder.plan_price.setText(arrayplans.get(i).getPlan_price());
        mViewHolder.plan_duration.setText("For " + arrayplans.get(i).getPlan_duration() + " Months");

        mViewHolder.clickhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDetailDialog(arrayplans.get(i).getPlan_description());
            }
        });

        mViewHolder.radiobutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                frag_plan.enable_proceed_btn();
            }
        });
        return convertView;
    }

    private void ShowDetailDialog(final String data) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_plansdetail);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView text_details = (TextView) dialog.findViewById(R.id.text_details);
        text_details.setText(Html.fromHtml(data));
        TextView text_close = (TextView) dialog.findViewById(R.id.text_close);
        text_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private class MyViewHolder {
        TextView clickhere, plan_duration, plan_price, plan_name;
        LinearLayout linear_layout;
        RadioButton radiobutton;

        public MyViewHolder(View convertView) {
            clickhere = (TextView) convertView.findViewById(R.id.clickhere);
            plan_duration = (TextView) convertView.findViewById(R.id.plan_duration);
            plan_price = (TextView) convertView.findViewById(R.id.plan_price);
            plan_name = (TextView) convertView.findViewById(R.id.plan_name);

            radiobutton = (RadioButton) convertView.findViewById(R.id.radiobutton);

        }
    }
}