package com.freshhome.DriverModule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.DirectionsJSONParser;
import com.freshhome.CommonUtil.GMapRoutes;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.R;
import com.freshhome.datamodel.KitchenLoc;
import com.freshhome.datamodel.MultipleLocOrder;
import com.freshhome.datamodel.track_driver;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.constraint.Constraints.TAG;

public class DriverCompanyHomefragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    TextView text_notification_count, text_user_name, text_loc, text_user_phone, text_totalprice,
            text_payment_method, text_delivery_time, text_timer, text_order_status, text_products, text_food, text_all_orders;
    ImageView image_notification, image_arrow;
    CircleImageView image_user;
    LinearLayout bottom_sheet, linear_delivery_time, linear_order_type;
    private GoogleMap mMap;
    //    private BottomSheetBehavior mBottomSheetBehavior;
    private SupportMapFragment mapFragment;
    private LinearLayout linear_main_slider;
    ApiInterface apiInterface;
    ArrayList<KitchenLoc> arrayKitchenLoc, arrayStoreHomeLoc, arraySelectedType;
    ArrayList<MultipleLocOrder> arrayPendingOrders;
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
    private static final int REQUEST_ORDER_DELIVER = 1001;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final int REQUEST_ORDER_ACCEPT = 222;
    UserSessionManager sessionManager;
    //    ArrayList<MultipleLocOrder> array_mutipleorderloc;
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    int PERMISSION_ALL = 1;
    boolean isfirstTime = true, isFoodSelected = true;
    ArrayList<Marker> arrayMarkers;
    GMapRoutes gMapRoutes;
    Document document;
    Marker marker1;
    BottomSheetDialog bottomdialog;
    String selected_kitchen_orders = "";
    DatabaseReference ref;
    boolean hasPendingOrder = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_driver_main, container, false);
        requestpermission(false);
        initLocation();
        arrayPendingOrders = new ArrayList<>();
        arrayMarkers = new ArrayList<>();
        arrayKitchenLoc = new ArrayList<>();
        arrayStoreHomeLoc = new ArrayList<>();
        arraySelectedType = new ArrayList<>();
        sessionManager = new UserSessionManager(getActivity());
        apiInterface = ApiClient.getInstance().getClient();
        image_arrow = (ImageView) v.findViewById(R.id.image_arrow);
        image_arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
        text_user_name = (TextView) v.findViewById(R.id.text_user_name);
        text_loc = (TextView) v.findViewById(R.id.text_loc);
        text_user_phone = (TextView) v.findViewById(R.id.text_user_phone);
        text_totalprice = (TextView) v.findViewById(R.id.text_totalprice);
        text_payment_method = (TextView) v.findViewById(R.id.text_payment_method);
        text_delivery_time = (TextView) v.findViewById(R.id.text_delivery_time);
        text_timer = (TextView) v.findViewById(R.id.text_timer);
        text_order_status = (TextView) v.findViewById(R.id.text_order_status);
        text_products = (TextView) v.findViewById(R.id.text_products);
        text_products.setOnClickListener(this);
        text_food = (TextView) v.findViewById(R.id.text_food);
        text_food.setOnClickListener(this);
        text_all_orders = (TextView) v.findViewById(R.id.text_all_orders);
        bottom_sheet = (LinearLayout) v.findViewById(R.id.bottom_sheet);
        bottom_sheet.setVisibility(View.GONE);
        linear_main_slider = (LinearLayout) v.findViewById(R.id.linear_main_slider);
        linear_order_type = (LinearLayout) v.findViewById(R.id.linear_order_type);
