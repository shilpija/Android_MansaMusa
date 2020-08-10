package com.freshhome;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;

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

import com.freshhome.CommonUtil.AddressResultReceiver;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.FetchAddressService;
import com.freshhome.CommonUtil.StartGooglePlayServices;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.datamodel.NameID;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    LinearLayout linear_signup, linear_back, linear_login_text, linear_supplier_type;
    TextView text_login, text_mobilenumber, text_selectlocation, text_select_dob, text_signup;
    EditText edit_emailid, edit_username, edit_re_password, edit_password, edit_name,
            edit_buildingname, edit_floor_no, edit_flat, edit_landmark;
    int PLACE_PICKER_REQUEST = 1;
    ApiInterface apiInterface;
    ProgressDialog progressDialog;
    String latitute = "0", longitute = "0", selected_city_id = "";
    UserSessionManager sessionManager;
    String dateofbirth = "", supplierType = "";
    Spinner spinner_city, stype_spinner;
    ImageView spinner_arrow, stype_spinner_arrow;
    boolean fromLogin = true;
    ArrayList<NameID> arrayCity;
    String[] spinner_city_array;
    double pLat, pLong, dLat, dLong;
    private String lat1;
    private String lat2;
    private String deslat1;
    private String deslat2;
    private String address1 = "";
    private String terms = "I accept the Terms and Conditions & Privacy Policy";

    private TextView tv_accep;

    StartGooglePlayServices startGooglePlayServices=new StartGooglePlayServices(this);
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private AddressResultReceiver mResultReceiver;
    private LocationCallback locationCallback;
    protected final int REQ_CODE_GPS_SETTINGS = 150;
    private final int REQ_CODE_LOCATION = 107;
    private Location mLastLocation;
    private boolean isLocServiceStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        startLocationFunctioning();

        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(SignUpActivity.this);

        linear_supplier_type = (LinearLayout) findViewById(R.id.linear_supplier_type);

        linear_login_text = (LinearLayout) findViewById(R.id.linear_login_text);
        text_signup = (TextView) findViewById(R.id.text_signup);
        linear_signup = (LinearLayout) findViewById(R.id.linear_signup);
        linear_signup.setOnClickListener(this);
        linear_back = (LinearLayout) findViewById(R.id.linear_back);
        linear_back.setOnClickListener(this);

        text_login = (TextView) findViewById(R.id.text_login);
        text_login.setOnClickListener(this);
        text_mobilenumber = (TextView) findViewById(R.id.text_mobilenumber);
        text_mobilenumber.setText(sessionManager.getPhoneNumber());

        tv_accep = (TextView) findViewById(R.id.tv_accep);
        text_selectlocation = (TextView) findViewById(R.id.text_selectlocation);
        text_selectlocation.setOnClickListener(this);
        text_select_dob = (TextView) findViewById(R.id.text_select_dob);
        text_select_dob.setOnClickListener(this);

        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_emailid = (EditText) findViewById(R.id.edit_emailid);
        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_re_password = (EditText) findViewById(R.id.edit_re_password);
        edit_username = (EditText) findViewById(R.id.edit_username);
        edit_buildingname = (EditText) findViewById(R.id.edit_buildingname);
        edit_floor_no = (EditText) findViewById(R.id.edit_floor_no);
        edit_flat = (EditText) findViewById(R.id.edit_flat);
        edit_landmark = (EditText) findViewById(R.id.edit_landmark);

        spinner_arrow = (ImageView) findViewById(R.id.spinner_arrow);
        spinner_arrow.setOnClickListener(this);
        stype_spinner_arrow = (ImageView) findViewById(R.id.stype_spinner_arrow);
        stype_spinner_arrow.setOnClickListener(this);

        spinner_city = (Spinner) findViewById(R.id.locationspinner);
        stype_spinner = (Spinner) findViewById(R.id.stype_spinner);

