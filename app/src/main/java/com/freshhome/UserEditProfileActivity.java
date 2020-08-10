package com.freshhome;

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
import com.freshhome.datamodel.Countries;
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
import retrofit2.http.Part;

public class UserEditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    Spinner spinner_gender, spinner_nationality, spinner_occupation;
    TextView text_username, text_dob;
    EditText edit_name, edit_aboutme;
    CircleImageView profile_image;
    ImageView image_back, profile_image_blur;
    LinearLayout linear_update,linear_edit_banner;
    UserSessionManager sessionManager;
    ApiInterface apiInterface;
    String selected_Country = "", selected_occupation = "", banner_id = "";
    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };
    private static final int REQUEST_BANNER_IMAGE = 4;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    Uri mCapturedImageURI;
    private static final int PLACE_PICKER_REQUEST = 3;
    String fileName = "";
    private static final int RESULT_LOAD_IMAGE = 1;
    int PERMISSION_ALL = 1;

    String image_namewith_path = "", dateofbirth = "", phonenumber_i = "", name_i = "";
    ArrayList<Countries> array_countries;
    List<String> spinnerArray_countries;
    ArrayList<NameID> arrayOccupation;
    String[] spinner_occupation_array;
    String[] spinner_gender_array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_profile);

        array_countries = new ArrayList<>();
        spinnerArray_countries = new ArrayList<>();
        arrayOccupation = new ArrayList<>();

        sessionManager = new UserSessionManager(UserEditProfileActivity.this);
        apiInterface = ApiClient.getInstance().getClient();

        linear_update = (LinearLayout) findViewById(R.id.linear_update);
        linear_update.setOnClickListener(this);

        linear_edit_banner = (LinearLayout) findViewById(R.id.linear_edit_banner);
        linear_edit_banner.setOnClickListener(this);

        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);

        profile_image_blur = (ImageView) findViewById(R.id.profile_image_blur);
        profile_image_blur.setOnClickListener(this);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        profile_image.setOnClickListener(this);

        //image
        if (getIntent().getStringExtra("imageurl").equalsIgnoreCase("")) {
            Picasso.get().load(R.drawable.icon).into(profile_image);
            Picasso.get().load(R.drawable.blur).into(profile_image_blur);
        } else {
            Picasso.get().load(getIntent().getStringExtra("imageurl")).placeholder(R.drawable.icon).into(profile_image);

        }

        banner_id=getIntent().getStringExtra("banner_id");
        if (!getIntent().getStringExtra("header_url").equalsIgnoreCase("")) {
            Picasso.get().load(getIntent().getStringExtra("header_url")).into(profile_image_blur);
        }

        text_username = (TextView) findViewById(R.id.text_username);
        text_username.setText(getIntent().getStringExtra("username"));

        text_dob = (TextView) findViewById(R.id.text_dob);
        text_dob.setText(getIntent().getStringExtra("dob"));
        text_dob.setOnClickListener(this);

        edit_aboutme = (EditText) findViewById(R.id.edit_aboutme);
        edit_aboutme.setText(getIntent().getStringExtra("about"));

        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_name.setText(getIntent().getStringExtra("name"));

        spinner_nationality = (Spinner) findViewById(R.id.spinner_nationality);
        selected_Country = getIntent().getStringExtra("nationality");

        spinner_occupation = (Spinner) findViewById(R.id.spinner_occupation);
        selected_occupation = getIntent().getStringExtra("occupation");

        spinner_gender = (Spinner) findViewById(R.id.spinner_gender);
        spinner_gender_array = getResources().getStringArray(R.array.gender_array);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(UserEditProfileActivity.this, R.layout.layout_spinner_text, spinner_gender_array);
        spinner_gender.setAdapter(genderAdapter);

        //-----request run time permission for storgae camera------------------
        requestpermission(false);

        //----------get list of countries---------------
        if (CommonMethods.checkConnection()) {
            getCountries();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(UserEditProfileActivity.this, getResources().getString(R.string.internetconnection));
        }

        //----------get list of occupations---------------
        if (CommonMethods.checkConnection()) {
            geOccupations();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(UserEditProfileActivity.this, getResources().getString(R.string.internetconnection));
        }

        spinner_occupation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (arrayOccupation.size() != 0) {
                    selected_occupation = arrayOccupation.get(position).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

    }

    private void requestpermission(boolean opencam) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : PERMISSIONS) {
            result = ContextCompat.checkSelfPermission(UserEditProfileActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(UserEditProfileActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_ALL);
        } else {
            if (opencam == true) {
                show_dialog();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                UserEditProfileActivity.this.finish();
                break;

            case R.id.profile_image:
                requestpermission(true);
                break;

            case R.id.linear_update:

                if (CommonMethods.checkConnection()) {
                    if (image_namewith_path.equalsIgnoreCase("")) {
                        updateProfile_without_Image();
                    } else {
                        updateProfile();
                    }

                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(UserEditProfileActivity.this, getResources().getString(R.string.internetconnection));
                }
                break;


            case R.id.text_dob:
                dateofbirth = CommonUtilFunctions.DatePickerDialog_DOB(UserEditProfileActivity.this, text_dob);
                break;

            case R.id.profile_image_blur:
                startActivityForResult(new Intent(UserEditProfileActivity.this, HeaderImageListActivity.class), REQUEST_BANNER_IMAGE);
                break;

            case R.id.linear_edit_banner:
                startActivityForResult(new Intent(UserEditProfileActivity.this, HeaderImageListActivity.class), REQUEST_BANNER_IMAGE);
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
                    if (ActivityCompat.shouldShowRequestPermissionRationale(UserEditProfileActivity.this, Manifest.permission.CAMERA)) {
//                        showStoragePermissionRationale();
                    } else {
                        CommonUtilFunctions.show_permission_alert(UserEditProfileActivity.this, "Please allow all permission to use all funtionalities in app.");
                    }
                }
            }
            return;
        }

        // other 'case' lines to check for other
        // permissions this app might request.
    }

