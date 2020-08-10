package com.freshhome.AdapterClass;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
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
import com.freshhome.DriverModule.DriverNotificationActivity;
import com.freshhome.FeedbackListActivity;
import com.freshhome.R;
import com.freshhome.UserOrderDetailActivity;
import com.freshhome.UserOrderFeedbackActivity;
import com.freshhome.datamodel.NotificationModel;
import com.freshhome.fragments.UserCartFragment;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by HP on 01-05-2018.
 */

public class DriverNotificationAdapter extends RecyclerView.Adapter<DriverNotificationAdapter.MyViewHolder> {
    Context context;
    ArrayList<NotificationModel> array_notification;
    LayoutInflater inflater;
    ApiInterface apiInterface;
    UserCartFragment userCartFragment;

    public DriverNotificationAdapter(Context context, ArrayList<NotificationModel> array_notification) {
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
                .inflate(R.layout.single_row_driver_notification, parent, false);

        return new DriverNotificationAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder mViewHolder, final int i) {
        mViewHolder.text_message.setText(array_notification.get(i).getMessage());
        mViewHolder.text_time.setText(array_notification.get(i).getTime());
        mViewHolder.text_order_no.setText(array_notification.get(i).getOrder_id());
        mViewHolder.text_deliveryloc.setText(array_notification.get(i).getDeliveryLoc());

        if(array_notification.get(i).getOrderstatus().equalsIgnoreCase("Pending")){
            mViewHolder.text_accept.setVisibility(View.VISIBLE);
            mViewHolder.text_reject.setVisibility(View.VISIBLE);
            mViewHolder.text_reject.setClickable(true);
            mViewHolder.text_reject.setEnabled(true);
            mViewHolder.text_reject.setFocusable(true);
        }else{
            mViewHolder.text_accept.setVisibility(View.GONE);
            mViewHolder.text_reject.setVisibility(View.GONE);
            mViewHolder.text_reject.setClickable(false);
            mViewHolder.text_reject.setEnabled(false);
            mViewHolder.text_reject.setFocusable(false);
        }
        mViewHolder.linear_main.setTag(i);
        mViewHolder.text_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonMethods.checkConnection()) {
                    AcceptRejectNotification(array_notification.get(i).getOrder_id(), true,mViewHolder.text_accept,mViewHolder.text_reject);
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.internetconnection));
                }
            }
        });

        mViewHolder.text_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonMethods.checkConnection()) {
                    AcceptRejectNotification(array_notification.get(i).getOrder_id(), false,mViewHolder.text_accept,mViewHolder.text_reject);
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.internetconnection));
                }
            }
        });

    }

    private void AcceptRejectNotification(String order_id, final boolean isAccept,
                                          final TextView textAccept, final TextView textReject) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls;
        if (isAccept) {
            calls = apiInterface.DriverAcceptOrder(order_id);
        } else {
            calls = apiInterface.DriverRejectOrder(order_id);
        }

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
                            if (isAccept) {
                                textAccept.setText("Accepted");
                                textReject.setVisibility(View.GONE);
                            } else {
                                textAccept.setVisibility(View.GONE);
                                textReject.setText("Rejected");
                            }
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(context, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.server_error));
            }
        });

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
        TextView text_order_no, text_deliveryloc, text_time, text_message, text_reject, text_accept;
        LinearLayout linear_main, linear_accept_reject;
        RatingBar ratingBar;
//        ImageView imageview;

        public MyViewHolder(View convertView) {
            super(convertView);
            text_accept = (TextView) convertView.findViewById(R.id.text_accept);
            text_reject = (TextView) convertView.findViewById(R.id.text_reject);
            text_message = (TextView) convertView.findViewById(R.id.text_message);
            text_order_no = (TextView) convertView.findViewById(R.id.text_order_no);
            text_time = (TextView) convertView.findViewById(R.id.text_time);
            text_deliveryloc = (TextView) convertView.findViewById(R.id.text_deliveryloc);
            linear_accept_reject = (LinearLayout) convertView.findViewById(R.id.linear_accept_reject);
            linear_main = (LinearLayout) convertView.findViewById(R.id.linear_main);
        }
    }

//    private void click_row(LinearLayout linear_main, final int pos) {
//        linear_main.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (array_notification.get(pos).getType().equalsIgnoreCase("order_delivered")
//                        || array_notification.get(pos).getType().equalsIgnoreCase("order")
//                        || array_notification.get(pos).getType().equalsIgnoreCase("order_cancelled")) {
//
//                    Intent intent = new Intent(context, UserOrderDetailActivity.class);
//                    intent.putExtra("order_id", array_notification.get(Integer.parseInt(v.getTag().toString())).getOrder_id());
//                    intent.putExtra("ispending", true);
//                    context.startActivity(intent);
//
//                } else if (array_notification.get(pos).getType().equalsIgnoreCase("dish_rating")) {
//
//                    Intent intent = new Intent(context, FeedbackListActivity.class);
//                    intent.putExtra("isSupplierDetail", false);
//                    intent.putExtra("id", array_notification.get(pos).getOrder_id());
//                    context.startActivity(intent);
//
//                } else if (array_notification.get(pos).getType().equalsIgnoreCase("order_rating")) {
//                    Intent intent = new Intent(context, UserOrderFeedbackActivity.class);
//                    intent.putExtra("order_id", array_notification.get(pos).getOrder_id());
//                    context.startActivity(intent);
//
//                } else {
//
//                }
//            }
//        });
//    }
}