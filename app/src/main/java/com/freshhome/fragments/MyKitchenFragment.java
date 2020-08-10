package com.freshhome.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.freshhome.AdapterClass.NotificationReviewAdapter;
import com.freshhome.AdapterClass.OrderDeliveredAdapter;
import com.freshhome.AdapterClass.OrderPendingAdapter;
import com.freshhome.AdapterClass.OrderPreorderAdapter;
import com.freshhome.AdapterClass.RecyclerCartAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.RecyclerItemClickListener;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.LoginActivity;
import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.OrderDetailActivity;
import com.freshhome.R;
import com.freshhome.datamodel.MyKitchenOrders;
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

public class MyKitchenFragment extends Fragment implements View.OnClickListener {
    LinearLayout linear_pending, linear_pending_line, linear_preorder, linear_preorder_line, linear_delivered, linear_delivered_line;
    RecyclerView myKitchenListpending, myKitchenListDelivered, myKitchenListpreorder;
    ImageView image_back;
    View v;
    String type = "";
    ApiInterface apiInterface;
    ArrayList<MyKitchenOrders> arrayPendingOrders, arrayPreOrders, arrayDeliveredOrders;
    UserSessionManager sessionManager;

    public MyKitchenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.activity_my_kitchen, container, false);
        sessionManager = new UserSessionManager(getActivity());
        arrayPendingOrders = new ArrayList<>();
        arrayPreOrders = new ArrayList<>();
        arrayDeliveredOrders = new ArrayList<>();
        apiInterface = ApiClient.getInstance().getClient();

        linear_preorder = (LinearLayout) v.findViewById(R.id.linear_preorder);
        linear_preorder_line = (LinearLayout) v.findViewById(R.id.linear_preorder_line);
        linear_preorder_line.setVisibility(View.GONE);
        linear_preorder.setOnClickListener(this);

        linear_pending = (LinearLayout) v.findViewById(R.id.linear_pending);
        linear_pending_line = (LinearLayout) v.findViewById(R.id.linear_pending_line);
        linear_pending_line.setVisibility(View.VISIBLE);
        linear_pending.setOnClickListener(this);

        linear_delivered = (LinearLayout) v.findViewById(R.id.linear_delivered);
        linear_delivered_line = (LinearLayout) v.findViewById(R.id.linear_delivered_line);
        linear_delivered_line.setVisibility(View.GONE);
        linear_delivered.setOnClickListener(this);

        myKitchenListpending = (RecyclerView) v.findViewById(R.id.myKitchenListpending);
        myKitchenListpreorder = (RecyclerView) v.findViewById(R.id.myKitchenListpreorder);
        myKitchenListDelivered = (RecyclerView) v.findViewById(R.id.myKitchenListDelivered);

        myKitchenListDelivered.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                myKitchenListDelivered, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(getActivity(), OrderDetailActivity.class);
                i.putExtra("type", type);
                i.putExtra("order_id", arrayDeliveredOrders.get(position).getOrder_id());
                startActivity(i);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));
