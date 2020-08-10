package com.freshhome;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.freshhome.CommonUtil.AllAPIs;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.DriverModule.DriverNavDrawerActivity;
import com.freshhome.SalesModule.ActivitySalesNavDrawer;
import com.freshhome.SalesModule.Activity_Sales_RequestDetail;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {
    UserSessionManager sessionManager;
    String user_type = "", request_status = "", request_type = "", location_Value = "",
            message = "", request_id = "", name = "", lat = "", lng = "", phonenumber = "", device_token = "";
    LinkedHashMap<String, String> data_hashmap;
    ApiInterface apiInterface;
    List<String> listPermissionsNeeded;
    private static final int SPLASH_DISPLAY_TIMER = 2000;
    private static final int PERMISSION_REQUEST_CODE = 12;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 255;
    Location mylocation;
    GoogleApiClient googleApiClient;
    private static final int REQUEST_CHECK_SETTINGS_GPS = 27;
    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
//        CommonMethods.setLocale("en",SplashScreen.this);
        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(SplashScreen.this);
        setUpGClient();



        String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);


        //tractions charges
        sessionManager.saveUserGuest(android_id);
        Log.e("device "," IMIE  "+ android_id);
    }



    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(SplashScreen.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                //denied
                Log.e("denied", permission);
                showSettingLocation(this);

            } else {
                if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                    //allowed
                    Log.e("allowed", permission);
                    getMyLocation();
                } else {
                    //set to never ask again
                    Log.e("set to never ask again", permission);
                    //startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                    Toast.makeText(SplashScreen.this, "Please accept permissions due to security purpose", Toast.LENGTH_SHORT).show();
                    // User selected the Never Ask Again Option
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, 0);

                }
            }
        }
    }

    private void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(SplashScreen.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest,  this);
                    PendingResult result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    //Toast.makeText(SplashActivity.this, "permission allow", Toast.LENGTH_SHORT).show();
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(SplashScreen.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                        //time();
                                        getdeepLinkdata();
//                                        //for deep linking
//                                        if (getIntent() != null) {
//                                            Intent appLinkIntent = getIntent();
//                                            Uri appLinkData = appLinkIntent.getData();
//                                            if (appLinkData != null) {
//                                                // BaseManager.saveDataIntoPreferences("dynamicLinking","Linking");
//                                                String url = appLinkData.toString();
//                                                String status1 = url.substring(url.lastIndexOf("/") + 1, url.length());
//                                                startActivity(JobDescription.getIntent(SplashActivity.this, status1));
//                                                // startActivity(EventDetailActivity.getIntance(this, status, "green"));
//                                            }else {
//                                                time();
//                                            }
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        //  Toast.makeText(SplashActivity.this, "permission denied", Toast.LENGTH_SHORT).show();
                                        status.startResolutionForResult(SplashScreen.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied. However, we have no way to fix the
                                    // settings so we won't show the dialog.
                                    //finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    //fifth
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                        break;
                }
                break;
        }
    }

    public void showSettingLocation(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage("Dr. Now requires access to location. To enjoy all that Dr. Now has to offer, " +
                "turn on your GPS and give Dr. Now access to your location.");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
               /* Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivityForResult(intent, PERMISSION_REQUEST_CODE);
                dialog.dismiss();*/

                checkPermissions();
                dialog.dismiss();

            }
        });

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        if (!(activity).isFinishing()) {
            alert.show();
        }



    }

    private void time() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                if (getIntent().getExtras() != null) {
//                    getNotificationData();
//                } else {
                if (sessionManager.isLoggedIn()) {
                    sessionManager.checkLogin();
                }else {
                    getGuestUser();
                    sessionManager.saveLoginType("1");
                    Intent i = new Intent(SplashScreen.this, MainActivity_NavDrawer.class);
                    i.putExtra("deepLink","ravi");
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);

                }
