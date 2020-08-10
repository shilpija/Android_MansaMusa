package com.freshhome.DriverModule;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.R;
import com.freshhome.SalesModule.Activity_Sales_EditProfile;
import com.freshhome.UpdateProfileActivity;
import com.freshhome.datamodel.NameID;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
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

public class DriverIndividualEditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    CircleImageView circle_userimage;
    SwitchCompat switch_button;
    EditText edit_name;
    TextView text_dob, text_address;
    Spinner cityspinner;
    ImageView city_spinner_arrow, image_back;
    LinearLayout linear_update;
    String gender = "", city = "", latitute = "", longitute = "";
    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    Uri mCapturedImageURI;
    private static final int PLACE_PICKER_REQUEST = 3;
    String fileName = "";
    private static final int RESULT_LOAD_IMAGE = 1;
    int PERMISSION_ALL = 1;
    String image_namewith_path = "";

    ApiInterface apiInterface;
    String[] spinner_gender_array;
    ArrayList<NameID> arrayCity;
    String[] spinner_city_array;
    UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_individual_profile);
        sessionManager = new UserSessionManager(DriverIndividualEditProfileActivity.this);
        apiInterface = ApiClient.getInstance().getClient();
        circle_userimage = (CircleImageView) findViewById(R.id.circle_userimage);
        circle_userimage.setOnClickListener(this);
        switch_button = (SwitchCompat) findViewById(R.id.switch_button);
        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_name.setText(getIntent().getStringExtra("name"));
        text_dob = (TextView) findViewById(R.id.text_dob);
        text_dob.setText(getIntent().getStringExtra("dob"));
        text_dob.setOnClickListener(this);
        text_address = (TextView) findViewById(R.id.text_address);
        text_address.setText(getIntent().getStringExtra("loc"));
        latitute = getIntent().getStringExtra("lat");
        longitute = getIntent().getStringExtra("lng");
        text_address.setOnClickListener(this);
        cityspinner = (Spinner) findViewById(R.id.cityspinner);
        city = getIntent().getStringExtra("city");
        city_spinner_arrow = (ImageView) findViewById(R.id.city_spinner_arrow);
        city_spinner_arrow.setOnClickListener(this);
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);
        linear_update = (LinearLayout) findViewById(R.id.linear_update);
        linear_update.setOnClickListener(this);

        if (!getIntent().getStringExtra("image_url").equalsIgnoreCase("")) {
            Picasso.get().load(getIntent().getStringExtra("image_url"))
                    .placeholder(R.drawable.icon).into(circle_userimage);
        }

        //-----request run time permission for storgae camera------------------
        requestpermission(false);

        if (CommonMethods.checkConnection()) {
            getCitdata();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(DriverIndividualEditProfileActivity.this, getResources().getString(R.string.internetconnection));
        }


        cityspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (arrayCity.size() != 0) {
                    city = arrayCity.get(position).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.city_spinner_arrow:
                cityspinner.performClick();
                break;

            case R.id.image_back:
                DriverIndividualEditProfileActivity.this.finish();
                break;

            case R.id.linear_update:
                if (CommonMethods.checkConnection()) {
                    if (image_namewith_path.equalsIgnoreCase("")) {
                        updateProfile_without_Image();
                    } else {
                        updateProfile();
                    }
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(DriverIndividualEditProfileActivity.this,
                            getResources().getString(R.string.internetconnection));
                }
                break;


            case R.id.text_dob:
                CommonUtilFunctions.DatePickerDialog_DOB(DriverIndividualEditProfileActivity.this, text_dob);
                break;


            case R.id.circle_userimage:
                requestpermission(true);
                break;

            case R.id.text_address:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    // pick image from gallery and click with cam
    private void requestpermission(boolean opencam) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : PERMISSIONS) {
            result = ContextCompat.checkSelfPermission(DriverIndividualEditProfileActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(DriverIndividualEditProfileActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_ALL);
        } else {
            if (opencam == true) {
                show_dialog();
            }
        }
    }

    private void show_dialog() {
        final Dialog dialog_img = new Dialog(DriverIndividualEditProfileActivity.this);
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
                    }
                });

        // show dialog on screen
        dialog_img.show();
    }

    private void activeTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            fileName = System.currentTimeMillis() + ".png";
            File dir = new File(Environment.getExternalStorageDirectory() + "/FreshHome");
            File output = new File(dir, fileName);
            if (Build.VERSION.SDK_INT >= 24) {
                mCapturedImageURI = FileProvider.getUriForFile(DriverIndividualEditProfileActivity.this,
                        getApplicationContext().getPackageName() + ".fileprovider", output);
            } else {
                mCapturedImageURI = Uri.fromFile(output);
            }
