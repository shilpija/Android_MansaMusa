package com.freshhome;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class MenuItemFeedbackActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout linear_cancel, linear_submit;
    ImageView image_back;
    EditText edit_feedback;
    CircleImageView dish_image;
    TextView text_dishname;
    RatingBar ratingBar;
    UserSessionManager sessionManager;
    ApiInterface apiInterface;
    String order_id = "", dish_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item_feedback);

        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(MenuItemFeedbackActivity.this);

        //get orderId and dish_id from intent
        order_id = getIntent().getStringExtra("order_id");
        dish_id = getIntent().getStringExtra("dish_id");

        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);

        linear_cancel = (LinearLayout) findViewById(R.id.linear_cancel);
        linear_cancel.setOnClickListener(this);

        linear_submit = (LinearLayout) findViewById(R.id.linear_submit);
        linear_submit.setOnClickListener(this);

        edit_feedback = (EditText) findViewById(R.id.edit_feedback);
        text_dishname = (TextView) findViewById(R.id.text_dishname);
        text_dishname.setText(getIntent().getStringExtra("dish_name"));
        dish_image = (CircleImageView) findViewById(R.id.dish_image);
        if(!getIntent().getStringExtra("dish_image").equalsIgnoreCase("")){
            Picasso.get().load(getIntent().getStringExtra("dish_image")).into(dish_image);
        }
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating < 1.0f) {
                    ratingBar.setRating(1.0f);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                MenuItemFeedbackActivity.this.finish();
                break;

            case R.id.linear_cancel:
                MenuItemFeedbackActivity.this.finish();
                break;

            case R.id.linear_submit:

                if(edit_feedback.getText().toString().equalsIgnoreCase("")){
                    CommonUtilFunctions.Error_Alert_Dialog(MenuItemFeedbackActivity.this, getResources().getString(R.string.enter_comment));
                }else {
                    CommonMethods.hideSoftKeyboard(MenuItemFeedbackActivity.this);
                    if (CommonMethods.checkConnection()) {
                        postFeedback();
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(MenuItemFeedbackActivity.this, getResources().getString(R.string.internetconnection));
                    }
                }
                break;
        }
    }

    private void postFeedback() {
        final ProgressDialog progressDialog = new ProgressDialog(MenuItemFeedbackActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.FeedbackMenuItem(order_id, dish_id, String.valueOf(ratingBar.getRating()),
                edit_feedback.getText().toString().trim()
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
                            CommonMethods.showtoast(MenuItemFeedbackActivity.this, obj.getString("msg"));
                            MenuItemFeedbackActivity.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(MenuItemFeedbackActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(MenuItemFeedbackActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(MenuItemFeedbackActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }
}
