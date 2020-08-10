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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.LoginActivity;
import com.freshhome.R;
import com.freshhome.SignUpActivity;
import com.freshhome.datamodel.NameID;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IndividualDriverRegisterActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView image_license;
    LinearLayout linear_signup, linear_back;
    TextView text_login, text_mobilenumber, text_selectlocation, text_select_dob, text_signup;
    EditText edit_emailid, edit_username, edit_re_password, edit_password, edit_name;
    private static final int PLACE_PICKER_REQUEST = 11;
    ApiInterface apiInterface;
    ProgressDialog progressDialog;
    String latitute = "0", longitute = "0", selected_city_id = "", image_namewith_path = "";
    UserSessionManager sessionManager;
    String dateofbirth = "", supplierType = "";
    Spinner spinner_city;
    ImageView spinner_arrow, stype_spinner_arrow;
    boolean fromLogin = true;
    ArrayList<NameID> arrayCity;
    String[] spinner_city_array;

    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };
    int PERMISSION_ALL = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    Uri mCapturedImageURI;
    String fileName = "";
    private static final int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_driver_register);
        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(IndividualDriverRegisterActivity.this);

        image_license = (ImageView) findViewById(R.id.image_license);
        image_license.setOnClickListener(this);

        text_signup = (TextView) findViewById(R.id.text_signup);
        linear_signup = (LinearLayout) findViewById(R.id.linear_signup);
        linear_signup.setOnClickListener(this);
        linear_back = (LinearLayout) findViewById(R.id.linear_back);
        linear_back.setOnClickListener(this);

        text_login = (TextView) findViewById(R.id.text_login);
        text_login.setOnClickListener(this);
        text_mobilenumber = (TextView) findViewById(R.id.text_mobilenumber);
        text_mobilenumber.setText(sessionManager.getPhoneNumber());

        text_selectlocation = (TextView) findViewById(R.id.text_selectlocation);
        text_selectlocation.setOnClickListener(this);
        text_select_dob = (TextView) findViewById(R.id.text_select_dob);
        text_select_dob.setOnClickListener(this);

        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_emailid = (EditText) findViewById(R.id.edit_emailid);
        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_re_password = (EditText) findViewById(R.id.edit_re_password);
        edit_username = (EditText) findViewById(R.id.edit_username);

        spinner_arrow = (ImageView) findViewById(R.id.spinner_arrow);
        spinner_arrow.setOnClickListener(this);
        spinner_city = (Spinner) findViewById(R.id.locationspinner);

        if (CommonMethods.checkConnection()) {
            getCitdata();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(IndividualDriverRegisterActivity.this, getResources().getString(R.string.internetconnection));
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

        //TODO-----request run time permission for storgae camera------------------
        requestpermission(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.linear_signup:
                if (edit_emailid.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(IndividualDriverRegisterActivity.this, getResources().getString(R.string.enter_email));
                } else if (!CommonMethods.isValidEmail(edit_emailid.getText().toString().trim())) {
                    CommonUtilFunctions.Error_Alert_Dialog(IndividualDriverRegisterActivity.this, getResources().getString(R.string.valid_email));
                } else if (edit_username.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(IndividualDriverRegisterActivity.this, getResources().getString(R.string.enter_username));
                } else if (text_select_dob.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(IndividualDriverRegisterActivity.this, getResources().getString(R.string.enterdob));
                } else if (text_selectlocation.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(IndividualDriverRegisterActivity.this, getResources().getString(R.string.enter_loc));
                } else if (edit_password.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(IndividualDriverRegisterActivity.this, getResources().getString(R.string.enter_password));
                } else if (edit_password.getText().toString().trim().length() < 5) {
                    CommonUtilFunctions.Error_Alert_Dialog(IndividualDriverRegisterActivity.this, getResources().getString(R.string.pass_length));
                } else if (edit_re_password.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(IndividualDriverRegisterActivity.this, getResources().getString(R.string.enter_repassword));
                } else if (!edit_password.getText().toString().trim().equalsIgnoreCase(edit_re_password.getText().toString().trim())) {
                    CommonUtilFunctions.Error_Alert_Dialog(IndividualDriverRegisterActivity.this, getResources().getString(R.string.password_match));
                } else if (image_namewith_path.equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(IndividualDriverRegisterActivity.this, getResources().getString(R.string.select_licese));
                } else {
                    if (CommonMethods.checkConnection()) {
                        postSignupdata();
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(IndividualDriverRegisterActivity.this, getResources().getString(R.string.internetconnection));
                    }
                }
                break;

            case R.id.linear_back:
                IndividualDriverRegisterActivity.this.finish();
                break;

            case R.id.text_login:
                Intent i = new Intent(IndividualDriverRegisterActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// This flag ensures all activities on top of the CloseAllViewsDemo are cleared.
                startActivity(i);
                IndividualDriverRegisterActivity.this.finish();
                break;

            case R.id.text_select_dob:
                dateofbirth = CommonUtilFunctions.DatePickerDialog_DOB(IndividualDriverRegisterActivity.this, text_select_dob);
                break;

            case R.id.text_selectlocation:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.spinner_arrow:
                spinner_city.performClick();
                break;

            case R.id.image_license:
                requestpermission(true);
                break;

        }
    }

    private void postSignupdata() {
        progressDialog = new ProgressDialog(IndividualDriverRegisterActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();

        File file = new File(image_namewith_path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("document", file.getName(), requestBody);

        RequestBody phone_number =
                RequestBody.create(MediaType.parse("text/plain"), text_mobilenumber.getText().toString().trim());

        RequestBody email_id =
                RequestBody.create(MediaType.parse("text/plain"), edit_emailid.getText().toString().trim());

        RequestBody password =
                RequestBody.create(MediaType.parse("text/plain"), edit_password.getText().toString().trim());

        RequestBody req_longitute =
                RequestBody.create(MediaType.parse("text/plain"), longitute);

        RequestBody req_latitute =
                RequestBody.create(MediaType.parse("text/plain"), latitute);

        RequestBody req_name =
                RequestBody.create(MediaType.parse("text/plain"), edit_name.getText().toString().trim());

        RequestBody req_username =
                RequestBody.create(MediaType.parse("text/plain"), edit_username.getText().toString().trim());

        RequestBody location =
                RequestBody.create(MediaType.parse("text/plain"), text_selectlocation.getText().toString().trim());

        RequestBody req_dob =
                RequestBody.create(MediaType.parse("text/plain"), CommonUtilFunctions.changeDateFormatYYYYMMDD(text_select_dob.getText().toString()));

        RequestBody req_city =
                RequestBody.create(MediaType.parse("text/plain"), selected_city_id);

        RequestBody req_role =
                RequestBody.create(MediaType.parse("text/plain"), ConstantValues.Driver);


        Call<JsonElement> calls = apiInterface.IndividualDriverSignUp(phone_number,
                email_id,
                password,
                req_latitute,
                req_longitute,
                req_name,
                location,
                req_dob,
                req_username,
                req_city,
                req_role,
                body);

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
                            Toast.makeText(IndividualDriverRegisterActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            Intent i_nav = new Intent(IndividualDriverRegisterActivity.this, LoginActivity.class);
                            i_nav.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i_nav);
                            IndividualDriverRegisterActivity.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(IndividualDriverRegisterActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(IndividualDriverRegisterActivity.this, getResources().getString(R.string.server_error));
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
        switch (requestCode) {
            case PLACE_PICKER_REQUEST:
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(data, this);
                    latitute = String.valueOf(place.getLatLng().latitude);
                    longitute = String.valueOf(place.getLatLng().longitude);
                    text_selectlocation.setText(place.getName().toString());
                    text_selectlocation.setTextColor(getResources().getColor(R.color.black));
                    String toastMsg = String.format("Place: %s", place.getAddress());
//                    Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                }
                break;


            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    image_namewith_path = CommonUtilFunctions.onCaptureImageResult(IndividualDriverRegisterActivity.this, data,
                            mCapturedImageURI, false, fileName, image_license);
                    Log.i("image", image_namewith_path);
                }
                break;

            //gallery
            case RESULT_LOAD_IMAGE:
                if (data != null && !data.equals("")) {
                    image_namewith_path = CommonUtilFunctions.onSelectFromGalleryResult_imageview(IndividualDriverRegisterActivity.this, data,
                            false, image_license);
                    Log.i("image", image_namewith_path);
                }
                break;
        }
    }

    //TODO API parsing
    // getcity
    private void getCitdata() {
        final ProgressDialog progressDialog = new ProgressDialog(IndividualDriverRegisterActivity.this);
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
                            ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(IndividualDriverRegisterActivity.this, R.layout.layout_spinner_text_bg_transparent, spinner_city_array);
                            spinner_city.setAdapter(spinner_adapter);

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(IndividualDriverRegisterActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(IndividualDriverRegisterActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(IndividualDriverRegisterActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    //other methods
    private void requestpermission(boolean opencam) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : PERMISSIONS) {
            result = ContextCompat.checkSelfPermission(IndividualDriverRegisterActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(IndividualDriverRegisterActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_ALL);
        } else {
            if (opencam == true) {
                show_dialog();
            }
        }
    }

    private void show_dialog() {
        final Dialog dialog_img = new Dialog(IndividualDriverRegisterActivity.this);
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
                mCapturedImageURI = FileProvider.getUriForFile(IndividualDriverRegisterActivity.this, getApplicationContext().getPackageName() + ".fileprovider", output);
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

}
