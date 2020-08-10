package com.freshhome;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.freshhome.CommonUtil.AddressResultReceiver;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.FetchAddressService;
import com.freshhome.CommonUtil.StartGooglePlayServices;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.datamodel.NameID;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class AddAddressActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView image_back;
    EditText edit_title, edit_buildingno, edit_floorno, edit_flat, edit_landmark;
    TextView text_address;
    Spinner spinner_city;
    LinearLayout linear_add, linear_delete;
    int PLACE_PICKER_REQUEST = 119;
    ApiInterface apiInterface;
    UserSessionManager sessionManager;
    ImageView spinner_arrow;
    boolean fromLogin = true;
    ArrayList<NameID> arrayCity;
    String[] spinner_city_array;
    String latitute = "0", longitute = "0", selected_city_id = "", address_id = "", is_default = "";
    boolean isUpdate = false;
    CheckBox checkbox;

    double pLat, pLong;
    private String lat1;
    private String lat2;
    private String deslat1;
    private String deslat2;
    private String address1 = "";

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
        setContentView(R.layout.activity_add_address);

        arrayCity = new ArrayList<>();
        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(AddAddressActivity.this);

        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);

        edit_title = (EditText) findViewById(R.id.edit_title);
        edit_buildingno = (EditText) findViewById(R.id.edit_buildingno);
        edit_floorno = (EditText) findViewById(R.id.edit_floorno);
        edit_flat = (EditText) findViewById(R.id.edit_flat);
        edit_landmark = (EditText) findViewById(R.id.edit_landmark);

        spinner_city = (Spinner) findViewById(R.id.spinner_city);

        spinner_arrow = (ImageView) findViewById(R.id.spinner_arrow);
        spinner_arrow.setOnClickListener(this);

        text_address = (TextView) findViewById(R.id.text_address);
        text_address.setOnClickListener(this);

        linear_add = (LinearLayout) findViewById(R.id.linear_add);
        linear_add.setOnClickListener(this);

        linear_delete = (LinearLayout) findViewById(R.id.linear_delete);
        linear_delete.setOnClickListener(this);

        checkbox = (CheckBox) findViewById(R.id.checkbox);
        //get data from previous activity and change ui according to that
        if (getIntent().hasExtra("id")) {
            isUpdate = true;
            getDataFromIntent();
            linear_delete.setVisibility(View.VISIBLE);
        } else {
            isUpdate = false;
            linear_delete.setVisibility(View.GONE);
        }

        //hit api to get city data
        if (CommonMethods.checkConnection()) {
            getCitdata();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(AddAddressActivity.this, getResources().getString(R.string.internetconnection));
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
    }

    private void getDataFromIntent() {
        is_default = getIntent().getStringExtra("default");
        if (is_default.equalsIgnoreCase("yes")) {
            checkbox.setChecked(true);
        }else{
            checkbox.setChecked(false);
        }
        address_id = getIntent().getStringExtra("id");
        selected_city_id = getIntent().getStringExtra("city");
        latitute = getIntent().getStringExtra("latitude");
        longitute = getIntent().getStringExtra("longitude");
        edit_title.setText(getIntent().getStringExtra("title"));
        text_address.setText(getIntent().getStringExtra("loc"));
        edit_buildingno.setText(getIntent().getStringExtra("buildingno"));
        edit_floorno.setText(getIntent().getStringExtra("floorno"));
        edit_flat.setText(getIntent().getStringExtra("flatno"));
        edit_landmark.setText(getIntent().getStringExtra("landmark"));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                AddAddressActivity.this.finish();
                break;

            case R.id.text_address:
                startActivityForResult(AddressPickerAct.getIntent(this, "DROPOFF", ""), PLACE_PICKER_REQUEST);
//                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//                try {
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

            case R.id.linear_add:
                if (checkbox.isChecked()) {
                    is_default = "yes";
                } else {
                    is_default = "no";
                }
                if (edit_title.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(AddAddressActivity.this, getResources().getString(R.string.enter_title));
                } else if (text_address.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(AddAddressActivity.this, getResources().getString(R.string.enter_loc));
                } else if (edit_buildingno.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(AddAddressActivity.this, getResources().getString(R.string.enter_buildingno));
                } else if (edit_floorno.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(AddAddressActivity.this, getResources().getString(R.string.enter_floorgno));
                } else if (edit_flat.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(AddAddressActivity.this, getResources().getString(R.string.enter_flatno));
                } else if (edit_landmark.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(AddAddressActivity.this, getResources().getString(R.string.enter_landmark));
                } else {
                    if (CommonMethods.checkConnection()) {
                        PostAddress(isUpdate);
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(AddAddressActivity.this, getResources().getString(R.string.internetconnection));
                    }
                }

                break;

            case R.id.linear_delete:
                //delete address
                if (CommonMethods.checkConnection()) {
                    DeleteAddress();
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(AddAddressActivity.this, getResources().getString(R.string.internetconnection));
                }
                break;
        }
    }


    private void DeleteAddress() {
        final ProgressDialog progressDialog = new ProgressDialog(AddAddressActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.DeleteAddress(address_id);

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
                            CommonMethods.showtoast(AddAddressActivity.this, obj.getString("msg"));
                            AddAddressActivity.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(AddAddressActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(AddAddressActivity.this, getResources().getString(R.string.server_error));
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
    private void PostAddress(boolean isUpdate) {
        final ProgressDialog progressDialog = new ProgressDialog(AddAddressActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;

        if (isUpdate) {
            //update address
            calls = apiInterface.UpdateDeliveryAddress(address_id, edit_title.getText().toString().trim(),
                    text_address.getText().toString().trim(),
                  //sk  longitute,latitute, selected_city_id,
                    lat2,lat1, selected_city_id,
                    edit_buildingno.getText().toString().trim(),
                    edit_flat.getText().toString().trim(),
                    edit_floorno.getText().toString().trim(),
                    edit_landmark.getText().toString().trim(), is_default);
        } else {
            //add address
            calls = apiInterface.AddDeliveryAddress(edit_title.getText().toString().trim(),
                    text_address.getText().toString().trim(),
                 //sk   latitute, longitute, selected_city_id,
                    lat2, lat1, selected_city_id,
                    edit_buildingno.getText().toString().trim(),
                    edit_flat.getText().toString().trim(),
                    edit_floorno.getText().toString().trim(),
                    edit_landmark.getText().toString().trim(), is_default);
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
                            JSONObject obj = object.getJSONObject("success");
                            CommonMethods.showtoast(AddAddressActivity.this, obj.getString("msg"));
                            AddAddressActivity.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(AddAddressActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(AddAddressActivity.this, getResources().getString(R.string.server_error));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {

                address1 = data.getStringExtra("ADDRESS");
                lat1 = data.getStringExtra("LAT");
                lat2 = data.getStringExtra("LONG");
                pLat = Double.parseDouble(lat1);
                pLong = Double.parseDouble(lat2);
                text_address.setText(address1);
                text_address.setTextColor(getResources().getColor(R.color.black));
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
//                text_address.setText(place.getName().toString());
//                text_address.setTextColor(getResources().getColor(R.color.black));
////                String toastMsg = String.format("Place: %s", place.getAddress());
////                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
//            }
//        }
    }

    //getcity
    private void getCitdata() {
        final ProgressDialog progressDialog = new ProgressDialog(AddAddressActivity.this);
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
                            ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(AddAddressActivity.this, R.layout.layout_spinner_text_bg_transparent, spinner_city_array);
                            spinner_city.setAdapter(spinner_adapter);
                            preSelectCity();

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(AddAddressActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(AddAddressActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(AddAddressActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    private void preSelectCity() {
        if (arrayCity.size() != 0) {
            for (int i = 0; i < arrayCity.size(); i++) {
                if (arrayCity.get(i).getId().equalsIgnoreCase(selected_city_id)) {
                    spinner_city.setSelection(i);
                }
            }
        }
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
                                    AddAddressActivity.this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            REQ_CODE_LOCATION);

                                    break;
                                case BUTTON_NEGATIVE:
                                    dialog.dismiss();
                                    AddAddressActivity.this.finish();
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
                        new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(android.location.Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object

                                    //Log.w(TAG, "addOnSuccessListener: location: " + location);

                                    AddAddressActivity.this.locationCallBack(location);


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

                                    AddAddressActivity.this.locationCallBack(location);

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
}
