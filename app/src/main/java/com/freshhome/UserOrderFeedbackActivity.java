package com.freshhome;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserOrderFeedbackActivity extends AppCompatActivity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener {
    LinearLayout linear_cancel, linear_submit;
    ImageView image_back;
    RatingBar ratingBar_taste, ratingBar_presentation, ratingBar_packing, ratingBar_overall;
    EditText edit_review;
    UserSessionManager sessionManager;
    ApiInterface apiInterface;
    String order_id = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order_feedback);

        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(UserOrderFeedbackActivity.this);

        ratingBar_taste = (RatingBar) findViewById(R.id.ratingBar_taste);
        ratingBar_taste.setOnRatingBarChangeListener(this);
        ratingBar_presentation = (RatingBar) findViewById(R.id.ratingBar_presentation);
        ratingBar_presentation.setOnRatingBarChangeListener(this);
        ratingBar_packing = (RatingBar) findViewById(R.id.ratingBar_packing);
        ratingBar_packing.setOnRatingBarChangeListener(this);
        ratingBar_overall = (RatingBar) findViewById(R.id.ratingBar_overall);
        ratingBar_overall.setOnRatingBarChangeListener(this);

        edit_review = (EditText) findViewById(R.id.edit_review);

        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);

        linear_cancel = (LinearLayout) findViewById(R.id.linear_cancel);
        linear_cancel.setOnClickListener(this);

        linear_submit = (LinearLayout) findViewById(R.id.linear_submit);
        linear_submit.setOnClickListener(this);

        if(getIntent().hasExtra("order_id")) {
            order_id = getIntent().getStringExtra("order_id");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                UserOrderFeedbackActivity.this.finish();
                break;

            case R.id.linear_cancel:
                UserOrderFeedbackActivity.this.finish();
                break;

            case R.id.linear_submit:
                if (edit_review.getText().toString().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(UserOrderFeedbackActivity.this, getResources().getString(R.string.enter_comment));
                } else {
                    CommonMethods.hideSoftKeyboard(UserOrderFeedbackActivity.this);
                    if (CommonMethods.checkConnection()) {
                        postFeedback();
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(UserOrderFeedbackActivity.this, getResources().getString(R.string.internetconnection));
                    }
                }
                break;
        }
    }

    private void postFeedback() {
        final ProgressDialog progressDialog = new ProgressDialog(UserOrderFeedbackActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.FeedbackOrder(order_id,
                String.valueOf(ratingBar_taste.getRating()),
                String.valueOf(ratingBar_presentation.getRating()),
                String.valueOf(ratingBar_packing.getRating()),
                String.valueOf(ratingBar_overall.getRating()),
                edit_review.getText().toString().trim()
        );

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
                            CommonMethods.showtoast(UserOrderFeedbackActivity.this, obj.getString("msg"));
                            UserOrderFeedbackActivity.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(UserOrderFeedbackActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(UserOrderFeedbackActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(UserOrderFeedbackActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        if (rating < 1.0f) {
            ratingBar.setRating(1.0f);
        }
    }
}
