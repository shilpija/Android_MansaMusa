package com.freshhome.AdapterClass;

import android.app.ProgressDialog;
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

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CompleteHelpRequestActivity;
import com.freshhome.FeedbackListActivity;
import com.freshhome.R;
import com.freshhome.SalesModule.Activity_Sales_RequestDetail;
import com.freshhome.UserOrderDetailActivity;
import com.freshhome.UserOrderFeedbackActivity;
import com.freshhome.datamodel.Cart;
import com.freshhome.datamodel.NotificationModel;
import com.freshhome.fragments.UserCartFragment;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by HP on 01-05-2018.
 */

public class RecyclerNotificationAdapter extends RecyclerView.Adapter<RecyclerNotificationAdapter.MyViewHolder> {
    Context context;
    ArrayList<NotificationModel> array_notification;
    LayoutInflater inflater;
    ApiInterface apiInterface;
    UserCartFragment userCartFragment;

    public RecyclerNotificationAdapter(Context context, ArrayList<NotificationModel> array_notification) {
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

        return new RecyclerNotificationAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder mViewHolder, final int i) {
        mViewHolder.text_message.setText(array_notification.get(i).getMessage());
        mViewHolder.text_time.setText(array_notification.get(i).getTime());
        mViewHolder.linear_main.setTag(i);

        if (array_notification.get(i).getType().equalsIgnoreCase("order_delivered")) {
            //open delivered orders // user //supplier
            mViewHolder.imageview.setImageDrawable(context.getResources().getDrawable(R.drawable.order));
            click_row(mViewHolder.linear_main, i);

        } else if (array_notification.get(i).getType().equalsIgnoreCase("order_rating")) {
            //open order rating  //supplier
            mViewHolder.imageview.setImageDrawable(context.getResources().getDrawable(R.drawable.star));
            click_row(mViewHolder.linear_main, i);

        } else if (array_notification.get(i).getType().equalsIgnoreCase("dish_rating")) {
            //open dish ratings //supplier
            mViewHolder.imageview.setImageDrawable(context.getResources().getDrawable(R.drawable.star));
            click_row(mViewHolder.linear_main, i);

        } else if (array_notification.get(i).getType().equalsIgnoreCase("order")) {
            //open my kitchen --//user
            mViewHolder.imageview.setImageDrawable(context.getResources().getDrawable(R.drawable.order));
            click_row(mViewHolder.linear_main, i);

        } else if (array_notification.get(i).getType().equalsIgnoreCase("order_cancelled")) {
            //order cancelled --//supplier //user
            mViewHolder.imageview.setImageDrawable(context.getResources().getDrawable(R.drawable.order));
            click_row(mViewHolder.linear_main, i);

        } else if (array_notification.get(i).getType().equalsIgnoreCase("help_request")) {
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
                if (array_notification.get(pos).getType().equalsIgnoreCase("order_delivered")
                        || array_notification.get(pos).getType().equalsIgnoreCase("order")
                        || array_notification.get(pos).getType().equalsIgnoreCase("order_cancelled")) {

                    Intent intent = new Intent(context, UserOrderDetailActivity.class);
                    intent.putExtra("order_id", array_notification.get(Integer.parseInt(v.getTag().toString())).getOrder_id());
                    intent.putExtra("ispending", true);
                    context.startActivity(intent);

                } else if (array_notification.get(pos).getType().equalsIgnoreCase("dish_rating")) {

                    Intent intent = new Intent(context, FeedbackListActivity.class);
                    intent.putExtra("isSupplierDetail", false);
                    intent.putExtra("id", array_notification.get(pos).getOrder_id());
                    context.startActivity(intent);

                } else if (array_notification.get(pos).getType().equalsIgnoreCase("order_rating")) {
                    Intent intent = new Intent(context, UserOrderFeedbackActivity.class);
                    intent.putExtra("order_id", array_notification.get(pos).getOrder_id());
                    context.startActivity(intent);
                }else if(array_notification.get(pos).getType().equalsIgnoreCase("help_request")){
                    Intent intent = new Intent(context, CompleteHelpRequestActivity.class);
                    intent.putExtra("request_id", array_notification.get(pos).getRequet_id());
                    intent.putExtra("notification_id", array_notification.get(pos).getNotification_id());
                    intent.putExtra("msg", array_notification.get(pos).getMessage());
                    intent.putExtra("sales_name", array_notification.get(pos).getSales_name());
                    intent.putExtra("sales_phone", array_notification.get(pos).getSales_phone());
                    intent.putExtra("rating_overall", array_notification.get(pos).getRating_overall());
                    intent.putExtra("sales_rating", array_notification.get(pos).getSales_rating());
                    intent.putExtra("sales_image", array_notification.get(pos).getSales_image());
                    intent.putExtra("request_status",array_notification.get(pos).getRequest_status());
                    context.startActivity(intent);
                }
            }
        });
    }
}