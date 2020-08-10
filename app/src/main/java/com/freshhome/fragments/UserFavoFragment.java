package com.freshhome.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.freshhome.AdapterClass.UserFavoChefAdapter;
import com.freshhome.AdapterClass.UserFavoDishAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.R;
import com.freshhome.datamodel.HomeItem;
import com.freshhome.datamodel.SupplierInfo;
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
public class UserFavoFragment extends Fragment implements View.OnClickListener {
    ListView FavoList;
    TextView text_chef, text_dish;
    LinearLayout linear_chef_line, linear_dish_line;
    public static LinearLayout linear_no_data;
    ApiInterface apiInterface;
    UserSessionManager sessionManager;
    ArrayList<HomeItem> arrayFavoDishes;
    ArrayList<SupplierInfo> arrayFavoSupplier;
    String categories = "", cuisines = "", meals = "";
    boolean isDish = false;

    public UserFavoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_favo, container, false);

        arrayFavoDishes = new ArrayList<>();
        sessionManager = new UserSessionManager(getActivity());
        apiInterface = ApiClient.getInstance().getClient();
        MainActivity_NavDrawer.heading.setText(R.string.myfavo);
        FavoList = (ListView) v.findViewById(R.id.FavoList);
        text_chef = (TextView) v.findViewById(R.id.text_chef);
        text_chef.setOnClickListener(this);
        text_dish = (TextView) v.findViewById(R.id.text_dish);
        text_dish.setOnClickListener(this);

        linear_chef_line = (LinearLayout) v.findViewById(R.id.linear_chef_line);
        linear_dish_line = (LinearLayout) v.findViewById(R.id.linear_dish_line);
        linear_no_data = (LinearLayout) v.findViewById(R.id.linear_no_data);

        //activate favo chef
        clickFavoActive(text_chef, linear_chef_line);
        clickFavoInactive(text_dish, linear_dish_line);

        return v;
    }

    private void getFavoData() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.GetFavoData();
        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        arrayFavoDishes = new ArrayList<>();
                        arrayFavoSupplier = new ArrayList<>();
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONObject jsonObject = object.getJSONObject("success");
                            JSONArray SupplierArray = jsonObject.getJSONArray("suppliers");
                            for (int i = 0; i < SupplierArray.length(); i++) {
                                JSONObject obj = SupplierArray.getJSONObject(i);
                                SupplierInfo supplierInfo = new SupplierInfo();
                                supplierInfo.setSid(obj.getString("id"));
                                supplierInfo.setSname(obj.getString("name"));
                                supplierInfo.setSimage(obj.getString("profile_pic"));
                                supplierInfo.setSloc(obj.getString("location"));
                                supplierInfo.setSstatus(obj.getString("availability"));
                                supplierInfo.setSrating(obj.getString("rating"));
                                arrayFavoSupplier.add(supplierInfo);
                            }


                            JSONArray MenuArray = jsonObject.getJSONArray("menus");
                            for (int i = 0; i < MenuArray.length(); i++) {
                                JSONObject obj = MenuArray.getJSONObject(i);
                                HomeItem homeItem = new HomeItem();
                                homeItem.setDish_id(obj.getString("dish_id"));
                                homeItem.setDish_name(obj.getString("dish_name"));
                                homeItem.setDish_price(obj.getString("dish_price"));
                                homeItem.setDish_image(obj.getString("dish_image"));
                                homeItem.setRate_point(obj.getString("rate_point"));
                                homeItem.setDate_status(obj.getString("status"));
                                homeItem.setProduct_type(obj.getString("product_type"));

                                //get categories
                                JSONArray category_array = obj.getJSONArray("categories");
                                categories = "";
                                for (int j = 0; j < category_array.length(); j++) {
                                    JSONObject cat_obj = category_array.getJSONObject(j);
                                    JSONObject d_obj = cat_obj.getJSONObject("detail");
                                    if (categories.equalsIgnoreCase("")) {
                                        categories = d_obj.getString("category_name");
                                    } else {
                                        categories = categories + ", " + d_obj.getString("category_name");
                                    }

                                }
                                homeItem.setDish_categories(categories);

                                // get cuisines
                                JSONArray cuisines_array = obj.getJSONArray("cuisines");
                                cuisines = "";
                                for (int k = 0; k < cuisines_array.length(); k++) {
                                    JSONObject cuis_obj = cuisines_array.getJSONObject(k);
                                    JSONObject d_obj = cuis_obj.getJSONObject("cuisine_detail");
                                    if (cuisines.equalsIgnoreCase("")) {
                                        cuisines = d_obj.getString("cuisine_name");
                                    } else {
                                        cuisines = cuisines + ", " + d_obj.getString("cuisine_name");
                                    }
                                }
                                homeItem.setDish_cuisines(cuisines);

                                //meals
                                JSONArray meals_array = obj.getJSONArray("meals");
                                meals = "";
                                for (int l = 0; l < meals_array.length(); l++) {
                                    JSONObject cat_obj = meals_array.getJSONObject(l);
                                    JSONObject d_obj = cat_obj.getJSONObject("meal_detail");
                                    if (meals.equalsIgnoreCase("")) {
                                        meals = d_obj.getString("meal_name");
                                    }
                                }
                                homeItem.setDish_meal(meals);
                                arrayFavoDishes.add(homeItem);
                            }

                            if (isDish) {
                                setDishList();
                            } else {
                                setChefList();
                            }
                        } else {
//                            JSONObject obj = object.getJSONObject("error");
                            linear_no_data.setVisibility(View.VISIBLE);
//                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_chef:
                isDish = false;
                clickFavoActive(text_chef, linear_chef_line);
                clickFavoInactive(text_dish, linear_dish_line);
                setChefList();

                break;

            case R.id.text_dish:
                isDish = true;
                clickFavoInactive(text_chef, linear_chef_line);
                clickFavoActive(text_dish, linear_dish_line);
                setDishList();

                break;
        }
    }

    private void setDishList() {
        if (arrayFavoDishes.size() != 0) {
            linear_no_data.setVisibility(View.GONE);
            FavoList.setVisibility(View.VISIBLE);
            UserFavoDishAdapter adapter_dish = new UserFavoDishAdapter(getActivity(), arrayFavoDishes);
            FavoList.setAdapter(adapter_dish);
        } else {
            linear_no_data.setVisibility(View.VISIBLE);
            FavoList.setVisibility(View.GONE);
        }
    }

    private void setChefList() {
        if (arrayFavoSupplier!=null && arrayFavoSupplier.size() != 0) {
            linear_no_data.setVisibility(View.GONE);
            FavoList.setVisibility(View.VISIBLE);
            UserFavoChefAdapter adapter_chef = new UserFavoChefAdapter(getActivity(), arrayFavoSupplier);
            FavoList.setAdapter(adapter_chef);
        } else {
            linear_no_data.setVisibility(View.VISIBLE);
            FavoList.setVisibility(View.GONE);
        }
    }

    private void clickFavoActive(TextView textView, LinearLayout linearLayout_line) {
        textView.setTextColor(getResources().getColor(R.color.app_color_blue));
        linearLayout_line.setBackgroundColor(getResources().getColor(R.color.app_color_blue));
    }

    private void clickFavoInactive(TextView textView, LinearLayout linearLayout_line) {
        textView.setTextColor(getResources().getColor(R.color.light_gray));
        linearLayout_line.setBackgroundColor(getResources().getColor(R.color.light_gray));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sessionManager.isLoggedIn()) {
            if (CommonMethods.checkConnection()) {
                getFavoData();
            } else {
                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
            }
        } else {
            linear_no_data.setVisibility(View.VISIBLE);
        }

    }
}