//        View bottomSheet = v.findViewById(R.id.bottom_sheet);
//        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
//        mBottomSheetBehavior.setPeekHeight(CommonUtilFunctions.dpToPx(getActivity(), 70));
        linear_main_slider.setVisibility(View.GONE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        gMapRoutes = new GMapRoutes(getActivity());
        return v;
    }

    private void addData() {
//        array_mutipleorderloc
        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                //near by
                MultipleLocOrder order = new MultipleLocOrder();
                order.setId(String.valueOf(i));
                order.setDestination_name("hawas adda");
                order.setDestination_lat("30.969344");
                order.setDestination_lng("75.9373093");
                order.setStarting_name("sansang biys");
                order.setStarting_lat("30.966573");
                order.setStarting_lng("75.9316502");
                order.setDistance_fromuser_loc(distance(Double.valueOf(mCurrentLocation.getLatitude()), Double.valueOf(mCurrentLocation.getLongitude()), Double.valueOf("30.966573"), Double.valueOf("75.9316502")));
//                array_mutipleorderloc.add(order);
            } else if (i == 1) {
                //near by
                MultipleLocOrder order = new MultipleLocOrder();
                order.setId(String.valueOf(i));
                order.setDestination_name("satluj school");
                order.setStarting_name("jai maa chintpurni");
                order.setDestination_lat("30.9695636");
                order.setDestination_lng("75.9394785");
                order.setStarting_lat("30.9695636");
                order.setStarting_lng("75.9394785");
                order.setDistance_fromuser_loc(distance(Double.valueOf(mCurrentLocation.getLatitude()), Double.valueOf(mCurrentLocation.getLongitude()), Double.valueOf("30.9695636"), Double.valueOf("75.9394785")));
//                array_mutipleorderloc.add(order);

            } else if (i == 2) {
                //near by
                MultipleLocOrder order = new MultipleLocOrder();
                order.setId(String.valueOf(i));
                order.setDestination_name("hwas");
                order.setDestination_lat("30.9697642");
                order.setDestination_lng("75.9365327");
                order.setStarting_name("boothgarh");
                order.setStarting_lat("30.9817299");
                order.setStarting_lng("75.9402037");
                order.setDistance_fromuser_loc(distance(Double.valueOf(mCurrentLocation.getLatitude()), Double.valueOf(mCurrentLocation.getLongitude()), Double.valueOf("30.9817299"), Double.valueOf("75.9402037")));

//                array_mutipleorderloc.add(order);

            } else if (i == 3) {
                //little away should be in starting area but delivery too far
                MultipleLocOrder order = new MultipleLocOrder();
                order.setId(String.valueOf(i));
                order.setDestination_name("raur");
                order.setStarting_name("satluj");
                order.setDestination_lat("30.984471");
                order.setDestination_lng("75.9488511");
                order.setStarting_lat("30.9717794");
                order.setStarting_lng("75.9402915");
                order.setDistance_fromuser_loc(distance(Double.valueOf(mCurrentLocation.getLatitude()), Double.valueOf(mCurrentLocation.getLongitude()), Double.valueOf("30.9717794"), Double.valueOf("75.9402915")));
//                array_mutipleorderloc.add(order);

            } else if (i == 4) {
                //far should not be in order list
                MultipleLocOrder order = new MultipleLocOrder();
                order.setId(String.valueOf(i));
                order.setDestination_name("museum");
                order.setStarting_name("passport office");
                order.setDestination_lat("30.9026801");
                order.setDestination_lng("75.8524855");
                order.setStarting_lat("30.8933037");
                order.setStarting_lng("75.8480884");
                order.setDistance_fromuser_loc(distance(Double.valueOf(mCurrentLocation.getLatitude()), Double.valueOf(mCurrentLocation.getLongitude()), Double.valueOf("30.8933037"), Double.valueOf("75.8480884")));
//                array_mutipleorderloc.add(order);
            }

        }
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
//                addData();
                updateLocationUI();
                startTrackerService();
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

    private String checkNearestPoint(ArrayList<KitchenLoc> arrayKitchenLoc) {
        double mindist = 0;
        int pos = 0;
        for (int i = 0; i < arrayKitchenLoc.size(); i++) {
            //get nearest pickup order
            if (mindist == 0) {
                pos = i;
                mindist = arrayKitchenLoc.get(i).getDistance_fromuser_loc();
            } else if (mindist > arrayKitchenLoc.get(i).getDistance_fromuser_loc()) {
                mindist = arrayKitchenLoc.get(i).getDistance_fromuser_loc();
                pos = i;
            }
        }
        //you will get nearest short distanced marker //change marker color
        Log.e("nearest", arrayKitchenLoc.get(pos).getName());
        return arrayKitchenLoc.get(pos).getName();

    }

    private void updateLocationOnServer(Double lat, Double lng) {
        Call<JsonElement> calls = apiInterface.DriverLocationUpdate(lat, lng);
        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                try {
                    if (response.code() == 200) {
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONObject jsonObject = object.getJSONObject("data");

                        } else {
                            JSONObject obj = object.getJSONObject("error");
//                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
                        }
                    } else {
//                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                call.cancel();
            }
        });
    }

    //TODO API HITS
    private void getPendingOrder() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetDriverPendingOrder();
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
                            arrayPendingOrders = new ArrayList<>();
                            if (!object.getString("unread_notifications").equalsIgnoreCase("0")) {
                                DriverNavDrawerActivity.text_notification_count
                                        .setText(object.getString("unread_notifications"));
                            } else {
                                DriverNavDrawerActivity.text_notification_count.setVisibility(View.GONE);
                            }
                            JSONArray array = object.getJSONArray("data");
                            for (int i = 0; i < array.length(); i++) {
                                DriverNavDrawerActivity.heading.setText(getActivity().getResources().getString(R.string.pending_orders));
                                linear_order_type.setVisibility(View.GONE);
                                text_all_orders.setVisibility(View.VISIBLE);
                                hasPendingOrder = true;
                                JSONObject jobj = array.getJSONObject(i);
                                MultipleLocOrder order = new MultipleLocOrder();
                                order.setOrder_id(jobj.getString("order_id"));
                                order.setOrder_currency(jobj.getString("currency_code"));
                                order.setOrder_total(jobj.getString("total"));
                                order.setOrder_paymethod(jobj.getString("payment_method"));
                                order.setOrder_items(jobj.getString("total_items"));
                                order.setOrder_delivery_time(jobj.getString("time"));
                                order.setOrder_status(jobj.getString("order_status"));

                                JSONObject uobj = jobj.getJSONObject("user_info");
                                order.setUser_phonenumber(uobj.getString("phone_number"));
                                order.setUser_name(uobj.getString("name"));

                                JSONObject locobj = uobj.getJSONObject("delivery_address");
                                order.setOrder_delivery_address(locobj.getString("location") + ", " + locobj.getString("flat_no") + ", " +
                                        locobj.getString("floor_no") + ", " + locobj.getString("building_name") + ", near " + locobj.getString("landmark") + ", " + locobj.getString("city_name"));
                                order.setDestination_lng(locobj.getString("longitude"));
                                order.setDestination_lat(locobj.getString("latitude"));

                                JSONObject kobj = jobj.getJSONObject("kitchen_location");
                                order.setKitchen_name(kobj.getString("username"));
                                order.setKitchen_phonenumber(kobj.getString("phone_number"));
                                order.setKitchen_loc(kobj.getString("location"));
                                order.setStarting_lng(kobj.getString("longitude"));
                                order.setStarting_lat(kobj.getString("latitude"));

                                JSONObject dobj = jobj.getJSONObject("driver_earning");
                                order.setDriver_earning(dobj.getString("currency") + " " + dobj.getString("total"));

                                arrayPendingOrders.add(order);
                            }
                            setPendingOrderonMap(arrayPendingOrders);
