package com.freshhome.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
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
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.AdapterClass.BuyerShowDataAdapter;
import com.freshhome.AdapterClass.SearchListAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.FlowLayout;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.HomeFilterActivity;
import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.ProductDetail;
import com.freshhome.ProductFilterActivity;
import com.freshhome.R;
import com.freshhome.datamodel.HomeItem;
import com.freshhome.datamodel.NameID;
import com.freshhome.datamodel.categories;
import com.freshhome.datamodel.product_attributes;
import com.freshhome.datamodel.subAttributes;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class AllProductShowFromCatFragment extends Fragment implements BuyerShowDataAdapter.OnUpdateBannerListener{
    private String filterratingdata, categoriesdata, newarrivalsData, filterpricedata, filterdiscountData;
    ArrayList<HomeItem> arrayHomeList;
    public static int REQUEST_CODE = 11;
    public static String MEALS = "meals";
    public static String CATEGORY = "category";
    public static String CUISINE = "cuisines";
    public static String ISLOAD = "isload";

    ArrayList<NameID> arrayCategory, arrayCuisines, arrayCity, arrayMeals, arraySortingMethods,arrayCatFiltr;
    String[] sortingArray;
    String filterJson = "";
    //screen id is product type id(food,home made,shops)
    String city_id = "", perpage = "300", categories = "",
            cuisines = "", meals = "", sorting = "", screen_id = "2", lat = "", lng = "";

    ArrayList<com.freshhome.datamodel.categories> arraySupplier,arrayCategories;
    ArrayList<HomeItem>  arrayOrderNow,arrayBestSelling,arrayLatestProduct;

    ApiInterface apiInterface;
    @BindView(R.id.search_list)
    EditText search_list;
    @BindView(R.id.rvSearch)
    RecyclerView rvSearch;
    @BindView(R.id.tvNoDataFound)
    TextView tvNoDataFound;

    private BuyerShowDataAdapter searchListAdapter;

    private ArrayList<HomeItem> main_data;
    private ArrayList<HomeItem> main_data1;

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
    public static UserSessionManager sessionManager;
    private String homecategoryID = "";
    private String homecategoryHeader = "";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_product_show_from_cat, container, false);
        ButterKnife.bind(this,view);

        sessionManager = new UserSessionManager(getActivity());
        apiInterface = ApiClient.getInstance().getClient();
        main_data = new ArrayList<>();
        main_data1 = new ArrayList<>();
        arraySupplier = new ArrayList<>();
        arrayCategories = new ArrayList<>();
        arrayOrderNow = new ArrayList<>();
        arrayBestSelling = new ArrayList<>();
        arrayLatestProduct = new ArrayList<>();

        arrayCategory = new ArrayList<>();
        arrayCatFiltr = new ArrayList<>();
        arrayCuisines = new ArrayList<>();
        arrayCity = new ArrayList<>();
        arrayMeals = new ArrayList<>();
        arraySortingMethods = new ArrayList<>();
        arrayHomeList = new ArrayList<>();

        linear_cart_info = (LinearLayout) view.findViewById(R.id.linear_cart_info);
        linear_cart_info.setVisibility(View.GONE);

        text_items = (TextView) view.findViewById(R.id.text_items);
        text_total_price = (TextView) view.findViewById(R.id.text_total_price);
        text_viewcart = (TextView) view.findViewById(R.id.text_viewcart);
        text_viewcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MainActivity_NavDrawer.class);
                i.putExtra("OpenFrag", ConstantValues.OPENCART);
                startActivity(i);
                ActivityCompat.finishAffinity(getActivity());
            }
        });
        search_list.setText("");
       // setUpRecyclerView();



        if(getArguments() != null){
            homecategoryID = getArguments().getString("cat_id");
            homecategoryHeader = getArguments().getString("cat_header");
            getAllProductsfromCateSideData(homecategoryID);
        }else {
           // getSearchData();
        }


        search_list.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (search_list.getText().toString().trim().length() > 0) {
                    main_data.clear();
                    main_data.addAll(main_data1);
                    List<HomeItem> list = filter(search_list.getText().toString(),
                            main_data, true);
                    main_data.clear();
                    main_data.addAll(list);
                    System.out.println("the size is " + list.size());
                    searchListAdapter.update(main_data);
                    //searchListAdapter.notifyDataSetChanged();
                    if(main_data.size()>0){
                        tvNoDataFound.setVisibility(View.GONE);
                    }else {
                        tvNoDataFound.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvNoDataFound.setVisibility(View.VISIBLE);
                    main_data.clear();
                    //main_data.addAll(main_data1);
                    searchListAdapter.update(main_data);
                    //searchListAdapter.notifyDataSetChanged();
                }
                search_list.requestFocus();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });

        getFilterCategory("product");
        return  view;

    }
    @SuppressLint("DefaultLocale")
    public static List<HomeItem> filter(String string,
                                        Iterable<HomeItem> iterable, boolean byName) {
        if (iterable == null)
            return new LinkedList<HomeItem>();
        else {
            List<HomeItem> collected = new LinkedList<HomeItem>();
            Iterator<HomeItem> iterator = iterable.iterator();
            if (iterator == null)
                return collected;
            while (iterator.hasNext()) {
                HomeItem item = iterator.next();

                System.out.println("the name is " + item.getValue());
                if (item.getValue().toLowerCase().contains(string.toLowerCase()))
                    collected.add(item);
            }
            return collected;
        }
    }


    @OnClick({R.id.iv_filter,R.id.iv_sort})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_filter:
//                for (int i = 0; i < arraySortingMethods.size(); i++) {
//                    categories = arrayCatFiltr.get(i).getId();
//                }

                Intent i = new Intent(getActivity(), ProductFilterActivity.class);
                i.putExtra("json", filterJson);
                i.putExtra("average_customer_review",filterratingdata);
                i.putExtra("new_arrival",newarrivalsData);
                i.putExtra("price",filterpricedata);
                i.putExtra("discount",filterdiscountData);
                i.putExtra("department",categoriesdata);

                i.putExtra("filter", AllProductShowFromCatFragment.class.getSimpleName());
                startActivityForResult(i, REQUEST_CODE);
                break;

            case R.id.iv_sort:
                show_sortdialog();
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity_NavDrawer.heading.setText(homecategoryHeader);
        if (CommonMethods.checkConnection()) {
           // getFilterdata();
            getFilterCategory("product");
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
        }
    }


    private void setUpRecyclerView() {
        rvSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        searchListAdapter = new BuyerShowDataAdapter(getContext(),main_data,"2");
        rvSearch.setAdapter(searchListAdapter);
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
                        //categoriesdata = arrayCatFiltr.get(i).getId();
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
                    getListData("","","","","");
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
                }
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }


    private void getAllProductsfromCateSideData(String categories) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.GetCatProducts
                (city_id, categories, perpage, "8",
                        sessionManager.getLoginType(), "2", lat, lng);

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
                                    item.setValue(obj.getString("dish_name"));

                                    item.setAttributes(false);
                                    arrayHomeList.add(item);