//        //set up spinner
        String[] spinner_array = getResources().getStringArray(R.array.stypePr);
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(this, R.layout.layout_spinner_text_bg_transparent, spinner_array);
        stype_spinner.setAdapter(spinner_adapter);

        if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToCook)) {
            linear_supplier_type.setVisibility(View.VISIBLE);
            text_selectlocation.setHint(getResources().getString(R.string.select_location));
        } else {
            linear_supplier_type.setVisibility(View.GONE);
            text_selectlocation.setHint(getResources().getString(R.string.uloc));
        }

        if (CommonMethods.checkConnection()) {
            getCitdata();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(SignUpActivity.this, getResources().getString(R.string.internetconnection));
        }

        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (arrayCity.size() != 0) {
                    selected_city_id = arrayCity.get(position).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        stype_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                } else {
                    if (stype_spinner.getSelectedItem().toString().equalsIgnoreCase(getResources().getString(R.string.type_sweets))) {
                        supplierType = "1";
                    } else if (stype_spinner.getSelectedItem().toString().equalsIgnoreCase(getResources().getString(R.string.type_products))) {
                        supplierType = "2";
                    }
//                else if (stype_spinner.getSelectedItem().toString().equalsIgnoreCase(getResources().getString(R.string.type_shop))) {
//                    supplierType = "3";
//                }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        createSpannableString();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_signup:
                if (edit_emailid.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(SignUpActivity.this, getResources().getString(R.string.enter_email));
                } else if (!CommonMethods.isValidEmail(edit_emailid.getText().toString().trim())) {
                    CommonUtilFunctions.Error_Alert_Dialog(SignUpActivity.this, getResources().getString(R.string.valid_email));
                } else if (edit_username.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(SignUpActivity.this, getResources().getString(R.string.enter_username));
                } else if (text_select_dob.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(SignUpActivity.this, getResources().getString(R.string.enterdob));
                } else if (text_selectlocation.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(SignUpActivity.this, getResources().getString(R.string.enter_loc));
                } else if (edit_password.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(SignUpActivity.this, getResources().getString(R.string.enter_password));
                } else if (edit_password.getText().toString().trim().length() < 5) {
                    CommonUtilFunctions.Error_Alert_Dialog(SignUpActivity.this, getResources().getString(R.string.pass_length));
                } else if (edit_re_password.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(SignUpActivity.this, getResources().getString(R.string.enter_repassword));
                } else if (!edit_password.getText().toString().trim().equalsIgnoreCase(edit_re_password.getText().toString().trim())) {
                    CommonUtilFunctions.Error_Alert_Dialog(SignUpActivity.this, getResources().getString(R.string.password_match));
                } else if (edit_buildingname.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(SignUpActivity.this, getResources().getString(R.string.enter_buildingno));
                } else if (edit_floor_no.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(SignUpActivity.this, getResources().getString(R.string.enter_floorgno));
                } else if (edit_flat.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(SignUpActivity.this, getResources().getString(R.string.enter_flatno));
                } else if (edit_landmark.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(SignUpActivity.this, getResources().getString(R.string.enter_landmark));
//                } else if (supplierType.equalsIgnoreCase("")) {
//                    CommonUtilFunctions.Error_Alert_Dialog(SignUpActivity.this, getResources().getString(R.string.enter_product_type));
                } else {
                    if (CommonMethods.checkConnection()) {
                        postSignupdata();
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(SignUpActivity.this, getResources().getString(R.string.internetconnection));
                    }
                }
                break;

            case R.id.linear_back:
                SignUpActivity.this.finish();
                break;

            case R.id.text_login:
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// This flag ensures all activities on top of the CloseAllViewsDemo are cleared.
                startActivity(i);
                SignUpActivity.this.finish();
                break;

            case R.id.text_select_dob:
                dateofbirth = CommonUtilFunctions.DatePickerDialog_DOB(SignUpActivity.this, text_select_dob);
                break;

            case R.id.text_selectlocation:
                startActivityForResult(AddressPickerAct.getIntent(this, "DROPOFF", ""), 100);

//                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//                try {
//
//                    startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
//                } catch (GooglePlayServicesRepairableException e) {
//                    e.printStackTrace();
//                } catch (GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                }

                break;

            case R.id.spinner_arrow:
                spinner_city.performClick();
                break;


            case R.id.stype_spinner_arrow:
                stype_spinner.performClick();
                break;
        }
    }

    //signup
    private void postSignupdata() {
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setCancelable(false);
//        progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.regiser(text_mobilenumber.getText().toString().trim(),
                edit_emailid.getText().toString().trim(),
                edit_password.getText().toString().trim(),
                latitute,
                longitute,
                edit_username.getText().toString().trim(),
                text_selectlocation.getText().toString().trim(),
                CommonUtilFunctions.changeDateFormatYYYYMMDD(text_select_dob.getText().toString()), edit_username.getText().toString().trim(), edit_buildingname.getText().toString(),
                edit_flat.getText().toString(),
                edit_floor_no.getText().toString(),
                edit_landmark.getText().toString(), selected_city_id, sessionManager.getLoginType(),
                supplierType);

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
                            JSONObject obj = object.getJSONObject("success");
                            //move to next login screen
                            Toast.makeText(SignUpActivity.this, "An email has been sent. Please verify your account.", Toast.LENGTH_SHORT).show();
                            Intent i_nav = new Intent(SignUpActivity.this, LoginActivity.class);
                            i_nav.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i_nav);
                            SignUpActivity.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(SignUpActivity.this, obj.getString("msg"));
                        }

                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(SignUpActivity.this, getResources().getString(R.string.server_error));
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

    //getcity
    private void getCitdata() {
        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
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
                        arrayCity = new ArrayList<>();

                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONObject jsonObject = object.getJSONObject("success");
                            //city array
                            JSONArray city_array = jsonObject.getJSONArray("city");
                            spinner_city_array = new String[city_array.length()];
                            for (int i = 0; i < city_array.length(); i++) {
                                JSONObject obj = city_array.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setId(obj.getString("city_id"));
                                nameID.setName(obj.getString("city_name"));
                                spinner_city_array[i] = obj.getString("city_name");
                                arrayCity.add(nameID);
                            }
                            //set up spinner
                            ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(SignUpActivity.this, R.layout.layout_spinner_text_bg_transparent, spinner_city_array);
                            spinner_city.setAdapter(spinner_adapter);

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(SignUpActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(SignUpActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(SignUpActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {

                address1 = data.getStringExtra("ADDRESS");
                lat1 = data.getStringExtra("LAT");
                lat2 = data.getStringExtra("LONG");
                pLat = Double.parseDouble(lat1);
                pLong = Double.parseDouble(lat2);
                text_selectlocation.setText(address1);
                text_selectlocation.setTextColor(getResources().getColor(R.color.black));
            }

        }else if(requestCode==REQ_CODE_GPS_SETTINGS){
            switch (resultCode) {
                    case Activity.RESULT_OK:
                        loadCurrentLoc();
                        break;
                    case Activity.RESULT_CANCELED:
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                        break;
                }
        }
//        if (requestCode == PLACE_PICKER_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                Place place = PlacePicker.getPlace(data, this);
//                latitute = String.valueOf(place.getLatLng().latitude);
//                longitute = String.valueOf(place.getLatLng().longitude);
//                text_selectlocation.setText(place.getName().toString());
//                text_selectlocation.setTextColor(getResources().getColor(R.color.black));
//                String toastMsg = String.format("Place: %s", place.getAddress());
//                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
//            }
//        }
    }

    //    METHOD: TO START FULL PROCESS FOR FIRST TIME..
    private void startLocationFunctioning() {
        if (!CommonMethods.isNetworkAvailable(this)) {
            Toast.makeText(this.getApplicationContext(), "Internet not available.", Toast.LENGTH_SHORT).show();
        } else {
            if (startGooglePlayServices.isGPlayServicesOK(this)) {
                buildGoogleApiClient();
            }
        }
    }

    //   METHOD: TO  Create an instance of the GoogleAPIClient AND LocationRequest
    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, 1 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
        createLocationRequest();
    }

    //Method: To create location request and set its priorities
    public  void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(10 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // CREATE A FUSED LOCATION CLIENT PROVIDER OBJECT
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(SignUpActivity.this);

        //      Initialize AddressResultReceiver class object
        mResultReceiver = new AddressResultReceiver(SignUpActivity.this,null);

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUpLocationSettingsTaskStuff();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    // LOCATION SETTINGS SET UP
    public  void setUpLocationSettingsTaskStuff() {
        /* Initialize the pending result and LocationSettingsRequest */
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());


        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.

                // GPS is already enabled no need of review_dialog
                //Log.w(TAG, "onResult: Success 1");

                SignUpActivity.this.loadCurrentLoc(); //GET CURRENT LOCATION
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a review_dialog.
                    // Log.w(TAG, "REQ_CODE_GPS_SETTINGS: REQ " + 0);

                    try {
                        // Show the review_dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        //Log.w(TAG, "REQ_CODE_GPS_SETTINGS: REQ " + 1);
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(SignUpActivity.this, REQ_CODE_GPS_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                        sendEx.printStackTrace();
                    }
                }
            }
        });
    }

    //    METHOD TO GET CURRENT LOCATION OF DEVICE
    public void loadCurrentLoc() {

        //      Marshmallow +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    &&
                    this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                    startGooglePlayServices.showDenyRationaleDialog(this,"You need to allow access to Device Location", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            switch (which) {
                                case BUTTON_POSITIVE:
                                    dialog.dismiss();
                                    SignUpActivity.this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            REQ_CODE_LOCATION);

                                    break;
                                case BUTTON_NEGATIVE:
                                    dialog.dismiss();
                                    SignUpActivity.this.finish();
                                    break;
                            }

                        }
                    });

                    return;
                }

                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQ_CODE_LOCATION);

                return;
            }


            /*DO THE LOCATION STUFF*/

            try {


                mFusedLocationClient.getLastLocation().addOnSuccessListener(this,
                        new OnSuccessListener<android.location.Location>() {
                            @Override
                            public void onSuccess(android.location.Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object

                                    //Log.w(TAG, "addOnSuccessListener: location: " + location);

                                    SignUpActivity.this.locationCallBack(location);


                                } else {
//                                    Toast.makeText(CreateYourBookClub.this, "Make sure that Location is Enabled on the device.", Toast.LENGTH_LONG).show();

                                    isLocServiceStarted = false;
                                    //Log.w(TAG, "addOnSuccessListener: location: " + null);
//                                    MyApplication.makeASnack(CreateYourBookClub.this.binding.getRoot(), getString(R.string.no_location_detected));
                                }
                            }
                        });


                locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        for (Location location : locationResult.getLocations()) {
                            // Update UI with location data
                            if (location != null) {
                                // Log.w(TAG, "LocationCallback:" + location);

                                if (!isLocServiceStarted) {


                                    locationCallBack(location);

                                }
                            }

                        }
                    }

                };
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else    //    PRE-Marshmallow
        {
            /*DO THE LOCATION STUFF*/

            try {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(this,
                        new OnSuccessListener<android.location.Location>() {
                            @Override
                            public void onSuccess(android.location.Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object

                                    //Log.w(TAG, "addOnSuccessListener: location: " + location);

                                    SignUpActivity.this.locationCallBack(location);

                                } else {
//                                    Toast.makeText(CreateYourBookClub.this, "Make sure that Location is Enabled on the device.", Toast.LENGTH_LONG).show();

                                    isLocServiceStarted = false;
                                    //Log.w(TAG, "addOnSuccessListener: location: " + null);
//                                    MyApplication.makeASnack(CreateYourBookClub.this.binding.getRoot(), getString(R.string.no_location_detected));
                                }
                            }
                        });


                locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        for (Location location : locationResult.getLocations()) {
                            // Update UI with location data
                            if (location != null) {
                                //Log.w(TAG, "LocationCallback:" + location);
                                if (!isLocServiceStarted) {
                                    locationCallBack(location);
                                }
                            }
                        }
                    }

                };
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void locationCallBack(Location location) {

        mLastLocation = location;
        //App.getInstance ().setmLastLocation (mLastLocation);

        //latitudeFromPicker = mLastLocation.getLatitude();
        //longitudeFromPicker = mLastLocation.getLongitude();

        sessionManager.saveLatitude(String.valueOf(mLastLocation.getLatitude()));
        sessionManager.saveLongitude(String.valueOf(mLastLocation.getLongitude()));


//        SharedPreferenceWriter.getInstance(this).writeStringValue(SPreferenceKey.LATITUDE,String.valueOf(mLastLocation.getLatitude()));
//        SharedPreferenceWriter.getInstance(this).writeStringValue(SPreferenceKey.LONGITUDE,String.valueOf(mLastLocation.getLongitude()));

//        double Lat = mLastLocation.getLatitude();
//        double Long = mLastLocation.getLongitude();


        Intent intent = new Intent(this, FetchAddressService.class);
        intent.putExtra(FetchAddressService.FIND_BY, FetchAddressService.FIND_BY_LOCATION);
        intent.putExtra(FetchAddressService.RECEIVER, mResultReceiver);
        intent.putExtra(FetchAddressService.LOCATION, mLastLocation);
        this.startService(intent);

        isLocServiceStarted = true;

        //currentLocationLatLngs = new LatLng (lat, lng);

    }

    //set up spannable String color
    private void createSpannableString() {

        SpannableString spannableString = new SpannableString (terms);

//for terms and conditions
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View vi) {
                Intent intent = new Intent(SignUpActivity.this, CommonWebViewActivity.class);
                intent.putExtra("Page", "tv_terms_condition");
                startActivity(intent);
            }
            @Override public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);

            }
        },13,33,0);
        spannableString.setSpan (new ForegroundColorSpan(getResources ().getColor (R.color.app_color_blue)),13,33, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);



//for privacy Policy
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View vi) {
                Intent intent = new Intent(SignUpActivity.this, CommonWebViewActivity.class);
                intent.putExtra("Page", "tv_privacyPolicy");
                startActivity(intent);

            }
            @Override public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);

            }
        },36,terms.length (),0);
        spannableString.setSpan (new ForegroundColorSpan(getResources ().getColor (R.color.app_color_blue)),36,terms.length (),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv_accep.setText(spannableString);
        tv_accep.setMovementMethod(LinkMovementMethod.getInstance());
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //fifth
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case REQ_CODE_GPS_SETTINGS:
//                switch (resultCode) {
//                    case Activity.RESULT_OK:
//                        loadCurrentLoc();
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
//                        break;
//                }
//                break;
//        }
//    }

}