//                            text_totalprice.setText(jsonObject.getString("currency_code") + " " + jsonObject.getString("total"));
//                            text_payment_method.setText(jsonObject.getString("payment_method"));
//                            JSONObject obj = jsonObject.getJSONObject("user_info");
//                            text_user_name.setText(obj.getString("name"));
//                            text_user_phone.setText(obj.getString("phone_number"));
//                            JSONObject obj_loc = obj.getJSONObject("delivery_address");
//                            text_loc.setText(obj_loc.getString("location") + ", \n" +
//                                    "Building Name " + obj_loc.getString("building_name") + ", " +
//                                    "Flat " + obj_loc.getString("flat_no") + ", " +
//                                    "Floor " + obj_loc.getString("floor_no") + ", " +
//
//     "Landmark " + obj_loc.getString("landmark"));

//                            //need to discuss
//                            text_delivery_time.setText("00.00");
//                            text_timer.setText("0.0");
//
//                            //TODO : CHECK IF STATUS PENDING THEN DRIVER HAVE TO PICKUP THE ORDER
//                            if (jsonObject.getString("order_status").equalsIgnoreCase("Pending")) {
//                                text_order_status.setText(getResources().getString(R.string.mark_pickedup));
//                            } else {
//                                //TODO HAVE TO DELIVER TO USER
//                                text_order_status.setText(getResources().getString(R.string.mark_out_deliervy));
//                            }

                            //update side bar
                            JSONObject obj_driver = object.getJSONObject("driver_data");
                            if (obj_driver.has("is_available")) {
                                if (obj_driver.getString("is_available").equalsIgnoreCase("1")) {
                                    DriverNavDrawerActivity.switch_button.setChecked(true);
                                    DriverNavDrawerActivity.linear_active_inactive.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green_filled));
                                } else {
                                    DriverNavDrawerActivity.linear_active_inactive.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_gray));
                                    DriverNavDrawerActivity.switch_button.setChecked(false);
                                }
                            }
                            DriverNavDrawerActivity.text_drivername.setText(CommonMethods.checkNull(obj_driver.getString("username")));
                            //image
                            if (obj_driver.getString("profile_pic").equalsIgnoreCase("null")
                                    || obj_driver.getString("profile_pic").equalsIgnoreCase("")) {
                                Picasso.get().load(R.drawable.icon).into(DriverNavDrawerActivity.profile_image);
                            } else {
                                Picasso.get().load(obj_driver.getString("profile_pic")).placeholder(R.drawable.icon).into(DriverNavDrawerActivity.profile_image);
                            }

                            JSONObject jsonSub = obj_driver.getJSONObject("subscription_data");
                            sessionManager.saveSubscriptionDetails(jsonSub.getString("status"), jsonSub.getString("subscription_status"),
                                    jsonSub.getString("subscription_start_format"), jsonSub.getString("subscription_end_format"));

                        } else {
                            JSONObject obj = object.getJSONObject("error");
//                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
                            //TODO if no order for delivery then show near by kitchens on map
                            if (CommonMethods.checkConnection()) {
                                linear_order_type.setVisibility(View.VISIBLE);
                                getNearbyKitchens();
                            } else {
                                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
                            }
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
            }
        });
    }

    private void setPendingOrderonMap(ArrayList<MultipleLocOrder> arrayPendingOrders) {
        mMap.clear();
        if (arrayPendingOrders.size() == 1) {
            text_all_orders.setText(arrayPendingOrders.size() + " " + getActivity().getResources().getString(R.string.p_one_order));
        } else {
            text_all_orders.setText(arrayPendingOrders.size() + " " + getActivity().getResources().getString(R.string.p_order));
        }
        for (int i = 0; i < arrayPendingOrders.size(); i++) {
            LatLng loc_user = new LatLng(Double.valueOf(arrayPendingOrders.get(i).getDestination_lat()),
                    Double.valueOf(arrayPendingOrders.get(i).getDestination_lng()));

            LatLng loc_supplier = new LatLng(Double.valueOf(arrayPendingOrders.get(i).getStarting_lat()),
                    Double.valueOf(arrayPendingOrders.get(i).getStarting_lng()));

            //TODO SHOWING USER'S LOC----------------------------------------------------------
            MarkerOptions marker = new MarkerOptions().position(loc_user).anchor(0.5f, 0.5f)
                    .title(arrayPendingOrders.get(i).getUser_name())
                    .snippet(getActivity().getResources().getString(R.string.delivery_time) + " " + arrayPendingOrders.get(i).getOrder_delivery_time()).
                            icon(BitmapDescriptorFactory.fromBitmap(marker_icon(R.drawable.username)));
            mMap.addMarker(marker);

            //TODO SHOWING SUPPLIER'S LOC--------------------------------------
            MarkerOptions marker_supplier = new MarkerOptions().position(loc_supplier).anchor(0.5f, 0.5f)
                    .title(arrayPendingOrders.get(i).getKitchen_name())
                    .snippet(getActivity().getResources().getString(R.string.delivery_time) + " " + arrayPendingOrders.get(i).getOrder_delivery_time()).
                            icon(BitmapDescriptorFactory.fromBitmap(marker_icon(R.drawable.home)));
            mMap.addMarker(marker_supplier);

            Marker marker1 = mMap.addMarker(marker);
            marker1.showInfoWindow();
            arrayPendingOrders.get(i).setMarker(marker1);

            //TODO SHOWING PATH BTW 2 MARKERS- supplier and user----------------------------------
            drawLineBtwStartEndPoint(loc_supplier, loc_user, "0");

            LatLng loc_driver = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

            //TODO SHOWING PATH BTW 2 MARKERS----------suplier and driver-------------------------
            drawLineBtwStartEndPoint(loc_supplier, loc_driver, "1");
        }

    }

    private void getNearbyKitchens() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetKitchenLocations(String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude()));
        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        arrayKitchenLoc = new ArrayList<>();
                        arrayStoreHomeLoc = new ArrayList<>();
                        DriverNavDrawerActivity.heading.setText(getActivity().getResources().getString(R.string.near_by_kitchen));
                        text_all_orders.setVisibility(View.GONE);
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONObject jobj = object.getJSONObject("data");
                            JSONArray jsonArray = jobj.getJSONArray("food_suppliers");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                KitchenLoc kitchenLoc = new KitchenLoc();
                                kitchenLoc.setStart_lat(String.valueOf(mCurrentLocation.getLatitude()));
                                kitchenLoc.setStart_lng(String.valueOf(mCurrentLocation.getLongitude()));
                                kitchenLoc.setDestination_lat(obj.getString("latitude"));
                                kitchenLoc.setDestination_lng(obj.getString("longitude"));
                                kitchenLoc.setName(obj.getString("name"));
                                kitchenLoc.setPending_orders(obj.getString("pending_orders"));
                                kitchenLoc.setDistance_fromuser_loc(
                                        distance(Double.valueOf(mCurrentLocation.getLatitude()),
                                                Double.valueOf(mCurrentLocation.getLongitude()),
                                                Double.valueOf(obj.getString("latitude")),
                                                Double.valueOf(obj.getString("longitude"))));
                                kitchenLoc.setPhone_number(obj.getString("phone_number"));
                                kitchenLoc.setCity(obj.getString("city_name"));
                                kitchenLoc.setFlat_no(obj.getString("flat_no"));
                                kitchenLoc.setFloor_no(obj.getString("floor_no"));
                                kitchenLoc.setLocation(obj.getString("location"));
                                kitchenLoc.setBuilding_no(obj.getString("building_no"));
                                kitchenLoc.setLandmark(obj.getString("landmark"));
                                kitchenLoc.setSupplier_type(obj.getString("supplier_type"));
                                if (obj.has("orders_data")) {
                                    kitchenLoc.setOrder_data(obj.getString("orders_data"));
                                }
                                arrayKitchenLoc.add(kitchenLoc);
                            }

                            JSONArray jsonproArray = jobj.getJSONArray("others_suppliers");
                            for (int i = 0; i < jsonproArray.length(); i++) {
                                JSONObject obj = jsonproArray.getJSONObject(i);
                                KitchenLoc storeLoc = new KitchenLoc();
                                storeLoc.setStart_lat(String.valueOf(mCurrentLocation.getLatitude()));
                                storeLoc.setStart_lng(String.valueOf(mCurrentLocation.getLongitude()));
                                storeLoc.setDestination_lat(obj.getString("latitude"));
                                storeLoc.setDestination_lng(obj.getString("longitude"));
                                storeLoc.setName(obj.getString("name"));
                                storeLoc.setPending_orders(obj.getString("pending_orders"));
                                storeLoc.setDistance_fromuser_loc(
                                        distance(Double.valueOf(mCurrentLocation.getLatitude()),
                                                Double.valueOf(mCurrentLocation.getLongitude()),
                                                Double.valueOf(obj.getString("latitude")),
                                                Double.valueOf(obj.getString("longitude"))));
                                storeLoc.setPhone_number(obj.getString("phone_number"));
                                storeLoc.setCity(obj.getString("city_name"));
                                storeLoc.setFlat_no(obj.getString("flat_no"));
                                storeLoc.setLocation(obj.getString("location"));
                                storeLoc.setFloor_no(obj.getString("floor_no"));
                                storeLoc.setBuilding_no(obj.getString("building_no"));
                                storeLoc.setLandmark(obj.getString("landmark"));
                                storeLoc.setSupplier_type(obj.getString("supplier_type"));
                                if (obj.has("orders_data")) {
                                    storeLoc.setOrder_data(obj.getString("orders_data"));
                                }
                                arrayStoreHomeLoc.add(storeLoc);
                            }

                            //set markers on map
                            isFoodSelected = true;
