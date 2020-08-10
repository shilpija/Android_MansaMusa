package com.freshhome.AdapterClass;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshhome.FeedbackListActivity;
import com.freshhome.R;


/**
 * Created by HP on 01-05-2018.
 */

public class NotificationReviewAdapter extends BaseAdapter {
    Context context;
    int i;
    LayoutInflater inflater;

    public NotificationReviewAdapter(Context context, int i) {
        this.context = context;
        this.i = i;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return i;
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
            convertView = inflater.inflate(R.layout.single_row_notreview, viewGroup, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, FeedbackListActivity.class);
                context.startActivity(i);
            }
        });

        return convertView;
    }

    private class MyViewHolder {
        TextView text_name;
        LinearLayout linear_layout;

        public MyViewHolder(View convertView) {
//            text_name = (TextView) convertView.findViewById(R.id.text_count);
//            linear_layout = (LinearLayout) convertView.findViewById(R.id.linear_layout);

        }
    }
}