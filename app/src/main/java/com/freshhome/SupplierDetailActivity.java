package com.freshhome;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
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

public class SupplierDetailActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout linear_back, linear_active_inactive, linear_schedule, linear_chef_menu, linear_schedule_title, linear_comment_section;
    TextView text_suppliername, text_supplierloc, text_review, text_price, text_customerviews, text_avialable,
            text_delivered, text_description, text_comment_user, text_comment_time, text_comment, text_schedule_loadmore, text_comments_loadmore;
    RatingBar ratingBar_overall;
    String supplier_id = "";
    ApiInterface apiInterface;
    UserSessionManager sessionManager;
    ArrayList<ScheduleData> array_Schedule;
    CircleImageView profile_image, image_comment;
    ImageView image_favo, headerimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_detail);
        array_Schedule = new ArrayList<>();

        sessionManager = new UserSessionManager(SupplierDetailActivity.this);
        apiInterface = ApiClient.getInstance().getClient();

        supplier_id = getIntent().getStringExtra("supplier_id");

        linear_schedule = (LinearLayout) findViewById(R.id.linear_schedule);
        linear_schedule_title = (LinearLayout) findViewById(R.id.linear_schedule_title);
        text_schedule_loadmore = (TextView) findViewById(R.id.text_schedule_loadmore);
        text_schedule_loadmore.setOnClickListener(this);

        linear_back = (LinearLayout) findViewById(R.id.linear_back);
        linear_back.setOnClickListener(this);

        linear_chef_menu = (LinearLayout) findViewById(R.id.linear_chef_menu);
        linear_chef_menu.setOnClickListener(this);

        linear_active_inactive = (LinearLayout) findViewById(R.id.linear_active_inactive);
        text_suppliername = (TextView) findViewById(R.id.text_suppliername);
        text_price = (TextView) findViewById(R.id.text_price);

        text_review = (TextView) findViewById(R.id.text_review);
        headerimg = (ImageView) findViewById(R.id.headerimg);
        linear_comment_section = (LinearLayout) findViewById(R.id.linear_comment_section);
        ratingBar_overall = (RatingBar) findViewById(R.id.ratingBar_overall);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        image_favo = (ImageView) findViewById(R.id.image_favo);
        image_favo.setOnClickListener(this);

        //hide favo for supplier, show for user and skip
        if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToCook)) {
            image_favo.setVisibility(View.GONE);
        } else {
            image_favo.setVisibility(View.VISIBLE);
        }


        text_description = (TextView) findViewById(R.id.text_description);
        text_customerviews = (TextView) findViewById(R.id.text_customerviews);
        text_avialable = (TextView) findViewById(R.id.text_avialable);
        text_delivered = (TextView) findViewById(R.id.text_delivered);

        image_comment = (CircleImageView) findViewById(R.id.image_comment);
        text_comment_user = (TextView) findViewById(R.id.text_comment_user);
        text_comment_time = (TextView) findViewById(R.id.text_comment_time);
        text_comment = (TextView) findViewById(R.id.text_comment);
        text_comments_loadmore = (TextView) findViewById(R.id.text_comments_loadmore);
        text_comments_loadmore.setOnClickListener(this);

        if (CommonMethods.checkConnection()) {
            getdata();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(SupplierDetailActivity.this, getResources().getString(R.string.internetconnection));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_back:
                SupplierDetailActivity.this.finish();
//                sessionManager.saveLoginType("1");
//                Intent ia = new Intent(SupplierDetailActivity.this, MainActivity_NavDrawer.class);
//                startActivity(ia);
//                finish();
                break;

            case R.id.text_schedule_loadmore:
                Intent i = new Intent(SupplierDetailActivity.this, ScheduleGridViewActivity.class);
                i.putExtra("supplier_id", supplier_id);
                startActivity(i);
                break;

            case R.id.text_comments_loadmore:
                Intent i_feedback = new Intent(SupplierDetailActivity.this, FeedbackListActivity.class);
                i_feedback.putExtra("isSupplierDetail", true);
                i_feedback.putExtra("name", text_suppliername.getText().toString());
                i_feedback.putExtra("id", supplier_id);
                startActivity(i_feedback);
                break;

            case R.id.linear_chef_menu:
                Intent i_menu = new Intent(SupplierDetailActivity.this, SupplierMenuActivity.class);
                i_menu.putExtra("supplier_id", supplier_id);
                startActivity(i_menu);
                break;

            case R.id.image_favo:
                //TODO : CHECK IF LOGGEDIN THEN HIT API
                if (sessionManager.isLoggedIn()) {
                    if (CommonMethods.checkConnection()) {
                        //check if we have to favo it or remove it from favo
                        if (((BitmapDrawable) image_favo.getDrawable()).getBitmap().sameAs(((BitmapDrawable) getResources().getDrawable(R.drawable.heart_green_filed)).getBitmap())) {
                            favoSupplier(supplier_id, image_favo, false);
                        } else {
                            favoSupplier(supplier_id, image_favo, true);
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(SupplierDetailActivity.this, getResources().getString(R.string.internetconnection));
                    }
                } else {
                    CommonMethods.ShowLoginDialog(SupplierDetailActivity.this);
                }
                break;
        }
    }

    private void favoSupplier(final String supplier_id, final ImageView image_favo, final boolean isAdd) {
        final ProgressDialog progressDialog = new ProgressDialog(SupplierDetailActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls;
        if (isAdd) {
            calls = apiInterface.AddFavoSupplier(supplier_id);
        } else {
            calls = apiInterface.RemoveFavoSupplier(supplier_id);
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
                            JSONObject jsonObject = object.getJSONObject("success");
                            CommonUtilFunctions.success_Alert_Dialog(SupplierDetailActivity.this, jsonObject.getString("msg"));
                            if (isAdd) {
                                image_favo.setImageDrawable(getResources().getDrawable(R.drawable.heart_green_filed));
                            } else {
                                image_favo.setImageDrawable(getResources().getDrawable(R.drawable.heart_green));
                            }
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(SupplierDetailActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(SupplierDetailActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(SupplierDetailActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    private void getdata() {
        final ProgressDialog progressDialog = new ProgressDialog(SupplierDetailActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetSupplierDeail(supplier_id);

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        array_Schedule = new ArrayList<>();
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONObject obj = object.getJSONObject("success");
                            JSONObject obj_detail = obj.getJSONObject("details");
                            text_suppliername.setText(CommonMethods.checkNull(obj_detail.getString("name")));
                            text_description.setText(CommonMethods.checkNull(obj_detail.getString("description")));
                            text_review.setText(CommonMethods.checkNull(obj_detail.getString("reviews") + " reviews"));
                            text_delivered.setText(CommonMethods.checkNull(obj_detail.getString("delivered_order")));
                            text_avialable.setText(CommonMethods.checkNull(obj_detail.getString("pending_order")));
                            text_customerviews.setText(CommonMethods.checkNull(obj_detail.getString("views")));

                            if (obj_detail.getString("is_fav").equalsIgnoreCase("1")) {
                                image_favo.setImageDrawable(getResources().getDrawable(R.drawable.heart_green_filed));
                            } else {
                                image_favo.setImageDrawable(getResources().getDrawable(R.drawable.heart_green));
                            }

                            if (!obj_detail.getString("overall_rating").equalsIgnoreCase(null) || !obj_detail.getString("overall_rating").equalsIgnoreCase("")) {
                                ratingBar_overall.setRating(Float.parseFloat(obj_detail.getString("overall_rating")));
                            }
                            if (obj_detail.getString("profile_pic").equalsIgnoreCase("")) {
                                profile_image.setImageDrawable(getResources().getDrawable(R.drawable.icon));
                            } else {
                                Picasso.get().load(obj_detail.getString("profile_pic")).placeholder(R.drawable.icon).into(profile_image);
                            }
                            if (obj_detail.getString("supplier_header_image_path").equalsIgnoreCase("")) {
                                headerimg.setImageDrawable(getResources().getDrawable(R.drawable.defualt_list));
                            } else {
                                Picasso.get().load(obj_detail.getString("supplier_header_image_path")).placeholder(R.drawable.defualt_list).into(headerimg);
                            }

                            //review---comments
                            JSONObject o_comment = obj_detail.getJSONObject("supplier_reviews");
                            if (o_comment.has("review")) {
                                linear_comment_section.setVisibility(View.VISIBLE);
                                JSONObject o_detail = o_comment.getJSONObject("user_info");
                                text_comment_user.setText(CommonMethods.checkNull(o_detail.getString("name")));
                                text_comment.setText(CommonMethods.checkNull(o_comment.getString("review")));
                                text_comment_time.setText(CommonMethods.checkNull(o_comment.getString("time")));
                                if (!o_detail.getString("profile_pic").equalsIgnoreCase("")) {
                                    Picasso.get().load(o_detail.getString("profile_pic")).placeholder(R.drawable.icon).into(image_comment);
                                }
//                                image_comment
                            } else {
                                linear_comment_section.setVisibility(View.GONE);
                            }


                            //schedule
                            JSONArray array_schedule = obj.getJSONArray("menu_schedules");
                            for (int k = 0; k < array_schedule.length(); k++) {
                                JSONObject obj_schedule = array_schedule.getJSONObject(k);
                                ScheduleData data = new ScheduleData();
                                data.setSchedule_date(obj_schedule.getString("schedule_date"));
                                data.setStart_time(obj_schedule.getString("start_time"));
                                data.setEnd_time(obj_schedule.getString("end_time"));
                                data.setScheduleItem(obj_schedule.getString("total_qty"));
                                array_Schedule.add(data);
                            }

                            setUpScheduleData(array_Schedule);


                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(SupplierDetailActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(SupplierDetailActivity.this, getResources().getString(R.string.server_error));
                    }
                } catch (
                        JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                call.cancel();
                CommonUtilFunctions.Error_Alert_Dialog(SupplierDetailActivity.this, getResources().getString(R.string.server_error));
            }
        });

    }

    private void setUpScheduleData(ArrayList<ScheduleData> scheduleData) {
        if (scheduleData.size() == 0) {
            linear_schedule_title.setVisibility(View.GONE);
        } else {
            linear_schedule_title.setVisibility(View.VISIBLE);
        }
        linear_schedule.removeAllViews();
        for (int l = 0; l < scheduleData.size(); l++) {
            if (l <= 5) {
                setData(scheduleData.get(l));
            }
        }

    }

    private void setData(ScheduleData scheduleData) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View scheduleview = inflater.inflate(R.layout.single_row_schedule_view, null);
        TextView text_starttime = (TextView) scheduleview.findViewById(R.id.text_starttime);
        text_starttime.setText(CommonMethods.checkNull(CommonUtilFunctions.changeTimeFormahhmm(scheduleData.getStart_time())) + "am");
        TextView text_endtime = (TextView) scheduleview.findViewById(R.id.text_endtime);
        text_endtime.setText(CommonMethods.checkNull(CommonUtilFunctions.changeTimeFormahhmm(scheduleData.getEnd_time())) + "pm");
        TextView text_available_items = (TextView) scheduleview.findViewById(R.id.text_available_items);
        text_available_items.setText(CommonMethods.checkNull(scheduleData.getScheduleItem()));

        TextView text_date = (TextView) scheduleview.findViewById(R.id.text_date);
        text_date.setText(CommonMethods.getDate(scheduleData.getSchedule_date()));
        TextView text_day = (TextView) scheduleview.findViewById(R.id.text_day);
        text_day.setText(CommonMethods.getDayName(scheduleData.getSchedule_date()));
        TextView text_month = (TextView) scheduleview.findViewById(R.id.text_month);
        text_month.setText(CommonMethods.getMonthYear(scheduleData.getSchedule_date()));
        TextView text_view_menu = (TextView) scheduleview.findViewById(R.id.text_view_menu);
        text_view_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        linear_schedule.addView(scheduleview);
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        sessionManager.saveLoginType("1");
//        Intent i = new Intent(SupplierDetailActivity.this, MainActivity_NavDrawer.class);
////        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(i);
//        finish();
//    }
}
