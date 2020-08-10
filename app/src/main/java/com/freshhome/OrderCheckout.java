package com.freshhome;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.ccavenue.InitialScreenActivity;
import com.freshhome.datamodel.Cart;
import com.freshhome.datamodel.TotalBreakdown;
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

public class OrderCheckout extends AppCompatActivity implements View.OnClickListener {
    private String checkWallet = "false";
    UserSessionManager sessionManager;
    ApiInterface apiInterface;
    ImageView image_back;
    ImageView image_edit_address, image_edit_payment;
    LinearLayout linear_payment, linear_cart_items, linear_select_address, linear_select_payment, linear_delivery_datetime,
            linear_select_date, linear_select_time, linear_payment_details, linear_payment_option;
    int address_Code = 121, paymentMethod_Code = 122;
    String address_id = "", payMethod = "", total_payment = "", supplier_id = "", from = "";
    ArrayList<Cart> array_cart;
    EditText edit_phone, edit_requirement;
    TextView text_deliveryaddress, text_payment_method, text_deliverydate,
            text_deliverytime, text_total_price;
    boolean isOnline = false;
    double sub_price = 0.0;
    String currency = "", card_id = "", pay_type = "";
    ArrayList<TotalBreakdown> totalpaymentArray;
    boolean isWalletDeduction = false;
    private String order_Id = "";

    private String getTotalValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_checkout);
        totalpaymentArray = new ArrayList<>();
        sessionManager = new UserSessionManager(OrderCheckout.this);
        apiInterface = ApiClient.getInstance().getClient();

        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);

        edit_requirement = (EditText) findViewById(R.id.edit_requirement);
        edit_phone = (EditText) findViewById(R.id.edit_phone);

        text_deliverydate = (TextView) findViewById(R.id.text_deliverydate);
        text_deliverytime = (TextView) findViewById(R.id.text_deliverytime);

        text_total_price = (TextView) findViewById(R.id.text_total_price);

        text_deliveryaddress = (TextView) findViewById(R.id.text_deliveryaddress);
        text_payment_method = (TextView) findViewById(R.id.text_payment_method);
        text_payment_method.setText(getResources().getString(R.string.select_paymethod));

        image_edit_address = (ImageView) findViewById(R.id.image_edit_address);
        image_edit_payment = (ImageView) findViewById(R.id.image_edit_payment);

        linear_payment = (LinearLayout) findViewById(R.id.linear_payment);
        linear_payment.setOnClickListener(this);

        linear_payment_option = (LinearLayout) findViewById(R.id.linear_payment_option);

        linear_select_address = (LinearLayout) findViewById(R.id.linear_select_address);
        linear_select_address.setOnClickListener(this);

        linear_select_payment = (LinearLayout) findViewById(R.id.linear_select_payment);
        linear_select_payment.setOnClickListener(this);

        linear_cart_items = (LinearLayout) findViewById(R.id.linear_cart_items);
        linear_cart_items.setOnClickListener(this);

        linear_select_date = (LinearLayout) findViewById(R.id.linear_select_date);
        linear_select_date.setOnClickListener(this);
        linear_select_time = (LinearLayout) findViewById(R.id.linear_select_time);
        linear_select_time.setOnClickListener(this);
        linear_delivery_datetime = (LinearLayout) findViewById(R.id.linear_delivery_datetime);

        linear_payment_details = (LinearLayout) findViewById(R.id.linear_payment_details);

        from = getIntent().getStringExtra("from");
        if (from.equalsIgnoreCase("checkout")) {
            total_payment = getIntent().getStringExtra("total_payment");
            supplier_id = getIntent().getStringExtra("supplier_id");
            setPayments(total_payment);
            //check if order is online order or pre-order
            isOnline = getIntent().getBooleanExtra("isOnline", false);

            if (isOnline) {
                linear_delivery_datetime.setVisibility(View.GONE);
            } else {
                linear_delivery_datetime.setVisibility(View.VISIBLE);
            }

            array_cart = (ArrayList<Cart>) getIntent().getSerializableExtra("order_items");
        } else {
            order_Id = getIntent().getStringExtra("orderId");
            PostData();
        }

        for (int i = 0; i < array_cart.size(); i++) {
            AddItemDetail(array_cart.get(i));
        }

        if (isWalletDeduction) {
            pay_type = "wallet";
            text_payment_method.setText("Wallet");
        }
