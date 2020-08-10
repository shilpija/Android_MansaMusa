package com.freshhome.fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshhome.AdapterClass.RecyclerCartAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.LoginActivity;
import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.OrderCheckout;
import com.freshhome.R;
import com.freshhome.datamodel.Cart;
import com.freshhome.datamodel.subAttributes;
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
public class UserCartFragment extends Fragment implements View.OnClickListener {
    String fromCart = "fromCart";
    private String supplier_id = "";
    UserSessionManager sessionManager;
    ApiInterface apiInterface;
    RecyclerView recyclerCartList;
    LinearLayout linear_main, linear_no_data, linear_pre_order_line, linear_pre_order, linear_online_line, linear_online;//linear_checkout
    ArrayList<Cart> array_cart_online, array_cart_preorders;
    ImageView image_edit_address, image_edit_payment;
    TextView text_sub_total, text_next, text_online, text_pre_order;//, text_vat, text_delivery,text_total_price;
    double pre_order_total = 0.0, online_order_total = 0.0;
    boolean isOnline = true;
    String total_payment = "";

    public UserCartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_cart, container, false);
        array_cart_online = new ArrayList<>();
        array_cart_preorders = new ArrayList<>();
        MainActivity_NavDrawer.heading.setText(R.string.mycart);
        sessionManager = new UserSessionManager(getActivity());
        apiInterface = ApiClient.getInstance().getClient();
        image_edit_address = (ImageView) v.findViewById(R.id.image_edit_address);
        image_edit_address.setOnClickListener(this);
        image_edit_payment = (ImageView) v.findViewById(R.id.image_edit_payment);
        image_edit_payment.setOnClickListener(this);
        recyclerCartList = (RecyclerView) v.findViewById(R.id.recyclerCartList);

        linear_main = (LinearLayout) v.findViewById(R.id.linear_main);
        linear_main.setVisibility(View.GONE);

        linear_pre_order_line = (LinearLayout) v.findViewById(R.id.linear_pre_order_line);
//        linear_pre_order_line.setVisibility(View.GONE);

        linear_pre_order = (LinearLayout) v.findViewById(R.id.linear_pre_order);
        linear_pre_order.setOnClickListener(this);
        linear_online = (LinearLayout) v.findViewById(R.id.linear_online);
        linear_online.setOnClickListener(this);

        linear_online_line = (LinearLayout) v.findViewById(R.id.linear_online_line);
//        linear_online_line.setVisibility(View.GONE);

//        linear_checkout = (LinearLayout) v.findViewById(R.id.linear_checkout);
//        linear_checkout.setOnClickListener(this);

        text_sub_total = (TextView) v.findViewById(R.id.text_sub_total);
        text_next = (TextView) v.findViewById(R.id.text_next);
        text_next.setOnClickListener(this);
        text_online = (TextView) v.findViewById(R.id.text_online);
        text_pre_order = (TextView) v.findViewById(R.id.text_pre_order);
        //        text_delivery=(TextView)v.findViewById(R.id.text_delivery);
