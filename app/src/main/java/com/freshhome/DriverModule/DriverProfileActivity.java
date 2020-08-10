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

import com.freshhome.CommonUtil.AllAPIs;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.R;
import com.freshhome.UpdateProfileActivity;
import com.freshhome.UserEditProfileActivity;
import com.freshhome.datamodel.Countries;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
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

public class DriverProfileActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView image_back, image_logout;
    EditText edit_age, edit_name;
    LinearLayout linear_update;
    Spinner spinner_nationality;
    SwitchCompat switch_button;
    ApiInterface apiInterface;
    ArrayList<Countries> array_countries;
    String selected_Country = "", isAvailable = "0", image_namewith_path = "";
    List<String> spinnerArray;
    UserSessionManager sessionManager;
    TextView text_update;
    CircleImageView circle_userimage;
    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };
    int PERMISSION_ALL = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    Uri mCapturedImageURI;
    private static final int PLACE_PICKER_REQUEST = 3;
    String fileName = "";
    private static final int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);
        sessionManager = new UserSessionManager(DriverProfileActivity.this);
        AllAPIs.token = sessionManager.getDriveDetails().get(UserSessionManager.KEY_BASETOKEN);
        array_countries = new ArrayList<>();
        spinnerArray = new ArrayList<>();

        apiInterface = ApiClient.getInstance().getClient();

        circle_userimage = (CircleImageView) findViewById(R.id.circle_userimage);
        circle_userimage.setOnClickListener(this);
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);
        image_logout = (ImageView) findViewById(R.id.image_logout);
        image_logout.setOnClickListener(this);
        edit_age = (EditText) findViewById(R.id.edit_age);
        edit_name = (EditText) findViewById(R.id.edit_name);
        switch_button = (SwitchCompat) findViewById(R.id.switch_button);
        spinner_nationality = (Spinner) findViewById(R.id.spinner_nationality);
        linear_update = (LinearLayout) findViewById(R.id.linear_update);
        linear_update.setOnClickListener(this);
        text_update = (TextView) findViewById(R.id.text_update);

        //TODO-----request run time permission for storgae camera------------------
        requestpermission(false);


        //check if from home or login
        checkIsFromLogin(getIntent().getBooleanExtra("isHome", true));

        if (CommonMethods.checkConnection()) {
            getCountries();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(DriverProfileActivity.this, getResources().getString(R.string.internetconnection));
        }

        spinner_nationality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (array_countries.size() != 0) {
                    if (position != 0) {
                        selected_Country = array_countries.get(position).getN_code();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void checkIsFromLogin(boolean isHome) {
        if (isHome) {
            image_logout.setVisibility(View.INVISIBLE);
            image_back.setVisibility(View.VISIBLE);
        } else {
            image_logout.setVisibility(View.INVISIBLE);
            image_back.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.image_back:
                DriverProfileActivity.this.finish();
                break;

            case R.id.linear_update:
                if (switch_button.isChecked()) {
                    isAvailable = "1";
                } else {
                    isAvailable = "0";
                }
//                selected_Country = spinner_nationality.getSelectedItem().toString();
                if (CommonMethods.checkConnection()) {
                    if (image_namewith_path.equalsIgnoreCase("")) {
                        UdpateProfile_WithoutImage();
                    } else {
                        UpdateProfile_WithImage();
                    }
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(DriverProfileActivity.this, getResources().getString(R.string.internetconnection));
                }

                break;


            case R.id.image_logout:
                sessionManager.clear_session_driver();
                ActivityCompat.finishAffinity(DriverProfileActivity.this);
                break;

            case R.id.circle_userimage:
                // TODO: OPENCAM true for open dialg after permission check
                requestpermission(true);
                break;

        }
    }

    private void getCountries() {
        final ProgressDialog progressDialog = new ProgressDialog(DriverProfileActivity.this);
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
                            country_i.setN_code("Select");
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
                                    (DriverProfileActivity.this, R.layout.layout_spinner_text,
                                            spinnerArray);
                            spinner_nationality.setAdapter(spinnerArrayAdapter);


                            if (CommonMethods.checkConnection()) {
                                getProfile();
                            } else {
                                CommonUtilFunctions.Error_Alert_Dialog(DriverProfileActivity.this, getResources().getString(R.string.internetconnection));
                            }
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(DriverProfileActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(DriverProfileActivity.this, getResources().getString(R.string.server_error));
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

    private void pre_Select_country() {
        if (array_countries.size() != 0) {
            for (int i = 0; i < array_countries.size(); i++) {
                if (array_countries.get(i).getN_code().equalsIgnoreCase(selected_Country)) {
                    spinner_nationality.setSelection(i);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getIntent().getBooleanExtra("isHome", true)) {
            DriverProfileActivity.this.finish();
        } else {
            ActivityCompat.finishAffinity(DriverProfileActivity.this);
        }
    }

    private void getProfile() {
        final ProgressDialog progressDialog = new ProgressDialog(DriverProfileActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetDriverProfile();
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
                            JSONObject obj = object.getJSONObject("data");

                            sessionManager.createDriverSession(obj.getString("driver_id"), obj.getString("is_available"),
                                    obj.getString("age"), obj.getString("username"), obj.getString("nationality"),
                                    obj.getString("access_token"),"","yes",obj.getString("profile_pic"),
                                   "",
                                    "",
                                    "");
                            selected_Country = obj.getString("nationality");
                            edit_age.setText(CommonMethods.checkNull(obj.getString("age")));
                            edit_name.setText(CommonMethods.checkNull(obj.getString("username")));

                            //image
                            if (obj.getString("profile_pic").equalsIgnoreCase("null") || obj.getString("profile_pic").equalsIgnoreCase("")) {
                                Picasso.get().load(R.drawable.icon).into(circle_userimage);
                            } else {
                                Picasso.get().load(obj.getString("profile_pic")).placeholder(R.drawable.icon).into(circle_userimage);
                            }

                            isAvailable = obj.getString("is_available");
                            if (obj.getString("is_available").equalsIgnoreCase("null") || obj.getString("is_available").equalsIgnoreCase("")) {
                                switch_button.setChecked(false);
                            } else {
                                if (obj.getString("is_available").equalsIgnoreCase("1")) {
                                    switch_button.setChecked(true);
                                } else {
                                    switch_button.setChecked(false);
                                }
                            }

                            pre_Select_country();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(DriverProfileActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(DriverProfileActivity.this, getResources().getString(R.string.server_error));
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

    private void UpdateProfile_WithImage() {
        final ProgressDialog progressDialog = new ProgressDialog(DriverProfileActivity.this);
        progressDialog.setCancelable(false);
//        progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();

        File file = new File(image_namewith_path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("profile_pic", file.getName(), requestBody);

        RequestBody name =
                RequestBody.create(MediaType.parse("text/plain"), edit_name.getText().toString());


        RequestBody age =
                RequestBody.create(MediaType.parse("text/plain"), edit_age.getText().toString().trim());

        RequestBody iaAvailableValue =
                RequestBody.create(MediaType.parse("text/plain"), isAvailable);

        RequestBody nationality =
                RequestBody.create(MediaType.parse("text/plain"), selected_Country);


        Call<JsonElement> calls = apiInterface.UpdateDriverProfile(name, iaAvailableValue,
                nationality, age, body);

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
                            JSONObject obj = object.getJSONObject("data");
                            sessionManager.createDriverSession(obj.getString("driver_id"), obj.getString("is_available"),
                                    obj.getString("age"), edit_name.getText().toString().trim(),
                                    obj.getString("nationality"), obj.getString("access_token"),
                                    "","yes",obj.getString("profile_pic"),
                                   "",
                                  "",
                                    "");

//                            sessionManager.createDriverSession(obj.getString("driver_id"), obj.getString("is_available"),
//                                    obj.getString("age"),obj.getString("name"), obj.getString("nationality"), obj.getString("access_token"));
                            MoveToNext();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(DriverProfileActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(DriverProfileActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(DriverProfileActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    private void UdpateProfile_WithoutImage() {
        final ProgressDialog progressDialog = new ProgressDialog(DriverProfileActivity.this);
        progressDialog.setCancelable(false);
//        progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        Call<JsonElement> calls = apiInterface.UpdateDriverProfileWithoutImage(edit_name.getText().toString().trim(),
                isAvailable,
                selected_Country,
                edit_age.getText().toString().trim());

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
                            JSONObject obj = object.getJSONObject("data");
                            sessionManager.createDriverSession(obj.getString("driver_id"), obj.getString("is_available"),
                                    obj.getString("age"), edit_name.getText().toString().trim(), obj.getString("nationality"),
                                    obj.getString("access_token"),"","yes",obj.getString("profile_pic"),
                                  "","","");

//                            sessionManager.createDriverSession(obj.getString("driver_id"), obj.getString("is_available"),
//                                    obj.getString("age"),obj.getString("name"), obj.getString("nationality"), obj.getString("access_token"));
                            MoveToNext();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(DriverProfileActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(DriverProfileActivity.this, getResources().getString(R.string.server_error));
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


    private void MoveToNext() {

        Intent i = new Intent(DriverProfileActivity.this, DriverNavDrawerActivity.class);
        startActivity(i);
        ActivityCompat.finishAffinity(DriverProfileActivity.this);
    }

    private void requestpermission(boolean opencam) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : PERMISSIONS) {
            result = ContextCompat.checkSelfPermission(DriverProfileActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(DriverProfileActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_ALL);
        } else {
            if (opencam == true) {
                show_dialog();
            }
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
                    if (ActivityCompat.shouldShowRequestPermissionRationale(DriverProfileActivity.this, Manifest.permission.CAMERA)) {
//                        showStoragePermissionRationale();
                    } else {
                        CommonUtilFunctions.show_permission_alert(DriverProfileActivity.this, "Please allow all permission to use all funtionalities in app.");
                    }
                }
            }
            return;
        }

        // other 'case' lines to check for other
        // permissions this app might request.
    }

    private void show_dialog() {
        final Dialog dialog_img = new Dialog(DriverProfileActivity.this);
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
                mCapturedImageURI = FileProvider.getUriForFile(DriverProfileActivity.this, getApplicationContext().getPackageName() + ".fileprovider", output);
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
                    image_namewith_path = CommonUtilFunctions.onCaptureImageResult(DriverProfileActivity.this, data,
                            mCapturedImageURI, false, fileName, circle_userimage);
                    Log.i("image", image_namewith_path);
                }
                break;
            //gallery
            case RESULT_LOAD_IMAGE:
                if (data != null && !data.equals("")) {
                    image_namewith_path = CommonUtilFunctions.onSelectFromGalleryResult_imageview(DriverProfileActivity.this, data,
                            false, circle_userimage);
                    Log.i("image", image_namewith_path);
                }
                break;

        }
    }


}
