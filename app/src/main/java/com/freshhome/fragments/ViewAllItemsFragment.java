package com.freshhome.fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.freshhome.AdapterClass.HomeAdapter;
import com.freshhome.AdapterClass.RecyclerSupplierAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.ProductFilterActivity;
import com.freshhome.R;
import com.freshhome.datamodel.HomeItem;
import com.freshhome.datamodel.NameID;
import com.freshhome.datamodel.categories;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewAllItemsFragment extends Fragment {
    public static int REQUEST_CODE = 12;
    public static String ISLOAD = "isload";
    public static UserSessionManager sessionManager;
    RecyclerView recyclerview;
    LinearLayout llMain;
    ApiInterface apiInterface;
    ArrayList<HomeItem> arrayProducts;
    String type = "", SCREEN_ID = ""; //SCREEN_ID MEANS 1 FOR FOOD PRODUCTS AND 2/3 FOR HOME-SHOP PRODUCTS
    ArrayList<categories> arraySupplier;
    ArrayList<NameID> arrayCategory, arraySortingMethods;
    String[] sortingArray;
    String filterJson = "";
    private String filterratingdata, categoriesdata, newarrivalsData, filterpricedata, filterdiscountData;
    private String sorting;

    public ViewAllItemsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_view_all_items, container, false);
        ButterKnife.bind(this, v);
        arrayProducts = new ArrayList<>();
        arraySupplier = new ArrayList<>();

        arrayCategory = new ArrayList<>();

        arraySortingMethods = new ArrayList<>();


        Bundle extras = getArguments();
        type = extras.getString("type");
        sessionManager = new UserSessionManager(getActivity());
        apiInterface = ApiClient.getInstance().getClient();
        recyclerview = (RecyclerView) v.findViewById(R.id.recyclerview);
        llMain = (LinearLayout) v.findViewById(R.id.linearLayout5);
        recyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setNestedScrollingEnabled(false);

        if (CommonMethods.checkConnection()) {
            getData();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
        }

        getFilterCategory("latest");

        return v;

    }


    @OnClick({R.id.iv_filter, R.id.iv_sort})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_filter:
                Intent i = new Intent(getActivity(), ProductFilterActivity.class);
                i.putExtra("json", filterJson);
                i.putExtra("filter", ViewAllItemsFragment.class.getSimpleName());
