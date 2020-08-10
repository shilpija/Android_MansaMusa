package com.freshhome.AdapterClass;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.OrderDetailActivity;
import com.freshhome.R;
import com.freshhome.datamodel.MyKitchenOrders;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by HP on 01-05-2018.
 */

public class OrderPendingAdapter extends RecyclerView.Adapter<OrderPendingAdapter.MyViewHolder> {
    Context context;
    int pos;
    LayoutInflater inflater;
    ArrayList<MyKitchenOrders> arrayPendingOrder;
    String type;
    UserSessionManager sessionManager;
    ApiInterface apiInterface;

    public OrderPendingAdapter(Context context, ArrayList<MyKitchenOrders> arrayPendingOrder, String type) {
        this.context = context;
        this.arrayPendingOrder = arrayPendingOrder;
        inflater = LayoutInflater.from(this.context);
        this.type = type;
        sessionManager = new UserSessionManager(context);
        apiInterface = ApiClient.getInstance().getClient();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_orderpending, parent, false);

        return new OrderPendingAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder mViewHolder, final int i) {
        mViewHolder.text_order_price.setText(arrayPendingOrder.get(i).getCurrency_code() + " " + arrayPendingOrder.get(i).getTotal());
        mViewHolder.text_total_items.setText(arrayPendingOrder.get(i).getTotal_items() + " Items");
        mViewHolder.user_name.setText(arrayPendingOrder.get(i).getuserName());
        mViewHolder.text_collection_time.setText(arrayPendingOrder.get(i).gettime());
        mViewHolder.text_collection_date.setText(arrayPendingOrder.get(i).getDate());

        if (!arrayPendingOrder.get(i).getUser_profile_pic().equalsIgnoreCase("null") && !arrayPendingOrder.get(i).getUser_profile_pic().equalsIgnoreCase("")) {
            Picasso.get().load(arrayPendingOrder.get(i).getUser_profile_pic()).placeholder(R.drawable.demo).into(mViewHolder.image_user);
        } else {
            Picasso.get().load(R.drawable.demo).into(mViewHolder.image_user);
        }

        if (!sessionManager.getUserDetails().get(UserSessionManager.KEY_SUPPLIER_TYPE).equalsIgnoreCase(ConstantValues.home_food)) {
            mViewHolder.linear_timer.setVisibility(View.GONE);
        } else {
            mViewHolder.linear_timer.setVisibility(View.VISIBLE);
        }

        final Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());

        mViewHolder.linear_main.setTag(arrayPendingOrder.get(i).getOrder_id());
        mViewHolder.linear_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, OrderDetailActivity.class);
                i.putExtra("type", type);
                i.putExtra("order_id", mViewHolder.linear_main.getTag().toString());
                context.startActivity(i);
            }
        });

        if (arrayPendingOrder.get(i).getDate().equalsIgnoreCase(formattedDate)) {
            mViewHolder.text_counter.setText(CommonMethods.timeLeftForDeliverySec(arrayPendingOrder.get(i).gettime()));
            if (mViewHolder.timer != null) {
                mViewHolder.timer.cancel();
            }
            //starttimer
            long timer = CommonMethods.timeLeftForDeliverySecond(arrayPendingOrder.get(i).gettime());
            timer = timer * 1000;
            mViewHolder.timer = new CountDownTimer(timer, 1000) {
                public void onTick(long millisUntilFinished) {
                    String time = CommonMethods.calculateTime(millisUntilFinished / 1000);
                    mViewHolder.text_counter.setText(time);
                }

                @Override
                public void onFinish() {
                    mViewHolder.text_counter.setText(context.getResources().getString(R.string.order_due));
                }
            }.start();
        } else {
            mViewHolder.text_counter.setText(context.getResources().getString(R.string.order_due));
        }


        if (arrayPendingOrder.get(i).getOrder_status().equalsIgnoreCase("Processing")) {
            mViewHolder.text_order_ready.setText("Processing");
            mViewHolder.linear_order_ready.setClickable(false);
            mViewHolder.linear_order_ready.setBackgroundColor(context.getResources().getColor(R.color.app_color_orange));
            mViewHolder.linear_order_ready.setOnClickListener(null);
        } else {
            mViewHolder.text_order_ready.setText(context.getResources().getString(R.string.orderready));
            mViewHolder.linear_order_ready.setClickable(true);
            mViewHolder.linear_order_ready.setBackgroundColor(context.getResources().getColor(R.color.app_color_blue));
            mViewHolder.linear_order_ready.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (sessionManager.getSubscriptionDetails().get(UserSessionManager.KEY_STATUS).equalsIgnoreCase("active")) {
                        if (CommonMethods.checkConnection()) {
                            OrderisReady(mViewHolder.linear_main.getTag().toString(), mViewHolder.text_order_ready, mViewHolder.linear_order_ready);
                        } else {
                            CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.internetconnection));
                        }
//                    } else {
//                        CommonMethods.show_buy_plan(context);
//                    }
                }
            });
        }


    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return arrayPendingOrder.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView user_name, text_collection_date, text_collection_time, text_order_price, text_total_items, text_counter, text_order_ready;
        CircleImageView image_user;
        LinearLayout linear_order_ready, linear_main, linear_timer;
        CountDownTimer timer;

        public MyViewHolder(View convertView) {
            super(convertView);
            text_order_ready = (TextView) convertView.findViewById(R.id.text_order_ready);
            text_counter = (TextView) convertView.findViewById(R.id.text_counter);
            user_name = (TextView) convertView.findViewById(R.id.user_name);
            text_collection_date = (TextView) convertView.findViewById(R.id.text_collection_date);
            text_collection_time = (TextView) convertView.findViewById(R.id.text_collection_time);
            text_order_price = (TextView) convertView.findViewById(R.id.text_order_price);
            text_total_items = (TextView) convertView.findViewById(R.id.text_total_items);
            image_user = (CircleImageView) convertView.findViewById(R.id.image_user);
            linear_main = (LinearLayout) convertView.findViewById(R.id.linear_main);
            linear_timer = (LinearLayout) convertView.findViewById(R.id.linear_timer);
            linear_order_ready = (LinearLayout) convertView.findViewById(R.id.linear_order_ready);

        }
    }

    private void OrderisReady(final String order_id, final TextView text_order_ready, final LinearLayout linear_order_ready) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.OrderReady(order_id);

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        JSONObject obj = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        if (obj.getString("code").equalsIgnoreCase("200")) {
                            JSONObject object = obj.getJSONObject("success");
                            CommonUtilFunctions.success_Alert_Dialog(context, object.getString("msg"));
                            text_order_ready.setText("Processing");
                            linear_order_ready.setClickable(false);
                            linear_order_ready.setBackgroundColor(context.getResources().getColor(R.color.app_color_orange));
                            linear_order_ready.setOnClickListener(null);
//                            linear_order_ready.setVisibility(View.GONE);
//                            text_cancel_order.setVisibility(View.GONE);
                        } else {
                            JSONObject object = obj.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(context, object.getString("msg"));
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

}