//            mCapturedImageURI = Uri.fromFile(output);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void activeGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    image_namewith_path = CommonUtilFunctions.onCaptureImageResult(DriverIndividualEditProfileActivity.this, data,
                            mCapturedImageURI, false, fileName, circle_userimage);
                    Log.i("image", image_namewith_path);
                }
                break;
            //gallery
            case RESULT_LOAD_IMAGE:
                if (data != null && !data.equals("")) {
                    image_namewith_path = CommonUtilFunctions.onSelectFromGalleryResult_imageview(DriverIndividualEditProfileActivity.this, data,
                            false, circle_userimage);
                    Log.i("image", image_namewith_path);
                }
                break;

            case PLACE_PICKER_REQUEST:

                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(data, this);
                    latitute = String.valueOf(place.getLatLng().latitude);
                    longitute = String.valueOf(place.getLatLng().longitude);
                    text_address.setText(place.getName().toString());
//                String toastMsg = String.format("Place: %s", place.getName());
//                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                }
                break;

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
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.a
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission
                    if (ActivityCompat.shouldShowRequestPermissionRationale(DriverIndividualEditProfileActivity.this, Manifest.permission.CAMERA)) {
//                        showStoragePermissionRationale();
                    } else {
                        CommonUtilFunctions.show_permission_alert(DriverIndividualEditProfileActivity.this, "Please allow all permission to use all funtionalities in app.");
                    }
                }
            }
            return;
        }

        // other 'case' lines to check for other
        // permissions this app might request.
    }


    //TODO API HITS-----------------------------------------------------------
    //getcity
    private void getCitdata() {
        final ProgressDialog progressDialog = new ProgressDialog(DriverIndividualEditProfileActivity.this);
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
                            ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(DriverIndividualEditProfileActivity.this, R.layout.layout_spinner_text_bg_transparent, spinner_city_array);
                            cityspinner.setAdapter(spinner_adapter);

                            preSelectCity();

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(DriverIndividualEditProfileActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(DriverIndividualEditProfileActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(DriverIndividualEditProfileActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    private void preSelectCity() {
        for (int i = 0; i < spinner_city_array.length; i++) {
            if (spinner_city_array[i].equalsIgnoreCase(city)) {
                cityspinner.setSelection(i);
            }
        }
    }

    private void updateProfile() {
        final ProgressDialog progressDialog = new ProgressDialog(DriverIndividualEditProfileActivity.this);
        progressDialog.setCancelable(false);
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

        RequestBody dob =
                RequestBody.create(MediaType.parse("text/plain"), CommonUtilFunctions.changeDateFormatYYYYMMDD(text_dob.getText().toString()));
        RequestBody latitute_r =
                RequestBody.create(MediaType.parse("text/plain"), latitute);

        RequestBody longitute_r =
                RequestBody.create(MediaType.parse("text/plain"), longitute);

        RequestBody city_rq =
                RequestBody.create(MediaType.parse("text/plain"), city);

        RequestBody loc =
                RequestBody.create(MediaType.parse("text/plain"), text_address.getText().toString());

        Call<JsonElement> calls = apiInterface.UpdateIndividaulDriverProfileImage(name, city_rq,
                dob, loc, latitute_r, longitute_r, body);

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
                            CommonMethods.showtoast(DriverIndividualEditProfileActivity.this, obj.getString("msg"));
                            DriverIndividualEditProfileActivity.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(DriverIndividualEditProfileActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(DriverIndividualEditProfileActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(DriverIndividualEditProfileActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    private void updateProfile_without_Image() {
        final ProgressDialog progressDialog = new ProgressDialog(DriverIndividualEditProfileActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        Call<JsonElement> calls = apiInterface.UpdateIndividaulDriverProfile(edit_name.getText().toString().trim(), city,
                CommonUtilFunctions.changeDateFormatYYYYMMDD(text_dob.getText().toString().trim()), text_address.getText().toString().trim(),
                latitute, longitute);

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
                            CommonMethods.showtoast(DriverIndividualEditProfileActivity.this, obj.getString("msg"));
                            DriverIndividualEditProfileActivity.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(DriverIndividualEditProfileActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(DriverIndividualEditProfileActivity.this, getResources().getString(R.string.server_error));
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
}