//                i.putExtra(MEALS, meals);
//                i.putExtra(CATEGORY, categories);
//                i.putExtra(CUISINE, cuisines);
                startActivityForResult(i, REQUEST_CODE);
                break;

            case R.id.iv_sort:
                show_sortdialog();
                break;
        }

    }

    private void getFilterData() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = null;

        //home page view all
        if (type.equalsIgnoreCase(getResources().getString(R.string.latest_products))) {
            SCREEN_ID = "2";
            calls = apiInterface.GetLatestProductsChange(sorting,categoriesdata,filterpricedata,filterdiscountData,filterratingdata,newarrivalsData);

        } else if (type.equalsIgnoreCase(getResources().getString(R.string.best_selling))) {
            SCREEN_ID = "2";
            //calls = apiInterface.GetBestSellingProducts("1");
            calls = apiInterface.GetBestSellingChange(sorting,categoriesdata,filterpricedata,filterdiscountData,filterratingdata,newarrivalsData);

        } else if (type.equalsIgnoreCase(getResources().getString(R.string.ordernow))) {
            SCREEN_ID = "1";
            //calls = apiInterface.GetOrderNowProducts("1");
            calls = apiInterface.GetBestdealChange(sorting,categoriesdata,filterpricedata,filterdiscountData,filterratingdata,newarrivalsData);
        } else if (type.equalsIgnoreCase(getResources().getString(R.string.order_kitchens))) {
            calls = apiInterface.GetKitchens("1");
        } else {
            //by deafult showing latest products
            SCREEN_ID = "2";
            calls = apiInterface.GetLatestProductsChange(sorting,categoriesdata,filterpricedata,filterdiscountData,filterratingdata,newarrivalsData);
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
                            arrayProducts = new ArrayList<>();
                            arraySupplier = new ArrayList<>();
                            if (object.has("latest_products")) {
                                JSONArray jLarray = object.getJSONArray("latest_products");
                                for (int i = 0; i < jLarray.length(); i++) {
                                    JSONObject obj = jLarray.getJSONObject(i);
                                    HomeItem item = new HomeItem();
                                    item.setDish_id(obj.getString("product_id"));
                                    item.setDish_description(obj.getString("product_description"));
                                    item.setDish_image(obj.getString("main_image"));
                                    item.setDish_name(obj.getString("product_name"));
                                    item.setDish_price(obj.getString("product_price"));
                                    item.setRate_point(obj.getString("rate_point"));
                                    item.setIsFavo(obj.getString("is_fav"));
                                    item.setFavo_count(obj.getString("fav_count"));
                                    item.setUser_views(obj.getString("user_view"));
                                    item.setCart_qty(obj.getInt("cart_qty"));
                                    item.setDiscount(obj.getString("discount"));
                                    //check attributes and if attributes then take him to detail screen else add to cart
                                    if (obj.has("product_attributes")) {
                                        JSONArray jAttribute = obj.getJSONArray("product_attributes");
                                        if (jAttribute.length() != 0) {
                                            item.setAttributes(true);
                                        } else {
                                            item.setAttributes(false);
                                        }
                                    }
                                    arrayProducts.add(item);
                                }
                            }


                            if (object.has("best_selling_products")) {
                                JSONArray jLarray = object.getJSONArray("best_selling_products");
                                for (int i = 0; i < jLarray.length(); i++) {
                                    JSONObject obj = jLarray.getJSONObject(i);
                                    HomeItem item = new HomeItem();
                                    item.setDish_id(obj.getString("product_id"));
                                    item.setDish_description(obj.getString("product_description"));
                                    item.setDish_image(obj.getString("main_image"));
                                    item.setDish_name(obj.getString("product_name"));
                                    item.setDish_price(obj.getString("product_price"));
                                    item.setRate_point(obj.getString("rate_point"));
                                    item.setIsFavo(obj.getString("is_fav"));
                                    item.setFavo_count(obj.getString("fav_count"));
                                    item.setUser_views(obj.getString("user_view"));
                                    item.setCart_qty(obj.getInt("cart_qty"));
                                    item.setDiscount(obj.getString("discount"));
                                    //check attributes and if attributes then take him to detail screen else add to cart
                                    if (obj.has("product_attributes")) {
                                        JSONArray jAttribute = obj.getJSONArray("product_attributes");
                                        if (jAttribute.length() != 0) {
                                            item.setAttributes(true);
                                        } else {
                                            item.setAttributes(false);
                                        }
                                    }
                                    arrayProducts.add(item);
                                }
                            }


                            //TODO if order now   FOOD PRODUCTS
                            if (object.has("order_now")) {
                                JSONArray jONarray = object.getJSONArray("order_now");
                                for (int i = 0; i < jONarray.length(); i++) {
                                    JSONObject obj = jONarray.getJSONObject(i);
                                    HomeItem item = new HomeItem();
                                    item.setDish_id(obj.getString("dish_id"));
                                    item.setDish_description(obj.getString("dish_description"));
                                    item.setDish_image(obj.getString("dish_image"));
                                    item.setDish_name(obj.getString("dish_name"));
                                    item.setDish_price(obj.getString("dish_price"));
                                    item.setRate_point(obj.getString("rate_point"));
                                    item.setIsFavo(obj.getString("is_fav"));
                                    item.setFavo_count(obj.getString("fav_count"));
                                    item.setUser_views(obj.getString("user_view"));
                                    item.setCart_qty(obj.getInt("cart_qty"));
                                    item.setDiscount(obj.getString("discount"));
                                    item.setAttributes(false);
                                    arrayProducts.add(item);
                                }
                            }

                            if (object.has("online_kitchens")) {
                                JSONArray jsarray = object.getJSONArray("online_kitchens");
                                for (int i = 0; i < jsarray.length(); i++) {
                                    JSONObject jobj = jsarray.getJSONObject(i);
                                    categories cat = new categories();
                                    cat.setId(jobj.getString("id"));
                                    cat.set_name(jobj.getString("name"));
                                    cat.setimage(jobj.getString("profile_pic"));
                                    cat.setAvailable(jobj.getString("availability"));
                                    arraySupplier.add(cat);
                                }

                            }
                            if (arrayProducts.size() > 0) {
                                recyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                                recyclerview.setItemAnimator(new DefaultItemAnimator());
                                recyclerview.setNestedScrollingEnabled(false);
                                HomeAdapter adapter_latestp = new HomeAdapter(getActivity(), arrayProducts, SCREEN_ID);
                                recyclerview.setAdapter(adapter_latestp);
                            }

                            if (arraySupplier.size() > 0) {
                                recyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                                recyclerview.setItemAnimator(new DefaultItemAnimator());
                                recyclerview.setNestedScrollingEnabled(false);
                                RecyclerSupplierAdapter adapter_supplier = new RecyclerSupplierAdapter(getActivity(), arraySupplier);
                                recyclerview.setAdapter(adapter_supplier);
                            }

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
            }
        });
    }

    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = null;

        //home page view all
        if (type.equalsIgnoreCase(getResources().getString(R.string.latest_products))) {
            SCREEN_ID = "2";
            calls = apiInterface.GetLatestProducts("1");

        } else if (type.equalsIgnoreCase(getResources().getString(R.string.best_selling))) {
            SCREEN_ID = "2";
            calls = apiInterface.GetBestSellingProducts("1");

        } else if (type.equalsIgnoreCase(getResources().getString(R.string.ordernow))) {
            SCREEN_ID = "1";
            calls = apiInterface.GetOrderNowProducts("1");

        } else if (type.equalsIgnoreCase(getResources().getString(R.string.order_kitchens))) {
            llMain.setVisibility(View.GONE);
            calls = apiInterface.GetKitchens("1");
        } else {
            //by deafult showing latest products
            SCREEN_ID = "2";
            calls = apiInterface.GetLatestProducts("1");
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
                            arrayProducts = new ArrayList<>();
                            arraySupplier = new ArrayList<>();
                            if (object.has("latest_products")) {
                                JSONArray jLarray = object.getJSONArray("latest_products");
                                for (int i = 0; i < jLarray.length(); i++) {
                                    JSONObject obj = jLarray.getJSONObject(i);
                                    HomeItem item = new HomeItem();
                                    item.setDish_id(obj.getString("product_id"));
                                    item.setDish_description(obj.getString("product_description"));
                                    item.setDish_image(obj.getString("main_image"));
                                    item.setDish_name(obj.getString("product_name"));
                                    item.setDish_price(obj.getString("product_price"));
                                    item.setRate_point(obj.getString("rate_point"));
                                    item.setIsFavo(obj.getString("is_fav"));
                                    item.setFavo_count(obj.getString("fav_count"));
                                    item.setUser_views(obj.getString("user_view"));
                                    item.setCart_qty(obj.getInt("cart_qty"));
                                    item.setDiscount(obj.getString("discount"));
                                    //check attributes and if attributes then take him to detail screen else add to cart
                                    if (obj.has("product_attributes")) {
                                        JSONArray jAttribute = obj.getJSONArray("product_attributes");
                                        if (jAttribute.length() != 0) {
                                            item.setAttributes(true);
                                        } else {
                                            item.setAttributes(false);
                                        }
                                    }
                                    arrayProducts.add(item);
                                }
                            }


                            if (object.has("best_selling_products")) {
                                JSONArray jLarray = object.getJSONArray("best_selling_products");
                                for (int i = 0; i < jLarray.length(); i++) {
                                    JSONObject obj = jLarray.getJSONObject(i);
                                    HomeItem item = new HomeItem();
                                    item.setDish_id(obj.getString("product_id"));
                                    item.setDish_description(obj.getString("product_description"));
                                    item.setDish_image(obj.getString("main_image"));
                                    item.setDish_name(obj.getString("product_name"));
                                    item.setDish_price(obj.getString("product_price"));
                                    item.setRate_point(obj.getString("rate_point"));
                                    item.setIsFavo(obj.getString("is_fav"));
                                    item.setFavo_count(obj.getString("fav_count"));
                                    item.setUser_views(obj.getString("user_view"));
                                    item.setCart_qty(obj.getInt("cart_qty"));
                                    item.setDiscount(obj.getString("discount"));
                                    //check attributes and if attributes then take him to detail screen else add to cart
                                    if (obj.has("product_attributes")) {
                                        JSONArray jAttribute = obj.getJSONArray("product_attributes");
                                        if (jAttribute.length() != 0) {
                                            item.setAttributes(true);
                                        } else {
                                            item.setAttributes(false);
                                        }
                                    }
                                    arrayProducts.add(item);
                                }
                            }


                            //TODO if order now   FOOD PRODUCTS
                            if (object.has("order_now")) {
                                JSONArray jONarray = object.getJSONArray("order_now");
                                for (int i = 0; i < jONarray.length(); i++) {
                                    JSONObject obj = jONarray.getJSONObject(i);
                                    HomeItem item = new HomeItem();
                                    item.setDish_id(obj.getString("dish_id"));
                                    item.setDish_description(obj.getString("dish_description"));
                                    item.setDish_image(obj.getString("dish_image"));
                                    item.setDish_name(obj.getString("dish_name"));
                                    item.setDish_price(obj.getString("dish_price"));
                                    item.setRate_point(obj.getString("rate_point"));
                                    item.setIsFavo(obj.getString("is_fav"));
                                    item.setFavo_count(obj.getString("fav_count"));
                                    item.setUser_views(obj.getString("user_view"));
                                    item.setCart_qty(obj.getInt("cart_qty"));
                                    item.setDiscount(obj.getString("discount"));
                                    item.setAttributes(false);
                                    arrayProducts.add(item);
                                }
                            }

                            if (object.has("online_kitchens")) {
                                JSONArray jsarray = object.getJSONArray("online_kitchens");
                                for (int i = 0; i < jsarray.length(); i++) {
                                    JSONObject jobj = jsarray.getJSONObject(i);
                                    categories cat = new categories();
                                    cat.setId(jobj.getString("id"));
                                    cat.set_name(jobj.getString("name"));
                                    cat.setimage(jobj.getString("profile_pic"));
                                    cat.setAvailable(jobj.getString("availability"));
                                    arraySupplier.add(cat);
                                }

                            }
                            if (arrayProducts.size() > 0) {
                                recyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                                recyclerview.setItemAnimator(new DefaultItemAnimator());
                                recyclerview.setNestedScrollingEnabled(false);
                                HomeAdapter adapter_latestp = new HomeAdapter(getActivity(), arrayProducts, SCREEN_ID);
                                recyclerview.setAdapter(adapter_latestp);
                            }

                            if (arraySupplier.size() > 0) {
                                recyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                                recyclerview.setItemAnimator(new DefaultItemAnimator());
                                recyclerview.setNestedScrollingEnabled(false);
                                RecyclerSupplierAdapter adapter_supplier = new RecyclerSupplierAdapter(getActivity(), arraySupplier);
                                recyclerview.setAdapter(adapter_supplier);
                            }

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
            }
        });
    }

    private void show_sortdialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_dialog_sorting);
        dialog.setCanceledOnTouchOutside(false);
        ListView sortingList = (ListView) dialog.findViewById(R.id.sortingList);
        if (sortingArray != null && sortingArray.length != 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_listview_text, sortingArray);
            sortingList.setAdapter(adapter);
        }
        sortingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < arraySortingMethods.size(); i++) {
                    if (arraySortingMethods.get(i).getName().equalsIgnoreCase(sortingArray[position])) {
                        sorting = arraySortingMethods.get(i).getId();
//                        if (sorting.equalsIgnoreCase("2")) {
//                            if (mCurrentLocation != null) {
//                                lat = String.valueOf(mCurrentLocation.getLatitude());
//                                lng = String.valueOf(mCurrentLocation.getLongitude());
//                            }
//                        }else{
//                            lat = "";
//                            lng ="";
//                        }
                    }
                }
                dialog.dismiss();
                if (CommonMethods.checkConnection()) {
                    getFilterData();
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
                }
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    private void getFilterCategory(String type) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.GetFilterDataChange("2", type,sessionManager.getCountryName(),"");

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        arrayCategory = new ArrayList<>();
                        arraySortingMethods = new ArrayList<>();
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONObject jsonObject = object.getJSONObject("success");
                            filterJson = object.toString();
                            //category array