//                                item.setDate_time(obj.getString(""));
                                }
                                main_data.clear();
                                main_data.addAll(arrayHomeList);
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
                                    item.setValue(obj.getString("product_name"));
                                    //check attributes and if attributes then take him to detail screen else add to cart
                                    if (obj.has("product_attributes")) {
                                        JSONArray jAttribute = obj.getJSONArray("product_attributes");
                                        if (jAttribute.length() != 0) {
                                            item.setAttributes(true);
                                        } else {
                                            item.setAttributes(false);
                                        }
                                    }

                                    if (obj.has("categories")) {
                                        JSONArray cat_array = obj.getJSONArray("categories");
                                        for (int j = 0; j < cat_array.length(); j++) {
                                            JSONObject obj1 = cat_array.getJSONObject(j);
                                            NameID nameID = new NameID();
                                            nameID.setId(obj1.getString("category_id"));
                                            arrayCatFiltr.add(nameID);
                                        }
                                    }

                                    arrayHomeList.add(item);

                                }
                                main_data.clear();
                                main_data.addAll(arrayHomeList);
                            }


                            main_data1.clear();
                            main_data1.addAll(main_data);
                            rvSearch.setVisibility(View.VISIBLE);
                            rvSearch.setLayoutManager(new GridLayoutManager(getActivity(),2));
                            rvSearch.setItemAnimator(new DefaultItemAnimator());
                            rvSearch.setNestedScrollingEnabled(false);
                            searchListAdapter = new BuyerShowDataAdapter(getActivity(), arrayHomeList, screen_id);
                            searchListAdapter.setOnUpdateBannerListener(AllProductShowFromCatFragment.this);
                            rvSearch.setAdapter(searchListAdapter);
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
                            rvSearch.setVisibility(View.GONE);
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
//                            JSONArray city_array = jsonObject.getJSONArray("city");
//                            if (city_array.length() != 0) {
//                                spinner_array = new String[city_array.length() + 1];
//                                //Add default value All for city to get all values intially
//                                NameID nameID_Default = new NameID();
//                                nameID_Default.setId("");
//                                nameID_Default.setName("All Cities");
//                                spinner_array[0] = "All Cities";
//                                arrayCity.add(nameID_Default);
//                                for (int i = 0; i < city_array.length(); i++) {
//                                    JSONObject obj = city_array.getJSONObject(i);
//                                    NameID nameID = new NameID();
//                                    nameID.setId(obj.getString("city_id"));
//                                    nameID.setName(obj.getString("city_name"));
//                                    spinner_array[i + 1] = obj.getString("city_name");
//                                    arrayCity.add(nameID);
//                                }
//                            }
//                            if (spinner_array != null && spinner_array.length != 0) {
//                                ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner_text, spinner_array);
//                                locationspinner.setAdapter(spinner_adapter);
//                            }

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
    public static void updateCartBanner(UserSessionManager sessionManager, String cart_items, String price) {
        if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToEat)) {
            if (!cart_items.equalsIgnoreCase("") && !cart_items.equalsIgnoreCase("0")) {
                linear_cart_info.setVisibility(View.GONE);
                text_items.setText(cart_items + " Items");
                text_total_price.setText(price);
            } else {
                linear_cart_info.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onUpdateCardBanner(String cardItem, String currency) {
        if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToEat)) {
            if (!cardItem.equalsIgnoreCase("") && !cardItem.equalsIgnoreCase("0")) {
                linear_cart_info.setVisibility(View.GONE);
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
                    getListData(filterdiscountData,filterpricedata,categoriesdata,newarrivalsData,filterratingdata);
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
                }
            }
        }
    }


    private void getListData(String filterdiscountData,String filterpricedata,String categorie,String newarrivalsData,String filterratingdata) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        if(categoriesdata != null && !categoriesdata.isEmpty()){

        }else {
            categoriesdata = homecategoryID;
        }
        Call<JsonElement> calls = apiInterface.GetDishItemsFilter
                (filterdiscountData, filterpricedata, categoriesdata, newarrivalsData,filterratingdata, perpage, sorting,
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
                            rvSearch.setVisibility(View.VISIBLE);
                            BuyerShowDataAdapter adapter = new BuyerShowDataAdapter(getActivity(), arrayHomeList, screen_id);
                            adapter.setOnUpdateBannerListener(AllProductShowFromCatFragment.this);
                            rvSearch.setAdapter(adapter);
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
                            rvSearch.setVisibility(View.GONE);
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



    private void getFilterCategory(String type) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.GetFilterDataChange("2", type,"United Arab Emirates",homecategoryID);

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
}
