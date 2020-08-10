package com.freshhome;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.freshhome.CommonUtil.AddressResultReceiver;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.FetchAddressService;
import com.freshhome.CommonUtil.StartGooglePlayServices;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.datamodel.Countries;
import com.freshhome.datamodel.NameID;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.TakeImage;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class UpdateProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_BANNER_IMAGE = 4;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_BIMAGE_CAPTURE = 12;
    private static final int PLACE_PICKER_REQUEST = 110;
    ;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_LOAD_BIMAGE = 11;
    protected final int REQ_CODE_GPS_SETTINGS = 150;
    private final int CAMERA_PIC_REQUEST = 77, REQ_CODE_PICK_IMAGE = 88;
    private final int REQ_CODE_LOCATION = 107;
    ImageView image_back, profile_image_blur;
    CircleImageView profile_image;
    SwitchCompat switch_button;
    File imgFile;
    double pLat, pLong;
    Uri mCapturedImageURI;
    String fileName = "";
    int PERMISSION_ALL = 1;
    String image_namewith_path = "", dateofbirth = "", phonenumber_i = "", name_i = "", banner_id = "";
    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };
    TextView text_username, text_loc, text_dob;
    EditText edit_aboutme, edit_name, edit_buildingname, edit_floor, edit_flat, edit_landmark;
    Spinner spinner_nationality, spinner_city;
    String availablity = "";
    LinearLayout linear_update, linear_edit_banner;
    ApiInterface apiInterface;
    String latitute = "0", longitute = "0";
    UserSessionManager sessionManager;
    ArrayList<Countries> array_countries;
    ArrayList<NameID> arrayCity;
    List<String> spinnerArray;
    String selected_Country = "", selected_city_id = "";
    String[] spinner_city_array;
    StartGooglePlayServices startGooglePlayServices = new StartGooglePlayServices(this);
    private String path = "";
    private String lat1;
    private String lat2;
    private String deslat1;
    private String deslat2;
    private String address1 = "";
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private AddressResultReceiver mResultReceiver;
    private LocationCallback locationCallback;
    private Location mLastLocation;
    private boolean isLocServiceStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        array_countries = new ArrayList<>();
        arrayCity = new ArrayList<>();
        spinnerArray = new ArrayList<>();

        sessionManager = new UserSessionManager(UpdateProfileActivity.this);
        apiInterface = ApiClient.getInstance().getClient();

        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);
        profile_image_blur = (ImageView) findViewById(R.id.profile_image_blur);
        profile_image_blur.setOnClickListener(this);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        profile_image.setOnClickListener(this);

        if (getIntent().getStringExtra("imageurl").equalsIgnoreCase("")) {
            Picasso.get().load(R.drawable.icon).into(profile_image);
        } else {
            Picasso.get().load(getIntent().getStringExtra("imageurl")).placeholder(R.drawable.icon).into(profile_image);

        }
        banner_id = getIntent().getStringExtra("supplier_banner_id");
        if (!getIntent().getStringExtra("header_url").equalsIgnoreCase("")) {
            Picasso.get().load(getIntent().getStringExtra("header_url")).placeholder(R.drawable.defualt_list).into(profile_image_blur);
        }
        switch_button = (SwitchCompat) findViewById(R.id.switch_button);

        availablity = getIntent().getStringExtra("available");
        if (availablity.equalsIgnoreCase("offline")) {
            switch_button.setChecked(false);
        } else {
            switch_button.setChecked(true);
        }

        text_username = (TextView) findViewById(R.id.text_username);
        text_username.setText(getIntent().getStringExtra("username"));
        name_i = getIntent().getStringExtra("name");

        text_dob = (TextView) findViewById(R.id.text_dob);
        text_dob.setText(getIntent().getStringExtra("dob"));
        text_dob.setOnClickListener(this);

        text_loc = (TextView) findViewById(R.id.text_loc);
        text_loc.setText(getIntent().getStringExtra("loc"));
        text_loc.setOnClickListener(this);

        edit_aboutme = (EditText) findViewById(R.id.edit_aboutme);
        edit_aboutme.setText(getIntent().getStringExtra("about"));

        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_name.setText(getIntent().getStringExtra("name"));

        phonenumber_i = getIntent().getStringExtra("phone");

        edit_buildingname = (EditText) findViewById(R.id.edit_buildingname);
        edit_buildingname.setText(getIntent().getStringExtra("building_no"));

        edit_floor = (EditText) findViewById(R.id.edit_floor);
        edit_floor.setText(getIntent().getStringExtra("floor_no"));

        edit_flat = (EditText) findViewById(R.id.edit_flat);
        edit_flat.setText(getIntent().getStringExtra("flat_no"));

        edit_landmark = (EditText) findViewById(R.id.edit_landmark);
        edit_landmark.setText(getIntent().getStringExtra("landmark_no"));

        spinner_nationality = (Spinner) findViewById(R.id.spinner_nationality);
        selected_Country = CommonMethods.checkNull(getIntent().getStringExtra("nationality"));

        spinner_city = (Spinner) findViewById(R.id.spinner_city);
        selected_city_id = getIntent().getStringExtra("city");

        linear_update = (LinearLayout) findViewById(R.id.linear_update);
        linear_update.setOnClickListener(this);

        linear_edit_banner = (LinearLayout) findViewById(R.id.linear_edit_banner);
        linear_edit_banner.setOnClickListener(this);

        requestpermission(false);
        requestpermissionbanner(false);

        if (CommonMethods.checkConnection()) {
            getCountries();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(UpdateProfileActivity.this, getResources().getString(R.string.internetconnection));
        }

        if (CommonMethods.checkConnection()) {
            getCitdata();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(UpdateProfileActivity.this, getResources().getString(R.string.internetconnection));
        }

        spinner_nationality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (array_countries.size() != 0) {
                    selected_Country = array_countries.get(position).getN_code();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

    private void getCountries() {
        final ProgressDialog progressDialog = new ProgressDialog(UpdateProfileActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetNationalities();
        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        array_countries = new ArrayList<>();
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONArray jarrary = object.getJSONArray("success");
                            array_countries = new ArrayList<>();
                            Countries country_i = new Countries();
                            country_i.setCountry_name("Select");
                            country_i.setNationality("Select");
                            country_i.setFlag("Select");
                            array_countries.add(country_i);

                            spinnerArray = new ArrayList<>();
                            spinnerArray.add("Select");
                            for (int i = 0; i < jarrary.length(); i++) {
                                JSONObject obj = jarrary.getJSONObject(i);
                                Countries country = new Countries();
                                country.setCountry_name(obj.getString("country_name"));
                                country.setNationality(obj.getString("nationality"));
                                country.setFlag(obj.getString("flag"));
                                country.setN_code(obj.getString("code"));
                                array_countries.add(country);
                                spinnerArray.add(obj.getString("nationality"));
                            }

                            //set spinner
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                    (UpdateProfileActivity.this, R.layout.layout_spinner_text,
                                            spinnerArray);
                            spinner_nationality.setAdapter(spinnerArrayAdapter);

                            pre_Select_country();

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(UpdateProfileActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(UpdateProfileActivity.this, getResources().getString(R.string.server_error));
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
        final ProgressDialog progressDialog = new ProgressDialog(UpdateProfileActivity.this);
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
                            ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(UpdateProfileActivity.this, R.layout.layout_spinner_text, spinner_city_array);
                            spinner_city.setAdapter(spinner_adapter);
                            pre_Select_City();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(UpdateProfileActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(UpdateProfileActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(UpdateProfileActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    private void pre_Select_City() {
        if (arrayCity.size() != 0) {
            for (int i = 0; i < arrayCity.size(); i++) {
                if (arrayCity.get(i).getName().equalsIgnoreCase(selected_city_id)) {
                    spinner_city.setSelection(i);
                }
            }
        }
    }

    private void pre_Select_country() {
        if (array_countries.size() != 0) {
            for (int i = 0; i < array_countries.size(); i++) {
                if (array_countries.get(i).getNationality().equalsIgnoreCase(selected_Country)) {
                    spinner_nationality.setSelection(i);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                UpdateProfileActivity.this.finish();
                break;

            case R.id.profile_image:
                requestpermission(true);
                break;

            case R.id.linear_update:

                if (switch_button.isChecked()) {
                    availablity = "online";
                } else {
                    availablity = "offline";
                }

                if (CommonMethods.checkConnection()) {
                    if (selected_Country != null && !selected_Country.equalsIgnoreCase("")) {
                        if (image_namewith_path.equalsIgnoreCase("")) {
                            if (path.equalsIgnoreCase("")) {
                                updateProfile_without_Image();

                            } else {
                                updateProfileWithBanner();
                            }
                        } else {
                            if (path.equalsIgnoreCase("")) {
                                updateProfileWithProfileImg();
                            } else {
                                updateProfile();

                            }


                        }
                    } else {
                        Toast.makeText(this, "Select Nationality", Toast.LENGTH_SHORT).show();
                    }

        } else{
            CommonUtilFunctions.Error_Alert_Dialog(UpdateProfileActivity.this, getResources().getString(R.string.internetconnection));
        }
        break;

        case R.id.text_loc:

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

        case R.id.text_dob:
        dateofbirth = CommonUtilFunctions.DatePickerDialog_DOB(UpdateProfileActivity.this, text_dob);
        break;

        case R.id.profile_image_blur:
            requestpermissionbanner(true);
        //startActivityForResult(new Intent(UpdateProfileActivity.this, HeaderImageListActivity.class), REQUEST_BANNER_IMAGE);
        break;

        case R.id.linear_edit_banner:
        requestpermissionbanner(true);
        //startActivityForResult(new Intent(UpdateProfileActivity.this, HeaderImageListActivity.class), REQUEST_BANNER_IMAGE);
        break;

    }

}

    private void updateProfile() {
        final ProgressDialog progressDialog = new ProgressDialog(UpdateProfileActivity.this);
        progressDialog.setCancelable(false);
//        progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();

        File file = new File(image_namewith_path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("profile_pic", file.getName(), requestBody);

        File filebanner = new File(path);
        RequestBody requestBodyb = RequestBody.create(MediaType.parse("*/*"), filebanner);
        MultipartBody.Part headerImg =
                MultipartBody.Part.createFormData("supplier_header_image", filebanner.getName(), requestBodyb);

        // add another part within the multipart request
        RequestBody id =
                RequestBody.create(MediaType.parse("text/plain"), sessionManager.getUserDetails().get("user_id"));

        RequestBody name =
                RequestBody.create(MediaType.parse("text/plain"), edit_name.getText().toString());

        RequestBody phonenumber =
                RequestBody.create(MediaType.parse("text/plain"), phonenumber_i);

        RequestBody aboutme =
                RequestBody.create(MediaType.parse("text/plain"), edit_aboutme.getText().toString().trim());

        RequestBody nationality =
                RequestBody.create(MediaType.parse("text/plain"), selected_Country);

        RequestBody dob =
                RequestBody.create(MediaType.parse("text/plain"), CommonUtilFunctions.changeDateFormatYYYYMMDD(text_dob.getText().toString()));
        RequestBody latitute_r =
                RequestBody.create(MediaType.parse("text/plain"), latitute);

        RequestBody longitute_r =
                RequestBody.create(MediaType.parse("text/plain"), longitute);

        RequestBody availablity_r =
                RequestBody.create(MediaType.parse("text/plain"), availablity);

        RequestBody buildingNo =
                RequestBody.create(MediaType.parse("text/plain"), edit_buildingname.getText().toString());
        RequestBody flatNo =
                RequestBody.create(MediaType.parse("text/plain"), edit_flat.getText().toString());

        RequestBody floorNo =
                RequestBody.create(MediaType.parse("text/plain"), edit_floor.getText().toString());

        RequestBody landmark =
                RequestBody.create(MediaType.parse("text/plain"), edit_landmark.getText().toString());

        RequestBody city =
                RequestBody.create(MediaType.parse("text/plain"), selected_city_id);

        RequestBody loc =
                RequestBody.create(MediaType.parse("text/plain"), text_loc.getText().toString());

        RequestBody bannerId =
                RequestBody.create(MediaType.parse("text/plain"), banner_id);

//        RequestBody banner_url =
//                RequestBody.create(MediaType.parse("text/plain"), "http:\\/\\/freshhomee.com\\/web\\/uploads\\/banners\\/a8c93de2c789aa384cfaab1f25f766ee.png");

        Call<JsonElement> calls = apiInterface.UpdateProfile(id, name, phonenumber,
                aboutme,
                nationality, dob, latitute_r, longitute_r, loc, availablity_r, buildingNo,
                flatNo, floorNo, landmark, city, body, headerImg);

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
                            CommonMethods.showtoast(UpdateProfileActivity.this, obj.getString("msg"));
//                            CommonUtilFunctions.success_Alert_Dialog(ChangePassword.this, obj.getString("msg"));
                            UpdateProfileActivity.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(UpdateProfileActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(UpdateProfileActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(UpdateProfileActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    private void updateProfile_without_Image() {
        final ProgressDialog progressDialog = new ProgressDialog(UpdateProfileActivity.this);
        progressDialog.setCancelable(false);
//        progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        Call<JsonElement> calls = apiInterface.UpdateProfileWithoutImage(sessionManager.getUserDetails().get("user_id"),
                edit_name.getText().toString().trim(), phonenumber_i,
                edit_aboutme.getText().toString().trim(), selected_Country,
                CommonUtilFunctions.changeDateFormatYYYYMMDD(text_dob.getText().toString()), latitute, longitute,
                text_loc.getText().toString(), availablity,
                edit_buildingname.getText().toString(),
                edit_flat.getText().toString(),
                edit_floor.getText().toString(),
                edit_landmark.getText().toString(), selected_city_id, banner_id);

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
                            CommonMethods.showtoast(UpdateProfileActivity.this, obj.getString("msg"));
//                            CommonUtilFunctions.success_Alert_Dialog(ChangePassword.this, obj.getString("msg"));
                            UpdateProfileActivity.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(UpdateProfileActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(UpdateProfileActivity.this, getResources().getString(R.string.server_error));
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

    private void requestpermission(boolean opencam) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : PERMISSIONS) {
            result = ContextCompat.checkSelfPermission(UpdateProfileActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(UpdateProfileActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_ALL);
        } else {
            if (opencam == true) {
                show_dialog();
            }
        }
    }

    private void requestpermissionbanner(boolean opencam) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : PERMISSIONS) {
            result = ContextCompat.checkSelfPermission(UpdateProfileActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(UpdateProfileActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_ALL);
        } else {
            if (opencam == true) {
                show_dialog_banner();
            }
        }
    }

    private void show_dialog() {
        final Dialog dialog_img = new Dialog(UpdateProfileActivity.this);
        dialog_img.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_img.setContentView(R.layout.dialog_for_menuimage);
        dialog_img.findViewById(R.id.btnChoosePath)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_img.dismiss();
                        activeGallery();

                    }
                });
        dialog_img.findViewById(R.id.btnTakePhoto)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_img.dismiss();
                        activeTakePhoto();

                      //  showCameraPreview();
                    }
                });

        // show dialog on screen
        dialog_img.show();
    }


    private void show_dialog_banner() {
        final Dialog dialog_img = new Dialog(UpdateProfileActivity.this);
        dialog_img.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_img.setContentView(R.layout.dialog_for_menuimage);
        dialog_img.findViewById(R.id.btnChoosePath)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_img.dismiss();
                        activeGalleryBanner();

                    }
                });
        dialog_img.findViewById(R.id.btnTakePhoto)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UpdateProfileActivity.this, TakeImage.class);
                        intent.putExtra("from", "camera");
                        startActivityForResult(intent, CAMERA_PIC_REQUEST);
                        dialog_img.dismiss();
//                        activeTakePhotoBanner();
                    }
                });

        // show dialog on screen
        dialog_img.show();
    }

    public void onLaunchCamera()  {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileName = System.currentTimeMillis() + ".png";
        File photoFile = getPhotoFileUri(fileName);
       // Uri fileProvider = FileProvider.getUriForFile(UpdateProfileActivity.this, "com.freshhome", photoFile);
        mCapturedImageURI = FileProvider.getUriForFile(UpdateProfileActivity.this, "com.freshhome", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }


    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "/FreshHome");
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("UpdateProfileActivity", "failed to create directory");
        }
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }



    private void activeTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            fileName = System.currentTimeMillis() + ".png";
            File dir = new File(Environment.getExternalStorageDirectory() + "/FreshHome");
            File output = new File(dir, fileName);
            if (Build.VERSION.SDK_INT >= 24) {
                String packageName = getApplicationContext().getPackageName();
               // mCapturedImageURI = FileProvider.getUriForFile(UpdateProfileActivity.this, getApplicationContext().getPackageName() + ".fileprovider", output);
                mCapturedImageURI = FileProvider.getUriForFile(UpdateProfileActivity.this,  "com.freshhome", output);
                System.out.println();
            } else {
                mCapturedImageURI = Uri.fromFile(output);
            }