//                            String nearloc = checkNearestPoint(arrayKitchenLoc);
                            setMarkersonMapIntial(arrayKitchenLoc, "");

                            //driver details
                            JSONObject jsonObject = jobj.getJSONObject("driver_info");
                            sessionManager.saveDriverWallet(jsonObject.getString("wallet_amount"), jsonObject.getString("availability"));
                            //update side bar
                            DriverNavDrawerActivity.text_drivername.setText(sessionManager.getDriveDetails().get(UserSessionManager.KEY_USERNAME));
                            DriverNavDrawerActivity.linear_active_inactive.setVisibility(View.VISIBLE);
                            if (jsonObject.getString("availability").equalsIgnoreCase("online")) {
                                DriverNavDrawerActivity.linear_active_inactive.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green_filled));
                            } else {
                                DriverNavDrawerActivity.linear_active_inactive.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_gray));
                            }

                            //image
                            if (sessionManager.getDriveDetails().get(UserSessionManager.KEY_profile_image).equalsIgnoreCase("null")
                                    || sessionManager.getDriveDetails().get(UserSessionManager.KEY_profile_image).equalsIgnoreCase("")) {
                                Picasso.get().load(R.drawable.icon).into(DriverNavDrawerActivity.profile_image);
                            } else {
                                Picasso.get().load(sessionManager.getDriveDetails().get(UserSessionManager.KEY_profile_image)).placeholder(R.drawable.icon).into(DriverNavDrawerActivity.profile_image);
                            }

                            JSONObject jsonSub = jsonObject.getJSONObject("subscription_status");
                            sessionManager.saveSubscriptionDetails(jsonSub.getString("status"), jsonSub.getString("subscription_status"),
                                    jsonSub.getString("subscription_start_format"), jsonSub.getString("subscription_end_format"));


                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
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

    private void setMarkersonMapIntial(ArrayList<KitchenLoc> arrayKitchenLoc, String nearloc) {
        arraySelectedType = new ArrayList<>();
        mMap.clear();
        for (int i = 0; i < arrayKitchenLoc.size(); i++) {
            LatLng loc = new LatLng(Double.valueOf(arrayKitchenLoc.get(i).getDestination_lat()),
                    Double.valueOf(arrayKitchenLoc.get(i).getDestination_lng()));
            MarkerOptions marker = null;
//            if (arrayKitchenLoc.get(i).getName().equalsIgnoreCase(nearloc)) {
//                marker = new MarkerOptions().position(loc).anchor(0.5f, 0.5f)
//                        .title(arrayKitchenLoc.get(i).getName()).
//                                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//            } else {
            marker = new MarkerOptions().position(loc).anchor(0.5f, 0.5f)
                    .title(arrayKitchenLoc.get(i).getName()).
                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//            }

            Marker marker1 = mMap.addMarker(marker);
            arrayMarkers.add(marker1);
            arrayKitchenLoc.get(i).setMarker(marker1);
            arraySelectedType.add(arrayKitchenLoc.get(i));
            Log.e("order_data", arraySelectedType.get(i).getOrder_data());
        }
    }

