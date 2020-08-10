package com.freshhome.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.freshhome.AdapterClass.HomeAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.HomeFilterActivity;
import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.ProductDetail;
import com.freshhome.R;
import com.freshhome.TrackOrderActivity;
import com.freshhome.datamodel.HomeItem;
import com.freshhome.datamodel.NameID;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener, HomeAdapter.OnUpdateBannerListener {
    RecyclerView commentsList;
    Spinner locationspinner;
    ImageView spinner_arrow, image_search, image_filter, image_sort;
    EditText edit_search;
    ApiInterface apiInterface;
    public static UserSessionManager sessionManager;
    ArrayList<NameID> arrayCategory, arrayCuisines, arrayCity, arrayMeals, arraySortingMethods;
    String[] sortingArray;
    String filterJson = "";
    //screen id is product type id(food,home made,shops)
    String city_id = "", perpage = "300", categories = "",
            cuisines = "", meals = "", sorting = "", screen_id = "", lat = "", lng = "";
    //stype_array supplier type
    String[] spinner_array, stype_array;
    ArrayList<HomeItem> arrayHomeList;
    public static int REQUEST_CODE = 1;
    public static String MEALS = "meals";
    public static String CATEGORY = "category";
    public static String CUISINE = "cuisines";
    public static String ISLOAD = "isload";
    HomeAdapter adapter;
    public static LinearLayout linear_cart_info, linear_product_types;
    public static TextView text_items, text_total_price, text_viewcart, textview_food, textview_products, textview_shop;

    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    int PERMISSION_ALL = 1;
    // location last updated time
    private String mLastUpdateTime;
    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    boolean isFirstTime = true;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        sessionManager = new UserSessionManager(getActivity());
        apiInterface = ApiClient.getInstance().getClient();
        MainActivity_NavDrawer.heading.setText(R.string.home);
        MainActivity_NavDrawer.image_addmenu.setVisibility(View.GONE);
        arrayCategory = new ArrayList<>();
        arrayCuisines = new ArrayList<>();
        arrayCity = new ArrayList<>();
        arrayMeals = new ArrayList<>();
        arraySortingMethods = new ArrayList<>();
        arrayHomeList = new ArrayList<>();

        linear_cart_info = (LinearLayout) v.findViewById(R.id.linear_cart_info);
        linear_cart_info.setVisibility(View.GONE);

        textview_food = (TextView) v.findViewById(R.id.textview_food);
        textview_food.setOnClickListener(this);
        textview_products = (TextView) v.findViewById(R.id.textview_products);
        textview_products.setOnClickListener(this);
        textview_shop = (TextView) v.findViewById(R.id.textview_shop);
        textview_shop.setOnClickListener(this);

        text_items = (TextView) v.findViewById(R.id.text_items);
        text_total_price = (TextView) v.findViewById(R.id.text_total_price);
        text_viewcart = (TextView) v.findViewById(R.id.text_viewcart);
        text_viewcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MainActivity_NavDrawer.class);
                i.putExtra("OpenFrag", ConstantValues.OPENCART);
                startActivity(i);
                ActivityCompat.finishAffinity(getActivity());
            }
        });

        commentsList = (RecyclerView) v.findViewById(R.id.commentsList);
        commentsList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        commentsList.setItemAnimator(new DefaultItemAnimator());
        spinner_arrow = (ImageView) v.findViewById(R.id.spinner_arrow);
        spinner_arrow.setOnClickListener(this);
        edit_search = (EditText) v.findViewById(R.id.edit_search);
        image_search = (ImageView) v.findViewById(R.id.image_search);
        image_search.setOnClickListener(this);
        image_filter = (ImageView) v.findViewById(R.id.image_filter);
        image_filter.setOnClickListener(this);
        image_sort = (ImageView) v.findViewById(R.id.image_sort);
        image_sort.setOnClickListener(this);
        locationspinner = (Spinner) v.findViewById(R.id.locationspinner);
        //enable home food category by default
        screen_id = ConstantValues.home_food;
        enableButton(textview_food);
        disableButton(textview_shop);
        disableButton(textview_products);

        edit_search.setTag(false);
        edit_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                edit_search.setTag(true);
            }
        });

        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((Boolean) edit_search.getTag() == true) {
                    if (count == 0) {
                        if (CommonMethods.checkConnection()) {
                            getSearchedData(edit_search.getText().toString());
                        } else {
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

//        if (CommonMethods.checkConnection()) {
//            getFilterdata();
//        } else {
//            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
//        }


        locationspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (isFirstTime == false) {
                    if (locationspinner.getSelectedItem().toString().equalsIgnoreCase(arrayCity.get(position).getName())) {
                        city_id = arrayCity.get(position).getId();
                        if (CommonMethods.checkConnection()) {
                            getListData();
                        } else {
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
                        }
                    }
//                } else {
//                    isFirstTime = false;
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        requestpermission(false);
        initLocation();
        startLocationUpdates();

        return v;
    }




    private void getListData() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.GetDishItems
                (city_id, cuisines, categories, meals, perpage, sorting,
                        sessionManager.getLoginType(), screen_id, lat, lng);

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        arrayHomeList = new ArrayList<>();
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            //unread_notifications
                            if (object.getString("unread_notifications").equalsIgnoreCase("0")) {
                                MainActivity_NavDrawer.linear_badge.setVisibility(View.GONE);
                            } else {
                                MainActivity_NavDrawer.linear_badge.setVisibility(View.VISIBLE);
                                MainActivity_NavDrawer.text_badge.setText(object.getString("unread_notifications"));
                            }
                            JSONObject jsonObject = object.getJSONObject("success");
                            JSONArray jarray = jsonObject.getJSONArray("data");
                            if (screen_id.equalsIgnoreCase(ConstantValues.home_food)) {
                                //home food
                                for (int i = 0; i < jarray.length(); i++) {
                                    JSONObject obj = jarray.getJSONObject(i);
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
                                    item.setAttributes(false);
                                    arrayHomeList.add(item);
//                                item.setDate_time(obj.getString(""));
                                }
                            } else {
                                //hand made product and shop products
                                for (int i = 0; i < jarray.length(); i++) {
                                    JSONObject obj = jarray.getJSONObject(i);
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

                                    //check attributes and if attributes then take him to detail screen else add to cart
                                    if (obj.has("product_attributes")) {
                                        JSONArray jAttribute = obj.getJSONArray("product_attributes");
                                        if (jAttribute.length() != 0) {
                                            item.setAttributes(true);
                                        } else {
                                            item.setAttributes(false);
                                        }
                                    }
                                    arrayHomeList.add(item);
                                }

                            }
                            commentsList.setVisibility(View.VISIBLE);
                            adapter = new HomeAdapter(getActivity(), arrayHomeList, screen_id);
                            adapter.setOnUpdateBannerListener(HomeFragment.this);
                            commentsList.setAdapter(adapter);
//                            }

                            //supplier details //update navigation drawer
                            JSONObject Supplierobj = jsonObject.getJSONObject("supplier");
                            MainActivity_NavDrawer.text_name.setText(Supplierobj.getString("username"));
                            if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToCook)) {
                                MainActivity_NavDrawer.linear_active_inactive.setVisibility(View.VISIBLE);
                                if (Supplierobj.getString("availability").equalsIgnoreCase("online")) {
                                    MainActivity_NavDrawer.linear_active_inactive.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green_filled));
                                } else {
                                    MainActivity_NavDrawer.linear_active_inactive.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_gray));
                                }
                            } else {
                                MainActivity_NavDrawer.linear_active_inactive.setVisibility(View.GONE);
                            }

                            if (!Supplierobj.getString("profile_pic").equalsIgnoreCase("")) {
//                                    MainActivity_NavDrawer.profile_image
                                Picasso.get().load(Supplierobj.getString("profile_pic")).placeholder(R.drawable.icon).into(MainActivity_NavDrawer.profile_image);
                            }


                            //set cart banner at bottom
                            if (jsonObject.has("cart_data")) {
                                JSONObject cartobj = jsonObject.getJSONObject("cart_data");
                                if (cartobj.has("cart_items")) {
                                    JSONObject priceobj = cartobj.getJSONObject("cart_total");
                                    updateCartBanner(sessionManager,cartobj.getString("cart_items"), priceobj.getString("currency") + " " + priceobj.getString("total"));
                                }
                            }

                            //get subscription detail and save in preferences
                            if (object.has("subscription_data")) {
                                JSONObject subjson = object.getJSONObject("subscription_data");
                                sessionManager.saveSubscriptionDetails(
                                        subjson.getString("status"),
                                        subjson.getString("subscription_status"),
                                        subjson.getString("subscription_start_format"),
                                        subjson.getString("subscription_end_format"));
                            }

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
                            commentsList.setVisibility(View.GONE);
                            //set cart banner at bottom

                            if (object.has("cart_data")) {
                                JSONObject cartobj = object.getJSONObject("cart_data");
                                if (cartobj.has("cart_items")) {
                                    JSONObject priceobj = cartobj.getJSONObject("cart_total");
                                    updateCartBanner(sessionManager,cartobj.getString("cart_items"), priceobj.getString("currency") + " " + priceobj.getString("total"));
                                }
                            }

                            //get subscription detail and save in preferences
                            if (object.has("subscription_data")) {
                                JSONObject subjson = object.getJSONObject("subscription_data");
                                sessionManager.saveSubscriptionDetails(
                                        subjson.getString("status"),
                                        subjson.getString("subscription_status"),
                                        subjson.getString("subscription_start_format"),
                                        subjson.getString("subscription_end_format"));
                            }
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

    public static void updateCartBanner(UserSessionManager sessionManager, String cart_items, String price) {
        if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToEat)) {
            if (!cart_items.equalsIgnoreCase("") && !cart_items.equalsIgnoreCase("0")) {
                linear_cart_info.setVisibility(View.VISIBLE);
                text_items.setText(cart_items + " Items");
                text_total_price.setText(price);
            } else {
                linear_cart_info.setVisibility(View.GONE);
            }
        }
    }

    private void getFilterdata() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.GetFilterData(sessionManager.getCountryName());

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        arrayCategory = new ArrayList<>();
                        arrayCuisines = new ArrayList<>();
                        arrayCity = new ArrayList<>();
                        arrayMeals = new ArrayList<>();
                        arraySortingMethods = new ArrayList<>();
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONObject jsonObject = object.getJSONObject("success");
                            filterJson = object.toString();
                            //category array
                            JSONArray cat_array = jsonObject.getJSONArray("category");
                            for (int i = 0; i < cat_array.length(); i++) {
                                JSONObject obj = cat_array.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setId(obj.getString("category_id"));
                                nameID.setName(obj.getString("category_name"));
                                arrayCategory.add(nameID);
                            }

                            //cuisines array
                            JSONArray cuis_array = jsonObject.getJSONArray("cuisine");
                            for (int i = 0; i < cuis_array.length(); i++) {
                                JSONObject obj = cuis_array.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setId(obj.getString("cuisine_id"));
                                nameID.setName(obj.getString("cuisine_name"));
                                arrayCuisines.add(nameID);
                            }

                            //city array
                            JSONArray city_array = jsonObject.getJSONArray("city");
                            if (city_array.length() != 0) {
                                spinner_array = new String[city_array.length() + 1];
                                //Add default value All for city to get all values intially
                                NameID nameID_Default = new NameID();
                                nameID_Default.setId("");
                                nameID_Default.setName("All Cities");
                                spinner_array[0] = "All Cities";
                                arrayCity.add(nameID_Default);
                                for (int i = 0; i < city_array.length(); i++) {
                                    JSONObject obj = city_array.getJSONObject(i);
                                    NameID nameID = new NameID();
                                    nameID.setId(obj.getString("city_id"));
                                    nameID.setName(obj.getString("city_name"));
                                    spinner_array[i + 1] = obj.getString("city_name");
                                    arrayCity.add(nameID);
                                }
                            }
                            if (spinner_array != null && spinner_array.length != 0) {
                                ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner_text, spinner_array);
                                locationspinner.setAdapter(spinner_adapter);
                            }

                            //meal array
                            JSONArray meal_array = jsonObject.getJSONArray("meal");
                            for (int i = 0; i < meal_array.length(); i++) {
                                JSONObject obj = meal_array.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setId(obj.getString("meal_id"));
                                nameID.setName(obj.getString("meal_name"));
                                arrayMeals.add(nameID);
                            }


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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.spinner_arrow:
                locationspinner.performClick();
                break;


            case R.id.image_filter:
                Intent i = new Intent(getActivity(), HomeFilterActivity.class);
                i.putExtra("json", filterJson);
                i.putExtra(MEALS, meals);
                i.putExtra(CATEGORY, categories);
                i.putExtra(CUISINE, cuisines);
                startActivityForResult(i, REQUEST_CODE);

                break;

            case R.id.image_sort:
                show_sortdialog();
                break;

            case R.id.image_search:
                if (!edit_search.getText().toString().equalsIgnoreCase("")) {
                    if (CommonMethods.checkConnection()) {
                        getSearchedData(edit_search.getText().toString());
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
                    }
                }
                break;


            case R.id.textview_shop:
                screen_id = ConstantValues.shops;
                enableButton(textview_shop);
                disableButton(textview_food);
                disableButton(textview_products);
                break;

            case R.id.textview_products:
                screen_id = ConstantValues.home_products;
                enableButton(textview_products);
                disableButton(textview_food);
                disableButton(textview_shop);
                break;

            case R.id.textview_food:
                screen_id = ConstantValues.home_food;
                enableButton(textview_food);
                disableButton(textview_shop);
                disableButton(textview_products);
                break;
        }
    }

    private void enableButton(TextView textview_shop) {
        textview_shop.setTextColor(getResources().getColor(R.color.white));
        textview_shop.setBackgroundResource(R.drawable.btn_with_shadow_green);
        if (CommonMethods.checkConnection()) {
            getFilterdata();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
        }
    }

    private void disableButton(TextView textview_food) {
        textview_food.setTextColor(getResources().getColor(R.color.light_gray));
        textview_food.setBackgroundResource(R.drawable.btn_with_shadow);

    }

    private void show_FilterDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_dialog_filtering);
        dialog.setCanceledOnTouchOutside(false);
        ImageView image_done = (ImageView) dialog.findViewById(R.id.image_done);
        Spinner locationspinner = (Spinner) dialog.findViewById(R.id.locationspinner);
        String[] locArray = getActivity().getResources().getStringArray(R.array.location);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_listview_text, locArray);
        locationspinner.setAdapter(adapter);
        locationspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        image_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

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
                        if (sorting.equalsIgnoreCase("2")) {
                            if (mCurrentLocation != null) {
                                lat = String.valueOf(mCurrentLocation.getLatitude());
                                lng = String.valueOf(mCurrentLocation.getLongitude());
                            }
                        }else{
                            lat = "";
                            lng ="";
                        }
                    }
                }
                dialog.dismiss();
                if (CommonMethods.checkConnection()) {
                    getListData();
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
                }
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (data.getBooleanExtra(ISLOAD, true)) {
                meals = data.getStringExtra(MEALS);
                categories = data.getStringExtra(CATEGORY);
                cuisines = data.getStringExtra(CUISINE);
                if (CommonMethods.checkConnection()) {
                    getListData();
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
                }
            }
        }
    }

    private void getSearchedData(String name) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.GetSearchedItems(city_id, perpage, name, screen_id);

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        CommonMethods.hideSoftKeyboard(getActivity());
                        arrayHomeList = new ArrayList<>();
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONObject jsonObject = object.getJSONObject("success");
                            JSONArray jarray = jsonObject.getJSONArray("data");
                            if (screen_id.equalsIgnoreCase(ConstantValues.home_food)) {
                                //home food
                                for (int i = 0; i < jarray.length(); i++) {
                                    JSONObject obj = jarray.getJSONObject(i);
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
                                    arrayHomeList.add(item);
//                                item.setDate_time(obj.getString(""));
                                }
                            } else {
                                //hand made product and shop products
                                for (int i = 0; i < jarray.length(); i++) {
                                    JSONObject obj = jarray.getJSONObject(i);
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
                                    arrayHomeList.add(item);
//                                item.setDate_time(obj.getString(""));
                                }
                            }
                            commentsList.setVisibility(View.VISIBLE);
