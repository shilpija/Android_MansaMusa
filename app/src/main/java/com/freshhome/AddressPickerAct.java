package com.freshhome;


import android.app.Activity;
import android.app.AlertDialog;

import android.Manifest;
import android.app.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.freshhome.CommonUtil.AddressResultReceiver;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.FetchAddressService;
import com.freshhome.CommonUtil.GPSTracker;
import com.freshhome.CommonUtil.StartGooglePlayServices;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.async.AsyncGetLatLong;
import com.freshhome.async.GooglePlacesAutocompleteAdapter;
import com.freshhome.async.ModelPlace;
import com.freshhome.async.OnLocationSelect;
import com.freshhome.datamodel.AddressList;
import com.google.android.gms.common.ConnectionResult;
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
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;


public class AddressPickerAct extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemClickListener, GoogleMap.OnMapClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    protected GoogleApiClient mGoogleApiClient;
    String latitudes = "";
    String longitudes = "";
    String getAddress = "";
    String spotLocation = "";
    String spotName = "";
    String region = "";
    @BindView(R.id.edt_search)
    AutoCompleteTextView autoLocation;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.showadd)
    TextView tvAddress;
    int i = 0;
    List<Address> addresses = null;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.edt_building_no)
    EditText edt_building_no;
    private double Lat, Long;
    private GoogleMap mMap;
    private String Location;
    private String lat;
    private String longs;
    private CharSequence primaryText;
    private boolean isCorrectAddress = false;
    private GooglePlacesAutocompleteAdapter autocompleteAdapter;
    private Marker marker;
    private MarkerOptions markerOptions;
    private LatLng latLng;
    private String currentAddress = "";
    private Context context;
    private LatLng mCenterLatLong;
    private AddressList addressList;
    private String commingfrom = "";
    private String currentLoc = "";
    private String edtTitle = "";
    UserSessionManager sessionManager;

    public static Intent getIntent(Context context, AddressList addressList, String from) {
        Intent intent = new Intent(context, AddressPickerAct.class);
        intent.putExtra("kData", (Serializable) addressList);
        intent.putExtra("kEdit", (Serializable) from);
        return intent;
    }

    public static Intent getIntent(Context context, String from, String currentLoc) {
        Intent intent = new Intent(context, AddressPickerAct.class);
        intent.putExtra("kEdit", (Serializable) from);
        intent.putExtra("Locat", (Serializable) currentLoc);
        return intent;
    }


    public static Intent getIntent(Context context) {
        return new Intent(context, AddressPickerAct.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_address);
        ButterKnife.bind(this);
        context = this;
        sessionManager = new UserSessionManager(this);

        tvTitle.setText("Choose your location");
        iv_back.setImageDrawable(getResources().getDrawable(R.drawable.close));


        if (getIntent() != null) {

            commingfrom = (String) getIntent().getStringExtra("kEdit");
            currentLoc = (String) getIntent().getStringExtra("Locat");
            addressList = (AddressList) getIntent().getSerializableExtra("kData");
            autoLocation.setText(currentLoc);

            if (addressList != null) {

                edt_building_no.setText(addressList.getBuildingAndApart());
            }

        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapview);
        mapFragment.getMapAsync(this);
        GPSTracker gpsTracker = new GPSTracker(this, this);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        Lat = gpsTracker.getLatitude();
        Long = gpsTracker.getLongitude();

        getAddress();

//        Lat = Double.parseDouble(sessionManager.getLatitude());
//        Long = Double.parseDouble(sessionManager.getLongitude());
////setToolbar();

//        if (gpsTracker != null) {
//            if (gpsTracker.canGetLocation()) {
//                Location location = gpsTracker.getLocation();
//                if (location != null) {
//                    Lat = gpsTracker.getLatitude();
//                    Long = gpsTracker.getLongitude();
//                } else {
//                    takeTime();
//                }
//            } else {
//                takeTime();
//            }
//        } else {
//            Toast.makeText(this, "Can't Fetch Location", Toast.LENGTH_SHORT).show();
//        }


    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        GPSTracker gpsTracker = new GPSTracker(this, this);
//        if (gpsTracker != null) {
//            if (gpsTracker.canGetLocation()) {
//                Location location = gpsTracker.getLocation();
//                if (location != null) {
//                    Lat = gpsTracker.getLatitude();
//                    Long = gpsTracker.getLongitude();
//                    initilizeMap();
//
//                    getAddress();
//
//                } else {
//                    takeTime();
//                }
//            } else {
//                takeTime();
//            }
//        } else {
//            Toast.makeText(this, "Can't Fetch Location", Toast.LENGTH_SHORT).show();
//        }
//
//
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        List<Address> addresses = null;
        lat = String.valueOf(Lat);
        longs = String.valueOf(Long);
        final LatLng latLngg = new LatLng(Lat, Long);
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLngg).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));
        marker.showInfoWindow();

