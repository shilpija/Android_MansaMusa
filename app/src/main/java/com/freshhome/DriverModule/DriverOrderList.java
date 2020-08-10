package com.freshhome.DriverModule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.freshhome.AdapterClass.DriverOrderHistoryAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.OrderDetailActivity;
import com.freshhome.R;
import com.freshhome.datamodel.Orders;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverOrderList extends Fragment implements View.OnClickListener {
    LinearLayout linear_pending, linear_pending_line, linear_delivered, linear_delivered_line;
    ImageView image_back;
    ListView driverOrderList;
    String type = "";
    ApiInterface apiInterface;
    ArrayList<Orders>  arrayDeliveredOrder;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.activity_driver_order_list, container, false);
        arrayDeliveredOrder = new ArrayList<>();
        apiInterface = ApiClient.getInstance().getClient();
        linear_pending = (LinearLayout) v.findViewById(R.id.linear_pending);
        linear_pending_line = (LinearLayout) v.findViewById(R.id.linear_pending_line);
//        linear_pending_line.setVisibility(View.VISIBLE);
        linear_pending.setOnClickListener(this);

        linear_delivered = (LinearLayout) v.findViewById(R.id.linear_delivered);
        linear_delivered_line = (LinearLayout) v.findViewById(R.id.linear_delivered_line);
//        linear_delivered_line.setVisibility(View.GONE);
        linear_delivered.setOnClickListener(this);

        driverOrderList = (ListView) v.findViewById(R.id.driverOrderList);

        if (CommonMethods.checkConnection()) {
            GetData();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
        }

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.linear_pending:
//                clickPending();
                break;

            case R.id.linear_delivered:
//                clickDelivered();
                break;
        }
    }

    private void clickDelivered() {
        type = "delivered";
        driverOrderList = (ListView) v.findViewById(R.id.driverOrderList);
        DriverOrderHistoryAdapter adapter_deliver = new DriverOrderHistoryAdapter(getActivity(), arrayDeliveredOrder, false);
        driverOrderList.setAdapter(adapter_deliver);
    }

    private void GetData() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetDriverOrders();
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
                            arrayDeliveredOrder = new ArrayList<>();
                            JSONArray jsonArray = object.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj=jsonArray.getJSONObject(i);
                                Orders order = new Orders();
                                order.setOrder_id(obj.getString("order_id"));
                                order.setOrder_no(obj.getString("order_id"));
                                order.setOrder_price(obj.getString("total"));
                                order.setCurrency(obj.getString("currency_code"));
                                order.setPayment_method(obj.getString("payment_method"));
                                order.setItem_qty(obj.getString("total_items"));
                                order.setOrder_date(obj.getString("order_date"));
                                JSONObject obj_user=obj.getJSONObject("user_info");
                                JSONObject obj_loc=obj_user.getJSONObject("delivery_address");
                                order.setOrder_delivery_loc(obj_loc.getString("location"));
                                arrayDeliveredOrder.add(order);
                            }

                            clickDelivered();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
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
            }
        });
    }
}