//        MainActivity_NavDrawer.heading.setText(R.string.kitchen);
        MainActivity_NavDrawer.image_addmenu.setVisibility(View.GONE);
        setUpTitles();

        //TODO : CHECK IF LOGGEDIN THEN HIT API
       /*sk if (sessionManager.isLoggedIn()) {
            if (CommonMethods.checkConnection()) {
                GetData();
                //clickPreOrder();
            } else {
                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
            }
        } else {
            CommonMethods.ShowLoginDialog(getActivity());
        }*/

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sessionManager.isLoggedIn()) {
            if (CommonMethods.checkConnection()) {
                GetData();
                //clickPreOrder();
            } else {
                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
            }
        } else {
            CommonMethods.ShowLoginDialog(getActivity());
        }
    }

    private void setUpTitles() {
        if (!sessionManager.getUserDetails().get(UserSessionManager.KEY_SUPPLIER_TYPE).equalsIgnoreCase(ConstantValues.home_food)) {
            linear_preorder.setVisibility(View.VISIBLE);// this is Gone (before) client required...
            linear_preorder_line.setVisibility(View.GONE);
            MainActivity_NavDrawer.heading.setText(R.string.my_orders);
        } else {
            linear_preorder.setVisibility(View.VISIBLE);
            linear_preorder_line.setVisibility(View.GONE);
            MainActivity_NavDrawer.heading.setText(R.string.my_order);

        }
    }

    private void GetData() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;
        if (!sessionManager.getUserDetails().get(UserSessionManager.KEY_SUPPLIER_TYPE).equalsIgnoreCase(ConstantValues.home_food)) {
            //means home products and handmade
            calls = apiInterface.GetMyOrders();
        } else {
            //home food
            calls = apiInterface.GetMyKitchenOrders();
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
                        arrayPendingOrders = new ArrayList<>();
                        arrayPreOrders = new ArrayList<>();
                        arrayDeliveredOrders = new ArrayList<>();
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            if(object.has("pre_orders")){
                            JSONArray jsonArray_pre = object.getJSONArray("pre_orders");
                            for (int i = 0; i < jsonArray_pre.length(); i++) {
                                JSONObject obj = jsonArray_pre.getJSONObject(i);
                                MyKitchenOrders orders = new MyKitchenOrders();
                                orders.setOrder_id(obj.getString("order_id"));
                                orders.setCurrency_code(obj.getString("currency_code"));
                                orders.setDate(obj.getString("date"));
                                orders.settime(obj.getString("time"));
                                orders.setTotal(obj.getString("total"));
                                orders.setPayment_method(obj.getString("payment_method"));
                                orders.setOrder_date(obj.getString("order_date"));
                                orders.setOrder_status(obj.getString("order_status"));
                                orders.setTotal_items(obj.getString("total_items"));
                                JSONObject jobj = obj.getJSONObject("user_info");
                                orders.setuserName(jobj.getString("name"));
                                orders.setUser_profile_pic(jobj.getString("profile_pic"));
                                arrayPreOrders.add(orders);
                            }}
                            JSONArray jsonArray_pending = object.getJSONArray("pending_orders");
                            for (int j = 0; j < jsonArray_pending.length(); j++) {
                                JSONObject obj = jsonArray_pending.getJSONObject(j);
                                MyKitchenOrders orders = new MyKitchenOrders();
                                orders.setOrder_id(obj.getString("order_id"));
                                orders.setCurrency_code(obj.getString("currency_code"));
                                orders.setDate(obj.getString("date"));
                                orders.settime(obj.getString("time"));
                                orders.setTotal(obj.getString("total"));
                                orders.setPayment_method(obj.getString("payment_method"));
                                orders.setOrder_date(obj.getString("order_date"));
                                orders.setOrder_status(obj.getString("order_status"));
                                orders.setTotal_items(obj.getString("total_items"));
                                JSONObject jobj = obj.getJSONObject("user_info");
                                orders.setuserName(jobj.getString("name"));
                                orders.setUser_profile_pic(jobj.getString("profile_pic"));

                                arrayPendingOrders.add(orders);
                            }
                            JSONArray jsonArray_delivered = object.getJSONArray("completed_orders");
                            for (int k = 0; k < jsonArray_delivered.length(); k++) {
                                JSONObject obj = jsonArray_delivered.getJSONObject(k);
                                MyKitchenOrders orders = new MyKitchenOrders();
                                orders.setOrder_id(obj.getString("order_id"));
                                orders.setCurrency_code(obj.getString("currency_code"));
                                orders.setDate(obj.getString("date"));
                                orders.settime(obj.getString("time"));
                                orders.setTotal(obj.getString("total"));
                                orders.setPayment_method(obj.getString("payment_method"));
                                orders.setOrder_date(obj.getString("order_date"));
                                orders.setOrder_status(obj.getString("order_status"));
                                orders.setTotal_items(obj.getString("total_items"));
                                JSONObject jobj = obj.getJSONObject("user_info");
                                orders.setuserName(jobj.getString("name"));
                                orders.setUser_profile_pic(jobj.getString("profile_pic"));
                                JSONObject r_obj = obj.getJSONObject("order_ratings");
                                orders.setRating(r_obj.getString("overall_rating"));
                                arrayDeliveredOrders.add(orders);
                            }
                            //clickPending();
                            clickPreOrder();

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
                    }
                } catch (
                        JSONException e)

                {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_preorder:
                clickPreOrder();
                break;

            case R.id.linear_pending:
                clickPending();
                break;

            case R.id.linear_delivered:
                clickDelivered();
                break;

        }
    }

    private void clickDelivered() {
        type = "delivered";
        linear_preorder_line.setVisibility(View.GONE);
        linear_delivered_line.setVisibility(View.VISIBLE);
        linear_pending_line.setVisibility(View.GONE);
        myKitchenListDelivered.setVisibility(View.VISIBLE);
        myKitchenListpending.setVisibility(View.GONE);
        myKitchenListpreorder.setVisibility(View.GONE);
        myKitchenListDelivered = (RecyclerView) v.findViewById(R.id.myKitchenListDelivered);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        myKitchenListDelivered.setLayoutManager(mLayoutManager);
        myKitchenListDelivered.setItemAnimator(new DefaultItemAnimator());
        OrderDeliveredAdapter adapter = new OrderDeliveredAdapter(getActivity(), arrayDeliveredOrders);
        myKitchenListDelivered.setAdapter(adapter);
    }

    private void clickPending() {
        type = "pending";
        linear_preorder_line.setVisibility(View.GONE);
        linear_delivered_line.setVisibility(View.GONE);
        linear_pending_line.setVisibility(View.VISIBLE);
        myKitchenListDelivered.setVisibility(View.GONE);
        myKitchenListpending.setVisibility(View.VISIBLE);
        myKitchenListpreorder.setVisibility(View.GONE);

        myKitchenListpending = (RecyclerView) v.findViewById(R.id.myKitchenListpending);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        myKitchenListpending.setLayoutManager(mLayoutManager);
        myKitchenListpending.setItemAnimator(new DefaultItemAnimator());
        OrderPendingAdapter adapter = new OrderPendingAdapter(getActivity(), arrayPendingOrders, type);
        myKitchenListpending.setAdapter(adapter);
    }

    private void clickPreOrder() {
        type = "pre_order";
        linear_preorder_line.setVisibility(View.VISIBLE);
        linear_delivered_line.setVisibility(View.GONE);
        linear_pending_line.setVisibility(View.GONE);
        myKitchenListDelivered.setVisibility(View.GONE);
        myKitchenListpending.setVisibility(View.GONE);
        myKitchenListpreorder.setVisibility(View.VISIBLE);
        myKitchenListpreorder = (RecyclerView) v.findViewById(R.id.myKitchenListpreorder);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        myKitchenListpreorder.setLayoutManager(mLayoutManager);
        myKitchenListpreorder.setItemAnimator(new DefaultItemAnimator());
        OrderPreorderAdapter adapter = new OrderPreorderAdapter(getActivity(), arrayPendingOrders, type);
        //OrderPreorderAdapter adapter = new OrderPreorderAdapter(getActivity(), arrayPreOrders, type);
        myKitchenListpreorder.setAdapter(adapter);


    }
}