//        text_total_price.setText(currency + " " + String.valueOf(total_price));
    }

    private void setPayments(String total_payment) {
        try {
            JSONArray array = new JSONArray(total_payment);
            totalpaymentArray = new ArrayList<>();
            totalpaymentArray.clear();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj_payment = array.getJSONObject(i);
                TotalBreakdown breakdown = new TotalBreakdown();
                if (obj_payment.getString("code").equalsIgnoreCase("total")) {
                    if (obj_payment.getString("total_value").equalsIgnoreCase("0")) {
                        isWalletDeduction = true;
                    }
                }
                if(obj_payment.getString("code").equalsIgnoreCase("wallet")){
                    checkWallet = "true";
                }
                breakdown.setCode(obj_payment.getString("code"));
                breakdown.setLabel(obj_payment.getString("label"));
                breakdown.setTotal(obj_payment.getString("total"));
                breakdown.setCurrency(obj_payment.getString("currency"));
                breakdown.setTotal_value(obj_payment.getString("total_value"));
                getTotalValue = obj_payment.getString("total_value");
                totalpaymentArray.add(breakdown);
            }

            setUpPaymentDetailUI(totalpaymentArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setUpPaymentDetailUI(ArrayList<TotalBreakdown> totalpaymentArray) {
        linear_payment_details.removeAllViews();
        for (int i = 0; i < totalpaymentArray.size(); i++) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemview = inflater.inflate(R.layout.single_row_payment_info, null);
            TextView text_label = (TextView) itemview.findViewById(R.id.text_label);

            if (checkWallet.equalsIgnoreCase("true")) {
                if (payMethod.equalsIgnoreCase("Cash on delivery")) {
                    if (!totalpaymentArray.get(i).getCode().equalsIgnoreCase("wdtotal") && !totalpaymentArray.get(i).getCode().equalsIgnoreCase("wallet")) {
                        text_label.setText(totalpaymentArray.get(i).getLabel());
                        TextView text_amount = (TextView) itemview.findViewById(R.id.text_amount);
                        text_amount.setText(totalpaymentArray.get(i).getCurrency() + " " + totalpaymentArray.get(i).getTotal());
                        linear_payment_details.addView(itemview);

                    }}else {
                        if (!totalpaymentArray.get(i).getCode().equalsIgnoreCase("total") && !totalpaymentArray.get(i).getCode().equalsIgnoreCase("cod_delivery_charges") &&
                                !totalpaymentArray.get(i).getCode().equalsIgnoreCase("wallet1")) {
                            text_label.setText(totalpaymentArray.get(i).getLabel());
                            TextView text_amount = (TextView) itemview.findViewById(R.id.text_amount);
                            text_amount.setText(totalpaymentArray.get(i).getCurrency() + " " + totalpaymentArray.get(i).getTotal());
                            linear_payment_details.addView(itemview);
                        }
                    }
            } else {

                if (payMethod.equalsIgnoreCase("Cash on delivery")) {
                    if (!totalpaymentArray.get(i).getCode().equalsIgnoreCase("wdtotal")) {
                        text_label.setText(totalpaymentArray.get(i).getLabel());
                        TextView text_amount = (TextView) itemview.findViewById(R.id.text_amount);
                        text_amount.setText(totalpaymentArray.get(i).getCurrency() + " " + totalpaymentArray.get(i).getTotal());
                        linear_payment_details.addView(itemview);

                    }
                } else {
                    if (!totalpaymentArray.get(i).getCode().equalsIgnoreCase("total") && !totalpaymentArray.get(i).getCode().equalsIgnoreCase("cod_delivery_charges")) {
                        text_label.setText(totalpaymentArray.get(i).getLabel());
                        TextView text_amount = (TextView) itemview.findViewById(R.id.text_amount);
                        text_amount.setText(totalpaymentArray.get(i).getCurrency() + " " + totalpaymentArray.get(i).getTotal());
                        linear_payment_details.addView(itemview);
                    }
                }


            }
        }
    }

    private void AddItemDetail(Cart cart) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemview = inflater.inflate(R.layout.cart_item_layout, null);
        TextView text_item_name = (TextView) itemview.findViewById(R.id.text_item_name);
        text_item_name.setText(cart.getDish_name() + " (" + cart.getDish_qty() + ") ");
        TextView text_item_price = (TextView) itemview.findViewById(R.id.text_item_price);
        text_item_price.setText(cart.getCurrency() + " " + cart.getTotal_price());
//        sub_price = Double.valueOf(sub_price) + Double.valueOf(cart.getTotal_price());
        currency = cart.getCurrency();
        linear_cart_items.addView(itemview);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                OrderCheckout.this.finish();
                break;

            case R.id.linear_payment:
                if (isOnline) {
                    CheckValidation_Online();
                } else {
                    CheckValidationPre_Order();
                }
                break;

            case R.id.linear_select_address:
                Intent i = new Intent(OrderCheckout.this, UserAddressListActivity.class);
                i.putExtra("fromCart", false);
                startActivityForResult(i, address_Code);
                break;

            case R.id.linear_select_payment:
                if (!isWalletDeduction) {
                    Intent i_payment = new Intent(OrderCheckout.this, UserPaymentMethodFragment.class);
                    i_payment.putExtra("isOnline", isOnline);
                    i_payment.putExtra("price", getTotalValue);
                    startActivityForResult(i_payment, paymentMethod_Code);
                }
                break;

            case R.id.linear_select_date:
                CommonUtilFunctions.DatePickerFuture(OrderCheckout.this, text_deliverydate);
                break;

            case R.id.linear_select_time:
                CommonUtilFunctions.timepickerCheckout(OrderCheckout.this, text_deliverytime);
                break;

        }
    }

    private void CheckValidationPre_Order() {
        if (Integer.parseInt(getTotalValue) < 100) {
            CommonUtilFunctions.Error_Alert_Dialog(OrderCheckout.this, getResources().getString(R.string.minimus_hundred));
        } else if (text_deliverydate.getText().toString().trim().equalsIgnoreCase("")
                || text_deliverydate.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.select_date))) {
            CommonUtilFunctions.Error_Alert_Dialog(OrderCheckout.this, getResources().getString(R.string.select_date));
        } else if (text_deliverytime.getText().toString().trim().equalsIgnoreCase("")
                || text_deliverytime.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.select_time))) {
            CommonUtilFunctions.Error_Alert_Dialog(OrderCheckout.this, getResources().getString(R.string.select_time));
        } else {
            if (!edit_phone.getText().toString().equalsIgnoreCase("")) {
                if (CommonMethods.isValidMobile(edit_phone.getText().toString())) {
                    PostData();
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(OrderCheckout.this, getResources().getString(R.string.valid_phone));
                }
            } else {
                PostData();
            }
        }
    }

    private void CheckValidation_Online() {
        if (!edit_phone.getText().toString().equalsIgnoreCase("")) {
            if (CommonMethods.isValidMobile(edit_phone.getText().toString())) {
                PostData();
            } else {
                CommonUtilFunctions.Error_Alert_Dialog(OrderCheckout.this, getResources().getString(R.string.valid_phone));
            }
        } else {
            PostData();
        }
    }

    private void CheckOut(String address_id, String payment_type, String card_id) {
        final ProgressDialog progressDialog = new ProgressDialog(OrderCheckout.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls;
        if (isOnline) {
            if(!isWalletDeduction) {
                calls = apiInterface.Checkout_Online(address_id,
                        edit_phone.getText().toString(), edit_requirement.getText().toString(), "online",
                        payment_type, card_id);
            }else {
                calls = apiInterface.Checkout_Online(address_id,
                        edit_phone.getText().toString(), edit_requirement.getText().toString(), "online",
                        payment_type, card_id);
            }
        } else {
            calls = apiInterface.Checkout_PreOrder(address_id,
                    edit_phone.getText().toString(), text_deliverytime.getText().toString().trim(),
                    text_deliverydate.getText().toString().trim(), edit_requirement.getText().toString(),
                    "pre-order", payment_type, card_id);
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
//                            CommonUtilFunctions.success_Alert_Dialog(OrderCheckout.this, obj.getString("msg"));
                            Successfull_Order_Dialog(obj.getString("msg"));
                            sessionManager.saveSupplierInfo("");
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(OrderCheckout.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(OrderCheckout.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(OrderCheckout.this, getResources().getString(R.string.server_error));
            }
        });
    }

    private void PostData() {
        if(!isWalletDeduction) {
            if (!checkWallet.equalsIgnoreCase("true")) {
                //remove the minimum order of 100 as we did in the website
               //sk if (Double.parseDouble(getTotalValue) < 100) {
                if (Double.parseDouble(getTotalValue) < 0) {
                    showDiffSellerLayout(this);
                    //CommonUtilFunctions.Error_Alert_Dialog(OrderCheckout.this, getResources().getString(R.string.minimus_hundred));
                } else if (address_id.equalsIgnoreCase("")) {
//            text_deliveryaddress.setTextColor(getResources().getColor(R.color.red));
                    CommonUtilFunctions.Error_Alert_Dialog(OrderCheckout.this, getResources().getString(R.string.select_address));
                } else if (!isWalletDeduction && payMethod.equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(OrderCheckout.this, getResources().getString(R.string.select_paymethod));
                } else {
                    if (CommonMethods.checkConnection()) {
                        if (pay_type.equalsIgnoreCase("card")) {
                            if (CommonMethods.checkConnection()) {
                                //subscribePLan(true);
                                Intent intent = new Intent(OrderCheckout.this, InitialScreenActivity.class);
                                intent.putExtra("price", getTotalValue);
                                intent.putExtra("from", "BuyerPay");
                                startActivity(intent);
                                finish();
                            } else {
                                CommonUtilFunctions.Error_Alert_Dialog(this, getResources().getString(R.string.internetconnection));
                            }
                        } else {
                            CheckOut(address_id, pay_type, card_id);
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(OrderCheckout.this, getResources().getString(R.string.internetconnection));
                    }
                }

            }else {
                walletAmountcheck();
            }


        }else {
            if (address_id.equalsIgnoreCase("")) {
                CommonUtilFunctions.Error_Alert_Dialog(OrderCheckout.this, getResources().getString(R.string.select_address));
            }else {
                CheckOut(address_id, pay_type, card_id);
            }

        }
    }


    private void walletAmountcheck(){
        if (address_id.equalsIgnoreCase("")) {
//            text_deliveryaddress.setTextColor(getResources().getColor(R.color.red));
            CommonUtilFunctions.Error_Alert_Dialog(OrderCheckout.this, getResources().getString(R.string.select_address));
        } else if (!isWalletDeduction && payMethod.equalsIgnoreCase("")) {
            CommonUtilFunctions.Error_Alert_Dialog(OrderCheckout.this, getResources().getString(R.string.select_paymethod));
        } else {
            if (CommonMethods.checkConnection()) {
                if (pay_type.equalsIgnoreCase("card")) {
                    if (CommonMethods.checkConnection()) {
                        //subscribePLan(true);
                        Intent intent = new Intent(OrderCheckout.this, InitialScreenActivity.class);
                        intent.putExtra("price", getTotalValue);
                        intent.putExtra("from", "BuyerPay");
                        startActivity(intent);
                        finish();
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(this, getResources().getString(R.string.internetconnection));
                    }
                } else {
                    CheckOut(address_id, pay_type, card_id);
                }
            } else {
                CommonUtilFunctions.Error_Alert_Dialog(OrderCheckout.this, getResources().getString(R.string.internetconnection));
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == address_Code && resultCode == Activity.RESULT_OK) {
            address_id = data.getStringExtra("address_id");
            text_deliveryaddress.setText(data.getStringExtra("address"));
//            text_deliveryaddress.setTextColor(getResources().getColor(R.color.black));

        } else if (requestCode == paymentMethod_Code && resultCode == Activity.RESULT_OK) {
            payMethod = data.getStringExtra("payMethod");
            if (payMethod.equalsIgnoreCase("Cash on delivery")) {
                text_payment_method.setText(payMethod);

                setPayments(total_payment);
            } else {
                text_payment_method.setText(payMethod + "\n" + data.getStringExtra("maskedno"));
            }
            card_id = data.getStringExtra("cardid");
            pay_type = data.getStringExtra("paytype");
        }
    }

    private void Successfull_Order_Dialog(String msg) {
        final Dialog dialog = new Dialog(OrderCheckout.this);
        dialog.setContentView(R.layout.layout_success_order);
        dialog.setCanceledOnTouchOutside(false);
        ImageView image_eat = (ImageView) dialog.findViewById(R.id.image_eat);
        Glide.with(OrderCheckout.this).load(R.raw.eating_g).into(image_eat);
        TextView text_msg = (TextView) dialog.findViewById(R.id.text_msg);
        text_msg.setText(msg);
        LinearLayout linear_done = (LinearLayout) dialog.findViewById(R.id.linear_done);
        linear_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderCheckout.this.finish();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public void showDiffSellerLayout(final Context context) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_less_amount_checkout_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        TextView tvAddMore = dialog.findViewById(R.id.tvAddMore);
        TextView tvyes = dialog.findViewById(R.id.tvyes);
        TextView tvCancel = dialog.findViewById(R.id.tvCancel);

//        tvyes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i_cart = new Intent(context, MainActivity_NavDrawer.class);
//                i_cart.putExtra("OpenFrag", ConstantValues.OPENCART);
//                context.startActivity(i_cart);
//                dialog.dismiss();
//            }
//        });

        tvyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i_menu = new Intent(context, SupplierMenuActivity.class);
                i_menu.putExtra("supplier_id", supplier_id);
                startActivity(i_menu);
                finish();
                dialog.dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}