//    private void setMarkersonMap(String nearestpoint) {
//        Marker mClosestMarker;
//        double mindist = 0;
//        int pos = 0;
//        for (int i = 0; i < array_mutipleorderloc.size(); i++) {
//            LatLng sydney = new LatLng(Double.valueOf(array_mutipleorderloc.get(i).getStarting_lat()),
//                    Double.valueOf(array_mutipleorderloc.get(i).getStarting_lng()));
//            // Add a marker in Sydney and move the camera
//            MarkerOptions marker = null;
//            if (array_mutipleorderloc.get(i).getStarting_name().equalsIgnoreCase(nearestpoint)) {
//                marker = new MarkerOptions().position(sydney).anchor(0.5f, 0.5f)
//                        .title(array_mutipleorderloc.get(i).getStarting_name()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//            } else {
//                marker = new MarkerOptions().position(sydney).anchor(0.5f, 0.5f)
//                        .title(array_mutipleorderloc.get(i).getStarting_name()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//            }
//            Marker marker1 = mMap.addMarker(marker);
//            arrayMarkers.add(marker1);
//            array_mutipleorderloc.get(i).setMarker(marker1);
//        }
//
//
//    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (hasPendingOrder) {
            for (int i = 0; i < arrayPendingOrders.size(); i++) {
                if (arrayPendingOrders.get(i).getMarker() != null) {
//                    if (arrayPendingOrders.get(i).getMarker().getTitle().equals(marker.getTitle())) {
                        //show bottom details with pickup and delivery btn
                        arrayPendingOrders.get(i).getMarker().showInfoWindow();
                        showPendingOrderDialog(arrayPendingOrders.get(i));
                        Log.e("markerdata", arrayPendingOrders.get(i).getOrder_status() + "-----" + arrayPendingOrders.get(i).getOrder_id());
//                    }
                }
            }
        } else {
            for (int i = 0; i < arraySelectedType.size(); i++) {
                if (arraySelectedType.get(i).getMarker() != null) {
                    if (arraySelectedType.get(i).getMarker().getTitle().equals(marker.getTitle())) {
                        Log.e("marker_click", arraySelectedType.get(i).getMarker().getTitle());
//                    LatLng start = new LatLng(Double.valueOf(arraySelectedType.get(i).getLatitude()),
//                            Double.valueOf(arraySelectedType.get(i).getStarting_lng()));
//                    LatLng end = new LatLng(Double.valueOf(arraySelectedType.get(i).getLongitude()),
//                            Double.valueOf(arraySelectedType.get(i).getDestination_lng()));
//                    drawLineBtwStartEndPoint(start, end);
                        //if isfoodselected then make the path and show dialog
//                        if (isFoodSelected) {
                        showKitchenDetails_Dialog(arraySelectedType.get(i).getName(), arraySelectedType.get(i).getPhone_number(),
                                arraySelectedType.get(i).getLocation() + ", " + arraySelectedType.get(i).getFlat_no() + ", " +
                                        arraySelectedType.get(i).getFloor_no() + ", " + arraySelectedType.get(i).getBuilding_no() +
                                        ", near " + arraySelectedType.get(i).getLandmark() + ", " + arraySelectedType.get(i).getCity(), arraySelectedType.get(i).getSupplier_type(),
                                arraySelectedType.get(i).getOrder_data(), arraySelectedType.get(i).getDestination_lat(),
                                arraySelectedType.get(i).getDestination_lng(), arraySelectedType.get(i).getPending_orders());
//                        } else {
                        //show
//                        }


                    }
                }
            }
        }
        return true;
    }

    private void showPendingOrderDialog(final MultipleLocOrder multipleLocOrder) {
        View view = getLayoutInflater().inflate(R.layout.layout_driver_pending_order, null);
        bottomdialog = new BottomSheetDialog(getActivity());
        bottomdialog.setContentView(view);
        bottomdialog.setCanceledOnTouchOutside(false);
        bottomdialog.show();

        TextView text_cancel = (TextView) bottomdialog.findViewById(R.id.text_cancel);
        text_cancel.setOnClickListener(this);
        TextView text_btn = (TextView) bottomdialog.findViewById(R.id.text_btn);
        LinearLayout linear_pickup = (LinearLayout) bottomdialog.findViewById(R.id.linear_pickup);
        linear_pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hit api and
                if (multipleLocOrder.getOrder_status().equalsIgnoreCase("Out for delivery")) {
                    //go to another screen
                    Intent i = new Intent(getActivity(), DeliverOrder.class);
                    startActivityForResult(i, REQUEST_ORDER_DELIVER);
                } else {
                    if (CommonMethods.checkConnection()) {
                        submitPickupOrderReq(multipleLocOrder.getOrder_id(), "10");
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
                    }
                }

            }
        });
        TextView text_your_earning = (TextView) bottomdialog.findViewById(R.id.text_your_earning);
        text_your_earning.setText(multipleLocOrder.getDriver_earning());
        TextView text_detail_type = (TextView) bottomdialog.findViewById(R.id.text_detail_type);
        TextView text_user_name = (TextView) bottomdialog.findViewById(R.id.text_user_name);
        TextView text_loc = (TextView) bottomdialog.findViewById(R.id.text_loc);
        TextView text_user_phone = (TextView) bottomdialog.findViewById(R.id.text_user_phone);
        TextView text_totalprice = (TextView) bottomdialog.findViewById(R.id.text_totalprice);
        TextView text_payment_method = (TextView) bottomdialog.findViewById(R.id.text_payment_method);
        TextView text_delivery_time = (TextView) bottomdialog.findViewById(R.id.text_delivery_time);
        TextView text_view_order_detail = (TextView) bottomdialog.findViewById(R.id.text_view_order_detail);
        text_view_order_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), DriverOrderDetailActivity.class);
                i.putExtra("ispending", true);
                i.putExtra("order_id", multipleLocOrder.getOrder_id());
                i.putExtra("fromDriver", true);
                startActivity(i);
            }
        });

        LinearLayout linear_price = (LinearLayout) bottomdialog.findViewById(R.id.linear_price);
        LinearLayout linear_payment = (LinearLayout) bottomdialog.findViewById(R.id.linear_payment);

        //if the order status is processsing else show delivery btn
        if (multipleLocOrder.getOrder_status().equalsIgnoreCase("Out for delivery")) {
            text_detail_type.setText(getActivity().getResources().getString(R.string.deliveryaddress));
            text_delivery_time.setText(CommonMethods.checkNull(multipleLocOrder.getOrder_delivery_time()));
            text_loc.setText(multipleLocOrder.getOrder_delivery_address());
            text_user_name.setText(multipleLocOrder.getUser_name());
            text_user_phone.setText(multipleLocOrder.getUser_phonenumber());
            linear_price.setVisibility(View.VISIBLE);
            linear_payment.setVisibility(View.VISIBLE);
            text_btn.setText(getActivity().getResources().getString(R.string.mark_out_deliervy));
        } else {
            text_detail_type.setText(getActivity().getResources().getString(R.string.kitchen_address));
            text_delivery_time.setText(CommonMethods.checkNull(multipleLocOrder.getOrder_delivery_time()));
            text_loc.setText(multipleLocOrder.getKitchen_loc());
            text_user_name.setText(multipleLocOrder.getKitchen_name());
            text_user_phone.setText(multipleLocOrder.getKitchen_phonenumber());
            linear_price.setVisibility(View.GONE);
            linear_payment.setVisibility(View.GONE);
            text_btn.setText(getActivity().getResources().getString(R.string.mark_pickedup));
        }
    }

    private void showKitchenDetails_Dialog(final String name, final String phone_number,
                                           final String loc, String supplier_type,
                                           final String order_data, final String destination_lat,
                                           final String destination_lng, String pending_orders) {
        View view = getLayoutInflater().inflate(R.layout.layout_kitchen_detail, null);
        bottomdialog = new BottomSheetDialog(getActivity());
        bottomdialog.setContentView(view);
        bottomdialog.setCanceledOnTouchOutside(false);
        TextView text_cancel = (TextView) bottomdialog.findViewById(R.id.text_cancel);
        text_cancel.setOnClickListener(this);
        ImageView img_type = (ImageView) bottomdialog.findViewById(R.id.img_type);
        if (supplier_type.equalsIgnoreCase(ConstantValues.home_food)) {
            img_type.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.home_food));
        } else {
            img_type.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.home_product));
        }
        TextView text_name = (TextView) bottomdialog.findViewById(R.id.text_name);
        text_name.setText(name);
        TextView text_loc = (TextView) bottomdialog.findViewById(R.id.text_loc);
        text_loc.setText(loc);
        TextView text_phone = (TextView) bottomdialog.findViewById(R.id.text_phone);
        text_phone.setText(phone_number);
        text_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtilFunctions.call(getActivity(), phone_number);
            }
        });
        LinearLayout linear_all_orders = (LinearLayout) bottomdialog.findViewById(R.id.linear_all_orders);
        linear_all_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AllOrderListActivity.class);
                i.putExtra("order_data", order_data);
                i.putExtra("kit_lat", destination_lat);
                i.putExtra("kit_lng", destination_lng);
                i.putExtra("kit_name", name);
                i.putExtra("kit_loc", loc);
                i.putExtra("isFood", isFoodSelected);
                startActivityForResult(i, REQUEST_ORDER_ACCEPT);
                if (bottomdialog.isShowing()) {
                    bottomdialog.dismiss();
                }
            }
        });

        if (pending_orders.equalsIgnoreCase("0")) {
            linear_all_orders.setVisibility(View.GONE);
        } else {
            linear_all_orders.setVisibility(View.VISIBLE);
        }
        bottomdialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_food:
                isFoodSelected = true;