//                }
                SplashScreen.this.finish();
            }
        }, 2000);
    }

    private void getNotificationData() {
        data_hashmap = new LinkedHashMap<>();
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                data_hashmap.put(key, value.toString());
                if (key.equalsIgnoreCase("user_type")) {
                    user_type = value.toString();
                }
                Log.e("data", "Key: " + key + " Value: " + value);
            }
            Intent resultIntent;

            //check if logged in then procced else move to select user screen
            if (sessionManager.isLoggedIn()) {
                if (user_type.equalsIgnoreCase(ConstantValues.Sales)) {
                    //sales
                    resultIntent = new Intent(getApplicationContext(), ActivitySalesNavDrawer.class);
                    resultIntent.putExtra("message", message);

                } else if (user_type.equalsIgnoreCase(ConstantValues.Driver)) {
                    //driver
                    resultIntent = new Intent(getApplicationContext(), DriverNavDrawerActivity.class);
                    resultIntent.putExtra("message", message);

                } else if (user_type.equalsIgnoreCase(ConstantValues.ToCook)) {
                    //supplier
//                    sessionManager.saveLoginType(ConstantValues.ToCook);
                    resultIntent = new Intent(getApplicationContext(), MainActivity_NavDrawer.class);
                    resultIntent.putExtra("message", message);

                } else if (user_type.equalsIgnoreCase(ConstantValues.ToEat)) {
                    //user
//                    sessionManager.saveLoginType(ConstantValues.ToEat);
                    resultIntent = new Intent(getApplicationContext(), MainActivity_NavDrawer.class);
                    resultIntent.putExtra("message", message);

                } else {
//                    sessionManager.saveLoginType(ConstantValues.ToEat);
                    resultIntent = new Intent(getApplicationContext(), MainActivity_NavDrawer.class);
                    resultIntent.putExtra("message", message);
                }
                startActivity(resultIntent);
            } else {
                Intent intent = new Intent(SplashScreen.this, SelectCookEatActivity.class);
                startActivity(intent);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }


    private void getGuestUser() {
//        final ProgressDialog progressDialog = new ProgressDialog(SplashScreen.this);
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
//        if (!progressDialog.isShowing()) {
//            progressDialog.show();
//        }
        Call<JsonElement> calls = apiInterface.guestregister(sessionManager.getUserGuestType());

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
                try {
                    if (response.code() == 200) {
                        //array_Sub_categories = new ArrayList<>();
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONObject obj = object.getJSONObject("data");


                            sessionManager.createGuestLoginSession(obj.getString("auth_key"),
                                    CommonMethods.checkNull(obj.getString("phone_number")));

                            if(!sessionManager.getUserDetails().get("token").equalsIgnoreCase("") && sessionManager.getUserDetails().get("token") != null){
                                AllAPIs.token = sessionManager.getUserDetails().get("token");
                            }else {
                                AllAPIs.token = sessionManager.getGuestUserDetails().get("token");
                            }

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(SplashScreen.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(SplashScreen.this, getResources().getString(R.string.server_error));
                    }
                } catch (
                        JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
                call.cancel();
                CommonUtilFunctions.Error_Alert_Dialog(SplashScreen.this, getResources().getString(R.string.server_error));
            }
        });
    }

    private void getdeepLinkdata(){
        //for deep linking
        if (getIntent() != null) {
            if (sessionManager.isLoggedIn()) {
                Intent appLinkIntent = getIntent();
                Uri appLinkData = appLinkIntent.getData();
                if (appLinkData != null) {
                    String url = appLinkData.toString();
                    String status1 = url.substring(url.lastIndexOf("/") + 1, url.length());
                    sessionManager.saveLoginType("1");
                    Intent i = new Intent(SplashScreen.this, MainActivity_NavDrawer.class);
                    i.putExtra("supplier_id",status1);
                    i.putExtra("deepLink","deepLink");
                    startActivity(i);
                    finish();

                }else {
                    time();
                }
            }else {
                getGuestUser();
                    Intent appLinkIntent = getIntent();
                    Uri appLinkData = appLinkIntent.getData();
                    if (appLinkData != null) {
                        String url = appLinkData.toString();
                        String status1 = url.substring(url.lastIndexOf("/") + 1, url.length());
                        sessionManager.saveLoginType("1");
                        Intent i = new Intent(SplashScreen.this, MainActivity_NavDrawer.class);
                        i.putExtra("supplier_id",status1);
                        i.putExtra("deepLink","deepLink");
                        startActivity(i);
                        finish();

                    }else {
                        time();
                    }

            }

        }else {
            time();
        }
    }

//    boolean isdriver = false, isSales = false;
//
//                sessionManager.createLoginSession(status1,
//            "",
//            "",
//            "",
//            "DTcPYr8hduN0k4VJ5ZCUtmryS1U6MHru",
//            sessionManager.getLoginType().toString(),
//                        CommonMethods.checkNull(""),
//                                "",
//                                "",
//                                "",
//                                "",
//                                "",
//                                "",
//                                "",
//                                "",
//                                "",
//                                "",
//                                "", "",
//                                "",
//    isdriver, isSales, "");

}
