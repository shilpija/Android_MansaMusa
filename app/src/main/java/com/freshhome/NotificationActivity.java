package com.freshhome;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.freshhome.AdapterClass.HomeAdapter;
import com.freshhome.AdapterClass.NotificationCommentsAdapter;
import com.freshhome.AdapterClass.NotificationPreOrderAdapter;
import com.freshhome.AdapterClass.NotificationReviewAdapter;
//import com.freshhome.AdapterClass.RecyclerCartAdapter;
import com.freshhome.AdapterClass.RecyclerNotificationAdapter;
import com.freshhome.AdapterClass.RecyclerNotificationAdapterSupplier;
import com.freshhome.AdapterClass.RecyclerNotificationSalesAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.datamodel.NameID;
import com.freshhome.datamodel.NotificationModel;
import com.freshhome.fragments.UserCartFragment;
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

public class NotificationActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout linear_preorder, linear_preorder_line, linear_comments, linear_comment_line, linear_review, linear_review_line;
    RecyclerView notificationRecyclerview;
    ImageView image_back;
    ApiInterface apiInterface;
    UserSessionManager sessionManager;
    ArrayList<NotificationModel> arrayNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        arrayNotification = new ArrayList<>();
        sessionManager = new UserSessionManager(NotificationActivity.this);
        apiInterface = ApiClient.getInstance().getClient();
        linear_preorder = (LinearLayout) findViewById(R.id.linear_preorder);
        linear_preorder_line = (LinearLayout) findViewById(R.id.linear_preorder_line);
        linear_preorder.setOnClickListener(this);

        linear_comments = (LinearLayout) findViewById(R.id.linear_comments);
        linear_comment_line = (LinearLayout) findViewById(R.id.linear_comment_line);
        linear_comment_line.setVisibility(View.GONE);
        linear_comments.setOnClickListener(this);

        linear_review = (LinearLayout) findViewById(R.id.linear_review);
        linear_review_line = (LinearLayout) findViewById(R.id.linear_review_line);
        linear_review_line.setVisibility(View.GONE);
        linear_review.setOnClickListener(this);

        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);

        notificationRecyclerview = (RecyclerView) findViewById(R.id.notificationRecyclerview);

//        clickPreOrder();
        if (CommonMethods.checkConnection()) {
            getNotificationdata();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(NotificationActivity.this, getResources().getString(R.string.internetconnection));
        }
    }

    private void getNotificationdata() {
        final ProgressDialog progressDialog = new ProgressDialog(NotificationActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.GetNotification(sessionManager.getLoginType());

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        arrayNotification = new ArrayList<>();
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = object.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                NotificationModel notificationModel = new NotificationModel();
                                notificationModel.setNotification_id(obj.getString("notification_id"));
                                notificationModel.setType(obj.getString("type"));
                                notificationModel.setTime(obj.getString("time"));
                                JSONObject jObj = obj.getJSONObject("information");

                                //TODO all data for help request-------sales person and sales requests
                                if (obj.getString("type").equalsIgnoreCase("help_request")) {
                                    notificationModel.setRequet_id(jObj.getString("request_id"));
                                    notificationModel.setMessage(jObj.getString("message"));

                                    //TODO supplier data in case fo request
                                    if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.Sales)) {
                                        if (!obj.getString("request_status").equalsIgnoreCase("null")) {
                                            notificationModel.setSupplier_name(jObj.getString("name"));
                                            notificationModel.setSupplier_loc(jObj.getString("location"));
                                            notificationModel.setSupplier_phone(jObj.getString("phonenumber"));
                                            notificationModel.setLat(jObj.getString("lat"));
                                            notificationModel.setLng(jObj.getString("lng"));
                                        }
                                    }
                                    notificationModel.setRequest_status(obj.getString("request_status"));
                                    notificationModel.setSales_rating(obj.getString("rating"));

                                    //TODO in case of supplier---------request after qr code scanner
                                    if (jObj.has("salesperson_data")) {
                                        JSONObject sales_obj = jObj.getJSONObject("salesperson_data");
                                        notificationModel.setSales_name(sales_obj.getString("name"));
                                        notificationModel.setRating_overall(sales_obj.getString("rating"));
                                        notificationModel.setSales_phone(sales_obj.getString("phone_number"));
                                        notificationModel.setSales_image(sales_obj.getString("profile_pic"));
                                    }
                                } else {
                                    //TODO in case of user and supplier
                                    notificationModel.setOrder_id(jObj.getString("order_id"));
                                    notificationModel.setMessage(jObj.getString("message"));
                                    notificationModel.setOrderstatus(jObj.getString("order_status"));

                                }
                                arrayNotification.add(notificationModel);
                            }

                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(NotificationActivity.this);
                            notificationRecyclerview.setLayoutManager(mLayoutManager);
                            notificationRecyclerview.setItemAnimator(new DefaultItemAnimator());

                            if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.Sales)) {
                                RecyclerNotificationSalesAdapter adapter = new RecyclerNotificationSalesAdapter(NotificationActivity.this, arrayNotification);
                                notificationRecyclerview.setAdapter(adapter);
                            } else if(sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToEat)) {
                                RecyclerNotificationAdapter adapter = new RecyclerNotificationAdapter(NotificationActivity.this, arrayNotification);
                                notificationRecyclerview.setAdapter(adapter);
                            }else if(sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToCook)){
                                RecyclerNotificationAdapterSupplier adapter = new RecyclerNotificationAdapterSupplier(NotificationActivity.this, arrayNotification);
                                notificationRecyclerview.setAdapter(adapter);
                            }


                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(NotificationActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(NotificationActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(NotificationActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_preorder:
//                clickPreOrder();
                break;

            case R.id.linear_comments:
//                clickComment();
                break;

            case R.id.linear_review:
//                clickReview();
                break;

            case R.id.image_back:
                NotificationActivity.this.finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (CommonMethods.checkConnection()) {
//            getNotificationdata();
//        } else {
//            CommonUtilFunctions.Error_Alert_Dialog(NotificationActivity.this, getResources().getString(R.string.internetconnection));
//        }
    }

    //    private void clickReview() {
//        linear_preorder_line.setVisibility(View.GONE);
//        linear_review_line.setVisibility(View.VISIBLE);
//        linear_comment_line.setVisibility(View.GONE);
//
//        notificationList = (ListView) findViewById(R.id.notificationList);
//        NotificationReviewAdapter adapter = new NotificationReviewAdapter(NotificationActivity.this, 12);
//        notificationList.setAdapter(adapter);
//    }
//
//    private void clickComment() {
//        linear_preorder_line.setVisibility(View.GONE);
//        linear_review_line.setVisibility(View.GONE);
//        linear_comment_line.setVisibility(View.VISIBLE);
//
//        notificationList = (ListView) findViewById(R.id.notificationList);
//        NotificationCommentsAdapter adapter = new NotificationCommentsAdapter(NotificationActivity.this, 5);
//        notificationList.setAdapter(adapter);
//
//    }
//
//    private void clickPreOrder() {
//        linear_preorder_line.setVisibility(View.VISIBLE);
//        linear_review_line.setVisibility(View.GONE);
//        linear_comment_line.setVisibility(View.GONE);
//
//        notificationList = (ListView) findViewById(R.id.notificationList);
//        NotificationPreOrderAdapter adapter = new NotificationPreOrderAdapter(NotificationActivity.this, 8);
//        notificationList.setAdapter(adapter);
//    }
}
