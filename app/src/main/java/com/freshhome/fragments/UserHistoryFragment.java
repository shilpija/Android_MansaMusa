package com.freshhome.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

//import com.freshhome.AdapterClass.RecyclerCartAdapter;
import com.freshhome.AdapterClass.UserHistoryAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.R;
import com.freshhome.datamodel.Orders;
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
 * A simple {@link Fragment} subclass.
 */
public class UserHistoryFragment extends Fragment implements View.OnClickListener {
    ListView historyList;
    TextView text_pending, text_delivered;
    LinearLayout linear_pending_line, linear_delivered_line;
    boolean isPending = true;
    ApiInterface apiInterface;
    ArrayList<Orders> arrayPendingOrder, arrayDeliveredOrder;
    UserSessionManager sessionManager;
    public UserHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_history, container, false);
        arrayDeliveredOrder = new ArrayList<>();
        arrayPendingOrder = new ArrayList<>();
        apiInterface = ApiClient.getInstance().getClient();
        sessionManager=new UserSessionManager(getActivity());
        historyList = (ListView) v.findViewById(R.id.historyList);
//        historyList.setOnItemClickListener(this);
        text_pending = (TextView) v.findViewById(R.id.text_pending);
        text_pending.setOnClickListener(this);
        text_delivered = (TextView) v.findViewById(R.id.text_delivered);
        text_delivered.setOnClickListener(this);
        linear_pending_line = (LinearLayout) v.findViewById(R.id.linear_pending_line);
        linear_delivered_line = (LinearLayout) v.findViewById(R.id.linear_delivered_line);


        return v;
    }

    private void getHistorydata() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.GetUserOrderHistory();

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        arrayDeliveredOrder = new ArrayList<>();
                        arrayPendingOrder = new ArrayList<>();
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONArray jarray = object.getJSONArray("pending_orders");
                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject obj = jarray.getJSONObject(i);
                                Orders order = new Orders();
                                order.setOrder_id(obj.getString("order_id"));
                                order.setOrder_no(obj.getString("order_id"));

                                order.setCurrency(obj.getString("currency_code"));
                                order.setPayment_method(obj.getString("payment_method"));
                                order.setItem_qty(obj.getString("total_items"));
                                order.setOrder_date(obj.getString("order_date"));
                                JSONObject obj_user = obj.getJSONObject("user_info");
                                JSONObject obj_deliveryadd = obj_user.getJSONObject("delivery_address");
                                order.setOrder_delivery_loc(obj_deliveryadd.getString("location"));

                                if(obj.has("order_total")) {
                                    JSONArray array = obj.getJSONArray("order_total");

                                    for (int j = 0; j < array.length();j++){
                                        JSONObject objsub = array.getJSONObject(j);
                                        if(objsub.getString("code").equalsIgnoreCase("total")){
                                            order.setOrder_price(objsub.getString("value"));
                                            break;
                                        }
                                    }

                                }

                                arrayPendingOrder.add(order);

                            }

                            JSONArray jarray_complete = object.getJSONArray("completed_orders");
                            for (int j = 0; j < jarray_complete.length(); j++) {
                                JSONObject obj = jarray_complete.getJSONObject(j);
                                Orders order = new Orders();
                                order.setOrder_id(obj.getString("order_id"));
                                order.setOrder_no(obj.getString("order_id"));
                                order.setOrder_price(obj.getString("total"));
                                order.setCurrency(obj.getString("currency_code"));
                                order.setPayment_method(obj.getString("payment_method"));
                                order.setItem_qty(obj.getString("total_items"));
                                order.setOrder_date(obj.getString("order_date"));

                                JSONObject obj_user = obj.getJSONObject("user_info");
                                JSONObject obj_deliveryadd = obj_user.getJSONObject("delivery_address");
                                order.setOrder_delivery_loc(obj_deliveryadd.getString("location"));

                                arrayDeliveredOrder.add(order);

                            }

                            if (isPending) {
                                ///enable pending
                                clickActive(text_pending, linear_pending_line, true, arrayPendingOrder);
                                clickInactive(text_delivered, linear_delivered_line);
                            } else {
                                clickActive(text_delivered, linear_delivered_line, false, arrayDeliveredOrder);
                                clickInactive(text_pending, linear_pending_line);
                            }

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
                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_pending:
                isPending = true;
                clickActive(text_pending, linear_pending_line, true, arrayPendingOrder);
                clickInactive(text_delivered, linear_delivered_line);

                break;

            case R.id.text_delivered:
                isPending = false;
                clickActive(text_delivered, linear_delivered_line, false, arrayDeliveredOrder);
                clickInactive(text_pending, linear_pending_line);
                break;
        }
    }

    private void clickActive(TextView textView, LinearLayout linearLayout_line, boolean ispending, ArrayList<Orders> arrayOrder) {
        textView.setTextColor(getResources().getColor(R.color.app_color_blue));
        linearLayout_line.setBackgroundColor(getResources().getColor(R.color.app_color_blue));
        UserHistoryAdapter adapter_pending = new UserHistoryAdapter(getActivity(), arrayOrder, ispending);
        historyList.setAdapter(adapter_pending);
    }

    private void clickInactive(TextView textView, LinearLayout linearLayout_line) {
        textView.setTextColor(getResources().getColor(R.color.light_gray));
        linearLayout_line.setBackgroundColor(getResources().getColor(R.color.light_gray));
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent i = new Intent(getActivity(), UserOrderDetailActivity.class);
//        i.putExtra("ispending", isPending);
//        startActivity(i);
//    }


    @Override
    public void onResume() {
        super.onResume();
        if(sessionManager.isLoggedIn()) {
        if (CommonMethods.checkConnection()) {
            getHistorydata();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
        }
        }
    }
}
