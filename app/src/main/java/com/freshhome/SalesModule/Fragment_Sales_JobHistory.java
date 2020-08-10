package com.freshhome.SalesModule;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshhome.AdapterClass.RecyclerSalesMyRequestAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.R;
import com.freshhome.datamodel.SalesRequest;
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
public class Fragment_Sales_JobHistory extends Fragment implements View.OnClickListener {
    private BottomSheetBehavior mBottomSheetBehavior;
    private LinearLayout linear_main_slider;
    ImageView image_notification, image_arrow;
    LinearLayout linear_food, linear_homemade, linear_shop, linear_shop_line, linear_homemade_line, linear_food_line;
    public LinearLayout linear_shadow;
    TextView text_food, text_homemade, text_shop,text_total_jobs,text_foodjobs,text_handmadejobs,text_shopsjobs;
    RecyclerView recyclerMyRequestList;
    ArrayList<SalesRequest> array_MyRequestFood, array_MyRequestHomeMade, array_MyRequestShop;
    ApiInterface apiInterface;

    public Fragment_Sales_JobHistory() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sales_job_history, container, false);
        ActivitySalesNavDrawer.heading.setText(getResources().getString(R.string.jobhistory));
        array_MyRequestFood = new ArrayList<>();
        array_MyRequestHomeMade = new ArrayList<>();
        array_MyRequestShop = new ArrayList<>();

        apiInterface = ApiClient.getInstance().getClient();

        image_arrow = (ImageView) v.findViewById(R.id.image_arrow);
        image_arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));

        linear_main_slider = (LinearLayout) v.findViewById(R.id.linear_main_slider);
        View bottomSheet = v.findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight(CommonUtilFunctions.dpToPx(getActivity(), 45));
        linear_main_slider.setVisibility(View.GONE);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior.setPeekHeight(CommonUtilFunctions.dpToPx(getActivity(), 45));
                    linear_main_slider.setVisibility(View.GONE);
                    image_arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
                } else {
                    linear_main_slider.setVisibility(View.VISIBLE);
                    image_arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });


        linear_food = (LinearLayout) v.findViewById(R.id.linear_food);
        linear_food.setOnClickListener(this);
        linear_homemade = (LinearLayout) v.findViewById(R.id.linear_homemade);
        linear_homemade.setOnClickListener(this);
        linear_shop = (LinearLayout) v.findViewById(R.id.linear_shop);
        linear_shop.setOnClickListener(this);

        linear_shop_line = (LinearLayout) v.findViewById(R.id.linear_shop_line);
        linear_homemade_line = (LinearLayout) v.findViewById(R.id.linear_homemade_line);
        linear_food_line = (LinearLayout) v.findViewById(R.id.linear_food_line);

        linear_shadow = (LinearLayout) v.findViewById(R.id.linear_shadow);
        linear_shadow.setVisibility(View.GONE);

        text_total_jobs = (TextView) bottomSheet.findViewById(R.id.text_total_jobs);
        text_foodjobs = (TextView) bottomSheet.findViewById(R.id.text_foodjobs);
        text_handmadejobs = (TextView) bottomSheet.findViewById(R.id.text_handmadejobs);
        text_shopsjobs = (TextView) bottomSheet.findViewById(R.id.text_shopsjobs);

        text_shop = (TextView) v.findViewById(R.id.text_shop);
        text_food = (TextView) v.findViewById(R.id.text_food);
        text_homemade = (TextView) v.findViewById(R.id.text_homemade);

        recyclerMyRequestList = (RecyclerView) v.findViewById(R.id.recyclerMyRequestList);

        if (CommonMethods.checkConnection()) {
            getdata();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
        }
        return v;
    }

    private void setUpList(ArrayList<SalesRequest> array_MyRequest) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerMyRequestList.setLayoutManager(mLayoutManager);
        recyclerMyRequestList.setItemAnimator(new DefaultItemAnimator());
        RecyclerSalesMyRequestAdapter adapter = new RecyclerSalesMyRequestAdapter(getActivity(), array_MyRequest, Fragment_Sales_JobHistory.this);
        recyclerMyRequestList.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_food:
                clickActive(text_food, linear_food_line, array_MyRequestFood);
                clickInactive(text_homemade, linear_homemade_line);
                clickInactive(text_shop, linear_shop_line);
                break;

            case R.id.linear_homemade:
                clickInactive(text_food, linear_food_line);
                clickActive(text_homemade, linear_homemade_line, array_MyRequestHomeMade);
                clickInactive(text_shop, linear_shop_line);
                break;

            case R.id.linear_shop:
                clickInactive(text_food, linear_food_line);
                clickInactive(text_homemade, linear_homemade_line);
                clickActive(text_shop, linear_shop_line, array_MyRequestShop);
                break;
        }
    }

    private void clickActive(TextView textView, LinearLayout linearLayout_line, ArrayList<SalesRequest> array_MyRequest) {
        textView.setTextColor(getResources().getColor(R.color.app_color_blue));
        linearLayout_line.setBackgroundColor(getResources().getColor(R.color.app_color_blue));
        setUpList(array_MyRequest);
    }

    private void clickInactive(TextView textView, LinearLayout linearLayout_line) {
        textView.setTextColor(getResources().getColor(R.color.light_gray));
        linearLayout_line.setBackgroundColor(getResources().getColor(R.color.light_gray));
    }


    private void getdata() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetSaleJobsDone();

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        array_MyRequestFood = new ArrayList<>();
                        array_MyRequestHomeMade = new ArrayList<>();
                        array_MyRequestShop = new ArrayList<>();
                        if (object.getString("code").equalsIgnoreCase("200")) {

                            JSONObject obj_data=object.getJSONObject("data");
                            text_total_jobs.setText(obj_data.getString("total"));
                            text_foodjobs.setText(obj_data.getString("ffd"));
                            text_handmadejobs.setText(obj_data.getString("fhp"));
                            //text_shopsjobs.setText(obj_data.getString("fs"));

                            JSONArray jsonObject = object.getJSONArray("completed_data");
                            //food
                            //JSONObject object_ffd = jsonObject.getJSONObject("ffd");
                            //JSONArray jsonArray_ffd = object_ffd.getJSONArray("results");
                            for (int i = 0; i < jsonObject.length(); i++) {
                                JSONObject obj = jsonObject.getJSONObject(i);
                                //JSONObject obj_info = obj.getJSONObject("information");
                                SalesRequest req = new SalesRequest();
                                //req.setId(obj.getString("request_id"));
                                req.setSupplier_name(obj.getString("name"));
                                req.setMsg(obj.getString("message"));
                                //req.setNotification_id(obj.getString("notification_id"));
                                req.setRequest_status(obj.getString("status"));
                                req.setTime(obj.getString("time"));
                                req.setRequest_type(obj.getString("request_type"));
                                //req.setIsread(obj.getString("is_read"));

                                req.setSupplier_loc((obj.getString("location")));
                                req.setSupplier_phonenumber((obj.getString("phonenumber")));
                                req.setSupplier_lat((obj.getString("lat")));
                                req.setSupplier_lng((obj.getString("lng")));
                                array_MyRequestFood.add(req);
                            }

//                            //home made
//                            JSONObject object_fhp = jsonObject.getJSONObject("fhp");
//                            JSONArray jsonArray_fhp = object_fhp.getJSONArray("results");
//                            for (int i = 0; i < jsonArray_fhp.length(); i++) {
//                                JSONObject obj = jsonArray_fhp.getJSONObject(i);
//                                JSONObject obj_info = obj.getJSONObject("information");
//                                SalesRequest req = new SalesRequest();
//                                req.setId(obj_info.getString("request_id"));
//                                req.setMsg(obj_info.getString("message"));
//                                req.setNotification_id(obj.getString("notification_id"));
//                                req.setRequest_status(obj.getString("request_status"));
//                                req.setTime(obj.getString("time"));
//                                req.setIsread(obj.getString("is_read"));
//
//                                req.setSupplier_loc((obj_info.getString("location")));
//                                req.setSupplier_phonenumber((obj_info.getString("phonenumber")));
//                                req.setSupplier_lat((obj_info.getString("lat")));
//                                req.setSupplier_lng((obj_info.getString("lng")));
//                                array_MyRequestHomeMade.add(req);
//                            }


//                            //shop
//                            JSONObject object_fs = jsonObject.getJSONObject("fs");
//                            JSONArray jsonArray_fs = object_fs.getJSONArray("results");
//                            for (int i = 0; i < jsonArray_fs.length(); i++) {
//                                JSONObject obj = jsonArray_fs.getJSONObject(i);
//                                JSONObject obj_info = obj.getJSONObject("information");
//                                SalesRequest req = new SalesRequest();
//                                req.setId(obj_info.getString("request_id"));
//                                req.setMsg(obj_info.getString("message"));
//                                req.setNotification_id(obj.getString("notification_id"));
//                                req.setRequest_status(obj.getString("request_status"));
//                                req.setTime(obj.getString("time"));
//                                req.setIsread(obj.getString("is_read"));
//
//                                req.setSupplier_loc((obj_info.getString("location")));
//                                req.setSupplier_phonenumber((obj_info.getString("phonenumber")));
//                                req.setSupplier_lat((obj_info.getString("lat")));
//                                req.setSupplier_lng((obj_info.getString("lng")));
//                                array_MyRequestShop.add(req);
//                            }

                            //show first one food
                            setUpList(array_MyRequestFood);
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
            }
        });
    }
}
