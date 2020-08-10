package com.freshhome;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompleteHelpRequestActivity extends AppCompatActivity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener {
    TextView text_roles, text_sales_name, text_salesphone;
    ApiInterface apiInterface;
    TextView text_msg;
    UserSessionManager sessionManager;
    ImageView image_back;
    RatingBar ratingBar_overall, ratingBar_sales;
    CircleImageView circle_image;
    String request_id = "", notification_id = "";
    LinearLayout linear_Accept, linear_reject, linear_accept_reject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_help_request);
        request_id = getIntent().getStringExtra("request_id");
        notification_id = getIntent().getStringExtra("notification_id");
        sessionManager = new UserSessionManager(CompleteHelpRequestActivity.this);
        apiInterface = ApiClient.getInstance().getClient();
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);
        circle_image = (CircleImageView) findViewById(R.id.circle_image);
        text_msg = (TextView) findViewById(R.id.text_msg);
        text_msg.setText(getIntent().getStringExtra("msg"));
        text_roles = (TextView) findViewById(R.id.text_roles);

        text_sales_name = (TextView) findViewById(R.id.text_sales_name);
        text_salesphone = (TextView) findViewById(R.id.text_salesphone);
        ratingBar_overall = (RatingBar) findViewById(R.id.ratingBar_overall);
        ratingBar_sales = (RatingBar) findViewById(R.id.ratingBar_sales);

        linear_Accept = (LinearLayout) findViewById(R.id.linear_Accept);
        linear_Accept.setOnClickListener(this);
        linear_reject = (LinearLayout) findViewById(R.id.linear_reject);
        linear_reject.setOnClickListener(this);
        linear_accept_reject = (LinearLayout) findViewById(R.id.linear_accept_reject);


        if (CommonMethods.checkConnection()) {
            GetCurrentRequestandRoles();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(CompleteHelpRequestActivity.this, getResources().getString(R.string.internetconnection));
        }

        text_sales_name.setText(getIntent().getStringExtra("sales_name"));
        text_salesphone.setText(getIntent().getStringExtra("sales_phone"));
        ratingBar_overall.setRating(Float.parseFloat(getIntent().getStringExtra("rating_overall")));
        ratingBar_sales.setRating(Float.parseFloat(getIntent().getStringExtra("sales_rating")));
        if (!getIntent().getStringExtra("sales_image").equalsIgnoreCase("")) {
            Picasso.get().load(getIntent().getStringExtra("sales_image")).into(circle_image);
        }

        if (getIntent().getStringExtra("request_status").equalsIgnoreCase("completed")) {
            text_msg.setText(getResources().getString(R.string.rat_sale));
            linear_accept_reject.setVisibility(View.GONE);
            ratingBar_sales.setVisibility(View.VISIBLE);
        }

        if (getIntent().getStringExtra("sales_rating").equalsIgnoreCase("0.0")) {
            ratingBar_sales.setOnRatingBarChangeListener(this);
        }else{
            ratingBar_sales.setEnabled(false);
        }
    }


    //get roles text and current request of supplier
    private void GetCurrentRequestandRoles() {
        final ProgressDialog progressDialog = new ProgressDialog(CompleteHelpRequestActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetCurrentReq(sessionManager.getFCMToken());

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
                            text_roles.setText(Html.fromHtml(object.getString("data")));
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(CompleteHelpRequestActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(CompleteHelpRequestActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(CompleteHelpRequestActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                CompleteHelpRequestActivity.this.finish();
                break;

            case R.id.linear_reject:

                if (CommonMethods.checkConnection()) {
                    CompleteCurrentRequest(false);
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(CompleteHelpRequestActivity.this, getResources().getString(R.string.internetconnection));
                }

                break;

            case R.id.linear_Accept:
                if (CommonMethods.checkConnection()) {
                    CompleteCurrentRequest(true);
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(CompleteHelpRequestActivity.this, getResources().getString(R.string.internetconnection));
                }
                break;
        }
    }


    //complete request
    private void CompleteCurrentRequest(final boolean isComplete) {
        final ProgressDialog progressDialog = new ProgressDialog(CompleteHelpRequestActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;

        if (isComplete) {
            calls = apiInterface.CompeleteSalesReq(request_id);
        } else {
            calls = apiInterface.RejectScanRequest(request_id);
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
                            JSONObject obj=object.getJSONObject("success");
                            CommonUtilFunctions.success_Alert_Dialog(CompleteHelpRequestActivity.this, obj.getString("msg"));
                            //active rating here
                            if (isComplete) {
                                linear_accept_reject.setVisibility(View.GONE);
                                ratingBar_sales.setVisibility(View.VISIBLE);

                            } else {
                                CompleteHelpRequestActivity.this.finish();
                            }

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(CompleteHelpRequestActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(CompleteHelpRequestActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(CompleteHelpRequestActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    //complete request
    private void RatetheSalesperson(String rating) {
        final ProgressDialog progressDialog = new ProgressDialog(CompleteHelpRequestActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.RateSalesPerson(request_id, rating);

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
                            CommonUtilFunctions.success_Alert_Dialog(CompleteHelpRequestActivity.this, object.getString("msg"));
                            //active rating here
                            linear_accept_reject.setVisibility(View.GONE);
                            ratingBar_sales.setVisibility(View.VISIBLE);

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(CompleteHelpRequestActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(CompleteHelpRequestActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(CompleteHelpRequestActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        switch (ratingBar.getId()) {
            case R.id.ratingBar_sales:
                if (fromUser) {
                    if (CommonMethods.checkConnection()) {
                        RatetheSalesperson(String.valueOf(rating));
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(CompleteHelpRequestActivity.this, getResources().getString(R.string.internetconnection));
                    }
                }

                break;
        }
    }
}
