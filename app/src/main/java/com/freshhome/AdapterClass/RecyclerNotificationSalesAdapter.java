package com.freshhome.AdapterClass;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.CompleteHelpRequestActivity;
import com.freshhome.FeedbackListActivity;
import com.freshhome.R;
import com.freshhome.SalesModule.Activity_Sales_RequestDetail;
import com.freshhome.UserOrderDetailActivity;
import com.freshhome.UserOrderFeedbackActivity;
import com.freshhome.datamodel.NotificationModel;
import com.freshhome.fragments.UserCartFragment;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;

import java.util.ArrayList;


/**
 * Created by HP on 01-05-2018.
 */

public class RecyclerNotificationSalesAdapter extends RecyclerView.Adapter<RecyclerNotificationSalesAdapter.MyViewHolder> {
    Context context;
    ArrayList<NotificationModel> array_notification;
    LayoutInflater inflater;
    ApiInterface apiInterface;
    UserCartFragment userCartFragment;

    public RecyclerNotificationSalesAdapter(Context context, ArrayList<NotificationModel> array_notification) {
        this.context = context;
        this.array_notification = array_notification;
        this.userCartFragment = userCartFragment;
        inflater = LayoutInflater.from(this.context);
        apiInterface = ApiClient.getInstance().getClient();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_notification, parent, false);

        return new RecyclerNotificationSalesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder mViewHolder, final int i) {
        mViewHolder.text_message.setText(array_notification.get(i).getMessage());
        mViewHolder.text_time.setText(array_notification.get(i).getTime());
        mViewHolder.linear_main.setTag(i);

        if (array_notification.get(i).getType().equalsIgnoreCase("help_request")) {
            mViewHolder.imageview.setImageDrawable(context.getResources().getDrawable(R.drawable.qr_code));
            click_row(mViewHolder.linear_main, i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return array_notification.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text_message, text_time;
        LinearLayout linear_main, linear_available;
        RatingBar ratingBar;
        ImageView image_delete;
        ImageView imageview;

        public MyViewHolder(View convertView) {
            super(convertView);
            text_message = (TextView) convertView.findViewById(R.id.text_message);
            text_time = (TextView) convertView.findViewById(R.id.text_time);
            imageview = (ImageView) convertView.findViewById(R.id.imageview);
            linear_main = (LinearLayout) convertView.findViewById(R.id.linear_main);

        }
    }

    private void click_row(LinearLayout linear_main, final int pos) {
        linear_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (array_notification.get(pos).getType().equalsIgnoreCase("help_request")) {

                    Intent i = new Intent(context, Activity_Sales_RequestDetail.class);
                    i.putExtra("id", array_notification.get(pos).getNotification_id());
                    i.putExtra("name", array_notification.get(pos).getSupplier_name());
                    i.putExtra("msg", array_notification.get(pos).getMessage());
                    i.putExtra("phone", array_notification.get(pos).getSupplier_phone());
                    i.putExtra("loc", array_notification.get(pos).getSupplier_loc());
                    i.putExtra("lat", array_notification.get(pos).getLat());
                    i.putExtra("lng", array_notification.get(pos).getLng());
                    i.putExtra("reqstatus", array_notification.get(pos).getRequest_status());
                    i.putExtra("req_id", array_notification.get(pos).getRequet_id());

                    context.startActivity(i);
                }
            }
        });
    }
}