//        try {
//            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//            addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (addresses != null && addresses.size() > 0) {
//            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//            tvAddress.setText(address);
//            Location = address;
//            currentAddress = address;
//        }

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera position change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;


                mMap.clear();

                try {
                    android.location.Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);

                    lat = String.valueOf(mCenterLatLong.latitude);
                    longs = String.valueOf(mCenterLatLong.longitude);

                    getAddressOnCameraMove(lat, longs);
                    // Toast.makeText(PickAddressActivity.this, "11", Toast.LENGTH_SHORT).show();
                    // startIntentService(mLocation);
                    // mLocationMarkerText.setText("Lat : " + mCenterLatLong.latitude + "," + "Long : " + mCenterLatLong.longitude);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            if (commingfrom.equalsIgnoreCase("editbtn")) {

                tvAddress.setText(addressList.getAddress());
                autoLocation.setText(addressList.getAddress());
                currentAddress = addressList.getAddress();

            } else if (commingfrom.equalsIgnoreCase("PICKUP")) {

                tvAddress.setText(currentLoc);
                autoLocation.setText(currentLoc);

            } else if (commingfrom.equalsIgnoreCase("DROPOFF")) {

//                tvAddress.setText(currentLoc);
//                autoLocation.setText(currentLoc);
                tvAddress.setText(addresses.get(0).getAddressLine(0));
                autoLocation.setText(addresses.get(0).getAddressLine(0));
            } else {
                autoLocation.setText(address);
                tvAddress.setText(address);
            }

            Location = address;
            currentAddress = address;
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngg));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        mMap.setOnMapClickListener(AddressPickerAct.this);

//        mMap.setOnMapLongClickListener(this);
//        mMap.setOnMarkerDragListener(this);

    }

    //for popup open after some time
    public void takeTime() {
// take time
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//Do something after 100ms
                showSettingsAlert(1124, AddressPickerAct.this);
            }
        }, 1000);
    }


    //for location alert turn on gps
    public void showSettingsAlert(final int requestCode, final Activity activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddressPickerAct.this);
        alertDialog.setCancelable(false);

        alertDialog.setTitle("Location Disable");

// alertDialog.setMessage("Please enable your Location");
        alertDialog.setMessage("Mansa Musa requires access to location. To enjoy all that Mansa Musa has to offer, turn on your GPS and give Mansa Musa access to your location.");


        alertDialog.setPositiveButton("Turn on GPS", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivityForResult(intent, requestCode);

            }
        });

        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        if (!(activity).isFinishing()) {
            alert.show();
        }

    }


    public void initilizeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapview);
        mapFragment.getMapAsync(this);

    }

    //getAddressfrommovingonMAP
    private void getAddressOnCameraMove(String lat, String longs) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(longs), 1);
            autoLocation.setText(addresses.get(0).getAddressLine(0));
            tvAddress.setText(addresses.get(0).getAddressLine(0));
            Location = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onMarkerDragStart(Marker marker) {
//    }
//
//    @Override
//    public void onMarkerDrag(Marker marker) {
//// marker.setAlpha(0.5f);
//    }
//
//    @Override
//    public void onMarkerDragEnd(Marker marker) {
//        isCorrectAddress = true;
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//        List<Address> addresses = null;
//        try {
//            lat = String.valueOf(marker.getPosition().latitude);
//            longs = String.valueOf(marker.getPosition().longitude);
//            addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (addresses != null && addresses.size() > 0) {
//            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//            tvAddress.setText(address);
//
//            Location = address;
//        }


    //   }

    @OnClick({R.id.btn_done, R.id.iv_back, R.id.iv_clear})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_done:
                Intent intent = new Intent();
                intent.putExtra("ADDRESS", Location);
                intent.putExtra("LAT", lat);
                intent.putExtra("LONG", longs);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_clear:
                autoLocation.setText("");
                break;


        }
    }


    private void pickNewLocation() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(autoLocation.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
        List<Address> addresses = null;
        if (mMap == null) {
            return;
        }
        mMap.clear();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));
        marker.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
        //Address pick here
        try {
            lat = String.valueOf(marker.getPosition().latitude);
            longs = String.valueOf(marker.getPosition().longitude);
            addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
            tvAddress.setText(addresses.get(0).getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
// autoLocation.setText(address);
            Location = autoLocation.getText().toString();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
    }

//    @Override
//    public void onMapLongClick(LatLng latLng) {
//        mMap.clear();
//
//        markerOptions = new MarkerOptions().position(latLng);
//        markerOptions.draggable(true);
//        mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));
//
//    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        pickNewLocation();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }
    }


    private void getAddress() {

        autocompleteAdapter = new GooglePlacesAutocompleteAdapter(this, R.layout.activity_pick_address);
        autoLocation.setAdapter(autocompleteAdapter);
        autoLocation.dismissDropDown();

        autoLocation.setOnItemClickListener(AddressPickerAct.this);

        autoLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ModelPlace str = (ModelPlace) parent.getItemAtPosition(position);
        getAddress = str.getPlaceName();
        spotName = str.getSpotName();
        region = str.getRegion();

        //set "address" before set "city"
        autoLocation.setText(getAddress);

        if (true) {
            new AsyncGetLatLong(this, str.getPlaceId(), new OnLocationSelect() {
                @Override
                public void onLocationSelect(String lat, String longi, String city, String state, String country, String postal) {
                    try {


                        latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(longi));

                        if (latLng != null) {
                            AddressPickerAct.this.pickNewLocation();
                        }

                        latitudes = lat;
                        longitudes = longi;

                        isCorrectAddress = true;

                        if (latitudes != null && !latitudes.equals("") && longitudes != null && !longitudes.equals("")) {
                            spotLocation = latitudes + "," + longitudes;

                            // getCurrentLocation(Double.parseDouble(latitudes),Double.parseDouble(longitudes));

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).execute();
        } else {
            //new DialogInternetConnection(this).show();
        }
    }


}