//        text_total_price=(TextView)v.findViewById(R.id.text_total_price);
        linear_no_data = (LinearLayout) v.findViewById(R.id.linear_no_data);
        linear_no_data.setVisibility(View.GONE);
        return v;
    }

    public void getCartdata() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.GetUserCart();
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
                            array_cart_online = new ArrayList<>();
                            array_cart_preorders = new ArrayList<>();
                            pre_order_total = 0.0;
                            online_order_total = 0.0;
                            JSONObject obj_sup = object.getJSONObject("supplier_info");
                            supplier_id = obj_sup.getString("supplier_id");
                            JSONArray jarray = obj_sup.getJSONArray("cart_data");
                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject obj = jarray.getJSONObject(i);
                                Cart cart = new Cart();
                                cart.setDish_id(obj.getString("menu_id"));
                                cart.setDish_name(obj.getString("dish_name"));
                                cart.setCurrency(obj.getString("currency"));
                                cart.setDish_image(obj.getString("dish_image"));
                                cart.setDish_qty(obj.getString("quantity"));
                                cart.setDish_price(obj.getString("dish_price"));
                                cart.setTotal_price(obj.getString("total_price"));
                                cart.setDish_status(obj.getString("status"));
                                cart.setSupplier_name(obj_sup.getString("name"));

                                String total_price = obj.getString("total_price");
                                if (total_price.contains(",")) {
                                    total_price = total_price.replace(",", "");
                                }

                                ArrayList<subAttributes> arrayAttributes = new ArrayList<>();
                                if (obj.has("options")) {
                                    JSONArray joptions = obj.getJSONArray("options");
                                    for (int j = 0; j < joptions.length(); j++) {
                                        JSONObject jobj = joptions.getJSONObject(j);
                                        subAttributes attributes = new subAttributes();
                                        attributes.setSubAttr_name(jobj.getString("value"));
                                        attributes.setSubAttr_main_name(jobj.getString("name"));
                                        attributes.setSubAttr_price(jobj.getString("price"));
                                        arrayAttributes.add(attributes);
                                    }
                                }
                                cart.setArrayAttributes(arrayAttributes);

                                if (obj.getString("status").equalsIgnoreCase("inactive")) {
                                    //inactive
                                    array_cart_preorders.add(cart);
                                    pre_order_total = Double.valueOf(total_price) + pre_order_total;
                                } else {
                                    //active
                                    array_cart_online.add(cart);
                                    online_order_total = Double.valueOf(total_price) + online_order_total;
                                }


                            }

                            if (object.has("cart_total")) {
                                total_payment = object.getString("cart_total");
                                JSONArray obj_array = object.getJSONArray("cart_total");
                                for (int i = 0; i < obj_array.length(); i++) {
                                    JSONObject obj_payment = obj_array.getJSONObject(i);
                                    if (obj_payment.getString("code").equalsIgnoreCase("sub_total")) {
                                        if (isOnline) {
                                            text_sub_total.setText(obj_payment.getString("currency") + " " + online_order_total);
                                        } else {
                                            text_sub_total.setText(obj_payment.getString("currency") + " " + pre_order_total);
                                        }
                                    }
//                                    else if (obj_payment.getString("code").equalsIgnoreCase("vat")) {
//                                        text_vat.setText(obj_payment.getString("currency") + " " + obj_payment.getString("total"));
//                                    } else if (obj_payment.getString("code").equalsIgnoreCase("delivery_charges")) {
//                                        text_delivery.setText(obj_payment.getString("currency") + " " + obj_payment.getString("total"));
//                                    } else if (obj_payment.getString("code").equalsIgnoreCase("total")) {
//                                        text_total_price.setText(obj_payment.getString("currency") + " " + obj_payment.getString("total"));
//                                    }
                                }
                            }

                            text_online.setText(getActivity().getResources().getString(R.string.online_orders)+"("+array_cart_online.size() +")");
                            text_pre_order.setText(getActivity().getResources().getString(R.string.pre_orders)+"("+array_cart_preorders.size() +")");
                            if (isOnline) {
                                setUpList(isOnline, array_cart_online);
                            } else {
                                setUpList(isOnline, array_cart_preorders);
                            }

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            if(obj.getString("msg").equalsIgnoreCase("Your cart is empty")){
                                sessionManager.saveSupplierInfo("");
                            }
//                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
                            linear_main.setVisibility(View.GONE);
                            linear_no_data.setVisibility(View.VISIBLE);
                            sessionManager.save_cart_item ("");
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
            case R.id.image_edit_payment:
                break;

            case R.id.image_edit_address:
                break;

            case R.id.text_next:
                if (sessionManager.isLoggedIn()) {
                    Intent intent = new Intent(getActivity(), OrderCheckout.class);
                    if (isOnline) {
                        intent.putExtra("order_items", array_cart_online);
                    } else {
                        intent.putExtra("order_items", array_cart_preorders);
                    }
                    intent.putExtra("supplier_id", supplier_id);
                    intent.putExtra("isOnline", isOnline);
                    intent.putExtra("total_payment", total_payment);
                    intent.putExtra("from", "checkout");
                    startActivity(intent);
                }else {
                    ShowLoginDialog(getActivity());
                }
                break;

            case R.id.linear_online:
                isOnline = true;
                clickActive(text_online, linear_online_line, isOnline, array_cart_online);
                clickInactive(text_pre_order, linear_pre_order_line);
                break;

            case R.id.linear_pre_order:
                isOnline = false;
                clickActive(text_pre_order, linear_pre_order_line, isOnline, array_cart_preorders);
                clickInactive(text_online, linear_online_line);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO : CHECK IF LOGGEDIN THEN HIT API
        if (sessionManager.isLoggedIn()) {
            if (CommonMethods.checkConnection()) {
                getCartdata();
            } else {
                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
            }
        } else {
            getCartdata();
//            linear_main.setVisibility(View.GONE);
//            linear_no_data.setVisibility(View.VISIBLE);
        }
    }

    //    private void CheckOut() {
