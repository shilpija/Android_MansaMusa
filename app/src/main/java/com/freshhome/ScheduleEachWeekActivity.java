package com.freshhome;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.FlowLayout;
import com.freshhome.datamodel.NameID;

import java.util.ArrayList;

public class ScheduleEachWeekActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView image_back;
    ImageView image_backward, image_forward;
    LinearLayout linear_mainview;
    String week = "0";
    ArrayList<NameID> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_each_week);
        arrayList = new ArrayList<>();
        arrayList = (ArrayList<NameID>) getIntent().getSerializableExtra("weekarray");
        week = String.valueOf(getIntent().getIntExtra("week", 0));
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);

        linear_mainview = (LinearLayout) findViewById(R.id.linear_mainview);

        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).isIsselected()) {
                String day = arrayList.get(i).getName().split("-")[0];
                String date = arrayList.get(i).getName().split("-")[2] + "," + arrayList.get(i).getName().split("-")[1] + " " + arrayList.get(i).getName().split("-")[3];
                setupdata(day, date,arrayList.get(i).getName().split("-")[2]);
            }
        }
    }

    private void setupdata(final String day, final String date, final String currentDate) {
        View view = getLayoutInflater().inflate(R.layout.single_row_schedule, null);
        FlowLayout flow_layout_w1 = (FlowLayout) view.findViewById(R.id.flow_layout_w1);
        TextView text_selectmenu = (TextView) view.findViewById(R.id.text_selectmenu);
        TextView text_wdate = (TextView) view.findViewById(R.id.text_wdate);
        text_wdate.setText(date);
        TextView text_day = (TextView) view.findViewById(R.id.text_day);
        text_day.setText(day);

        final TextView text_end_date = (TextView) view.findViewById(R.id.text_end_date);
        final TextView text_start_date = (TextView) view.findViewById(R.id.text_start_date);

        LinearLayout linear_week = (LinearLayout) view.findViewById(R.id.linear_week);
        if (week.equalsIgnoreCase("1")) {
            linear_week.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_w_one));
        } else if (week.equalsIgnoreCase("2")) {
            linear_week.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_w_two));
        } else if (week.equalsIgnoreCase("3")) {
            linear_week.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_w_three));
        } else if (week.equalsIgnoreCase("4")) {
            linear_week.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_w_four));
        }

        text_selectmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ScheduleEachWeekActivity.this, SelectMenuActivity.class);
                i.putExtra("weekarray", arrayList);
                i.putExtra("date",currentDate);
                i.putExtra("start_time", text_start_date.getText().toString());
                i.putExtra("end_time", text_end_date.getText().toString());
                startActivity(i);
            }
        });

        text_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtilFunctions.timepickerdialog_to(ScheduleEachWeekActivity.this, text_end_date, text_start_date.getText().toString().trim());
            }
        });

        text_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtilFunctions.timepickerdialog(ScheduleEachWeekActivity.this, text_start_date);
            }
        });
//add
//        for (int j = 0; j < 5; j++) {
//            View view_inside = getLayoutInflater().inflate(R.layout.single_selected_item, null);
//            flow_layout_w1.addView(view_inside);
//        }
        linear_mainview.addView(view);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                ScheduleEachWeekActivity.this.finish();
                break;

//            case R.id.image_backward:
//
//                break;
//
//            case R.id.image_forward:
//
//
//                break;
        }
    }


}