//------------------------------select and capture image----------------

    private void show_dialog() {
        final Dialog dialog_img = new Dialog(UserEditProfileActivity.this);
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
                mCapturedImageURI = FileProvider.getUriForFile(UserEditProfileActivity.this, getApplicationContext().getPackageName() + ".fileprovider", output);
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
                    image_namewith_path = CommonUtilFunctions.onCaptureImageResultblur(UserEditProfileActivity.this, data,
                            mCapturedImageURI, false, fileName, profile_image, profile_image_blur);
                    Log.i("image", image_namewith_path);
                }
                break;
            //gallery
            case RESULT_LOAD_IMAGE:
                if (data != null && !data.equals("")) {
                    image_namewith_path = CommonUtilFunctions.onSelectFromGalleryResult_imageview_blur(UserEditProfileActivity.this, data,
                            false, profile_image, profile_image_blur);
                    Log.i("image", image_namewith_path);
                }
                break;

            case REQUEST_BANNER_IMAGE:
//                banner_id
                if (data != null && data.hasExtra("banner_id")) {
                    banner_id = data.getStringExtra("banner_id");
                    Picasso.get().load(data.getStringExtra("image_url")).into(profile_image_blur);
                }
                break;

        }
    }


    //----------all api's call-------------

    //get countris
    private void getCountries() {
        final ProgressDialog progressDialog = new ProgressDialog(UserEditProfileActivity.this);
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

                            spinnerArray_countries = new ArrayList<>();
                            spinnerArray_countries.add("Select");
                            for (int i = 0; i < jarrary.length(); i++) {
                                JSONObject obj = jarrary.getJSONObject(i);
                                Countries country = new Countries();
                                country.setCountry_name(obj.getString("country_name"));
                                country.setNationality(obj.getString("nationality"));
                                country.setFlag(obj.getString("flag"));
                                country.setN_code(obj.getString("code"));
                                array_countries.add(country);
                                spinnerArray_countries.add(obj.getString("nationality"));
                            }

                            //set spinner
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                    (UserEditProfileActivity.this, R.layout.layout_spinner_text,
                                            spinnerArray_countries);
                            spinner_nationality.setAdapter(spinnerArrayAdapter);

                            pre_Select_country();

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(UserEditProfileActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(UserEditProfileActivity.this, getResources().getString(R.string.server_error));
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
                if (array_countries.get(i).getNationality().equalsIgnoreCase(selected_Country)) {
                    spinner_nationality.setSelection(i);
                }
            }
        }
    }

    //getoccupation
    private void geOccupations() {
        final ProgressDialog progressDialog = new ProgressDialog(UserEditProfileActivity.this);
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
                        arrayOccupation = new ArrayList<>();

                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONObject jsonObject = object.getJSONObject("success");
                            //city array
                            JSONArray city_array = jsonObject.getJSONArray("occupations");
                            spinner_occupation_array = new String[city_array.length()];
                            for (int i = 0; i < city_array.length(); i++) {
                                JSONObject obj = city_array.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setId(obj.getString("occupation_id"));
                                nameID.setName(obj.getString("occupation_name"));
                                spinner_occupation_array[i] = obj.getString("occupation_name");
                                arrayOccupation.add(nameID);
                            }
                            //set up spinner
                            ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(UserEditProfileActivity.this, R.layout.layout_spinner_text, spinner_occupation_array);
                            spinner_occupation.setAdapter(spinner_adapter);
                            pre_Select_Occupation();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(UserEditProfileActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(UserEditProfileActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(UserEditProfileActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    private void pre_Select_Occupation() {
        if (arrayOccupation.size() != 0) {
            for (int i = 0; i < arrayOccupation.size(); i++) {
                if (arrayOccupation.get(i).getName().equalsIgnoreCase(selected_occupation)) {
                    spinner_occupation.setSelection(i);
                }
            }
        }
    }

    private void updateProfile() {
        final ProgressDialog progressDialog = new ProgressDialog(UserEditProfileActivity.this);
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


        RequestBody aboutme =
                RequestBody.create(MediaType.parse("text/plain"), edit_aboutme.getText().toString().trim());

        RequestBody nationality =
                RequestBody.create(MediaType.parse("text/plain"), selected_Country);

        RequestBody dob =
                RequestBody.create(MediaType.parse("text/plain"), CommonUtilFunctions.changeDateFormatYYYYMMDD(text_dob.getText().toString()));

        RequestBody gender =
                RequestBody.create(MediaType.parse("text/plain"), spinner_gender.getSelectedItem().toString());

        RequestBody occupation_id =
                RequestBody.create(MediaType.parse("text/plain"), selected_occupation);

        RequestBody bannerid =
                RequestBody.create(MediaType.parse("text/plain"), banner_id);

//        RequestBody banner_url =
//                RequestBody.create(MediaType.parse("text/plain"), "http:\\/\\/freshhomee.com\\/web\\/uploads\\/banners\\/a8c93de2c789aa384cfaab1f25f766ee.png");
        Call<JsonElement> calls = apiInterface.UpdateUserProfile(name, aboutme,
                nationality, dob, gender, occupation_id, body, bannerid);

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
                            CommonMethods.showtoast(UserEditProfileActivity.this, obj.getString("msg"));
//                            CommonUtilFunctions.success_Alert_Dialog(ChangePassword.this, obj.getString("msg"));
                            UserEditProfileActivity.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(UserEditProfileActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(UserEditProfileActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(UserEditProfileActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    private void updateProfile_without_Image() {
        final ProgressDialog progressDialog = new ProgressDialog(UserEditProfileActivity.this);
        progressDialog.setCancelable(false);
//        progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        Call<JsonElement> calls = apiInterface.UpdateUserProfileWithoutImage(edit_name.getText().toString().trim(),
                edit_aboutme.getText().toString().trim(),
                selected_Country,
                CommonUtilFunctions.changeDateFormatYYYYMMDD(text_dob.getText().toString()),
                spinner_gender.getSelectedItem().toString(), selected_occupation,
                banner_id);

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
                            CommonMethods.showtoast(UserEditProfileActivity.this, obj.getString("msg"));
//                            CommonUtilFunctions.success_Alert_Dialog(ChangePassword.this, obj.getString("msg"));
                            UserEditProfileActivity.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(UserEditProfileActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(UserEditProfileActivity.this, getResources().getString(R.string.server_error));
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