//        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
//        if (!progressDialog.isShowing()) {
//            progressDialog.show();
//        }
//        Call<JsonElement> calls = apiInterface.Checkout("2");
//
//        calls.enqueue(new Callback<JsonElement>() {
//            @Override
//            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                try {
//                    if (response.code() == 200) {
//                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
//                        if (object.getString("code").equalsIgnoreCase("200")) {
//                            JSONObject obj = object.getJSONObject("success");
//                            CommonUtilFunctions.success_Alert_Dialog(getActivity(), obj.getString("msg"));
//
//                        } else {
//                            JSONObject obj = object.getJSONObject("error");
//                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
//                            linear_main.setVisibility(View.GONE);
//                        }
//                    } else {
//                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<JsonElement> call, Throwable t) {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                call.cancel();
//                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
//            }
//        });
//    }

    private void clickActive(TextView textView, LinearLayout linearLayout_line, boolean isOnline, ArrayList<Cart> array_cart) {
        textView.setTextColor(getResources().getColor(R.color.app_color_blue));
        linearLayout_line.setBackgroundColor(getResources().getColor(R.color.app_color_blue));
        setUpList(isOnline, array_cart);
    }

    private void clickInactive(TextView textView, LinearLayout linearLayout_line) {
        textView.setTextColor(getResources().getColor(R.color.light_gray));
        linearLayout_line.setBackgroundColor(getResources().getColor(R.color.light_gray));
    }

    private void setUpList(boolean isOnline, ArrayList<Cart> array_cart) {
        if (array_cart_online.size() != 0 || array_cart_preorders.size() != 0) {
            linear_main.setVisibility(View.VISIBLE);
            linear_no_data.setVisibility(View.GONE);
            sessionManager.save_cart_item (String.valueOf (array_cart.size ()));
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerCartList.setLayoutManager(mLayoutManager);
            recyclerCartList.setItemAnimator(new DefaultItemAnimator());
            RecyclerCartAdapter adapter = new RecyclerCartAdapter(getActivity(), array_cart, UserCartFragment.this);
            recyclerCartList.setAdapter(adapter);
        } else {
            linear_main.setVisibility(View.GONE);
            linear_no_data.setVisibility(View.VISIBLE);
            sessionManager.save_cart_item ("");
        }

        if (isOnline) {
            text_sub_total.setText(String.valueOf(online_order_total));
            ActiveNext(online_order_total);
        } else {
            text_sub_total.setText(String.valueOf(pre_order_total));
            ActiveNext(pre_order_total);
        }
    }

    private void ActiveNext(double order_total) {
        if (order_total != 0.0) {
            text_next.setAlpha(1);
            text_next.setOnClickListener(this);
        } else {
            text_next.setAlpha((float) .5);
            text_next.setOnClickListener(null);
        }
    }

    public void ShowLoginDialog(final Context con) {
        final Dialog dialog = new Dialog(con);
        dialog.setContentView(R.layout.layout_loginfirst_dialog);
        dialog.setCanceledOnTouchOutside(false);
        TextView text_login = (TextView) dialog.findViewById(R.id.text_login);
        text_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(con, LoginActivity.class);
                i.putExtra("FROMCART",fromCart);
                con.startActivity(i);
            }
        });

        TextView text_cancel = (TextView) dialog.findViewById(R.id.text_cancel);
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
