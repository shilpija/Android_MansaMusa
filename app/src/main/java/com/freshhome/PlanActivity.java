package com.freshhome;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.FlowLayout;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.ccavenue.InitialScreenActivity;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView image_back, img_delete;
    LinearLayout linear_proceed, linear_cancel_sub, linear_make_payment, linear_plan_details, linear_sub_details, linear_plan_status;
    UserSessionManager sessionManager;
    ApiInterface apiInterface;
    TextView clickhere, plan_duration, plan_price, plan_name, text_details, text_cardno, text_plan_expiry_date, text_plan_status;
    String plan_id = "", card_id = "";
    private int CARD_CODE = 123;
    private String cmFrom = "";
    String pendingamount="", acStatus="",orderId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        if (getIntent() != null) {
            cmFrom = (String) getIntent().getStringExtra("payments");
        }
        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(PlanActivity.this);


        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);

        img_delete = (ImageView) findViewById(R.id.img_delete);
        img_delete.setOnClickListener(this);
        img_delete.setVisibility(View.GONE);

        linear_cancel_sub = (LinearLayout) findViewById(R.id.linear_cancel_sub);
        linear_cancel_sub.setOnClickListener(this);
        linear_make_payment = (LinearLayout) findViewById(R.id.linear_make_payment);
        linear_plan_details = (LinearLayout) findViewById(R.id.linear_plan_details);
        linear_sub_details = (LinearLayout) findViewById(R.id.linear_sub_details);
        linear_plan_status = (LinearLayout) findViewById(R.id.linear_plan_status);

        linear_proceed = (LinearLayout) findViewById(R.id.linear_proceed);
        linear_proceed.setOnClickListener(this);
        linear_proceed.setBackgroundColor(getResources().getColor(R.color.light_gray));


        text_plan_expiry_date = (TextView) findViewById(R.id.text_plan_expiry_date);
        text_plan_status = (TextView) findViewById(R.id.text_plan_status);

        text_cardno = (TextView) findViewById(R.id.text_cardno);
        text_cardno.setOnClickListener(this);
        plan_duration = (TextView) findViewById(R.id.plan_duration);
        plan_price = (TextView) findViewById(R.id.plan_price);
        plan_name = (TextView) findViewById(R.id.plan_name);
        text_details = (TextView) findViewById(R.id.text_details);


        if (cmFrom != null && !cmFrom.equalsIgnoreCase("")) {
            if (cmFrom.equalsIgnoreCase("Payment")) {

                orderId = getIntent().getStringExtra("orderId");
                subscribePLan(true);
            }
        } else {
            if (CommonMethods.checkConnection()) {
                GetPlans();
            } else {
                CommonUtilFunctions.Error_Alert_Dialog(PlanActivity.this, getResources().getString(R.string.internetconnection));
            }
        }
    }

    private void enableSubdetails() {
        linear_sub_details.setVisibility(View.VISIBLE);
        text_plan_expiry_date.setText(sessionManager.getSubscriptionDetails().get(UserSessionManager.KEY_SUB_END));
        text_plan_status.setText(sessionManager.getSubscriptionDetails().get(UserSessionManager.KEY_SUB_STATUS));
        linear_cancel_sub.setVisibility(View.VISIBLE);
        linear_make_payment.setVisibility(View.GONE);
        linear_plan_details.setVisibility(View.GONE);
        linear_plan_status.setVisibility(View.VISIBLE);

    }

    private void enablePlanHitAPI() {
        if (sessionManager.getSubscriptionDetails().get(UserSessionManager.KEY_STATUS).equalsIgnoreCase("active")) {
            linear_sub_details.setVisibility(View.VISIBLE);
            text_plan_expiry_date.setText(sessionManager.getSubscriptionDetails().get(UserSessionManager.KEY_SUB_END));
            text_plan_status.setText(sessionManager.getSubscriptionDetails().get(UserSessionManager.KEY_SUB_STATUS));
            linear_plan_status.setVisibility(View.GONE);
        } else {
            linear_sub_details.setVisibility(View.GONE);
        }
        linear_cancel_sub.setVisibility(View.GONE);
        linear_make_payment.setVisibility(View.VISIBLE);
        linear_plan_details.setVisibility(View.VISIBLE);
        text_cardno.setText(getResources().getString(R.string.select_card));
        text_cardno.setOnClickListener(this);
        img_delete.setVisibility(View.GONE);
        card_id = "";
        linear_proceed.setBackgroundColor(getResources().getColor(R.color.light_gray));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                PlanActivity.this.finish();
                break;

            case R.id.linear_proceed:
                //if (card_id.equalsIgnoreCase("")) {
                //CommonUtilFunctions.Error_Alert_Dialog(PlanActivity.this, getResources().getString(R.string.select_card));
                //} else {
                if(acStatus.equalsIgnoreCase("yes")){
                    paymentOptionDialog();

                }else if(acStatus.equalsIgnoreCase("no")){
                    if (CommonMethods.checkConnection()) {
                        //subscribePLan(true);
                        Intent intent = new Intent(PlanActivity.this, InitialScreenActivity.class);
                        intent.putExtra("price", plan_price.getText().toString());
                        intent.putExtra("from", "SubPlan");
                        startActivity(intent);
                        finish();
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(PlanActivity.this, getResources().getString(R.string.internetconnection));
                    }
                }

                //}
                break;

            case R.id.img_delete:
                text_cardno.setText(getResources().getString(R.string.select_card));
                text_cardno.setOnClickListener(this);
                img_delete.setVisibility(View.GONE);
                card_id = "";
                linear_proceed.setBackgroundColor(getResources().getColor(R.color.light_gray));
                break;

            case R.id.text_cardno:
                Intent i = new Intent(PlanActivity.this, ChooseCard.class);
                i.putExtra("plan_id", plan_id);
                startActivityForResult(i, CARD_CODE);
                break;

            case R.id.linear_cancel_sub:
                show_confirmation_alert(PlanActivity.this, getResources().getString(R.string.cancel_plan_msg));
                break;


        }
    }

    private void GetPlans() {
        final ProgressDialog progressDialog = new ProgressDialog(PlanActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;
        if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToCook)) {
            //supplier
            calls = apiInterface.GetPlans("supplier");
        } else {
            //driver
            calls = apiInterface.GetPlans("delivery");
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
                            JSONArray jsonArray = object.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                plan_id = obj.getString("plan_id");
                                text_details.setText(Html.fromHtml(obj.getString("plan_description")));
                                plan_duration.setText("For " + obj.getString("months") + " Months");
                                plan_name.setText(obj.getString("plan_name"));
                                plan_price.setText(obj.getString("price"));
                            }
                            if (object.has("subscription_data")) {
                                JSONObject sub_obj = object.getJSONObject("subscription_data");
                                sessionManager.saveSubscriptionDetails(sub_obj.getString("status"),
                                        sub_obj.getString("subscription_status"),
                                        sub_obj.getString("subscription_start_format"),
                                        sub_obj.getString("subscription_end_format"));
                            }
                            if (object.has("is_bd")) {
                                JSONObject is_bd = object.getJSONObject("is_bd");
                                pendingamount = is_bd.getString("pendingamount");
                                acStatus = is_bd.getString("status");
                            }

                            if (sessionManager.getSubscriptionDetails().get(UserSessionManager.KEY_SUB_STATUS).equalsIgnoreCase("pending") || sessionManager.getSubscriptionDetails().get(UserSessionManager.KEY_SUB_STATUS).equalsIgnoreCase("active")) {
                                //means already subsribed to plan
                                enableSubdetails();
                            } else {
                                enablePlanHitAPI();
                            }
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(PlanActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(PlanActivity.this, getResources().getString(R.string.server_error));
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

    private void subscribePLan(final boolean isSubscribe) {
        final ProgressDialog progressDialog = new ProgressDialog(PlanActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;
        if (isSubscribe) {
            calls = apiInterface.SubscribePlan("1", orderId);
        } else {
            calls = apiInterface.CancelPlan("");
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
//                            if(isSubscribe) {
//                                JSONObject sub_obj = object.getJSONObject("subscription_data");
//                                sessionManager.saveSubscriptionDetails(sub_obj.getString("status"),
//                                        sub_obj.getString("subscription_status"),
//                                        sub_obj.getString("subscription_start_format"),
//                                        sub_obj.getString("subscription_end_format"));
                            //}
                            success_Alert_Dialog(PlanActivity.this, obj.getString("msg"), isSubscribe);
                            GetPlans();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(PlanActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(PlanActivity.this, getResources().getString(R.string.server_error));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CARD_CODE) {
            if (data != null && data.hasExtra("cardid")) {
                card_id = data.getStringExtra("cardid");
                text_cardno.setText(data.getStringExtra("maskedno"));
                text_cardno.setOnClickListener(null);
                img_delete.setVisibility(View.VISIBLE);
                linear_proceed.setBackgroundColor(getResources().getColor(R.color.app_color_blue));
            }
        }
    }

    private void success_Alert_Dialog(final Context context, String message, final boolean isSubscribe) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
        alertDialog.setTitle("Success!");
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }

                if (CommonMethods.checkConnection()) {
                    GetPlans();
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(PlanActivity.this, getResources().getString(R.string.internetconnection));
                }

            }
        });
        alertDialog.show();
    }

    public void show_confirmation_alert(final Context context, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
        alertDialog.setTitle("Warning!");
        alertDialog.setMessage(message);
        // Setting OK Button
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (CommonMethods.checkConnection()) {
                    subscribePLan(false);
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(PlanActivity.this, getResources().getString(R.string.internetconnection));
                }
            }
        });
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();

            }
        });
        alertDialog.show();
    }



    private void paymentOptionDialog() {
        final Dialog dialog = new Dialog(PlanActivity.this);
        dialog.setContentView(R.layout.dialog_payment_option_layout);
//        Dialog dialog = new Dialog(NormalMessageTrackActivity.this);
//        View view = getLayoutInflater().inflate(R.layout.dialog_cancel_order_layout, null, false);
//        dialog.setContentView(view);

        TextView tvWalletPay = (TextView)dialog.findViewById(R.id.tvWalletPay);
        TextView tvCardPay = (TextView)dialog.findViewById(R.id.tvCardPay);
        TextView tvCancel = (TextView)dialog.findViewById(R.id.tvCancel);

        tvCardPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonMethods.checkConnection()) {
                    //subscribePLan(true);
                    Intent intent = new Intent(PlanActivity.this, InitialScreenActivity.class);
                    intent.putExtra("price", plan_price.getText().toString());
                    startActivity(intent);
                    finish();
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(PlanActivity.this, getResources().getString(R.string.internetconnection));
                }
                dialog.dismiss();
            }
        });

        tvWalletPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subscribePlanfromWallet(true);

//                if(Double.valueOf(plan_price.getText().toString()) <= Double.valueOf(pendingamount)){
//                    subscribePlanfromWallet(true);
//                }else {
//                    Toast.makeText(PlanActivity.this, "not sufficient balance", Toast.LENGTH_SHORT).show();
//                }
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



    //subscritonPlan from wallet....

    private void subscribePlanfromWallet(final boolean isSubscribe) {
        final ProgressDialog progressDialog = new ProgressDialog(PlanActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;

            calls = apiInterface.subscribewallet("1", "");

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
//                            if(isSubscribe) {
//                                JSONObject sub_obj = object.getJSONObject("subscription_data");
//                                sessionManager.saveSubscriptionDetails(sub_obj.getString("status"),
//                                        sub_obj.getString("subscription_status"),
//                                        sub_obj.getString("subscription_start_format"),
//                                        sub_obj.getString("subscription_end_format"));
                            //}
                            success_Alert_Dialog(PlanActivity.this, obj.getString("msg"), isSubscribe);
                            GetPlans();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(PlanActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(PlanActivity.this, getResources().getString(R.string.server_error));
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