//                            if (adapter != null) {
//                                adapter.notifyDataSetChanged();
//                            } else {
                            adapter = new HomeAdapter(getActivity(), arrayHomeList, screen_id);
                            adapter.setOnUpdateBannerListener(HomeFragment.this);
                            commentsList.setAdapter(adapter);
//                            }
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
                            commentsList.setVisibility(View.GONE);
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
    public void onResume() {
        super.onResume();
        if (CommonMethods.checkConnection()) {
            getFilterdata();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
        }
    }

    private void getLOGIN() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.ulogin("ankit", "ankitdureja0077@gmail.com");

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        arrayCategory = new ArrayList<>();
                        arrayCuisines = new ArrayList<>();
                        arrayCity = new ArrayList<>();
                        arrayMeals = new ArrayList<>();
                        arraySortingMethods = new ArrayList<>();
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONObject jsonObject = object.getJSONObject("success");
                            filterJson = object.toString();
                            //category array
                            JSONArray cat_array = jsonObject.getJSONArray("category");
                            for (int i = 0; i < cat_array.length(); i++) {
                                JSONObject obj = cat_array.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setId(obj.getString("category_id"));
                                nameID.setName(obj.getString("category_name"));
                                arrayCategory.add(nameID);
                            }


                            //cuisines array
                            JSONArray cuis_array = jsonObject.getJSONArray("cuisine");
                            for (int i = 0; i < cuis_array.length(); i++) {
                                JSONObject obj = cuis_array.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setId(obj.getString("cuisine_id"));
                                nameID.setName(obj.getString("cuisine_name"));
                                arrayCuisines.add(nameID);
                            }


                            //city array
                            JSONArray city_array = jsonObject.getJSONArray("city");
                            if (city_array.length() != 0) {
                                spinner_array = new String[city_array.length() + 1];
                                //Add default value All for city to get all values intially
                                NameID nameID_Default = new NameID();
                                nameID_Default.setId("");
                                nameID_Default.setName("All");
                                spinner_array[0] = "All";
                                arrayCity.add(nameID_Default);
                                for (int i = 0; i < city_array.length(); i++) {
                                    JSONObject obj = city_array.getJSONObject(i);
                                    NameID nameID = new NameID();
                                    nameID.setId(obj.getString("city_id"));
                                    nameID.setName(obj.getString("city_name"));
                                    spinner_array[i + 1] = obj.getString("city_name");
                                    arrayCity.add(nameID);
                                }
                            }
                            if (spinner_array != null && spinner_array.length != 0) {
                                ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner_sales, spinner_array);
                                locationspinner.setAdapter(spinner_adapter);
                            }

                            //meal array
                            JSONArray meal_array = jsonObject.getJSONArray("meal");
                            for (int i = 0; i < meal_array.length(); i++) {
                                JSONObject obj = meal_array.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setId(obj.getString("meal_id"));
                                nameID.setName(obj.getString("meal_name"));
                                arrayMeals.add(nameID);
                            }


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

    private void initLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mSettingsClient = LocationServices.getSettingsClient(getActivity());

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                updateLocationUI();
            }
        };

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    //  run time permission  -------------------------------
    private void requestpermission(boolean opencam) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : PERMISSIONS) {
            result = ContextCompat.checkSelfPermission(getActivity(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_ALL);
        } else {
            //all granted
            initLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initLocation();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                        showStoragePermissionRationale();
                    } else {
                        CommonUtilFunctions.show_permission_alert(getActivity(), "Please allow all permission to use all funtionalities in app.");
                    }
                }
            }
            return;
        }
    }

    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            sessionManager.saveCountryName("");
            LatLng loc = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
//                sessionManager.saveCountryName(CommonMethods.getCountryName(getActivity(),mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
            if (mLocationCallback != null) {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }
        }


    }

    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//                        Log.i("tag", "All location settings are satisfied.");
//                        Toast.makeText(getActivity(), "Started location updates!", Toast.LENGTH_SHORT).show();
                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

//                        updateLocationUI();
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i("tag", "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i("tag", "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e("tag", errorMessage);

                                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                        }

//                        updateLocationUI();
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationCallback != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onUpdateCardBanner(String cardItem, String currency) {
        if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToEat)) {
            if (!cardItem.equalsIgnoreCase("") && !cardItem.equalsIgnoreCase("0")) {
                linear_cart_info.setVisibility(View.VISIBLE);
                text_items.setText(cardItem + " Items");
                text_total_price.setText(currency);
            } else {
                linear_cart_info.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onMoveToProductDetails(String itemId, String screenId) {
        Intent i = new Intent(getContext(), ProductDetail.class);
        i.putExtra("item_type", screenId);
        i.putExtra("item_id", itemId);
        i.putExtra("cart_count", text_items.getText().toString());
        i.putExtra("cart_price", text_total_price.getText().toString());
        startActivity(i);
    }
}