//                String nearloc = checkNearestPoint(arrayKitchenLoc);
                setMarkersonMapIntial(arrayKitchenLoc, "");
                text_food.setBackgroundColor(getActivity().getResources().getColor(R.color.app_color_blue));
                text_products.setBackgroundColor(getActivity().getResources().getColor(R.color.light_gray));
                break;

            case R.id.text_products:
                isFoodSelected = false;
//                String nearloc_i = checkNearestPoint(arrayStoreHomeLoc);
                setMarkersonMapIntial(arrayStoreHomeLoc, "");
                text_food.setBackgroundColor(getActivity().getResources().getColor(R.color.light_gray));
                text_products.setBackgroundColor(getActivity().getResources().getColor(R.color.app_color_blue));

                break;


            case R.id.text_cancel:
                if (bottomdialog.isShowing()) {
                    bottomdialog.dismiss();
                }
                break;

            case R.id.text_all_orders:
                isFoodSelected = false;
                break;

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMarkerClickListener(this);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        loginToFirebase();
        updateLocationUI();


    }

    @Override
    public void onResume() {
        super.onResume();
        requestpermission(false);
        startLocationUpdates();
        updateLocationUI();
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

    private void startTrackerService() {
//        getActivity().startService(new Intent(getActivity(), TrackerService.class));
//        getActivity().finish();
    }

    private void loginToFirebase() {
        String email = getString(R.string.firebase_email);
        String password = getString(R.string.firebase_password);
        // Authenticate with Firebase and subscribe to updates
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    updateLocationUI();
                    Log.d(TAG, "firebase auth success");
                } else {
                    Log.d(TAG, "firebase auth failed");
                }
            }
        });
    }

    private void updateLocationUI() {
        if (mMap != null) {
            if (mCurrentLocation != null) {
                LatLng loc = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                sessionManager.saveCountryName(CommonMethods.getCountryName(getActivity(), mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
                if (isfirstTime) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(loc, 12);
                    mMap.animateCamera(cameraUpdate);
                    MarkerOptions marker = new MarkerOptions().position(loc).anchor(0.5f, 0.5f)
                            .title("You").
                                    icon(BitmapDescriptorFactory.fromBitmap(marker_icon(R.drawable.delivery_bike)));
                    mMap.addMarker(marker);
                    isfirstTime = false;
                    if (CommonMethods.checkConnection()) {
                        getPendingOrder();
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
                    }
                }

                //TODO SAVE DRIVER LOCAION IN FIREBASE DB FOR REAL TIME TRAKCING------------------------------------------------------------------------------------
                if (sessionManager != null) {
//                    String path = getActivity().getString(R.string.firebase_path) + "/" + sessionManager.getUserDetails().get(UserSessionManager.KEY_USERNAME);
                    String path = "locations/" + sessionManager.getDriveDetails().get(UserSessionManager.KEY_USERNAME);
                    ref = FirebaseDatabase.getInstance().getReference(path);
                    if (mCurrentLocation != null) {
//                        Log.e(" map_db", "location update " + mCurrentLocation);
                        track_driver driver = new track_driver();
                        driver.setDriver_name(sessionManager.getUserDetails().get(UserSessionManager.KEY_USERNAME));
                        driver.setDriver_lat(mCurrentLocation.getLatitude());
                        driver.setDriver_lng(mCurrentLocation.getLongitude());
                        ref.setValue(driver);
//                    }
                    }
                }


            }
        }
//        if (mCurrentLocation != null) {
//
//            if (CommonMethods.checkConnection()) {
//                updateLocationOnServer(mCurrentLocation.getLatitude(),
//                        mCurrentLocation.getLongitude());
//            } else {
//                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
//            }
//
//        }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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

    private void drawLineBtwStartEndPoint(LatLng start, LatLng end, String type) {
//        new GetRoute(start,end).execute();

        // Getting URL to the Google Directions API
        LatLng end_pos = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        String url = getDirectionsUrl(start, end);

        DownloadTask downloadTask = new DownloadTask(type);
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);

    }

    private class DownloadTask extends AsyncTask<String, String, String> {
        String type;

        public DownloadTask(String type) {
            this.type = type;
        }

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = gMapRoutes.downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask(type);
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        String type;

        public ParserTask(String type) {
            this.type = type;
        }

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                if (type.equalsIgnoreCase("0")) {
                    //supplier
                    lineOptions.color(Color.rgb(91, 216, 16));
                } else {
                    //user
                    lineOptions.color(Color.rgb(41, 98, 7));
                }
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            if(lineOptions!=null) {
                mMap.addPolyline(lineOptions);
            }
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";
        String key = "key=" + getResources().getString(R.string.api_key);
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&" + key;


        return url;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        // haversine great circle distance approximation, returns meters
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60; // 60 nautical miles per degree of seperation
        dist = dist * 1852; // 1852 meters per nautical mile
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public Bitmap resizeMapIcons(int width, int height) {
        Drawable d = getActivity().getResources().getDrawable(R.drawable.delivery_bike);
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return resizedBitmap;
    }

    private void submitPickupOrderReq(String order_id, String status_id) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.PickuptheOrder(order_id, status_id);
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
                            success_Alert_Dialog(getActivity(), object.getString("success"));
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
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
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ORDER_DELIVER) {
            if (data != null)
                if (data.hasExtra("deliver")) {
                    refreshScreen();
                }
        } else if (requestCode == REQUEST_ORDER_ACCEPT) {
            if (data != null)
                if (data.hasExtra("accept")) {
                    refreshScreen();
                }
        }
    }

    private void refreshScreen() {
        if (bottomdialog.isShowing()) {
            bottomdialog.dismiss();
        }
        if (CommonMethods.checkConnection()) {
            getPendingOrder();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationCallback != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    public void success_Alert_Dialog(final Context context, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();

        // Setting Dialog Title
        alertDialog.setTitle("Success!");

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting Icon to Dialog
//        alertDialog.setIcon(R.drawable.call);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                alertDialog.dismiss();
                refreshScreen();
                if (bottomdialog.isShowing()) {
                    bottomdialog.dismiss();
                }

            }
        });

        // Showing Alert Message

        alertDialog.show();
    }

    private Bitmap marker_icon(int username) {
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(username);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 60, 60, false);
        return smallMarker;
    }
}
