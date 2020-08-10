package com.freshhome.SalesModule;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.GetLocation;
import com.freshhome.R;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Sales_RequestDetail extends AppCompatActivity implements View.OnClickListener {
    GoogleMap mMap;
    MapView mMapView;
    ImageView image_back, image_navigation;
    LinearLayout linear_Accept, linear_reject, linear_phone, linear_accept_reject;
    TextView text_msg, text_supplierphone, text_supplierloc, text_status, text_want_cancel;
    double latitute = 0.0, longitute = 0.0;
    String loc = "", reqStatus = "", notification_id = "", request_id = "", reason_id = "";
    ApiInterface apiInterface;
    BottomSheetDialog dialog;
    ArrayList<NameID> cancelReasonList;
    String[] spinner_reason_Array;
    EditText edit_msg;
    Spinner stype_spinner;
    LatLng destination_loc;
    // location last updated time
    private String mLastUpdateTime;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_request_detail);
        initLocation();
        cancelReasonList = new ArrayList<>();
        apiInterface = ApiClient.getInstance().getClient();
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);
        image_navigation = (ImageView) findViewById(R.id.image_navigation);
        image_navigation.setOnClickListener(this);
        linear_phone = (LinearLayout) findViewById(R.id.linear_phone);
        linear_phone.setVisibility(View.GONE);
        linear_reject = (LinearLayout) findViewById(R.id.linear_reject);
        linear_reject.setOnClickListener(this);
        linear_Accept = (LinearLayout) findViewById(R.id.linear_Accept);
        linear_Accept.setOnClickListener(this);
        linear_accept_reject = (LinearLayout) findViewById(R.id.linear_accept_reject);

        text_want_cancel = (TextView) findViewById(R.id.text_want_cancel);
        text_want_cancel.setVisibility(View.GONE);
        text_want_cancel.setOnClickListener(this);
        text_status = (TextView) findViewById(R.id.text_status);
        text_msg = (TextView) findViewById(R.id.text_msg);
        text_msg.setText(getIntent().getStringExtra("msg"));
        text_supplierphone = (TextView) findViewById(R.id.text_supplierphone);
        text_supplierphone.setOnClickListener(this);
        text_supplierphone.setText(getIntent().getStringExtra("phone"));
        text_supplierloc = (TextView) findViewById(R.id.text_supplierloc);
        text_supplierloc.setText(getIntent().getStringExtra("loc"));
        latitute = Double.parseDouble(getIntent().getStringExtra("lat"));
        longitute = Double.parseDouble(getIntent().getStringExtra("lng"));
        loc = getIntent().getStringExtra("loc");
        reqStatus = getIntent().getStringExtra("reqstatus");
        notification_id = getIntent().getStringExtra("id");
        request_id = getIntent().getStringExtra("req_id");
        CheckStatusHideShowViews();

        mMapView = (MapView) findViewById(R.id.mapView);
        MapsInitializer.initialize(Activity_Sales_RequestDetail.this);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();// needed to get the map to display immediately

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                mMap = googleMap;
                destination_loc = new LatLng(latitute, longitute);
                //your lat lng
                googleMap.addMarker(new MarkerOptions().position(destination_loc).title(loc));
                googleMap.getUiSettings().setMapToolbarEnabled(false);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(destination_loc));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }
        });

        if (CommonMethods.checkConnection()) {
            ReasonForCancel();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_RequestDetail.this, getResources().getString(R.string.internetconnection));
        }

    }


    private void CheckStatusHideShowViews() {
        //show accept/reject text as per request status
        if (reqStatus.equalsIgnoreCase("accepted")) {
            text_status.setText("Accepted");
            linear_phone.setVisibility(View.VISIBLE);
            text_want_cancel.setVisibility(View.VISIBLE);
            linear_accept_reject.setVisibility(View.GONE);
        } else if (reqStatus.equalsIgnoreCase("rejected")) {
            linear_phone.setVisibility(View.GONE);
            text_status.setText("Rejected");
            text_want_cancel.setVisibility(View.GONE);
            linear_accept_reject.setVisibility(View.GONE);
        } else if (reqStatus.equalsIgnoreCase("completed")) {
            text_status.setText("Completed");
            linear_phone.setVisibility(View.VISIBLE);
            text_want_cancel.setVisibility(View.GONE);
            linear_accept_reject.setVisibility(View.GONE);
        } else {
            text_status.setText("Pending");
            linear_phone.setVisibility(View.GONE);
            linear_accept_reject.setVisibility(View.VISIBLE);
            text_want_cancel.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                Activity_Sales_RequestDetail.this.finish();
                break;

            case R.id.linear_Accept:
                if (CommonMethods.checkConnection()) {
                    AcceptRejectRequest(notification_id, true);
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_RequestDetail.this, getResources().getString(R.string.internetconnection));
                }
                break;

            case R.id.linear_reject:
                if (CommonMethods.checkConnection()) {
                    AcceptRejectRequest(notification_id, false);
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_RequestDetail.this, getResources().getString(R.string.internetconnection));
                }
                break;

            case R.id.text_want_cancel:
                ShowCancelRequestDialog();
                break;

            case R.id.linear_send:
                if (CommonMethods.checkConnection()) {
                    if (edit_msg != null) {
                        if (reason_id.equalsIgnoreCase("")) {
                            CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_RequestDetail.this, getResources().getString(R.string.select_reason));
                        } else if (edit_msg.getText().toString().equalsIgnoreCase("")) {
                            CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_RequestDetail.this, getResources().getString(R.string.enter_msg));
                        } else {
                            CancelRequest(request_id, reason_id, edit_msg.getText().toString().trim());
                        }
                    }
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_RequestDetail.this, getResources().getString(R.string.internetconnection));
                }
                break;

            case R.id.text_cancel:
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                break;

            case R.id.image_navigation:
                if (mCurrentLocation != null) {
                    GoToGoogleMapForNavigation();
                } else {
                    initLocation();
                }
                break;

            case R.id.text_supplierphone:
                CommonUtilFunctions.call(Activity_Sales_RequestDetail.this,text_supplierphone.getText().toString());
                break;
        }
    }

    //dialog
    private void ShowCancelRequestDialog() {
        View view = getLayoutInflater().inflate(R.layout.layout_cancel_request_dialog, null);
        dialog = new BottomSheetDialog(Activity_Sales_RequestDetail.this);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        edit_msg = (EditText) dialog.findViewById(R.id.edit_msg);
        LinearLayout linear_send = (LinearLayout) dialog.findViewById(R.id.linear_send);
        linear_send.setOnClickListener(this);
        TextView text_cancel = (TextView) dialog.findViewById(R.id.text_cancel);
        text_cancel.setOnClickListener(this);
        stype_spinner = (Spinner) dialog.findViewById(R.id.stype_spinner);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(Activity_Sales_RequestDetail.this,
                R.layout.layout_spinner_text_bg_transparent, spinner_reason_Array);
        stype_spinner.setAdapter(genderAdapter);


        stype_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (cancelReasonList.size() != 0) {
                    for (int i = 0; i < cancelReasonList.size(); i++) {
                        if (cancelReasonList.get(i).getName().equalsIgnoreCase(stype_spinner.getSelectedItem().toString())) {
                            reason_id = cancelReasonList.get(position).getId();
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        dialog.show();
    }

    //TODO API HITs
    private void AcceptRejectRequest(final String notification_id, final boolean isAccept) {
        final ProgressDialog progressDialog = new ProgressDialog(Activity_Sales_RequestDetail.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;
        if (isAccept) {
            calls = apiInterface.AcceptSalesReq(notification_id);
        } else {
            calls = apiInterface.RejectSalesReq(notification_id);
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
                            if (isAccept) {
                                reqStatus = "accepted";
                            } else {
                                reqStatus = "rejected";
                            }
                            CheckStatusHideShowViews();

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_RequestDetail.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_RequestDetail.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_RequestDetail.this, getResources().getString(R.string.server_error));
            }
        });
    }

    //TODO API HITs
    private void CancelRequest(final String request_id, String reasonID, String msg) {
        final ProgressDialog progressDialog = new ProgressDialog(Activity_Sales_RequestDetail.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.CancelRequest(request_id, reasonID, msg);
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
                            text_want_cancel.setVisibility(View.GONE);
                            text_status.setText("Cancelled");
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_RequestDetail.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_RequestDetail.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_RequestDetail.this, getResources().getString(R.string.server_error));
            }
        });
    }

    //TODO API HITs
    private void ReasonForCancel() {
        final ProgressDialog progressDialog = new ProgressDialog(Activity_Sales_RequestDetail.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetCancelReason();
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
                            cancelReasonList = new ArrayList<>();
                            JSONArray jsonArray = object.getJSONArray("data");
                            spinner_reason_Array = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setId(jsonObject.getString("reason_id"));
                                nameID.setName(jsonObject.getString("name"));
                                cancelReasonList.add(nameID);
                                spinner_reason_Array[i] = jsonObject.getString("name");
                            }

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_RequestDetail.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_RequestDetail.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_RequestDetail.this, getResources().getString(R.string.server_error));
            }
        });
    }

    private void initLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Activity_Sales_RequestDetail.this);
        mSettingsClient = LocationServices.getSettingsClient(Activity_Sales_RequestDetail.this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//                updateLocationUI();
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

    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            if (CommonMethods.checkConnection()) {
                updateLocationOnServer(mCurrentLocation.getLatitude(),
                        mCurrentLocation.getLongitude());
            } else {
                CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_RequestDetail.this, getResources().getString(R.string.internetconnection));
            }

        }
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

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
//        updateLocationUI();
    }

    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(Activity_Sales_RequestDetail.this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i("tag", "All location settings are satisfied.");

//                        Toast.makeText(Activity_Sales_RequestDetail.this, "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

//                        updateLocationUI();
                    }
                })
                .addOnFailureListener(Activity_Sales_RequestDetail.this, new OnFailureListener() {
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
                                    rae.startResolutionForResult(Activity_Sales_RequestDetail.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i("tag", "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e("tag", errorMessage);

                                Toast.makeText(Activity_Sales_RequestDetail.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

//                        updateLocationUI();
                    }
                });
    }


    private void GoToGoogleMapForNavigation() {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude() + "&daddr=" + destination_loc.latitude + "," + destination_loc.longitude));
        startActivity(intent);
    }
}