//            mCapturedImageURI = Uri.fromFile(output);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void activeTakePhotoBanner() {
        Intent intent = new Intent(UpdateProfileActivity.this, TakeImage.class);
        intent.putExtra("from", "camera");
        startActivityForResult(intent, CAMERA_PIC_REQUEST);

    }

    private void activeGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    private void activeGalleryBanner() {
        Intent intent = new Intent(UpdateProfileActivity.this, TakeImage.class);
        intent.putExtra("from", "gallery");
        startActivityForResult(intent, REQ_CODE_PICK_IMAGE);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.a
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission
                    if (ActivityCompat.shouldShowRequestPermissionRationale(UpdateProfileActivity.this, Manifest.permission.CAMERA)) {
//                        showStoragePermissionRationale();
                    } else {
                        CommonUtilFunctions.show_permission_alert(UpdateProfileActivity.this, "Please allow all permission to use all funtionalities in app.");
                    }
                }
            }
            return;
        }

        // other 'case' lines to check for other
        // permissions this app might request.
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    image_namewith_path = CommonUtilFunctions.onCaptureImageResultblur(UpdateProfileActivity.this, data,
                            mCapturedImageURI, false, fileName, profile_image, profile_image_blur);
                    Log.i("image", image_namewith_path);
                }
                break;
            //gallery
            case RESULT_LOAD_IMAGE:
                if (data != null && !data.equals("")) {
                    image_namewith_path = CommonUtilFunctions.onSelectFromGalleryResult_imageview_blur(UpdateProfileActivity.this, data,
                            false, profile_image, profile_image_blur);
                    Log.i("image", image_namewith_path);
                }
                break;

            case REQ_CODE_PICK_IMAGE:
            case CAMERA_PIC_REQUEST:
                if (resultCode == RESULT_CANCELED) {
                    finish();
                } else {
                    if (data.getStringExtra("filePath") != null) {
                        path = data.getStringExtra("filePath");
                        imgFile = new File(data.getStringExtra("filePath"));

                        if (imgFile.exists() && imgFile != null) {
                            //ivMyProfile.setImageURI(Uri.fromFile(fileFlyer));
                            Glide.with(UpdateProfileActivity.this)
                                    .load(path)
                                    .apply(RequestOptions.placeholderOf(R.drawable.defualt_list)
                                            .error(R.drawable.defualt_list))
                                    .into(profile_image_blur);
                        }
                    }
                }
                break;

            case PLACE_PICKER_REQUEST:

                if (requestCode == 110) {
                    if (resultCode == Activity.RESULT_OK) {

                        address1 = data.getStringExtra("ADDRESS");
                        lat1 = data.getStringExtra("LAT");
                        lat2 = data.getStringExtra("LONG");
                        latitute =lat1;
                        longitute = lat2;

                        pLat = Double.parseDouble(lat1);
                        pLong = Double.parseDouble(lat2);
                        text_loc.setText(address1);
                        text_loc.setTextColor(getResources().getColor(R.color.black));
                    }

                } else if (requestCode == REQ_CODE_GPS_SETTINGS) {
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            loadCurrentLoc();
                            break;
                        case Activity.RESULT_CANCELED:
                            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                            break;
                    }
                }

