package com.freshhome.AdapterClass;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.R;
import com.freshhome.UserOrderDetailActivity;
import com.freshhome.datamodel.Orders;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by HP on 01-05-2018.
 */

public class UserHistoryAdapter extends BaseAdapter {
    Context context;
    ArrayList<Orders> arrayOrder;
    LayoutInflater inflater;
    boolean ispending;
    ApiInterface apiInterface;

    public UserHistoryAdapter(Context context, ArrayList<Orders> arrayOrder, boolean ispending) {
        this.context = context;
        this.arrayOrder = arrayOrder;
        apiInterface = ApiClient.getInstance().getClient();
        this.ispending = ispending;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return arrayOrder.size();
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
            convertView = inflater.inflate(R.layout.single_row_user_history, viewGroup, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.text_order_no.setText("#" + arrayOrder.get(i).getOrder_no());
        mViewHolder.text_date.setText(arrayOrder.get(i).getOrder_date());
        mViewHolder.text_items.setText(arrayOrder.get(i).getItem_qty());
        mViewHolder.text_price.setText(arrayOrder.get(i).getCurrency() + arrayOrder.get(i).getOrder_price());
        mViewHolder.text_loc.setText(arrayOrder.get(i).getOrder_delivery_loc());
        mViewHolder.linear_main.setTag(arrayOrder.get(i).getOrder_id());
        if (ispending) {
            mViewHolder.linear_repeat.setVisibility(View.GONE);
        } else {
            mViewHolder.linear_repeat.setVisibility(View.VISIBLE);
        }
        mViewHolder.linear_repeat.setTag(arrayOrder.get(i).getOrder_id());
        mViewHolder.linear_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonMethods.checkConnection()) {
                    RepeatOrder(mViewHolder.linear_repeat.getTag().toString());
                }else{
                    CommonUtilFunctions.Error_Alert_Dialog(context,context.getResources().getString(R.string.internetconnection));
                }
            }
        });

        mViewHolder.linear_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, UserOrderDetailActivity.class);
                i.putExtra("ispending", ispending);
                i.putExtra("order_id", mViewHolder.linear_main.getTag().toString());
                context.startActivity(i);
            }
        });
        return convertView;
    }

    private class MyViewHolder {
        TextView text_order_no, text_date, text_items, text_price, text_loc;
        ImageView dishimage;
        RatingBar ratingBar;
        LinearLayout linear_repeat, linear_main;

        public MyViewHolder(View convertView) {
            text_order_no = (TextView) convertView.findViewById(R.id.text_order_no);
            text_date = (TextView) convertView.findViewById(R.id.text_date);
            text_items = (TextView) convertView.findViewById(R.id.text_items);
            text_price = (TextView) convertView.findViewById(R.id.text_price);
            text_loc = (TextView) convertView.findViewById(R.id.text_loc);
            linear_repeat = (LinearLayout) convertView.findViewById(R.id.linear_repeat);
            linear_main = (LinearLayout) convertView.findViewById(R.id.linear_main);
        }
    }

    private void RepeatOrder(String order_id) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.RepeatOrder(order_id);

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
                            CommonUtilFunctions.success_Alert_Dialog(context, obj.getString("success"));

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