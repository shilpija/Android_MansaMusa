package com.freshhome;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.datamodel.Cart;
import com.freshhome.datamodel.MyKitchenOrders;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout linear_order, linear_orderdetail, linear_delivery, linear_deliverydetail,
            linear_items, linear_order_ready, linear_timmer, linear_req, linear_phn, linear_ratings, linear_delivery_person_details, linear_accept_reject;
    ImageView image_back;
    String order_id = "", cancel_msg = "";
    ApiInterface apiInterface;
    TextView text_order_no, text_user_name, text_order_date, text_additional_phn,
            text_additional_req, text_timer, text_collection_time, text_order_finish, text_review, text_order_ready, text_cancel,
            text_delivery_phn, text_delivery_name, text_cancel_order, text_accept, text_reject,
            text_freshomme_fee, text_sub_total, text_total, text_transactions, text_vat, text_total_earning;
    CircleImageView image_user, image_driver;
    ArrayList<Cart> arrayListOrderItems;
    RatingBar ratingBar_overall, ratingtaste, ratingBar_presentation, ratingBar_packing;
    CountDownTimer timer;
    BottomSheetDialog dialog;
    LinearLayout linear_send;
    EditText edit_msg;
    UserSessionManager sessionManager;

    TextView sub_total_title, mansamusa_fee_title, transaction_title, vat_title,total_earning_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        arrayListOrderItems = new ArrayList<>();
        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(OrderDetailActivity.this);
        linear_order = (LinearLayout) findViewById(R.id.linear_order);
        linear_order.setOnClickListener(this);
        linear_delivery = (LinearLayout) findViewById(R.id.linear_delivery);
        linear_delivery.setOnClickListener(this);
        linear_order_ready = (LinearLayout) findViewById(R.id.linear_order_ready);
        linear_order_ready.setOnClickListener(this);
        linear_timmer = (LinearLayout) findViewById(R.id.linear_timmer);
        linear_orderdetail = (LinearLayout) findViewById(R.id.linear_orderdetail);
        linear_deliverydetail = (LinearLayout) findViewById(R.id.linear_deliverydetail);
        linear_items = (LinearLayout) findViewById(R.id.linear_items);
        linear_delivery_person_details = (LinearLayout) findViewById(R.id.linear_delivery_person_details);
        linear_accept_reject = (LinearLayout) findViewById(R.id.linear_accept_reject);
        linear_req = (LinearLayout) findViewById(R.id.linear_req);
        linear_phn = (LinearLayout) findViewById(R.id.linear_phn);
        linear_ratings = (LinearLayout) findViewById(R.id.linear_ratings);
        text_order_no = (TextView) findViewById(R.id.text_order_no);
        text_user_name = (TextView) findViewById(R.id.text_user_name);
        text_order_date = (TextView) findViewById(R.id.text_order_date);
        text_additional_phn = (TextView) findViewById(R.id.text_additional_phn);
        text_additional_req = (TextView) findViewById(R.id.text_additional_req);
        text_timer = (TextView) findViewById(R.id.text_timer);
      //sk  text_collection_time = (TextView) findViewById(R.id.text_collection_time);
        text_order_finish = (TextView) findViewById(R.id.text_order_finish);
        text_review = (TextView) findViewById(R.id.text_review);
        text_order_ready = (TextView) findViewById(R.id.text_order_ready);
        text_delivery_phn = (TextView) findViewById(R.id.text_delivery_phn);
        text_delivery_name = (TextView) findViewById(R.id.text_delivery_name);

        text_freshomme_fee = (TextView) findViewById(R.id.text_freshomme_fee);
        text_sub_total = (TextView) findViewById(R.id.text_sub_total);
       //sk text_total = (TextView) findViewById(R.id.text_total);
        text_total_earning = (TextView) findViewById(R.id.text_total_earning);
        text_transactions = (TextView) findViewById(R.id.text_transactions);
        text_vat = (TextView) findViewById(R.id.text_vat);
        text_accept = (TextView) findViewById(R.id.text_accept);
        text_accept.setOnClickListener(this);
        text_reject = (TextView) findViewById(R.id.text_reject);
        text_reject.setOnClickListener(this);
        ratingBar_overall = (RatingBar) findViewById(R.id.ratingBar_overall);
        ratingtaste = (RatingBar) findViewById(R.id.ratingtaste);
        ratingBar_presentation = (RatingBar) findViewById(R.id.ratingBar_presentation);
        ratingBar_packing = (RatingBar) findViewById(R.id.ratingBar_packing);

        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);
        image_user = (CircleImageView) findViewById(R.id.image_user);
        image_driver = (CircleImageView) findViewById(R.id.image_driver);

        text_cancel_order = (TextView) findViewById(R.id.text_cancel_order);
        text_cancel_order.setOnClickListener(this);

        sub_total_title = (TextView) findViewById(R.id.sub_total_title);
        mansamusa_fee_title = (TextView) findViewById(R.id.mansamusa_fee_title);
        transaction_title = (TextView) findViewById(R.id.transaction_title);
        vat_title = (TextView) findViewById(R.id.vat_title);
        total_earning_title = (TextView) findViewById(R.id.total_earning_title);

        order_id = getIntent().getStringExtra("order_id");

        if (getIntent().getStringExtra("type").equalsIgnoreCase("delivered") ||
                getIntent().getStringExtra("type").equalsIgnoreCase("completed")) {
            linear_delivery.setClickable(true);
            linear_delivery.setAlpha(1);
            linear_delivery.setOnClickListener(this);
            linear_order_ready.setVisibility(View.GONE);
            text_order_finish.setVisibility(View.GONE);
            text_timer.setVisibility(View.GONE);
            linear_ratings.setVisibility(View.VISIBLE);
            linear_accept_reject.setVisibility(View.GONE);
            text_cancel_order.setVisibility(View.GONE);

        } else if (getIntent().getStringExtra("type").equalsIgnoreCase("pre_order")) {
            linear_ratings.setVisibility(View.GONE);
            linear_order_ready.setVisibility(View.GONE);
            /*sk linear_delivery.setClickable(false);
            linear_delivery.setAlpha((float) .2);
            linear_delivery.setOnClickListener(null);*/

            linear_delivery.setClickable(true);
            linear_delivery.setAlpha(1);
            linear_delivery.setOnClickListener(this);

            text_order_finish.setVisibility(View.GONE);
//            text_cancel_order.setVisibility(View.VISIBLE);
            text_timer.setVisibility(View.GONE);
//            linear_accept_reject.setVisibility(View.VISIBLE);

        } else {
            linear_ratings.setVisibility(View.GONE);
            linear_order_ready.setVisibility(View.VISIBLE);
            linear_delivery.setClickable(false);
            linear_delivery.setAlpha((float) .2);
            linear_delivery.setOnClickListener(null);
            text_order_finish.setVisibility(View.VISIBLE);
            linear_accept_reject.setVisibility(View.GONE);
            text_cancel_order.setVisibility(View.VISIBLE);
        }

        //if order cancel change status of text_order_ready btn


        selectOrder();
        if (CommonMethods.checkConnection()) {
            GetData();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(OrderDetailActivity.this, getResources().getString(R.string.internetconnection));
        }

    }

    private void GetData() {
        final ProgressDialog progressDialog = new ProgressDialog(OrderDetailActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetOrdersDetail(order_id);
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
                            arrayListOrderItems = new ArrayList<>();
                            JSONObject obj = object.getJSONObject("order_info");
                            text_order_no.setText(getResources().getString(R.string.order_no) + " " + obj.getString("order_id") + "(" + obj.getString("order_status") + ")");
                            //hide and show cancel button
                            if (obj.getString("allow_cancel").equalsIgnoreCase("true")) {
                                text_cancel_order.setVisibility(View.VISIBLE);
                            } else {
                                text_cancel_order.setVisibility(View.GONE);
                            }
                            if (obj.getString("order_status").equalsIgnoreCase("Pending")) {
                                linear_accept_reject.setVisibility(View.VISIBLE);
                                text_cancel_order.setVisibility(View.GONE);
                                linear_order_ready.setVisibility(View.GONE);
                            } else if (obj.getString("order_status").equalsIgnoreCase("Processing")
                                    || obj.getString("order_status").equalsIgnoreCase("Cancelled")
                                    || obj.getString("order_status").equalsIgnoreCase("Completed")
                                    || obj.getString("order_status").equalsIgnoreCase("Rejected")
                                    || obj.getString("order_status").equalsIgnoreCase("Return")
                                    || obj.getString("order_status").equalsIgnoreCase("Out for Delivery")
                                    || obj.getString("order_status").equalsIgnoreCase("New Order")) {
                                linear_accept_reject.setVisibility(View.GONE);
                                text_cancel_order.setVisibility(View.GONE);
                                linear_order_ready.setVisibility(View.GONE);
                            } else {
                                linear_accept_reject.setVisibility(View.GONE);
                                text_cancel_order.setVisibility(View.VISIBLE);
                                linear_order_ready.setVisibility(View.VISIBLE);
                            }
                            //set times

                            startTimer(obj.getString("time"));
                            if (obj.getString("additional_phone_number").equalsIgnoreCase("")) {
                                linear_phn.setVisibility(View.GONE);
                            } else {
                                text_additional_phn.setText(obj.getString("additional_phone_number"));
                                linear_phn.setVisibility(View.VISIBLE);
                             //sk   linear_phn.setVisibility(View.GONE);
                            }

                            if (obj.getString("additional_notes").equalsIgnoreCase("")) {
                                linear_req.setVisibility(View.GONE);
                            } else {
                                text_additional_req.setText(obj.getString("additional_notes"));
                                linear_req.setVisibility(View.VISIBLE);
                            }

                          //sk  text_order_date.setText(obj.getString("date"));
                            text_order_date.setText(obj.getString("order_date"));
                         //sk   text_collection_time.setText(obj.getString("time"));

                            JSONObject user_obj = obj.getJSONObject("user_info");
                            if (!user_obj.getString("profile_pic").equalsIgnoreCase(null) || !user_obj.getString("profile_pic").equalsIgnoreCase("")) {
                                Picasso.get().load(user_obj.getString("profile_pic")).into(image_user);
                            }
                            text_user_name.setText(user_obj.getString("name"));

                            JSONArray arrayItems = obj.getJSONArray("order_items");
                            for (int i = 0; i < arrayItems.length(); i++) {
                                JSONObject aobj = arrayItems.getJSONObject(i);
                                Cart cart = new Cart();
                                cart.setDish_id(aobj.getString("dish_id"));
                                cart.setDish_image(aobj.getString("dish_image"));
                                cart.setDish_name(aobj.getString("name"));
                                cart.setDish_qty(aobj.getString("quantity"));
                                cart.setTotal_price(aobj.getString("total"));
                                cart.setDish_price(aobj.getString("price"));
                                arrayListOrderItems.add(cart);
                            }
                            setUpItemsOnViews(arrayListOrderItems, obj.getString("currency_code"));

                            JSONObject robj = obj.getJSONObject("order_ratings");
                            ratingtaste.setRating(Float.parseFloat(CommonMethods.checkNullRatings(robj.getString("taste_rating"))));
                            ratingBar_overall.setRating(Float.parseFloat(CommonMethods.checkNullRatings(robj.getString("overall_rating"))));
                            ratingBar_packing.setRating(Float.parseFloat(CommonMethods.checkNullRatings(robj.getString("packing_rating"))));
                            ratingBar_presentation.setRating(Float.parseFloat(CommonMethods.checkNullRatings(robj.getString("presentation_rating"))));
                            text_review.setText(CommonMethods.checkNull(robj.getString("review")));

                            JSONArray obj_array = obj.getJSONArray("order_total");
                            for (int i = 0; i < obj_array.length(); i++) {
                                JSONObject obj_payment = obj_array.getJSONObject(i);
                                if (obj_payment.getString("code").equalsIgnoreCase("sub_total")) {
                                   //sk text_sub_total.setText(obj.getString("currency_code") + " " + obj_payment.getString("value"));
                                    text_sub_total.setText(obj_payment.getString("value"));
                                    sub_total_title.setText(obj_payment.getString("title"));
                                }else if(obj_payment.getString("code").equalsIgnoreCase("freshhomee_fee")){

                                    text_freshomme_fee.setText(obj_payment.getString("value"));
                                    mansamusa_fee_title.setText(obj_payment.getString("title"));

                                }else if(obj_payment.getString("code").equalsIgnoreCase("transaction_fee")){
                                    text_transactions.setText(obj_payment.getString("value"));
                                    transaction_title.setText(obj_payment.getString("title"));
                                }

                                else if (obj_payment.getString("code").equalsIgnoreCase("vat")) {
                                   //sk text_vat.setText(obj.getString("currency_code") + " " + obj_payment.getString("value"));
                                    text_vat.setText(obj_payment.getString("value"));
                                    vat_title.setText(obj_payment.getString("title"));
                                }
                                else if (obj_payment.getString("code").equalsIgnoreCase("total_earning")) {
                                  //sk  text_total.setText(obj.getString("currency_code") + " " + obj_payment.getString("value"));
                                    text_total_earning.setText( obj_payment.getString("value"));
                                    total_earning_title.setText( obj_payment.getString("title"));
                                }
                            }

                            cancel_msg = obj.getString("refund_text");

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(OrderDetailActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(OrderDetailActivity.this, getResources().getString(R.string.server_error));
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

    private void startTimer(String time) {
        text_timer.setText(CommonMethods.timeLeftForDeliverySec(time));
        //change the timer is not null
        if (timer != null) {
            timer.cancel();
        }
        //starttimer
        long timer_value = CommonMethods.timeLeftForDeliverySecond(time);
        timer_value = timer_value * 1000;
        timer = new CountDownTimer(timer_value, 1000) {
            public void onTick(long millisUntilFinished) {
                String time = CommonMethods.calculateTime(millisUntilFinished / 1000);
                text_timer.setText(time);
            }

            @Override
            public void onFinish() {
                text_timer.setText(getResources().getString(R.string.order_due));

            }
        }.start();
    }

    private void setUpItemsOnViews(ArrayList<Cart> arrayListOrderItems, String currencyCode) {
        for (int i = 0; i < arrayListOrderItems.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.single_row_supplier_orderitem, null);
            CircleImageView circle_image = (CircleImageView) view.findViewById(R.id.circle_image);
            TextView text_name = (TextView) view.findViewById(R.id.text_name);
            TextView text_qty = (TextView) view.findViewById(R.id.text_qty);
            TextView text_price = (TextView) view.findViewById(R.id.text_price);
            TextView text_total_price = (TextView) view.findViewById(R.id.text_total_price);
            text_name.setText(arrayListOrderItems.get(i).getDish_name());
            text_qty.setText(arrayListOrderItems.get(i).getDish_qty());
            text_price.setText(currencyCode + " " + arrayListOrderItems.get(i).getDish_price());
            text_total_price.setText(currencyCode + " " + arrayListOrderItems.get(i).getTotal_price());
            if (!arrayListOrderItems.get(i).getDish_image().equalsIgnoreCase(null) || !arrayListOrderItems.get(i).getDish_image().equalsIgnoreCase("")) {
                Picasso.get().load(arrayListOrderItems.get(i).getDish_image()).into(circle_image);
            }
            linear_items.addView(view);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_order:
                selectOrder();
                break;


            case R.id.linear_delivery:
                selectDelivery();
                break;

            case R.id.image_back:
                OrderDetailActivity.this.finish();
                break;
            case R.id.text_cancel_order:
                openCancelDialog(cancel_msg);
                break;

            case R.id.text_cancel:
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                break;

            case R.id.linear_send:
                if (edit_msg != null) {
                    if (edit_msg.getText().toString().equalsIgnoreCase("")) {
                        CommonUtilFunctions.Error_Alert_Dialog(OrderDetailActivity.this, getResources().getString(R.string.enter_msg));
                    } else {
                        if (CommonMethods.checkConnection()) {
                            CancelOrder(edit_msg.getText().toString());
                        } else {
                            CommonUtilFunctions.Error_Alert_Dialog(OrderDetailActivity.this, getResources().getString(R.string.internetconnection));
                        }
                    }
                }
                break;

            case R.id.text_reject:
                RejectOrderDialog(order_id);
                break;

            case R.id.text_accept:
                if (CommonMethods.checkConnection()) {
                    AcceptRejectOrder(order_id, true, "", "");
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(OrderDetailActivity.this, getResources().getString(R.string.internetconnection));
                }
                break;

            case R.id.linear_order_ready:
//                if (sessionManager.getSubscriptionDetails().get(UserSessionManager.KEY_STATUS).equalsIgnoreCase("active")) {
                    if (CommonMethods.checkConnection()) {
                        OrderisReady(order_id);
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(OrderDetailActivity.this, getResources().getString(R.string.internetconnection));
                    }
//                } else {
//                    CommonMethods.show_buy_plan(OrderDetailActivity.this);
//                }
                break;

        }
    }

    private void selectDelivery() {
        linear_order.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_outline_gray_round));
        linear_delivery.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_outline_green));
        linear_orderdetail.setVisibility(View.GONE);
        linear_deliverydetail.setVisibility(View.VISIBLE);
    }

    private void selectOrder() {
        linear_order.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_outline_green));
        linear_delivery.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_outline_gray_round));
        linear_orderdetail.setVisibility(View.VISIBLE);
        linear_deliverydetail.setVisibility(View.GONE);
    }

    private void openCancelDialog(String msg) {
        View view = getLayoutInflater().inflate(R.layout.layout_cancel_order, null);
        dialog = new BottomSheetDialog(OrderDetailActivity.this);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        TextView text_msg = (TextView) dialog.findViewById(R.id.text_msg);
        text_msg.setText(msg);
        linear_send = (LinearLayout) dialog.findViewById(R.id.linear_send);
        linear_send.setOnClickListener(this);
        text_cancel = (TextView) dialog.findViewById(R.id.text_cancel);
        text_cancel.setOnClickListener(this);
        edit_msg = (EditText) dialog.findViewById(R.id.edit_msg);
        dialog.show();
    }

    private void CancelOrder(String msg) {
        final ProgressDialog progressDialog = new ProgressDialog(OrderDetailActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.CancelOrder_Supplier(order_id, msg);

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
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            success_Alert_Dialog(OrderDetailActivity.this, obj.getString("success"));

                        } else {
                            JSONObject object = obj.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(OrderDetailActivity.this, object.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(OrderDetailActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(OrderDetailActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    public void success_Alert_Dialog(final Context context, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();

        // Setting Dialog Title
        alertDialog.setTitle("Success!");

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting Icon to Dialog
//        alertDialog.setIcon(R.drawable.call);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                alertDialog.dismiss();
                OrderDetailActivity.this.finish();
            }
        });

        // Showing Alert Message

        alertDialog.show();
    }

    private void AcceptRejectOrder(final String order_id, final boolean isAccept, String reject_type, String reject_msg) {
        final ProgressDialog progressDialog = new ProgressDialog(OrderDetailActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
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
                                text_cancel_order.setVisibility(View.VISIBLE);
                                linear_order_ready.setVisibility(View.VISIBLE);
                            } else {
                                text_order_no.setText(getResources().getString(R.string.order_no) + " " + order_id + "(" + "Rejected" + ")");
                            }
                            linear_accept_reject.setVisibility(View.GONE);
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(OrderDetailActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(OrderDetailActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(OrderDetailActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    private void RejectOrderDialog(final String order_id) {
        final Dialog dialog = new Dialog(OrderDetailActivity.this);
        dialog.setContentView(R.layout.layout_rejectorder_dialog);
        dialog.setCanceledOnTouchOutside(false);
        TextView text_save = (TextView) dialog.findViewById(R.id.text_save);
        TextView text_cancel = (TextView) dialog.findViewById(R.id.text_cancel);
        final Spinner spinner_reject = (Spinner) dialog.findViewById(R.id.spinner_reject);
        String[] reject_reason_Array = getResources().getStringArray(R.array.reject_reason);
        spinner_reject.setAdapter(new ArrayAdapter<>(OrderDetailActivity.this, R.layout.layout_spinner_text, reject_reason_Array));
        final EditText edit_text = (EditText) dialog.findViewById(R.id.edit_text);

        text_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonMethods.checkConnection()) {
                    dialog.dismiss();
                    AcceptRejectOrder(order_id, false, spinner_reject.getSelectedItem().toString(), edit_text.getText().toString().trim());
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(OrderDetailActivity.this, getResources().getString(R.string.internetconnection));
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


    private void OrderisReady(final String order_id) {
        final ProgressDialog progressDialog = new ProgressDialog(OrderDetailActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
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
                            CommonUtilFunctions.success_Alert_Dialog(OrderDetailActivity.this, object.getString("msg"));
                            text_order_no.setText(getResources().getString(R.string.order_no) + " " + order_id + "(" + "Processing" + ")");
                            linear_order_ready.setVisibility(View.GONE);
                            text_cancel_order.setVisibility(View.GONE);
                        } else {
                            JSONObject object = obj.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(OrderDetailActivity.this, object.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(OrderDetailActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(OrderDetailActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }
}