//                if (resultCode == RESULT_OK) {
//                    Place place = PlacePicker.getPlace(data, this);
//                    latitute = String.valueOf(place.getLatLng().latitude);
//                    longitute = String.valueOf(place.getLatLng().longitude);
//                    text_loc.setText(place.getName().toString());
////                String toastMsg = String.format("Place: %s", place.getName());
////                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
//
//                }
                break;


            case REQUEST_BANNER_IMAGE:
//                banner_id
                if (data != null && data.hasExtra("banner_id")) {
                    banner_id = data.getStringExtra("banner_id");
                    Picasso.get().load(data.getStringExtra("image_url")).into(profile_image_blur);
                }
                break;

//
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

                    startGooglePlayServices.showDenyRationaleDialog(this, "You need to allow access to Device Location", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            switch (which) {
                                case BUTTON_POSITIVE:
                                    dialog.dismiss();
                                    UpdateProfileActivity.this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            REQ_CODE_LOCATION);

                                    break;
                                case BUTTON_NEGATIVE:
                                    dialog.dismiss();
                                    UpdateProfileActivity.this.finish();
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

                                    UpdateProfileActivity.this.locationCallBack(location);


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

                                    UpdateProfileActivity.this.locationCallBack(location);

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


    private void updateProfileWithBanner() {
        final ProgressDialog progressDialog = new ProgressDialog(UpdateProfileActivity.this);
        progressDialog.setCancelable(false);
//        progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();

        File filebanner = new File(path);
        RequestBody requestBodyb = RequestBody.create(MediaType.parse("*/*"), filebanner);
        MultipartBody.Part headerImg =
                MultipartBody.Part.createFormData("supplier_header_image", filebanner.getName(), requestBodyb);

        // add another part within the multipart request
        RequestBody id =
                RequestBody.create(MediaType.parse("text/plain"), sessionManager.getUserDetails().get("user_id"));

        RequestBody name =
                RequestBody.create(MediaType.parse("text/plain"), edit_name.getText().toString());

        RequestBody phonenumber =
                RequestBody.create(MediaType.parse("text/plain"), phonenumber_i);

        RequestBody aboutme =
                RequestBody.create(MediaType.parse("text/plain"), edit_aboutme.getText().toString().trim());

        RequestBody nationality =
                RequestBody.create(MediaType.parse("text/plain"), selected_Country);

        RequestBody dob =
                RequestBody.create(MediaType.parse("text/plain"), CommonUtilFunctions.changeDateFormatYYYYMMDD(text_dob.getText().toString()));
        RequestBody latitute_r =
                RequestBody.create(MediaType.parse("text/plain"), latitute);

        RequestBody longitute_r =
                RequestBody.create(MediaType.parse("text/plain"), longitute);

        RequestBody availablity_r =
                RequestBody.create(MediaType.parse("text/plain"), availablity);

        RequestBody buildingNo =
                RequestBody.create(MediaType.parse("text/plain"), edit_buildingname.getText().toString());
        RequestBody flatNo =
                RequestBody.create(MediaType.parse("text/plain"), edit_flat.getText().toString());

        RequestBody floorNo =
                RequestBody.create(MediaType.parse("text/plain"), edit_floor.getText().toString());

        RequestBody landmark =
                RequestBody.create(MediaType.parse("text/plain"), edit_landmark.getText().toString());

        RequestBody city =
                RequestBody.create(MediaType.parse("text/plain"), selected_city_id);

        RequestBody loc =
                RequestBody.create(MediaType.parse("text/plain"), text_loc.getText().toString());

        RequestBody bannerId =
                RequestBody.create(MediaType.parse("text/plain"), banner_id);

//        RequestBody banner_url =
//                RequestBody.create(MediaType.parse("text/plain"), "http:\\/\\/freshhomee.com\\/web\\/uploads\\/banners\\/a8c93de2c789aa384cfaab1f25f766ee.png");

        Call<JsonElement> calls = apiInterface.UpdateProfileBanner(id, name, phonenumber,
                aboutme,
                nationality, dob, latitute_r, longitute_r, loc, availablity_r, buildingNo,
                flatNo, floorNo, landmark, city, headerImg);

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
                            CommonMethods.showtoast(UpdateProfileActivity.this, obj.getString("msg"));
//                            CommonUtilFunctions.success_Alert_Dialog(ChangePassword.this, obj.getString("msg"));
                            UpdateProfileActivity.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(UpdateProfileActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(UpdateProfileActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(UpdateProfileActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }


    private void updateProfileWithProfileImg() {
        final ProgressDialog progressDialog = new ProgressDialog(UpdateProfileActivity.this);
        progressDialog.setCancelable(false);
//        progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();

        File file = new File(image_namewith_path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("profile_pic", file.getName(), requestBody);

        // add another part within the multipart request
        RequestBody id =
                RequestBody.create(MediaType.parse("text/plain"), sessionManager.getUserDetails().get("user_id"));

        RequestBody name =
                RequestBody.create(MediaType.parse("text/plain"), edit_name.getText().toString());

        RequestBody phonenumber =
                RequestBody.create(MediaType.parse("text/plain"), phonenumber_i);

        RequestBody aboutme =
                RequestBody.create(MediaType.parse("text/plain"), edit_aboutme.getText().toString().trim());

        RequestBody nationality =
                RequestBody.create(MediaType.parse("text/plain"), selected_Country);

        RequestBody dob =
                RequestBody.create(MediaType.parse("text/plain"), CommonUtilFunctions.changeDateFormatYYYYMMDD(text_dob.getText().toString()));
        RequestBody latitute_r =
                RequestBody.create(MediaType.parse("text/plain"), latitute);

        RequestBody longitute_r =
                RequestBody.create(MediaType.parse("text/plain"), longitute);

        RequestBody availablity_r =
                RequestBody.create(MediaType.parse("text/plain"), availablity);

        RequestBody buildingNo =
                RequestBody.create(MediaType.parse("text/plain"), edit_buildingname.getText().toString());
        RequestBody flatNo =
                RequestBody.create(MediaType.parse("text/plain"), edit_flat.getText().toString());

        RequestBody floorNo =
                RequestBody.create(MediaType.parse("text/plain"), edit_floor.getText().toString());

        RequestBody landmark =
                RequestBody.create(MediaType.parse("text/plain"), edit_landmark.getText().toString());

        RequestBody city =
                RequestBody.create(MediaType.parse("text/plain"), selected_city_id);

        RequestBody loc =
                RequestBody.create(MediaType.parse("text/plain"), text_loc.getText().toString());

        RequestBody bannerId =
                RequestBody.create(MediaType.parse("text/plain"), banner_id);

//        RequestBody banner_url =
//                RequestBody.create(MediaType.parse("text/plain"), "http:\\/\\/freshhomee.com\\/web\\/uploads\\/banners\\/a8c93de2c789aa384cfaab1f25f766ee.png");

        Call<JsonElement> calls = apiInterface.UpdateProfileImg(id, name, phonenumber,
                aboutme,
                nationality, dob, latitute_r, longitute_r, loc, availablity_r, buildingNo,
                flatNo, floorNo, landmark, city, body);

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
                            CommonMethods.showtoast(UpdateProfileActivity.this, obj.getString("msg"));
//                            CommonUtilFunctions.success_Alert_Dialog(ChangePassword.this, obj.getString("msg"));
                            UpdateProfileActivity.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(UpdateProfileActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(UpdateProfileActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(UpdateProfileActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }
}
