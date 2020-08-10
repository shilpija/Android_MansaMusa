package com.freshhome.AdapterClass;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.OrderDetailActivity;
import com.freshhome.R;
import com.freshhome.datamodel.MyKitchenOrders;
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

public class OrderPreorderAdapter extends RecyclerView.Adapter<OrderPreorderAdapter.MyViewHolder> {
    Context context;
    ApiInterface apiInterface;
    int i;
    LayoutInflater inflater;
    ArrayList<MyKitchenOrders> arrayOrder;
    String type;

    public OrderPreorderAdapter(Context context, ArrayList<MyKitchenOrders> arrayOrder, String type) {
        this.context = context;
        this.arrayOrder = arrayOrder;
        inflater = LayoutInflater.from(this.context);
        apiInterface = ApiClient.getInstance().getClient();
        this.type = type;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_orderpreorder, parent, false);

        return new OrderPreorderAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder mViewHolder, int i) {
        mViewHolder.text_order_price.setText(arrayOrder.get(i).getCurrency_code() + " " + arrayOrder.get(i).getTotal());
        mViewHolder.text_total_items.setText(arrayOrder.get(i).getTotal_items() + " Items");
        mViewHolder.user_name.setText(arrayOrder.get(i).getuserName());
      //sk  mViewHolder.text_collection_time.setText(arrayOrder.get(i).gettime());
        mViewHolder.text_order_no.setText(arrayOrder.get(i).getOrder_id());
      //sk  mViewHolder.text_collection_date.setText(arrayOrder.get(i).getDate());
        mViewHolder.text_collection_date.setText(arrayOrder.get(i).getOrder_date());

        if (!arrayOrder.get(i).getUser_profile_pic().equalsIgnoreCase("null") && !arrayOrder.get(i).getUser_profile_pic().equalsIgnoreCase("")) {
            Picasso.get().load(arrayOrder.get(i).getUser_profile_pic()).placeholder(R.drawable.demo).into(mViewHolder.image_user);
        } else {
            Picasso.get().load(R.drawable.demo).into(mViewHolder.image_user);
        }

        if (arrayOrder.get(i).getOrder_status().equalsIgnoreCase("Accept")) {
            mViewHolder.text_accept.setVisibility(View.GONE);
            mViewHolder.text_reject.setVisibility(View.GONE);
            mViewHolder.text_accepted.setVisibility(View.VISIBLE);

            mViewHolder.text_accepted.setText("Accepted");

            mViewHolder.text_processing.setVisibility(View.GONE);
            mViewHolder.text_return.setVisibility(View.GONE);
            mViewHolder.text_rejected.setVisibility(View.GONE);
            mViewHolder.text_cancelled.setVisibility(View.GONE);
            mViewHolder.text_out_of_delivery.setVisibility(View.GONE);
        } else if(arrayOrder.get(i).getOrder_status().equalsIgnoreCase("New Order")) {
            mViewHolder.text_accept.setVisibility(View.VISIBLE);
            mViewHolder.text_reject.setVisibility(View.VISIBLE);
            mViewHolder.text_accepted.setVisibility(View.GONE);
            mViewHolder.text_processing.setVisibility(View.GONE);

            mViewHolder.text_return.setVisibility(View.GONE);
            mViewHolder.text_rejected.setVisibility(View.GONE);
            mViewHolder.text_cancelled.setVisibility(View.GONE);
            mViewHolder.text_out_of_delivery.setVisibility(View.GONE);
        }
        else if(arrayOrder.get(i).getOrder_status().equalsIgnoreCase("Processing")){
            mViewHolder.text_accept.setVisibility(View.GONE);
            mViewHolder.text_reject.setVisibility(View.GONE);
            mViewHolder.text_accepted.setVisibility(View.GONE);
            mViewHolder.text_return.setVisibility(View.GONE);
            mViewHolder.text_rejected.setVisibility(View.GONE);
            mViewHolder.text_cancelled.setVisibility(View.GONE);
            mViewHolder.text_out_of_delivery.setVisibility(View.GONE);
            mViewHolder.text_processing.setVisibility(View.VISIBLE);

        }else if(arrayOrder.get(i).getOrder_status().equalsIgnoreCase("Cancelled")){

            mViewHolder.text_accept.setVisibility(View.GONE);
            mViewHolder.text_reject.setVisibility(View.GONE);
            mViewHolder.text_accepted.setVisibility(View.GONE);
            mViewHolder.text_return.setVisibility(View.GONE);
            mViewHolder.text_rejected.setVisibility(View.GONE);
            mViewHolder.text_cancelled.setVisibility(View.VISIBLE);
            mViewHolder.text_out_of_delivery.setVisibility(View.GONE);
            mViewHolder.text_processing.setVisibility(View.GONE);

        }else if(arrayOrder.get(i).getOrder_status().equalsIgnoreCase("Rejected")){

            mViewHolder.text_accept.setVisibility(View.GONE);
            mViewHolder.text_reject.setVisibility(View.GONE);
            mViewHolder.text_accepted.setVisibility(View.GONE);
            mViewHolder.text_return.setVisibility(View.GONE);
            mViewHolder.text_rejected.setVisibility(View.VISIBLE);
            mViewHolder.text_cancelled.setVisibility(View.GONE);
            mViewHolder.text_out_of_delivery.setVisibility(View.GONE);
            mViewHolder.text_processing.setVisibility(View.GONE);

        }else if(arrayOrder.get(i).getOrder_status().equalsIgnoreCase("Return")){

            mViewHolder.text_accept.setVisibility(View.GONE);
            mViewHolder.text_reject.setVisibility(View.GONE);
            mViewHolder.text_accepted.setVisibility(View.GONE);
            mViewHolder.text_return.setVisibility(View.VISIBLE);
            mViewHolder.text_rejected.setVisibility(View.GONE);
            mViewHolder.text_cancelled.setVisibility(View.GONE);
            mViewHolder.text_out_of_delivery.setVisibility(View.GONE);
            mViewHolder.text_processing.setVisibility(View.GONE);

        }else if(arrayOrder.get(i).getOrder_status().equalsIgnoreCase("Out for delivery")){

            mViewHolder.text_accept.setVisibility(View.GONE);
            mViewHolder.text_reject.setVisibility(View.GONE);
            mViewHolder.text_accepted.setVisibility(View.GONE);
            mViewHolder.text_return.setVisibility(View.GONE);
            mViewHolder.text_rejected.setVisibility(View.GONE);
            mViewHolder.text_cancelled.setVisibility(View.GONE);
            mViewHolder.text_out_of_delivery.setVisibility(View.VISIBLE);
            mViewHolder.text_processing.setVisibility(View.GONE);

        }

        mViewHolder.text_accept.setTag(arrayOrder.get(i).getOrder_id());
        mViewHolder.text_reject.setTag(arrayOrder.get(i).getOrder_id());
        mViewHolder.linear_main.setTag(arrayOrder.get(i).getOrder_id());
        mViewHolder.text_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonMethods.checkConnection()) {
                    AcceptRejectOrder(mViewHolder.text_accept.getTag().toString(), true, "", "", mViewHolder.text_accept, mViewHolder.text_reject, mViewHolder.text_accepted);
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.internetconnection));
                }
            }
        });

        mViewHolder.text_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RejectOrderDialog(mViewHolder.text_accept.getTag().toString(), mViewHolder.text_accept, mViewHolder.text_reject, mViewHolder.text_accepted);
            }
        });

        mViewHolder.linear_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, OrderDetailActivity.class);
                i.putExtra("type", type);
                i.putExtra("order_id", mViewHolder.linear_main.getTag().toString());
                context.startActivity(i);
            }
        });
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return arrayOrder.size();
    }


    private void AcceptRejectOrder(String order_id, final boolean isAccept, String reject_type, String reject_msg, final TextView textaccept, final TextView textreject, final TextView text_accepted) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;
        if (isAccept) {
            calls = apiInterface.AcceptOrder(order_id);
        } else {
            calls = apiInterface.RejectOrder(order_id, reject_type, reject_msg);
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
                            JSONObject obj = object.getJSONObject("success");
                            if (isAccept) {
                                text_accepted.setText("Accepted");
                            } else {
                                text_accepted.setText("Rejected");
                            }
                            text_accepted.setVisibility(View.VISIBLE);
                            textreject.setVisibility(View.GONE);
                            textaccept.setVisibility(View.GONE);
//                            CommonUtilFunctions.success_Alert_Dialog(context, obj.getString("msg"));
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text_accept, text_reject, user_name, text_collection_date, text_collection_time, text_order_price, text_total_items, text_accepted;
        TextView text_order_no,text_processing, text_rejected, text_return, text_cancelled, text_out_of_delivery;
        CircleImageView image_user;
        LinearLayout linear_main;

        public MyViewHolder(View convertView) {
            super(convertView);
            user_name = (TextView) convertView.findViewById(R.id.user_name);
            text_collection_date = (TextView) convertView.findViewById(R.id.text_collection_date);
          //sk  text_collection_time = (TextView) convertView.findViewById(R.id.text_collection_time);
            text_order_no = (TextView) convertView.findViewById(R.id.text_order_no);
            text_order_price = (TextView) convertView.findViewById(R.id.text_order_price);
            text_total_items = (TextView) convertView.findViewById(R.id.text_total_items);
            text_accept = (TextView) convertView.findViewById(R.id.text_accept);
            text_reject = (TextView) convertView.findViewById(R.id.text_reject);
            text_accepted = (TextView) convertView.findViewById(R.id.text_accepted);
            text_processing = (TextView) convertView.findViewById(R.id.text_processing);
            text_rejected = (TextView) convertView.findViewById(R.id.text_rejected);
            text_return = (TextView) convertView.findViewById(R.id.text_return);
            text_cancelled = (TextView) convertView.findViewById(R.id.text_cancelled);
            text_out_of_delivery = (TextView) convertView.findViewById(R.id.text_out_of_delivery);
            image_user = (CircleImageView) convertView.findViewById(R.id.image_user);
            linear_main = (LinearLayout) convertView.findViewById(R.id.linear_main);

        }
    }

    private void RejectOrderDialog(final String order_id, final TextView textaccept, final TextView textreject, final TextView text_accepted) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_rejectorder_dialog);
        dialog.setCanceledOnTouchOutside(false);
        TextView text_save = (TextView) dialog.findViewById(R.id.text_save);
        TextView text_cancel = (TextView) dialog.findViewById(R.id.text_cancel);
        final Spinner spinner_reject = (Spinner) dialog.findViewById(R.id.spinner_reject);
        String[] reject_reason_Array = context.getResources().getStringArray(R.array.reject_reason);
        spinner_reject.setAdapter(new ArrayAdapter<>(context, R.layout.layout_spinner_text, reject_reason_Array));
        final EditText edit_text = (EditText) dialog.findViewById(R.id.edit_text);

        text_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonMethods.checkConnection()) {
                    dialog.dismiss();
                    AcceptRejectOrder(order_id, false, spinner_reject.getSelectedItem().toString(), edit_text.getText().toString().trim(), textaccept, textreject, text_accepted);
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.internetconnection));
                }
            }
        });

        text_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}