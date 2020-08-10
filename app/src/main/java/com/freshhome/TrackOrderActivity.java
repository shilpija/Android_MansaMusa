package com.freshhome;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.DirectionsJSONParser;
import com.freshhome.CommonUtil.GMapRoutes;
import com.freshhome.CommonUtil.UserSessionManager;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class TrackOrderActivity extends AppCompatActivity implements OnMapReadyCallback {
    ImageView image_back;
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
    private GoogleMap mMap;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    ApiInterface apiInterface;
    UserSessionManager sessionManager;
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    GMapRoutes gMapRoutes;
    String kitchen_lat = "", kitchen_lng = "", user_lat = "", user_lng = "", kitchen_loc = "", user_loc = "", driver_usernamme = "";
    boolean isfirsttime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);
        sessionManager = new UserSessionManager(TrackOrderActivity.this);
        apiInterface = ApiClient.getInstance().getClient();
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TrackOrderActivity.this.finish();
            }
        });

        if (getIntent().hasExtra("kitchen_lat")) {
            kitchen_lat = getIntent().getStringExtra("kitchen_lat");
            kitchen_lng = getIntent().getStringExtra("kitchen_lng");
            user_lat = getIntent().getStringExtra("user_lat");
            user_lng = getIntent().getStringExtra("user_lng");
            kitchen_loc = getIntent().getStringExtra("kitchen_loc");
            user_loc = getIntent().getStringExtra("user_loc");
            driver_usernamme = getIntent().getStringExtra("driver_username");
        }
        requestpermission(false);
        initLocation();
        startLocationUpdates();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        gMapRoutes = new GMapRoutes(TrackOrderActivity.this);
    }

    private void initLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(TrackOrderActivity.this);
        mSettingsClient = LocationServices.getSettingsClient(TrackOrderActivity.this);

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
            result = ContextCompat.checkSelfPermission(TrackOrderActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(TrackOrderActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_ALL);
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
                    if (ActivityCompat.shouldShowRequestPermissionRationale(TrackOrderActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                        showStoragePermissionRationale();
                    } else {
                        CommonUtilFunctions.show_permission_alert(TrackOrderActivity.this, "Please allow all permission to use all funtionalities in app.");
                    }
                }
            }
            return;
        }
    }

    private void updateLocationUI() {
        LatLng loc_user = new LatLng(Double.valueOf(28.5746), Double.valueOf(77.3561));
        LatLng loc_kitchen = new LatLng(Double.valueOf(28.6406), Double.valueOf(77.2495));
//        LatLng loc_user = new LatLng(Double.valueOf(user_lat), Double.valueOf(user_lng));
//        LatLng loc_kitchen = new LatLng(Double.valueOf(kitchen_lat), Double.valueOf(kitchen_lng));
        if (mMap != null) {
            if (mCurrentLocation != null) {
//                if (isMovetoUser == false) {
                LatLng loc = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(loc, 13);
                if (isfirsttime) {
                    mMap.animateCamera(cameraUpdate);
                    isfirsttime = false;
                }

                //type 0 means supplier and 1 means to user
                drawLineBtwStartEndPoint(loc, loc_kitchen, "0");
                drawLineBtwStartEndPoint(loc_kitchen, loc_user, "1");
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(TrackOrderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(TrackOrderActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        mMap.setMaxZoomPreference(16);
        setStartEndMarker();
        loginToFirebase();
        updateLocationUI();

    }

    private void setStartEndMarker() {
        LatLng loc_user = new LatLng(Double.valueOf(28.5746), Double.valueOf(77.3561));
        LatLng loc_kitchen = new LatLng(Double.valueOf(28.6406), Double.valueOf(77.2495));
// LatLng loc_user = new LatLng(Double.valueOf(user_lat), Double.valueOf(user_lng));
//        LatLng loc_kitchen = new LatLng(Double.valueOf(kitchen_lat), Double.valueOf(kitchen_lng));

        MarkerOptions marker = new MarkerOptions().position(loc_kitchen).anchor(0.5f, 0.5f)
                .title(kitchen_loc).
                        icon(BitmapDescriptorFactory.fromBitmap( marker_icon(R.drawable.home)));

        MarkerOptions marker_user = new MarkerOptions().position(loc_user).anchor(0.5f, 0.5f)
                .title(user_loc).
                        icon(BitmapDescriptorFactory.fromBitmap( marker_icon(R.drawable.username)));
        mMap.addMarker(marker);
        mMap.addMarker(marker_user);


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
                    subscribeToUpdates();
                    Log.d(TAG, "firebase auth success");
                } else {
                    Log.d(TAG, "firebase auth failed");
                }
            }
        });
    }

    private void subscribeToUpdates() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_path));
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setMarker(DataSnapshot dataSnapshot) {
        // When a location update is received, put or update
        // its value in mMarkers, which contains all the markers
        // for locations received, so that we can build the
        // boundaries required to show them all on the map at once
        String key = dataSnapshot.getKey();
        if (key.equalsIgnoreCase(driver_usernamme)) {
            track_driver driver = new track_driver();
            driver = dataSnapshot.getValue(track_driver.class);
            double lat = driver.getDriver_lat();
            double lng = driver.getDriver_lng();
            LatLng location = new LatLng(lat, lng);
            if (!mMarkers.containsKey(key)) {
                mMarkers.put(key, mMap.addMarker(new MarkerOptions().title(key).position(location)
                        .icon(BitmapDescriptorFactory.fromBitmap( marker_icon(R.drawable.delivery_bike)))));
            } else {
                mMarkers.get(key).setPosition(location);
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : mMarkers.values()) {
                builder.include(marker.getPosition());
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0));

        }
    }

    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(TrackOrderActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
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
                .addOnFailureListener(TrackOrderActivity.this, new OnFailureListener() {
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
                                    rae.startResolutionForResult(TrackOrderActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i("tag", "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e("tag", errorMessage);

                                Toast.makeText(TrackOrderActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

//                        updateLocationUI();
                    }
                });
    }

    //TODO SHOW LINES BTW POINTS

    private void drawLineBtwStartEndPoint(LatLng start, LatLng end, String type) {
//        new GetRoute(start,end).execute();
        // Getting URL to the Google Directions API
//        LatLng end_pos = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        String url = CommonMethods.getDirectionsUrl(TrackOrderActivity.this, start, end);
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
            if (lineOptions != null)
                mMap.addPolyline(lineOptions);
        }
    }

    private Bitmap marker_icon(int username) {
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(username);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 60, 60, false);
        return smallMarker;
    }
}