//                            JSONArray cat_array = jsonObject.getJSONArray("category");
//                            for (int i = 0; i < cat_array.length(); i++) {
//                                JSONObject obj = cat_array.getJSONObject(i);
//                                NameID nameID = new NameID();
//                                nameID.setId(obj.getString("category_id"));
//                                nameID.setName(obj.getString("category_name"));
//                                arrayCategory.add(nameID);
//                            }

                            //sorting array
                            JSONArray sort_array = jsonObject.getJSONArray("sortmethod");
                            sortingArray = new String[sort_array.length()];
                            for (int i = 0; i < sort_array.length(); i++) {
                                JSONObject obj = sort_array.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setId(obj.getString("sort_id"));
                                nameID.setName(obj.getString("sortmethod_name"));
                                sortingArray[i] = obj.getString("sortmethod_name");
                                arraySortingMethods.add(nameID);
                            }

                            //tractions charges
                            JSONObject object1 = jsonObject.getJSONObject("transactions");
                            sessionManager.saveCharges(object1.getString("vat"),
                                    object1.getString("freshhomee_fee"),
                                    object1.getString("transaction_fee"));

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (data.getBooleanExtra(ISLOAD, true)) {
                ArrayList<String> department = data.getStringArrayListExtra("department");
                categoriesdata = TextUtils.join(",", department);
                Log.e("RaviOne  "," latest "+categoriesdata);
                filterratingdata = data.getStringExtra("average_customer_review");
                newarrivalsData = data.getStringExtra("new_arrival");
                filterpricedata = data.getStringExtra("price");
                filterdiscountData = data.getStringExtra("discount");
                if (CommonMethods.checkConnection()) {
                    getFilterData();
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
                }
            }
        }
    }
}
