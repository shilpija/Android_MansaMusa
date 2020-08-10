package com.freshhome.SalesModule;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.freshhome.AdapterClass.RecyclerCartAdapter;
import com.freshhome.AdapterClass.RecyclerSalesMyRequestAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.R;
import com.freshhome.datamodel.SalesRequest;
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
public class Fragment_Sales_Myrequest extends Fragment implements View.OnClickListener {
    LinearLayout linear_food, linear_homemade, linear_shop, linear_shop_line,
            linear_homemade_line, linear_food_line, linear_badge_food, linear_badge_home, linear_badge_shop;
    public LinearLayout linear_shadow;
    TextView text_food, text_homemade, text_shop, text_badge_food, text_badge_home, text_badge_shop;
    RecyclerView recyclerMyRequestList;
    ArrayList<SalesRequest> array_MyRequestFood, array_MyRequestHomeMade, array_MyRequestShop;
    ApiInterface apiInterface;
    UserSessionManager sessionManager;
    int isClicked = 0;//0 food //1 home made //2 shop

    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    int PERMISSION_ALL = 1;
    // location last updated time
    private String mLastUpdateTime;
    // bunch of location related apis
    private String cmFrom="";
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
    public Fragment_Sales_Myrequest() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sales_myrequest, container, false);
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            cmFrom = bundle.getString("message");
        }
        sessionManager = new UserSessionManager(getActivity());
        ActivitySalesNavDrawer.heading.setText(getResources().getString(R.string.myrequest));
        array_MyRequestFood = new ArrayList<>();
        array_MyRequestHomeMade = new ArrayList<>();
        array_MyRequestShop = new ArrayList<>();

        apiInterface = ApiClient.getInstance().getClient();
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

        linear_badge_food = (LinearLayout) v.findViewById(R.id.linear_badge_food);
        linear_badge_food.setVisibility(View.GONE);
        linear_badge_home = (LinearLayout) v.findViewById(R.id.linear_badge_home);
        linear_badge_home.setVisibility(View.GONE);
        linear_badge_shop = (LinearLayout) v.findViewById(R.id.linear_badge_shop);
        linear_badge_shop.setVisibility(View.GONE);

        text_badge_food = (TextView) v.findViewById(R.id.text_badge_food);
        text_badge_home = (TextView) v.findViewById(R.id.text_badge_home);
        text_badge_shop = (TextView) v.findViewById(R.id.text_badge_shop);

        text_shop = (TextView) v.findViewById(R.id.text_shop);
        text_food = (TextView) v.findViewById(R.id.text_food);
        text_homemade = (TextView) v.findViewById(R.id.text_homemade);

        recyclerMyRequestList = (RecyclerView) v.findViewById(R.id.recyclerMyRequestList);
        requestpermission(false);
        initLocation();
        startLocationUpdates();
        return v;
    }


    private void setUpList(ArrayList<SalesRequest> array_MyRequest, TextView textViewbadge, LinearLayout linearLayoutBadge) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerMyRequestList.setLayoutManager(mLayoutManager);
        recyclerMyRequestList.setItemAnimator(new DefaultItemAnimator());
        RecyclerSalesMyRequestAdapter adapter = new RecyclerSalesMyRequestAdapter(getActivity(), array_MyRequest, Fragment_Sales_Myrequest.this, textViewbadge, linearLayoutBadge);
        recyclerMyRequestList.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_food:
                isClicked = 0;
                clickActive(text_food, linear_food_line, array_MyRequestFood, text_badge_food, linear_badge_food);
                clickInactive(text_homemade, linear_homemade_line);
                clickInactive(text_shop, linear_shop_line);
                break;

            case R.id.linear_homemade:
                isClicked = 1;
                clickInactive(text_food, linear_food_line);
                clickActive(text_homemade, linear_homemade_line, array_MyRequestHomeMade, text_badge_home, linear_badge_home);
                clickInactive(text_shop, linear_shop_line);
                break;

            case R.id.linear_shop:
                isClicked = 2;
                clickInactive(text_food, linear_food_line);
                clickInactive(text_homemade, linear_homemade_line);
                clickActive(text_shop, linear_shop_line, array_MyRequestShop, text_badge_shop, linear_badge_shop);
                break;
        }
    }

    private void clickActive(TextView textView, LinearLayout linearLayout_line, ArrayList<SalesRequest> array_MyRequest, TextView textViewBadge, LinearLayout linearLayoutBadge) {
        textView.setTextColor(getResources().getColor(R.color.app_color_blue));
        linearLayout_line.setBackgroundColor(getResources().getColor(R.color.app_color_blue));
        setUpList(array_MyRequest, textViewBadge, linearLayoutBadge);
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
        Call<JsonElement> calls;
        if(cmFrom.equalsIgnoreCase("myCustomer")) {
            calls = apiInterface.GetSaleMyCustomer();
        }else {
            calls = apiInterface.GetSaleRequest();
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
                        array_MyRequestFood = new ArrayList<>();
                        array_MyRequestHomeMade = new ArrayList<>();
                        array_MyRequestShop = new ArrayList<>();
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            sessionManager.update_isWatched(object.getString("is_watched"));
                            //update side bar
                            JSONObject jObject = object.getJSONObject("sales_person");
                            ActivitySalesNavDrawer.text_username.setText(jObject.getString("username"));
                            if (!jObject.getString("profile_pic").equalsIgnoreCase("")) {
                                Picasso.get().load(jObject.getString("profile_pic")).placeholder(R.drawable.icon).into(ActivitySalesNavDrawer.profile_image);
                            }

                            JSONObject jsonObject = object.getJSONObject("data");
                            //food
//                            JSONObject object_ffd = jsonObject.getJSONObject("ffd");
//                            if (!object_ffd.getString("count").equalsIgnoreCase("0")) {
//                                linear_badge_food.setVisibility(View.VISIBLE);
//                                text_badge_food.setText(object_ffd.getString("count"));
//                            }else{
//                                linear_badge_food.setVisibility(View.GONE);
//                            }
                            JSONArray jsonArray_ffd = jsonObject.getJSONArray("pending");
                            for (int i = 0; i < jsonArray_ffd.length(); i++) {
                                JSONObject obj = jsonArray_ffd.getJSONObject(i);
                               // JSONObject obj_info = obj.getJSONObject("information");
                                SalesRequest req = new SalesRequest();

                                req.setNotification_id(obj.getString("notification_id"));
                                req.setRequest_status(obj.getString("status"));
                                req.setTime(obj.getString("time"));
                                //req.setIsread(obj.getString("is_read"));
                                //req.setId(obj_info.getString("request_id"));
                                req.setMsg(obj.getString("message"));
                                req.setRequest_type((obj.getString("request_type")));
                                req.setSupplier_loc((obj.getString("location")));
                                req.setSupplier_phonenumber((obj.getString("phonenumber")));
                                req.setSupplier_lat((obj.getString("lat")));
                                req.setSupplier_lng((obj.getString("lng")));
                                array_MyRequestFood.add(req);
                            }

                            //home made
//                            JSONObject object_fhp = jsonObject.getJSONObject("fhp");
//                            if (!object_fhp.getString("count").equalsIgnoreCase("0")) {
//                                linear_badge_home.setVisibility(View.VISIBLE);
//                                text_badge_home.setText(object_fhp.getString("count"));
//                            }else{
//                                linear_badge_home.setVisibility(View.GONE);
//                            }
                            JSONArray jsonArray_fhp = jsonObject.getJSONArray("new");
                            for (int i = 0; i < jsonArray_fhp.length(); i++) {
                                JSONObject obj = jsonArray_fhp.getJSONObject(i);
                                //JSONObject obj_info = obj.getJSONObject("information");
                                SalesRequest req = new SalesRequest();
                                //req.setId(obj.getString("request_id"));
                                req.setMsg(obj.getString("message"));
                                req.setNotification_id(obj.getString("notification_id"));
                                req.setRequest_status(obj.getString("status"));
                                req.setTime(obj.getString("time"));
                                //req.setIsread(obj.getString("is_read"));

                                req.setRequest_type((obj.getString("request_type")));
                                req.setSupplier_loc((obj.getString("location")));
                                req.setSupplier_phonenumber((obj.getString("phonenumber")));
                                req.setSupplier_lat((obj.getString("lat")));
                                req.setSupplier_lng((obj.getString("lng")));
                                array_MyRequestHomeMade.add(req);
                            }


                            //shop
//                            JSONObject object_fs = jsonObject.getJSONObject("fs");
//                            if (!object_fs.getString("count").equalsIgnoreCase("0")) {
//                                linear_badge_shop.setVisibility(View.VISIBLE);
//                                text_badge_shop.setText(object_fs.getString("count"));
//                            }else{
//                                linear_badge_shop.setVisibility(View.GONE);
//                            }
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
                            if (isClicked == 0) {
                                setUpList(array_MyRequestFood, text_badge_food, linear_badge_food);
                            } else if (isClicked == 1) {
                                setUpList(array_MyRequestHomeMade, text_badge_home, linear_badge_home);
                            } else if (isClicked == 2) {
                                setUpList(array_MyRequestShop, text_badge_shop, linear_badge_shop);
                            }

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

    @Override
    public void onResume() {
        super.onResume();
        if(cmFrom.equalsIgnoreCase("myCustomer")) {
            ActivitySalesNavDrawer.heading.setText(getResources().getString(R.string.mycustomer));
        }else {
            ActivitySalesNavDrawer.heading.setText(getResources().getString(R.string.myrequest));
        }

        if (CommonMethods.checkConnection()) {
            getdata();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
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
            if(mLocationCallback!=null) {
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
        if(mLocationCallback!=null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }
}
