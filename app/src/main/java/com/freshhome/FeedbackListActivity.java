package com.freshhome;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.AdapterClass.FeedbackAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.datamodel.Feedback;
import com.freshhome.datamodel.ScheduleData;
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

public class FeedbackListActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView image_back;
    ListView feedbacklist;
    boolean isSupplierDetail, fromProductDetail = false;
    String id = "";
    ApiInterface apiInterface;
    UserSessionManager sessionManager;
    ArrayList<Feedback> arrayFeedback;
    TextView heading,text_packing,text_presentation,text_taste;
    RatingBar ratingBar_overall, ratingtaste, ratingBar_presentation, ratingBar_packing;
    LinearLayout linear_no_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        arrayFeedback = new ArrayList<>();
        sessionManager = new UserSessionManager(FeedbackListActivity.this);
        apiInterface = ApiClient.getInstance().getClient();

        isSupplierDetail = getIntent().getBooleanExtra("isSupplierDetail", true);
        id = getIntent().getStringExtra("id");

        if (getIntent().hasExtra("fromProductDetail")) {
            fromProductDetail = getIntent().getBooleanExtra("fromProductDetail", false);
        }

        ratingBar_overall = (RatingBar) findViewById(R.id.ratingBar_overall);
        ratingtaste = (RatingBar) findViewById(R.id.ratingtaste);
        ratingBar_presentation = (RatingBar) findViewById(R.id.ratingBar_presentation);
        ratingBar_packing = (RatingBar) findViewById(R.id.ratingBar_packing);
        linear_no_data = (LinearLayout) findViewById(R.id.linear_no_data);
        linear_no_data.setVisibility(View.GONE);

        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);

        text_packing = (TextView) findViewById(R.id.text_packing);
        text_presentation = (TextView) findViewById(R.id.text_presentation);
        text_taste = (TextView) findViewById(R.id.text_taste);

        //display chef name or menu item name
        heading = (TextView) findViewById(R.id.heading);
        heading.setText(getIntent().getStringExtra("name"));

        feedbacklist = (ListView) findViewById(R.id.feedbacklist);


        if (CommonMethods.checkConnection()) {
            getdata();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(FeedbackListActivity.this, getResources().getString(R.string.internetconnection));
        }
    }

    private void getdata() {
        final ProgressDialog progressDialog = new ProgressDialog(FeedbackListActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;

        if (fromProductDetail) {
            calls = apiInterface.GetProductCommentList(id);
        } else {
            if (isSupplierDetail) {
                calls = apiInterface.GetSupplierCommentList(id, "1");
            } else {
                calls = apiInterface.GetMenuItemCommentList(id, "1");
            }
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
                        arrayFeedback = new ArrayList<>();
                        if (object.getString("code").equalsIgnoreCase("200")) {
//                            JSONObject obj = object.getJSONObject("success");

                            if (isSupplierDetail) {
                                //suplier detail
                                ratingBar_overall.setRating(Float.parseFloat(object.getString("overall_rating")));
                                ratingtaste.setRating(Float.parseFloat(object.getString("taste_rating")));
                                ratingBar_presentation.setRating(Float.parseFloat(object.getString("presentation_rating")));
                                ratingBar_packing.setRating(Float.parseFloat(object.getString("packing_rating")));
                            } else {
                                //menu iem //product item
                                ratingBar_overall.setRating(Float.parseFloat(object.getString("avg_reviews")));
                                ratingtaste.setVisibility(View.GONE);
                                ratingBar_presentation.setVisibility(View.GONE);
                                ratingBar_packing.setVisibility(View.GONE);
                                text_packing.setVisibility(View.GONE);
                                text_presentation.setVisibility(View.GONE);
                                text_taste.setVisibility(View.GONE);
                            }

                            JSONArray jsonArray = object.getJSONArray("reviews");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject j_obj = jsonArray.getJSONObject(i);
                                Feedback feedback = new Feedback();
                                feedback.setComment(j_obj.getString("review"));
                                feedback.setTime(j_obj.getString("time"));
                                JSONObject obj_user = j_obj.getJSONObject("uploaded_by");
                                feedback.setUser_name(obj_user.getString("name"));
                                feedback.setUser_image(obj_user.getString("profile_pic"));
                                arrayFeedback.add(feedback);
                            }
                            FeedbackAdapter adapter = new FeedbackAdapter(FeedbackListActivity.this, arrayFeedback, isSupplierDetail);
                            feedbacklist.setAdapter(adapter);

                            if (arrayFeedback.size() == 0) {
                                linear_no_data.setVisibility(View.VISIBLE);
                                feedbacklist.setVisibility(View.GONE);
                            } else {
                                linear_no_data.setVisibility(View.GONE);
                                feedbacklist.setVisibility(View.VISIBLE);
                            }


                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(FeedbackListActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(FeedbackListActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(FeedbackListActivity.this, getResources().getString(R.string.server_error));
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                FeedbackListActivity.this.finish();
                break;
        }
    }
}
